package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountStatusUpdatedEvent;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S6Steps {

    private AccountAggregate aggregate;
    private String accountNumber;
    private String newStatus;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        accountNumber = "ACC-001";
        aggregate = new AccountAggregate(accountNumber);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // already set in a_valid_account_aggregate
    }

    @Given("a valid newStatus is provided")
    public void a_valid_new_status_is_provided() {
        newStatus = "Frozen";
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_update_account_status_cmd_command_is_executed() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(accountNumber, newStatus);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountStatusUpdatedEvent);
        AccountStatusUpdatedEvent event = (AccountStatusUpdatedEvent) resultEvents.get(0);
        assertEquals("account.status.updated", event.type());
        assertEquals(accountNumber, event.aggregateId());
        assertNotNull(event.occurredAt());
    }

    // --- Scenarios for Rejections ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        accountNumber = "ACC-LOW-BAL";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setAccountType("Student"); // Requires min 100.00
        aggregate.setBalance(new BigDecimal("50.00")); // Below min
        aggregate.setStatus("Active");
        newStatus = "Frozen";
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        accountNumber = "ACC-NOT-ACTIVE";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setStatus("Frozen");
        newStatus = "Closed";
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_immutability() {
        accountNumber = "ACC-IMMUTABLE";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setImmutable(true);
        newStatus = "Closed";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

}
