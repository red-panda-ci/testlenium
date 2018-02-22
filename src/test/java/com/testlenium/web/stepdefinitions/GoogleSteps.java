package com.testlenium.web.stepdefinitions;

import com.testlenium.web.site.pages.GoogleElements;
import cucumber.api.java8.En;
import lombok.extern.log4j.Log4j;

import static com.testlenium.web.Site.site;


@Log4j
public class GoogleSteps implements En {
    public GoogleSteps() {
        Given("^the (.*) page$", GoogleSteps::openGooglePage);
        When("^the google page is full loaded$", GoogleSteps::isGoogleReady);
        Then("^I can fill the search element with (.*)$", GoogleSteps::fillSearchField);
    }

    private static void fillSearchField(String text) {
        GoogleElements.fillSearchField(text);
    }

    private static void isGoogleReady() {
        GoogleElements.isGoogleReady();
    }

    private static void openGooglePage(String page) {
        site().openSite(page);
        log.info("The " + site().getDataSet().getUrl() + " page is opened.");
    }
}
