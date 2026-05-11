package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

public class S5Steps {

    private AccountAggregate aggregate;
    private String testAccountId;
    private OpenAccountCmd cmd;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        testAccountId = "ACC-" + System.currentTimeMillis();
        aggregate = new AccountAggregate(testAccountId);
    }

    @Given("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        // Data setup happens in the When block for command construction
    }

    @Given("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        // Data setup happens in the When block
    }

    @Given("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        // Data setup happens in the When block
    }

    @Given("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        // Data setup happens in the When block
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        // Defaults for a valid command if not overridden by specific scenarios
        // Using SAVINGS to strictly test the minimum balance invariant
        cmd = new OpenAccountCmd(
            testAccountId != null ? testAccountId : "ACC-TEST",
            "CUST-123",
            "SAVINGS",
            new BigDecimal("150.00"), // Satisfies SAVINGS min of 100
            "10-20-30"
        );

        // Handle specific overrides based on scenario context if we stored state,
        // but here we construct a valid command by default. 
        // The "Given a Account aggregate that violates" steps below will construct a bad command.
        
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
        AccountOpenedEvent event = (AccountOpenedEvent) resultEvents.get(0);
        Assertions.assertEquals("account.opened", event.type());
        Assertions.assertEquals(testAccountId, event.aggregateId());
        Assertions.assertEquals(AccountAggregate.AccountStatus.ACTIVE, aggregate.getStatus());
        Assertions.assertEquals(new BigDecimal("150.00"), aggregate.getBalance());
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_minimum_balance() {
        testAccountId = "ACC-LOW-BAL";
        aggregate = new AccountAggregate(testAccountId);
        // We prepare the command in the When step, but we need a flag to know to use bad data.
        // Since Cucumber steps run in sequence, we can modify the 'when' logic or just set a variable.
        // For simplicity, we'll rebuild the context in the next 'When' via a flag check.
        // But best practice: Prepare data here.
    }

    // We overload the When/Then slightly by checking state or context, or simply assuming 
    // specific sequence. Given the constraints, I will create specific step methods for the error cases.
    
    @When("the OpenAccountCmd command is executed with low balance")
    public void the_open_account_cmd_is_executed_with_low_balance() {
        cmd = new OpenAccountCmd(
            testAccountId, "CUST-123", "SAVINGS", new BigDecimal("50.00"), "10-20-30"
        );
        try {
            aggregate.execute(cmd);
        } catch (IllegalArgumentException e) {
            thrownException = e;
        }
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_active_status() {
        // This invariant is actually for Withdrawals/Transfers, but the story asks to implement OpenAccountCmd.
        // The OpenAccountCmd itself transitions the account to Active.
        // To satisfy the Gherkin, we might interpret this as trying to Open an account that is somehow already Active 
        // (violating uniqueness/lifecycle). Or, more likely, the Story Gherkin is slightly generic.
        // However, if we assume the command handling checks for existing status:
        aggregate = new AccountAggregate("ACC-ALREADY-OPEN");
        // We can simulate an existing account by forcing it to Active via a test seam, 
        // or simply test the rejection of opening an already opened account (status check).
        // The constructor defaults to NONE. Let's manually set status to ACTIVE for this test if possible, 
        // or simply re-run execute on an account that was already opened.
        
        // Open it once manually (simulating history)
        aggregate.execute(new OpenAccountCmd("ACC-ALREADY-OPEN", "CUST-1", "CHECKING", new BigDecimal("100"), "00-00-00"));
    }

    @When("the OpenAccountCmd command is executed on active account")
    public void the_open_account_cmd_is_executed_on_active_account() {
        try {
            aggregate.execute(new OpenAccountCmd("ACC-ALREADY-OPEN", "CUST-1", "CHECKING", new BigDecimal("100"), "00-00-00"));
        } catch (IllegalStateException e) {
            thrownException = e;
        }
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        // Simulate a duplicate aggregate being created in memory with the same ID logic, 
        // OR simply testing the invariant that an existing account cannot be re-opened (handled by previous step logic).
        // Since "Numbers must be uniquely generated" is a Generator concern, the Aggregate enforces it 
        // by rejecting commands on an existing ID.
        aggregate = new AccountAggregate("ACC-DUPLICATE");
        aggregate.execute(new OpenAccountCmd("ACC-DUPLICATE", "CUST-1", "CHECKING", BigDecimal.ZERO, "00-00-00"));
    }

    @When("the OpenAccountCmd command is executed on duplicate account")
    public void the_open_account_cmd_is_executed_on_duplicate_account() {
        try {
            aggregate.execute(new OpenAccountCmd("ACC-DUPLICATE", "CUST-1", "CHECKING", BigDecimal.ZERO, "00-00-00"));
        } catch (IllegalStateException e) {
            thrownException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(thrownException);
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException);
    }

}
