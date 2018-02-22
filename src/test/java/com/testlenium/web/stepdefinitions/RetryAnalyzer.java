package com.testlenium.web.stepdefinitions;

import lombok.extern.log4j.Log4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

@Log4j
public class RetryAnalyzer implements IRetryAnalyzer {

    private static InheritableThreadLocal<Integer> retryCount = new InheritableThreadLocal<>();

    @Override
    public boolean retry(ITestResult result) {
        Boolean needRetry = false;
        if (retryCount.get() == null)
            retryCount.set(0);
        if (!result.isSuccess()) {
            int maxRetryCount = 1;
            if (retryCount.get() < maxRetryCount) {
                retryCount.set(retryCount.get() + 1);
                needRetry = true;
            }
        }
        return needRetry;
    }
}