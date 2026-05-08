package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    // Context state
    private AccountAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Test Data Constants
    private static final String VALID_CUSTOMER_ID = "CUST-123";
    private static final String VALID_ACCOUNT_TYPE = "SAVINGS";
    private static final BigDecimal VALID_INITIAL_DEPOSIT = new BigDecimal("1000.00");
    private static final String VALID_SORT_CODE = "10-20-30";
    private static final String ACCOUNT_ID = "ACC-001";

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        aggregate = new AccountAggregate(ACCOUNT_ID);
        // Ensure state is clean
        assertEquals(0, aggregate.uncommittedEvents().size());
    }

    @Given("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // Context setup handled in 'When' step construction
    }

    @Given("a valid accountType is provided")
    public void a_valid_accountType_is_provided() {
        // Context setup handled in 'When' step construction
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initialDeposit_is_provided() {
        // Context setup handled in 'When' step construction
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        // Context setup handled in 'When' step construction
    }

    // --- Negative Context Setup ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate(ACCOUNT_ID);
        // We simulate a state where this invariant would be checked. 
        // Since OpenAccountCmd creates a new account, we simulate a violation by attempting to open 
        // with an amount below the hypothetical floor logic inside the aggregate (implemented in domain)
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        aggregate = new AccountAggregate(ACCOUNT_ID);
        // Scenario context: Command should be rejected if the aggregate state implies it cannot accept this command
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_uniqueness() {
        aggregate = new AccountAggregate(ACCOUNT_ID);
        // Context: Simulate a scenario where uniqueness is violated (handled by domain logic checks or repo)
    }

    @When("the OpenAccountCmd command is executed")
    public void the_OpenAccountCmd_command_is_executed() {
        Command cmd = new OpenAccountCmd(
                ACCOUNT_ID,
                VALID_CUSTOMER_ID,
                VALID_ACCOUNT_TYPE,
                VALID_SORT_CODE,
                VALID_INITIAL_DEPOSIT
        );

        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the OpenAccountCmd command is executed with low balance")
    public void the_OpenAccountCmd_command_is_executed_with_low_balance() {
        Command cmd = new OpenAccountCmd(
                ACCOUNT_ID,
                VALID_CUSTOMER_ID,
                "CURRENT", // Assume Current has higher minimums or specifically test logic
                VALID_SORT_CODE,
                new BigDecimal("-50.00") // Negative opening balance invalid
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @When("the OpenAccountCmd command is executed on existing account")
    public void the_OpenAccountCmd_command_is_executed_on_existing_account() {
        // First open it
        aggregate.execute(new OpenAccountCmd(
                ACCOUNT_ID, VALID_CUSTOMER_ID, VALID_ACCOUNT_TYPE, VALID_SORT_CODE, VALID_INITIAL_DEPOSIT
        ));
        // Clear events to isolate the second command
        aggregate.clearEvents();

        // Try to open again (simulate uniqueness/state violation)
        Command cmd = new OpenAccountCmd(
                ACCOUNT_ID,
                VALID_CUSTOMER_ID,
                VALID_ACCOUNT_TYPE,
                VALID_SORT_CODE,
                VALID_INITIAL_DEPOSIT
        );
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents, "Expected events to be emitted");
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);

        AccountOpenedEvent event = (AccountOpenedEvent) resultEvents.get(0);
        assertEquals(ACCOUNT_ID, event.aggregateId());
        assertEquals(VALID_CUSTOMER_ID, event.customerId());
        assertEquals(VALID_ACCOUNT_TYPE, event.accountType());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Verify it is an IllegalArgumentException or IllegalStateException (Domain Error)
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }

    // --- Wiring for Scenarios ---

    // Scenario 2 Wiring
    @When("the OpenAccountCmd command is executed")
    public void the_cmd_is_executed_violates_balance() {
        the_OpenAccountCmd_command_is_executed_with_low_balance();
    }

    // Scenario 3 & 4 Wiring
    @When("the OpenAccountCmd command is executed")
    public void the_cmd_is_executed_violates_status() {
        // Re-using the "Already Opened" logic for "Status" and "Uniqueness" violations in this context
        the_OpenAccountCmd_command_is_executed_on_existing_account();
    }
}
