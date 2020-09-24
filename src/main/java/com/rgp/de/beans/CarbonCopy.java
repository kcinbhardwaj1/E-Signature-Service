package com.rgp.de.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CarbonCopy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("deliveredDateTime")
	private String deliveredDateTime;

	@JsonProperty("deliveryMethod")
	private String deliveryMethod;

	@JsonProperty("email")
	private String email;

	@JsonProperty("name")
	private String name;

	@JsonProperty("recipientId")
	private String recipientId;

	@JsonProperty("routingOrder")
	private String routingOrder;

	@JsonProperty("status")
	private String status;

	public CarbonCopy() {

	}

	/**
	 * @param name
	 * @param recipientId
	 * @param email
	 * @param deliveredDateTime
	 * @param deliveryMethod
	 * @param routingOrder
	 * @param status
	 */
	public CarbonCopy(com.docusign.esign.model.CarbonCopy envCc) {
		deliveredDateTime = envCc.getDeliveredDateTime();
		deliveryMethod = envCc.getDeliveryMethod();
		recipientId = envCc.getRecipientId();
		routingOrder = envCc.getRoutingOrder();
		email = envCc.getEmail();
		name = envCc.getName();
		status = envCc.getStatus();
	}

	public CarbonCopy(String deliveredDateTime, String email, String name, String status) {
		super();
		this.email = email;
		this.deliveredDateTime = deliveredDateTime;
		this.status = status;
		this.name = name;
	}

	/**
	 * @param email
	 * @param name
	 * @param recipientId
	 */
	public CarbonCopy(String email, String name, String recipientId) {
		this.email = email;
		this.name = name;
		this.recipientId = recipientId;
	}

	public String getDeliveredDateTime() {
		return deliveredDateTime;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getRecipientId() {
		return recipientId;
	}

	public String getRoutingOrder() {
		return routingOrder;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "CarbonCopy [name=" + name + ", recipientId=" + recipientId + ", email=" + email + ", deliveredDateTime="
				+ deliveredDateTime + ", deliveryMethod=" + deliveryMethod + ", routingOrder=" + routingOrder
				+ ", status=" + status + "]";
	}

}
