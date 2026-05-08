package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
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
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        accountNumber = "ACC-123-456";
        aggregate = new AccountAggregate(accountNumber);
        // Setup valid state
        aggregate.setState("Active", new BigDecimal("1000.00"), "Savings", BigDecimal.ZERO);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // accountNumber already set in 'Given a valid Account aggregate'
        assertNotNull(accountNumber);
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        newStatus = "Frozen";
        assertNotNull(newStatus);
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            Command cmd = new UpdateAccountStatusCmd(accountNumber, newStatus);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNull(thrownException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.status.updated", resultEvents.get(0).type());
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        accountNumber = "ACC-LOW-BAL";
        aggregate = new AccountAggregate(accountNumber);
        // Set state such that balance < minimum
        aggregate.setState("Active", new BigDecimal("50.00"), "Premium", new BigDecimal("100.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        accountNumber = "ACC-NOT-ACTIVE";
        aggregate = new AccountAggregate(accountNumber);
        // Set state to Frozen or Closed
        aggregate.setState("Frozen", new BigDecimal("100.00"), "Standard", BigDecimal.ZERO);
        // The command will attempt to change status, but logic enforces Active
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        accountNumber = "ACC-ORIGINAL";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setState("Active", BigDecimal.ZERO, "Standard", BigDecimal.ZERO);
        // Simulate the violation by providing a mismatched account number in the command step
        // We'll change 'accountNumber' variable to something else before the When clause
        accountNumber = "ACC-MODIFIED-TAMPER"; 
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

}