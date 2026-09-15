

## Codely Structured Memories

### User
- [2026-09-15 15:33:28] 用户是小组课程项目（番茄任务管理系统）的组员B，负责任务模块代码已完成，当前处于写实验报告/准备答辩阶段（2026-09 期间），需要按"每个文件涉及哪些课程技术点（集合/泛型/数据库编程/序列化/反射/多线程/网络编程）+ 对应代码"的中文说明来直接引用进报告，同时需要 Git 协作流程的说明材料。

### Feedback

### Project
- [2026-09-12 18:20:53] 小组课程项目（番茄任务管理系统，纯 Java 11 + JDBC + SQL Server，无框架）：本用户是组员B，只负责任务模块 4 个文件（TaskDaoImpl、TaskDailyRecordDaoImpl、TaskService、TaskMenu），已完成并冒烟测试通过。组规：实体类与 DAO 接口签名冻结不许改；UI 不能直接 new DAO 必须经 Service；Service 不能直接写 SQL；Java 11 语法禁 var/record/switch 表达式；注释全中文；SQL 必须 PreparedStatement。数据库 PomodoroDB（localhost:1433，sa/123456）。本机 mvn 不在 PATH，编译验证用 javac -encoding UTF-8 -cp ~/.m2 中 mssql-jdbc-12.10.2.jre11.jar。
### Reference

