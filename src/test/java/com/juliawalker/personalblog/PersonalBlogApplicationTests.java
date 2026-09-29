package com.juliawalker.personalblog;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Full application context with the real security chain and the admin account from
 * {@code src/test/resources/config/application.properties} (password "test-password").
 */
@SpringBootTest
@AutoConfigureMockMvc
class PersonalBlogApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_correctCredentials_authenticatesAndRedirectsToDashboard() throws Exception {
        // Act + Assert
        mockMvc.perform(formLogin("/login").user("admin").password("test-password"))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"))
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void login_wrongPassword_redirectsBackWithError() throws Exception {
        // Act + Assert
        mockMvc.perform(formLogin("/login").user("admin").password("wrong"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void loginPage_withErrorParameter_showsErrorMessage() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/login").param("error", "").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Invalid username or password.")));
    }

    @Test
    void logout_post_redirectsToLoginWithMessage() throws Exception {
        // Act + Assert
        mockMvc.perform(logout("/logout"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void home_anonymous_isPublic() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

}
