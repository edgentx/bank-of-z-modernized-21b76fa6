package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class S7Steps {

    private AccountAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    // Factory for clean aggregates per scenario
    static class TestAggregateFactory {
        public static AccountAggregate createAccount(String accountNumber) {
            return new AccountAggregate(accountNumber);
        }
    }

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        String accountNumber = "ACC-123";
        aggregate = TestAggregateFactory.createAccount(accountNumber);
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @And("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // No-op, using the default from the aggregate creation
    }

    // --- Negative Invariant Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance() {
        String accountNumber = "ACC-999";
        aggregate = TestAggregateFactory.createAccount(accountNumber);
        aggregate.setBalance(new BigDecimal("100.00")); // Non-zero balance prevents closing
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        String accountNumber = "ACC-888";
        aggregate = TestAggregateFactory.createAccount(accountNumber);
        aggregate.setStatus(AccountAggregate.AccountStatus.FROZEN); // Not active
        aggregate.setBalance(BigDecimal.ZERO);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        String accountNumber = "ACC-777";
        aggregate = TestAggregateFactory.createAccount(accountNumber);
        aggregate.markImmutable(); // Simulates immutability constraint enforcement
        aggregate.setBalance(BigDecimal.ZERO);
        aggregate.setStatus(AccountAggregate.AccountStatus.ACTIVE);
    }

    @When("the CloseAccountCmd command is executed")
    public void the_CloseAccountCmd_command_is_executed() {
        try {
            CloseAccountCmd cmd = new CloseAccountCmd(aggregate.id());
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void a_account_closed_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        assertEquals("account.closed", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(caughtException);
        // Check if it's a domain exception (IllegalStateException or IllegalArgumentException)
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }
}
