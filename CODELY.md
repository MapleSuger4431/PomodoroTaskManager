

## Codely Structured Memories

### User
- [2026-09-15 15:33:28] 用户是小组课程项目（番茄任务管理系统）的组员B，负责任务模块代码已完成，当前处于写实验报告/准备答辩阶段（2026-09 期间），需要按"每个文件涉及哪些课程技术点（集合/泛型/数据库编程/序列化/反射/多线程/网络编程）+ 对应代码"的中文说明来直接引用进报告，同时需要 Git 协作流程的说明材料。

### Feedback

### Project
- [2026-09-12 18:20:53] 小组课程项目（番茄任务管理系统，纯 Java 11 + JDBC + SQL Server，无框架）：本用户是组员B，只负责任务模块 4 个文件（TaskDaoImpl、TaskDailyRecordDaoImpl、TaskService、TaskMenu），已完成并冒烟测试通过。组规：实体类与 DAO 接口签名冻结不许改；UI 不能直接 new DAO 必须经 Service；Service 不能直接写 SQL；Java 11 语法禁 var/record/switch 表达式；注释全中文；SQL 必须 PreparedStatement。数据库 PomodoroDB（localhost:1433，sa/123456）。本机 mvn 不在 PATH，编译验证用 javac -encoding UTF-8 -cp ~/.m2 中 mssql-jdbc-12.10.2.jre11.jar。
- [2026-09-14 16:08:59] PomodoroTaskManager 的 Git 工作流：远程 GitHub 仓库 github.com/MapleSuger4431/PomodoroTaskManager，主分支 master；提交信息为中文单行"实现xxx：文件列表"风格；用户要求每次功能提交在项目根目录"提交记录.md"中维护记录（含修改文件、功能说明、验证结果，已补记过用户模块/A 模块）；分支流程：在功能分支（如 masterA）提交 → push 远程同名新分支 → 合并回 master 并推送 origin/master。**Why:** 用户明确的仓库管理与提交文档要求。**How to apply:** 后续任何 git 提交/推送操作都遵循此流程，并同步更新"提交记录.md"。

### Reference

