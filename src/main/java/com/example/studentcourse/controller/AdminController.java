package com.example.studentcourse.controller;

import com.example.studentcourse.entity.Course;
import com.example.studentcourse.service.CourseService;
import com.example.studentcourse.service.SelectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CourseService courseService;
    private final SelectionService selectionService;

    public AdminController(CourseService courseService, SelectionService selectionService) {
        this.courseService = courseService;
        this.selectionService = selectionService;
    }

    @GetMapping("/home")
    public String home() {
        return "admin-home";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.listAll());
        return "admin-courses";
    }

    @GetMapping("/courses/new")
    public String newCourse(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("pageTitle", "添加课程");
        return "course-form";
    }

    @PostMapping("/courses")
    public String createCourse(
            @ModelAttribute Course course,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            courseService.createCourse(course);
            redirectAttributes.addFlashAttribute("success", "课程添加成功");
            return "redirect:/admin/courses";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("course", course);
            model.addAttribute("pageTitle", "添加课程");
            model.addAttribute("error", ex.getMessage());
            return "course-form";
        }
    }

    @GetMapping("/courses/{courseId}/edit")
    public String editCourse(@PathVariable Long courseId, Model model) {
        model.addAttribute("course", courseService.getRequired(courseId));
        model.addAttribute("pageTitle", "修改课程");
        return "course-form";
    }

    @PostMapping("/courses/{courseId}/edit")
    public String updateCourse(
            @PathVariable Long courseId,
            @ModelAttribute Course course,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            courseService.updateCourse(courseId, course);
            redirectAttributes.addFlashAttribute("success", "课程修改成功");
            return "redirect:/admin/courses";
        } catch (IllegalArgumentException ex) {
            course.setId(courseId);
            model.addAttribute("course", course);
            model.addAttribute("pageTitle", "修改课程");
            model.addAttribute("error", ex.getMessage());
            return "course-form";
        }
    }

    @PostMapping("/courses/{courseId}/delete")
    public String deleteCourse(@PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(courseId);
            redirectAttributes.addFlashAttribute("success", "课程删除成功，相关选课记录已同步删除");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/selections")
    public String selections(Model model) {
        model.addAttribute("selections", selectionService.listAllSelections());
        return "admin-selections";
    }
}
