package com.rgp.de.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docusign.esign.client.ApiException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.rgp.de.beans.TemplateDTO;
import com.rgp.de.exception.EnvelopeException;
import com.rgp.de.exception.ErrorDetails;
import com.rgp.de.service.TemplateService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/template")
public class TemplateController {

	private Logger logger = LoggerFactory.getLogger(TemplateController.class);
	private String serviceDownResponse = "You are seeing this fallback response because the underlying microservice is down or has thrown an error!";

	@Autowired
	private TemplateService templateService;

	/**
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	@ApiOperation(value = "Template List")
	@GetMapping
	@HystrixCommand(threadPoolKey = "threadpoolkey", ignoreExceptions = {
			ApiException.class, EnvelopeException.class}, fallbackMethod = "templateListDefaultResponse")
	public ResponseEntity<?> listTemplates() throws IOException, ApiException {
		List<TemplateDTO> templateInfoList = new ArrayList<TemplateDTO>();
		templateInfoList = templateService.getAllTemplates();
		return new ResponseEntity<List<TemplateDTO>>(templateInfoList, HttpStatus.OK);

	}

	@SuppressWarnings("unused")
	private ResponseEntity<?> templateListDefaultResponse(Throwable throwable) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(), serviceDownResponse,
				500, LocalDateTime.now().toString());
		logger.info("Fallback Response: " + serviceDownResponse, errorDetails);
		logger.error(throwable.getMessage(), throwable);
		return ResponseEntity.status(500).body(errorDetails);
	}

}
