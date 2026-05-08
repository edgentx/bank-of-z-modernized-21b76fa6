package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
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
    private Exception capturedException;
    private List<DomainEvent> resultEvents;

    // Standard Valid Data
    private String validCustomerId = "CUST-123";
    private String validAccountType = "SAVINGS";
    private BigDecimal validInitialDeposit = new BigDecimal("150.00");
    private String validSortCode = "10-20-30";
    private String accountId = "ACC-001";

    // Scenario 1: Success
    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        aggregate = new AccountAggregate(accountId);
    }

    @And("a valid customerId is provided")
    public void aValidCustomerIdIsProvided() {
        // No-op, using default
    }

    @And("a valid accountType is provided")
    public void aValidAccountTypeIsProvided() {
        // No-op, using default
    }

    @And("a valid initialDeposit is provided")
    public void aValidInitialDepositIsProvided() {
        // No-op, using default
    }

    @And("a valid sortCode is provided")
    public void aValidSortCodeIsProvided() {
        // No-op, using default
    }

    @When("the OpenAccountCmd command is executed")
    public void theOpenAccountCmdCommandIsExecuted() {
        try {
            OpenAccountCmd cmd = new OpenAccountCmd(accountId, validCustomerId, validAccountType, validInitialDeposit, validSortCode);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void aAccountOpenedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.opened", resultEvents.get(0).type());
        assertNull(capturedException);
    }

    // Scenario 2: Balance violation
    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesBalance() {
        aggregate = new AccountAggregate(accountId);
        validInitialDeposit = new BigDecimal("50.00"); // Below 100 for SAVINGS
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("below minimum"));
    }

    // Scenario 3: Status violation
    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesStatus() {
        // Context: Opening an account requires the aggregate to be in a 'NONE' state. 
        // If it is already ACTIVE, we cannot 'Open' it again.
        aggregate = new AccountAggregate(accountId);
        // Manually force the aggregate into a state where it violates the 'Open' pre-condition
        // or implies a double-open attempt which is illegal.
        // We will simulate a pre-existing account by executing a valid command first 
        // (bypassing the current scenario setup for a moment to establish state)
        // Or simply assume the aggregate is constructed in an invalid state for 'Opening'.
        // Let's assume the aggregate is pre-activated.
        // Since we don't have a load method here, we'll rely on the logic that opening an already open account fails.
        // We can execute the command twice, the second time will fail.
        
        // However, the 'Given' usually sets up the state BEFORE the When.
        // Let's assume this means we are trying to open an account that is somehow already ACTIVE.
        // Since we cannot directly set status without a 'loadFromHistory' method in this snippet, 
        // we will simulate a 'Double Open' attempt in the execution flow or rely on the fact 
        // that if we execute the command twice, the second one fails.
        
        // To strictly follow Gherkin, let's try to open it once here to make it ACTIVE.
        try {
             aggregate.execute(new OpenAccountCmd(accountId, validCustomerId, validAccountType, validInitialDeposit, validSortCode));
        } catch (Exception e) {
            // ignore
        }
    }

    @Then("the command is rejected with a domain error.")
    public void theCommandIsRejectedWithADomainErrorStatus() {
         // This captures the rejection of the second open attempt triggered by @When
        assertNotNull(capturedException);
        assertTrue(capturedException.getMessage().contains("not in a state that allows opening") 
            || capturedException.getMessage().contains("already generated"));
    }

    // Scenario 4: Immutable Account Number
    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutable() {
        aggregate = new AccountAggregate(accountId);
        // To violate 'Immutable' implies generating it twice. 
        // Similar to above, we open it once.
        try {
             aggregate.execute(new OpenAccountCmd(accountId, validCustomerId, validAccountType, validInitialDeposit, validSortCode));
        } catch (Exception e) {
            // ignore
        }
    }
}