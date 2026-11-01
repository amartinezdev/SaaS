package com.example.listingai.project;

import com.example.listingai.auth.GoogleOidcUserService;
import com.example.listingai.auth.SecurityConfig;
import com.example.listingai.user.User;
import com.example.listingai.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(SecurityConfig.class)
class ProjectControllerSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean ProjectService projects;
    @MockitoBean UserService users;
    @MockitoBean GoogleOidcUserService googleUsers;
    @MockitoBean ClientRegistrationRepository registrations;

    @Test
    void anonymousProjectListIsUnauthorized() throws Exception {
        mvc.perform(get("/api/projects")).andExpect(status().isUnauthorized());
    }

    @Test
    void inaccessibleProjectLooksNotFound() throws Exception {
        User owner = new User("google-123", "creator@example.com", "Creator");
        UUID projectId = UUID.randomUUID();
        when(users.getByGoogleSub("google-123")).thenReturn(owner);
        when(projects.get(owner.getId(), projectId)).thenThrow(new ProjectNotFoundException());

        mvc.perform(get("/api/projects/{id}", projectId)
                .with(oidcLogin().idToken(token -> token.claim("sub", "google-123"))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("PROJECT_NOT_FOUND"));
    }

    @Test
    void creatingProjectRequiresCsrf() throws Exception {
        String body = "{\"name\":\"Lamp\"}";
        mvc.perform(post("/api/projects").with(oidcLogin())
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isForbidden());
    }

    @Test
    void invalidProjectNameReturnsStableError() throws Exception {
        mvc.perform(post("/api/projects").with(oidcLogin()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"   \"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }
}
