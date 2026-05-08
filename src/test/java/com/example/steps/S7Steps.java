package com.example.steps;

import com.example.domain.account.model.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S7Steps {

    private AccountAggregate aggregate;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        aggregate = new AccountAggregate("acc-123", "ACC-123");
        // Manually set state to valid open state
        // In a real scenario, this would be loaded from events
        // For testing, we rely on the constructor handling the initial state creation via hypothetical OpenAccountCmd
        // but since S-7 is only CloseAccountCmd, we assume the aggregate can be instantiated in an OPEN state.
        // We will use reflection or package-private setters if available, or simply rely on the Aggregate
        // being constructed with a balance of zero.
        // The default constructor sets balance to 0 and status to ACTIVE.
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // The account number is usually internal to the aggregate, passed via command.
        // This step is essentially a no-op given the context of the previous step,
        // but it ensures we have the context ready.
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance() {
        aggregate = new AccountAggregate("acc-999", "ACC-999");
        // Force balance to be non-zero to simulate violation (invariant check)
        aggregate.setBalance(BigDecimal.TEN); 
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        aggregate = new AccountAggregate("acc-888", "ACC-888");
        // Force status to CLOSED to simulate violation
        aggregate.setStatus(AccountAggregate.Status.CLOSED);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        aggregate = new AccountAggregate("acc-777", "ACC-777");
        // Simulating a scenario where the command tries to change the ID? 
        // This scenario is tricky for a simple command. 
        // We will interpret this as: The command's ID doesn't match the Aggregate ID.
        // The step definition logic for 'When' will handle the specific mismatch.
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            String id = aggregate.id();
            String accountNumber = aggregate.getAccountNumber();
            
            // For the immutability violation scenario
            if (aggregate.id().equals("acc-777")) {
                accountNumber = "MALICIOUS-ID";
            }

            CloseAccountCmd cmd = new CloseAccountCmd(id, accountNumber);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNull(capturedException, "Expected no exception, but got: " + capturedException);
        Assertions.assertNotNull(resultEvents, "Events list should not be null");
        Assertions.assertEquals(1, resultEvents.size(), "Expected exactly one event");
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountClosedEvent, "Expected AccountClosedEvent");
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException,
            "Expected a domain error (IllegalStateException or IllegalArgumentException), but got: " + capturedException.getClass()
        );
    }
}
