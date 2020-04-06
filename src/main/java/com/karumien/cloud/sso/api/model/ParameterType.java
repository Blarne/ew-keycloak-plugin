package com.karumien.cloud.sso.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ParameterType
 */
public enum ParameterType {
  
  SUBJECT("Subject"),
  
  BODY("Body");

  private String value;

  ParameterType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ParameterType fromValue(String text) {
    for (ParameterType b : ParameterType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

