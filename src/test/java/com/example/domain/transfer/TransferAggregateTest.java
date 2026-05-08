package com.example.domain.transfer;

import com.example.domain.transfer.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

public class TransferAggregateTest {

    @Test
    public void testTransferInitiated() {
        var agg = new TransferAggregate("tx-1");
        var cmd = new InitiateTransferCmd("tx-1", "acc-1", "acc-2", new BigDecimal("100"), "USD");
        var events = agg.execute(cmd);

        Assertions.assertEquals(1, events.size());
        Assertions.assertInstanceOf(TransferInitiatedEvent.class, events.get(0));
    }
}
