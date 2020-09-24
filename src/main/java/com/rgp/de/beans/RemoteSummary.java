package com.rgp.de.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoteSummary implements Serializable{
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("envelopeId")
	private String envelopeId = null;

	@JsonProperty("status")
	private String status = null;

	@JsonProperty("statusDateTime") 
	private String statusDateTime = null;

	@JsonProperty("uri") 
	private String uri = null;


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
	 * @return the statusDateTime
	 */
	public String getStatusDateTime() {
		return statusDateTime;
	}


	/**
	 * @param statusDateTime the statusDateTime to set
	 */
	public void setStatusDateTime(String statusDateTime) {
		this.statusDateTime = statusDateTime;
	}


	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}


	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}


	@Override
	public String toString() {
		return "RemoteSummary [envelopeId=" + envelopeId + ", status=" + status + ", statusDateTime=" + statusDateTime
				+ ", uri=" + uri + "]";
	}
		  
}
