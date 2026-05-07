package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
public class S10Steps {

    private Transaction aggregate;
    private PostDepositCmd command;
    private DepositPostedEvent resultEvent;
    private Exception domainError;

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        // Create a fresh, unposted transaction
        aggregate = new Transaction(UUID.randomUUID());
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Command will be built in the When step, this just sets context
    }

    @And("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Context
    }

    @And("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Context
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        try {
            // Assuming default valid values if not set by specific 'Given' overrides
            if (command == null) {
                command = new PostDepositCmd("ACC-123", new BigDecimal("100.00"), "USD");
            }
            resultEvent = aggregate.execute(command);
        } catch (IllegalArgumentException | IllegalStateException e) {
            domainError = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        Assertions.assertNotNull(resultEvent, "Expected event to be emitted");
        Assertions.assertNull(domainError, "Expected no domain error");
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateThatViolatesTransactionAmountsMustBeGreaterThanZero() {
        aggregate = new Transaction(UUID.randomUUID());
        // Setup command with invalid amount
        command = new PostDepositCmd("ACC-123", new BigDecimal("-50.00"), "USD");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(domainError, "Expected a domain error");
        Assertions.assertNull(resultEvent, "Expected no event to be emitted");
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatViolatesTransactionsCannotBeAlteredOrDeletedOncePosted() {
        aggregate = new Transaction(UUID.randomUUID());
        // Manually post it to make it immutable
        aggregate.apply(new DepositPostedEvent(UUID.randomUUID(), "ACC-123", BigDecimal.ZERO, "USD"));
        // Setup command to try and post again
        command = new PostDepositCmd("ACC-123", new BigDecimal("10.00"), "USD");
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesATransactionMustResultInAValidAccountBalance() {
        aggregate = new Transaction(UUID.randomUUID());
        // Simulate a state where the balance logic fails (e.g., aggregate cap reached)
        // For simplicity, we pass a flag or specific value that triggers the validation failure in the domain logic
        command = new PostDepositCmd("INVALID-BALANCE-ACC", new BigDecimal("100.00"), "USD");
    }
}
