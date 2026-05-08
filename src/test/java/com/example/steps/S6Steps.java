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

    private AccountAggregate account;
    private String testAccountNumber;
    private AccountAggregate.AccountStatus testNewStatus;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        testAccountNumber = "ACC-123";
        account = new AccountAggregate(testAccountNumber);
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("500.00"));
        account.setType(AccountAggregate.AccountType.STANDARD);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // testAccountNumber is already set in the Given step
        assertNotNull(testAccountNumber);
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        testNewStatus = AccountAggregate.AccountStatus.FROZEN;
        assertNotNull(testNewStatus);
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            Command cmd = new UpdateAccountStatusCmd(testAccountNumber, testNewStatus);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
    }

    // Scenario 2
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        testAccountNumber = "ACC-LOW";
        account = new AccountAggregate(testAccountNumber);
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        // Set balance below the hypothetical 100 check in the aggregate
        account.setBalance(new BigDecimal("50.00")); 
        account.setType(AccountAggregate.AccountType.PREMIUM);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalStateException);
    }

    // Scenario 3
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        testAccountNumber = "ACC-INACTIVE";
        account = new AccountAggregate(testAccountNumber);
        // Set status to FROZEN, so the check `if (this.status != AccountStatus.ACTIVE)` in the handler fails
        account.setStatus(AccountAggregate.AccountStatus.FROZEN); 
        account.setBalance(new BigDecimal("500.00"));
    }

    // Scenario 4
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutability() {
        // The aggregate is created with ID "FIXED-ID"
        testAccountNumber = "DIFFERENT-ID"; // Provide a command for a different ID
        account = new AccountAggregate("FIXED-ID");
        account.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        account.setBalance(new BigDecimal("100.00"));
    }
}
