package com.example.steps;

import com.example.domain.shared.DomainEvent;
import com.example.domain.statement.model.ExportStatementCmd;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.model.StatementExportedEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Story-specific step definitions for S-9 (ExportStatementCmd).
 * Shared Statement Given steps live in {@link StatementSharedSteps};
 * scenario state is shared via {@link StatementSharedContext}. The
 * "the command is rejected with a domain error" assertion is supplied
 * by {@link AccountSharedSteps} and reused here via picocontainer glue.
 */
public class S9Steps {

    private final StatementSharedContext ctx;
    private final ScenarioContext sc;

    public S9Steps(StatementSharedContext ctx, ScenarioContext sc) {
        this.ctx = ctx;
        this.sc = sc;
    }

    @When("the ExportStatementCmd command is executed")
    public void theExportStatementCmdCommandIsExecuted() {
        try {
            StatementAggregate agg = ctx.repository.findById(ctx.aggregate.id()).orElse(ctx.aggregate);
            agg.execute(new ExportStatementCmd(agg.id(), "PDF"));
            ctx.repository.save(agg);
        } catch (Throwable t) {
            ctx.thrownException = t;
            sc.thrownException = t;
        }
    }

    @Then("a statement.exported event is emitted")
    public void aStatementExportedEventIsEmitted() {
        Assertions.assertNull(ctx.thrownException, "Expected no error, but got: " + ctx.thrownException);
        StatementAggregate updated = ctx.repository.findById(ctx.aggregate.id()).orElseThrow();
        List<DomainEvent> events = updated.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        DomainEvent event = events.get(events.size() - 1);
        Assertions.assertEquals("statement.exported", event.type());
        Assertions.assertInstanceOf(StatementExportedEvent.class, event);
    }
}
