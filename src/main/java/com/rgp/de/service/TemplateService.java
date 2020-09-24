package com.rgp.de.service;

import java.io.IOException;
import java.util.List;

import com.docusign.esign.client.ApiException;
import com.rgp.de.beans.TemplateDTO;

public interface TemplateService {

	public List<TemplateDTO> getAllTemplates() throws IOException, ApiException;
}
