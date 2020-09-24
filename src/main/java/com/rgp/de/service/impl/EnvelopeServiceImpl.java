package com.rgp.de.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.api.EnvelopesApi.ListStatusChangesOptions;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.EnvelopeDocument;
import com.docusign.esign.model.EnvelopeDocumentsResult;
import com.docusign.esign.model.EnvelopeEvent;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.EventNotification;
import com.docusign.esign.model.RecipientEvent;
import com.docusign.esign.model.RecipientViewRequest;
import com.docusign.esign.model.TemplateRole;
import com.docusign.esign.model.ViewUrl;
import com.rgp.de.beans.CarbonCopy;
import com.rgp.de.beans.Document;
import com.rgp.de.beans.DocumentDTO;
import com.rgp.de.beans.DocumentResult;
import com.rgp.de.beans.Embed;
import com.rgp.de.beans.EmbedSummary;
import com.rgp.de.beans.Envelope;
import com.rgp.de.beans.EnvelopeDefination;
import com.rgp.de.beans.EnvelopeQueryStrings;
import com.rgp.de.beans.EnvelopesInformation;
import com.rgp.de.beans.Recipients;
import com.rgp.de.beans.RemoteSummary;
import com.rgp.de.beans.Signer;
import com.rgp.de.constants.Roles;
import com.rgp.de.exception.EnvelopeException;
import com.rgp.de.service.EnvelopeService;
import com.rgp.de.util.EnvelopeUtils;
import com.rgp.de.util.TokenBase;
import com.sun.jersey.core.util.Base64;

@Service
public class EnvelopeServiceImpl extends BaseService implements EnvelopeService {

	private Logger logger = LoggerFactory.getLogger(EnvelopeServiceImpl.class);

	protected static final String DATE_FORMAT = "yyyy/MM/dd";

	private Map<String, Envelope> envelopeRepo = new HashMap<String, Envelope>();

	@Autowired
	private Environment env;

	/**
	 *
	 */
	/*
	 * Uncomment this code to enable caching
	 * 
	 * @Caching(evict = { @CacheEvict(value = "Envelope", allEntries = true),
	 * 
	 * @CacheEvict(value = "EnvelopesInformation", allEntries = true),
	 * 
	 * @CacheEvict(value = "DocumentDTO", allEntries = true) })
	 */
	@Override
	public RemoteSummary createAndSendEnvelopeByTemplate(EnvelopeDefination envelope) throws ApiException, IOException {

		logger.info("Remote Signing By Template Starts:");
		com.docusign.esign.model.EnvelopeDefinition envelopeDefinition = new com.docusign.esign.model.EnvelopeDefinition();

		// 1. set envelope definition
		envelopeDefinition.setEmailSubject(envelope.getEmailSubject());

		// 2. set template role(recipients)
		List<TemplateRole> templateRoleList = new ArrayList<TemplateRole>();
		// create a cc recipient to receive a copy of the documents, identified by name
		// and email We're setting the parameters via setters
		if (null != envelope.getRecipients().getCarbonCopies()
				&& !envelope.getRecipients().getCarbonCopies().isEmpty()) {
			for (com.docusign.esign.model.CarbonCopy ccData : envelope.getRecipients().getCarbonCopies()) {
				TemplateRole cc = new TemplateRole();
				cc.setEmail(ccData.getEmail());
				cc.setName(ccData.getName());
				cc.setRoleName(Roles.CC_ROLE_NAME);
				// routingOrder (lower means earlier) determines the order of deliveries to the
				// recipients. Parallel routing order is supported by using the same integer as
				// the order
				// for two or more recipients.
				cc.setRoutingOrder(ccData.getRoutingOrder());
				templateRoleList.add(cc);
			}
		}

		if (null != envelope.getRecipients().getSigners() && !envelope.getRecipients().getSigners().isEmpty()) {
			templateRoleList = getTemplateSignerData(envelope.getRecipients().getSigners(), false);
		}
		envelopeDefinition.setTemplateRoles(templateRoleList);

		// 3. set template id
		envelopeDefinition.setTemplateId(envelope.getTemplateId());

		// 4. set envelope status
		// Request that the envelope be sent by setting |status| to "sent". To request
		// that the envelope be created as a draft, set to "created"
		envelopeDefinition.setStatus(envelope.getStatus());

		// 5. webhook notifications
		envelopeDefinition.setEventNotification(getEventNotification());

		// 6.call docusign Api
		EnvelopeSummary envelopeSummary = callCreateEnvelopeApi(envelopeDefinition);

		RemoteSummary remoteResponse = new RemoteSummary();
		remoteResponse.setEnvelopeId(envelopeSummary.getEnvelopeId());
		remoteResponse.setStatus(envelopeSummary.getStatus());
		remoteResponse.setStatusDateTime(envelopeSummary.getStatusDateTime());
		remoteResponse.setUri(envelopeSummary.getUri());

		logger.info("Remote Signing By Template Summary: " + remoteResponse);

		return remoteResponse;
	}

