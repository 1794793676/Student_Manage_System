package com.example.studentcourse.service;

import com.example.studentcourse.entity.Course;
import com.example.studentcourse.entity.Student;
import com.example.studentcourse.entity.StudentCourse;
import com.example.studentcourse.entity.User;
import com.example.studentcourse.repository.CourseRepository;
import com.example.studentcourse.repository.StudentCourseRepository;
import com.example.studentcourse.repository.StudentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SelectionService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentCourseRepository studentCourseRepository;

    public SelectionService(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            StudentCourseRepository studentCourseRepository
    ) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    @Transactional
    public void selectCourse(User currentUser, Long courseId) {
        Student student = resolveStudent(currentUser);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("课程不存在"));

        if (studentCourseRepository.existsByStudent_IdAndCourse_Id(student.getId(), courseId)) {
            throw new IllegalArgumentException("不能重复选择同一门课程");
        }
        if (course.isFull()) {
            throw new IllegalArgumentException("课程容量已满，不能继续选课");
        }

        course.increaseSelectedCount();
        courseRepository.save(course);
        studentCourseRepository.save(new StudentCourse(student, course, LocalDateTime.now()));
    }

    @Transactional
    public void dropCourse(User currentUser, Long courseId) {
        Student student = resolveStudent(currentUser);
        StudentCourse selection = studentCourseRepository.findByStudent_IdAndCourse_Id(student.getId(), courseId)
                .orElseThrow(() -> new IllegalArgumentException("只能退选自己已选的课程"));
        Course course = selection.getCourse();

        studentCourseRepository.delete(selection);
        course.decreaseSelectedCount();
        courseRepository.save(course);
    }

    public List<StudentCourse> listSelectedCourses(User currentUser) {
        Student student = resolveStudent(currentUser);
        return studentCourseRepository.findByStudent_IdOrderBySelectTimeDesc(student.getId());
    }

    public Set<Long> listSelectedCourseIds(User currentUser) {
        return listSelectedCourses(currentUser).stream()
                .map(selection -> selection.getCourse().getId())
                .collect(Collectors.toSet());
    }

    public List<StudentCourse> listAllSelections() {
        return studentCourseRepository.findAllWithDetails();
    }

    private Student resolveStudent(User currentUser) {
        if (currentUser == null || !currentUser.isStudent()) {
            throw new IllegalArgumentException("当前用户不是学生");
        }
        return studentRepository.findByUser_Id(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("学生资料不存在"));
    }
}
