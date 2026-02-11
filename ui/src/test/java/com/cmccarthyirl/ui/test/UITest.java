package com.cmccarthyirl.ui.test;

import com.cmccarthyirl.common.LogManager;
import com.cmccarthyirl.ui.page.SearchPage;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class UITest extends BaseUITests {

    private SearchPage searchPage;
    private static final LogManager log = new LogManager(UITest.class);

    /**
     * This test searches for an exact book title on the automation bookstore.
     * It navigates to the site, performs a search for the given title, and validates
     * that the correct book appears in the search results.
     */
    @Test
    public void searchForExactTitle() {
        // Initialize the SearchPage object with the driver manager
        searchPage = new SearchPage(getPlaywrightManager());

        // Define the title we are searching for
        String title = "Agile Testing";

        // Navigate to the automation bookstore's homepage
        searchPage.getPage().navigate("https://automationbookstore.dev/");
        log.info("The user navigated to: https://automationbookstore.dev/");
        // Perform the search for the book title
        searchPage.search(title);
        log.info("The search results contained: " + searchPage.getNumberOfVisibleBooks() + " book");
        // Validate that exactly 1 book is visible in the search results
        assertEquals(searchPage.getNumberOfVisibleBooks(), 1, "Number of visible books");
        log.info("The book's title is: " + searchPage.getVisibleBooks().toString());
        // Validate that the visible book's title matches the searched title
        assertTrue(searchPage.getVisibleBooks().contains(title), "Title of visible book");
    }

    /**
     * This test searches for books with partial titles and validates that the correct
     * list of books appears in the search results. It performs a search for the term "Test"
     * and compares the actual search results with an expected list of book titles.
     */
    @Test
    public void searchForPartialTitle() {
        // Initialize the SearchPage object with the driver manager
        searchPage = new SearchPage(getPlaywrightManager());

        // Navigate to the automation bookstore's homepage
        searchPage.getPage().navigate("https://automationbookstore.dev/");
        log.info(" The user navigated to: https://automationbookstore.dev/");
        // Perform the search for the partial title "Test"
        searchPage.search("Test");

        // Define the expected list of book titles based on the search term
        List<String> expectedBooks = List.of(
                "Test Automation in the Real World",
                "Experiences of Test Automation",
                "Agile Testing",
                "How Google Tests Software",
                "Java For Testers"
        );

        log.info("Verify the expected books were returned: " + expectedBooks);
        // Validate that the number of visible books matches the expected number
        assertEquals(searchPage.getNumberOfVisibleBooks(), expectedBooks.size(), "Number of visible books");

        // Validate that the visible books match the expected titles
        assertEquals(searchPage.getVisibleBooks(), expectedBooks, "Titles of visible books");
    }
}
