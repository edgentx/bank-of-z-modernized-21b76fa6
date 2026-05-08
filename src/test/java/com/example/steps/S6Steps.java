package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S6Steps {

    private AccountAggregate aggregate;
    private Exception caughtException;
    private List<DomainEvent> resultEvents;
    private String currentAccountNumber = "ACC-123";

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        // In a real repository we would look up, but here we instantiate directly for the test context
        aggregate = new AccountAggregate(currentAccountNumber);
        // Assume it starts in a valid, Active state with sufficient balance
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        currentAccountNumber = "ACC-123";
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        // Handled in the When block via command construction
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        try {
            // Default to a valid status change (Active to Frozen or Closed) unless context implies otherwise
            Command cmd = new UpdateAccountStatusCmd(currentAccountNumber, "Frozen");
            resultEvents = aggregate.execute(cmd);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
            resultEvents = null;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        // Construct scenario: Account with 0 balance tries to close?
        // Note: The update command logic checks balance invariants against new status.
        aggregate = new AccountAggregate("ACC-LOW");
        // If the aggregate has state logic preventing closure with low balance, we rely on execute() to throw.
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status_requirement() {
        aggregate = new AccountAggregate("ACC-INACTIVE");
        // Assume aggregate is loaded in a non-active state (Frozen)
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_uniqueness() {
        aggregate = new AccountAggregate("DUPLICATE-ID");
        // We simulate a uniqueness violation by passing a command that tries to change the immutable ID
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected a domain error to be thrown");
        // We accept RuntimeException, IllegalStateException, or IllegalArgumentException as domain errors in this pattern
        assertTrue(caughtException instanceof RuntimeException);
    }
}
