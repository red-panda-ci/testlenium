package com.testlenium.web;

import com.testlenium.web.site.MySite;

public class Site {
    private static InheritableThreadLocal<MySite> mySite = new InheritableThreadLocal<>();

    public static MySite site() {
        return mySite.get();
    }

    public static void setSite(MySite s) {
        mySite.set(s);
    }

    private Site() {
    }
}
