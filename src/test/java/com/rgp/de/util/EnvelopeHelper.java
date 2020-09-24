package com.rgp.de.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.docusign.esign.model.CarbonCopy;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.RecipientViewRequest;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;
import com.rgp.de.beans.EnvelopeDefination;
import com.sun.jersey.core.util.Base64;

@Component
public class EnvelopeHelper {

	private String signerName1;
	private String signerName2;
	private String signerEmail1;
	private String signerEmail2;
	private String ccName;
	private String ccEmail;
	private String emailSubject;
	private String documentPath;
	private String returnUrl;
	private String templateId;
	
	/**
	 * @param signerName1
	 * @param signerName2
	 * @param signerEmail1
	 * @param signerEmail2
	 * @param ccName
	 * @param ccEmail
	 * @param emailSubject
	 * @param documentPath
	 * @param returnUrl
	 */
	public EnvelopeHelper(String signerName1, String signerName2, String signerEmail1, String signerEmail2,
			String ccName, String ccEmail, String emailSubject, String documentPath, String returnUrl,String templateId) {
		super();
		this.signerName1 = signerName1;
		this.signerName2 = signerName2;
		this.signerEmail1 = signerEmail1;
		this.signerEmail2 = signerEmail2;
		this.ccName = ccName;
		this.ccEmail = ccEmail;
		this.emailSubject = emailSubject;
		this.documentPath = documentPath;
		this.returnUrl = returnUrl;
		this.templateId = templateId;
	}

	public EnvelopeDefination getEnvelopeDefination() throws IOException {
		EnvelopeDefination envelopeData = new EnvelopeDefination();
		envelopeData.setDocuments(getDocumnets());
		envelopeData.setEmailSubject(emailSubject);
		envelopeData.setStatus("sent");
		envelopeData.setRecipients(getRecipient());
		return envelopeData;

	}
	
	public  EnvelopeDefination getEnvelopeDataForEmbeddedSigning() throws IOException {
		EnvelopeDefination envelopeData = new EnvelopeDefination();
		envelopeData.setDocuments(getDocumnets());
		envelopeData.setEmailSubject(emailSubject);
		envelopeData.setStatus("sent");
		envelopeData.setRecipients(getEmbedRecipients());
		envelopeData.setRecipientViewRequest(getViewRecipientRequest());
		return envelopeData;
	}
	
	public EnvelopeDefination getEnvelopeDefinationUsingTemplate() throws IOException {
		EnvelopeDefination envelopeData = new EnvelopeDefination();
		envelopeData.setEmailSubject(emailSubject);
		envelopeData.setStatus("sent");
		envelopeData.setRecipients(getRecipient());
		envelopeData.setTemplateId(templateId);
		return envelopeData;
	}
	
	public EnvelopeDefination getEnvelopeDefinationUsingTemplateForEmbed() throws IOException {
		EnvelopeDefination envelopeData = new EnvelopeDefination();
		envelopeData.setEmailSubject(emailSubject);
		envelopeData.setStatus("sent");
		envelopeData.setRecipients(getEmbedRecipients());
		envelopeData.setRecipientViewRequest(getViewRecipientRequest());
		envelopeData.setTemplateId(templateId);
		return envelopeData;
	}
	
	private Recipients getEmbedRecipients() {
		Recipients recipient=new Recipients();
		
		List<Signer> signerList = new ArrayList<Signer>();
		Signer signer1 = new Signer();
		signer1.setEmail(signerEmail1);
		signer1.setName(signerName1);
		signer1.setRoutingOrder("0");
		
		Signer signer2 = new Signer();
		signer2.setEmail(signerEmail2);
		signer2.setName(signerName2);
		signer2.setRoutingOrder("0");
		
		signerList.add(signer1);
		signerList.add(signer2);

		Tabs tabs = getTabs();
		for (Signer signerData : signerList) {
			signerData.setTabs(tabs);
		}
		recipient.setSigners(signerList);
		
		return recipient;
	}

