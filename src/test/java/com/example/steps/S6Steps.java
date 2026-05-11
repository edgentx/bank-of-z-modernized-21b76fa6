package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.account.repository.InMemoryAccountRepository;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private final InMemoryAccountRepository repository = new InMemoryAccountRepository();
    private AccountAggregate account;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        account = new AccountAggregate("ACC-123", "ACC-123", AccountAggregate.Status.ACTIVE, 1000.00);
        account.applyHistory(); // Simulate loading from history
        repository.save(account);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Implicitly handled by the setup, uses "ACC-123"
    }

    @Given("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        // Implicitly handled in the When step
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd("ACC-123", AccountAggregate.Status.FROZEN);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("ACC-123", event.aggregateId());
        assertEquals("FROZEN", event.newStatus());
    }

    // Invariant Scenarios

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_min_balance() {
        // Setup account with 0 balance
        account = new AccountAggregate("ACC-LOW", "ACC-LOW", AccountAggregate.Status.ACTIVE, 0.0);
        account.applyHistory();
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        // Setup account that is FROZEN but tries to act
        account = new AccountAggregate("ACC-FRZ", "ACC-FRZ", AccountAggregate.Status.FROZEN, 100.0);
        account.applyHistory();
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        // This scenario tests the command trying to change the AccountNumber itself via Status Update logic
        // if business rules were strictly coupled. Here we simulate the aggregate state rejecting modification
        // or a validation error.
        account = new AccountAggregate("ACC-IMMUTABLE", "ACC-IMMUTABLE", AccountAggregate.Status.ACTIVE, 100.0);
        account.applyHistory();
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Ideally we would catch a specific DomainException, but RuntimeException is the baseline
        assertTrue(capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException);
    }
}