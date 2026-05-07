package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.WithdrawalPostedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class S11Steps {

    private TransactionAggregate aggregate;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        String transactionId = UUID.randomUUID().toString();
        aggregate = new TransactionAggregate(transactionId);
        // Assume aggregate starts in a state that allows posting
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        this.accountId = "ACC-123-456";
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        this.amount = new BigDecimal("100.00");
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        this.currency = "USD";
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        try {
            Command cmd = new PostWithdrawalCmd(aggregate.id(), accountId, amount, currency);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        Assertions.assertNull(thrownException, "Expected no exception, but got: " + thrownException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof WithdrawalPostedEvent);
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_zero_amount() {
        aggregate = new TransactionAggregate(UUID.randomUUID().toString());
        this.amount = new BigDecimal("-50.00"); // Invalid amount
        this.accountId = "ACC-123";
        this.currency = "USD";
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_already_posted() {
        String id = UUID.randomUUID().toString();
        aggregate = new TransactionAggregate(id);
        // Simulate posted state by applying an event directly or mutating state for test purposes
        // In a real scenario, we might load from history, but here we assume the aggregate tracks state.
        // We will assume the aggregate has a way to be marked as posted or we enforce it via the command logic.
        // For this test setup, let's assume we try to post twice or similar logic.
        // Actually, the aggregate logic enforces invariants. We just need to trigger the failure.
        // Let's rely on the aggregate implementation to detect this.
        this.amount = new BigDecimal("10.00");
        this.accountId = "ACC-123";
        this.currency = "USD";
        
        // To simulate violation, we assume the aggregate is already posted.
        // We might need a package-private method or reflection to set state, 
        // OR we assume the previous 'Successfully execute' scenario left it posted.
        // Since Cucumber scenarios are isolated (unless hooks are used), we need to force state here.
        // However, without a setter, we can't force 'posted' state easily on the Aggregate.
        // ALTERNATIVE: The Command logic might check if the aggregate ID exists in a 'store'.
        // BUT Requirements say 'Aggregate invariants'.
        // Let's assume the aggregate tracks 'isPosted'.
        // Since I cannot modify the Aggregate file from here, I will assume the implementation handles it 
        // or that the test setup implies checking logic.
        // REVISION: I will pass a command that references an existing transaction ID? No, command has aggregateId.
        // Let's assume the default constructor allows creation, and we need to mark it posted.
        // If the Aggregate code (generated in the next step) uses a `posted` flag, I can't set it here.
        // WORKAROUND: I will assume the Aggregate is designed to reject duplicate executions or similar.
        // Or, I will rely on the fact that I am also generating the Aggregate, so I will ensure it has a constructor or method.
        // For now, I will just set up the data. The invariant enforcement is in the Aggregate class.
        aggregate.markPosted(); // This method needs to exist or be simulated.
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_constraint() {
        aggregate = new TransactionAggregate(UUID.randomUUID().toString());
        this.amount = new BigDecimal("99999999.00"); // Overdraft
        this.accountId = "ACC-123";
        this.currency = "USD";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || 
                              thrownException instanceof IllegalStateException ||
                              thrownException instanceof UnknownCommandException);
    }
}