	private  RecipientViewRequest getViewRecipientRequest() {
		RecipientViewRequest viewRequest = new RecipientViewRequest();
		// Set the url where you want the recipient to go once they are done signing
		// should typically be a callback route somewhere in your app.
		viewRequest.setReturnUrl(returnUrl);
		viewRequest.setAuthenticationMethod("None");
		return viewRequest;

	}

	private  Recipients getRecipient(){
		Recipients recipients = new Recipients();
		recipients.setSigners(getSigners());
		recipients.setCarbonCopies(getCarbonCopy());
		return recipients;
	}

	private  List<Document> getDocumnets() throws IOException {

		List<Document> documentList = new ArrayList<Document>();
		Document document1 = new Document();
		File file = new File(documentPath);
		FileInputStream input = new FileInputStream(file);
		MultipartFile multipartFile = new MockMultipartFile("file",
		            file.getName(), "text/plain", IOUtils.toByteArray(input));
		document1.setDocumentBase64(new String(Base64.encode(multipartFile.getBytes())));
		document1.setName(multipartFile.getOriginalFilename().substring(0,
				multipartFile.getOriginalFilename().lastIndexOf(".")));
		document1.setFileExtension(multipartFile.getOriginalFilename().substring(
				multipartFile.getOriginalFilename().lastIndexOf(".") + 1,
				multipartFile.getOriginalFilename().length()));
		document1.setDocumentId("1");
	
		documentList.add(document1);
		return documentList;

	}

	private  List<Signer> getSigners(){

		List<Signer> signerList = new ArrayList<Signer>();

		Signer signer1 = new Signer();
		signer1.setEmail(signerEmail2);
		signer1.setName(signerName2);
		signer1.setRoutingOrder("2");
		signerList.add(signer1);
		
		Signer signer2 = new Signer();
		signer2.setEmail(signerEmail1);
		signer2.setName(signerName1);
		signer2.setRoutingOrder("1");
		signerList.add(signer2);

		Tabs tabs = getTabs();
		for (Signer signerData : signerList) {

			signerData.setTabs(tabs);
		}
		return signerList;
	}

	// create a cc recipient to receive a copy of the documents, identified by name and email
	// We're setting the parameters via setters
	private  List<CarbonCopy> getCarbonCopy(){
		List<CarbonCopy> carbonCopyList = new ArrayList<CarbonCopy>();

		CarbonCopy carbonCopy = new CarbonCopy();
		carbonCopy.setEmail(ccEmail);
		carbonCopy.setName(ccName);
		carbonCopy.setRoutingOrder("3");
		carbonCopyList.add(carbonCopy);
		return carbonCopyList;
	}

	private  Tabs getTabs() {
		SignHere signHere1 = new SignHere();
		signHere1.setAnchorString("**signature_1**");
		signHere1.setAnchorUnits("pixels");
		signHere1.setAnchorXOffset("20");
		signHere1.anchorYOffset("10");
		signHere1.setDocumentId("1");
		signHere1.setPageNumber("1");

		SignHere signHere2 = new SignHere();
		signHere2.setAnchorString("/sn1/");
		signHere2.setAnchorUnits("pixels");
		signHere2.setAnchorXOffset("20");
		signHere2.anchorYOffset("10");
		signHere2.setDocumentId("1");
		signHere2.setPageNumber("1");

		// Tabs are set per recipient / signer
		Tabs tabs = new Tabs();
		tabs.setSignHereTabs(Arrays.asList(signHere1, signHere2));
		return tabs;

	}
	
	@Override
	public String toString() {
		return "EnvelopeHelper [signerName1=" + signerName1 + ", signerName2=" + signerName2 + ", signerEmail1="
				+ signerEmail1 + ", signerEmail2=" + signerEmail2 + ", ccName=" + ccName + ", ccEmail=" + ccEmail
				+ ", emailSubject=" + emailSubject + ", documentPath=" + documentPath + ", returnUrl=" + returnUrl
				+ "]";
	}
}
