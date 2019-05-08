
package com.pnfsoftware.jeb.rcpclient.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimplePatternMatcher
        implements IPatternMatcher {
    private IValueProvider valueProvider;

    public SimplePatternMatcher(IValueProvider valueProvider) {
        this.valueProvider = valueProvider;
    }

    public IValueProvider getValueProvider() {
        return this.valueProvider;
    }

    public boolean match(Pattern pattern, Object element) {
        String value = getValueProvider().getString(element);
        if (value == null) {
            return false;
        }
        return pattern.matcher(value).find();
    }
}


