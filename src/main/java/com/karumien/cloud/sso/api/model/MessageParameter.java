package com.karumien.cloud.sso.api.model;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MessageParameter
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-04-05T00:17:09.982+02:00")

public class MessageParameter  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("parameterType")
  private ParameterType parameterType = null;

  @JsonProperty("isVariable")
  private Boolean isVariable = null;

  @JsonProperty("value")
  private String value = null;

  public MessageParameter parameterType(ParameterType parameterType) {
    this.parameterType = parameterType;
    return this;
  }

  /**
   * Get parameterType
   * @return parameterType
  **/
  @NotNull

  @Valid

  public ParameterType getParameterType() {
    return parameterType;
  }

  public void setParameterType(ParameterType parameterType) {
    this.parameterType = parameterType;
  }

  public MessageParameter isVariable(Boolean isVariable) {
    this.isVariable = isVariable;
    return this;
  }

  /**
   * Get isVariable
   * @return isVariable
  **/


  public Boolean isIsVariable() {
    return isVariable;
  }

  public void setIsVariable(Boolean isVariable) {
    this.isVariable = isVariable;
  }

  public MessageParameter value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  **/
  @NotNull

@Size(max=250) 
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageParameter messageParameter = (MessageParameter) o;
    return Objects.equals(this.parameterType, messageParameter.parameterType) &&
        Objects.equals(this.isVariable, messageParameter.isVariable) &&
        Objects.equals(this.value, messageParameter.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parameterType, isVariable, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MessageParameter {\n");
    
    sb.append("    parameterType: ").append(toIndentedString(parameterType)).append("\n");
    sb.append("    isVariable: ").append(toIndentedString(isVariable)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

