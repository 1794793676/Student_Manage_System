package com.example.studentcourse.repository;

import com.example.studentcourse.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("""
            select c from Course c
            where lower(c.courseName) like lower(concat('%', :keyword, '%'))
               or lower(c.teacher) like lower(concat('%', :keyword, '%'))
            order by c.id
            """)
    List<Course> search(@Param("keyword") String keyword);
}
