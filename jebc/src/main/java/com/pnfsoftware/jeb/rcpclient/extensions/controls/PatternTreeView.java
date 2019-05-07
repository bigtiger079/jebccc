
package com.pnfsoftware.jeb.rcpclient.extensions.controls;


import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPatternMatcher;
import com.pnfsoftware.jeb.rcpclient.util.regex.PatternFilter;
import org.eclipse.swt.widgets.Composite;


public class PatternTreeView
        extends FilteredTreeView {
    private FilteredTreeViewer filteredViewer;


    public PatternTreeView(Composite parent, int style, String[] columnNames, int[] columnWidths, IPatternMatcher patternMatcher, boolean expandAfterFilter) {

        super(parent, style, columnNames, columnWidths);


        this.filteredViewer = new FilteredTreeViewer(this, expandAfterFilter);

        this.filteredViewer.setFilterPatternFactory(new PatternFilter(patternMatcher, "", columnNames));

    }


    public FilteredTreeViewer getTreeViewer() {

        return this.filteredViewer;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\controls\PatternTreeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */