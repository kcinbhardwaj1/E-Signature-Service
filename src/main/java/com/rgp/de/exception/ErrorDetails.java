package com.rgp.de.exception;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("error")
	private String error;

	@JsonProperty("message")
	private String message;

	@JsonProperty("status")
	private int status = 0;

	@JsonProperty("timestamp")
	private String timestamp;

	public ErrorDetails() {

	}

	/**
	 * @param error
	 * @param message
	 * @param status
	 * @param timestamp
	 */
	public ErrorDetails(String error, String message, int status, String timestamp) {
		super();
		this.error = error;
		this.message = message;
		this.status = status;
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "ErrorDetails [error=" + error + ", message=" + message + ", status=" + status + ", timestamp="
				+ timestamp + "]";
	}
}
