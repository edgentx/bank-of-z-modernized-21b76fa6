package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S5Steps {

    private AccountAggregate aggregate;
    private OpenAccountCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        this.aggregate = new AccountAggregate("acc-123");
        this.caughtException = null;
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // Handled in command construction below or context management
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
    }

    // We construct the command effectively with valid defaults here unless modified by violation steps
    private void buildCommand(String customerId, String accountType, BigDecimal initialDeposit, String sortCode, String accountNumber) {
        this.cmd = new OpenAccountCmd(
            "acc-123",
            customerId,
            accountType,
            initialDeposit,
            sortCode,
            accountNumber
        );
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        try {
            // Defaults for happy path
            if (cmd == null) {
                buildCommand("cust-99", "CHECKING", new BigDecimal("500.00"), "10-20-30", null);
            }
            this.resultEvents = aggregate.execute(cmd);
        } catch (IllegalArgumentException | IllegalStateException | UnknownCommandException e) {
            this.caughtException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertEquals("account.opened", resultEvents.get(0).type());
        Assertions.assertEquals("acc-123", resultEvents.get(0).aggregateId());
        Assertions.assertNull(caughtException, "Expected success but got error: " + caughtException);
    }

    // --- Violation Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        this.aggregate = new AccountAggregate("acc-violation-balance");
        // SAVINGS requires 100.00. We provide 10.00.
        this.buildCommand("cust-99", "SAVINGS", new BigDecimal("10.00"), "10-20-30", null);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatus() {
        // While this AC specifically mentions processing withdrawals/transfers, for the context of 'OpenAccountCmd',
        // we interpret this as the aggregate needing to reach ACTIVE status. 
        // If we attempt to OPEN an account that is somehow already locked or processing (simulated), it should fail.
        // However, 'OpenAccountCmd' transitions PENDING -> ACTIVE. 
        // A more direct interpretation for OPEN is that you cannot open an account that is already ACTIVE/CLOSED.
        this.aggregate = new AccountAggregate("acc-violation-status");
        
        // Force state to ACTIVE manually (simulating an already opened account)
        // Since we don't have a direct setter, we rely on the constructor defaults to PENDING_OPEN.
        // To make 'execute' fail with status logic, we'd need the aggregate to be opened already.
        // Let's assume this scenario catches the invariant: "Cannot open an already opened account"
        
        // Open it once successfully
        OpenAccountCmd firstCmd = new OpenAccountCmd("acc-violation-status", "cust-1", "CHECKING", BigDecimal.ZERO, "00-00-00", null);
        aggregate.execute(firstCmd);
        
        // Now prepare the second command (the one we are about to execute in the 'When' clause)
        this.buildCommand("cust-1", "CHECKING", BigDecimal.ZERO, "00-00-00", null);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesUniqueness() {
        this.aggregate = new AccountAggregate("acc-violation-unique");
        // Provide a specific account number to open the account
        OpenAccountCmd firstCmd = new OpenAccountCmd("acc-violation-unique", "cust-1", "CHECKING", BigDecimal.ZERO, "00-00-00", "FORCE-123");
        aggregate.execute(firstCmd);
        
        // Prepare command that attempts to reuse or force another number
        this.buildCommand("cust-1", "CHECKING", BigDecimal.ZERO, "00-00-00", "FORCE-456");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(caughtException, "Expected domain error (exception) but command succeeded.");
        Assertions.assertTrue(
            caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException,
            "Expected IllegalArgumentException or IllegalStateException, got: " + caughtException.getClass()
        );
    }
}
