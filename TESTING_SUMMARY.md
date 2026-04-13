# CAS 认证测试方案总结

## 已添加的测试组件

### 1. 前端测试 (Vitest + MSW)

**位置**: `frontend/src/test/`

**文件**:
- `CasLoginFlow.test.js` - 前端组件单元测试
- `mocks/server.js` - Mock CAS 服务器

**覆盖场景**:
- ✓ CAS 状态检测与自动跳转
- ✓ URL token 解析与存储
- ✓ 完善资料表单提交
- ✓ 合并账号数据解析
- ✓ 错误处理与重试机制

**运行命令**:
```bash
cd frontend
npm install
npm run test
```

### 2. 后端集成测试 (JUnit)

**位置**: `src/test/java/com/abajin/innovation/controller/`

**文件**:
- `CasFrontendIntegrationTest.java` - 前后端联合测试

**覆盖场景**:
- ✓ 新用户完整登录流程
- ✓ 同名账号合并流程
- ✓ 已存在用户直接登录
- ✓ 重定向 URL 验证
- ✓ Token 生成与验证

**运行命令**:
```bash
./mvnw test -Dtest=CasFrontendIntegrationTest
```

### 3. GitHub Actions 工作流

**文件**: `.github/workflows/cas-integration-test.yml`

**功能**:
- ✓ 自动运行后端集成测试
- ✓ 自动运行前端单元测试
- ✓ MySQL 和 Redis 服务容器
- ✓ 构建验证
- ✓ Docker 镜像验证

**触发条件**:
- 推送到 `main` 或 `develop` 分支
- 创建 Pull Request
- 修改相关代码文件

### 4. API 测试集合 (Postman)

**文件**: `docs/cas-api-tests.json`

**包含测试**:
1. 获取 CAS 状态
2. CAS 登录重定向
3. Ticket 验证（新用户）
4. 获取用户信息
5. 完善资料
6. 合并场景验证
7. 执行账号合并

**运行命令**:
```bash
newman run docs/cas-api-tests.json --env-var baseUrl=http://localhost:8080
```

### 5. 本地测试脚本

**文件**: `scripts/run-cas-tests.sh`

**功能**:
- 一键运行前后端测试
- 彩色输出结果
- 支持参数控制

**使用方法**:
```bash
./scripts/run-cas-tests.sh           # 运行所有测试
./scripts/run-cas-tests.sh --backend-only   # 只运行后端
./scripts/run-cas-tests.sh --frontend-only  # 只运行前端
```

## Mock 数据说明

### Mock CAS 票据

| 票据 | 场景 |
|------|------|
| `MOCK-2021001-张三` | 普通新用户 |
| `MOCK-NEW-*` | 强制新用户流程 |
| `MOCK-MERGE-*` | 触发合并检测 |
| `MOCK-EXISTING-*` | 已存在用户 |

### Mock 用户信息

```json
{
  "casUid": "2021001",
  "casName": "张三",
  "role": "STUDENT",
  "authType": "CAS"
}
```

## 测试架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      GitHub Actions                         │
└─────────────────────────────────────────────────────────────┘
                           │
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
┌─────────────────┐ ┌──────────────┐ ┌──────────────┐
│  Backend Tests  │ │ Frontend     │ │ Docker Build │
│  (JUnit)        │ │ Tests        │ │ Verification │
│                 │ │ (Vitest)     │ │              │
└────────┬────────┘ └──────┬───────┘ └──────────────┘
         │                 │
         ▼                 ▼
┌─────────────────┐ ┌──────────────┐
│  Mock CAS       │ │  Mock API    │
│  Server         │ │  Responses   │
└─────────────────┘ └──────────────┘
```

## 调试指南

### 前端测试调试

```javascript
// 开启详细日志
import { server } from './mocks/server'
server.events.on('request:start', ({ request }) => {
  console.log('Mock intercepted:', request.method, request.url)
})
```

### 后端测试调试

```bash
# 运行单个测试方法
./mvnw test -Dtest=CasFrontendIntegrationTest#testNewUserCompleteFlow

# 开启调试模式
./mvnw test -Dtest=CasFrontendIntegrationTest -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

## 常见问题

### Q: 前端测试报错 "TextEncoder is not defined"
**A**: 在 `vitest.config.js` 中添加:
```javascript
export default defineConfig({
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.js']
  }
})
```

### Q: 后端测试数据库连接失败
**A**: 确保使用了 `@Transactional` 注解，或配置内存数据库

### Q: Mock Server 未拦截请求
**A**: 检查请求 URL 是否与 mock 处理器匹配，注意 baseURL 配置

## 下一步建议

1. **添加 E2E 测试**: 使用 Playwright 或 Cypress 进行真实浏览器测试
2. **性能测试**: 使用 JMeter 或 k6 测试 CAS 登录性能
3. **安全测试**: 测试 SQL 注入、XSS 等安全场景
4. **契约测试**: 使用 Pact 验证前后端 API 契约

## 参考文档

- [Vitest Documentation](https://vitest.dev/)
- [MSW Documentation](https://mswjs.io/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [CAS Protocol](https://apereo.github.io/cas/6.6.x/protocol/CAS-Protocol.html)
