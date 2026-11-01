package com.example.listingai.user;

import com.example.listingai.credits.CreditService;
import com.example.listingai.subscription.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository users;
    private final SubscriptionService subscriptions;
    private final CreditService credits;

    public UserService(UserRepository users, SubscriptionService subscriptions, CreditService credits) {
        this.users = users;
        this.subscriptions = subscriptions;
        this.credits = credits;
    }

    @Transactional
    public User findOrCreate(String googleSub, String email, String name) {
        if (googleSub == null || googleSub.isBlank()) {
            throw new IllegalArgumentException("Google subject is required");
        }
        return users.findByGoogleSub(googleSub)
            .map(user -> {
                user.updateProfile(email, name);
                return user;
            })
            .orElseGet(() -> {
                User user = users.saveAndFlush(new User(googleSub, email, name));
                subscriptions.createFree(user.getId());
                credits.grantInitial(user.getId());
                return user;
            });
    }

    @Transactional(readOnly = true)
    public User getByGoogleSub(String googleSub) {
        return users.findByGoogleSub(googleSub)
            .orElseThrow(() -> new IllegalStateException("Authenticated user is missing"));
    }
}
