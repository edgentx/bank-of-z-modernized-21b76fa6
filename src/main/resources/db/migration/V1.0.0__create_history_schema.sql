-- BANK S-29 — DB2 shared history schema baseline.
--
-- The legacy Bank-of-Z workload retains a long-tail of historical
-- transaction and account records in DB2. During the Strangler Fig
-- cutover (DB2 -> PostgreSQL) the modernized Spring Boot application
-- still needs read-only access to these rows so reporting,
-- reconciliation, and statement generation can span the old + new
-- worlds.
--
-- This baseline creates the two read-side history tables that the
-- modernized JPA entities map to. Columns mirror the legacy DB2 DDL
-- using portable ANSI types (Flyway runs the same script against the
-- DB2 production database and the H2 DB2-compatibility-mode test
-- engine).
--
-- Semantic versioning (V<MAJOR>.<MINOR>.<PATCH>):
--   1.0.0 — initial baseline. Future schema changes will land as
--           V1.1.0, V2.0.0, etc., never as edits to this file.

CREATE TABLE transaction_history (
    transaction_id   VARCHAR(64)   NOT NULL,
    account_id       VARCHAR(64)   NOT NULL,
    kind             VARCHAR(16)   NOT NULL,
    amount           DECIMAL(19,4) NOT NULL,
    currency         VARCHAR(3)    NOT NULL,
    posted_at        TIMESTAMP     NOT NULL,
    reversed         SMALLINT      DEFAULT 0 NOT NULL,
    legacy_source    VARCHAR(32)   DEFAULT 'DB2' NOT NULL,
    CONSTRAINT pk_transaction_history PRIMARY KEY (transaction_id)
);

CREATE INDEX idx_txn_history_account ON transaction_history (account_id);
CREATE INDEX idx_txn_history_kind    ON transaction_history (kind);
CREATE INDEX idx_txn_history_posted  ON transaction_history (posted_at);

CREATE TABLE account_history (
    account_id       VARCHAR(64)   NOT NULL,
    customer_id      VARCHAR(64)   NOT NULL,
    sort_code        VARCHAR(16),
    account_type     VARCHAR(32),
    status           VARCHAR(16)   NOT NULL,
    opened_at        TIMESTAMP,
    closed_at        TIMESTAMP,
    legacy_source    VARCHAR(32)   DEFAULT 'DB2' NOT NULL,
    CONSTRAINT pk_account_history PRIMARY KEY (account_id)
);

CREATE INDEX idx_acct_history_customer ON account_history (customer_id);
CREATE INDEX idx_acct_history_status   ON account_history (status);
