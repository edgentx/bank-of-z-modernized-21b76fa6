package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
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

    private AccountAggregate aggregate;
    private String customerId;
    private String accountType;
    private BigDecimal initialDeposit;
    private String sortCode;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Scenario 1: Success
    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        aggregate = new AccountAggregate("acc-123");
    }

    @And("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        this.customerId = "cust-456";
    }

    @And("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        this.accountType = "SAVINGS";
    }

    @And("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        this.initialDeposit = new BigDecimal("150.00"); // Valid for SAVINGS (min 100)
    }

    @And("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        this.sortCode = "10-20-30";
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        OpenAccountCmd cmd = new OpenAccountCmd(aggregate.id(), customerId, accountType, initialDeposit, sortCode);
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
        AccountOpenedEvent event = (AccountOpenedEvent) resultEvents.get(0);
        assertEquals("account.opened", event.type());
    }

    // Scenario 2: Minimum Balance Violation
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate("acc-violation-balance");
        customerId = "cust-1";
        accountType = "CURRENT"; // Requires 500
        initialDeposit = new BigDecimal("50.00"); // Too low
        sortCode = "10-20-30";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException);
        assertTrue(capturedException.getMessage().contains("below minimum required balance"));
    }

    // Scenario 3: Active Status (Simulated by trying to open twice, second one fails because number is immutable/set)
    // Note: The aggregate starts PENDING and moves to ACTIVE. The invariant "must be in Active status to process withdrawals"
    // is handled by WithdrawalCmd usually, but here we test OpenAccountCmd rejection.
    // The only way OpenAccountCmd is rejected based on the aggregate state in this BDD is if we consider the account number immutable.
    // Or, if the Aggregate was somehow Active and we tried to open it again (Double Open).
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("acc-violation-status");
        // Open it once to make it Active
        aggregate.execute(new OpenAccountCmd(aggregate.id(), "cust", "STUDENT", BigDecimal.ZERO, "sc"));
        // Now it is ACTIVE. Attempting to open it again should be rejected (Invariant: Account number immutable)
        customerId = "cust-new";
        accountType = "CURRENT";
        initialDeposit = new BigDecimal("600");
        sortCode = "sc";
    }

    // Scenario 4: Immutable Account Number
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_account_number_immutable() {
        aggregate = new AccountAggregate("acc-violation-immutable");
        // Open it once
        aggregate.execute(new OpenAccountCmd(aggregate.id(), "cust", "STUDENT", BigDecimal.ZERO, "sc"));
        
        // Setup for second attempt
        customerId = "cust"; 
        accountType = "STUDENT";
        initialDeposit = BigDecimal.ZERO;
        sortCode = "sc";
    }

}
