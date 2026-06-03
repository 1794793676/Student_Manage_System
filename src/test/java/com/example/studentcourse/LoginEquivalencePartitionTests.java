package com.example.studentcourse;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.studentcourse.config.SessionKeys;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LoginEquivalencePartitionTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPageContainsRequiredFieldsAndButtons() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"username\"")))
                .andExpect(content().string(containsString("name=\"password\"")))
                .andExpect(content().string(containsString("登录")))
                .andExpect(content().string(containsString("取消")))
                .andExpect(content().string(containsString("minlength=\"6\"")))
                .andExpect(content().string(containsString("maxlength=\"10\"")))
                .andExpect(content().string(containsString("type=\"submit\"")))
                .andExpect(content().string(containsString("type=\"reset\"")));
    }

    @Test
    void studentCanLoginWithValidCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "student001")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/home"))
                .andExpect(request().sessionAttribute(
                        SessionKeys.CURRENT_USER,
                        hasProperty("username", is("student001"))
                ));
    }

    @Test
    void adminCanLoginWithValidCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "admin01")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/home"))
                .andExpect(request().sessionAttribute(
                        SessionKeys.CURRENT_USER,
                        hasProperty("username", is("admin01"))
                ));
    }

    @ParameterizedTest(name = "{index}: {0}/{1} -> {2}")
    @MethodSource("invalidLoginCases")
    void invalidLoginCasesReturnExpectedErrors(String username, String password, String expectedError)
            throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", expectedError))
                .andExpect(model().attribute("username", username.trim()));
    }

    static Stream<Arguments> invalidLoginCases() {
        return Stream.of(
                Arguments.of("", "123456", "用户名不能为空"),
                Arguments.of("student001", "", "密码不能为空"),
                Arguments.of("abcde", "123456", "用户名长度必须为6-10位"),
                Arguments.of("abcdefghijk", "123456", "用户名长度必须为6-10位"),
                Arguments.of("student001", "12345", "密码长度必须为6-10位"),
                Arguments.of("student001", "12345678901", "密码长度必须为6-10位"),
                Arguments.of("user001", "123456", "用户名或密码错误"),
                Arguments.of("student001", "654321", "用户名或密码错误"),
                Arguments.of("user06", "123456", "用户名或密码错误"),
                Arguments.of("student001", "1234567890", "用户名或密码错误")
        );
    }
}
