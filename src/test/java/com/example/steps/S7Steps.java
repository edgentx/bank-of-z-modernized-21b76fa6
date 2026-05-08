package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class S7Steps {

    private AccountAggregate account;
    private List<com.example.domain.shared.DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        // Use the constructor that creates a valid active account
        account = new AccountAggregate("ACC-123", "SAVINGS", BigDecimal.ZERO, Instant.now());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Account number is implicitly handled by the aggregate instance in these tests
        // The command targets the specific aggregate instance.
        Assertions.assertNotNull(account.id());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        // SAVINGS requires min balance. If we set it to > 0, the logic assumes we can't close.
        // The aggregate is initialized with a balance > 0 to simulate this state.
        account = new AccountAggregate("ACC-999", "SAVINGS", new BigDecimal("50.00"), Instant.now());
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        account = new AccountAggregate("ACC-888", "SAVINGS", BigDecimal.ZERO, Instant.now());
        // Manually force closed state (simulating it was closed previously) or suspended
        // For this test, we assume 'CLOSED' prevents closing again.
        // We simulate a previously closed account.
        account.markAsClosed(); 
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        // In this domain context, the aggregate IS the instance.
        // If we load an aggregate and the ID matches, it's immutable.
        // We simulate a scenario where the command attempts to change the ID (logic inside Command).
        // We create a valid aggregate, but the Command will be constructed with a DIFFERENT ID.
        account = new AccountAggregate("ACC-EXISTING", "SAVINGS", BigDecimal.ZERO, Instant.now());
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            // Scenario 4 Specifics: Trying to change ID via command payload
            if (account.id().equals("ACC-EXISTING")) {
                 // Construct command with a mismatched ID to test immutability error
                 CloseAccountCmd cmd = new CloseAccountCmd("ACC-MISMATCHED");
                 resultEvents = account.execute(cmd);
            } else {
                // Standard execution
                CloseAccountCmd cmd = new CloseAccountCmd(account.id());
                resultEvents = account.execute(cmd);
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        AccountClosedEvent event = (AccountClosedEvent) resultEvents.get(0);
        Assertions.assertEquals("ACC-123", event.aggregateId());
        Assertions.assertEquals("AccountClosed", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(caughtException);
        // Specific message assertions based on scenario
        if (caughtException.getMessage().contains("balance")) {
            Assertions.assertTrue(caughtException.getMessage().contains("Balance must be zero"));
        } else if (caughtException.getMessage().contains("Active")) {
            Assertions.assertTrue(caughtException.getMessage().contains("Account not active"));
        } else if (caughtException.getMessage().contains("immutable")) {
            Assertions.assertTrue(caughtException.getMessage().contains("immutable"));
        }
    }
}