package com.rgp.de.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Embed implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("signerName")
	private String signerName;

	@JsonProperty("viewUrl")
	private String viewUrl;

	/**
	 * @param signerName the signerName to set
	 */
	public void setSignerName(String signerName) {
		this.signerName = signerName;
	}

	/**
	 * @param viewUrl the viewUrl to set
	 */
	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

	/**
	 * @return the signerName
	 */
	public String getSignerName() {
		return signerName;
	}

	/**
	 * @return the viewUrl
	 */
	public String getViewUrl() {
		return viewUrl;
	}

	@Override
	public String toString() {
		return "Embed [signerName=" + signerName + ", viewUrl=" + viewUrl + "]";
	}

}
