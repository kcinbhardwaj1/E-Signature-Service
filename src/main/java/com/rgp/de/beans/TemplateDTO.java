package com.rgp.de.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemplateDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("templateId")
	private String templateId = null;

	@JsonProperty("name")
	private String name = null;

	@JsonProperty("documents")
	private List<Document> documents;

	@JsonProperty("recipients")
	private Recipients recipients;

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

	/**
	 * @return the documentInfo
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * @param documentInfo the documentInfo to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
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

	@Override
	public String toString() {
		return "Template [templateId=" + templateId + ", name=" + name + ", documents=" + documents + ", recipients="
				+ recipients + "]";
	}

}