	/**
	 *
	 */
	/*
	 * Uncomment this code to enable caching
	 * 
	 * @Caching(evict = { @CacheEvict(value = "Envelope", allEntries = true),
	 * 
	 * @CacheEvict(value = "EnvelopesInformation", allEntries = true),
	 * 
	 * @CacheEvict(value = "DocumentDTO", allEntries = true) })
	 */
	@Override
	public RemoteSummary createAndSendEnvelope(EnvelopeDefination envelope) throws ApiException, IOException {

		logger.info("Remote Signing Starts:");

		com.docusign.esign.model.EnvelopeDefinition envelopeDefinition = new com.docusign.esign.model.EnvelopeDefinition();

		// 1.set email subject
		envelopeDefinition.setEmailSubject(envelope.getEmailSubject());

		// 2.set documents
		List<com.docusign.esign.model.Document> documentList = getDocumentsData(envelope.getDocuments());
		envelopeDefinition.setDocuments(documentList);

		// 3. Add the recipients to the envelope object
		com.docusign.esign.model.Recipients recipients = new com.docusign.esign.model.Recipients();

		// create a cc recipient to receive a copy of the documents, identified by name
		List<com.docusign.esign.model.CarbonCopy> carbonCopyList = new ArrayList<com.docusign.esign.model.CarbonCopy>();
		for (com.docusign.esign.model.CarbonCopy signerData : envelope.getRecipients().getCarbonCopies()) {
			com.docusign.esign.model.CarbonCopy carbonCopy = new com.docusign.esign.model.CarbonCopy();
			carbonCopy.setEmail(signerData.getEmail());
			carbonCopy.setName(signerData.getName());
			carbonCopy.setRecipientId(EnvelopeUtils.getRecipientId().toString());
			// routingOrder (lower means earlier) determines the order of deliveries to the
			// recipients.
			// Parallel routing order is supported by using the same integer as the order
			// for two or more recipients.
			carbonCopy.setRoutingOrder(signerData.getRoutingOrder());
			carbonCopyList.add(carbonCopy);
		}
		List<com.docusign.esign.model.Signer> signerList = getSignersData(envelope.getRecipients().getSigners(), false);
		recipients.setSigners(signerList);
		recipients.setCarbonCopies(carbonCopyList);
		envelopeDefinition.setRecipients(recipients);

		// 4. set envelope status
		// Request that the envelope be sent by setting |status| to "sent".To request
		// that the envelope be created as a draft, set to "created"
		envelopeDefinition.setStatus(envelope.getStatus());

		// 5. webhook notifications
		envelopeDefinition.setEventNotification(getEventNotification());

		// 6. call docusign Api
		EnvelopeSummary envelopeSummary = callCreateEnvelopeApi(envelopeDefinition);

		RemoteSummary remoteSummary = new RemoteSummary();
		remoteSummary.setEnvelopeId(envelopeSummary.getEnvelopeId());
		remoteSummary.setStatus(envelopeSummary.getStatus());
		remoteSummary.setStatusDateTime(envelopeSummary.getStatusDateTime());
		remoteSummary.setUri(envelopeSummary.getUri());

		logger.info("Remote Signing Summary: " + remoteSummary);

		return remoteSummary;
	}

	/**
	 *
	 */
	/*
	 * Uncomment this code to enable caching
	 * 
	 * @Caching(evict = { @CacheEvict(value = "Envelope", allEntries = true),
	 * 
	 * @CacheEvict(value = "EnvelopesInformation", allEntries = true),
	 * 
	 * @CacheEvict(value = "DocumentDTO", allEntries = true) })
	 */
	@Override
	public EmbedSummary embeddedSigning(EnvelopeDefination envelope) throws IOException, ApiException {

		logger.info("Embedded signing starts:");

		com.docusign.esign.model.EnvelopeDefinition envelopeDefinition = new com.docusign.esign.model.EnvelopeDefinition();

		// 1. set email subject
		envelopeDefinition.setEmailSubject(envelope.getEmailSubject());

		// 2.set documents
		List<com.docusign.esign.model.Document> documentList = getDocumentsData(envelope.getDocuments());
		envelopeDefinition.setDocuments(documentList);

		// 3. Add the recipient to the envelope object
		com.docusign.esign.model.Recipients recipients = new com.docusign.esign.model.Recipients();
		List<com.docusign.esign.model.Signer> signerList = getSignersData(envelope.getRecipients().getSigners(), true);
		recipients.setSigners(signerList);
		envelopeDefinition.setRecipients(recipients);

		// 4. set envelope status
		envelopeDefinition.setStatus(envelope.getStatus());

		// 5. webhook notifications
		envelopeDefinition.setEventNotification(getEventNotification());

		// 6. call docusign Api
		EnvelopeSummary envelopeSummary = callCreateEnvelopeApi(envelopeDefinition);

		String envelopeId = envelopeSummary.getEnvelopeId();

		List<Embed> embedList = new ArrayList<Embed>();

		// 7. create Request and call a Recipient View URL (the Signing Ceremony URL)
		for (com.docusign.esign.model.Signer signerData : signerList) {
			RecipientViewRequest recipientViewRequest = getRecipientViewRequest(signerData,
					envelope.getRecipientViewRequest());
			Embed embed = callRecipientViewApi(envelopeId, recipientViewRequest);
			embedList.add(embed);
			logger.info("Signer Name: " + signerData.getName() + " Embedded URL: " + embed.getViewUrl());
		}

		EmbedSummary embedSummary = new EmbedSummary(embedList, envelopeId);
		return embedSummary;
	}

