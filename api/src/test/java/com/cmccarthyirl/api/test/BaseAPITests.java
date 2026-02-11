package com.cmccarthyirl.api.test;

import com.cmccarthyirl.api.utils.RequestContext;
import com.cmccarthyirl.common.*;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

@Listeners({TestListener.class})
public class BaseAPITests extends RequestContext {

    private final MDCModel mdcModel = new MDCModel();
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Properties> propertiesThreadLocal = new ThreadLocal<>();

    /**
     * The setup method that runs before all tests in the suite.
     * It initializes the Playwright instance, which is necessary for interacting with the browser or APIs.
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() throws IOException {
        // Playwright instance used for managing browser and API interactions
        playwrightThreadLocal.set(Playwright.create());

        // Load the configuration properties (like browser settings, headless mode, etc based on the maven profile)
        propertiesThreadLocal.set(new ReadPropertyFile().loadProperties("./" + System.getProperty("config.file", "default-config.properties")));
    }

    /**
     * The teardown method that runs after all tests in the suite.
     * It disposes of the API request context and closes the Playwright instance.
     */
    @AfterSuite(alwaysRun = true)
    public void afterAll() {
        closePlaywright();
        ExtentReporter.extent.flush();
    }

    /**
     * This method closes the Playwright instance, if it exists.
     * It ensures that any resources associated with Playwright are properly cleaned up.
     */
    protected void closePlaywright() {
        if (playwrightThreadLocal.get() != null) {
            playwrightThreadLocal.get().close();
            playwrightThreadLocal.remove();
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method, ITestContext context, ITestResult iTestResult) {
        String methodName = method.getName();
        String logFileName = "API-" + methodName;

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
     * A helper method to return the weather-related API request context.
     * It utilizes the `getWeatherAPIContext` method (inherited from `RequestContext`) to get the specific API context.
     *
     * @return the API request context for the weather API
     */
    public APIRequestContext weatherContext() {
        return getWeatherAPIContext(playwrightThreadLocal.get(), propertiesThreadLocal.get());
    }
}

