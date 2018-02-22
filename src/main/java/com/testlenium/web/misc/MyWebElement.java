package com.testlenium.web.misc;

import org.openqa.selenium.WebElement;

/**
 * Custom webelement
 */
public class MyWebElement {

    private WebElement webElement;
    private String selector;
    private String typeSelector;

    /**
     * Get the strings for the seletector and typeSelector from the webElement to String method
     *
     * @param webElement
     */
    public MyWebElement(WebElement webElement) {
        this.webElement = webElement;
        typeSelector = webElement.toString().substring(webElement.toString().lastIndexOf("->") + 3, (webElement.toString().lastIndexOf(']')));
        selector = typeSelector;
        selector = selector.substring(typeSelector.indexOf(':') + 1, selector.length()).trim();
        typeSelector = typeSelector.substring(0, typeSelector.indexOf(':')).trim();
    }

    /**
     * return webElement
     *
     * @return
     */
    public WebElement getWebElement() {
        return webElement;
    }

    /**
     * Return string selector
     *
     * @return
     */
    public String getSelector() {
        return selector;
    }

    /**
     * Return the selector type xpath, id ...
     *
     * @return
     */
    public String getTypeSelector() {
        return typeSelector;
    }

}
