package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        aggregate = new AccountAggregate("ACC-123", BigDecimal.ZERO);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // No-op, implied by construction
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        aggregate = new AccountAggregate("ACC-999", new BigDecimal("100.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        aggregate = new AccountAggregate("ACC-888", BigDecimal.ZERO);
        // Simulate closed state by applying a command or internal mutation (in a real repo)
        // For this unit test, we mutate internal state directly to simulate the condition
        aggregate.execute(new CloseAccountCmd("ACC-888", BigDecimal.ZERO));
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_number() {
        aggregate = new AccountAggregate("ACC-777", BigDecimal.ZERO);
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            String cmdNumber = "ACC-123";
            // For the violation test regarding immutability, we pass a different number
            if (aggregate.id().equals("ACC-777")) {
                cmdNumber = "ACC-FAKE";
            }
            resultEvents = aggregate.execute(new CloseAccountCmd(cmdNumber, aggregate.getBalance()));
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.closed", resultEvents.get(0).type());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
