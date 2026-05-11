package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
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
    private Exception thrownException;
    private String accountType = "STANDARD";
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        aggregate = new AccountAggregate("acc-123");
        aggregate.hydrate("123456", "ACTIVE", BigDecimal.TEN, "STANDARD");
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        accountNumber = "123456";
    }

    @And("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        newStatus = "FROZEN";
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd("acc-123", accountNumber, newStatus, currentBalance, accountType);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("account.status.updated", resultEvents.get(0).type());
    }

    // Negative Scenarios

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate("acc-min-violation");
        aggregate.hydrate("9999", "ACTIVE", new BigDecimal("50"), "PREMIUM"); // Premium min is 1000
        accountType = "PREMIUM";
        currentBalance = new BigDecimal("50");
        newStatus = "CLOSED";
        accountNumber = "9999";
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        aggregate = new AccountAggregate("acc-inactive");
        aggregate.hydrate("8888", "FROZEN", BigDecimal.TEN, "STANDARD");
        newStatus = "FROZEN"; // Trying to update while already frozen (simulating operation attempt)
        accountNumber = "8888";
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutable_number() {
        aggregate = new AccountAggregate("acc-immutable");
        aggregate.hydrate("1111", "ACTIVE", BigDecimal.TEN, "STANDARD");
        accountNumber = "2222"; // Trying to change number
        newStatus = "ACTIVE";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Checking for Exception types. The aggregate throws IllegalStateException or IllegalArgumentException
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
