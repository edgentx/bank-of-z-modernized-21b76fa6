package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.shared.DomainEvent;
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
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        aggregate = new AccountAggregate("ACC-123");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        command = new CloseAccountCmd("ACC-123");
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance_min_balance() {
        aggregate = new AccountAggregate("ACC-999");
        aggregate.setBalance(new BigDecimal("100.00")); // Non-zero balance violates close invariant
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        command = new CloseAccountCmd("ACC-999");
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("ACC-888");
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.AccountStatus.SUSPENDED); // Not active
        command = new CloseAccountCmd("ACC-888");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_account_number_immutability() {
        aggregate = new AccountAggregate("ACC-777"); // Aggregate ID
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
        command = new CloseAccountCmd("ACC-DIFFERENT"); // Command ID differs from Aggregate ID
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(command);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        
        AccountClosedEvent event = (AccountClosedEvent) resultEvents.get(0);
        assertEquals("account.closed", event.type());
        assertEquals(aggregate.id(), event.aggregateId());
        assertEquals(AccountAggregate.AccountStatus.CLOSED, aggregate.getStatus());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Depending on specific invariant, we check exception type
        assertTrue(caughtException instanceof IllegalStateException || 
                   caughtException instanceof IllegalArgumentException);
    }
}
