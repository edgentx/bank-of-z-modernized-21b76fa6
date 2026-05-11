package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;

public class S7Steps {

    private AccountAggregate aggregate;
    private Command command;
    private Throwable thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        aggregate = new AccountAggregate("123456", "CHECKING");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Scenario context assumes command is created in 'When' step
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        command = new CloseAccountCmd("123456");
        try {
            aggregate.execute(command);
        } catch (Throwable t) {
            thrownException = t;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNull(thrownException, "Should not have thrown an exception");
        Assertions.assertFalse(aggregate.uncommittedEvents().isEmpty(), "Should have uncommitted events");
        DomainEvent event = aggregate.uncommittedEvents().get(0);
        Assertions.assertEquals("account.closed", event.type());
        Assertions.assertEquals("123456", event.aggregateId());
    }

    // -------------------------------------------------------

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_with_non_zero_balance() {
        aggregate = new AccountAggregate("BAD_BALANCE", "SAVINGS");
        aggregate.setBalance(new BigDecimal("100.00")); // Not zero
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException, "Expected an exception to be thrown");
        Assertions.assertTrue(thrownException instanceof IllegalStateException, "Expected IllegalStateException");
        Assertions.assertTrue(thrownException.getMessage().contains("balance must be zero"));
    }

    // -------------------------------------------------------

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_is_not_active() {
        aggregate = new AccountAggregate("INACTIVE", "CHECKING");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.CLOSED); // Already closed
    }

    // -------------------------------------------------------

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_with_mismatched_number() {
        aggregate = new AccountAggregate("ORIG_123", "CHECKING");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.Status.ACTIVE);
    }

    // Override command execution for the immutability test scenario
    @When("the CloseAccountCmd command is executed with mismatched number")
    public void the_close_account_cmd_command_is_executed_with_mismatched_number() {
        // Trying to close ORIG_123 with command for OTHER_123
        command = new CloseAccountCmd("OTHER_123");
        try {
            aggregate.execute(command);
        } catch (Throwable t) {
            thrownException = t;
        }
    }

}
