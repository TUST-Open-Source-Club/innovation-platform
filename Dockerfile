# 多阶段构建 - 构建阶段
FROM eclipse-temurin:17-jdk-alpine AS builder

# 设置 Alpine 阿里云镜像源
RUN sed -i 's|https://dl-cdn.alpinelinux.org/alpine|http://mirrors.aliyun.com/alpine|g' /etc/apk/repositories && \
    apk add --no-cache curl

# 设置工作目录
WORKDIR /app

# 配置阿里云 Maven 镜像
# RUN mkdir -p /root/.m2 && \
#    echo '<?xml version="1.0" encoding="UTF-8"?>' > /root/.m2/settings.xml && \
#    echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"' >> /root/.m2/settings.xml && \
#    echo '          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' >> /root/.m2/settings.xml && \
#    echo '          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0' >> /root/.m2/settings.xml && \
#    echo '                              http://maven.apache.org/xsd/settings-1.0.0.xsd">' >> /root/.m2/settings.xml && \
#    echo '  <mirrors>' >> /root/.m2/settings.xml && \
#    echo '    <mirror>' >> /root/.m2/settings.xml && \
#    echo '      <id>aliyunmaven</id>' >> /root/.m2/settings.xml && \
#    echo '      <name>阿里云公共仓库</name>' >> /root/.m2/settings.xml && \
#    echo '      <url>https://maven.aliyun.com/repository/public</url>' >> /root/.m2/settings.xml && \
#    echo '      <mirrorOf>central</mirrorOf>' >> /root/.m2/settings.xml && \
#    echo '    </mirror>' >> /root/.m2/settings.xml && \
#    echo '  </mirrors>' >> /root/.m2/settings.xml && \
#    echo '</settings>' >> /root/.m2/settings.xml

# 复制 Maven 配置文件
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 下载依赖（利用 Docker 缓存层）
RUN ./mvnw dependency:go-offline -B

# 复制源代码
COPY src src

# 打包应用（跳过测试）
RUN ./mvnw clean package -DskipTests -B

# 运行阶段 - 使用 Alpine JRE
FROM eclipse-temurin:17-jre-alpine

# 设置 Alpine 阿里云镜像源并安装工具
RUN sed -i 's|https://dl-cdn.alpinelinux.org/alpine|http://mirrors.aliyun.com/alpine|g' /etc/apk/repositories && \
    apk add --no-cache tzdata curl

# 设置时区
ENV TZ=Asia/Shanghai

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health 2>/dev/null || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "app.jar"]
