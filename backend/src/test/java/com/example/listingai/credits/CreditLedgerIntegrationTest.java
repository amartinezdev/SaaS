package com.example.listingai.credits;

import com.example.listingai.user.User;
import com.example.listingai.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "RUN_LOCAL_INTEGRATION", matches = "true")
class CreditLedgerIntegrationTest {
    @Autowired private UserService users;
    @Autowired private CreditService credits;
    @Autowired private JdbcTemplate jdbc;

    @Test
    void newUserGetsOneCreditOnceAndRepeatedDebitIsIdempotent() {
        String googleSub = "ledger-test-" + UUID.randomUUID();
        User user = users.findOrCreate(googleSub, null, null);
        try {
            assertThat(credits.balance(user.getId())).isEqualTo(1);
            assertThat(users.findOrCreate(googleSub, null, null).getId()).isEqualTo(user.getId());
            assertThat(credits.balance(user.getId())).isEqualTo(1);

            credits.debit(user.getId(), 1, "Test generation", "test-generation:1");
            credits.debit(user.getId(), 1, "Test generation", "test-generation:1");
            assertThat(credits.balance(user.getId())).isZero();

            credits.refund(user.getId(), 1, "Test refund", "test-refund:1");
            assertThat(credits.balance(user.getId())).isEqualTo(1);
        } finally {
            jdbc.update("DELETE FROM credit_transactions WHERE user_id = ?", user.getId());
            jdbc.update("DELETE FROM subscriptions WHERE user_id = ?", user.getId());
            jdbc.update("DELETE FROM app_users WHERE id = ?", user.getId());
        }
    }

    @Test
    void simultaneousDebitsCannotSpendTheSameCreditTwice() throws Exception {
        User user = users.findOrCreate("concurrent-ledger-" + UUID.randomUUID(), null, null);
        try (var executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch start = new CountDownLatch(1);
            var first = executor.submit(() -> debitWhenReleased(user.getId(), "concurrent:1", start));
            var second = executor.submit(() -> debitWhenReleased(user.getId(), "concurrent:2", start));
            start.countDown();

            assertThat((first.get(10, TimeUnit.SECONDS) ? 1 : 0)
                + (second.get(10, TimeUnit.SECONDS) ? 1 : 0)).isEqualTo(1);
            assertThat(credits.balance(user.getId())).isZero();
        } finally {
            jdbc.update("DELETE FROM credit_transactions WHERE user_id = ?", user.getId());
            jdbc.update("DELETE FROM subscriptions WHERE user_id = ?", user.getId());
            jdbc.update("DELETE FROM app_users WHERE id = ?", user.getId());
        }
    }

    private boolean debitWhenReleased(UUID userId, String key, CountDownLatch start) throws InterruptedException {
        start.await();
        try {
            credits.debit(userId, 1, "Concurrent generation", key);
            return true;
        } catch (IllegalStateException insufficient) {
            assertThat(insufficient).hasMessage("Insufficient credits");
            return false;
        }
    }
}
