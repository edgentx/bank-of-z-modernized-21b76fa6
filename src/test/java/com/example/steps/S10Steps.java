package com.example.steps;

import com.example.domain.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.UUID;

public class S10Steps {

    // Context state
    private Transaction transaction;
    private TransactionError capturedError;
    private DepositPostedEvent lastEvent;

    // Test Data
    private final String validAccountNumber = "ACC-1001";
    private final BigDecimal validAmount = new BigDecimal("500.00");
    private final String validCurrency = "USD";

    @Given("a valid Transaction aggregate")
    public void a_valid_Transaction_aggregate() {
        // Create a fresh transaction
        this.transaction = new Transaction(UUID.randomUUID());
        // Pre-condition: Account balance is valid (e.g., 0)
        this.transaction.setCurrentBalance(new BigDecimal("0.00"));
    }

    @Given("a valid accountNumber is provided")
    public void a_valid_accountNumber_is_provided() {
        // Intentionally empty: state is implicit in the test data constants
    }

    @Given("a valid amount is provided")
    public void a_valid_amount_is_provided() {
        // Intentionally empty
    }

    @Given("a valid currency is provided")
    public void a_valid_currency_is_provided() {
        // Intentionally empty
    }

    // --- Negative Scenarios (Violations) ---

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void a_Transaction_aggregate_that_violates_amounts_must_be_greater_than_zero() {
        this.transaction = new Transaction(UUID.randomUUID());
        this.transaction.setCurrentBalance(BigDecimal.ZERO);
        // We will pass invalid amount in the 'When' step
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void a_Transaction_aggregate_that_violates_cannot_be_altered() {
        this.transaction = new Transaction(UUID.randomUUID());
        this.transaction.setCurrentBalance(BigDecimal.ZERO);
        // Simulate that the transaction is already posted/finalized
        this.transaction.markAsPosted();
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void a_Transaction_aggregate_that_violates_valid_account_balance() {
        this.transaction = new Transaction(UUID.randomUUID());
        // Set a balance such that a deposit would exceed max limit (assuming max is 1000)
        this.transaction.setCurrentBalance(new BigDecimal("999.99"));
    }

    // --- Actions ---

    @When("the PostDepositCmd command is executed")
    public void the_PostDepositCmd_command_is_executed() {
        try {
            // Determine data based on context (Simulated)
            String acc = validAccountNumber;
            BigDecimal amt = validAmount;
            String curr = validCurrency;

            // Heuristic overrides based on the 'Given' context setup above
            if (transaction.getCurrentBalance().compareTo(new BigDecimal("999.00")) > 0) {
                amt = new BigDecimal("500.00"); // This will trigger the balance overflow invariant
            }
            if (transaction.isPosted()) {
                amt = validAmount; // Amount doesn't matter, status blocks it
            }
            // Check for zero/negative amount context
            // Since we can't inspect the text of the previous step directly, we assume
            // specific state. However, for the specific 'zero amount' test:
            // We will check a flag or thread-local. 
            // Simplified: If the transaction is new but we haven't set specific overrides, 
            // we default to valid. 
            // *Refinement*: To strictly follow the BDD flow without complex state management:
            // We rely on the specific 'Given' setting internal flags or state that the Execute method checks.
            // For the 'Zero amount' scenario, we can't know '0' was intended unless we pass it.
            // I will assume the test runner context implies the data. 
            // *Correction*: Let's look at the specific violation setup. 
            // If the violation was "amounts > 0", we need to pass 0.
            // I will use a heuristic: If the transaction is in a base state, but the scenario description matches,
            // we pass the violating data. 
            // BETTER APPROACH: The steps should set the data directly.
            // But the Gherkin provided separates "Valid Account" from "Violation Given".
            // I will interpret the flow: The 'Violation Given' sets up the Aggregate state (e.g. Posted, or Balance).
            // What about the Amount violation? That's a Command validation.
            // I'll pass '0' if the aggregate is 'fresh' but the scenario logic implies we are testing the invariant.
            // Actually, let's just assume the steps act on the command payload.
            
            // Handling the "Amount > 0" scenario specifically:
            // Since I can't detect the scenario name easily here, I will add a helper logic or 
            // assume the 'Given' setup for that scenario does something distinct (like setting a flag).
            // To keep it simple and working: I will assume the command payload is driven by the setup.
            // If the setup method for zero amount was called, I'd set a payload.
            // Since I can't share state easily between steps in this generated code without a shared context object:
            // I'll assume a specific condition.
            
            // Let's refine the 'Violations' logic.
            // Scenario: Amount <= 0. 
            // If I use a variable 'testAmount' initialized to validAmount (500), and set to 0 in the violation Given.
            
        } catch (TransactionError e) {
            capturedError = e;
        }
    }

    // Refined When method to handle specific data context
    @When("the PostDepositCmd command is executed with specific context")
    public void the_PostDepositCmd_command_is_executed_with_context() {
        try {
            String acc = validAccountNumber;
            BigDecimal amt = validAmount; // Default valid
            String curr = validCurrency;

            // Detecting context based on Aggregate state (as set in Given)
            // 1. Zero amount violation: (No specific aggregate state check, need a flag)
            // 2. Posted violation: transaction.isPosted() == true
            // 3. Balance violation: transaction.getBalance() > 999
            
            // To handle the Zero Amount case, let's assume if the balance is 0 and not posted,
            // but we are in a 'violation' mode (hard to know), we pass 0.
            // Let's make it robust: The 'Given' for amount violation is the only one
            // that leaves the balance at 0.0 and unposted.
            // BUT, the Success scenario also leaves it at 0.0 and unposted.
            // To distinguish, I will check a ThreadLocal or simple field 'forceInvalidAmount'.
            // For the sake of this generated code, I will add a field.
        } catch (TransactionError e) {
            capturedError = e;
        }
    }

    // --- Clean implementation of the Step Logic ---
    
    private String cmdAccountNumber;
    private BigDecimal cmdAmount;
    private String cmdCurrency;

    @Given("a valid accountNumber is provided")
    public void setValidAccountNumber() {
        this.cmdAccountNumber = "ACC-123";
    }

    @Given("a valid amount is provided")
    public void setValidAmount() {
        this.cmdAmount = new BigDecimal("100.00");
    }

    @Given("a valid currency is provided")
    public void setValidCurrency() {
        this.cmdCurrency = "USD";
    }

    // Violation setup for Amount
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void setupZeroAmountViolation() {
        this.transaction = new Transaction(UUID.randomUUID());
        this.transaction.setCurrentBalance(BigDecimal.ZERO);
        // Set the command payload to invalid for the upcoming When
        this.cmdAmount = BigDecimal.ZERO;
        this.cmdAccountNumber = "ACC-123";
        this.cmdCurrency = "USD";
    }

    // Violation setup for Posted
    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted")
    public void setupAlreadyPostedViolation() {
        this.transaction = new Transaction(UUID.randomUUID());
        this.transaction.setCurrentBalance(BigDecimal.ZERO);
        this.transaction.markAsPosted();
        // Command payload is valid, but Aggregate state rejects it
        this.cmdAmount = new BigDecimal("100.00");
        this.cmdAccountNumber = "ACC-123";
        this.cmdCurrency = "USD";
    }

    // Violation setup for Balance
    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance")
    public void setupBalanceViolation() {
        this.transaction = new Transaction(UUID.randomUUID());
        // Balance is already at max (assuming invariant is balance < 1000)
        this.transaction.setCurrentBalance(new BigDecimal("999.99"));
        // Deposit is valid, but result is invalid
        this.cmdAmount = new BigDecimal("100.00");
        this.cmdAccountNumber = "ACC-123";
        this.cmdCurrency = "USD";
    }

    @When("the PostDepositCmd command is executed")
    public void executeCommand() {
        try {
            // For the success scenario, ensure defaults are set if not overridden
            if (this.cmdAmount == null) this.cmdAmount = new BigDecimal("100.00");
            if (this.cmdAccountNumber == null) this.cmdAccountNumber = "ACC-123";
            if (this.cmdCurrency == null) this.cmdCurrency = "USD";
            if (this.transaction == null) {
                this.transaction = new Transaction(UUID.randomUUID());
                this.transaction.setCurrentBalance(BigDecimal.ZERO);
            }

            PostDepositCommand cmd = new PostDepositCommand(this.cmdAccountNumber, this.cmdAmount, this.cmdCurrency);
            lastEvent = transaction.execute(cmd);
        } catch (TransactionError e) {
            capturedError = e;
        }
    }

    @Then("a deposit.posted event is emitted")
    public void a_deposit_posted_event_is_emitted() {
        Assertions.assertNotNull(lastEvent, "Event should not be null");
        Assertions.assertEquals("deposit.posted", lastEvent.getType());
        Assertions.assertEquals(cmdAmount, lastEvent.getAmount());
    }

    @Then("the command is rejected with a domain error")
    public void the_command_is_rejected_with_a_domain_error() {
        Assertions.assertNotNull(capturedError, "A TransactionError should have been thrown");
    }
}