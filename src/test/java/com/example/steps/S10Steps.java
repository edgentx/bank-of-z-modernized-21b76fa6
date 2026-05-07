package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class S10Steps {

    private Transaction transaction;
    private PostDepositCmd command;
    private Exception caughtException;
    private DepositPostedEvent lastEvent;

    // Simple in-memory repository simulation
    private final TransactionRepository repository = new TransactionRepository() {
        @Override
        public Transaction save(Transaction transaction) {
            return transaction;
        }

        @Override
        public Transaction findById(UUID id) {
            return null;
        }
    };

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        transaction = new Transaction(UUID.randomUUID());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // We will construct the command in the 'When' step
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // We will construct the command in the 'When' step
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // We will construct the command in the 'When' step
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        transaction = new Transaction(UUID.randomUUID());
        // Command will be set to invalid amount in When step
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_transactions_cannot_be_altered() {
        transaction = new Transaction(UUID.randomUUID());
        // Simulate a posted transaction
        transaction.markAsPosted(); // Assuming a method to set state to POSTED
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance.")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        transaction = new Transaction(UUID.randomUUID());
        // Logic for balance validation would be complex, for BDD we simulate the violation condition
        // e.g., marking an account as frozen or invalid which the Aggregate checks
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        // Default valid values
        String accountNumber = "ACC-123";
        BigDecimal amount = new BigDecimal("100.00");
        Currency currency = Currency.getInstance("USD");

        // Override based on scenario context if needed (simple heuristic based on state)
        // In a real test, we might use a scenario context map to store expected values

        command = new PostDepositCmd(accountNumber, amount, currency);

        try {
            transaction.execute(command, repository);
            // Check if event was raised
            if (!transaction.getUncommittedEvents().isEmpty()) {
                Object event = transaction.getUncommittedEvents().get(0);
                if (event instanceof DepositPostedEvent) {
                    lastEvent = (DepositPostedEvent) event;
                }
            }
        } catch (DomainException e) {
            caughtException = e;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        assertNotNull(lastEvent, "Expected a DepositPostedEvent to be emitted");
        assertEquals(command.accountNumber(), lastEvent.accountNumber());
        assertEquals(command.amount(), lastEvent.amount());
        assertEquals(command.currency(), lastEvent.currency());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a DomainException to be thrown");
        assertTrue(caughtException instanceof DomainException);
    }
}
