# 学生选课管理系统

基于 Java 17、Spring Boot、Thymeleaf、Spring Data JPA 和 H2 数据库实现的学生选课管理系统实验项目。系统支持学生登录选课、退课、查看已选课程，也支持管理员维护课程和查看学生选课记录。

## 技术栈

- Java 17
- Spring Boot 3.2.5
- Maven
- Thymeleaf
- Spring Data JPA
- H2 内存数据库

## 功能列表

- 学生登录、管理员登录、退出登录
- Session 保存登录状态，拦截未登录或角色不匹配的访问
- 学生查看课程列表，按课程名称或教师搜索课程
- 学生选课、退课、查看已选课程
- 防止重复选课，课程满员后禁止继续选课
- 管理员添加、修改、删除课程
- 删除课程时同步删除相关选课记录
- 管理员查看课程已选人数和全部选课记录

## 运行步骤

```bash
mvn clean package
mvn spring-boot:run
```

启动成功后访问：

- 登录页：http://localhost:8080/login
- H2 控制台：http://localhost:8080/h2-console

H2 连接信息：

- JDBC URL：`jdbc:h2:mem:student_course_db`
- User Name：`sa`
- Password：留空

## 测试账号

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 学生 | student001 | 123456 |
| 学生 | student002 | 123456 |
| 管理员 | admin | admin123 |

## 页面地址

- `/login`：登录页面
- `/student/home`：学生首页
- `/student/courses`：课程列表与选课
- `/student/selected`：我的选课与退课
- `/admin/home`：管理员首页
- `/admin/courses`：课程管理
- `/admin/selections`：选课记录

## 项目结构

```text
src/main/java/com/example/studentcourse
├── config
├── controller
├── entity
├── initializer
├── repository
├── service
└── StudentCourseSystemApplication.java

src/main/resources
├── static/css/style.css
├── templates
└── application.yml

docs
├── 实验说明.md
└── uml.md
```

## 手动验收建议

1. 使用 `student001 / 123456` 登录，查看课程列表。
2. 选择一门未满课程，再次选择同一课程，确认系统提示不能重复选课。
3. 访问“我的选课”，退选刚才选择的课程。
4. 使用 `admin / admin123` 登录，添加、修改、删除课程。
5. 使用学生账号访问 `/admin/courses`，确认被拦截到登录页并提示无权限。
