package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate aggregate;
    private String accountNumber;
    private String newStatus;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        accountNumber = "ACC-123";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setBalance(BigDecimal.valueOf(100.00));
        aggregate.setStatus("Active");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // accountNumber already set in previous step
        assertNotNull(accountNumber);
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        newStatus = "Frozen";
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        try {
            Command cmd = new UpdateAccountStatusCmd(accountNumber, newStatus);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals("Frozen", event.newStatus());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_with_balance_violation() {
        accountNumber = "ACC-FAIL-BAL";
        aggregate = new AccountAggregate(accountNumber);
        // Set a non-zero balance. The command will try to Close it, which logic rejects.
        aggregate.setBalance(BigDecimal.valueOf(500.00)); 
        aggregate.setStatus("Active");
        newStatus = "Closed";
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status_constraint() {
        // Scenario context: Trying to reopen a closed account using this command
        accountNumber = "ACC-FAIL-STAT";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setStatus("Closed");
        newStatus = "Active"; // Attempting to reopen via Update command is rejected
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_with_immutability_violation() {
        // Setup a valid aggregate
        accountNumber = "ACC-ORIG";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setStatus("Active");
        
        // Simulate an attempt to change the account number by sending a command with a different ID
        // Note: In the code, the aggregate ID is immutable (final). The command mismatch triggers the error.
        newStatus = "Frozen";
        // We will tamper with the accountNumber in the cmd in the When step logic or pass a different one
        // For clarity, we change the accountNumber variable to something that doesn't match the aggregate ID.
        accountNumber = "ACC-FAKE"; 
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Verify it's a domain logic error (IllegalStateException or IllegalArgumentException)
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }

}
