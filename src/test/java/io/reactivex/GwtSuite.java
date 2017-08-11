package io.reactivex;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;

public class GwtSuite {
    public static Test suite() {
        GWTTestSuite suite = new GWTTestSuite("Test suite for RxJava GWT");
        suite.addTestSuite(RxGwtTest.class);
        return suite;
    }
}
