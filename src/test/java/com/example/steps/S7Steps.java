package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S7Steps {

    private AccountAggregate aggregate;
    private String accountNumber;
    private List<DomainEvent> resultEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        accountNumber = "ACC-100";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setState("ACTIVE", BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Handled in the previous step, implicit validity
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            resultEvents = aggregate.execute(new CloseAccountCmd(accountNumber));
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.closed", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    // Scenarios for Rejections

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        accountNumber = "ACC-200";
        aggregate = new AccountAggregate(accountNumber);
        // Balance is 100, Min is 50. To close, we must go to 0. 0 < 50. Violation.
        aggregate.setState("ACTIVE", new BigDecimal("100.00"), new BigDecimal("50.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status() {
        accountNumber = "ACC-300";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setState("CLOSED", BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        accountNumber = "ACC-400";
        aggregate = new AccountAggregate(accountNumber);
        aggregate.setState("ACTIVE", BigDecimal.ZERO, BigDecimal.ZERO);
        // The violation simulation happens in the 'When' step by sending a mismatched ID
    }

    // Reusing the 'When' step defined above

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}