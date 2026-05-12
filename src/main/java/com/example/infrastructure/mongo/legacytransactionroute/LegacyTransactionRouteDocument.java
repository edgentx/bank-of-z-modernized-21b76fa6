package com.example.infrastructure.mongo.legacytransactionroute;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "legacy_transaction_routes")
public class LegacyTransactionRouteDocument {
  @Id
  private String id;
  private int version;

  public LegacyTransactionRouteDocument() {}

  public LegacyTransactionRouteDocument(String id, int version) {
    this.id = id;
    this.version = version;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
