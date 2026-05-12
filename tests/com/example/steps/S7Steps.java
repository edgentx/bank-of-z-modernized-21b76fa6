package com.example.steps;

import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.model.AccountClosedEvent;
import com.example.domain.account.model.CloseAccountCmd;
import com.example.domain.shared.DomainEvent;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Story-specific step definitions for S-7 (CloseAccountCmd).
 * Shared Account Given/Then steps live in {@link AccountSharedSteps};
 * scenario state is shared via {@link AccountSharedContext}. The
 * "a valid accountNumber is provided" step is defined in {@link S6Steps}
 * and is reused here via picocontainer glue.
 */
public class S7Steps {

    private final AccountSharedContext ctx;

    public S7Steps(AccountSharedContext ctx) {
        this.ctx = ctx;
    }

    @When("the CloseAccountCmd command is executed")
    public void theCloseAccountCmdCommandIsExecuted() {
        try {
            AccountAggregate agg = ctx.repository.findById(ctx.aggregate.id()).orElse(ctx.aggregate);
            String accountNumber = ctx.accountNumber != null ? ctx.accountNumber : "ACC-DEFAULT";
            agg.execute(new CloseAccountCmd(accountNumber));
            ctx.repository.save(agg);
        } catch (Throwable t) {
            ctx.thrownException = t;
        }
    }

    @Then("a account.closed event is emitted")
    public void aAccountClosedEventIsEmitted() {
        Assertions.assertNull(ctx.thrownException, "Expected no error, but got: " + ctx.thrownException);
        AccountAggregate updated = ctx.repository.findById(ctx.aggregate.id()).orElseThrow();
        List<DomainEvent> events = updated.uncommittedEvents();
        Assertions.assertFalse(events.isEmpty(), "Expected events to be emitted");
        DomainEvent event = events.get(0);
        Assertions.assertEquals("account.closed", event.type());
        Assertions.assertInstanceOf(AccountClosedEvent.class, event);
    }
}
