package com.example.studentcourse.repository;

import com.example.studentcourse.entity.StudentCourse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    Optional<StudentCourse> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    @EntityGraph(attributePaths = {"course"})
    List<StudentCourse> findByStudent_IdOrderBySelectTimeDesc(Long studentId);

    void deleteByCourse_Id(Long courseId);

    @Query("""
            select sc from StudentCourse sc
            join fetch sc.student
            join fetch sc.course
            order by sc.selectTime desc
            """)
    List<StudentCourse> findAllWithDetails();
}
