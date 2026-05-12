package com.example.infrastructure.mongo.reconciliationbatch;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB persistence model for {@link com.example.domain.reconciliationbatch.model.ReconciliationBatchAggregate}.
 *
 * The canonical aggregate is currently a scaffold stub — only id + version are
 * captured. Adding fields here later is additive and does not break readers
 * that pre-date them, which is the whole point of Mongo's flexible schema.
 */
@Document(collection = "reconciliation_batches")
public class ReconciliationBatchDocument {
  @Id
  private String id;
  private int version;

  public ReconciliationBatchDocument() {}

  public ReconciliationBatchDocument(String id, int version) {
    this.id = id;
    this.version = version;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
