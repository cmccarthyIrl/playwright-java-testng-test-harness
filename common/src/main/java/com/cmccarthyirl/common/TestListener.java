package com.cmccarthyirl.common;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static final LogManager logger = new LogManager(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("============= Starting test: " + result.getName() + " =============");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("============= Passed test: " + result.getName() + " ==============");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("============= Failed test: " + result.getName() + " ==============");
        logger.error("============= " + result.getThrowable() + " ==============");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("============= Skipped test: " + result.getName() + " ==============");
        logger.warn("============= " + result.getThrowable() + " ==============");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }
}
