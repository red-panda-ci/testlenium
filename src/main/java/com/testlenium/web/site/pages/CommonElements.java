package com.testlenium.web.site.pages;

import com.github.webdriverextensions.WebDriverExtensionFieldDecorator;
import com.github.webdriverextensions.WebDriverExtensionsContext;
import com.github.webdriverextensions.WebRepository;
import com.testlenium.web.site.Locators;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CommonElements extends WebRepository {

    @FindBy(xpath = Locators.CommonLocators.BODY_XPATH)
    public WebElement body;

    public CommonElements(){
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(WebDriverExtensionsContext.getDriver()),
                this);
    }
}