	/**
	 *
	 */
	/*
	 * Uncomment this code to enable caching
	 * 
	 * @Caching(evict = { @CacheEvict(value = "Envelope", allEntries = true),
	 * 
	 * @CacheEvict(value = "EnvelopesInformation", allEntries = true),
	 * 
	 * @CacheEvict(value = "DocumentDTO", allEntries = true) })
	 */
	@Override
	public EmbedSummary embeddedSigningByTemplate(EnvelopeDefination envelope) throws IOException, ApiException {

		logger.info("Embedded signing starts:");
		com.docusign.esign.model.EnvelopeDefinition envelopeDefinition = new com.docusign.esign.model.EnvelopeDefinition();

		// 1.set email subject
		envelopeDefinition.setEmailSubject(envelope.getEmailSubject());

		// 2. set Template Role(recipients)
		List<TemplateRole> templateRoleList = new ArrayList<TemplateRole>();
		if (null != envelope.getRecipients().getSigners() && !envelope.getRecipients().getSigners().isEmpty()) {
			templateRoleList = getTemplateSignerData(envelope.getRecipients().getSigners(), true);
		}
		envelopeDefinition.setTemplateRoles(templateRoleList);

		// 3.set template id
		envelopeDefinition.setTemplateId(envelope.getTemplateId());

		// 4.set envelope status
		// Request that the envelope be sent by setting |status| to "sent".To request
		// that the envelope be created as a draft, set to "created"
		envelopeDefinition.setStatus(envelope.getStatus());

		// 5. webhook notifications
		envelopeDefinition.setEventNotification(getEventNotification());

		// 6. call docusign Api, create envelope and get envelopeId
		EnvelopeSummary envelopeSummary = callCreateEnvelopeApi(envelopeDefinition);
		String envelopeId = envelopeSummary.getEnvelopeId();

		List<Embed> embedList = new ArrayList<Embed>();

		// 7. create Request and call Recipient View URL (the Signing Ceremony URL)
		for (TemplateRole templateRole : templateRoleList) {
			RecipientViewRequest recipientViewRequest = getRecipientViewRequestForTemplateRole(templateRole,
					envelope.getRecipientViewRequest());
			Embed embed = callRecipientViewApi(envelopeId, recipientViewRequest);
			embedList.add(embed);
			logger.info("Signer Name: " + templateRole.getName() + " Embedded URL: " + embed.getViewUrl());
		}

		EmbedSummary embedSummary = new EmbedSummary(embedList, envelopeId);
		return embedSummary;
	}

	/**
	 *
	 */
	/*
	 * Uncomment this code to enable caching
	 * 
	 * @Cacheable(cacheNames = "EnvelopesInformation")
	 */
	@Override
	public EnvelopesInformation fetchAllEnvelopes(Integer days) throws ApiException, IOException {
		logger.info("Fetching all envelopes:");

		ListStatusChangesOptions options = getEnvelopeApi().new ListStatusChangesOptions();
		LocalDate fromDate;
		if (null == days || days == 0) {
			fromDate = LocalDate.now().minusDays(30);
		} else {
			fromDate = LocalDate.now().minusDays(days);
		}

		options.setFromDate(fromDate.toString(DATE_FORMAT));
		logger.info("From Date: " + fromDate);

		/*
		 * (Optional) The order in which to sort the results. Valid values are: asc:
		 * Ascending order. desc: Descending order.
		 */
		options.setOrder("desc");

		/*
		 * The field used to sort the results. Example: Created
		 */
		options.setOrderBy("created");

		logger.info("Envelope Options:" + options);

		com.docusign.esign.model.EnvelopesInformation envelopesInformation = getEnvelopeApi()
				.listStatusChanges(TokenBase.getAccountId(), options);

		List<Envelope> envelopeList = new ArrayList<Envelope>();
		for (com.docusign.esign.model.Envelope env : envelopesInformation.getEnvelopes()) {
			Envelope envelope = new Envelope();
			Recipients recipients = getEnvelopeRecipients(env);
			List<Document> documentList = getEnvelopeDocuments(env);
			envelope.setCreatedDateTime(env.getCreatedDateTime());
			envelope.setEnvelopeDocuments(documentList);
			envelope.setEmailSubject(env.getEmailSubject());
			envelope.setLastModifiedDateTime(env.getLastModifiedDateTime());
			envelope.setRecipients(recipients);
			envelope.setSentDateTime(env.getSentDateTime());
			envelope.setStatus(env.getStatus());
			envelope.setStatusChangedDateTime(env.getStatusChangedDateTime());
			envelope.setEnvelopeId(env.getEnvelopeId());
			envelopeList.add(envelope);
		}

		EnvelopesInformation envelopesInfo = new EnvelopesInformation();
		envelopesInfo.setEnvelopes(envelopeList);
		logger.debug("Envelope List: " + envelopeList);
		return envelopesInfo;

	}

	/**
	 *
	 */
	/*
	 * Uncomment this code to enable caching
	 * 
	 * @Cacheable(cacheNames = "DocumentDTO")
	 */
	@Override
	public List<DocumentDTO> getAllDocumntsByEnvelopeId(String envelopeId) throws ApiException, IOException {
		logger.info("Getting document list from envelope:");

		logger.info("Envelope Id: " + envelopeId);

		// 1.verify the required parameter 'envelopeId' is set
		if (envelopeId == null || envelopeId.isEmpty()) {
			throw new EnvelopeException(400, "Missing the required parameter 'envelopeId' when calling listDocuments");
		}

		// 2.fetch list of documents
		List<EnvelopeDocument> documentList = new ArrayList<EnvelopeDocument>();
		EnvelopeDocumentsResult envelopeDocumentsResult = getEnvelopeApi().listDocuments(TokenBase.getAccountId(),
				envelopeId);
		documentList = envelopeDocumentsResult.getEnvelopeDocuments();

		// 3.get physical copy of documents
		List<DocumentDTO> documentDtoList = new ArrayList<DocumentDTO>();
		for (EnvelopeDocument envelopeDocument : documentList) {
			byte[] result = callGetDocumentApi(envelopeId, envelopeDocument.getDocumentId());
			DocumentDTO documentDTO = new DocumentDTO();
			documentDTO.setDocumnetName(envelopeDocument.getName());
			documentDTO.setDocumnetType(envelopeDocument.getType());
			documentDTO.setDocumentBase64(new String(Base64.encode(result)));
			documentDtoList.add(documentDTO);
		}

		logger.debug("Document List: " + documentDtoList);

		return documentDtoList;
	}

