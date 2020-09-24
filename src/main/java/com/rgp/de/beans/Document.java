package com.rgp.de.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Document implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@JsonProperty("documentId")
	private String documentId = null;

	@JsonProperty("name")
	private String name = null;

	@JsonProperty("type")
	private String type = null;

	public Document() {

	}

	/**
	 * @param documentId
	 * @param documentName
	 * @param documentType
	 */
	public Document(com.docusign.esign.model.EnvelopeDocument envelopeDocument) {
		documentId = envelopeDocument.getDocumentId();
		name = envelopeDocument.getName();
		type = envelopeDocument.getType();
	}
	
	

	/**
	 * @param documentId
	 * @param documentName
	 * @param documentType
	 */
	public Document(String documentId, String documentName) {
		this.documentId = documentId;
		this.name = documentName;
	}

	@Override
	public String toString() {
		return "Document [documentId=" + documentId + ", name=" + name + ", type="
				+ type + "]";
	}

}
