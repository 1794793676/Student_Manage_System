package com.example.studentcourse.controller;

import com.example.studentcourse.config.SessionKeys;
import com.example.studentcourse.entity.User;
import com.example.studentcourse.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        User currentUser = (User) session.getAttribute(SessionKeys.CURRENT_USER);
        return currentUser == null ? "redirect:/login" : redirectHome(currentUser);
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) Boolean forbidden,
            HttpSession session,
            Model model
    ) {
        User currentUser = (User) session.getAttribute(SessionKeys.CURRENT_USER);
        if (currentUser != null && !Boolean.TRUE.equals(forbidden)) {
            return redirectHome(currentUser);
        }
        if (Boolean.TRUE.equals(forbidden)) {
            model.addAttribute("error", "无权访问目标页面，请使用对应角色账号登录");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Optional<User> user = userService.login(username, password);
        if (user.isEmpty()) {
            model.addAttribute("error", "用户名或密码错误");
            model.addAttribute("username", username);
            return "login";
        }

        session.setAttribute(SessionKeys.CURRENT_USER, user.get());
        return redirectHome(user.get());
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String redirectHome(User user) {
        if (user.isAdmin()) {
            return "redirect:/admin/home";
        }
        return "redirect:/student/home";
    }
}
