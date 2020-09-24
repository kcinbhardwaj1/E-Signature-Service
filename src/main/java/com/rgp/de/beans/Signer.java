package com.rgp.de.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Signer implements Serializable {

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

	@JsonProperty("signedDateTime")
	private String signedDateTime;

	@JsonProperty("status")
	private String status;

	public Signer() {

	}

	/**
	 * @param deliveredDateTime
	 * @param deliveryMethod
	 * @param email
	 * @param name
	 * @param recipientId
	 * @param routingOrder
	 * @param status
	 */
	public Signer(com.docusign.esign.model.Signer envSigner) {

		deliveredDateTime = envSigner.getDeliveredDateTime();
		deliveryMethod = envSigner.getDeliveryMethod();
		recipientId = envSigner.getRecipientId();
		routingOrder = envSigner.getRoutingOrder();
		email = envSigner.getEmail();
		name = envSigner.getName();
		signedDateTime = envSigner.getSignedDateTime();
		status = envSigner.getStatus();
	}

	public Signer(String deliveredDateTime, String email, String name, String status) {
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
	public Signer(String email, String name, String recipientId) {
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

	public String getSignedDateTime() {
		return signedDateTime;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "Signer [name=" + name + ", recipientId=" + recipientId + ", email=" + email + ", deliveredDateTime="
				+ deliveredDateTime + ", deliveryMethod=" + deliveryMethod + ", routingOrder=" + routingOrder
				+ ", status=" + status + "]";
	}
}