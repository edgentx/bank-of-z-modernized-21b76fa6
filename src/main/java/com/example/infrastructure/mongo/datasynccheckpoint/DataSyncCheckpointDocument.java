package com.example.infrastructure.mongo.datasynccheckpoint;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "data_sync_checkpoints")
public class DataSyncCheckpointDocument {
  @Id
  private String id;
  private int version;

  public DataSyncCheckpointDocument() {}

  public DataSyncCheckpointDocument(String id, int version) {
    this.id = id;
    this.version = version;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
