package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    // Context variables
    private AccountAggregate account;
    private String providedAccountNumber;
    private AccountStatus providedNewStatus;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Constants for testing
    private static final BigDecimal MIN_BALANCE = new BigDecimal("100.00");

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Setup: Account Number, Active Status, Balance > Min, Min Balance
        this.account = new AccountAggregate("ACC-123", AccountStatus.ACTIVE, new BigDecimal("500.00"), MIN_BALANCE);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        this.providedAccountNumber = "ACC-123";
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        this.providedNewStatus = AccountStatus.FROZEN;
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            var cmd = new UpdateAccountStatusCmd(providedAccountNumber, providedNewStatus);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should emit exactly one event");
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent, "Event should be AccountStatusUpdatedEvent");
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        // Balance 50, Min 100. Trying to close (requires balance check in command logic per AC)
        this.account = new AccountAggregate("ACC-LOW", AccountStatus.ACTIVE, new BigDecimal("50.00"), MIN_BALANCE);
        this.providedAccountNumber = "ACC-LOW";
        this.providedNewStatus = AccountStatus.CLOSED; // Closing triggers balance check
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        // Status is FROZEN, trying to update
        this.account = new AccountAggregate("ACC-FRZ", AccountStatus.FROZEN, new BigDecimal("500.00"), MIN_BALANCE);
        this.providedAccountNumber = "ACC-FRZ";
        this.providedNewStatus = AccountStatus.CLOSED;
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        this.account = new AccountAggregate("ACC-ORIG", AccountStatus.ACTIVE, new BigDecimal("500.00"), MIN_BALANCE);
        this.providedAccountNumber = "ACC-FAKE"; // Mismatch
        this.providedNewStatus = AccountStatus.FROZEN;
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Exception should be thrown");
        // We accept IllegalStateException or IllegalArgumentException as domain errors
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
