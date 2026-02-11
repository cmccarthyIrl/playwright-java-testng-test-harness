package com.cmccarthyirl.api.test;

import com.cmccarthyirl.api.models.WeatherResponse;
import com.cmccarthyirl.common.LogManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class WeatherAPITest extends BaseAPITests {

    // API key for accessing the weather service
    private final String appId = "0a1b11f110d4b6cd43181d23d724cb94";
    private static final LogManager log = new LogManager(WeatherAPITest.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Data provider for city locations to test weather API.
     * @return Array of city names: Dublin and Sydney
     */
    @DataProvider(name = "cityProvider")
    public Object[][] cityProvider() {
        return new Object[][] {
            {"Dublin"},
            {"Sydney"}
        };
    }

    /**
     * Test to get the weather information for different cities.
     * This test makes a GET request to the weather API and checks the response.
     * 
     * @param location The city name to get weather for
     */
    @Test(dataProvider = "cityProvider", description = "Test to get weather information for different cities")
    public void testGetWeatherForCity(String location) throws JsonProcessingException {
        // Make the GET request to the weather API with the location and appid as query parameters
        APIResponse response = weatherContext().get("/data/2.5/weather",
                RequestOptions.create()
                        .setQueryParam("q", location)  // Query parameter for location
                        .setQueryParam("appid", appId));  // Query parameter for the API key (appid)

        // Print the response body to the console (useful for debugging)
        log.info("The weather for " + location + " is: " + response.text());

        // Assert that the response status is OK (200-299 range)
        Assert.assertTrue(response.ok(), "Response should be OK");

        // Assert that the status code is 200, meaning the request was successful
        Assert.assertEquals(response.status(), 200, "Status code should be 200");

        // Parse the response body and assert that it contains the correct location
        WeatherResponse weather =
        objectMapper.readValue(response.text(), WeatherResponse.class);

        Assert.assertEquals(weather.getName(), location, "Weather response should contain the correct location name");
        Assert.assertNotNull(weather.getMain());
        Assert.assertTrue(weather.getMain().getTemp() > 0);
    }
}
