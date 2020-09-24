package com.rgp.de.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnvelopesInformation {

	@JsonProperty("envelopes")
	private List<Envelope> envelopes;

	/**
	 * @return the envelopes
	 */
	public List<Envelope> getEnvelopes() {
		return envelopes;
	}

	/**
	 * @param envelopes the envelopes to set
	 */
	public void setEnvelopes(List<Envelope> envelopes) {
		this.envelopes = envelopes;
	}
	
}
