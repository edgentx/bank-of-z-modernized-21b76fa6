package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.UpdateAccountStatusCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

public class S6Steps {

    private AccountAggregate account;
    private String providedAccountNumber;
    private String providedNewStatus;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void aValidAccountAggregate() {
        account = new AccountAggregate("ACC-001");
        account.setBalance(new BigDecimal("500.00"));
        account.setStatus("Active");
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        providedAccountNumber = "ACC-001";
    }

    @And("a valid newStatus is provided")
    public void aValidNewStatusIsProvided() {
        providedNewStatus = "Frozen";
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void theUpdateAccountStatusCmdCommandIsExecuted() {
        try {
            UpdateAccountStatusCmd cmd = new UpdateAccountStatusCmd(providedAccountNumber, providedNewStatus);
            resultEvents = account.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void aAccountStatusUpdatedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertEquals("account.status.updated", resultEvents.get(0).type());
    }

    // --- Error Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void aAccountAggregateThatViolatesMinimumBalance() {
        account = new AccountAggregate("ACC-LOW");
        account.setBalance(new BigDecimal("50.00")); // Below 100.00
        account.setStatus("Active");
        providedAccountNumber = "ACC-LOW";
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void aAccountAggregateThatViolatesActiveStatusRequirement() {
        account = new AccountAggregate("ACC-FROZEN");
        account.setBalance(new BigDecimal("500.00"));
        account.setStatus("Frozen"); // Not active
        providedAccountNumber = "ACC-FROZEN";
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void aAccountAggregateThatViolatesImmutableNumber() {
        account = new AccountAggregate("ACC-IMMU");
        account.setBalance(new BigDecimal("500.00"));
        providedAccountNumber = "ACC-DIFFERENT"; // Providing a different number than the aggregate ID
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
