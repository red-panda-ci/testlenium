package com.testlenium.web.site;

/**
 * Promotion data set Object
 */
public class DataSet {
    private String title;
    private String url;

    public DataSet(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public DataSet() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
