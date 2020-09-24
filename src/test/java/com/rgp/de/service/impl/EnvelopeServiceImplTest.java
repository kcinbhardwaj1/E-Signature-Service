package com.rgp.de.service.impl;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.docusign.esign.client.ApiException;
import com.rgp.de.beans.DocumentDTO;
import com.rgp.de.beans.DocumentResult;
import com.rgp.de.beans.EmbedSummary;
import com.rgp.de.beans.Envelope;
import com.rgp.de.beans.EnvelopesInformation;
import com.rgp.de.beans.RemoteSummary;
import com.rgp.de.exception.EnvelopeException;
import com.rgp.de.util.EnvelopeHelper;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class EnvelopeServiceImplTest {

	private Logger logger = LoggerFactory.getLogger(EnvelopeServiceImplTest.class);

	private EnvelopeServiceImpl envelopeServiceImpl = new EnvelopeServiceImpl();

	@Value("${docId}")
	private String docId;

	@Value("${envelopeId}")
	private String envelopeId;

	@Value("${signerName1}")
	private String signerName1;

	@Value("${signerName2}")
	private String signerName2;

	@Value("${signerEmail1}")
	private String signerEmail1;

	@Value("${signerEmail2}")
	private String signerEmail2;

	@Value("${ccName}")
	private String ccName;

	@Value("${ccEmail}")
	private String ccEmail;

	@Value("${emailSubject}")
	private String emailSubject;

	@Value("${documentPath}")
	private String documentPath;

	@Value("${returnUrl}")
	private String returnUrl;

	@Value("${templateId}")
	private String templateId;

	private EnvelopeHelper envelopeHelper;

	public void setup() {
		envelopeHelper = new EnvelopeHelper(signerName1, signerName2, signerEmail1, signerEmail2, ccName, ccEmail,
				emailSubject, documentPath, returnUrl, templateId);
		logger.debug("details: " + envelopeHelper);
	}

	@Test
	public void test_createAndSendEnvelope() throws ApiException, IOException {
		setup();
		logger.info("In test Remote signing starts: ");
		RemoteSummary envelopeDTO = envelopeServiceImpl.createAndSendEnvelope(envelopeHelper.getEnvelopeDefination());
		logger.debug("Remote signing response: " + envelopeDTO);
		Assert.assertNotNull(envelopeDTO);
	}

	@Test
	public void test_createEmbeddedSigningEnvelope() throws ApiException, IOException {
		setup();
		logger.info("In test Embed signing starts: ");
		EmbedSummary actualResult = envelopeServiceImpl
				.embeddedSigning(envelopeHelper.getEnvelopeDataForEmbeddedSigning());
		logger.debug("Embed signing response: " + actualResult);
		Assert.assertNotNull(actualResult.getEmbeds());
	}

	@Test
	public void test_createAndSendEnvelopeByTemplate() throws ApiException, IOException {
		setup();
		logger.info("In test Remote signing using template starts ");
		RemoteSummary actualResponse = envelopeServiceImpl
				.createAndSendEnvelopeByTemplate(envelopeHelper.getEnvelopeDefinationUsingTemplate());
		logger.debug("Remote signing response using template: " + actualResponse);
		Assert.assertNotNull(actualResponse);
	}

	@Test
	public void test_embeddedSigningByTemplate() throws ApiException, IOException {
		setup();
		logger.info("In test Embed signing using template starts: ");
		EmbedSummary actualResult= envelopeServiceImpl
				.embeddedSigningByTemplate(envelopeHelper.getEnvelopeDefinationUsingTemplateForEmbed());
		logger.debug("Embed signing response using template: " + actualResult);
		Assert.assertNotNull(actualResult.getEmbeds());
	}

	@Test
	public void test_fetchAllEnvelopes() throws ApiException, IOException {
		Integer days = 3;
		logger.info("In test Listing envelopes starts: ");
		EnvelopesInformation envelopeInfo = envelopeServiceImpl.fetchAllEnvelopes(days);
		List<Envelope> actualEnvelopeList = envelopeInfo.getEnvelopes();
		logger.debug("List of Envelopes response: " + actualEnvelopeList);
		Assert.assertNotNull(actualEnvelopeList);
	}

	@Test
	public void test_getAllDocumntsByEnvelopeId() throws ApiException, IOException {
		logger.info("In test get all documents from envelope starts: ");
		logger.info("Envelope Id: " + envelopeId);
		List<DocumentDTO> actualAllDocument = envelopeServiceImpl.getAllDocumntsByEnvelopeId(envelopeId);
		logger.debug("Download all document response: " + actualAllDocument);
		Assert.assertNotNull(actualAllDocument);
	}

	@Test
	public void test_getDocumentOfEnvelopeByDocuId() throws ApiException, IOException {
		logger.info("In test get document from envelope starts: ");
		logger.info("Document id:" + docId + "Envelope Id: " + envelopeId);
		DocumentDTO actualDocument = envelopeServiceImpl.getDocumentOfEnvelopeByDocuId(envelopeId, docId);
		logger.debug("Download document response: " + actualDocument);
		Assert.assertNotNull(actualDocument);
	}

	@Test
	public void test_deleteDocumentById() throws ApiException, IOException, EnvelopeException {
		logger.info("In test delete document from envelope starts: ");
		logger.info("Document id:" + docId + " Envelope Id: " + envelopeId);
		DocumentResult actualResult = envelopeServiceImpl.deleteDocumentById(envelopeId, docId);
		logger.debug("Delete document response: " + actualResult);
		Assert.assertNotNull(actualResult);
	}

}
