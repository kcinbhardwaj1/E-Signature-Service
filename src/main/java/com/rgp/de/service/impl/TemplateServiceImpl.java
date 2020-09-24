package com.rgp.de.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.docusign.esign.api.TemplatesApi;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.EnvelopeTemplate;
import com.docusign.esign.model.EnvelopeTemplateResults;
import com.docusign.esign.model.TemplateDocumentsResult;
import com.rgp.de.beans.CarbonCopy;
import com.rgp.de.beans.Document;
import com.rgp.de.beans.Recipients;
import com.rgp.de.beans.Signer;
import com.rgp.de.beans.TemplateDTO;
import com.rgp.de.service.TemplateService;
import com.rgp.de.util.TokenBase;

@Service
public class TemplateServiceImpl extends BaseService implements TemplateService {

	private Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

	/**
	 *
	 */
	@Override
	public List<TemplateDTO> getAllTemplates() throws IOException, ApiException {
		
		logger.info("Getting all templates");

		// 1.check token
		tokenBase = new TokenBase(apiClient);
		tokenBase.checkToken();

		// 2.get template list
		TemplatesApi templatesApi = new TemplatesApi(apiClient);
		EnvelopeTemplateResults envelopeTemplateResults = templatesApi.listTemplates(TokenBase.getAccountId());

		List<TemplateDTO> templateList = new ArrayList<TemplateDTO>();

		// 3.get recipients and document associated with template
		if (envelopeTemplateResults.getEnvelopeTemplates() != null
				&& !envelopeTemplateResults.getEnvelopeTemplates().isEmpty()) {

			for (EnvelopeTemplate envelopeTemplate : envelopeTemplateResults.getEnvelopeTemplates()) {
				TemplateDTO template = new TemplateDTO();
				template.setTemplateId(envelopeTemplate.getTemplateId());
				template.setName(envelopeTemplate.getName());

				List<Document> docList = getDocuments(envelopeTemplate.getTemplateId());
				Recipients recipients = getRecipients(envelopeTemplate.getTemplateId());
				template.setDocuments(docList);
				template.setRecipients(recipients);

				templateList.add(template);
			}

			logger.debug("Template List: " + templateList);

		}

		return templateList;
	}

	private List<Document> getDocuments(String templateId) throws IOException, ApiException {

		// 1.check token
		tokenBase = new TokenBase(apiClient);
		tokenBase.checkToken();

		// 2.get documents
		TemplatesApi templatesApi = new TemplatesApi(apiClient);
		TemplateDocumentsResult templateDocumentsResult = templatesApi.listDocuments(TokenBase.getAccountId(),
				templateId);

		// 3.set documents
		List<Document> docList = templateDocumentsResult.getTemplateDocuments().stream()
				.map(envelopeDocument -> new Document(envelopeDocument.getDocumentId(), envelopeDocument.getName()))
				.collect(Collectors.toList());
		return docList;
	}

	private Recipients getRecipients(String templateId) throws IOException, ApiException {

		// 1.check token
		tokenBase = new TokenBase(apiClient);
		tokenBase.checkToken();

		// 2.get recipients
		TemplatesApi templatesApi = new TemplatesApi(apiClient);
		com.docusign.esign.model.Recipients templateRecipients = templatesApi.listRecipients(TokenBase.getAccountId(),
				templateId);

		// 3.set signers
		Recipients recipients = new Recipients();
		if (templateRecipients.getSigners() != null && !templateRecipients.getSigners().isEmpty()) {
			List<Signer> signerList = templateRecipients.getSigners().stream()
					.map(templateSigner -> new Signer(templateSigner.getEmail(), templateSigner.getName(),
							templateSigner.getRecipientId()))
					.collect(Collectors.toList());
			recipients.setSigners(signerList);
		}
		
		// 4. set cc
		if (templateRecipients.getCarbonCopies() != null && !templateRecipients.getCarbonCopies().isEmpty()) {
			List<CarbonCopy> cCList = templateRecipients.getCarbonCopies().stream()
					.map(templateCc -> new CarbonCopy(templateCc.getEmail(), templateCc.getName(),
							templateCc.getRecipientId()))
					.collect(Collectors.toList());
			recipients.setCarbonCopy(cCList);
		}

		return recipients;
	}

}