	/**
	 *
	 */
	@Override
	public DocumentDTO getDocumentOfEnvelopeByDocuId(String envelopeId, String docId) throws IOException, ApiException {

		logger.info("Getting document:");

		logger.info("In Download document Envelope ID => " + envelopeId + "  Document ID => " + docId);

		// 1.verify the required parameter 'envelopeId' is set
		if (envelopeId == null || envelopeId.isEmpty()) {
			throw new EnvelopeException(400, "Missing the required parameter 'envelopeId' when calling getDocument");
		}

		// 2.verify the required parameter 'docId' is set
		if (docId == null || docId.isEmpty()) {
			throw new EnvelopeException(400, "Missing the required parameter 'docId' when calling getDocument");
		}

		// 3.fetch list of documents
		List<EnvelopeDocument> documentList = new ArrayList<EnvelopeDocument>();
		EnvelopeDocumentsResult envelopeDocumentsResult = getEnvelopeApi().listDocuments(TokenBase.getAccountId(),
				envelopeId);
		documentList = envelopeDocumentsResult.getEnvelopeDocuments();

		// 4.get particular document
		List<EnvelopeDocument> filteredDocuments = documentList.stream()
				.filter(doc -> doc.getDocumentId().equalsIgnoreCase(docId)).collect(Collectors.toList());

		EnvelopeDocument envelopeDocument = new EnvelopeDocument();

		if (null == filteredDocuments || filteredDocuments.isEmpty()) {
			throw new EnvelopeException(404, "Could not find the specified ID. Please check your input and try again.");

		}

		envelopeDocument = filteredDocuments.get(0);

		// 5. get physical copy of document
		byte[] result = getEnvelopeApi().getDocument(TokenBase.getAccountId(), envelopeId, docId);

		// 6.set document details
		DocumentDTO documentDTO = new DocumentDTO();
		documentDTO.setDocumnetName(envelopeDocument.getName());
		documentDTO.setDocumnetType(envelopeDocument.getType());
		documentDTO.setDocumentBase64(new String(Base64.encode(result)));

		logger.debug("Document => " + documentDTO);
		return documentDTO;
	}

	/**
	 *
	 */
	/*
	 * Uncomment this code to enable caching
	 * 
	 * @Caching(evict = { @CacheEvict(value = "Envelope", allEntries = true),
	 * 
	 * @CacheEvict(value = "EnvelopesInformation", allEntries = true),
	 * 
	 * @CacheEvict(value = "DocumentDTO", allEntries = true) })
	 */
	@Override
	public DocumentResult deleteDocumentById(String envelopeId, String docId)
			throws IOException, ApiException, EnvelopeException {
		logger.info("Deleting document:");

		logger.info("Envelope Id => " + envelopeId + " Document Id => " + docId);

		// 1.verify the required parameter 'envelopeId' is set
		if (envelopeId == null || envelopeId.isEmpty()) {
			throw new EnvelopeException(400, "Missing the required parameter 'envelopeId' when calling deleteDocument");
		}

		// 2.verify the required parameter 'docId' is set
		if (docId == null || docId.isEmpty()) {
			throw new EnvelopeException(400, "Missing the required parameter 'docId' when calling get deleteDocument");
		}

		// 3.fetch list of documents
		List<EnvelopeDocument> documentList = new ArrayList<EnvelopeDocument>();
		EnvelopeDocumentsResult envelopeDocumentsResult = getEnvelopeApi().listDocuments(TokenBase.getAccountId(),
				envelopeId);
		documentList = envelopeDocumentsResult.getEnvelopeDocuments();

		// 4.check document exist
		List<EnvelopeDocument> filteredDocuments = documentList.stream()
				.filter(doc -> doc.getDocumentId().equalsIgnoreCase(docId)).collect(Collectors.toList());

		EnvelopeDocument envelopeDocument = new EnvelopeDocument();

		if (null == filteredDocuments || filteredDocuments.isEmpty()) {
			throw new EnvelopeException(404, "Could not find the specified ID. Please check your input and try again.");

		}

		envelopeDocument = filteredDocuments.get(0);

		// 5.set document id
		com.docusign.esign.model.EnvelopeDefinition envelopeDefinition = new com.docusign.esign.model.EnvelopeDefinition();
		List<com.docusign.esign.model.Document> envelopeDocumentList = new ArrayList<com.docusign.esign.model.Document>();

		com.docusign.esign.model.Document document = new com.docusign.esign.model.Document();
		document.setDocumentId(docId);
		envelopeDocumentList.add(document);
		envelopeDefinition.setDocuments(envelopeDocumentList);

		EnvelopeDocumentsResult result = getEnvelopeApi().deleteDocuments(TokenBase.getAccountId(), envelopeId,
				envelopeDefinition);

		DocumentResult documentResult = new DocumentResult();
		documentResult.setEnvelopeId(result.getEnvelopeId());
		documentResult.setDocumentId(envelopeDocument.getDocumentId());
		documentResult.setName(envelopeDocument.getName());
		documentResult.setMessage("Document deleted successfully");

		logger.info("Delete doc response: " + documentResult);
		return documentResult;
	}

