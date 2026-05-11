package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
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

    private AccountAggregate account;
    private String accountNumber;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        this.accountNumber = "ACC-123";
        this.account = new AccountAggregate(this.accountNumber);
        // Setup valid state (Active, Zero Balance)
        this.account.setStatus(AccountAggregate.Status.ACTIVE);
        this.account.setBalance(BigDecimal.ZERO);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // The account number is already set in the context of the aggregate
        assertNotNull(accountNumber);
    }

    // --- Scenario: Balance Constraint Violation ---
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        this.accountNumber = "ACC-BAD-BAL";
        this.account = new AccountAggregate(this.accountNumber);
        this.account.setStatus(AccountAggregate.Status.ACTIVE);
        // Violation: Balance is 100, not 0
        this.account.setBalance(new BigDecimal("100.00"));
    }

    // --- Scenario: Status Constraint Violation ---
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        this.accountNumber = "ACC-BAD-STAT";
        this.account = new AccountAggregate(this.accountNumber);
        // Violation: Status is not ACTIVE (e.g. already CLOSED)
        this.account.setStatus(AccountAggregate.Status.CLOSED);
        this.account.setBalance(BigDecimal.ZERO);
    }

    // --- Scenario: Immutable/Unique Account Number Violation ---
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        this.accountNumber = "ACC-ORIG";
        this.account = new AccountAggregate(this.accountNumber);
        this.account.setStatus(AccountAggregate.Status.ACTIVE);
        this.account.setBalance(BigDecimal.ZERO);
        
        // To simulate the "Command mismatch" aspect of this invariant in a unit-testable way:
        // We will change the 'accountNumber' context so the command executed differs from the aggregate ID.
        // This simulates trying to close account A using a command intended for account B.
        this.accountNumber = "ACC-DIFFERENT";
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        CloseAccountCmd cmd = new CloseAccountCmd(accountNumber);
        try {
            resultingEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof AccountClosedEvent);
        assertEquals("account.closed", resultingEvents.get(0).type());
        assertEquals(AccountAggregate.Status.CLOSED, account.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNull(resultingEvents); // No events should be produced on failure
        assertNotNull(capturedException);
        // We expect either IllegalStateException or IllegalArgumentException depending on the specific invariant
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}