package com.example.infrastructure.mongo.screenmap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "screen_maps")
public class ScreenMapDocument {
  @Id
  private String id;
  private boolean mandatoryFieldsValidated;
  private boolean bmsFieldLengthCompliant;
  private int version;

  public ScreenMapDocument() {}

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public boolean isMandatoryFieldsValidated() { return mandatoryFieldsValidated; }
  public void setMandatoryFieldsValidated(boolean v) { this.mandatoryFieldsValidated = v; }
  public boolean isBmsFieldLengthCompliant() { return bmsFieldLengthCompliant; }
  public void setBmsFieldLengthCompliant(boolean v) { this.bmsFieldLengthCompliant = v; }
  public int getVersion() { return version; }
  public void setVersion(int version) { this.version = version; }
}
