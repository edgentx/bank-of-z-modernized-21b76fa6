package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    // Context variables
    private AccountAggregate aggregate;
    private String customerId;
    private String accountType;
    private BigDecimal initialDeposit;
    private String sortCode;
    private String accountId;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        this.accountId = "ACC-123-TEST";
        // A 'valid' aggregate for opening usually means a fresh, uninitialized aggregate root
        this.aggregate = new AccountAggregate(accountId);
    }

    @Given("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        this.customerId = "CUST-001";
    }

    @Given("a valid accountType is provided")
    public void a_valid_accountType_is_provided() {
        this.accountType = "STANDARD";
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initialDeposit_is_provided() {
        this.initialDeposit = new BigDecimal("500.00");
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        this.sortCode = "10-20-30";
    }

    @When("the OpenAccountCmd command is executed")
    public void the_OpenAccountCmd_command_is_executed() {
        try {
            OpenAccountCmd cmd = new OpenAccountCmd(accountId, customerId, accountType, initialDeposit, sortCode);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size(), "One event should be emitted");
        assertEquals("account.opened", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException, "Expected an exception to be thrown");
        // In Java Domain, domain errors are typically exceptions (IllegalArgument, IllegalState, etc.)
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
                "Exception should be a domain error type");
    }

    // --- Scenarios for Violations ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        a_valid_Account_aggregate();
        a_valid_customerId_is_provided();
        a_valid_sortCode_is_provided();
        // Setting up a scenario that violates the rule: Low deposit for a Premium account
        this.accountType = "PREMIUM"; // Requires 1000
        this.initialDeposit = new BigDecimal("50.00"); // Only 50
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        // For Opening, this AC is interpreted as: An account that is already Active/Frozen/Closed cannot be 'Opened' again.
        // Or, simulating an aggregate that is already initialized.
        this.accountId = "ACC-INVALID-STATUS";
        this.aggregate = new AccountAggregate(accountId);
        
        // Simulate the aggregate already being Active (by running a successful command or forcing state)
        // Since we can't easily force state without a command in this pattern, we execute a valid 'open' first 
        // to put it in ACTIVE, then the TEST STEP will try to open it again, triggering the rejection.
        
        // Setup data for the first open
        this.customerId = "CUST-STATUS-FAIL";
        this.accountType = "STANDARD";
        this.initialDeposit = new BigDecimal("200.00");
        this.sortCode = "10-10-10";
        
        // Execute open to move to ACTIVE
        OpenAccountCmd setupCmd = new OpenAccountCmd(accountId, customerId, accountType, initialDeposit, sortCode);
        aggregate.execute(setupCmd);
        
        // The Scenario's 'When' step will now attempt to execute ANOTHER OpenAccountCmd on this active aggregate.
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_uniqueness() {
        // Reuse the logic for Status: attempting to open an account with an ID that already exists.
        a_Account_aggregate_that_violates_status();
    }

}
