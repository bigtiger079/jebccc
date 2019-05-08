
package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePatternMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TablePatternMatcher
        extends SimplePatternMatcher {
    private IFilteredTableContentProvider contentProvider;

    public TablePatternMatcher(IFilteredTableContentProvider contentProvider, IValueProvider valueProvider) {
        super(valueProvider);
        this.contentProvider = contentProvider;
    }

    public boolean match(Pattern pattern, Object element) {
        Object[] list = this.contentProvider.getRowElements(element);
        for (Object o : list) {
            if ((o != null) && (pattern.matcher(o.toString()).find())) {
                return true;
            }
        }
        return false;
    }
}


