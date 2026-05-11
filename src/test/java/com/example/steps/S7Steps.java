package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.account.model.AccountClosedEvent;
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
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        account = new AccountAggregate("ACC-123", BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.ACTIVE);
        account.setBalance(BigDecimal.ZERO);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Handled by the aggregate initialization
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        try {
            var cmd = new CloseAccountCmd("ACC-123");
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountClosedEvent);
        assertEquals("account.closed", resultEvents.get(0).type());
        assertEquals(AccountAggregate.Status.CLOSED, account.getStatus());
    }

    // --- Rejection Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesAccountBalanceCannotDropBelowTheMinimumRequiredBalance() {
        account = new AccountAggregate("ACC-456", BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.ACTIVE);
        account.setBalance(new BigDecimal("100.00")); // Positive balance prevents closing
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesAnAccountMustBeInAnActiveStatus() {
        account = new AccountAggregate("ACC-789", BigDecimal.ZERO);
        account.setStatus(AccountAggregate.Status.FROZEN); // Not active
        account.setBalance(BigDecimal.ZERO);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesAccountNumbersMustBeUniquelyGeneratedAndImmutable() {
        account = new AccountAggregate("ACC-ORIG", BigDecimal.ZERO);
        // Simulate a command arriving for a different account number than the aggregate ID
        // This will be handled in the When step by sending a mismatched command
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException || caughtException instanceof IllegalArgumentException);
    }

    // Specific When for the immutable account number check since it requires a different cmd setup
    @When("the CloseAccountCmd command is executed with mismatched ID")
    public void theCloseAccountCmdCommandIsExecutedWithMismatchedId() {
        try {
            // Aggregate ID is ACC-ORIG, but command is for ACC-OTHER
            var cmd = new CloseAccountCmd("ACC-OTHER");
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }
}
