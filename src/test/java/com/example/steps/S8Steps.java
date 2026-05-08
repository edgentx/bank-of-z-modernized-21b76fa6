package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {

    private StatementAggregate aggregate;
    private final InMemoryStatementRepository repository = new InMemoryStatementRepository();
    private List<DomainEvent> resultEvents;
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate("stmt-123");
        // Setup a default opening balance for the happy path
        aggregate.setOpeningBalance(new BigDecimal("100.00"));
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // Handled in When step construction
    }

    @And("a valid periodEnd is provided")
    public void aValidPeriodEndIsProvided() {
        // Handled in When step construction
    }

    @When("the GenerateStatementCmd command is executed")
    public void theGenerateStatementCmdCommandIsExecuted() {
        try {
            BigDecimal openingBalance = aggregate.getOpeningBalance();
            GenerateStatementCmd cmd = new GenerateStatementCmd(
                "stmt-123",
                "ACC-456",
                Instant.now(),
                openingBalance
            );
            resultEvents = aggregate.execute(cmd);
            repository.save(aggregate);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void aStatementGeneratedEventIsEmitted() {
        assertNotNull(resultEvents);
        assertEquals(1, resultEvents.size());
        assertTrue(resultEvents.get(0) instanceof StatementGeneratedEvent);
        
        StatementGeneratedEvent event = (StatementGeneratedEvent) resultEvents.get(0);
        assertEquals("statement.generated", event.type());
        assertEquals("ACC-456", event.accountNumber());
    }

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        aggregate = new StatementAggregate("stmt-closed");
        aggregate.setOpeningBalance(BigDecimal.ZERO);
        aggregate.setClosed(true);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesOpeningBalance() {
        aggregate = new StatementAggregate("stmt-mismatch");
        // Aggregate expects 100.00, but command will provide 50.00 (simulating mismatch)
        aggregate.setOpeningBalance(new BigDecimal("100.00"));
    }

    @When("the GenerateStatementCmd command is executed with mismatched opening balance")
    public void theGenerateStatementCmdCommandIsExecutedWithMismatch() {
        try {
            // Simulating a mismatch by providing a different opening balance in the command
            // than what is currently set on the aggregate (which represents the 'truth' of previous closing)
            GenerateStatementCmd cmd = new GenerateStatementCmd(
                "stmt-mismatch",
                "ACC-789",
                Instant.now(),
                new BigDecimal("50.00") 
            );
            resultEvents = aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        // Specific message checks depend on the exception type (IllegalArgument vs IllegalState)
        assertTrue(caughtException instanceof IllegalArgumentException || caughtException instanceof IllegalStateException);
    }
}
