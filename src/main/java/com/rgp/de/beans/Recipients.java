package com.rgp.de.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Recipients implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("signers")
	private List<Signer> signers;

	@JsonProperty("carbonCopy")
	private List<CarbonCopy> carbonCopy;

	/**
	 * @return the signers
	 */
	public List<Signer> getSigners() {
		return signers;
	}

	/**
	 * @param signers the signers to set
	 */
	public void setSigners(List<Signer> signers) {
		this.signers = signers;
	}

	/**
	 * @return the cc
	 */
	public List<CarbonCopy> getCarbonCopy() {
		return carbonCopy;
	}

	/**
	 * @param cc the cc to set
	 */
	public void setCarbonCopy(List<CarbonCopy> carbonCopy) {
		this.carbonCopy = carbonCopy;
	}

	@Override
	public String toString() {
		return "Recipients [signers=" + signers + ", carbonCopy=" + carbonCopy + "]";
	}

}
