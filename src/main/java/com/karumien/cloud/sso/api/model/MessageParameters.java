package com.karumien.cloud.sso.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MessageParameters implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("messageParameter")
	@Valid
	private List<MessageParameter> messageParameter = new ArrayList<>();

}
