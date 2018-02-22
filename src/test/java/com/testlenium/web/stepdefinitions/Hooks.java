package com.testlenium.web.stepdefinitions;

import com.github.webdriverextensions.WebDriverExtensionsContext;
import com.testlenium.web.config.ConfigRunner;
import com.testlenium.web.site.MySite;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import lombok.extern.log4j.Log4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static com.testlenium.web.Site.setSite;
import static com.testlenium.web.config.ConfigRunner.getSeleniumGrid;
import static com.testlenium.web.config.ConfigRunner.initFeature;


@Log4j
public class Hooks {

    private String browser;
    private static SessionId sessionId;

    @Before()
    public void setUp(Scenario scenario) throws Exception {
        log.info("Initiating test configuration");
        getBrowser(scenario);
        initFeature(browser);
        browser = ConfigRunner.getBrowser();
        initFeature(browser);
        DesiredCapabilities capabilities = new DesiredCapabilities(ConfigRunner.getBrowser(), "", Platform.ANY);
        capabilities.setCapability("idleTimeout", 60);
        capabilities.setCapability("name", scenario.getName());
        if (ConfigRunner.getGridEnviroment().equals("browserstack"))
            capabilities = setBrowserStack(capabilities, ConfigRunner.getOs() ,"");
        RemoteWebDriver rw = new RemoteWebDriver(getSeleniumGrid(), capabilities);
        WebDriverExtensionsContext.setDriver(rw);
        sessionId = rw.getSessionId();
        setSite(new MySite());
        log.info("Launching Scenario: " + scenario.getName());
        log.info("With sessionId: " + sessionId);
    }

    @After()
    public void tearDown(Scenario scenario) throws IOException, URISyntaxException {
        log.info("Closing Webdriver");
        TestReport(browser);
        zaleniumTestPassedFaild(scenario);
        broserTestPassedFaild();
        WebDriverExtensionsContext.getDriver().quit();
        WebDriverExtensionsContext.removeDriver();
    }


    /**
     * Mark test in Zalenium as failed or passed
     *
     * @param scenario
     * @return
     */

    public void zaleniumTestPassedFaild(Scenario scenario) {
        Cookie cookie = null;
        log.info("Scenario: \"" + scenario.getName() + "\" has been " + scenario.getStatus());
        if (scenario.getStatus().equals("passed"))
            cookie = new Cookie("zaleniumTestPassed", "true");
        if (scenario.getStatus().equals("failed"))
            cookie = new Cookie("zaleniumTestPassed", "false");

        WebDriverExtensionsContext.getDriver().manage().addCookie(cookie);
    }

    /**
     * Get browser in case the user set the browser from the scenario
     * @param scenario
     * @return
     */
    public String getBrowser(Scenario scenario){
        browser = "";
        if (scenario.getSourceTagNames().toString().contains("firefox"))
            browser = "firefox";
        else if (scenario.getSourceTagNames().toString().contains("chrome"))
            browser = "chrome";
        return browser;
    }

    /**
     * Set Capabilities for  broserstack
     * @param caps
     * @param os
     * @param browser
     * @return
     */
    public DesiredCapabilities setBrowserStack( DesiredCapabilities caps, String os, String browser){

        switch (os){
            case "android":
                log.info("Setting android capabilites for browserStack.");
                caps.setCapability("os_version", "7.0");
                caps.setCapability("device", "Samsung Galaxy S8");
                caps.setCapability("real_mobile", "true");
                break;
            case "ios":
                log.info("Setting IOS capabilites for browserStack.");
                caps.setCapability("os_version", "10.3");
                caps.setCapability("device", "iPhone 7");
                caps.setCapability("real_mobile", "true");
                break;
            case "win":
                log.info("Setting Win10 capabilites for browserStack.");
                caps.setCapability("os", "Windows");
                caps.setCapability("os_version", "10");
                break;
            case "osx":
                log.info("Setting OS X High Sierra capabilites for browserStack.");
                caps.setCapability("os", "OS X");
                caps.setCapability("os_version", "High Sierra");
                break;
        }

        switch (browser){
            case "ie":
                caps.setCapability("browser", "IE");
                break;
            case "edge":
                caps.setCapability("browser", "Edge");
                break;
            case "chrome":
                caps.setCapability("browser", "Chrome");
                break;
            case "firefox":
                caps.setCapability("browser", "Firefox");
                break;
            case "shafari":
                caps.setCapability("browser", "Safari");
                break;
            case "opera":
                caps.setCapability("browser", "Opera");
                break;
        }

        caps.setCapability("browserstack.networkLogs", "true");
        caps.setCapability("browserstack.debug", true);
        caps.setCapability("project", "YourProject");
        caps.setCapability("browserstack.selenium_version", "3.5.2");

        return caps;
    }

    /**
     * Rename Json test result
     * @param browser
     */
    public void TestReport (String browser) {
        String Path = "ci-scripts/reports/cucumber-extentsreport/";
        File f = new File(Path);
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().toString().equals(String.valueOf(i + 1) + ".json")) {
                files[i].renameTo(new File(Path + browser + files[i].getName()));
            }
        }
    }
    /**
      Mark browserstack test
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void broserTestPassedFaild() throws URISyntaxException, UnsupportedEncodingException, IOException {
        URI uri = new URI("https://" + ConfigRunner.getBrowserStackUser() + ":" + ConfigRunner.getBrowserStackKey()
                + "@www.browserstack.com/automate/sessions/" + sessionId + ".json");
        HttpPut putRequest = new HttpPut(uri);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add((new BasicNameValuePair("status", "passed")));
        nameValuePairs.add((new BasicNameValuePair("reason", "")));
        putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpClientBuilder.create().build().execute(putRequest);
    }
}
