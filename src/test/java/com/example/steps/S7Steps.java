package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate aggregate;
    private Throwable thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        // Setup a fresh aggregate in a valid state (Active, Zero Balance)
        // We simulate the state resulting from an AccountOpenedEvent
        aggregate = new AccountAggregate("ACC-123");
        // Manually setting state to simulate an 'Active' account ready for closing
        // In a real scenario, this would be done by loading from events or via a factory method
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
        aggregate.setAccountNumber("ACC-123");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // The account number is implicitly set in the aggregate setup
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_min_balance() {
        aggregate = new AccountAggregate("ACC-456");
        aggregate.setBalance(new BigDecimal("100.00")); // Positive balance means not closed correctly or just > 0
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
        aggregate.setAccountNumber("ACC-456");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        aggregate = new AccountAggregate("ACC-789");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.SUSPENDED); // Not active
        aggregate.setAccountNumber("ACC-789");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_number_uniqueness() {
        aggregate = new AccountAggregate("ACC-999");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
        aggregate.setAccountNumber("DIFFERENT-NUMBER"); // Mismatch
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            Command cmd = new CloseAccountCmd(aggregate.id(), aggregate.getAccountNumber());
            resultEvents = aggregate.execute(cmd);
        } catch (Throwable t) {
            thrownException = t;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        AccountClosedEvent event = (AccountClosedEvent) resultEvents.get(0);
        assertEquals("ACC-123", event.aggregateId());
        assertEquals("account.closed", event.type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
