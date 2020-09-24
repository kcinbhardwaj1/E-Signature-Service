package com.rgp.de.beans;

import java.io.Serializable;

public class DocumentDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String documnetName;
	private String documnetType;
	private String documentBase64;

	/**
	 * @return the documentBase64
	 */
	public String getDocumentBase64() {
		return documentBase64;
	}

	/**
	 * @param documentBase64 the documentBase64 to set
	 */
	public void setDocumentBase64(String documentBase64) {
		this.documentBase64 = documentBase64;
	}

	/**
	 * @return the documnetName
	 */
	public String getDocumnetName() {
		return documnetName;
	}

	/**
	 * @param documnetName the documnetName to set
	 */
	public void setDocumnetName(String documnetName) {
		this.documnetName = documnetName;
	}

	/**
	 * @return the documnetType
	 */
	public String getDocumnetType() {
		return documnetType;
	}

	/**
	 * @param documnetType the documnetType to set
	 */
	public void setDocumnetType(String documnetType) {
		this.documnetType = documnetType;
	}

	@Override
	public String toString() {
		return "DocumentDTO [documnetName=" + documnetName + ", documnetType=" + documnetType + ", documentBase64="
				+ documentBase64 + "]";
	}

}
