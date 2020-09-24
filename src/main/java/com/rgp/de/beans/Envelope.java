package com.rgp.de.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Envelope implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("createdDateTime")
	private String createdDateTime;

	@JsonProperty("envelopeDocuments")
	private List<Document> envelopeDocuments = new ArrayList<Document>();

	@JsonProperty("envelopeId")
	private String envelopeId = null;

	@JsonProperty("emailSubject")
	private String emailSubject = null;

	@JsonProperty("lastModifiedDateTime")
	private String lastModifiedDateTime;

	@JsonProperty("recipients")
	private Recipients recipients = null;

	@JsonProperty("sentDateTime")
	private String sentDateTime;

	@JsonProperty("status")
	private String status;

	@JsonProperty("statusChangedDateTime")
	private String statusChangedDateTime;
	
	
	public Envelope() {
		
	}
	
	/**
	 * @param createdDateTime
	 * @param envelopeDocuments
	 * @param envelopeId
	 * @param emailSubject
	 * @param lastModifiedDateTime
	 * @param recipients
	 * @param sentDateTime
	 * @param status
	 * @param statusChangedDateTime
	 */
	public Envelope(String createdDateTime, List<Document> envelopeDocuments, String envelopeId, String emailSubject,
			String lastModifiedDateTime, Recipients recipients, String sentDateTime, String status,
			String statusChangedDateTime) {
		this.createdDateTime = createdDateTime;
		this.envelopeDocuments = envelopeDocuments;
		this.envelopeId = envelopeId;
		this.emailSubject = emailSubject;
		this.lastModifiedDateTime = lastModifiedDateTime;
		this.recipients = recipients;
		this.sentDateTime = sentDateTime;
		this.status = status;
		this.statusChangedDateTime = statusChangedDateTime;
	}



	/**
	 * @return the emailSubject
	 */
	public String getEmailSubject() {
		return emailSubject;
	}

	/**
	 * @param emailSubject the emailSubject to set
	 */
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	/**
	 * @return the recipients
	 */
	public Recipients getRecipients() {
		return recipients;
	}

	/**
	 * @param recipients the recipients to set
	 */
	public void setRecipients(Recipients recipients) {
		this.recipients = recipients;
	}

	/**
	 * @return the envelopeDocuments
	 */
	public List<Document> getEnvelopeDocuments() {
		return envelopeDocuments;
	}

	/**
	 * @param envelopeDocuments the envelopeDocuments to set
	 */
	public void setEnvelopeDocuments(List<Document> envelopeDocuments) {
		this.envelopeDocuments = envelopeDocuments;
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the createdDateTime
	 */
	public String getCreatedDateTime() {
		return createdDateTime;
	}

	/**
	 * @param createdDateTime the createdDateTime to set
	 */
	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	/**
	 * @return the lastModifiedDateTime
	 */
	public String getLastModifiedDateTime() {
		return lastModifiedDateTime;
	}

	/**
	 * @param lastModifiedDateTime the lastModifiedDateTime to set
	 */
	public void setLastModifiedDateTime(String lastModifiedDateTime) {
		this.lastModifiedDateTime = lastModifiedDateTime;
	}

	/**
	 * @return the sentDateTime
	 */
	public String getSentDateTime() {
		return sentDateTime;
	}

	/**
	 * @param sentDateTime the sentDateTime to set
	 */
	public void setSentDateTime(String sentDateTime) {
		this.sentDateTime = sentDateTime;
	}

	/**
	 * @return the statusChangedDateTime
	 */
	public String getStatusChangedDateTime() {
		return statusChangedDateTime;
	}

	/**
	 * @param statusChangedDateTime the statusChangedDateTime to set
	 */
	public void setStatusChangedDateTime(String statusChangedDateTime) {
		this.statusChangedDateTime = statusChangedDateTime;
	}

	@Override
	public String toString() {
		return "Envelope [createdDateTime=" + createdDateTime + ", envelopeDocuments=" + envelopeDocuments
				+ ", envelopeId=" + envelopeId + ", emailSubject=" + emailSubject + ", lastModifiedDateTime="
				+ lastModifiedDateTime + ", recipients=" + recipients + ", sentDateTime=" + sentDateTime + ", status="
				+ status + ", statusChangedDateTime=" + statusChangedDateTime + "]";
	}

}
