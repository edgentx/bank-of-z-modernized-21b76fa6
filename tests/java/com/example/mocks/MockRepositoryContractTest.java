package com.example.mocks;

import com.example.domain.customer.model.CustomerAggregate;
import com.example.domain.customer.repository.CustomerRepository;
import com.example.domain.account.model.AccountAggregate;
import com.example.domain.account.repository.AccountRepository;
import com.example.domain.statement.model.StatementAggregate;
import com.example.domain.statement.repository.StatementRepository;
import com.example.domain.transaction.model.TransactionAggregate;
import com.example.domain.transaction.repository.TransactionRepository;
import com.example.domain.transfer.model.TransferAggregate;
import com.example.domain.transfer.repository.TransferRepository;
import com.example.domain.reconciliation.model.ReconciliationBatchAggregate;
import com.example.domain.reconciliation.repository.ReconciliationBatchRepository;
import com.example.domain.teller.model.TellerSessionAggregate;
import com.example.domain.teller.repository.TellerSessionRepository;
import com.example.domain.ui.model.ScreenMapAggregate;
import com.example.domain.ui.repository.ScreenMapRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract test to verify all Mock Repositories implement their interfaces correctly
 * and behave as standard in-memory stores.
 */
class MockRepositoryContractTest {

    @Test
    void customerRepositoryShouldImplementInterface() {
        CustomerRepository repo = new MockCustomerRepository();
        CustomerAggregate aggregate = new CustomerAggregate("cust-1");
        repo.save(aggregate);
        assertEquals("cust-1", repo.findById("cust-1").id());
    }

    @Test
    void accountRepositoryShouldImplementInterface() {
        AccountRepository repo = new MockAccountRepository();
        AccountAggregate aggregate = new AccountAggregate("acc-1");
        repo.save(aggregate);
        assertEquals("acc-1", repo.findById("acc-1").id());
    }

    @Test
    void statementRepositoryShouldImplementInterface() {
        StatementRepository repo = new MockStatementRepository();
        StatementAggregate aggregate = new StatementAggregate("stmt-1");
        repo.save(aggregate);
        assertEquals("stmt-1", repo.findById("stmt-1").id());
    }

    @Test
    void transactionRepositoryShouldImplementInterface() {
        TransactionRepository repo = new MockTransactionRepository();
        TransactionAggregate aggregate = new TransactionAggregate("txn-1");
        repo.save(aggregate);
        assertEquals("txn-1", repo.findById("txn-1").id());
    }

    @Test
    void transferRepositoryShouldImplementInterface() {
        TransferRepository repo = new MockTransferRepository();
        TransferAggregate aggregate = new TransferAggregate("txf-1");
        repo.save(aggregate);
        assertEquals("txf-1", repo.findById("txf-1").id());
    }

    @Test
    void reconciliationBatchRepositoryShouldImplementInterface() {
        ReconciliationBatchRepository repo = new MockReconciliationBatchRepository();
        ReconciliationBatchAggregate aggregate = new ReconciliationBatchAggregate("rb-1");
        repo.save(aggregate);
        assertEquals("rb-1", repo.findById("rb-1").id());
    }

    @Test
    void tellerSessionRepositoryShouldImplementInterface() {
        TellerSessionRepository repo = new MockTellerSessionRepository();
        TellerSessionAggregate aggregate = new TellerSessionAggregate("teller-1");
        repo.save(aggregate);
        assertEquals("teller-1", repo.findById("teller-1").id());
    }

    @Test
    void screenMapRepositoryShouldImplementInterface() {
        ScreenMapRepository repo = new MockScreenMapRepository();
        ScreenMapAggregate aggregate = new ScreenMapAggregate("screen-1");
        repo.save(aggregate);
        assertEquals("screen-1", repo.findById("screen-1").id());
    }
}
