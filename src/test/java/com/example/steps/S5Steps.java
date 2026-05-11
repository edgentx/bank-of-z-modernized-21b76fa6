package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.account.repository.InMemoryAccountRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S5Steps {

    private AccountAggregate aggregate;
    private Throwable thrownException;
    private List<DomainEvent> resultingEvents;

    // --- Scenarios ---

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        this.aggregate = new AccountAggregate("acc-new-001");
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // No-op, data will be supplied in the command
    }

    @Given("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // No-op
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        // No-op
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // No-op
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        try {
            OpenAccountCmd cmd = new OpenAccountCmd(
                "acc-new-001",
                "cust-123",
                AccountType.SAVINGS,
                new BigDecimal("100.00"),
                "10-20-30"
            );
            resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        Assertions.assertNotNull(resultingEvents);
        Assertions.assertFalse(resultingEvents.isEmpty());
        Assertions.assertTrue(resultingEvents.get(0) instanceof AccountOpenedEvent);
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        this.aggregate = new AccountAggregate("acc-low-bal");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        this.aggregate = new AccountAggregate("acc-inactive");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        this.aggregate = new AccountAggregate("acc-duplicate");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || 
                              thrownException instanceof IllegalStateException);
    }
}
