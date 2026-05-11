package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.account.repository.AccountRepository;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    // Simple In-Memory Repository implementation for testing
    static class InMemoryAccountRepository implements AccountRepository {
        private AccountAggregate aggregate;
        @Override
        public void save(AccountAggregate aggregate) { this.aggregate = aggregate; }
        @Override
        public Optional<AccountAggregate> findById(String id) { return Optional.ofNullable(aggregate); }
    }

    private AccountAggregate aggregate;
    private final AccountRepository repository = new InMemoryAccountRepository();
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    // Scenario 1: Success
    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        String accountId = "acc-123";
        this.aggregate = new AccountAggregate(accountId);
    }

    @And("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Context handled in execution
    }

    @And("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // Context handled in execution
    }

    @And("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        // Context handled in execution
    }

    @And("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Context handled in execution
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        try {
            // Use valid data for success scenario
            OpenAccountCmd cmd = new OpenAccountCmd(
                    "acc-123",
                    "cust-456",
                    "SAVINGS",
                    new BigDecimal("100.00"),
                    "10-20-30"
            );
            this.resultingEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNull(thrownException, "Should not have thrown exception");
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof AccountOpenedEvent);
        assertEquals("account.opened", resultingEvents.get(0).type());
    }

    // Scenario 2: Balance violation
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_with_min_balance_violation() {
        this.aggregate = new AccountAggregate("acc-min-violation");
    }

    // Reuse When from above

    @When("the OpenAccountCmd command is executed with negative deposit")
    public void execute_open_cmd_negative() {
        try {
             OpenAccountCmd cmd = new OpenAccountCmd(
                    "acc-min-violation",
                    "cust-456",
                    "CURRENT",
                    new BigDecimal("-50.00"), // Violation: negative balance
                    "10-20-30"
            );
            this.resultingEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            this.thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

    // Scenario 3: Status violation
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_with_status_violation() {
        // In this specific story, OpenAccountCmd creates the account.
        // But if we try to Open an already Active account (status violation check),
        // we simulate it by creating an aggregate, opening it, then trying to open it again.
        this.aggregate = new AccountAggregate("acc-status-violation");
        // Open it once
        OpenAccountCmd cmd1 = new OpenAccountCmd("acc-status-violation", "cust-1", "SAVINGS", BigDecimal.ZERO, "00-00-00");
        aggregate.execute(cmd1);
        // Now it is ACTIVE. Trying to OpenAccount again on an Active account violates the state transition.
    }

    @When("the OpenAccountCmd command is executed on active account")
    public void execute_open_cmd_active() {
        try {
             OpenAccountCmd cmd = new OpenAccountCmd(
                    "acc-status-violation",
                    "cust-456",
                    "SAVINGS",
                    BigDecimal.TEN,
                    "10-20-30"
            );
            this.resultingEvents = aggregate.execute(cmd);
        } catch (IllegalStateException e) {
            this.thrownException = e;
        }
    }

    // Reuse Then from above

    // Scenario 4: Immutable Number
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_with_number_violation() {
        // This invariant is usually enforced at the repository/infrastructure level (uniqueness).
        // Or via an ID generation service. In the aggregate, we ensure the ID doesn't change.
        this.aggregate = new AccountAggregate("fixed-id-123");
    }

    @When("the OpenAccountCmd command is executed with mismatched ID")
    public void execute_open_cmd_mismatched_id() {
        try {
            // Attempt to open an account where the command ID implies creating a NEW aggregate ID,
            // but here we are operating on an existing aggregate with a different ID concept.
            // However, standard aggregate pattern accepts commands for itself.
            // If the invariant is "Immutable", we ensure we don't overwrite the ID.
            // Let's assume the test checks that we don't allow creating an account with an existing ID from the repo.
            // Since this is a Unit test on Aggregate, we check ID immutability logic.
            // If we try to 'open' an account that somehow changes the internal ID, that's a fail.
            // Given the simple aggregate structure, let's assume the test simulates attempting to
            // initialize an aggregate that is already initialized (treated as ID conflict).
            OpenAccountCmd cmd = new OpenAccountCmd(
                    "fixed-id-123", // ID matches aggregate, so it passes aggregate ID check
                    "cust-456",
                    "SAVINGS",
                    BigDecimal.TEN,
                    "10-20-30"
            );
             // If we already opened it in this step (mocked), we fail.
             // But we need to simulate the scenario.
             aggregate.execute(cmd); // First open
             aggregate.execute(cmd); // Second open (effectively trying to re-use/re-initialize the immutable identity)
        } catch (IllegalStateException e) {
            this.thrownException = e;
        }
    }
}