package com.example.listingai.user;

import com.example.listingai.credits.CreditService;
import com.example.listingai.subscription.SubscriptionService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private final UserRepository repository = mock(UserRepository.class);
    private final SubscriptionService subscriptions = mock(SubscriptionService.class);
    private final CreditService credits = mock(CreditService.class);
    private final UserService service = new UserService(repository, subscriptions, credits);

    @Test
    void createsUserOnFirstLogin() {
        when(repository.findByGoogleSub("google-123")).thenReturn(Optional.empty());
        when(repository.saveAndFlush(any(User.class))).thenAnswer(call -> call.getArgument(0));

        User user = service.findOrCreate("google-123", "first@example.com", "First");

        assertThat(user.getId()).isNotNull();
        assertThat(user.getGoogleSub()).isEqualTo("google-123");
        verify(repository).saveAndFlush(user);
        verify(subscriptions).createFree(user.getId());
        verify(credits).grantInitial(user.getId());
    }

    @Test
    void reusesUserWhenEmailChanges() {
        User existing = new User("google-123", "old@example.com", "Old");
        when(repository.findByGoogleSub("google-123")).thenReturn(Optional.of(existing));

        User user = service.findOrCreate("google-123", "new@example.com", "New");

        assertThat(user.getId()).isEqualTo(existing.getId());
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        verify(repository, never()).save(any());
        verify(subscriptions, never()).createFree(existing.getId());
        verify(credits, never()).grantInitial(existing.getId());
    }
}
