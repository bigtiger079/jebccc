
package com.pnfsoftware.jeb.rcpclient.util.regex;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SimplePattern
        implements IPattern {
    private Pattern pattern;


    public SimplePattern(String filterString) {

        this.pattern = Pattern.compile(filterString, 98);

    }


    public boolean match(Object element, Object[] list) {

        if (list != null) {

            for (Object o : list) {

                if ((o != null) && (this.pattern.matcher(o.toString()).find())) {

                    return true;

                }

            }

        }

        return false;

    }


    public IPattern createInstance(String filterString) {

        return new SimplePattern(filterString);

    }

}


