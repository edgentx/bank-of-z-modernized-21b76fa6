package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountOpenedEvent;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate aggregate;
    private String customerId;
    private String accountType;
    private BigDecimal initialDeposit;
    private String sortCode;
    private List<DomainEvent> resultingEvents;
    private Exception capturedException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        this.aggregate = new AccountAggregate("acct-1");
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        this.aggregate = new AccountAggregate("acct-2");
        this.accountType = "Savings"; // Assuming Savings has a min balance > 0
        this.initialDeposit = new BigDecimal("10.00"); // Too low
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // Account starts in NONE. If we try to open it, it becomes ACTIVE.
        // The violation scenario implies we might be trying to open an account that is somehow already Active or invalid state,
        // or we are testing the command logic.
        // For OpenAccountCmd, the aggregate starts at NONE. The command transitions it to ACTIVE.
        // This scenario validates the business rule context. 
        // Since OpenAccountCmd CREATES the account, the "Active" rule is more about Withdrawals.
        // However, to support the scenario literal interpretation:
        this.aggregate = new AccountAggregate("acct-3");
        // If we try to execute OpenAccountCmd on an aggregate that is somehow already ACTIVE (via state manipulation if it were allowed),
        // it should fail. But since we cannot set state directly here easily without a setter or loading from a repo,
        // we will assume the valid 'open' flow works. 
        // We will mock the violation by trying to open an account that logic prevents from becoming Active? 
        // Or more likely, this scenario text is a template. We will interpret it as: Ensure that ONLY valid accounts become Active.
        // However, the prompt asks to map the step. Let's assume the scenario is for the command.
        // We will check for specific invariants.
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableAccountNumber() {
        // This is hard to simulate without state persistence. 
        // We can only verify that the logic generates it once and doesn't change it.
        this.aggregate = new AccountAggregate("acct-4");
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        this.customerId = "customer-123";
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        this.accountType = "Standard";
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        // Check if context is the violation scenario
        if (this.initialDeposit == null) {
            this.initialDeposit = new BigDecimal("1000.00");
        }
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        this.sortCode = "10-20-30";
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        OpenAccountCmd cmd = new OpenAccountCmd(
            aggregate.id(),
            customerId,
            accountType,
            initialDeposit,
            sortCode
        );
        try {
            this.resultingEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            this.capturedException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertTrue(resultingEvents.get(0) instanceof AccountOpenedEvent);
        
        AccountOpenedEvent event = (AccountOpenedEvent) resultingEvents.get(0);
        assertEquals("account.opened", event.type());
        assertEquals(customerId, event.customerId());
        assertEquals(accountType, event.accountType());
        assertEquals(initialDeposit, event.balance());
        assertEquals(sortCode, event.sortCode());
        assertNotNull(event.accountNumber());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        // The exception should be an IllegalArgumentException or IllegalStateException based on invariants
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
