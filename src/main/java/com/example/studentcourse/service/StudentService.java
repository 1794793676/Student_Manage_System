package com.example.studentcourse.service;

import com.example.studentcourse.entity.Student;
import com.example.studentcourse.entity.User;
import com.example.studentcourse.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getCurrentStudent(User user) {
        if (user == null || !user.isStudent()) {
            throw new IllegalArgumentException("当前用户不是学生");
        }
        return studentRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new IllegalStateException("学生资料不存在"));
    }
}
