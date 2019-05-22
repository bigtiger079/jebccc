package com.pnfsoftware.jeb.rcpclient.util.regex;

import java.util.regex.Pattern;

public interface IPatternMatcher {
    boolean match(Pattern paramPattern, Object paramObject);

    IValueProvider getValueProvider();
}


