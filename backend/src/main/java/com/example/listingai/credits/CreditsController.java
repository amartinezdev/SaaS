package com.example.listingai.credits;

import com.example.listingai.subscription.SubscriptionService;
import com.example.listingai.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/me/credits")
public class CreditsController {
    private final UserService users;
    private final SubscriptionService subscriptions;
    private final CreditService credits;

    public CreditsController(UserService users, SubscriptionService subscriptions, CreditService credits) {
        this.users = users;
        this.subscriptions = subscriptions;
        this.credits = credits;
    }

    @GetMapping
    public CreditsResponse get(@AuthenticationPrincipal OidcUser principal) {
        UUID userId = users.getByGoogleSub(principal.getSubject()).getId();
        return new CreditsResponse(subscriptions.planCode(userId), credits.balance(userId));
    }

    public record CreditsResponse(String plan, long balance) {}
}
