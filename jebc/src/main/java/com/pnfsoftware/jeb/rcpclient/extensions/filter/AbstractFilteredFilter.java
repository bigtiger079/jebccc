
package com.pnfsoftware.jeb.rcpclient.extensions.filter;


import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredContentProvider;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPattern;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;


public abstract class AbstractFilteredFilter
        extends ViewerFilter {
    protected String simplePattern;
    protected IPattern regexPattern;
    private StructuredViewer viewer;
    protected boolean patternChanged = true;


    public AbstractFilteredFilter(StructuredViewer viewer) {

        this.viewer = viewer;

    }


    public void setFilterText(String text, boolean refresh) {

        this.simplePattern = text;

        this.regexPattern = null;

        this.patternChanged = true;

        if (refresh) {

            this.viewer.refresh();

        }

        onRefreshDone();

    }


    protected void onRefreshDone() {
    }


    public void setFilterPattern(IPattern pattern, boolean refresh) {

        this.simplePattern = null;

        this.regexPattern = pattern;

        this.patternChanged = true;

        if (refresh) {

            this.viewer.refresh();

        }

        onRefreshDone();

    }


    public boolean isFiltered() {

        return (this.simplePattern != null) || (this.regexPattern != null);

    }


    public boolean isElementMatch(Object element) {

        Object[] list = getProvider().getRowElements(element);

        if (list == null) {

            return true;

        }


        if (this.regexPattern != null) {

            return this.regexPattern.match(element, list);

        }


        if ((this.simplePattern != null) && (this.simplePattern.length() > 0)) {

            for (Object o : list) {

                if ((o != null) && (o.toString().contains(this.simplePattern))) {

                    return true;

                }

            }

            return false;

        }


        return true;

    }


    public abstract IFilteredContentProvider getProvider();

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\filter\AbstractFilteredFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */