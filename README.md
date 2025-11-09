# 📘 AccountBook —— Java 桌面记账系统


## 项目简介
AccountBook 是一款基于 **Java Swing + MySQL** 的轻量级桌面记账应用，旨在提供简洁高效的个人财务管理功能。支持账单录入、预算统计、分类管理等核心功能，内置数据库自动初始化机制，无需手动建表，适合Java桌面开发学习者快速上手实践。


## 🧩 核心功能
- **收支管理**：支持账单的新增、修改、删除与查询，记录收支金额、分类、时间等信息  （前后端都已实现）
- **预算统计**：（待实现）按月份和分类设置预算，自动计算剩余额度与支出占比  （后端接口已实现，前端界面待实现）
- **分类管理**：（待实现）预设常用收支分类（如餐饮、交通等），支持自定义维护  （后端接口已实现，前端界面待实现）
- **可视化界面**：基于Swing的图形化交互，操作简单直观。**当前已实现收支管理相关的首页（HomePage）和账单记录页（RecodePage），其他功能界面待开发** （后端接口已实现，前端界面待实现）
- **自动初始化**：首次启动自动创建数据库、数据表及默认数据，降低使用门槛 

## ⚙️ 环境要求
| 依赖组件       | 版本要求       | 说明                     |
|----------------|----------------|--------------------------|
| JDK            | 17 及以上      | 推荐 JDK 21，确保兼容性   |
| Maven          | 3.8 及以上     | 用于项目构建与依赖管理   |
| MySQL          | 8.0 及以上     | 存储应用数据，需提前启动 |


## 📦 项目结构
```
accountbook/
├─ pom.xml                             # Maven 配置文件（依赖与构建规则）
├─ src/
│  ├─ main/java/com/accountbook/
│  │  ├─ Application.java              # 程序入口类（包含 main 方法）
│  │  ├─ frontend/                     # 前端界面模块（Swing 组件）
│  │  │  ├─ HomePage.java              # 已实现：首页（收支管理入口）
│  │  │  ├─ RecodePage.java            # 已实现：账单记录页（新增/修改/删除账单）
│  │  │  └─ 其他页面（如BudgetPage.java等） # 待实现：预算、分类等功能界面
│  │  ├─ proxy/                        # 数据交互层（请求/响应封装、数据库访问）
│  │  └─ backend/                      # 后端逻辑（数据库工具、业务处理）
│  └─ main/resources/
│     └─ db.properties                 # 数据库连接配置（需手动修改）
└─ target/                             # Maven 编译输出目录（包含可执行文件）
```


## 🔧 数据库配置
启动前需修改数据库连接参数，确保应用能正常访问MySQL：

1. 打开配置文件：`src/main/resources/db.properties`  
2. 修改以下内容（替换为你的MySQL信息）：
   ```properties
   # 数据库连接地址（无需提前创建库，程序会自动生成）
   db.url=jdbc:mysql://localhost:3306?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   # MySQL 用户名
   db.user=你的用户名
   # MySQL 密码
   db.password=你的密码
   # 驱动类（无需修改）
   db.driver=com.mysql.cj.jdbc.Driver
   ```

> 注意：需确保MySQL用户具备 `CREATE DATABASE` 和 `CREATE TABLE` 权限，否则初始化会失败。


## 🚀 启动指南

### 方式1：通过IDE启动（推荐，适合开发）
1. 使用 IntelliJ IDEA 或 Eclipse 导入项目（选择 `pom.xml` 作为Maven项目）  
2. 等待Maven自动下载依赖  
3. 找到 `src/main/java/com/accountbook/Application.java`  
4. 右键点击 `Run Application.main()`  
5. 首次启动会自动执行：
   - 创建数据库 `account_book`  
   - 生成数据表（账单表、预算表、分类表等）  
   - 插入默认分类数据  
   - 启动图形化界面（默认进入已实现的HomePage）  


### 方式2：通过Maven命令行启动
1. 打开终端，进入项目根目录（`accountbook/`）  
2. 编译项目（跳过测试以加速）：
   ```bash
   mvn -DskipTests package
   ```
3. 运行主类：
   ```bash
   mvn -DskipTests org.codehaus.mojo:exec-maven-plugin:3.3.0:java \
       -Dexec.mainClass=com.accountbook.Application
   ```


### 方式3：打包为可执行JAR（适合分发）
1. 在 `pom.xml` 中添加打包插件（已包含可跳过）：
   ```xml
   <build>
     <plugins>
       <plugin>
         <groupId>org.apache.maven.plugins</groupId>
         <artifactId>maven-shade-plugin</artifactId>
         <version>3.5.0</version>
         <executions>
           <execution>
             <phase>package</phase>
             <goals><goal>shade</goal></goals>
             <configuration>
               <transformers>
                 <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                   <mainClass>com.accountbook.Application</mainClass>
                 </transformer>
               </transformers>
             </configuration>
           </execution>
         </executions>
       </plugin>
     </plugins>
   </build>
   ```
2. 打包：
   ```bash
   mvn -DskipTests package
   ```
3. 运行JAR包：
   ```bash
   java -jar target/accountbook-1.0-SNAPSHOT-shaded.jar
   ```


## 📌 模块说明
| 模块路径                          | 功能说明                                  |
|-----------------------------------|-------------------------------------------|
| `frontend/`                       | 所有图形界面组件（窗口、按钮、表单等）。<br>✅ 已实现：`HomePage.java`（首页）、`RecodePage.java`（账单记录页）<br>⏳ 待实现：预算管理页、分类管理页等 |
| `backend/storage/db/`             | 数据库连接工具、表结构定义、初始化逻辑    |
| `proxy/helper/`                   | 数据库访问封装（简化CRUD操作）            |
| `proxy/request/` `proxy/response/`| 数据交互模型（请求参数与响应结果封装）    |
| `component/`                      | 通用UI组件（弹窗、分页、刷新逻辑等）      |


## 当前实现进度
| 功能模块       | 后端逻辑状态 | 图形化界面状态          | 说明                          |
|----------------|--------------|-------------------------|-------------------------------|
| 收支管理       | 已实现       | 已完成（HomePage、RecodePage） | 支持账单的增删改查操作        |
| 预算统计       | 待实现       | 未实现                  | 暂未开发，点击入口将显示提示  |
| 分类管理       | 待实现       | 未实现                  | 暂未开发，点击入口将显示提示  |


## ❓ 常见问题
1. **点击某些功能按钮无反应或弹窗提示“未实现”**  
   - 原因：预算统计、分类管理等功能的图形界面及后端逻辑暂未开发  
   - 解决：仅使用已实现的收支管理功能（首页及账单记录页）  

2. **数据库连接失败**  
   - 检查 `db.properties` 中用户名、密码是否正确  
   - 确认MySQL服务已启动（可通过 `systemctl status mysql` 查看）  
   - 检查MySQL端口是否为3306（非默认端口需修改 `db.url`）

3. **启动后无界面**  
   - 检查是否在图形化环境中运行（纯命令行不支持Swing界面）  
   - 查看控制台错误日志，确认是否因数据库初始化失败导致  
   - Linux环境需安装X11图形库（`sudo apt install libx11-6 libxtst6`）

4. **数据乱码**  
   - 数据库默认使用 `utf8mb4` 字符集，若手动创建库表需确保字符集一致  


## 🧪 开发与测试
```bash
# 编译并执行单元测试
mvn clean test

# 仅打包不执行测试
mvn -DskipTests package
```


## 📜 许可证
本项目仅供学习与个人非商业使用，二次开发或商用需保留原作者信息。


**版本**：v1.0（部分功能待实现）   
**作者**： tangxianyu