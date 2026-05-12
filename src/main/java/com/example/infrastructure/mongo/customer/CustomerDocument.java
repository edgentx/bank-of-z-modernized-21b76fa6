package com.example.infrastructure.mongo.customer;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB persistence model for {@link com.example.domain.customer.model.CustomerAggregate}.
 *
 * Indexed fields:
 *   - email (commonly queried for de-duplication / lookup),
 *   - sortCode (used by Account-creation flows to scope by branch).
 */
@Document(collection = "customers")
public class CustomerDocument {
  @Id
  private String id;
  @Indexed
  private String email;
  @Indexed
  private String sortCode;
  private String fullName;
  private boolean enrolled;
  private int version;

  public CustomerDocument() {}

  public CustomerDocument(String id, String fullName, String email, String sortCode, boolean enrolled, int version) {
    this.id = id;
    this.fullName = fullName;
    this.email = email;
    this.sortCode = sortCode;
    this.enrolled = enrolled;
    this.version = version;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getSortCode() { return sortCode; }
  public void setSortCode(String sortCode) { this.sortCode = sortCode; }
  public boolean isEnrolled() { return enrolled; }
  public void setEnrolled(boolean enrolled) { this.enrolled = enrolled; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
