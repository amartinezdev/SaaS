package com.example.listingai.auth;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Forces Spring Security to resolve the CSRF token and expose the cookie
     * through CookieCsrfTokenRepository.
     */
    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    /**
     * Returns the raw Google profile of the authenticated session.
     * Replace with the persisted User (keyed by google_sub, see .ai/domain/users.md)
     * once the user entity/repository exists — see .ai/state/next.md, Priority 1.
     */
    @GetMapping("/me")
    public Map<String, Object> me(OAuth2User principal) {
        return Map.of(
            "sub", principal.getAttribute("sub"),
            "email", principal.getAttribute("email"),
            "name", principal.getAttribute("name")
        );
    }
}
