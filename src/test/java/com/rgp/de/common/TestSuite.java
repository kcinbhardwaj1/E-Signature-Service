package com.rgp.de.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import com.rgp.de.service.impl.EnvelopeServiceImplTest;
import com.rgp.de.service.impl.TemplateServiceImplTest;

@RunWith(Suite.class)
@SuiteClasses({
TemplateServiceImplTest.class,
EnvelopeServiceImplTest.class
 })
public class TestSuite {

}
