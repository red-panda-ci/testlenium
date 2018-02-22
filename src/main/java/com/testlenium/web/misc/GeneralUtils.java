package com.testlenium.web.misc;

import lombok.extern.log4j.Log4j;

import static com.github.webdriverextensions.Bot.assertTextContains;
import static com.github.webdriverextensions.Bot.waitFor;
import static com.testlenium.web.Site.site;

@Log4j
public class GeneralUtils {
    private GeneralUtils() { }

    /**
     * Assertion for text in page
     *
     * @param text
     */
    public static void assertPageText(String text) {
        int wait = 0;
        while (!(site().getCommonElements().body.getText()).contains(text) && wait < 10) {
            waitFor(1);
            wait++;
        }
        assertTextContains(text, site().getCommonElements().body);
        CustomBot.setZaleniumMessage("The page must contains " + text + " text");
    }

}
