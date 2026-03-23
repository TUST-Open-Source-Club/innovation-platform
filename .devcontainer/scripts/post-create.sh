#!/bin/bash
set -e

echo "=== Dev Container Post Create Setup ==="

# Configure Alibaba Cloud Maven mirror
mkdir -p ~/.m2
cat > ~/.m2/settings.xml << 'EOF'
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <name>阿里云公共仓库</name>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
    <mirror>
      <id>aliyunmaven-spring</id>
      <name>阿里云Spring仓库</name>
      <url>https://maven.aliyun.com/repository/spring</url>
      <mirrorOf>spring*</mirrorOf>
    </mirror>
    <mirror>
      <id>aliyunmaven-spring-plugin</id>
      <name>阿里云Spring插件仓库</name>
      <url>https://maven.aliyun.com/repository/spring-plugin</url>
      <mirrorOf>spring-plugin*</mirrorOf>
    </mirror>
  </mirrors>
</settings>
EOF

echo "✓ Maven settings.xml configured with Aliyun mirrors"

# Configure npm to use npmmirror (Taobao mirror)
npm config set registry https://registry.npmmirror.com

echo "✓ npm registry set to npmmirror (https://registry.npmmirror.com)"

# Verify installations
echo ""
echo "=== Environment Verification ==="
echo "Java version:"
java -version 2>&1 | head -1

echo ""
echo "Maven version:"
mvn -version 2>&1 | head -1

echo ""
echo "Node version:"
node --version

echo ""
echo "npm version:"
npm --version

echo ""
echo "npm registry:"
npm config get registry

echo ""
echo "=== Setup Complete ==="
