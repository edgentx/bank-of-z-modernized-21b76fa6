package com.example.steps;

import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.PostWithdrawalCmd;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.model.WithdrawalPostedEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S11Steps {

    private TransactionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    // Given Steps

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        aggregate = new TransactionAggregate(
            "txn-123", 
            "acct-456", 
            new BigDecimal("1000.00")
        );
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Account number is handled in the aggregate setup or command creation
        // Placeholder for semantic clarity
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Amount is handled in command creation
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Currency is handled in command creation
    }

    // Violation States

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        aggregate = new TransactionAggregate("txn-1", "acct-1", new BigDecimal("100.00"));
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_cannot_be_altered_once_posted() {
        aggregate = new TransactionAggregate("txn-2", "acct-2", new BigDecimal("100.00"));
        // Manually force posted state to simulate the invariant condition
        // In a real scenario, we might execute a valid command first, but here we control the state directly for test setup
        aggregate.execute(new PostWithdrawalCmd("txn-2", "acct-2", new BigDecimal("10.00"), "USD"));
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        // Set balance low enough that a valid withdrawal amount would overdraft
        aggregate = new TransactionAggregate("txn-3", "acct-3", new BigDecimal("5.00"));
    }

    // When Steps

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        try {
            // Using a standard "valid" command for the positive case or where the violation is purely in aggregate state
            // For the negative amount test, we will override this in the step logic or create a specific command
            
            // Note: Gherkin context is flat, so we need to distinguish commands based on the scenario context.
            // For simplicity in this BDD framework, we use a standard command for most, but we will check specific scenarios if needed.
            // However, to strictly satisfy the "violates amount > 0" scenario, we need to pass a bad amount.
            
            BigDecimal amount = new BigDecimal("50.00");
            
            // Heuristic: If the balance is 5.00, the amount is 50.00 to trigger the balance error.
            if (aggregate.getCurrentBalance().compareTo(new BigDecimal("10.00")) < 0) {
                 amount = new BigDecimal("50.00"); // Trigger overdraft
            }
            
            // Heuristic for the amount violation check is tricky without scenario context injection.
            // We will rely on the fact that the first scenario sets valid data, and the specific violation scenarios
            // might need specific command construction. Given the flat nature of Cucumber steps without a context manager,
            // we will construct the command generally.
            
            // Special handling for the negative amount scenario based on aggregate ID (hacky but works for simple steps)
            if ("txn-1".equals(aggregate.id())) {
                amount = BigDecimal.ZERO; 
            }

            Command cmd = new PostWithdrawalCmd(aggregate.id(), aggregate.getAccountNumber(), amount, "USD");
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    // Then Steps

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof WithdrawalPostedEvent);
        
        WithdrawalPostedEvent event = (WithdrawalPostedEvent) resultEvents.get(0);
        assertEquals("withdrawal.posted", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Domain errors typically manifest as IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

}
