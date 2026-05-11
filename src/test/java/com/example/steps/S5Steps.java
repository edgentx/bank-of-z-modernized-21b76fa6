package com.example.steps;

import com.example.domain.account.model.*;
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

public class S5Steps {

    private AccountAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;
    private String testAccountId;
    private String testCustomerId = "customer-123";
    private AccountAggregate.AccountType testType = AccountAggregate.AccountType.SAVINGS;
    private BigDecimal testDeposit = new BigDecimal("100.00");
    private String testSortCode = "10-20-30";

    // Clean state for every scenario
    public S5Steps() {
        resetState();
    }

    private void resetState() {
        this.testAccountId = java.util.UUID.randomUUID().toString();
        this.aggregate = new AccountAggregate(testAccountId);
        this.resultEvents = null;
        this.caughtException = null;
    }

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        // Valid aggregate setup, nothing to do here other than ensuring it's initialized
        assertNotNull(aggregate);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        assertNotNull(testCustomerId);
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        assertNotNull(testType);
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        assertNotNull(testDeposit);
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        assertNotNull(testSortCode);
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        try {
            OpenAccountCmd cmd = new OpenAccountCmd(testAccountId, testCustomerId, testType, testDeposit, testSortCode);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        assertNull(caughtException, "Should not have thrown an exception");
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        DomainEvent event = resultEvents.get(0);
        assertEquals("account.opened", event.type());
        assertEquals(testAccountId, event.aggregateId());
        assertTrue(event instanceof AccountOpenedEvent);
        
        // Verify state mutation
        assertEquals(AccountAggregate.AccountStatus.ACTIVE, aggregate.getStatus());
        assertEquals(testDeposit, aggregate.getBalance());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        // Setup valid basics, but deposit will be too low for SAVINGS (min 100)
        testType = AccountAggregate.AccountType.SAVINGS;
        testDeposit = new BigDecimal("50.00"); // Violates minimum
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        // This aggregate starts as NONE.
        // To simulate the "Active" requirement violation for OPENING, we interpret this 
        // as trying to open an account that is somehow already ACTIVE (i.e., re-opening).
        // Or simply, we can set the status manually (reflection-like via package access if supported, or simulating)
        // Since AccountAggregate defaults to NONE, let's execute a valid command first to make it ACTIVE, 
        // then try to execute OpenCmd again.
        
        // First valid open
        OpenAccountCmd firstCmd = new OpenAccountCmd(testAccountId, testCustomerId, testType, testDeposit, testSortCode);
        aggregate.execute(firstCmd);
        
        // Now aggregate is ACTIVE. Running execute again will trigger the Status check.
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesUniqueness() {
        // Similar to above, open the account first to set 'immutable' flag true.
        OpenAccountCmd firstCmd = new OpenAccountCmd(testAccountId, testCustomerId, testType, testDeposit, testSortCode);
        aggregate.execute(firstCmd);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // We expect either IllegalStateException or IllegalArgumentException (domain errors)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
