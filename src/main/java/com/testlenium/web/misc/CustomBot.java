package com.testlenium.web.misc;

import com.github.webdriverextensions.Bot;
import com.github.webdriverextensions.WebDriverExtensionsContext;
import com.testlenium.web.config.ConfigRunner;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.testlenium.web.Site.site;

@Log4j
public class CustomBot extends Bot {

    /**
     * Wait for element while is not clickable
     *
     * @param webElement
     */
    public static void waitForElementToBeClickable(WebElement webElement) {
        waitForElementToBeClickable(webElement, 30);
    }

    /**
     * Wait for element while is not clickable
     *
     * @param webElement
     * @param secondsToWait
     */
    public static void waitForElementToBeClickable(WebElement webElement, long secondsToWait) {
        WebDriverWait wait = new WebDriverWait(driver(), secondsToWait);
        wait.until(ExpectedConditions.elementToBeClickable(webElement));
    }


    /**
     * Return true if text is present in the page
     *
     * @param searchText
     * @return
     */
    public static boolean isTextPresent(String searchText) {
        return site().getCommonElements().body.getText().contains(searchText);
    }

    /**
     * Assertion for page text
     *
     * @param searchText
     */
    public static void assertPageContainsText(String searchText) {
        assertTextContains(searchText, site().getCommonElements().body);
    }

    /**
     * Click on a button with the argument text
     *
     * @param buttonText
     */
    public static void clickOnButtonWithText(String buttonText) {
        driver().findElement(By.xpath("//button[contains(.,'" + buttonText + "')]")).click();
    }

    /**
     * Find element with the argument text
     *
     * @param buttonText
     */
    public static WebElement findElementWithText(String buttonText) {
        log.info("Buscamos el elemento: " + buttonText);
        return driver().findElement(By.xpath("//*[contains(.,'" + buttonText + "')]"));
    }

    /**
     * Check if element is present return true
     * wait equal to 0 for default time to wait
     *
     * @param wEle
     * @param wait
     * @return
     */
    public static Boolean isElementPresent(WebElement wEle, int wait) {
        Bot.waitFor(2);
        if (wait <= 0) wait = 30;
        try {
            Bot.waitForElementToDisplay(wEle, wait);

            return true;
        } catch (Exception e) {
            log.info("Element" + wEle + "not present." + e);
            return false;
        }
    }

    /**
     * Scroll to a specific WebElement and then clicks
     *
     * @param wEle
     */
    public static void scrollToAndClick(WebElement wEle) {
        waitFor(2);
        waitForElementToBeClickable(wEle);
        scrollTo(wEle);
        click(wEle);
    }

    /**
     * Scroll to a specific WebElement and then clicks
     *
     * @param wEle
     */
    public static void scrollToAndSubmit(WebElement wEle) {
        waitForElementToDisplay(wEle);
        Bot.scrollTo(wEle);
        wEle.submit();
    }

    /**
     * Scroll to a specific WebElement and the fill[arg1]
     *
     * @param wEle
     * @param arg1
     */
    public static void sendKeys(WebElement wEle, String arg1) {
        scrollTo(wEle);
        waitForElementToDisplay(wEle);
        wEle.sendKeys(arg1);
    }

    /**
     * Select an option with value [option] from a select element
     * the element must be an xpath or Id  or class type
     *
     * @param wEle
     * @param option
     */
    public static void selectwithOptionValue(WebElement wEle, String option) {
        MyWebElement myWebElement = new MyWebElement(wEle);
        String element = "";
        switch (myWebElement.getTypeSelector()) {
            case "id":
                option = "']/option[@value='" + option + "']";
                element = "//*[@" + myWebElement.getTypeSelector() + "='" + myWebElement.getSelector() + option;
                break;
            case "xpath":
                option = "/option[@value='" + option + "']";
                element = myWebElement.getSelector() + option;
                log.info("Element: " + element);
                break;
            case "class":
                break;
            default:
                log.error("The selected element does not mach, choose other element type.");
                break;
        }
        log.info("Select element: " + element);
        scrollToAndClick(driver().findElement(By.xpath(element)));
    }

    /**
     * Switch to iframe
     *
     * @param wEle
     */
    public static void switchToIframe(WebElement wEle) {
        driver().switchTo().frame(wEle);
    }

    /**
     * Wait until JQuery is active
     */
    public static void wait4JQuery (){
        waitUntil(input -> (Boolean) ((JavascriptExecutor) input).executeScript("return jQuery.active == 0"), 30);
    }

    /**
     * Set zaleniumMessage cookie for video
     * @param text
     */
    public static void setZaleniumMessage(String text){
        if (ConfigRunner.getVideolog().equals("true") ) {
            Cookie cookie = new Cookie("zaleniumMessage", text);
            WebDriverExtensionsContext.getDriver().manage().addCookie(cookie);
        }
        log.info(text);
    }

    /**
     * Press on TAB Key
     * @param webElement
     */
    public static void sendKeysTab(WebElement webElement){
        webElement.sendKeys(Keys.TAB);
    }
}