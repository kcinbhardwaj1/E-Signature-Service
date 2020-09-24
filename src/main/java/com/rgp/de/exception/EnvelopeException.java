package com.rgp.de.exception;

public class EnvelopeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int code;

	private String message;

	public EnvelopeException() {
	}

	public EnvelopeException(String message) {
		super(message);
	}

	/**
	 * @param status
	 * @param timestamp
	 * @param error
	 * @param message
	 * @param path
	 */
	public EnvelopeException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
