package com.cmccarthyirl.ui.utils;

import com.cmccarthyirl.common.LogManager;
import com.microsoft.playwright.*;

import java.net.URL;
import java.util.List;
import java.util.Properties;

public class PlaywrightManager {

    private Browser browser;
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private static final LogManager log = new LogManager(PlaywrightManager.class);

    /**
     * Initializes the browser if it's not already initialized.
     * Uses the provided properties to set up the browser and its context.
     *
     * @param properties Properties to configure the browser setup.
     */
    public void createBrowser(Properties properties) {
        if (getBrowser() == null) {
            playwrightThreadLocal.set(Playwright.create());
            setLocalWebDriver(properties);  // Setup local browser (chrome/firefox)
        }
    }

    /**
     * Sets up the local WebDriver (browser) based on the properties provided.
     * It supports different browsers like Chrome and Firefox.
     * Sets the browser in headless mode or non-headless as per the properties.
     *
     * @param properties Properties to configure the browser setup.
     */
    public void setLocalWebDriver(Properties properties) {
        String binary = properties.getProperty("binary");
        String browserName = properties.getProperty("browser");
        String headless = properties.getProperty("headless");

        // Choose browser based on the configuration in properties
        switch (browserName) {
            case ("chrome") -> {
                List<String> chromeOptions = List.of(
                        "--start-maximized",
                        "--disable-gpu",
                        "--no-sandbox",
                        "--disable-logging",
                        "--disable-dev-shm-usage"
                );

                if (headless.equals("true")) {
                    // Launch the Chrome browser with options like headless, max resolution, etc.
                    browser = playwrightThreadLocal.get().chromium().launch(new BrowserType.LaunchOptions().setHeadless(true)
                            .setSlowMo(50)  // Slow down interactions by 50 ms
                            .setArgs(chromeOptions));
                } else {
                    // Launch the Chrome browser with options like headless, max resolution, etc.
                    browser = playwrightThreadLocal.get().chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
                            .setSlowMo(50)  // Slow down interactions by 50 ms
                            .setArgs(chromeOptions));
                }


            }
            case ("firefox") -> {
                // Launch the Firefox browser
                browser = playwrightThreadLocal.get()
                        .firefox()
                        .launch(new BrowserType.LaunchOptions().setHeadless(false));
            }
            default -> throw new RuntimeException("Failed to create an instance of WebDriver for: " + browserName);
        }
        log.info("Created a browser for: " + browserName);
        // Create a browser context (isolated session)
        BrowserContext context = browser.newContext();
        // Create a new page within the context
        Page page = context.newPage();

        // Set the page, context, and browser in ThreadLocal storage
        pageThreadLocal.set(page);
        contextThreadLocal.set(context);
        browserThreadLocal.set(browser);
    }

    /**
     * Sets up the remote WebDriver (browser) for remote execution using a hub URL.
     * This is useful for running tests on remote grid setups.
     *
     * @param hubUrl     URL of the remote Selenium or Playwright hub
     * @param properties Properties to configure the browser setup.
     */
    private void setRemoteDriver(URL hubUrl, Properties properties) {
        String binary = properties.getProperty("binary");
        String browserName = properties.getProperty("browser");
        String headless = properties.getProperty("headless");

        // Launch remote browsers based on the configuration in properties
        switch (browserName) {
            case ("chrome") -> {
                browser = playwrightThreadLocal.get()
                        .chromium()
                        .launch(new BrowserType.LaunchOptions().setHeadless(false));
            }
            case ("firefox") -> {
                browser = playwrightThreadLocal.get()
                        .firefox()
                        .launch(new BrowserType.LaunchOptions().setHeadless(false));
            }
            default -> throw new RuntimeException("Failed to create an instance of RemoteWebDriver for: " + binary);
        }

        // Create a browser context (isolated session)
        BrowserContext context = browser.newContext();
        // Create a new page within the context
        Page page = context.newPage();

        // Set the page, context, and browser in ThreadLocal storage
        pageThreadLocal.set(page);
        contextThreadLocal.set(context);
        browserThreadLocal.set(browser);
    }

    /**
     * Gets the Playwright instance (thread-local).
     *
     * @return Playwright instance.
     */
    public Playwright getPlaywright() {
        return playwrightThreadLocal.get();
    }

    /**
     * Gets the current browser instance (thread-local).
     *
     * @return Browser instance.
     */
    public Browser getBrowser() {
        return browserThreadLocal.get();
    }

    /**
     * Gets the current browser context instance (thread-local).
     *
     * @return BrowserContext instance.
     */
    public BrowserContext getBrowserContext() {
        return contextThreadLocal.get();
    }

    /**
     * Gets the current page instance (thread-local).
     *
     * @return Page instance.
     */
    public Page getPage() {
        return pageThreadLocal.get();
    }
}
