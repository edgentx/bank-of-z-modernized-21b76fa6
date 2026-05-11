package com.example.steps;

import com.example.domain.account.model.*;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S5Steps {

    private AccountAggregate aggregate;
    private Exception thrownException;
    private List<DomainEvent> resultEvents;

    // Helper constants
    private static final String VALID_CUSTOMER_ID = "CUST-123";
    private static final AccountAggregate.AccountType VALID_TYPE = AccountAggregate.AccountType.CHECKING;
    private static final String VALID_SORT_CODE = "10-20-30";
    private static final String ACCOUNT_ID = "ACC-AGG-1";

    @Given("a valid Account aggregate")
    public void a_valid_Account_aggregate() {
        // Standard initialization for a new aggregate
        aggregate = new AccountAggregate(ACCOUNT_ID);
    }

    @And("a valid customerId is provided")
    public void a_valid_customerId_is_provided() {
        // Context: Handled in the When step by constructing the command with this constant
    }

    @And("a valid accountType is provided")
    public void a_valid_accountType_is_provided() {
        // Context: Handled in the When step
    }

    @And("a valid initialDeposit is provided")
    public void a valid_initialDeposit_is_provided() {
        // Context: Handled in the When step
    }

    @And("a valid sortCode is provided")
    public void a_valid_sortCode_is_provided() {
        // Context: Handled in the When step
    }

    @When("the OpenAccountCmd command is executed")
    public void the_OpenAccountCmd_command_is_executed() {
        // Default command construction for the positive path scenario
        // Minimum for CHECKING is 100.00
        OpenAccountCmd cmd = new OpenAccountCmd(
            ACCOUNT_ID,
            VALID_CUSTOMER_ID,
            VALID_TYPE,
            new BigDecimal("150.00"),
            VALID_SORT_CODE
        );
        executeCommand(cmd);
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof AccountOpenedEvent);
        
        AccountOpenedEvent event = (AccountOpenedEvent) resultEvents.get(0);
        assertEquals("account.opened", event.type());
        assertEquals(ACCOUNT_ID, event.aggregateId());
    }

    // --- Negative Scenarios ---

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_Account_aggregate_that_violates_minimum_balance() {
        aggregate = new AccountAggregate(ACCOUNT_ID);
        // Context: The violation will be triggered by the command parameters in the 'When' step.
        // We set up a flag or handle specific command logic in the step below.
    }

    // Overriding When for the specific negative context using a custom internal state or context is tricky in pure Cucumber without DataTables.
    // However, Scenario isolation implies we can just use specific variable states.
    // We will use a specific method for the negative scenario 'When' if needed, or combine logic.
    // Here, we assume the context 'Given... violates...' sets up the 'aggregate' and we use a specific 'When' implementation.
    
    // Let's implement specific Whens for the negative flows to ensure command params trigger the errors.
    
    @When("the OpenAccountCmd command is executed with insufficient balance")
    public void the_OpenAccountCmd_command_is_executed_with_insufficient_balance() {
        // Checking min is 100.00, so 50.00 is invalid.
        OpenAccountCmd cmd = new OpenAccountCmd(
            ACCOUNT_ID,
            VALID_CUSTOMER_ID,
            AccountAggregate.AccountType.CHECKING,
            new BigDecimal("50.00"),
            VALID_SORT_CODE
        );
        executeCommand(cmd);
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_Account_aggregate_that_violates_status() {
        // To violate 'must be Active to process', we initialize the aggregate in a state that isn't Active, 
        // or we try to open it into a wrong state. The AC says 'Active status to process...'.
        // For OpenAccount, this usually implies the resulting state must be Active, or if we are 'processing' the command, 
        // the logic inside 'handleOpenAccount' enforces the invariant.
        // We create an aggregate that is CLOSED, which cannot be opened.
        aggregate = new AccountAggregate(ACCOUNT_ID, "123", AccountAggregate.AccountType.SAVINGS, BigDecimal.ZERO, AccountAggregate.Status.CLOSED);
    }

    @When("the OpenAccountCmd command is executed on closed account")
    public void the_OpenAccountCmd_command_is_executed_on_closed_account() {
         OpenAccountCmd cmd = new OpenAccountCmd(
            ACCOUNT_ID,
            VALID_CUSTOMER_ID,
            AccountAggregate.AccountType.SAVINGS,
            new BigDecimal("500.00"),
            VALID_SORT_CODE
        );
        executeCommand(cmd);
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_Account_aggregate_that_violates_uniqueness() {
        // We construct an aggregate that already has an account number assigned, simulating an 'existing' account
        aggregate = new AccountAggregate(ACCOUNT_ID, "EXISTING-ACC-123", AccountAggregate.AccountType.CHECKING, BigDecimal.ZERO, AccountAggregate.Status.ACTIVE);
    }

    @When("the OpenAccountCmd command is executed on existing account")
    public void the_OpenAccountCmd_command_is_executed_on_existing_account() {
        // Attempting to 'Open' an already open/numbered account should fail due to immutability check
        OpenAccountCmd cmd = new OpenAccountCmd(
            ACCOUNT_ID,
            VALID_CUSTOMER_ID,
            AccountAggregate.AccountType.CHECKING,
            new BigDecimal("100.00"),
            VALID_SORT_CODE
        );
        executeCommand(cmd);
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(thrownException);
        // Checking for common exception types used in Domain layer
        assertTrue(thrownException instanceof IllegalArgumentException || thrownException instanceof IllegalStateException || thrownException instanceof UnknownCommandException);
    }

    // Helper
    private void executeCommand(Command cmd) {
        try {
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
