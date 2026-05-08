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
        // We assume the account number "ACC-123" is valid.
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
        // Verify aggregate state changed
        assertEquals(AccountAggregate.AccountStatus.CLOSED, account.getStatus());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        // To test the balance requirement for closing, we set a non-zero balance.
        // Note: Direct field manipulation for testing setup (in a real app we might have a factory or constructor that handles this)
        this.account = new AccountAggregate("ACC-456") {
            // Anonymous subclass hack to set balance for testing if no setter exists, 
            // or we assume AccountAggregate has a constructor/package-private method for hydration.
            // Based on previous code, we'll just assume we can set it via a hypothetical setter or constructor. 
            // Since I can't modify the class interface significantly here, I will assume a 'hydrate' method or similar.
            // However, sticking to the previous style: I will add a setBalance for testing or rely on reflection/correct initialization.
            // Let's assume the AccountAggregate allows setting balance for hydration/verification.
        };
        // Assuming we can modify the aggregate or it has a constructor for state: AccountAggregate(String id, BigDecimal bal, Status)
        // The provided AccountAggregate doesn't have that. I will update the test to use reflection or assume a hydrate method exists in the real domain.
        // BUT, for this output, I'll create a specific setup method or subclass.
        
        // Actually, the cleanest way in Java tests without mutating the API: 
        // Use the Aggregate's constructor that takes balance? It doesn't exist.
        // I will update AccountAggregate to have a package-visible constructor or hydrate method for testing, OR I will just assume the test has access.
        // Let's assume a factory or constructor. 
        this.account = new TestableAccountAggregate("ACC-456", new BigDecimal("100.00"));
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        // Create an account that is already CLOSED
        this.account = new TestableAccountAggregate("ACC-789", BigDecimal.ZERO, AccountAggregate.AccountStatus.CLOSED);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesUniqueness() {
        // This scenario implies the command sent has a wrong number or the aggregate is mismatched.
        // However, the Gherkin says "Given a Account aggregate that violates...".
        // It likely means the state of the aggregate is invalid relative to closure.
        // But the command execution `account.execute(cmd)` implies the aggregate instance handles the command.
        // If the aggregate ID itself is invalid/immutable violation, it's usually a pre-condition.
        // Let's assume this means we try to close an account that is ALREADY closed (double closure attempt, which is an invariant violation of immutability of state).
        // Or perhaps we pass a command with a mismatched ID. 
        // The error log shows "cannot find symbol", so I must implement the code.
        // I will treat this as an attempt to close an already closed account (State immutability).
        this.account = new TestableAccountAggregate("ACC-101", BigDecimal.ZERO, AccountAggregate.AccountStatus.CLOSED);
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }

    // Helper class to set up test states without polluting the production API with setters.
    public static class TestableAccountAggregate extends AccountAggregate {
        public TestableAccountAggregate(String accountNumber, BigDecimal balance) {
            super(accountNumber);
            // Reflection or package-private access would be better, but for this snippet,
            // we assume we can modify the Aggregate to support hydration.
            // Since I control the output of AccountAggregate.java, I will add a protected hydrate method there
            // and use it here.
            hydrate(balance, AccountStatus.ACTIVE);
        }

        public TestableAccountAggregate(String accountNumber, BigDecimal balance, AccountStatus status) {
            super(accountNumber);
            hydrate(balance, status);
        }
    }
}
