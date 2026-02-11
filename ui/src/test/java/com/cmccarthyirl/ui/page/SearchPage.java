package com.cmccarthyirl.ui.page;

import com.cmccarthyirl.common.LogManager;
import com.cmccarthyirl.ui.utils.PlaywrightManager;
import com.microsoft.playwright.Page;

import java.util.List;
import java.util.stream.Collectors;

import static com.microsoft.playwright.options.WaitForSelectorState.ATTACHED;
import static com.microsoft.playwright.options.WaitForSelectorState.DETACHED;

public class SearchPage extends AbstractPage {

    // The page object used to interact with the current browser page
    private final Page page;

    // Locators for the search bar and various book elements
    private final String locator_searchBar = "#searchBar";
    private final String locator_hiddenBooks = "li.ui-screen-hidden";
    private String locator_visibleBooks = "li:not(.ui-screen-hidden)";
    private String locator_visibleBookTitles = "li:not(.ui-screen-hidden) h2";
    private static final LogManager log = new LogManager(SearchPage.class);

    /**
     * Constructor for SearchPage class.
     * Initializes the page object by calling the parent constructor and getting the current page.
     *
     * @param playwrightManager the BrowserManager instance used to interact with the browser
     */
    public SearchPage(PlaywrightManager playwrightManager) {
        super(playwrightManager);  // Call parent constructor to initialize BrowserManager
        page = getPage();  // Get the current page object from BrowserManager
    }

    /**
     * Performs a search on the page by entering the query into the search bar and waiting for the search results.
     *
     * @param query the search term entered into the search bar
     */
    public void search(String query) {
        clearSearchBar();  // Clear any existing search term in the search bar
        page.fill(locator_searchBar, query);  // Fill the search bar with the given query
        log.info("The user searched for " + query);
        // Wait for the hidden books to be attached to the DOM, indicating the search results are being updated
        var expectedState = new Page.WaitForSelectorOptions().setState(ATTACHED);
        page.waitForSelector(locator_hiddenBooks, expectedState);
    }

    /**
     * Clears the search bar by filling it with an empty string and waits for the search results to disappear.
     */
    public void clearSearchBar() {
        page.fill(locator_searchBar, "");  // Clear the search bar
        log.info("The user cleared the search field");
        var expectedState = new Page.WaitForSelectorOptions().setState(DETACHED);
        page.waitForSelector(locator_hiddenBooks, expectedState);  // Wait for the hidden books to be detached from the DOM
    }

    /**
     * Returns the number of visible books in the search results.
     *
     * @return the count of visible books
     */
    public int getNumberOfVisibleBooks() {
        // Query for all visible books and return the size of the result
        return page.querySelectorAll(locator_visibleBooks).size();
    }

    /**
     * Returns a list of titles of the visible books in the search results.
     *
     * @return a list of visible book titles
     */
    public List<String> getVisibleBooks() {
        // Query for all visible book titles, extract their inner text, and return it as a list
        return page.querySelectorAll(locator_visibleBookTitles)
                .stream()
                .map(e -> e.innerText())  // Extract the inner text (title) from each book element
                .collect(Collectors.toList());  // Collect the titles into a list
    }
}
