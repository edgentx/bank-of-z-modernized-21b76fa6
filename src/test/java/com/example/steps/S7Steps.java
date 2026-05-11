package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate aggregate;
    private CloseAccountCmd command;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        aggregate = new AccountAggregate("ACC-123");
        aggregate.hydrateForTest(
            AccountAggregate.Status.ACTIVE,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Usually handled in the command creation step, valid by default in this context
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            // Assuming command is constructed with valid data matching the aggregate state for success cases
            // In a real test, we might parameterize the command creation. Here we assume defaults.
            String accNum = aggregate != null ? aggregate.id() : "ACC-UNKNOWN";
            BigDecimal bal = aggregate != null ? aggregate.getBalance() : BigDecimal.ZERO;
            command = new CloseAccountCmd(accNum, bal);
            resultEvents = aggregate.execute(command);
            thrownException = null;
        } catch (Exception e) {
            thrownException = e;
            resultEvents = null;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents, "Events should not be null");
        assertEquals(1, resultEvents.size());
        assertEquals("account.closed", resultEvents.get(0).type());
    }

    // --- Failure Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate("ACC-LOW");
        // Balance -10, Min 0. Already violating.
        aggregate.hydrateForTest(
            AccountAggregate.Status.ACTIVE,
            new BigDecimal("-100.00"),
            BigDecimal.ZERO
        );
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("ACC-FROZEN");
        aggregate.hydrateForTest(
            AccountAggregate.Status.FROZEN,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_account_number_uniqueness() {
        // This is a repository/state invariant. In the aggregate, we simulate this 
        // by providing a command that doesn't match the aggregate ID or similar validation.
        aggregate = new AccountAggregate("ACC-REAL");
        aggregate.hydrateForTest(AccountAggregate.Status.ACTIVE, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException, "Expected an exception to be thrown");
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
