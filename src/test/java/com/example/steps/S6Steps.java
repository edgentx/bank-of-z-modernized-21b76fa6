package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber Steps for S-6: UpdateAccountStatusCmd.
 */
public class S6Steps {

    private AccountAggregate account;
    private Command cmd;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        this.account = new AccountAggregate("ACC-123");
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Handled in the When block construction
    }

    @Given("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // Handled in the When block construction
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        // Default command if not specified in scenario flow (assumes success context)
        if (cmd == null) {
            cmd = new UpdateAccountStatusCmd("ACC-123", "FROZEN", null);
        }
        try {
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertFalse(resultEvents.isEmpty(), "At least one event should be emitted");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof AccountStatusUpdatedEvent, "Event should be AccountStatusUpdatedEvent");
        
        AccountStatusUpdatedEvent statusEvent = (AccountStatusUpdatedEvent) event;
        assertEquals("account.status.updated", statusEvent.type());
        assertEquals("ACC-123", statusEvent.aggregateId());
    }

    // --- Error Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        this.account = new AccountAggregate("ACC-LOW-BAL");
        // Set balance below the minimum threshold (assumed 100 in the aggregate logic)
        this.account.setBalance(new BigDecimal("50.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        this.account = new AccountAggregate("ACC-NOT-ACTIVE");
        this.account.setStatus("FROZEN"); // Not Active
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        // The aggregate itself is valid, but the command will attempt to change the number
        this.account = new AccountAggregate("ACC-ORIG");
    }

    // We need to customize the command setup for the negative cases 
    // since the When clause is generic.
    // In real Cucumber, we might use a table or specific Given to store the cmd params.
    // For simplicity, we check the state in the Given/When flow.

    @When("the UpdateAccountStatusCmd command is executed with context {string}")
    public void theUpdateAccountStatusCmdCommandIsExecutedWithContext(String context) {
        // Determine the command based on the scenario state (captured implicitly by class fields)
        if (account.getId().equals("ACC-NOT-ACTIVE")) {
            cmd = new UpdateAccountStatusCmd("ACC-NOT-ACTIVE", "PROCESS_TRANSFER", "TRANSFER");
        } else if (account.getId().equals("ACC-ORIG")) {
            cmd = new UpdateAccountStatusCmd("ACC-CHANGED", "ACTIVE", null);
        } else {
             // Default for the balance violation case
             cmd = new UpdateAccountStatusCmd("ACC-LOW-BAL", "FROZEN", null);
        }

        try {
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException,
                "Expected IllegalStateException or IllegalArgumentException, got: " + thrownException.getClass().getSimpleName());
    }
}
