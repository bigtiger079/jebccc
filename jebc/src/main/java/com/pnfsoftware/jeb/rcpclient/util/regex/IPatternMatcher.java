package com.pnfsoftware.jeb.rcpclient.util.regex;

import java.util.regex.Pattern;

public abstract interface IPatternMatcher {
    public abstract boolean match(Pattern paramPattern, Object paramObject);

    public abstract IValueProvider getValueProvider();
}


