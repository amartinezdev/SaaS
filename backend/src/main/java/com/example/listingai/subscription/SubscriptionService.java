package com.example.listingai.subscription;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubscriptionService {
    private final PlanRepository plans;
    private final SubscriptionRepository subscriptions;

    public SubscriptionService(PlanRepository plans, SubscriptionRepository subscriptions) {
        this.plans = plans;
        this.subscriptions = subscriptions;
    }

    @Transactional
    public void createFree(UUID userId) {
        Plan free = plans.findById("FREE").orElseThrow(() -> new IllegalStateException("FREE plan is missing"));
        subscriptions.save(new Subscription(userId, free));
    }

    @Transactional(readOnly = true)
    public String planCode(UUID userId) {
        return subscriptions.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User subscription is missing"))
            .getPlan().getCode();
    }
}
