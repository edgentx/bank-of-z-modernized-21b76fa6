package com.example.infrastructure.mongo.tellersession;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "teller_sessions")
public class TellerSessionDocument {
  @Id
  private String id;
  @Indexed
  private String status;
  private boolean authenticated;
  private boolean timedOut;
  private boolean inactivityTimeoutRuleViolated;
  private boolean navigationStateValid;
  private int version;

  public TellerSessionDocument() {}

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public boolean isAuthenticated() { return authenticated; }
  public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
  public boolean isTimedOut() { return timedOut; }
  public void setTimedOut(boolean timedOut) { this.timedOut = timedOut; }
  public boolean isInactivityTimeoutRuleViolated() { return inactivityTimeoutRuleViolated; }
  public void setInactivityTimeoutRuleViolated(boolean v) { this.inactivityTimeoutRuleViolated = v; }
  public boolean isNavigationStateValid() { return navigationStateValid; }
  public void setNavigationStateValid(boolean v) { this.navigationStateValid = v; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
