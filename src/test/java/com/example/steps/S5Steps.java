package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.OpenAccountCmd;
import com.example.domain.shared.DomainException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

public class S5Steps {

    private AccountAggregate aggregate;
    private String testAccountId = "acc-123";
    private String testCustomerId = "cust-456";
    private String testAccountType = "STANDARD";
    private BigDecimal testInitialDeposit = new BigDecimal("500.00");
    private String testSortCode = "10-20-30";
    
    private Exception capturedException;
    private List<com.example.domain.shared.DomainEvent> resultEvents;

    @Given("a valid Account aggregate")
    public void a_valid_account_aggregate() {
        aggregate = new AccountAggregate(testAccountId);
        // Initially status is NONE
    }

    @Given("a Account aggregate that violates: Account balance cannot drop below the minimum required balance for its specific account type.")
    public void a_account_aggregate_that_violates_balance() {
        aggregate = new AccountAggregate(testAccountId);
        // Setup: A PREMIER account with insufficient funds
        testAccountType = "PREMIER";
        testInitialDeposit = new BigDecimal("100"); // Too low for PREMIER (min 1000)
    }

    @Given("a Account aggregate that violates: An account must be in an Active status to process withdrawals or transfers.")
    public void a_account_aggregate_that_violates_status() {
        aggregate = new AccountAggregate(testAccountId);
        // Setup: Although OpenAccountCmd creates Active, 
        // we simulate a state where the aggregate is NOT active (e.g. CLOSED/FROZEN) 
        // and the command execution logic should catch this or we assume the invariants
        // prevent the transition to Active. 
        // For this specific story, the Invariants listed in the feature file seem to be 
        // checks performed *during* the opening or checks that would apply to other commands.
        // Interpreting strictly for OpenAccountCmd: If the account is already OPENED (Active), 
        // we can't open it again. 
        aggregate.execute(new OpenAccountCmd(testAccountId, testCustomerId, "STANDARD", BigDecimal.ZERO, testSortCode));
        // Now it is ACTIVE. Trying to open again should fail.
    }

    @Given("a Account aggregate that violates: Account numbers must be uniquely generated and immutable.")
    public void a_account_aggregate_that_violates_uniqueness() {
        // This is enforced by the Repository usually. In the Aggregate, we check for null/blank.
        aggregate = new AccountAggregate(""); // Invalid ID
        testAccountId = "";
    }

    @And("a valid customerId is provided")
    public void a_valid_customer_id_is_provided() {
        testCustomerId = "cust-valid-99";
    }

    @And("a valid accountType is provided")
    public void a_valid_account_type_is_provided() {
        testAccountType = "STANDARD";
    }

    @And("a valid initialDeposit is provided")
    public void a_valid_initial_deposit_is_provided() {
        testInitialDeposit = new BigDecimal("100.00");
    }

    @And("a valid sortCode is provided")
    public void a_valid_sort_code_is_provided() {
        testSortCode = "10-10-10";
    }

    @When("the OpenAccountCmd command is executed")
    public void the_open_account_cmd_command_is_executed() {
        try {
            OpenAccountCmd cmd = new OpenAccountCmd(testAccountId, testCustomerId, testAccountType, testInitialDeposit, testSortCode);
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            capturedException = e;
        }
    }

    @Then("a account.opened event is emitted")
    public void a_account_opened_event_is_emitted() {
        assertNotNull(resultEvents);
        assertFalse(resultEvents.isEmpty());
        assertEquals("account.opened", resultEvents.get(0).type());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        assertNotNull(capturedException);
        // Depending on strictness, could be IllegalArgumentException or IllegalStateException
        assertTrue(capturedException instanceof IllegalArgumentException || capturedException instanceof IllegalStateException);
    }
}
