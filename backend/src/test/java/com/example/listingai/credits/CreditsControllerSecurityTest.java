package com.example.listingai.credits;

import com.example.listingai.auth.GoogleOidcUserService;
import com.example.listingai.auth.SecurityConfig;
import com.example.listingai.subscription.SubscriptionService;
import com.example.listingai.user.User;
import com.example.listingai.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreditsController.class)
@Import(SecurityConfig.class)
class CreditsControllerSecurityTest {
    @Autowired MockMvc mvc;
    @MockitoBean UserService users;
    @MockitoBean SubscriptionService subscriptions;
    @MockitoBean CreditService credits;
    @MockitoBean GoogleOidcUserService googleUsers;
    @MockitoBean ClientRegistrationRepository registrations;

    @Test
    void anonymousCannotReadBalance() throws Exception {
        mvc.perform(get("/api/me/credits")).andExpect(status().isUnauthorized());
    }

    @Test
    void balanceUsesAuthenticatedGoogleIdentity() throws Exception {
        User user = new User("google-123", null, null);
        when(users.getByGoogleSub("google-123")).thenReturn(user);
        when(subscriptions.planCode(user.getId())).thenReturn("FREE");
        when(credits.balance(user.getId())).thenReturn(1L);

        mvc.perform(get("/api/me/credits")
                .with(oidcLogin().idToken(token -> token.claim("sub", "google-123"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.plan").value("FREE"))
            .andExpect(jsonPath("$.balance").value(1));
    }
}
