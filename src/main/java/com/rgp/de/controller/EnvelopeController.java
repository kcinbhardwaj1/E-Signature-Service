package com.rgp.de.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docusign.esign.client.ApiException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.rgp.de.beans.DocumentDTO;
import com.rgp.de.beans.DocumentResult;
import com.rgp.de.beans.EmbedSummary;
import com.rgp.de.beans.Envelope;
import com.rgp.de.beans.EnvelopeDefination;
import com.rgp.de.beans.EnvelopeQueryStrings;
import com.rgp.de.beans.EnvelopesInformation;
import com.rgp.de.beans.RemoteSummary;
import com.rgp.de.exception.EnvelopeException;
import com.rgp.de.exception.ErrorDetails;
import com.rgp.de.service.EnvelopeService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/envelope")
public class EnvelopeController {

	private Logger logger = LoggerFactory.getLogger(EnvelopeController.class);
	private String serviceDownResponse = "You are seeing this fallback response because the underlying microservice is down or has thrown an error!";

	@Autowired
	private EnvelopeService envelopeService;

	/**
	 * @param envelope
	 * @return
	 * @throws IOException
	 * @throws ApiException
	 */
	@ApiOperation(value = "create Envelope using template")
	@PostMapping("/template")
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = { ApiException.class,
			EnvelopeException.class }, fallbackMethod = "createRemoteSigningDefaultResponse")
	public ResponseEntity<?> createEnvelopeUsingTemplate(@RequestBody EnvelopeDefination envelope)
			throws ApiException, IOException {

		RemoteSummary remoteSummary = envelopeService.createAndSendEnvelopeByTemplate(envelope);
		return new ResponseEntity<RemoteSummary>(remoteSummary, HttpStatus.OK);

	}

	@ApiOperation(value = "create new Envelope")
	@PostMapping
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = { ApiException.class,
			EnvelopeException.class }, fallbackMethod = "createRemoteSigningDefaultResponse")
	public ResponseEntity<?> createEnvelope(@RequestBody EnvelopeDefination envelope) throws ApiException, IOException {

		RemoteSummary remoteSummary = envelopeService.createAndSendEnvelope(envelope);
		return new ResponseEntity<RemoteSummary>(remoteSummary, HttpStatus.OK);

	}

	/**
	 * @param envelope
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	@ApiOperation(value = "create Embedded Signing Envelope")
	@PostMapping("/embed/template")
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = { ApiException.class,
			EnvelopeException.class }, fallbackMethod = "embeddedDefaultResponse")
	public ResponseEntity<?> embeddedSigningUsingTemplate(@RequestBody EnvelopeDefination envelope)
			throws IOException, ApiException {
		
		EmbedSummary embedSummary = envelopeService.embeddedSigningByTemplate(envelope);
		return new ResponseEntity<EmbedSummary>(embedSummary, HttpStatus.OK);

	}

	@ApiOperation(value = "create Embedded Signing Envelope")
	@PostMapping("/embed")
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = { ApiException.class,
			EnvelopeException.class }, fallbackMethod = "embeddedDefaultResponse")
	public ResponseEntity<?> embeddedSigning(@RequestBody EnvelopeDefination envelope)
			throws IOException, ApiException {
		
		EmbedSummary embedSummary = envelopeService.embeddedSigning(envelope);
		return new ResponseEntity<EmbedSummary>(embedSummary, HttpStatus.OK);

	}

	/**
	 * @return
	 * @throws IOException
	 * @throws ApiException
	 */
	@ApiOperation(value = "Reterive All Envelopes List")
	@GetMapping("/{days}")
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = { ApiException.class,
			EnvelopeException.class }, fallbackMethod = "getAllEnvelopeDefaultResponse")
	public ResponseEntity<?> getAllEnvelopes(@PathVariable Integer days) throws ApiException, IOException {
		
		EnvelopesInformation envelopesInformation = envelopeService.fetchAllEnvelopes(days);
		return new ResponseEntity<EnvelopesInformation>(envelopesInformation, HttpStatus.OK);

	}

	/**
	 * @param envelopeId
	 * @return
	 * @throws IOException
	 * @throws ApiException
	 */

	@ApiOperation(value = "Reterive All Envelopes List by envelopeId")
	@GetMapping("/{envelopeId}/document")
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = { ApiException.class,
			EnvelopeException.class }, fallbackMethod = "getAllDocumentDefaultResponse")
	public ResponseEntity<?> getAllDocuments(@PathVariable String envelopeId) throws ApiException, IOException {
		
		List<DocumentDTO> documentList = envelopeService.getAllDocumntsByEnvelopeId(envelopeId);
		return new ResponseEntity<List<DocumentDTO>>(documentList, HttpStatus.OK);

	}

	/**
	 * @param envelopeId
	 * @param docId
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	@ApiOperation(value = "Reterive All Envelopes List by envelopeId")
	@GetMapping("/{envelopeId}/document/{docId}")
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = { ApiException.class,
			EnvelopeException.class }, fallbackMethod = "getDocumentDefaultResponse")
	public ResponseEntity<?> getDocument(@PathVariable String envelopeId, @PathVariable String docId)
			throws IOException, ApiException {

		DocumentDTO document = envelopeService.getDocumentOfEnvelopeByDocuId(envelopeId, docId);
		return new ResponseEntity<DocumentDTO>(document, HttpStatus.OK);

	}

	/**
	 * @param envelopeId
	 * @param docId
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 * @throws EnvelopeException
	 */

	@ApiOperation(value = "Delete Document by envelopeId")
	@DeleteMapping("/{envelopeId}/document/{docId}")
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = { ApiException.class,
			EnvelopeException.class }, fallbackMethod = "deleteDocumentDefaultResponse")
	public ResponseEntity<?> deleteDocument(@PathVariable String envelopeId, @PathVariable String docId)
			throws EnvelopeException, IOException, ApiException {
		
		DocumentResult result = envelopeService.deleteDocumentById(envelopeId, docId);
		return new ResponseEntity<DocumentResult>(result, HttpStatus.OK);

	}

	/**
	 * @param envelopeQueryStrings
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	@ApiOperation(value = "Filter Envelopes")
	@PostMapping("/query")
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = { ApiException.class,
			EnvelopeException.class }, fallbackMethod = "filterEnvelopesDefaultResponse")
	public ResponseEntity<?> filterEnvelopes(@RequestBody EnvelopeQueryStrings envelopeQueryStrings)
			throws IOException, ApiException {
		
		EnvelopesInformation envelopesInformation = envelopeService.filterEnvelopes(envelopeQueryStrings);
		return new ResponseEntity<EnvelopesInformation>(envelopesInformation, HttpStatus.OK);

	}

	@ApiOperation(value = "Webhook Listener")
	@PostMapping(value = "/status")
	public void envelopeStatus(@RequestBody String xmlString) {
		envelopeService.envelopeStatusProcessing(xmlString);
		logger.debug("Webhook reponse end: ");

	}

	@ApiOperation(value = "Webhook Status polling")
	@GetMapping("/status/{envelopeId}")
	public Envelope envelopeStatusPolling(@PathVariable String envelopeId) {
		return envelopeService.envelopeStatusPolling(envelopeId);
	}

	// When we define a fallback-method, the fallback-method must match the same
	// parameters of the method where you define the Hystrix Command
	// using the hystrix-command annotation.

	@SuppressWarnings("unused")
	private ResponseEntity<?> createRemoteSigningDefaultResponse(@RequestBody EnvelopeDefination envelope,
			Throwable throwable) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(), serviceDownResponse,
				500, LocalDateTime.now().toString());
		logger.info("Fallback Response: " + serviceDownResponse, errorDetails);
		logger.error(throwable.getMessage(), throwable);
		return ResponseEntity.status(500).body(errorDetails);
	}

	@SuppressWarnings("unused")
	private ResponseEntity<?> embeddedDefaultResponse(@RequestBody EnvelopeDefination envelope, Throwable throwable) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(), serviceDownResponse,
				500, LocalDateTime.now().toString());
		logger.info("Fallback Response: " + serviceDownResponse, errorDetails);
		logger.error(throwable.getMessage(), throwable);
		return ResponseEntity.status(500).body(errorDetails);
	}

	@SuppressWarnings("unused")
	private ResponseEntity<?> getAllEnvelopeDefaultResponse(@PathVariable(required = false) Integer days,
			Throwable throwable) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(), serviceDownResponse,
				500, LocalDateTime.now().toString());
		logger.info("Fallback Response: " + serviceDownResponse, errorDetails);
		logger.error(throwable.getMessage(), throwable);
		return ResponseEntity.status(500).body(errorDetails);
	}

	@SuppressWarnings("unused")
	private ResponseEntity<?> getAllDocumentDefaultResponse(@PathVariable String envelopeId, Throwable throwable) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(), serviceDownResponse,
				500, LocalDateTime.now().toString());
		logger.info("Fallback Response: " + serviceDownResponse, errorDetails);
		logger.error(throwable.getMessage(), throwable);
		return ResponseEntity.status(500).body(errorDetails);
	}

	@SuppressWarnings("unused")
	private ResponseEntity<?> getDocumentDefaultResponse(@PathVariable String envelopeId, @PathVariable String docId,
			Throwable throwable) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(), serviceDownResponse,
				500, LocalDateTime.now().toString());
		logger.info("Fallback Response: " + serviceDownResponse, errorDetails);
		logger.error(throwable.getMessage(), throwable);
		return ResponseEntity.status(500).body(errorDetails);
	}

	@SuppressWarnings("unused")
	private ResponseEntity<?> deleteDocumentDefaultResponse(@PathVariable String envelopeId, @PathVariable String docId,
			Throwable throwable) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(), serviceDownResponse,
				500, LocalDateTime.now().toString());
		logger.info("Fallback Response: " + serviceDownResponse, errorDetails);
		logger.error(throwable.getMessage(), throwable);
		return ResponseEntity.status(500).body(errorDetails);
	}

	@SuppressWarnings("unused")
	private ResponseEntity<?> filterEnvelopesDefaultResponse(@RequestBody EnvelopeQueryStrings envelopeQueryStrings,
			Throwable throwable) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(), serviceDownResponse,
				500, LocalDateTime.now().toString());
		logger.info("Fallback Response: " + serviceDownResponse, errorDetails);
		logger.error(throwable.getMessage(), throwable);
		return ResponseEntity.status(500).body(errorDetails);
	}

}