	/**
	 *
	 */
	/*
	 * Uncomment this code to enable caching
	 * 
	 * @Cacheable(cacheNames = "Envelope")
	 */
	@Override
	public EnvelopesInformation filterEnvelopes(EnvelopeQueryStrings envelopeQueryStrings)
			throws IOException, ApiException {
		logger.info("Filter Documents:");
		logger.debug("Options to filter envelopes: " + envelopeQueryStrings);

		// 1.create query strings
		ListStatusChangesOptions options = getEnvelopeApi().new ListStatusChangesOptions();

		/*
		 * The date/time setting that specifies the date/time when the request begins
		 * checking for status changes for envelopes in the account. This is required
		 * unless &#39;envelopeId&#39;s are used.
		 */
		if (null != envelopeQueryStrings.getFromDate() && !envelopeQueryStrings.getFromDate().isEmpty()) {
			LocalDate fromDate = new LocalDate(envelopeQueryStrings.getFromDate());
			options.setFromDate(fromDate.toString(DATE_FORMAT));
			logger.info("From Date: " + fromDate);
		} else {
			LocalDate fromDate = LocalDate.now().minusDays(30);
			options.setFromDate(fromDate.toString(DATE_FORMAT));
			logger.info("From Date: " + fromDate);
		}

		/*
		 * Optional date/time setting that specifies the date/time when the request
		 * stops for status changes for envelopes in the account. If no entry, the
		 * system uses the time of the call as the &#x60;to_date&#x60;.
		 */
		if (null != envelopeQueryStrings.getToDate() && !envelopeQueryStrings.getToDate().isEmpty()) {
			LocalDate toDate = new LocalDate(envelopeQueryStrings.getToDate());
			options.setToDate(toDate.toString(DATE_FORMAT));
			logger.info("To Date: " + toDate);
		}

		/*
		 * Specifies the Authoritative Copy Status for the envelopes. The possible
		 * values are: Unknown, Original, Transferred, AuthoritativeCopy,
		 * AuthoritativeCopyExportPending, AuthoritativeCopyExported, DepositPending,
		 * Deposited, DepositedEO, DepositFailed
		 */
		if (null != envelopeQueryStrings.getAcStatus() && !envelopeQueryStrings.getAcStatus().isEmpty()) {
			options.setAcStatus(envelopeQueryStrings.getAcStatus());
		}

		/* The email address of the sender. */
		if (null != envelopeQueryStrings.getEmail() && !envelopeQueryStrings.getEmail().isEmpty()) {
			options.setEmail(envelopeQueryStrings.getEmail());
		}

		/* The maximum number of results to return. */
		if (null != envelopeQueryStrings.getCount() && !envelopeQueryStrings.getCount().isEmpty()) {
			options.setCount(envelopeQueryStrings.getCount());
		}

		if (null != envelopeQueryStrings.getContinuationToken()
				&& !envelopeQueryStrings.getContinuationToken().isEmpty()) {
			options.setContinuationToken(envelopeQueryStrings.getContinuationToken());
		}

		/*
		 * The envelope IDs to include in the results. The value of this property can
		 * be: A comma-separated list of envelope IDs The special value request_body. In
		 * this case, the method uses the envelope IDs in the request body.
		 */
		if (null != envelopeQueryStrings.getEnvelopeIds() && !envelopeQueryStrings.getEnvelopeIds().isEmpty()) {
			options.setEnvelopeIds(envelopeQueryStrings.getEnvelopeIds());
		}

		if (null != envelopeQueryStrings.getFolderIds() && !envelopeQueryStrings.getFolderIds().isEmpty()) {
			options.setFolderIds(envelopeQueryStrings.getFolderIds());
		}

		if (null != envelopeQueryStrings.getFolderTypes() && !envelopeQueryStrings.getFolderTypes().isEmpty()) {
			options.setFolderTypes(envelopeQueryStrings.getFolderTypes());
		}

		/*
		 * This is the status type checked for in the
		 * &#x60;from_date&#x60;/&#x60;to_date&#x60; period. If &#x60;changed&#x60; is
		 * specified, then envelopes that changed status during the period are found. If
		 * for example, &#x60;created&#x60; is specified, then envelopes created during
		 * the period are found. Default is &#x60;changed&#x60;. Possible values are:
		 * Voided, Changed, Created, Deleted, Sent, Delivered, Signed, Completed,
		 * Declined, TimedOut and Processing.
		 */
		if (null != envelopeQueryStrings.getFromToStatus() && !envelopeQueryStrings.getFromToStatus().isEmpty()) {
			options.setFromToStatus(envelopeQueryStrings.getFromToStatus());
		}

		/*
		 * (Optional) The order in which to sort the results. Valid values are: asc:
		 * Ascending order. desc: Descending order.
		 */
		if (null != envelopeQueryStrings.getOrder() && !envelopeQueryStrings.getOrder().isEmpty()) {
			options.setOrder(envelopeQueryStrings.getOrder());
		} else {
			options.setOrder("desc");
		}

		/*
		 * The field used to sort the results. Example: Created
		 */
		if (null != envelopeQueryStrings.getOrderBy() && !envelopeQueryStrings.getOrderBy().isEmpty()) {
			options.setOrderBy(envelopeQueryStrings.getOrderBy());
		} else {
			options.setOrderBy("created");
		}

		if (null != envelopeQueryStrings.getLastQueriedDate() && !envelopeQueryStrings.getLastQueriedDate().isEmpty()) {
			options.setLastQueriedDate(envelopeQueryStrings.getLastQueriedDate());
		}

		/*
		 * A free text search field for searching across the items in a folder. The
		 * search looks for the text that you enter in the recipient names and emails,
		 * envelope custom fields, sender name, and subject.
		 */
		if (null != envelopeQueryStrings.getSearchText() && !envelopeQueryStrings.getSearchText().isEmpty()) {
			options.setSearchText(envelopeQueryStrings.getSearchText());
		}

		/*
		 * The list of current statuses to include in the response. By default, all
		 * envelopes found are returned. If values are specified, then of the envelopes
		 * found, only those with the current status specified are returned in the
		 * results. Possible values are: Voided, Created, Deleted, Sent, Delivered,
		 * Signed, Completed, Declined, TimedOut and Processing.
		 */
		if (null != envelopeQueryStrings.getStatus() && !envelopeQueryStrings.getStatus().isEmpty()) {
			options.setStatus(envelopeQueryStrings.getStatus());
		}

		/*
		 * If included in the query string, this is a comma separated list of envelope
		 * &#x60;transactionId&#x60;s. If included in the &#x60;request_body&#x60;, this
		 * is a list of envelope &#x60;transactionId&#x60;s. ###### Note:
		 * &#x60;transactionId&#x60;s are only valid in the DocuSign system for seven
		 * days.
		 */

		if (null != envelopeQueryStrings.getTransactionIds() && !envelopeQueryStrings.getTransactionIds().isEmpty()) {
			options.setTransactionIds(envelopeQueryStrings.getTransactionIds());
		}

		if (null != envelopeQueryStrings.getUserFilter() && !envelopeQueryStrings.getUserFilter().isEmpty()) {
			options.setUserFilter(envelopeQueryStrings.getUserFilter());
		}

		/*
		 * The ID of the user to access. Generally this is the ID of the current
		 * authenticated user, but if the authenticated user is an Administrator on the
		 * account, userId can represent another user whom the Administrator is
		 * accessing.
		 */
		if (null != envelopeQueryStrings.getUserId() && !envelopeQueryStrings.getUserId().isEmpty()) {
			options.setUserId(envelopeQueryStrings.getUserId());
		}

		/*
		 * Limits results to envelopes sent by the account user with this user name.
		 * email must be given as well, and both email and user_name must refer to an
		 * existing account user.
		 */
		if (null != envelopeQueryStrings.getUserName() && !envelopeQueryStrings.getUserName().isEmpty()) {
			options.setUserName(envelopeQueryStrings.getUserName());
		}

		com.docusign.esign.model.EnvelopesInformation envelopesInformation = getEnvelopeApi()
				.listStatusChanges(TokenBase.getAccountId(), options);

		if (null == envelopesInformation.getEnvelopes() || envelopesInformation.getEnvelopes().isEmpty()) {
			throw new EnvelopeException(404,
					"Envelopes are not present with query criteria when calling query envelope");
		}

		List<Envelope> envelopeList = new ArrayList<Envelope>();
		for (com.docusign.esign.model.Envelope env : envelopesInformation.getEnvelopes()) {
			Envelope envelope = new Envelope();
			envelope.setCreatedDateTime(env.getCreatedDateTime());
			envelope.setEnvelopeDocuments(getEnvelopeDocuments(env));
			envelope.setEmailSubject(env.getEmailSubject());
			envelope.setLastModifiedDateTime(env.getLastModifiedDateTime());
			envelope.setRecipients(getEnvelopeRecipients(env));
			envelope.setSentDateTime(env.getSentDateTime());
			envelope.setStatus(env.getStatus());
			envelope.setStatusChangedDateTime(env.getStatusChangedDateTime());
			envelope.setEnvelopeId(env.getEnvelopeId());
			envelopeList.add(envelope);
		}

		EnvelopesInformation envelopesInfo = new EnvelopesInformation();
		envelopesInfo.setEnvelopes(envelopeList);
		logger.debug("Filter Envelopes: " + envelopeList);
		return envelopesInfo;
	}

