# FakeItemChecker-Reloaded

一个现代化的Minecraft Bukkit插件，用于检测和防止玩家使用假物品。

## 功能特性

- 🔍 **智能物品检测**: 检测玩家背包中的假物品
- 🛡️ **权限系统**: 基于权限的物品使用控制
- 📊 **详细日志**: 记录所有违规行为
- ⚡ **高性能**: 使用缓存机制提高性能
- 🔧 **灵活配置**: 支持多种配置选项
- 🚫 **多种封禁模式**: 支持Bukkit和BungeeCord封禁

## 架构设计

### 分层架构

```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│  (Commands, Listeners, Events)     │
├─────────────────────────────────────┤
│            Service Layer            │
│     (Business Logic, Cache)        │
├─────────────────────────────────────┤
│         Repository Layer            │
│      (Data Access, SQL)            │
├─────────────────────────────────────┤
│         Infrastructure Layer        │
│  (Database, Configuration)         │
└─────────────────────────────────────┘
```

### 核心组件

1. **配置管理** (`config/`)
   - `PluginConfig`: 统一的配置管理
   - 支持数据库连接池配置
   - 灵活的插件行为配置

2. **数据库管理** (`database/`)
   - `DatabaseManager`: 连接池管理
   - 自动连接测试和健康检查
   - 优化的连接参数

3. **数据访问层** (`repository/`)
   - `Repository<T, ID>`: 通用仓库接口
   - `RuleRepository`: 规则数据访问
   - `LogRepository`: 日志数据访问
   - 使用PreparedStatement防止SQL注入

4. **业务逻辑层** (`service/`)
   - `ItemCheckService`: 物品检查服务
   - 内存缓存机制
   - 异步处理支持

5. **实体模型** (`entity/`)
   - `Rule`: 规则实体，支持验证
   - `Log`: 日志实体
   - 使用Builder模式

6. **命令系统** (`command/`)
   - `AdminCommand`: 管理员命令
   - 支持Tab补全
   - 完善的错误处理

7. **事件监听** (`listener/`)
   - `PlayerActionListener`: 玩家行为监听
   - 异步检查机制
   - 可配置的延迟检查

## 安装说明

### 系统要求

- Java 11+
- MySQL 5.7+ 或 MariaDB 10.2+
- Bukkit/Spigot 1.7.10+

### 安装步骤

1. 下载插件JAR文件
2. 将JAR文件放入服务器的`plugins`文件夹
3. 启动服务器（插件会自动创建配置文件）
4. 编辑`plugins/FakeItemChecker-Reloaded/config.yml`
5. 重启服务器

## 配置说明

### 数据库配置

```yaml
database:
  url: "jdbc:mysql://localhost:3306/fic"
  username: "root"
  password: ""
  max_pool_size: 10
  min_idle: 5
```

### 插件配置

```yaml
# 忽略的背包标题
ignored_inventories:
  - "菜单"
  - "Menu"

# 封禁模式
ban_mode: "BUKKIT_BAN"  # BUNGEE_BAN 或 BUKKIT_BAN

# 封禁原因
ban_reason: "你被ban了"

# 检查延迟（tick）
check_delay: 1

# 调试模式
debug_mode: false
```

## 命令使用

### 管理员命令

- `/fic reload` - 重新加载插件
- `/fic add <amount> <permission>` - 添加新规则
- `/fic list` - 列出所有规则
- `/fic remove <id>` - 删除规则
- `/fic info` - 显示插件信息

### 权限节点

- `fic.admin` - 管理员权限（默认OP）
- `fic.default` - 基础权限（默认OP）

## 数据库结构

### Rules表

```sql
CREATE TABLE rules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item VARCHAR(255) NOT NULL,
    permission VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_item (item),
    INDEX idx_permission (permission)
);
```

### Logs表

```sql
CREATE TABLE logs (
    id VARCHAR(36) PRIMARY KEY,
    player_name VARCHAR(255) NOT NULL,
    time TIMESTAMP NOT NULL,
    server VARCHAR(255) NOT NULL,
    location TEXT,
    event VARCHAR(255),
    inventory_type VARCHAR(255),
    rule_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_player_name (player_name),
    INDEX idx_time (time),
    INDEX idx_rule_id (rule_id),
    FOREIGN KEY (rule_id) REFERENCES rules(id) ON DELETE SET NULL
);
```

## 性能优化

1. **连接池**: 使用HikariCP连接池
2. **缓存机制**: 规则内存缓存
3. **异步处理**: 物品检查异步执行
4. **索引优化**: 数据库查询索引
5. **批量处理**: 物品分组检查

## 安全特性

1. **SQL注入防护**: 使用PreparedStatement
2. **权限验证**: 完善的权限检查
3. **输入验证**: 实体数据验证
4. **错误处理**: 异常安全处理
5. **日志记录**: 详细的操作日志

## 开发说明

### 构建项目

```bash
./gradlew build
```

### 运行测试

```bash
./gradlew test
```

### 生成JAR

```bash
./gradlew shadowJar
```

## 更新日志

### v2.0.0
- 完全重构架构
- 添加缓存机制
- 改进错误处理
- 优化性能
- 添加新命令
- 改进配置系统

### v1.0.4
- 初始版本

## 许可证

本项目采用MIT许可证。

## 贡献

欢迎提交Issue和Pull Request！

## 支持

如有问题，请通过以下方式联系：
- GitHub Issues
- 邮箱: [your-email@example.com]