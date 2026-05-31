package com.example.studentcourse.controller;

import com.example.studentcourse.config.SessionKeys;
import com.example.studentcourse.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CourseController {

    @GetMapping("/courses")
    public String routeCourses(HttpSession session) {
        User currentUser = (User) session.getAttribute(SessionKeys.CURRENT_USER);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (currentUser.isAdmin()) {
            return "redirect:/admin/courses";
        }
        return "redirect:/student/courses";
    }
}
