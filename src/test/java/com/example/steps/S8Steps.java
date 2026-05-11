package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import com.example.domain.statement.repository.StatementRepository;
import com.example.mocks.InMemoryStatementRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class S8Steps {
    private StatementRepository repository = new InMemoryStatementRepository();
    private StatementAggregate aggregate;
    private String statementId = "stmt-1";
    private String accountNumber = "acct-123";
    private LocalDate periodStart = LocalDate.of(2023, 1, 1);
    private LocalDate periodEnd = LocalDate.of(2023, 1, 31);
    private BigDecimal openingBalance = new BigDecimal("100.00");
    private Exception caughtException;

    @Given("a valid Statement aggregate")
    public void aValidStatementAggregate() {
        aggregate = new StatementAggregate(statementId);
    }

    @And("a valid accountNumber is provided")
    public void aValidAccountNumberIsProvided() {
        // accountNumber is set in initialization
        assertNotNull(accountNumber);
    }

    @And("a valid periodEnd is provided")
    public void aValidPeriodEndIsProvided() {
        // periodEnd is set in initialization
        assertNotNull(periodEnd);
    }

    @When("the GenerateStatementCmd command is executed")
    public void theGenerateStatementCmdCommandIsExecuted() {
        var cmd = new GenerateStatementCmd(statementId, accountNumber, periodStart, periodEnd, openingBalance);
        try {
            aggregate.execute(cmd);
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("a statement.generated event is emitted")
    public void aStatementGeneratedEventIsEmitted() {
        List<DomainEvent> events = aggregate.uncommittedEvents();
        assertFalse(events.isEmpty());
        assertTrue(events.get(0) instanceof StatementGeneratedEvent);
        StatementGeneratedEvent event = (StatementGeneratedEvent) events.get(0);
        assertEquals("statement.generated", event.type());
        assertEquals(statementId, event.aggregateId());
    }

    // --- Error Scenarios ---

    @Given("a Statement aggregate that violates: A statement must be generated for a closed period and cannot be altered retroactively.")
    public void aStatementAggregateThatViolatesClosedPeriod() {
        aggregate = new StatementAggregate(statementId);
        // Future date to violate closed period invariant
        periodEnd = LocalDate.now().plusDays(1);
    }

    @Given("a Statement aggregate that violates: Statement opening balance must exactly match the closing balance of the previous statement.")
    public void aStatementAggregateThatViolatesOpeningBalance() {
        aggregate = new StatementAggregate(statementId);
        // Negative balance to violate business rule (simplified check)
        openingBalance = new BigDecimal("-50.00");
    }

    @Then("the command is rejected with a domain error")
    public void theCommandIsRejectedWithADomainError() {
        assertNotNull(caughtException);
        assertTrue(caughtException instanceof IllegalStateException);
    }
}