	@Override
	public void envelopeStatusProcessing(String xmlString) {
		logger.info("Data received from DS Connect: " + xmlString);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			Envelope envelope = new Envelope();
			builder = factory.newDocumentBuilder();

			org.w3c.dom.Document xml = builder.parse(new InputSource(new StringReader(xmlString)));
			xml.getDocumentElement().normalize();
			logger.info("Connect data parsed!");
			Element envelopeStatus = (Element) xml.getElementsByTagName("EnvelopeStatus").item(0);
			String envelopeId = envelopeStatus.getElementsByTagName("EnvelopeID").item(0).getChildNodes().item(0)
					.getNodeValue();
			String sentDateTime = envelopeStatus.getElementsByTagName("Sent").item(0).getChildNodes().item(0)
					.getNodeValue();
			logger.info("envelopeId=" + envelopeId);
			NodeList recipientStatuses = envelopeStatus.getElementsByTagName("RecipientStatuses").item(0)
					.getChildNodes();
			List<Signer> signerStatusList = new ArrayList<>();
			List<CarbonCopy> ccStatusList = new ArrayList<>();

			for (int i = 0; i < recipientStatuses.getLength(); i++) {
				if (recipientStatuses.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) recipientStatuses.item(i);
					String sent = null, email = null, userName = null, status = null;
					String type = element.getElementsByTagName("Type").item(0).getChildNodes().item(0).getNodeValue();

					if (null != element.getElementsByTagName("Email").item(0).getChildNodes().item(0).getNodeValue()) {
						email = element.getElementsByTagName("Email").item(0).getChildNodes().item(0).getNodeValue();
					}

					if (null != element.getElementsByTagName("UserName").item(0).getChildNodes().item(0)
							.getNodeValue()) {
						userName = element.getElementsByTagName("UserName").item(0).getChildNodes().item(0)
								.getNodeValue();
					}

					if (null != element.getElementsByTagName("Sent").item(0)) {
						if (null != element.getElementsByTagName("Sent").item(0).getChildNodes().item(0).getNodeValue())
							sent = element.getElementsByTagName("Sent").item(0).getChildNodes().item(0).getNodeValue();
					}

					if (null != element.getElementsByTagName("Status").item(0).getChildNodes().item(0).getNodeValue()) {
						status = element.getElementsByTagName("Status").item(0).getChildNodes().item(0).getNodeValue();
					}

					if (type.equalsIgnoreCase("Signer")) {
						Signer signerStatus = new Signer(sent, email, userName, status);
						signerStatusList.add(signerStatus);
					} else if (type.equalsIgnoreCase("CarbonCopy")) {
						CarbonCopy ccStatus = new CarbonCopy(sent, email, userName, status);
						ccStatusList.add(ccStatus);
					}
				}

			}

			String emailSubject = envelopeStatus.getElementsByTagName("Subject").item(0).getChildNodes().item(0)
					.getNodeValue();
			String envStatus = envelopeStatus.getElementsByTagName("Status").item(0).getChildNodes().item(0)
					.getNodeValue();
			String timeGenerated = envelopeStatus.getElementsByTagName("TimeGenerated").item(0).getChildNodes().item(0)
					.getNodeValue();
			logger.info("timeGenerated=" + timeGenerated);

			envelope.setStatusChangedDateTime(timeGenerated);
			envelope.setEnvelopeId(envelopeId);
			envelope.setEmailSubject(emailSubject);
			envelope.setStatus(envStatus);
			envelope.setSentDateTime(sentDateTime);

