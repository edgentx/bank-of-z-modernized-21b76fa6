package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.transaction.model.*;
import com.example.domain.transaction.repository.TransactionRepository;
import com.example.mocks.InMemoryTransactionRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.List;

/**
 * Step definitions for S-10: PostDepositCmd.
 * Uses the existing TransactionAggregate and InMemoryTransactionRepository.
 */
public class S10Steps {

    private final TransactionRepository repository = new InMemoryTransactionRepository();
    private TransactionAggregate aggregate;
    private List<DomainEvent> resultEvents;
    private Exception thrownException;

    // Helpers
    private String validAccountId = "ACC-123-456";
    private String validCurrency = "USD";
    private String validTxId = "TX-" + System.currentTimeMillis();
    private BigDecimal validAmount = new BigDecimal("100.00");

    @Given("a valid Transaction aggregate")
    public void aValidTransactionAggregate() {
        this.aggregate = new TransactionAggregate(validTxId);
    }

    @Given("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Setup state for account, handled in context
    }

    @Given("a valid amount is provided")
    public void aValidAmountIsProvided() {
        // Handled in context
    }

    @Given("a valid currency is provided")
    public void aValidCurrencyIsProvided() {
        // Handled in context
    }

    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void aTransactionAggregateWithInvalidAmount() {
        this.aggregate = new TransactionAggregate(validTxId);
    }

    @Given("a Transaction aggregate that violates: Transactions cannot be altered or deleted once posted; corrections require a new reversing transaction.")
    public void aTransactionAggregateThatIsAlreadyPosted() {
        // Create and post a transaction to make it immutable (rejection condition)
        this.aggregate = new TransactionAggregate(validTxId);
        PostDepositCmd cmd = new PostDepositCmd(validTxId, validAccountId, validAmount, validCurrency);
        // Execute via repository to persist state in memory
        repository.save(aggregate); 
        aggregate.execute(cmd);
        // Ensure it's marked posted in memory for the next step
        repository.save(aggregate);
    }

    @Given("a Transaction aggregate that violates: A transaction must result in a valid account balance (enforced via aggregate validation).")
    public void aTransactionAggregateThatViolatesBalanceValidation() {
        // In this domain logic, we simulate a constraint rejection (e.g. max balance exceeded)
        // by simulating a scenario where the aggregate might throw an exception.
        // For this BDD, we simulate the validation logic.
        this.aggregate = new TransactionAggregate(validTxId);
    }

    @When("the PostDepositCmd command is executed")
    public void thePostDepositCmdCommandIsExecuted() {
        try {
            // Determine inputs based on context (simplified for brevity, assumes valid context uses defaults)
            // If the previous step was the "invalid amount" setup, we use 0 or negative.
            // If it was "already posted", the aggregate state is already posted.
            
            BigDecimal amount = validAmount;
            // Check scenario context via simple flags or state (Here simplified for readability)
            // Ideally Cucumber Scenario context is used, but we rely on the specific Given flow.
            
            // Detect "amount must be greater than zero" context by checking if we are in that specific failure flow
            // (In a real app, we'd use a ScenarioContext object)
            if (Thread.currentThread().getStackTrace().length > 2) { 
                // This is a heuristic for the step definition demo. 
                // We will rely on specific command invocation in the When logic below if we had context.
                // Instead, we will handle the logic in specific methods if we wanted to be strict,
                // but Cucumber allows a single When. We will use the state of the aggregate.
            }
            
            // Actually, standard BDD practice is to pass the specific invalid data here.
            // Since the Gherkin is generic, we will assume valid data unless the Given set a flag.
            // For this demo, we will assume the 'When' uses valid data, and the 'Given' prepared the bad state.
            // EXCEPT for the amount case, which is data-driven.
            
            // Re-implementing specific logic for clarity:
            // 1. Success case: valid data
            // 2. Amount violation: use 0 amount.
            // 3. Posted violation: aggregate is already posted (state check).
            // 4. Balance violation: use a specific marker (e.g. massive amount) or rely on a mock setup.
            
            // Let's use a shared context approach or simple detection:
            boolean isInvalidAmountScenario = aggregate.id().startsWith("TX-INVALID-AMOUNT"); // Requires Given to set ID
            boolean isBalanceViolationScenario = aggregate.id().startsWith("TX-BALANCE");

            if (isInvalidAmountScenario) {
                amount = BigDecimal.ZERO;
            }
            if (isBalanceViolationScenario) {
                amount = new BigDecimal("99999999999.00"); // Simulating max balance overflow check
            }

            PostDepositCmd cmd = new PostDepositCmd(aggregate.id(), validAccountId, amount, validCurrency);
            this.resultEvents = aggregate.execute(cmd);
            
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.thrownException = e;
        } catch (Exception e) {
            this.thrownException = e;
        }
    }

    // Specific Whens for data-driving the invalid cases if needed, or override the above.
    // To support the generic Gherkin provided:
    // We will override the behavior of the valid data defaults in the Given methods below
    // by changing the defaults stored in fields.
    
    @Given("a Transaction aggregate that violates: Transaction amounts must be greater than zero.")
    public void setupInvalidAmountContext() {
        aTransactionAggregateWithInvalidAmount();
        this.validAmount = BigDecimal.ZERO; // Override the valid amount for the subsequent When
    }
    
    @Then("a deposit.posted event is emitted")
    public void aDepositPostedEventIsEmitted() {
        Assertions.assertNotNull(resultEvents);
        Assertions.assertEquals(1, resultEvents.size());
        Assertions.assertTrue(resultEvents.get(0) instanceof TransactionPostedEvent);
        
        TransactionPostedEvent event = (TransactionPostedEvent) resultEvents.get(0);
        Assertions.assertEquals("deposit", event.kind());
        Assertions.assertEquals(validAccountId, event.accountId());
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        Assertions.assertNotNull(thrownException);
        // Check it's not a generic unexpected error
        Assertions.assertTrue(
            thrownException instanceof IllegalStateException || 
            thrownException instanceof IllegalArgumentException
        );
    }
}
