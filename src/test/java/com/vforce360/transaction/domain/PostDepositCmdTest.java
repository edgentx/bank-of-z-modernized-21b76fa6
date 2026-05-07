package com.vforce360.transaction.domain;

import com.vforce360.transaction.domain.command.PostDepositCmd;
import com.vforce360.transaction.domain.model.Transaction;
import com.vforce360.transaction.domain.shared.DomainError;
import com.vforce360.transaction.domain.shared.Result;
import com.vforce360.transaction.mocks.MockAccountStateAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TDD Red Phase Tests for S-10: PostDepositCmd
 * 
 * Covers:
 * 1. Successful execution and event emission.
 * 2. Rejection when amount <= 0.
 * 3. Rejection when transaction already posted (immutability).
 * 4. Rejection when resulting balance is invalid (invariant check).
 */
public class PostDepositCmdTest {

    private MockAccountStateAdapter mockAccountPort;
    private static final String TEST_ACCOUNT = "123456789";
    private static final UUID TEST_TX_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockAccountPort = new MockAccountStateAdapter();
        // Default valid balance state
        mockAccountPort.setBalance(TEST_ACCOUNT, new BigDecimal("100.00"));
    }

    @Test
    void testSuccessfullyExecutePostDepositCmd() {
        // Given
        Transaction transaction = new Transaction(TEST_TX_ID, TEST_ACCOUNT);
        BigDecimal amount = new BigDecimal("50.00");
        PostDepositCmd cmd = new PostDepositCmd(TEST_TX_ID, TEST_ACCOUNT, amount, "USD");

        // When
        Result<Void, DomainError> result = transaction.execute(cmd, mockAccountPort);

        // Then
        assertTrue(result.isSuccess(), "Command should succeed");
        assertThat(transaction.isPosted()).isTrue();
        // NOTE: In full implementation, verify that transaction.getUncommittedEvents() contains DepositPostedEvent
    }

    @Test
    void testRejectWhenAmountIsZero() {
        // Given
        Transaction transaction = new Transaction(TEST_TX_ID, TEST_ACCOUNT);
        BigDecimal amount = BigDecimal.ZERO;
        PostDepositCmd cmd = new PostDepositCmd(TEST_TX_ID, TEST_ACCOUNT, amount, "USD");

        // When
        Result<Void, DomainError> result = transaction.execute(cmd, mockAccountPort);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError().getMessage()).contains("Transaction amounts must be greater than zero");
    }

    @Test
    void testRejectWhenAmountIsNegative() {
        // Given
        Transaction transaction = new Transaction(TEST_TX_ID, TEST_ACCOUNT);
        BigDecimal amount = new BigDecimal("-10.00");
        PostDepositCmd cmd = new PostDepositCmd(TEST_TX_ID, TEST_ACCOUNT, amount, "USD");

        // When
        Result<Void, DomainError> result = transaction.execute(cmd, mockAccountPort);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError().getMessage()).contains("Transaction amounts must be greater than zero");
    }

    @Test
    void testRejectWhenTransactionAlreadyPosted() {
        // Given
        Transaction transaction = new Transaction(TEST_TX_ID, TEST_ACCOUNT);
        BigDecimal amount = new BigDecimal("50.00");
        
        // First execution succeeds
        PostDepositCmd firstCmd = new PostDepositCmd(TEST_TX_ID, TEST_ACCOUNT, amount, "USD");
        transaction.execute(firstCmd, mockAccountPort);
        assertThat(transaction.isPosted()).isTrue();

        // When trying to execute again on the same aggregate
        PostDepositCmd secondCmd = new PostDepositCmd(TEST_TX_ID, TEST_ACCOUNT, amount, "USD");
        Result<Void, DomainError> secondResult = transaction.execute(secondCmd, mockAccountPort);

        // Then
        assertThat(secondResult.isSuccess()).isFalse();
        assertThat(secondResult.getError().getMessage())
            .contains("Transactions cannot be altered or deleted once posted");
    }

    @Test
    void testRejectWhenResultingBalanceIsInvalid() {
        // Given
        // Set balance such that adding 50 exceeds limit (assuming limit is 1,000,000 in impl)
        mockAccountPort.setBalance(TEST_ACCOUNT, new BigDecimal("999999.99"));
        Transaction transaction = new Transaction(TEST_TX_ID, TEST_ACCOUNT);
        BigDecimal amount = new BigDecimal("50.00"); // 999999.99 + 50 > 1M limit (example check)
        PostDepositCmd cmd = new PostDepositCmd(TEST_TX_ID, TEST_ACCOUNT, amount, "USD");

        // When
        Result<Void, DomainError> result = transaction.execute(cmd, mockAccountPort);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError().getMessage())
            .contains("A transaction must result in a valid account balance");
    }
}
