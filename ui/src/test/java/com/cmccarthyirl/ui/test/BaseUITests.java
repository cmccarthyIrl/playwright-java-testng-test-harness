package com.cmccarthyirl.ui.test;

import com.cmccarthyirl.common.*;
import com.cmccarthyirl.ui.utils.PlaywrightManager;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

@Listeners({TestListener.class})
public class BaseUITests {

    private final MDCModel mdcModel = new MDCModel();
    // Instance of BrowserManager to handle browser setup and management
    private final PlaywrightManager playwrightManager = new PlaywrightManager();

    /**
     * Set up the test suite by initializing the browser and loading the necessary configurations.
     * This method is executed once before the tests start running in the suite.
     *
     * @throws IOException if there is an issue reading the properties file.
     */
    @BeforeSuite
    public void setUp() throws IOException {
        // Load the configuration properties (like browser settings, headless mode, etc based on the maven profile)
        String configFile = System.getProperty("config.file", "default-config.properties");
        Properties properties = new ReadPropertyFile().loadProperties("./" + configFile);

        // Create a new browser instance using the loaded properties
        playwrightManager.createBrowser(properties);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method, ITestContext context, ITestResult iTestResult) {
        String methodName = method.getName();
        String logFileName = "UI-" + methodName;

        // Set log file name in MDC
        MDC.put("logFileName", logFileName);

        // Update model name
        mdcModel.setName(logFileName);

        // Check if a test method needs to be created or updated
        boolean isTestMethodValid = ExtentTests.getTest() != null
                && ExtentTests.getTest().getModel().getName().equals(methodName);

        // Create or update the test if necessary
        if (ExtentTests.getTest() == null || !isTestMethodValid) {
            ExtentTests.createOrUpdateTestMethod(iTestResult, false);
        }

    }

    /**
     * Gets the instance of the getPlaywrightManager.
     * This method allows tests to access the BrowserManager for browser interactions.
     *
     * @return the BrowserManager instance
     */
    public PlaywrightManager getPlaywrightManager() {
        return playwrightManager;
    }

    /**
     * Clean up after the tests are complete by closing the page, browser, and Playwright instance.
     * This method is executed once after all the tests in the class have run.
     */
    @AfterClass
    public void tearDown() {
        // Close the currently opened page
        playwrightManager.getPage().close();

        // Close the browser
        playwrightManager.getBrowser().close();

        // Close the Playwright instance to free up resources
        playwrightManager.getPlaywright().close();

        ExtentReporter.extent.flush();

    }
}
