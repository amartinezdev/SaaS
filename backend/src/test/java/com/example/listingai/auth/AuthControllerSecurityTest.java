package com.example.listingai.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.listingai.user.User;
import com.example.listingai.user.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerSecurityTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockitoBean UserService users;
    @MockitoBean GoogleOidcUserService googleUsers;
    @MockitoBean ClientRegistrationRepository registrations;

    @Test
    void currentUserRequiresAuthentication() throws Exception {
        mvc.perform(get("/api/auth/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void currentUserReturnsLocalIdentity() throws Exception {
        User user = new User("google-123", "creator@example.com", "Creator");
        when(users.getByGoogleSub("google-123")).thenReturn(user);

        mvc.perform(get("/api/auth/me").with(oidcLogin()
                .idToken(token -> token.claim("sub", "google-123"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(user.getId().toString()))
            .andExpect(jsonPath("$.email").value("creator@example.com"))
            .andExpect(jsonPath("$.sub").doesNotExist());
    }

    @Test
    void csrfBootstrapIsPublic() throws Exception {
        mvc.perform(get("/api/auth/csrf"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void googleLoginStartsRedirectFlow() throws Exception {
        ClientRegistration google = ClientRegistration.withRegistrationId("google")
            .clientId("test-client")
            .clientSecret("test-secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
            .tokenUri("https://oauth2.googleapis.com/token")
            .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
            .userNameAttributeName("sub")
            .scope("openid", "email", "profile")
            .clientName("Google")
            .build();
        when(registrations.findByRegistrationId("google")).thenReturn(google);

        mvc.perform(get("/oauth2/authorization/google"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("https://accounts.google.com/**"));
    }

    @Test
    void logoutRequiresCsrfAndThenReturnsNoContent() throws Exception {
        mvc.perform(post("/logout").with(oidcLogin()))
            .andExpect(status().isForbidden());

        MvcResult bootstrap = mvc.perform(get("/api/auth/csrf"))
            .andExpect(status().isOk())
            .andReturn();
        String token = json.readTree(bootstrap.getResponse().getContentAsString()).get("token").asText();
        Cookie cookie = bootstrap.getResponse().getCookie("XSRF-TOKEN");

        mvc.perform(post("/logout")
                .with(oidcLogin())
                .cookie(cookie)
                .header("X-XSRF-TOKEN", token))
            .andExpect(status().isNoContent());
    }
}
