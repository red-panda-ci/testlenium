package com.testlenium.web.site.pages;

import com.github.webdriverextensions.Bot;
import com.github.webdriverextensions.WebDriverExtensionFieldDecorator;
import com.github.webdriverextensions.WebDriverExtensionsContext;
import com.github.webdriverextensions.WebRepository;
import com.testlenium.web.misc.CustomBot;
import com.testlenium.web.site.Locators;
import lombok.extern.log4j.Log4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import static com.testlenium.web.Site.site;

@Log4j
public class GoogleElements extends WebRepository {

    @FindBy(id = Locators.GoogleLocators.LOGO_ID)
    public WebElement logo;

    @FindBy(name = Locators.GoogleLocators.SEARCH_NAME)
    public WebElement search;

    public GoogleElements(){
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(WebDriverExtensionsContext.getDriver()),
                this);
    }

    public static void fillSearchField(String text) {
        CustomBot.scrollToAndClick(site().googleElements.search);
        CustomBot.sendKeys(site().googleElements.search, text);
    }

    public static void isGoogleReady() {
        Bot.waitForElementToDisplay(site().googleElements.logo);
        Assert.assertTrue(Bot.isDisplayed(site().googleElements.logo, 10),
                "The logo element is not present.");
        Assert.assertTrue(Bot.isDisplayed(site().googleElements.search, 10),
                "The search element is not present.");
    }
}