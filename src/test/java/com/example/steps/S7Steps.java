package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
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
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        aggregate = new AccountAggregate("ACC-100");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // The account number is implicitly "ACC-100" from the previous step
        // We assume the command is constructed with this valid ID
        if (aggregate == null) {
            throw new IllegalStateException("Aggregate must be initialized first");
        }
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance_requirement() {
        aggregate = new AccountAggregate("ACC-101");
        // S-7 Description: "Closes the account permanently, provided the balance is zero."
        // Non-zero balance violates this requirement.
        aggregate.setBalance(new BigDecimal("100.50"));
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("ACC-102");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.FROZEN); // Not ACTIVE
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        // This context usually implies a repository check for uniqueness.
        // In the context of executing a Command on an Aggregate instance:
        // We simulate a mismatch where the command targets a different ID than the aggregate.
        aggregate = new AccountAggregate("ACC-103");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
        
        // We will construct the command in the 'When' step to target "ACC-999" to simulate the violation 
        // (Command ID != Aggregate ID).
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        String targetId;
        
        // Special case for the Uniqueness violation scenario: Command targets wrong ID
        if (aggregate.id().equals("ACC-103")) {
            targetId = "ACC-999"; 
        } else {
            targetId = aggregate.id();
        }

        command = new CloseAccountCmd(targetId);
        
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNull(capturedException, "Should not have thrown an exception");
        assertNotNull(resultEvents, "Events list should not be null");
        assertEquals(1, resultEvents.size(), "Should produce one event");
        
        DomainEvent event = resultEvents.get(0);
        assertTrue(event instanceof AccountClosedEvent, "Event should be AccountClosedEvent");
        assertEquals("account.closed", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException, "Expected an exception to be thrown");
        // Validate it's a domain logic violation (IllegalStateException or IllegalArgumentException)
        assertTrue(
            capturedException instanceof IllegalStateException || capturedException instanceof IllegalArgumentException,
            "Expected domain error (IllegalStateException/IllegalArgumentException), but got: " + capturedException.getClass().getName()
        );
    }
}
