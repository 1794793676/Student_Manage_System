package com.example.studentcourse.config;

import com.example.studentcourse.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        String uri = request.getRequestURI();
        User currentUser = (User) request.getSession().getAttribute(SessionKeys.CURRENT_USER);

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        if (uri.startsWith(request.getContextPath() + "/student") && !currentUser.isStudent()) {
            response.sendRedirect(request.getContextPath() + "/login?forbidden=true");
            return false;
        }
        if (uri.startsWith(request.getContextPath() + "/admin") && !currentUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login?forbidden=true");
            return false;
        }
        return true;
    }
}
