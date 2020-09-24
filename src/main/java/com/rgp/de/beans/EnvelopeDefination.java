package com.rgp.de.beans;

import java.util.ArrayList;
import java.util.List;

import com.docusign.esign.model.Document;
import com.docusign.esign.model.RecipientViewRequest;
import com.docusign.esign.model.Recipients;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnvelopeDefination {

	@JsonProperty("emailSubject")
	private String emailSubject = null;
	
	@JsonProperty("recipients")
	private Recipients recipients = null;
	
	@JsonProperty("documents")
	private List<Document> documents = new ArrayList<Document>();
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("recipientViewRequest")
	private RecipientViewRequest recipientViewRequest;
	
	@JsonProperty("templateId")
	private String templateId;
	

	/**
	 * @return the templateId
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the recipientViewRequest
	 */
	public RecipientViewRequest getRecipientViewRequest() {
		return recipientViewRequest;
	}

	/**
	 * @param recipientViewRequest the recipientViewRequest to set
	 */
	public void setRecipientViewRequest(RecipientViewRequest recipientViewRequest) {
		this.recipientViewRequest = recipientViewRequest;
	}

	/**
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	/**
	 * @return the emailSubject
	 */
	public String getEmailSubject() {
		return emailSubject;
	}

	/**
	 * @return the recipients
	 */
	public Recipients getRecipients() {
		return recipients;
	}


	/**
	 * @param emailSubject the emailSubject to set
	 */
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	/**
	 * @param recipients the recipients to set
	 */
	public void setRecipients(Recipients recipients) {
		this.recipients = recipients;
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

	

}
