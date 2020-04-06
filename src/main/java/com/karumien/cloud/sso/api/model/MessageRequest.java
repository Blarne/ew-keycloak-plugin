package com.karumien.cloud.sso.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Message Request Definition
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-04-05T00:17:09.982+02:00")

public class MessageRequest  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("clientName")
  private String clientName = null;

  @JsonProperty("clientNo")
  private String clientNo = null;

  @JsonProperty("language")
  private String language = null;

  @JsonProperty("messageCode")
  private String messageCode = null;

  @JsonProperty("parameters")
  @Valid
  private List<MessageParameter> parameters = null;

  @JsonProperty("recipients")
  @Valid
  private List<MessageRecipient> recipients = null;

  @JsonProperty("source")
  private String source = null;

  public MessageRequest clientName(String clientName) {
    this.clientName = clientName;
    return this;
  }

  /**
   * Get clientName
   * @return clientName
  **/
@Size(max=350) 
  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public MessageRequest clientNo(String clientNo) {
    this.clientNo = clientNo;
    return this;
  }

  /**
   * Get clientNo
   * @return clientNo
  **/
@Size(max=50) 
  public String getClientNo() {
    return clientNo;
  }

  public void setClientNo(String clientNo) {
    this.clientNo = clientNo;
  }

  public MessageRequest language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
  **/

@Size(max=5) 
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public MessageRequest messageCode(String messageCode) {
    this.messageCode = messageCode;
    return this;
  }

  /**
   * Get messageCode
   * @return messageCode
  **/
  @NotNull

@Size(max=50) 
  public String getMessageCode() {
    return messageCode;
  }

  public void setMessageCode(String messageCode) {
    this.messageCode = messageCode;
  }

  public MessageRequest parameters(List<MessageParameter> parameters) {
    this.parameters = parameters;
    return this;
  }

  public MessageRequest addParametersItem(MessageParameter parametersItem) {
    if (this.parameters == null) {
      this.parameters = new ArrayList<>();
    }
    this.parameters.add(parametersItem);
    return this;
  }

  /**
   * Get parameters
   * @return parameters
  **/
  @Valid

  public List<MessageParameter> getParameters() {
    return parameters;
  }

  public void setParameters(List<MessageParameter> parameters) {
    this.parameters = parameters;
  }

  public MessageRequest recipients(List<MessageRecipient> recipients) {
    this.recipients = recipients;
    return this;
  }

  public MessageRequest addRecipientsItem(MessageRecipient recipientsItem) {
    if (this.recipients == null) {
      this.recipients = new ArrayList<>();
    }
    this.recipients.add(recipientsItem);
    return this;
  }

  /**
   * Get recipients
   * @return recipients
  **/
  @Valid

  public List<MessageRecipient> getRecipients() {
    return recipients;
  }

  public void setRecipients(List<MessageRecipient> recipients) {
    this.recipients = recipients;
  }

  public MessageRequest source(String source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
  **/
  @NotNull

@Size(max=100) 
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageRequest messageRequest = (MessageRequest) o;
    return Objects.equals(this.clientName, messageRequest.clientName) &&
        Objects.equals(this.clientNo, messageRequest.clientNo) &&
        Objects.equals(this.language, messageRequest.language) &&
        Objects.equals(this.messageCode, messageRequest.messageCode) &&
        Objects.equals(this.parameters, messageRequest.parameters) &&
        Objects.equals(this.recipients, messageRequest.recipients) &&
        Objects.equals(this.source, messageRequest.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientName, clientNo, language, messageCode, parameters, recipients, source);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MessageRequest {\n");
    
    sb.append("    clientName: ").append(toIndentedString(clientName)).append("\n");
    sb.append("    clientNo: ").append(toIndentedString(clientNo)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    messageCode: ").append(toIndentedString(messageCode)).append("\n");
    sb.append("    parameters: ").append(toIndentedString(parameters)).append("\n");
    sb.append("    recipients: ").append(toIndentedString(recipients)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
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

