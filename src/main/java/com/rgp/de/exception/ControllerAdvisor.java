package com.rgp.de.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.docusign.esign.client.ApiException;

@ControllerAdvice(annotations = RestController.class)
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EnvelopeException.class)
	public final ResponseEntity<ErrorDetails> handleEnvelopeException(EnvelopeException ex, WebRequest request) {
		logger.error(ex.getMessage(), ex);
		ErrorDetails errorDetails= new ErrorDetails(HttpStatus.valueOf(ex.getCode()).getReasonPhrase(),
				ex.getMessage(),ex.getCode(),LocalDateTime.now().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.valueOf(ex.getCode()));

	}

	@ExceptionHandler(ApiException.class)
	public final ResponseEntity<ErrorDetails> handleApiException(ApiException ex, WebRequest request) {
		logger.error(ex.getResponseBody(), ex);
		ErrorDetails errorDetails= new ErrorDetails(HttpStatus.valueOf(ex.getCode()).getReasonPhrase()
				,ex.getResponseBody(),ex.getCode(),LocalDateTime.now().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.valueOf(ex.getCode()));

	}

	@ExceptionHandler(IOException.class)
	public final ResponseEntity<ErrorDetails> handleIoException(IOException ex, WebRequest request) {
		logger.error(ex.getMessage(), ex);
		ErrorDetails errorDetails= new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(),
				ex.getMessage(),500, LocalDateTime.now().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorDetails> handleException(Exception ex) {
		logger.error(ex.getMessage(), ex);
		ErrorDetails errorDetails= new ErrorDetails(HttpStatus.valueOf(500).getReasonPhrase(),
				ex.getMessage(),500, LocalDateTime.now().toString());
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);

	}
}
