package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S11Steps {

    private Transaction transaction;
    private PostWithdrawalCmd command;
    private WithdrawalPostedEvent resultEvent;
    private Exception caughtException;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        transaction = new Transaction(UUID.randomUUID(), new BigDecimal("1000.00"));
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled in context setup or specific command construction
    }

    @And("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Handled in context setup
    }

    @And("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Handled in context setup
    }

    @When("the PostWithdrawalCmd command is executed")
    public void the_PostWithdrawalCmd_command_is_executed() {
        try {
            if (command == null) {
                // Default valid command setup if scenarios don't specify explicitly before
                command = new PostWithdrawalCmd(transaction.getId(), "ACC-123", new BigDecimal("50.00"), "USD");
            }
            resultEvent = transaction.execute(command);
        } catch (DomainViolationException e) {
            caughtException = e;
        }
    }

    @Then("a withdrawal.posted event is emitted")
    public void a_withdrawal_posted_event_is_emitted() {
        assertNotNull(resultEvent, "Event should not be null");
        assertEquals("ACC-123", resultEvent.accountNumber());
        assertEquals(new BigDecimal("50.00"), resultEvent.amount());
        assertEquals(new BigDecimal("950.00"), resultEvent.resultingBalance());
        assertFalse(transaction.getUncommittedEvents().isEmpty());
    }

    // --- Error Scenarios ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amount_must_be_greater_than_zero() {
        transaction = new Transaction(UUID.randomUUID(), new BigDecimal("100.00"));
        command = new PostWithdrawalCmd(transaction.getId(), "ACC-123", BigDecimal.ZERO, "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_Transaction_aggregate_that_violates_cannot_be_altered() {
        transaction = new Transaction(UUID.randomUUID(), new BigDecimal("100.00"));
        // Manually forcing the posted state to simulate an already posted transaction
        transaction.markAsPostedAlready(new BigDecimal("100.00"));
        command = new PostWithdrawalCmd(transaction.getId(), "ACC-123", new BigDecimal("10.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        transaction = new Transaction(UUID.randomUUID(), new BigDecimal("10.00")); // Low balance
        command = new PostWithdrawalCmd(transaction.getId(), "ACC-123", new BigDecimal("100.00"), "USD"); // High withdrawal
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a DomainViolationException to be thrown");
        assertTrue(caughtException instanceof DomainViolationException);
    }
}
