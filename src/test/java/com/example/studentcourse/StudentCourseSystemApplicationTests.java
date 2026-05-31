package com.example.studentcourse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.studentcourse.entity.Course;
import com.example.studentcourse.entity.User;
import com.example.studentcourse.repository.CourseRepository;
import com.example.studentcourse.service.CourseService;
import com.example.studentcourse.service.SelectionService;
import com.example.studentcourse.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StudentCourseSystemApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SelectionService selectionService;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void presetAccountsCanLogin() {
        Optional<User> student = userService.login("student001", "123456");
        Optional<User> admin = userService.login("admin", "admin123");

        assertThat(student).isPresent();
        assertThat(student.get().isStudent()).isTrue();
        assertThat(admin).isPresent();
        assertThat(admin.get().isAdmin()).isTrue();
    }

    @Test
    void wrongPasswordIsRejected() {
        Optional<User> user = userService.login("student001", "wrong-password");

        assertThat(user).isEmpty();
    }

    @Test
    void studentCannotSelectSameCourseTwiceAndCanDropCourse() {
        User student = userService.login("student001", "123456").orElseThrow();
        Course course = courseService.createCourse(new Course("测试课程", "测试教师", 1, 2));

        selectionService.selectCourse(student, course.getId());

        assertThatThrownBy(() -> selectionService.selectCourse(student, course.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("不能重复选择");

        selectionService.dropCourse(student, course.getId());

        Course afterDrop = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(afterDrop.getSelectedCount()).isZero();
    }

    @Test
    void fullCourseCannotBeSelected() {
        User student1 = userService.login("student001", "123456").orElseThrow();
        User student2 = userService.login("student002", "123456").orElseThrow();
        Course course = courseService.createCourse(new Course("容量测试课", "容量教师", 1, 1));

        selectionService.selectCourse(student1, course.getId());

        assertThatThrownBy(() -> selectionService.selectCourse(student2, course.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("课程容量已满");
    }
}
