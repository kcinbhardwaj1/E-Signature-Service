package com.rgp.de.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmbedSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("embeds")
	private List<Embed> embeds;

	@JsonProperty("envelopeId")
	private String envelopeId;

	/**
	 * @param embeds
	 * @param envelopeId
	 */
	public EmbedSummary(List<Embed> embeds, String envelopeId) {
		super();
		this.embeds = embeds;
		this.envelopeId = envelopeId;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the embeds
	 */
	public List<Embed> getEmbeds() {
		return embeds;
	}

	/**
	 * @return the envelopeId
	 */
	public String getEnvelopeId() {
		return envelopeId;
	}

	@Override
	public String toString() {
		return "EmbedSummary [embeds=" + embeds + ", envelopeId=" + envelopeId + "]";
	}

}
