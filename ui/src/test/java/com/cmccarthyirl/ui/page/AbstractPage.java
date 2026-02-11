package com.cmccarthyirl.ui.page;

import com.cmccarthyirl.ui.utils.PlaywrightManager;
import com.microsoft.playwright.Page;

public abstract class AbstractPage {

    // Instance of BrowserManager to manage browser interactions for the page
    private final PlaywrightManager playwrightManager;

    /**
     * Constructor for AbstractPage class, initializing the BrowserManager.
     * This constructor is used by subclasses to initialize the page with the provided BrowserManager.
     *
     * @param playwrightManager the BrowserManager instance used to interact with the browser
     */
    protected AbstractPage(PlaywrightManager playwrightManager) {
        // Initialize the browserManager field with the provided BrowserManager instance
        this.playwrightManager = playwrightManager;
    }

    /**
     * Gets the current Page object from the BrowserManager.
     * This method is used to get the current page being interacted with in the browser.
     *
     * @return the current Page object managed by the BrowserManager
     */
    public Page getPage() {
        return playwrightManager.getPage();
    }

}
