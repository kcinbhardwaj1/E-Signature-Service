package com.rgp.de.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("documentId")
	private String documentId;

	@JsonProperty("envelopeId")
	private String envelopeId;

	@JsonProperty("message")
	private String message;

	@JsonProperty("name")
	private String name;

	/**
	 * @return the documentId
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * @return the envelopeId
	 */
	public String getEnvelopeId() {
		return envelopeId;
	}

	/**
	 * @param envelopeId the envelopeId to set
	 */
	public void setEnvelopeId(String envelopeId) {
		this.envelopeId = envelopeId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DocumentResult [documentId=" + documentId + ", envelopeId=" + envelopeId + ", message=" + message
				+ ", name=" + name + "]";
	}

}
