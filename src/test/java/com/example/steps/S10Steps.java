package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S10Steps {

    private Transaction aggregate;
    private PostDepositCmd command;
    private DomainError error;
    private DepositPostedEvent event;

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        // Create a standard valid aggregate that can accept deposits
        aggregate = new Transaction(UUID.randomUUID());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd();
        }
        this.command.accountNumber = "ACC-123-456";
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd();
        }
        this.command.amount = new BigDecimal("100.00");
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        if (this.command == null) {
            this.command = new PostDepositCmd();
        }
        this.command.currency = "USD";
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        this.aggregate = new Transaction(UUID.randomUUID());
        this.command = new PostDepositCmd("ACC-123", new BigDecimal("-50.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void a_Transaction_aggregate_that_violates_immutability() {
        // Create an aggregate, post a valid transaction, then try to modify it
        UUID id = UUID.randomUUID();
        this.aggregate = new Transaction(id);
        // Simulate a previous state where the transaction is already sealed/posted
        // By marking the aggregate version > 0 (or simulating a loaded aggregate)
        aggregate.markAsPosted(); 
        
        this.command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void a_Transaction_aggregate_that_violates_balance_validation() {
        // Setup a scenario where adding this amount would overflow (e.g. > MAX_DECIMAL or business rule)
        this.aggregate = new Transaction(UUID.randomUUID());
        // Setup aggregate to have a balance near limit for validation logic simulation
        aggregate.setMaxBalanceReached(); 
        
        this.command = new PostDepositCmd("ACC-999", new BigDecimal("999999999.00"), "USD");
    }

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            // Ensure command fields are populated if they were missed in Given steps for the happy path
            if (command.amount == null) command.amount = new BigDecimal("100.00");
            if (command.currency == null) command.currency = "USD";
            if (command.accountNumber == null) command.accountNumber = "ACC-TEST";

            event = aggregate.execute(command);
        } catch (DomainError e) {
            this.error = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(event, "Event should not be null");
        Assertions.assertNotNull(event.transactionId);
        Assertions.assertEquals(command.accountNumber, event.accountNumber);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(error, "Expected a DomainError to be thrown");
        Assertions.assertFalse(error.getMessage().isEmpty());
    }
}