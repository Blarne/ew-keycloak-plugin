package com.karumien.cloud.sso.api.model;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * MessageRecipient
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-04-05T00:17:09.982+02:00")

public class MessageRecipient  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("address")
  private String address = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("phone")
  private String phone = null;

  public MessageRecipient address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
  **/

@Size(max=250) 
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public MessageRecipient name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  **/
  @NotNull

@Size(max=500) 
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MessageRecipient phone(String phone) {
    this.phone = phone;
    return this;
  }

  /**
   * Get phone
   * @return phone
  **/

@Size(max=100) 
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageRecipient messageRecipient = (MessageRecipient) o;
    return Objects.equals(this.address, messageRecipient.address) &&
        Objects.equals(this.name, messageRecipient.name) &&
        Objects.equals(this.phone, messageRecipient.phone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, name, phone);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MessageRecipient {\n");
    
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
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

