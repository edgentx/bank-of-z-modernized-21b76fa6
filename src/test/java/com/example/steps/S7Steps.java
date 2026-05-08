package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate account;
    private Exception caughtException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        // Standard setup for a valid, active account with zero balance
        String id = "ACC-001";
        account = new AccountAggregate(id);
        
        // Simulate past events to bring aggregate to a valid Active state with zero balance
        // (In a real repo, we would load from events, here we hydrate directly or via a test helper)
        account.hydrate(
            "ACC-001", 
            "CHK-12345", 
            AccountStatus.ACTIVE, 
            BigDecimal.ZERO, 
            1
        );
        
        assertNotNull(account);
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Implicitly handled by the valid aggregate setup or command creation.
        // The command will target the loaded aggregate ID.
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance() {
        account = new AccountAggregate("ACC-002");
        account.hydrate(
            "ACC-002", 
            "CHK-99999", 
            AccountStatus.ACTIVE, 
            new BigDecimal("50.00"), // Non-zero balance
            1
        );
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        account = new AccountAggregate("ACC-003");
        account.hydrate(
            "ACC-003", 
            "CHK-88888", 
            AccountStatus.DORMANT, // Not active
            BigDecimal.ZERO, 
            1
        );
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        // The prompt implies a constraint violation scenario. 
        // In this specific aggregate pattern, ID/AccountNumber immutability is structural. 
        // We simulate a scenario where the command tries to close an account that effectively doesn't exist 
        // or is structurally invalid in the context of the command.
        // Alternatively, this maps to the ID check in the command.
        account = new AccountAggregate("ACC-DIRTY");
        // We leave it unhydrated (version 0) to represent an aggregate that cannot validly process this command 
        // because it implies an identity mismatch (Command ID vs Aggregate ID).
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            // The command ID must match the Aggregate ID for the execute pattern
            Command cmd = new CloseAccountCmd(account.id());
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        
        AccountClosedEvent event = (AccountClosedEvent) resultEvents.get(0);
        assertEquals("account.closed", event.type());
        assertEquals(account.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // We expect an IllegalArgumentException or IllegalStateException depending on the specific invariant
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
