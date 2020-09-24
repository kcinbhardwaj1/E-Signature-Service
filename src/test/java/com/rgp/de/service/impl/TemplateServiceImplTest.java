package com.rgp.de.service.impl;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.docusign.esign.client.ApiException;
import com.rgp.de.beans.TemplateDTO;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class TemplateServiceImplTest {

	private Logger logger = LoggerFactory.getLogger(TemplateServiceImplTest.class);

	private TemplateServiceImpl templateServiceImpl = new TemplateServiceImpl();

	@Test
	public void test_getAllTemplates() throws ApiException, IOException {
		logger.info("In test Listing template starts: ");
		List<TemplateDTO> actualTemplateList = templateServiceImpl.getAllTemplates();
		logger.debug("List of Template response: " + actualTemplateList);
		Assert.assertNotNull(actualTemplateList);
	}

}
