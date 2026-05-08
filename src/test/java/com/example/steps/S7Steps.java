package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.AccountType;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S7Steps {

    private AccountAggregate account;
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        account = new AccountAggregate("ACC-123", AccountType.SAVINGS);
        account.setBalance(BigDecimal.ZERO);
        account.setActive(true);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_account_number_is_provided() {
        // Covered in setup
    }

    @When("the CloseAccountCmd command is executed")
    public void the_close_account_cmd_command_is_executed() {
        try {
            CloseAccountCmd cmd = new CloseAccountCmd("ACC-123");
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        Assertions.assertNull(capturedException, "Should not have thrown an exception");
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        AccountClosedEvent event = (AccountClosedEvent) resultEvents.get(0);
        Assertions.assertEquals("ACC-123", event.aggregateId());
        Assertions.assertEquals("account.closed", event.type());
    }

    // --- Failure Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        account = new AccountAggregate("ACC-999", AccountType.SAVINGS);
        account.setBalance(new BigDecimal("50.00")); // Min for Savings is 0, assuming 0 is requirement for closing. 
        // Or if min balance for account type is 100, we set 50. Let's assume S-7 implies balance must be 0 to close.
        account.setActive(true);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        account = new AccountAggregate("ACC-888", AccountType.CHECKING);
        account.setBalance(BigDecimal.ZERO);
        account.setActive(false); // Inactive
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_id() {
        account = new AccountAggregate("ACC-777", AccountType.SAVINGS);
        account.setBalance(BigDecimal.ZERO);
        account.setActive(true);
        // Simulation of command targeting wrong ID happens in the When/Then logic or specific command validation
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_with_non_zero_balance() {
        // Setup specifically for the balance check failure scenario
        account = new AccountAggregate("ACC-FAIL-BAL", AccountType.SAVINGS);
        account.setBalance(new BigDecimal("10.00"));
        account.setActive(true);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedException, "Expected an exception to be thrown");
        // Check it's a domain violation (IllegalStateException or IllegalArgumentException)
        Assertions.assertTrue(
            capturedException instanceof IllegalStateException || 
            capturedException instanceof IllegalArgumentException
        );
    }
}
