package org.dulab.adapcompounddb.models.enums;

public enum StatusType {
  NOT_STARTED("Not Started"), PENDING("Pending"), FINISHED("Finished");

  private final String label;

  StatusType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
