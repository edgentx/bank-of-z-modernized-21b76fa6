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

    private AccountAggregate account;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        this.account = new AccountAggregate("ACC-123");
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Implicitly handled by the aggregate creation in the previous step.
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        try {
            CloseAccountCmd cmd = new CloseAccountCmd("ACC-123");
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.closed", resultEvents.get(0).type());
        assertEquals("ACC-123", resultEvents.get(0).aggregateId());
        assertEquals(AccountAggregate.AccountStatus.CLOSED, account.getStatus());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        // Using a static helper to create a test-specific aggregate with state
        this.account = TestAggregateFactory.createWithBalance("ACC-456", new BigDecimal("100.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        // Account is already closed
        this.account = TestAggregateFactory.createClosed("ACC-789");
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesUniqueness() {
        // Scenario implies closing an already closed account (State immutability)
        this.account = TestAggregateFactory.createClosed("ACC-101");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException);
    }

    // Inner factory for test setup without polluting production code
    private static class TestAggregateFactory extends AccountAggregate {
        public TestAggregateFactory(String id) { super(id); }
        
        public static AccountAggregate createWithBalance(String id, BigDecimal balance) {
            AccountAggregate agg = new AccountAggregate(id);
            agg.hydrate(balance, AccountAggregate.AccountStatus.ACTIVE);
            return agg;
        }

        public static AccountAggregate createClosed(String id) {
            AccountAggregate agg = new AccountAggregate(id);
            agg.hydrate(BigDecimal.ZERO, AccountAggregate.AccountStatus.CLOSED);
            return agg;
        }
    }
}
