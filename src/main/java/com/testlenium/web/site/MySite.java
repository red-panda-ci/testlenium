package com.testlenium.web.site;

import com.github.webdriverextensions.Bot;
import com.github.webdriverextensions.WebDriverExtensionFieldDecorator;
import com.github.webdriverextensions.WebDriverExtensionsContext;
import com.github.webdriverextensions.WebSite;
import com.testlenium.web.misc.CustomBot;
import com.testlenium.web.site.pages.CommonElements;
import com.testlenium.web.site.pages.GoogleElements;
import lombok.extern.log4j.Log4j;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.PageFactory;

import java.io.IOException;

import static com.github.webdriverextensions.Bot.assertCurrentUrlStartsWith;

@Log4j
public class MySite extends WebSite {

    // Url
    private String url;

    // Pages for POM
    private CommonElements commonElements;
    public GoogleElements googleElements;

    //test data
    private String name;
    private DataSet dataSet;

    public void openSite(String page) {
        setUrl(page);
        url = dataSet.getUrl();
        open(url);
    }

    @Override
    public void open(Object... arguments) {
        Bot.waitUntil(input -> (Boolean) ((JavascriptExecutor) WebDriverExtensionsContext.getDriver()).executeScript
                ("return (window.jQuery != null) && (jQuery.active === 0);"), 90);
        assertIsOpen();
        CustomBot.setZaleniumMessage("As a user I go to: " + name + " page");
    }

    @Override
    public void assertIsOpen(Object... arguments) throws Error {
        assertCurrentUrlStartsWith(url);
    }

    public MySite() {
        PageFactory.initElements(new WebDriverExtensionFieldDecorator(WebDriverExtensionsContext.getDriver()), this);
    }

    public void setUrl(String page) {
        name = page;
        try {
            dataSet = DataSetReader.reader(name);
        } catch (ParseException | IOException e) {
            log.error("Ny error: " + e);
        }
        url = dataSet.getUrl();
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public CommonElements getCommonElements() {
        return commonElements;
    }

}
