package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        account = new AccountAggregate("ACC-123");
        // Simulate account opening to set valid state
        account.execute(new com.example.domain.account.model.OpenAccountCmd(
            "ACC-123", "ACC-123", BigDecimal.ZERO, "ACTIVE"
        ));
        account.clearEvents();
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled implicitly by the aggregate initialization in the previous step
        // or verified within the command execution context
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_with_non_zero_balance() {
        account = new AccountAggregate("ACC-456");
        account.execute(new com.example.domain.account.model.OpenAccountCmd(
            "ACC-456", "ACC-456", new BigDecimal("100.00"), "ACTIVE"
        ));
        account.clearEvents();
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_with_invalid_status() {
        account = new AccountAggregate("ACC-789");
        account.execute(new com.example.domain.account.model.OpenAccountCmd(
            "ACC-789", "ACC-789", BigDecimal.ZERO, "DORMANT"
        ));
        account.clearEvents();
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_with_mismatched_id() {
        account = new AccountAggregate("ACC-ORIG");
        account.execute(new com.example.domain.account.model.OpenAccountCmd(
            "ACC-ORIG", "ACC-ORIG", BigDecimal.ZERO, "ACTIVE"
        ));
        account.clearEvents();
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        Command cmd;
        
        // Context check: If this is the "immutable id" scenario, send mismatched ID
        if ("ACC-ORIG".equals(account.id())) {
             // Try to close with a different account number than the aggregate ID
             cmd = new CloseAccountCmd("ACC-FAKE");
        } else {
             cmd = new CloseAccountCmd(account.id());
        }

        try {
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "Should emit one event");
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent, "Event type should be AccountClosedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Should have thrown exception");
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        assertTrue(capturedException instanceof IllegalStateException || 
                   capturedException instanceof IllegalArgumentException,
                   "Exception should be a domain rule violation");
    }
}