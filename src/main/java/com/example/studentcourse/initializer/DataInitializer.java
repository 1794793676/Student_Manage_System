package com.example.studentcourse.initializer;

import com.example.studentcourse.entity.Course;
import com.example.studentcourse.entity.Student;
import com.example.studentcourse.entity.StudentCourse;
import com.example.studentcourse.entity.User;
import com.example.studentcourse.repository.CourseRepository;
import com.example.studentcourse.repository.StudentCourseRepository;
import com.example.studentcourse.repository.StudentRepository;
import com.example.studentcourse.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentCourseRepository studentCourseRepository;

    public DataInitializer(
            UserRepository userRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            StudentCourseRepository studentCourseRepository
    ) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = new User("admin", "admin123", "admin");
        User studentUser1 = new User("student001", "123456", "student");
        User studentUser2 = new User("student002", "123456", "student");
        userRepository.saveAll(List.of(admin, studentUser1, studentUser2));

        Student student1 = new Student("2024001", "李明", "软件工程", "2024", studentUser1);
        Student student2 = new Student("2024002", "王芳", "计算机科学与技术", "2024", studentUser2);
        studentRepository.saveAll(List.of(student1, student2));

        Course java = new Course("Java 程序设计", "张老师", 3, 60);
        Course database = new Course("数据库原理", "刘老师", 3, 45);
        Course web = new Course("Web 应用开发", "陈老师", 2, 50);
        Course software = new Course("软件工程", "周老师", 3, 1);
        Course ai = new Course("人工智能导论", "赵老师", 2, 40);
        Course network = new Course("计算机网络", "孙老师", 3, 55);
        courseRepository.saveAll(List.of(java, database, web, software, ai, network));

        addSelection(student1, java);
        addSelection(student1, software);
        addSelection(student2, web);
    }

    private void addSelection(Student student, Course course) {
        course.increaseSelectedCount();
        courseRepository.save(course);
        studentCourseRepository.save(new StudentCourse(student, course, LocalDateTime.now().minusDays(1)));
    }
}
