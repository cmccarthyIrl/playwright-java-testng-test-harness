package com.cmccarthyirl.common;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Test;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExtentTests {

    // Map to store ExtentTest objects by class name
    private static final Map<String, ExtentTest> classTestMap = new HashMap<>();

    // ThreadLocal variables to store ExtentTest objects specific to each thread (method and data provider)
    private static final ThreadLocal<ExtentTest> methodTest = new ThreadLocal<>();
    private static final ThreadLocal<ExtentTest> dataProviderTest = new ThreadLocal<>();

    // Get the current test, first checking dataProviderTest, then methodTest
    public static ExtentTest getTest() {
        return Optional.ofNullable(dataProviderTest.get()).orElse(methodTest.get());
    }

    // Get the appropriate test (method or data provider) based on the ITestResult
    public static ExtentTest getTest(ITestResult result) {
        // If the test has parameters (i.e., data provider is used), return dataProviderTest; otherwise, return methodTest
        return (result.getParameters() != null && result.getParameters().length > 0)
                ? dataProviderTest.get()
                : methodTest.get();
    }

    // Create or update the test based on the result. If 'createAsChild' is true, create a child test under the class test.
    public static void createOrUpdateTestMethod(ITestResult result, boolean createAsChild) {
        if (createAsChild) {
            // Create test with class hierarchy (i.e., method test under the class test)
            createTestWithClassHierarchy(result);
            return;
        }
        // Create or update test without class hierarchy
        createOrUpdateTestMethod(result);
    }

    // Create a test under the class test (hierarchical structure)
    private static void createTestWithClassHierarchy(ITestResult result) {
        String className = result.getTestContext().getName();
        String methodName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        // Check if a class-level test already exists, if not, create it
        ExtentTest classTest = classTestMap.computeIfAbsent(className,
                key -> ExtentReporter.extent.createTest(className, description));

        // Check if method-level test already exists for the class test
        Optional<Test> existingMethodTest = classTest.getModel().getChildren().stream()
                .filter(test -> test.getName().equals(methodName))
                .findFirst();

        // If there are parameters (data provider), create the parameterized test
        if (result.getParameters().length > 0) {
            // If method test does not exist, create it
            if (existingMethodTest.isEmpty()) {
                createTest(result, classTest);
            }
            // Create a test node for the data provider parameters
            String paramName = Arrays.toString(result.getParameters());
            ExtentTest paramTest = methodTest.get().createNode(paramName);
            dataProviderTest.set(paramTest);
        } else {
            // If no parameters, remove the data provider test and create/update the method test
            dataProviderTest.remove();
            createTest(result, classTest);
        }

        methodTest.get(); // Ensures that the methodTest is properly initialized
    }

    // Create or update the test method without a class hierarchy
    private static void createOrUpdateTestMethod(ITestResult result) {
        String methodName = result.getMethod().getMethodName();

        // If there are parameters, create/update test method with parameters (data provider test)
        if (result.getParameters().length > 0) {
            // If methodTest does not exist or its name does not match, create a new test
            if (methodTest.get() == null || !methodTest.get().getModel().getName().equals(methodName)) {
                createTest(result, null);
            }
            // Create a test node for the data provider parameters
            String paramName = Arrays.toString(result.getParameters());
            ExtentTest paramTest = methodTest.get().createNode(paramName);
            dataProviderTest.set(paramTest);
        } else {
            // Remove data provider test if no parameters and create/update the method test
            dataProviderTest.remove();
            createTest(result, null);
        }

        methodTest.get(); // Ensures that the methodTest is properly initialized
    }

    // Create a new ExtentTest (either method-level or class-level)
    private static void createTest(ITestResult result, ExtentTest classTest) {
        String methodName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        // If classTest is provided, create a node under the class test; otherwise, create a top-level test
        ExtentTest test = (classTest != null)
                ? classTest.createNode(methodName, description)
                : ExtentReporter.extent.createTest(methodName, description);

        // Set the current methodTest to the created test
        methodTest.set(test);
        // Assign groups to the test (e.g., device, author, tag)
        assignGroups(test, result.getMethod().getGroups());
    }

    // Assign groups to a test (groups could represent devices, authors, categories, etc.)
    private static void assignGroups(ExtentTest test, String[] groups) {
        if (groups != null && groups.length > 0) {
            Arrays.stream(groups).forEach(group -> {
                // Check if the group represents a device, author, or category and assign accordingly
                if (group.startsWith("d:") || group.startsWith("device:")) {
                    test.assignDevice(group.replaceAll("d:|device:", ""));
                } else if (group.startsWith("a:") || group.startsWith("author:")) {
                    test.assignAuthor(group.replaceAll("a:|author:", ""));
                } else if (group.startsWith("t:") || group.startsWith("tag:")) {
                    test.assignCategory(group.replaceAll("t:|tag:", ""));
                } else {
                    // Assign other groups as categories
                    test.assignCategory(group);
                }
            });
        }
    }

    // Map TestNG status code to ExtentReports status (PASS, FAIL, SKIP)
    private static Status mapStatus(int statusCode) {
        switch (statusCode) {
            case ITestResult.FAILURE:
                return Status.FAIL;
            case ITestResult.SUCCESS:
                return Status.PASS;
            default:
                return Status.SKIP;
        }
    }
}
