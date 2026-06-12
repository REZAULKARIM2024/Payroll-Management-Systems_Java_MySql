package com.payroll.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Helper runner class for Cucumber feature files.
 *
 * NOTE: Must be named *Test / Test* / *Tests / *TestCase so that the
 * maven-surefire-plugin's default include pattern picks it up.
 *
 * Running `mvn test` (or `mvn clean verify`) will:
 *  - discover all .feature files under src/test/resources/features
 *  - run them against the step definitions in com.payroll.cucumber
 *  - generate pretty/HTML/JSON reports under target/cucumber-reports
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.payroll.cucumber")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
public class RunCucumberTest {
}
