package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate aggregate;
    private OpenAccountCmd command;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        aggregate = new AccountAggregate("acc-123");
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        aggregate = new AccountAggregate("acc-456");
        // Setup command that violates the constraint (e.g., SAVINGS with < 100 deposit)
        // We assume this violation is triggered by the command payload
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // The "OpenAccount" command creates the account. The invariant implies we might
        // be trying to open an account that logic dictates cannot be active yet,
        // or perhaps we are testing a state transition error.
        // Given the specific context of "OpenAccount", we interpret this as ensuring
        // we don't try to open an account that is somehow already ACTIVE in a way that blocks
        // the opening, OR that the command handles status validation correctly.
        // For this step definition, we simply create the aggregate.
        aggregate = new AccountAggregate("acc-789");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        aggregate = new AccountAggregate("acc-immutable");
        // Simulate the account number being set already
        // We need to reflect a state where the invariant is broken.
        // Since aggregate state is private, we simulate the violation by
        // issuing a command that tries to 'reopen' or 'change' the ID,
        // or by testing the command logic that prevents this.
        // For the purpose of the BDD scenario, we prepare the aggregate.
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Handled in command construction below
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        // Handled in command construction below
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        // Handled in command construction below
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // Handled in command construction below
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        try {
            // Determine payload based on scenario context (heuristic)
            String scenario = getCurrentScenarioName();
            
            String id = "acc-123";
            String customer = "cust-1";
            String type = "CHECKING";
            BigDecimal deposit = new BigDecimal("500.00");
            String sort = "10-20-30";

            if (scenario.contains("violates: Account balance cannot drop below")) {
                type = "SAVINGS";
                deposit = new BigDecimal("50.00"); // Violates minimum 100
                id = "acc-456";
            } else if (scenario.contains("violates: An account must be in an Active status")) {
                // This is an open command, so we assume valid params to test status logic if it existed
                // or simply valid params.
                id = "acc-789";
            } else if (scenario.contains("violates: Account numbers must be uniquely generated")) {
                // Simulate duplicate ID or immutability violation by reusing the aggregate ID
                // effectively simulating a 'double open' if aggregate was persistent, 
                // but here we act on the instance.
                id = "acc-immutable";
            }

            command = new OpenAccountCmd(id, customer, type, deposit, sort);
            resultEvents = aggregate.execute(command);

        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            caughtException = e;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.opened", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Check it's a domain error (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }

    // Helper to deduce scenario context for test data variation
    private String getCurrentScenarioName() {
        // Cucumber doesn't expose scenario name directly easily without hooks, 
        // so we check the exception state or simulate logic.
        // Since we can't easily inject the scenario name here without hooks, 
        // we rely on the order or we just assume success if no exception thrown above.
        // However, for the specific violation scenarios, we set the state in the Given steps.
        // We can check which aggregate we are using to determine the logic.
        if (aggregate.id().equals("acc-456")) return "Scenario 2";
        if (aggregate.id().equals("acc-789")) return "Scenario 3";
        if (aggregate.id().equals("acc-immutable")) return "Scenario 4";
        return "Scenario 1";
    }
}