			Recipients recipients = new Recipients();
			recipients.setCarbonCopy(ccStatusList);
			recipients.setSigners(signerStatusList);
			envelope.setRecipients(recipients);

			// putting into in memory repo
			envelopeRepo.put(envelope.getEnvelopeId(), envelope);

			logger.info("Envelope status updated in HashMap repo: " + envStatus);

		} catch (Exception e) {
			logger.error("!!!!!! PROBLEM DocuSign Webhook: Couldn't parse the XML sent by DocuSign Connect: "
					+ e.getMessage());
		}
	}

	@Override
	public Envelope envelopeStatusPolling(String envelopeId) {
		Envelope envelope = null;
		boolean envStatus = false;
		logger.info("Checking in hashmap repo...");
		
		if (!envelopeRepo.containsKey(envelopeId)) {
			logger.info("Envelope not present in hash map repo");
			throw new EnvelopeException(404, "Envelope Not Found");

		} 
			
		envelope = envelopeRepo.get(envelopeId);
		
		if (envelope.getStatus().equalsIgnoreCase("Completed")) {
			for (com.rgp.de.beans.Signer signer : envelope.getRecipients().getSigners()) {
				if (signer.getStatus().equalsIgnoreCase("Completed")) {
						envStatus = true;
				}else {
						envStatus = false;
						break;
					  }
			 }
		}

		if (envStatus) {
			for (com.rgp.de.beans.CarbonCopy cc : envelope.getRecipients().getCarbonCopy()) {
				if (cc.getStatus().equalsIgnoreCase("Completed")) {
						envStatus = true;
				}else {
						envStatus = false;
						break;
					  }
			}
		}
		
		if (envStatus) {
			envelopeRepo.remove(envelopeId);
		}

		logger.info("Envelope polling result: " + envelope);
		return envelope;
	}

	private Recipients getEnvelopeRecipients(com.docusign.esign.model.Envelope env) throws ApiException, IOException {
		com.docusign.esign.model.Recipients envRecipients = getEnvelopeApi().listRecipients(TokenBase.getAccountId(),
				env.getEnvelopeId());

		Recipients recipients = new Recipients();
		List<Signer> signerList = envRecipients.getSigners().stream().map(objects -> new Signer(objects))
				.collect(Collectors.toList());

		if (null != envRecipients.getCarbonCopies() && !envRecipients.getCarbonCopies().isEmpty()) {
			List<CarbonCopy> ccList = envRecipients.getCarbonCopies().stream().map(objects -> new CarbonCopy(objects))
					.collect(Collectors.toList());
			recipients.setCarbonCopy(ccList);
		}

		recipients.setSigners(signerList);
		return recipients;
	}

	private EnvelopeEvent getEnvelopeEvent() {
		EnvelopeEvent envelopeEvent = new EnvelopeEvent();
		envelopeEvent.setEnvelopeEventStatusCode("sent");
		envelopeEvent.setEnvelopeEventStatusCode("delivered");
		envelopeEvent.setEnvelopeEventStatusCode("completed");
		envelopeEvent.setEnvelopeEventStatusCode("declined");
		envelopeEvent.setEnvelopeEventStatusCode("voided");
		return envelopeEvent;
	}

	private RecipientEvent getRecipientsEvent() {
		RecipientEvent recipientEvent = new RecipientEvent();
		recipientEvent.setRecipientEventStatusCode("Sent");
		recipientEvent.setRecipientEventStatusCode("Delivered");
		recipientEvent.setRecipientEventStatusCode("Completed");
		recipientEvent.setRecipientEventStatusCode("Declined");
		recipientEvent.setRecipientEventStatusCode("AuthenticationFailed");
		recipientEvent.setRecipientEventStatusCode("AutoResponded");
		return recipientEvent;
	}

	private EventNotification getEventNotification() {
		EventNotification eventNotification = new EventNotification();
		eventNotification.setUrl(env.getProperty("webhook.url"));
		eventNotification.setLoggingEnabled("true");
		eventNotification.setRequireAcknowledgment("true");
		eventNotification.setUseSoapInterface("false");
		eventNotification.setSignMessageWithX509Cert("false");
		eventNotification.setIncludeEnvelopeVoidReason("true");
		eventNotification.setIncludeTimeZone("true");
		eventNotification.setIncludeCertificateWithSoap("false");
		eventNotification.setIncludeDocuments("true");
		eventNotification.setIncludeSenderAccountAsCustomField("true");
		eventNotification.setIncludeDocumentFields("true");
		eventNotification.setIncludeCertificateOfCompletion("true");
		
		List<EnvelopeEvent> envelopeEvents = new ArrayList<>();
		envelopeEvents.add(getEnvelopeEvent());

		List<RecipientEvent> recipientEvents = new ArrayList<>();
		recipientEvents.add(getRecipientsEvent());
		
		eventNotification.setEnvelopeEvents(envelopeEvents);
		eventNotification.setRecipientEvents(recipientEvents);
		return eventNotification;
	}

	private List<Document> getEnvelopeDocuments(com.docusign.esign.model.Envelope env)
			throws ApiException, IOException {

		EnvelopeDocumentsResult result = getEnvelopeApi().listDocuments(TokenBase.getAccountId(), env.getEnvelopeId());
		List<Document> documentList = result.getEnvelopeDocuments().stream().map(objects -> new Document(objects))
				.collect(Collectors.toList());
		return documentList;
	}

	private RecipientViewRequest getRecipientViewRequest(com.docusign.esign.model.Signer signerData,
			com.docusign.esign.model.RecipientViewRequest recipientViewRequest) {
		RecipientViewRequest viewRequest = new RecipientViewRequest();
		// Set the url where you want the recipient to go once they are done signing
		// should typically be a callback route somewhere in your app.
		viewRequest.setReturnUrl(recipientViewRequest.getReturnUrl());
		viewRequest.setAuthenticationMethod(recipientViewRequest.getAuthenticationMethod());
		viewRequest.setEmail(signerData.getEmail());
		viewRequest.setUserName(signerData.getName());
		viewRequest.setClientUserId(signerData.getClientUserId());
		return viewRequest;
	}

	private RecipientViewRequest getRecipientViewRequestForTemplateRole(
			com.docusign.esign.model.TemplateRole templateRole,
			com.docusign.esign.model.RecipientViewRequest recipientView) {
		RecipientViewRequest recipientViewRequest = new RecipientViewRequest();
		// Set the url where you want the recipient to go once they are done signing
		// should typically be a callback route somewhere in your app.
		recipientViewRequest.setReturnUrl(recipientView.getReturnUrl());
		recipientViewRequest.setAuthenticationMethod(recipientView.getAuthenticationMethod());
		recipientViewRequest.setEmail(templateRole.getEmail());
		recipientViewRequest.setUserName(templateRole.getName());
		recipientViewRequest.setClientUserId(templateRole.getClientUserId());
		return recipientViewRequest;
	}

	private List<com.docusign.esign.model.TemplateRole> getTemplateSignerData(
			List<com.docusign.esign.model.Signer> signerList, boolean isEmbed) {

		List<com.docusign.esign.model.TemplateRole> templateRoleList = new ArrayList<com.docusign.esign.model.TemplateRole>();
		for (com.docusign.esign.model.Signer signerData : signerList) {
			TemplateRole signer = new TemplateRole();
			signer.setEmail(signerData.getEmail());
			signer.setName(signerData.getName());
			signer.setRoleName(Roles.SIGNER_ROLE_NAME);
			if (isEmbed) {
				/**
				 * Specifies whether the recipient is embedded or remote. If the `clientUserId`
				 * property is not null then the recipient is embedded. Note that if the
				 * `ClientUserId` property is set and either `SignerMustHaveAccount` or
				 * `SignerMustLoginToSign` property of the account settings is set to **true**,
				 * an error is generated on sending.ng. Maximum length: 100 characters. We set
				 * the clientUserId to enable embedded signing for the recipient
				 **/
				signer.setClientUserId(EnvelopeUtils.getClientUserId().toString());
			}
			templateRoleList.add(signer);
		}
		return templateRoleList;
	}

	private List<com.docusign.esign.model.Signer> getSignersData(List<com.docusign.esign.model.Signer> signers,
			boolean isEmbed) {

		List<com.docusign.esign.model.Signer> signerList = new ArrayList<com.docusign.esign.model.Signer>();

		for (com.docusign.esign.model.Signer signerData : signers) {
			com.docusign.esign.model.Signer signer = new com.docusign.esign.model.Signer();
			signer.setEmail(signerData.getEmail());
			signer.setName(signerData.getName());
			// Unique for the recipient, It is used by the tab element to indicate which
			// recipient is to sign the Document.
			signer.setRecipientId(EnvelopeUtils.getRecipientId().toString());
			signer.setTabs(signerData.getTabs());
			if (isEmbed) {
				/**
				 * Specifies whether the recipient is embedded or remote. If the `clientUserId`
				 * property is not null then the recipient is embedded. Note that if the
				 * `ClientUserId` property is set and either `SignerMustHaveAccount` or
				 * `SignerMustLoginToSign` property of the account settings is set to **true**,
				 * an error is generated on sending.ng. Maximum length: 100 characters. We set
				 * the clientUserId to enable embedded signing for the recipient
				 **/
				signer.setClientUserId(EnvelopeUtils.getClientUserId().toString());
			}
			signer.setRoutingOrder(signerData.getRoutingOrder());
			signerList.add(signer);
		}
		return signerList;
	}

	private List<com.docusign.esign.model.Document> getDocumentsData(
			List<com.docusign.esign.model.Document> documentList) {
		List<com.docusign.esign.model.Document> documents = new ArrayList<com.docusign.esign.model.Document>();

		for (com.docusign.esign.model.Document documentData : documentList) {
			com.docusign.esign.model.Document document = new com.docusign.esign.model.Document();
			document.setDocumentBase64(new String(documentData.getDocumentBase64()));
			document.setName(documentData.getName());
			document.setFileExtension(documentData.getFileExtension());
			document.setDocumentId(documentData.getDocumentId());
			documents.add(document);
		}
		return documents;
	}

	private Embed callRecipientViewApi(String envelopeId, RecipientViewRequest viewRequest)
			throws IOException, ApiException {
		// call the CreateRecipientView API
		ViewUrl viewUrl = getEnvelopeApi().createRecipientView(TokenBase.getAccountId(), envelopeId, viewRequest);

		// The Recipient View URL (the Signing Ceremony URL) has been received.
		// The user's browser will be redirected to it.
		Embed embed = new Embed();
		embed.setViewUrl(viewUrl.getUrl());
		embed.setSignerName(viewRequest.getUserName());
		return embed;
	}

	private EnvelopeSummary callCreateEnvelopeApi(com.docusign.esign.model.EnvelopeDefinition envelopeDefinition)
			throws IOException, ApiException {
		EnvelopeSummary results = getEnvelopeApi().createEnvelope(TokenBase.getAccountId(), envelopeDefinition);
		return results;
	}

	private byte[] callGetDocumentApi(String envelopeId, String docId) throws IOException, ApiException {
		byte[] result = getEnvelopeApi().getDocument(TokenBase.getAccountId(), envelopeId, docId);
		return result;
	}

	private EnvelopesApi getEnvelopeApi() throws IOException, ApiException {
		tokenBase = new TokenBase(apiClient);
		tokenBase.checkToken();
		EnvelopesApi envelopeApi = new EnvelopesApi(apiClient);
		return envelopeApi;
	}
}