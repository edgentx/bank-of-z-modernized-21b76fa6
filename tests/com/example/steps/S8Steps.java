package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.GenerateStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementGeneratedEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.util.List;

/**
 * Story-specific step definitions for S-8 (GenerateStatementCmd).
 * Shared Statement Given steps live in {@link StatementSharedSteps};
 * scenario state is shared via {@link StatementSharedContext}. The
 * "a valid accountNumber is provided" step is defined in {@link S6Steps}
 * and reused here via picocontainer glue.
 */
public class S8Steps {

    private final StatementSharedContext ctx;
    private final ScenarioContext sc;

    public S8Steps(StatementSharedContext ctx, ScenarioContext sc) {
        this.ctx = ctx;
        this.sc = sc;
    }

    @When("the GenerateStatementCmd command is executed")
    public void theGenerateStatementCmdCommandIsExecuted() {
        try {
            StatementAggregate agg = ctx.repository.findById(ctx.aggregate.id()).orElse(ctx.aggregate);
            agg.execute(new GenerateStatementCmd("ACCT-001", LocalDate.of(2026, 1, 31)));
            ctx.repository.save(agg);
        } catch (Throwable t) {
            ctx.thrownException = t;
            sc.thrownException = t;
        }
    }

    @Then("a statement.generated event is emitted")
    public void aStatementGeneratedEventIsEmitted() {
        Assertions.assertNull(ctx.thrownException, "Expected no error, but got: " + ctx.thrownException);
        StatementAggregate updated = ctx.repository.findById(ctx.aggregate.id()).orElseThrow();
        List<DomainEvent> events = updated.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        DomainEvent event = events.get(0);
        Assertions.assertEquals("statement.generated", event.type());
        Assertions.assertInstanceOf(StatementGeneratedEvent.class, event);
    }
}
