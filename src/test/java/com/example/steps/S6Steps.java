package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {

    private AccountAggregate account;
    private UpdateAccountStatusCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Scenario 1 Helpers
    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // ID "acc-1" is used for the aggregate ID
        account = new AccountAggregate("acc-1");
        // Ensure defaults are valid (Active, 1000.00 balance, Checking)
        account.setStatus("ACTIVE");
        account.setBalance(new BigDecimal("1000.00"));
        account.setType("CHECKING");
        account.setAccountNumber("ACC-123");
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // The aggregate is initialized with a valid number.
        // The command is constructed in the 'When' step.
        // This step effectively validates the pre-state.
        Assertions.assertNotNull(account);
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        // We will use "FROZEN" as the new status
    }

    // Scenario 2 Helpers
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesAccountBalance() {
        account = new AccountAggregate("acc-2");
        account.setStatus("ACTIVE");
        // Set balance below the minimum required for CHECKING (100.00)
        account.setBalance(new BigDecimal("50.00")); 
        account.setType("CHECKING");
        account.setAccountNumber("ACC-LOW");
    }

    // Scenario 3 Helpers
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        account = new AccountAggregate("acc-3");
        // Set status to something other than ACTIVE
        account.setStatus("FROZEN");
        account.setBalance(new BigDecimal("1000.00"));
        account.setType("CHECKING");
        account.setAccountNumber("ACC-FRZ");
    }

    // Scenario 4 Helpers
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        account = new AccountAggregate("acc-4");
        // We simulate a violation by setting the internal number to null, 
        // which triggers the logic check in execute().
        account.setAccountNumber(null);
    }

    // Execution
    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            // We construct the command. The specific status value matters less for error cases 
            // as the invariant check happens before the switch in this design, 
            // but we pass a valid target status anyway.
            cmd = new UpdateAccountStatusCmd("ACC-123", "FROZEN");
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    // Outcomes
    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        Assertions.assertNull(caughtException, "Should not have thrown exception: " + caughtException);
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        Assertions.assertEquals("acc-1", event.aggregateId());
        Assertions.assertEquals("FROZEN", event.newStatus());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected an exception to be thrown");
        Assertions.assertTrue(caughtException instanceof IllegalStateException);
    }
}