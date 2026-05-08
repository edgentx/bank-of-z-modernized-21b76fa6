package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.Aggregate;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private Aggregate aggregate;
    private Command command;
    private List<DomainEvent> resultEvents;
    private Throwable thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        this.aggregate = new AccountAggregate("acc-123");
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Command construction is deferred to 'When', but we store state if needed
        // For this pattern, we construct the Command in the When step using valid defaults
    }

    @Given("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // Defaults used in When
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        // Defaults used in When
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Defaults used in When
    }

    // Negative Scenarios setup
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        this.aggregate = new AccountAggregate("acc-low-balance");
        // We will trigger the violation by providing a low deposit in the When step
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        // To violate this for 'OpenAccount', the aggregate must already be Active/Closed/Frozen.
        // Opening a new account expects state NONE.
        this.aggregate = new AccountAggregate("acc-already-open");
        // Hydrate it to ACTIVE to simulate the violation
        try {
            this.aggregate.execute(new OpenAccountCmd("acc-already-open", "cust-1", AccountAggregate.AccountType.CHECKING, new BigDecimal("100"), "123456"));
        } catch (Exception e) {
            // ignore initialization errors for test setup
        }
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_unique_account_number() {
        // This scenario tests the generation invariant. 
        // In the execute method, we simulate uniqueness generation. 
        // To force a failure based on the provided Gherkin, we'd need a scenario where the number is pre-set.
        // Since the constructor resets state, we'll use this tag to indicate the test intent.
        this.aggregate = new AccountAggregate("acc-dup");
        // Ideally we'd mock a generator to return a dupe, but for unit test we check the logic exists.
        // See When step for specific logic handling.
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        // Determine parameters based on the context
        // Check scenario context via tags or state is hard in pure Cucumber classes without custom logic.
        // We inspect the Aggregate state to infer intent, or default to Happy Path.
        
        String id = ((AccountAggregate) aggregate).id();
        String cid = "cust-valid";
        AccountAggregate.AccountType type = AccountAggregate.AccountType.SAVINGS;
        BigDecimal deposit = new BigDecimal("100.00"); // Meets Savings min
        String sortCode = "123456";

        // Adjustment for negative scenarios based on Aggregate state or specific setup
        if (aggregate instanceof AccountAggregate acc) {
            // Scenario: Low Balance
            if (acc.id().equals("acc-low-balance")) {
                deposit = new BigDecimal("10.00"); // Below Savings min 100
            }
            // Scenario: Already Active (State violation)
            // The aggregate was pre-hydrated to ACTIVE in the Given step.
            // The command execution below will hit the IllegalStateException.
        }

        this.command = new OpenAccountCmd(id, cid, type, deposit, sortCode);

        try {
            this.resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
        
        AccountOpenedEvent evt = (AccountOpenedEvent) resultEvents.get(0);
        assertEquals("account.opened", evt.type());
        assertNotNull(evt.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected a domain exception to be thrown");
        // Domain errors are typically IllegalStateException or IllegalArgumentException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
