package com.example.studentcourse.controller;

import com.example.studentcourse.config.SessionKeys;
import com.example.studentcourse.entity.Student;
import com.example.studentcourse.entity.User;
import com.example.studentcourse.service.CourseService;
import com.example.studentcourse.service.SelectionService;
import com.example.studentcourse.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final SelectionService selectionService;

    public StudentController(
            StudentService studentService,
            CourseService courseService,
            SelectionService selectionService
    ) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.selectionService = selectionService;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        User currentUser = currentUser(session);
        model.addAttribute("student", studentService.getCurrentStudent(currentUser));
        return "student-home";
    }

    @GetMapping("/courses")
    public String courses(
            @RequestParam(required = false) String keyword,
            HttpSession session,
            Model model
    ) {
        User currentUser = currentUser(session);
        model.addAttribute("courses", courseService.search(keyword));
        model.addAttribute("selectedCourseIds", selectionService.listSelectedCourseIds(currentUser));
        model.addAttribute("keyword", keyword);
        return "student-courses";
    }

    @PostMapping("/courses/{courseId}/select")
    public String selectCourse(
            @PathVariable Long courseId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            selectionService.selectCourse(currentUser(session), courseId);
            redirectAttributes.addFlashAttribute("success", "选课成功");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/student/courses";
    }

    @GetMapping("/selected")
    public String selected(HttpSession session, Model model) {
        User currentUser = currentUser(session);
        Student student = studentService.getCurrentStudent(currentUser);
        model.addAttribute("student", student);
        model.addAttribute("selections", selectionService.listSelectedCourses(currentUser));
        return "student-selected";
    }

    @PostMapping("/selected/{courseId}/drop")
    public String dropCourse(
            @PathVariable Long courseId,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            selectionService.dropCourse(currentUser(session), courseId);
            redirectAttributes.addFlashAttribute("success", "退课成功");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/student/selected";
    }

    private User currentUser(HttpSession session) {
        return (User) session.getAttribute(SessionKeys.CURRENT_USER);
    }
}
