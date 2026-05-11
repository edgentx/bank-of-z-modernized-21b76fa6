package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        aggregate = new AccountAggregate("ACC-12345");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Implicitly handled by the 'a valid Account aggregate' step setup
    }

    // S-7 Scenario: CloseAccountCmd rejected — Account balance cannot drop below the minimum required balance for its specific account type.
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance() {
        aggregate = new AccountAggregate("ACC-BALANCE");
        // Setup: Give it a balance so it cannot be closed
        aggregate.setBalance(new BigDecimal("100.50"));
    }

    // S-7 Scenario: CloseAccountCmd rejected — An account must be in an Active status to process withdrawals or transfers.
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        aggregate = new AccountAggregate("ACC-STATUS");
        // Setup: Set status to DORMANT or CLOSED
        aggregate.setStatus(AccountAggregate.AccountStatus.DORMANT);
    }

    // S-7 Scenario: CloseAccountCmd rejected — Account numbers must be uniquely generated and immutable.
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_identity() {
        aggregate = new AccountAggregate("ACC-ORIG");
        // This context sets up the aggregate.
        // The violation logic is triggered by sending a command with a MISMATCHED accountNumber.
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            // We use the aggregate's ID as the command's accountNumber, unless testing the immutable ID scenario.
            // For the "violates identity" scenario, we deliberately pass a different ID in the command.
            String cmdId;
            // Check if we are in the ID violation scenario (assuming specific naming or simple heuristic)
            // In a real framework, we might pass the ID via scenario context, but here we can infer from state or use the aggregate ID.
            // For the specific "violates identity" test, we will force a mismatch in the steps below or here.
            if (aggregate.id().equals("ACC-ORIG")) {
                cmdId = "ACC-FAKE"; // Trigger ID mismatch
            } else {
                cmdId = aggregate.id();
            }

            CloseAccountCmd cmd = new CloseAccountCmd(cmdId);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.closed", resultEvents.get(0).type());
        assertNull(caughtException, "Expected no exception, but got: " + caughtException);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException,
                "Exception should be a domain error (IllegalStateException or IllegalArgumentException), but was: " + caughtException.getClass().getSimpleName());
    }

}
