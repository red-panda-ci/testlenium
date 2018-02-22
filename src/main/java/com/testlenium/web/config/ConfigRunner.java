package com.testlenium.web.config;

import lombok.NonNull;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

@Log4j
public class ConfigRunner {

    private ConfigRunner() {
    }

    static URL seleniumGrid;
    static String browser;
    static Integer webDriverWait;
    static String vLog;
    static String gEnvironment;
    static String target;

    static String username;
    static String automatekey;

    static String browserName = "browser_name";
    static String videoLog = "video_log";
    static String gridEnvironment = "grid_environment";
    static String targetProp = "target";

    public static void initFeature(String browser) throws IOException {

        String filename = "config.properties";
        InputStream input = ConfigRunner.class.getClassLoader().getResourceAsStream(filename);
        if (input == null) {
            log.fatal("Sorry, unable to find " + filename);
            return;
        }
        Properties prop = new Properties();
        prop.load(input);
        setBroserStack(prop);
        setVideoLog(prop);
        setSeleniumGrid(prop);
        if (browser.equals(""))
            setBrowser(prop);
        else
            setBrowser(browser);
        setOs(prop);
        setWebDriverWait(prop);
    }

    /**
     * Set browser from properties
     *
     * @param prop
     */
    private static void setBrowser(Properties prop) {
        String property = prop.getProperty(browserName);
        if (System.getProperty(browserName) != null) {
            property = System.getProperty(browserName);
        }
        browser = property;
        log.info("Browser: " + browser);
    }

    /**
     * Set propertie for Zalenium  video log
     *
     * @param prop
     */
    private static void setVideoLog(Properties prop) {
        String property = prop.getProperty(videoLog);
        if (System.getProperty(videoLog) != null) {
            property = System.getProperty(videoLog);
        }
        vLog = property;
    }

    /**
     * get video_log propertie
     *
     * @return
     */
    public static String getVideolog() {
        return vLog;
    }

    /**
     * Overdrive broser from scenario configuration
     *
     * @param myBrowser
     */
    public static void setBrowser(String myBrowser) {
        browser = myBrowser;
        log.info("Browser: " + browser);
    }

    private static void setWebDriverWait(Properties prop) {
        webDriverWait = Integer.valueOf(prop.getProperty("webDriverWait"));
        log.info("Web Driver Wait: " + webDriverWait + " seconds");

    }

    /***
     *
     * @throws MalformedURLException
     * @param prop
     */
    private static void setSeleniumGrid(Properties prop) throws MalformedURLException {
        setGridEnviroment(prop);
        log.info("Grid_environment execution: " + gEnvironment);
        switch (gEnvironment) {
            case "local":
                seleniumGrid = new URL(
                        System.getProperty("grid_protocol", "http"),
                        System.getProperty("grid_host", "localhost"),
                        Integer.valueOf(System.getProperty("grid_port", "4444")),
                        "/wd/hub"
                );
                break;
            case "browserstack":
                seleniumGrid = new URL("https://" + getBrowserStackUser() + ":" + getBrowserStackKey() + "@hub-cloud.browserstack.com/wd/hub");
                break;
            default:
                seleniumGrid = setRemoteGrid(prop.getProperty("grid_protocol"), prop.getProperty("grid_host"), prop.getProperty("grid_port"));
                break;
        }
        log.info("seleniumGrid: " + seleniumGrid);
    }

    /***
     *
     * @param gridProtocol
     * @param gridHost
     * @param gridPort
     * @return
     * @throws MalformedURLException
     */
    private static URL setRemoteGrid(@NonNull String gridProtocol, @NonNull String gridHost, @NonNull String gridPort) throws MalformedURLException {
        return new URL(gridProtocol, gridHost, Integer.valueOf(gridPort), "/wd/hub");

    }

    public static void setGridEnviroment(Properties prop) {
        gEnvironment = (System.getProperty(gridEnvironment) != null) ? System.getProperty(gridEnvironment) : prop.getProperty(gridEnvironment);
    }


    public static String getGridEnviroment() {
        return gEnvironment;
    }

    public static String getBrowser() {
        log.info(browser);
        return browser;
    }

    /**
     * get selenium grid
     *
     * @return
     */
    public static URL getSeleniumGrid() {
        return seleniumGrid;
    }

    /**
     * set OS for browserStack
     *
     * @param prop
     */
    public static void setOs(Properties prop) {
        target = (System.getProperty(targetProp) != null) ? System.getProperty(targetProp) : prop.getProperty(targetProp);
    }

    /**
     * return OS for browserStack
     *
     * @return
     */
    public static String getOs() {
        return target;
    }

    /**
     * SetUsername and password for BrowserStack
     *
     * @param prop
     */
    private static void setBroserStack(Properties prop) {
        username = prop.getProperty("username");
        automatekey = prop.getProperty("automate_key");
    }

    /**
     * return BrowserStack Key
     *
     * @return
     */
    public static String getBrowserStackKey() {
        return automatekey;
    }

    /**
     * return BroserStack user
     *
     * @return
     */
    public static String getBrowserStackUser() {
        return username;
    }
}

