package com.example.infrastructure.mongo.statement;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "statements")
public class StatementDocument {
  @Id
  private String id;
  @Indexed
  private String accountNumber;
  private boolean generated;
  private boolean exported;
  private String exportedFormat;
  private int version;

  public StatementDocument() {}

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getAccountNumber() { return accountNumber; }
  public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
  public boolean isGenerated() { return generated; }
  public void setGenerated(boolean generated) { this.generated = generated; }
  public boolean isExported() { return exported; }
  public void setExported(boolean exported) { this.exported = exported; }
  public String getExportedFormat() { return exportedFormat; }
  public void setExportedFormat(String exportedFormat) { this.exportedFormat = exportedFormat; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
