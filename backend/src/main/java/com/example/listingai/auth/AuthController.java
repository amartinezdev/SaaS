package com.example.listingai.auth;

import com.example.listingai.user.User;
import com.example.listingai.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    /**
     * Forces Spring Security to resolve the CSRF token and expose the cookie
     * through CookieCsrfTokenRepository.
     */
    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @GetMapping("/me")
    public CurrentUserResponse me(@AuthenticationPrincipal OidcUser principal) {
        User user = users.getByGoogleSub(principal.getSubject());
        return new CurrentUserResponse(user.getId(), user.getEmail(), user.getName());
    }

    public record CurrentUserResponse(UUID id, String email, String name) {}
}
