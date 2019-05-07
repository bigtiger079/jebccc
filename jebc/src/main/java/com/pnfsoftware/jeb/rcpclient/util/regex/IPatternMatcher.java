package com.pnfsoftware.jeb.rcpclient.util.regex;

import java.util.regex.Pattern;

public abstract interface IPatternMatcher {
    public abstract boolean match(Pattern paramPattern, Object paramObject);

    public abstract IValueProvider getValueProvider();
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\regex\IPatternMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */