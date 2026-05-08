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

    private AccountAggregate account;
    private String accountNumber;
    private String newStatus;
    private List<DomainEvent> resultingEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        this.account = new AccountAggregate("acct-123");
        this.account.setAccountNumber("123456789");
        this.account.setStatus("Active");
        this.account.setBalance(BigDecimal.valueOf(500.00));
        this.account.setMinimumRequiredBalance(BigDecimal.valueOf(100.00));
        this.account.setAccountType("Standard");
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        this.accountNumber = "123456789";
    }

    @Given("a valid newStatus is provided")
    public void a_valid_newStatus_is_provided() {
        this.newStatus = "Frozen";
    }

    @When("the UpdateAccountStatusCmd command is executed")
    public void the_UpdateAccountStatusCmd_command_is_executed() {
        try {
            var cmd = new UpdateAccountStatusCmd(this.accountNumber, this.newStatus);
            this.resultingEvents = this.account.execute(cmd);
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    @Then("a account.status.updated event is emitted")
    public void a_account_status_updated_event_is_emitted() {
        assertNotNull(resultingEvents);
        assertEquals(1, resultingEvents.size());
        assertEquals("account.status.updated", resultingEvents.get(0).type());
    }

    // --- Scenarios for Rejections ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_balance_constraint() {
        this.account = new AccountAggregate("acct-low-balance");
        this.account.setAccountNumber("999999");
        this.account.setStatus("Active");
        this.account.setBalance(BigDecimal.valueOf(50.00)); // Below min
        this.account.setMinimumRequiredBalance(BigDecimal.valueOf(100.00));
        this.account.setAccountType("Standard");
        
        this.accountNumber = "999999";
        this.newStatus = "Closed";
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_active_status_requirement() {
        this.account = new AccountAggregate("acct-frozen");
        this.account.setAccountNumber("888888");
        this.account.setStatus("Frozen"); // Not active
        this.account.setBalance(BigDecimal.valueOf(500.00));
        this.account.setMinimumRequiredBalance(BigDecimal.ZERO);
        this.account.setAccountType("Standard");

        this.accountNumber = "888888";
        this.newStatus = "Closed";
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_immutability() {
        this.account = new AccountAggregate("acct-immutable");
        this.account.setAccountNumber("111111"); // Existing number
        this.account.setStatus("Active");
        this.account.setBalance(BigDecimal.valueOf(1000.00));
        this.account.setMinimumRequiredBalance(BigDecimal.ZERO);
        this.account.setAccountType("Standard");

        // Attempting to change the number in the command
        this.accountNumber = "222222"; // Different number
        this.newStatus = "Frozen";
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        assertTrue(thrownException instanceof IllegalStateException || thrownException instanceof IllegalArgumentException);
    }
}
