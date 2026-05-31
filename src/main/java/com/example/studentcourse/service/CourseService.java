package com.example.studentcourse.service;

import com.example.studentcourse.entity.Course;
import com.example.studentcourse.repository.CourseRepository;
import com.example.studentcourse.repository.StudentCourseRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentCourseRepository studentCourseRepository;

    public CourseService(CourseRepository courseRepository, StudentCourseRepository studentCourseRepository) {
        this.courseRepository = courseRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    public List<Course> listAll() {
        return courseRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public List<Course> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return listAll();
        }
        return courseRepository.search(keyword.trim());
    }

    public Course getRequired(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("课程不存在"));
    }

    @Transactional
    public Course createCourse(Course course) {
        validateCourse(course);
        course.setId(null);
        course.setSelectedCount(0);
        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long courseId, Course input) {
        validateCourse(input);
        Course course = getRequired(courseId);
        if (input.getCapacity() < course.getSelectedCount()) {
            throw new IllegalArgumentException("课程容量不能小于已选人数");
        }
        course.setCourseName(input.getCourseName().trim());
        course.setTeacher(input.getTeacher().trim());
        course.setCredit(input.getCredit());
        course.setCapacity(input.getCapacity());
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = getRequired(courseId);
        studentCourseRepository.deleteByCourse_Id(course.getId());
        courseRepository.delete(course);
    }

    private void validateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("课程信息不能为空");
        }
        if (!StringUtils.hasText(course.getCourseName())) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        if (!StringUtils.hasText(course.getTeacher())) {
            throw new IllegalArgumentException("授课教师不能为空");
        }
        if (course.getCredit() == null || course.getCredit() <= 0) {
            throw new IllegalArgumentException("学分必须大于 0");
        }
        if (course.getCapacity() == null || course.getCapacity() <= 0) {
            throw new IllegalArgumentException("容量必须大于 0");
        }
    }
}
