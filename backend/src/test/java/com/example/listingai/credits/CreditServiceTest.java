package com.example.listingai.credits;

import com.example.listingai.user.User;
import com.example.listingai.user.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CreditServiceTest {
    private final UserRepository users = mock(UserRepository.class);
    private final CreditTransactionRepository transactions = mock(CreditTransactionRepository.class);
    private final CreditService service = new CreditService(users, transactions, 1);
    private final User user = new User("google-123", null, null);

    @Test
    void grantsWelcomeCreditOnceWithStableKey() {
        when(users.lockById(user.getId())).thenReturn(Optional.of(user));
        when(transactions.findByUserIdAndIdempotencyKey(user.getId(), "welcome:" + user.getId()))
            .thenReturn(Optional.empty(), Optional.of(new CreditTransaction(user.getId(),
                CreditTransaction.Type.GRANT, 1, "Initial free credit", "welcome:" + user.getId())));
        when(transactions.balance(user.getId())).thenReturn(0L);

        service.grantInitial(user.getId());
        service.grantInitial(user.getId());

        verify(transactions, times(1)).save(any(CreditTransaction.class));
    }

    @Test
    void rejectsDebitWhenBalanceIsInsufficient() {
        when(users.lockById(user.getId())).thenReturn(Optional.of(user));
        when(transactions.findByUserIdAndIdempotencyKey(user.getId(), "generation:1"))
            .thenReturn(Optional.empty());
        when(transactions.balance(user.getId())).thenReturn(1L);

        assertThatThrownBy(() -> service.debit(user.getId(), 2, "Generation", "generation:1"))
            .isInstanceOf(IllegalStateException.class).hasMessage("Insufficient credits");
        verify(transactions, never()).save(any());
    }

    @Test
    void repeatedDebitDoesNotSpendAgain() {
        CreditTransaction prior = new CreditTransaction(user.getId(), CreditTransaction.Type.DEBIT,
            -1, "Generation", "generation:1");
        when(users.lockById(user.getId())).thenReturn(Optional.of(user));
        when(transactions.findByUserIdAndIdempotencyKey(user.getId(), "generation:1"))
            .thenReturn(Optional.of(prior));

        assertThat(service.debit(user.getId(), 1, "Generation", "generation:1")).isSameAs(prior);
        verify(transactions, never()).balance(any());
        verify(transactions, never()).save(any());
    }

    @Test
    void reusedKeyCannotChangeTheOperation() {
        when(users.lockById(user.getId())).thenReturn(Optional.of(user));
        when(transactions.findByUserIdAndIdempotencyKey(eq(user.getId()), eq("same-key")))
            .thenReturn(Optional.of(new CreditTransaction(user.getId(), CreditTransaction.Type.GRANT,
                1, "Welcome", "same-key")));

        assertThatThrownBy(() -> service.refund(user.getId(), 1, "Refund", "same-key"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void balanceComesFromLedger() {
        when(transactions.balance(user.getId())).thenReturn(3L);
        assertThat(service.balance(user.getId())).isEqualTo(3);
    }
}
