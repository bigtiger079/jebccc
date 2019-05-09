package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.AbstractFilteredView;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.IFilterText;
import com.pnfsoftware.jeb.rcpclient.extensions.filter.AbstractFilteredFilter;
import com.pnfsoftware.jeb.rcpclient.util.regex.IPattern;
import com.pnfsoftware.jeb.rcpclient.util.regex.SimplePattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class AbstractFilteredViewer<T extends Composite, V extends ColumnViewer> extends Viewer {
    private AbstractFilteredView<T> widget;
    private V viewer;
    AbstractFilteredFilter filter;
    protected IFilteredContentProvider provider;
    protected FilteredViewerComparator comparator;
    private boolean displayFilteredRowCount;
    private IPattern filterPatternFactory = new SimplePattern("");
    private IFilterText filterText;
    private List<Listener> filterDoneListeners = new ArrayList<>();

    public AbstractFilteredViewer(AbstractFilteredView<T> widget) {
        this.widget = widget;
        this.viewer = buildViewer(widget);
        this.filter = buildFilter(this.viewer);
        this.viewer.addFilter(this.filter);
        this.filterText = widget.getFilterText();
        this.filterText.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.character != '\r') {
                    AbstractFilteredViewer.this.filterText.setStatus(null);
                    return;
                }
                Object result = AbstractFilteredViewer.this.applyFilterText();
                Event event = new Event();
                event.widget = AbstractFilteredViewer.this.widget;
                event.data = result;
                for (Listener l : AbstractFilteredViewer.this.filterDoneListeners) {
                    l.handleEvent(event);
                }
            }
        });
        refreshItemCountLabel();
    }

    public Object applyFilterText() {
        IFilterText filterText = this.widget.getFilterText();
        String filterString = filterText.getText();
        Object result = null;
        if ((filterString == null) || (filterString.length() == 0)) {
            this.filter.setFilterText(null, true);
            filterText.setStatus(null);
            result = doAfterEmptyFilter();
        } else {
            doBeforeNotNullFilter();
            try {
                this.filter.setFilterPattern(this.filterPatternFactory.createInstance(filterString), true);
                filterText.setStatus(Boolean.TRUE);
            } catch (PatternSyntaxException ex) {
                String text = filterString.toLowerCase();
                this.filter.setFilterText(text, true);
                filterText.setStatus(Boolean.FALSE);
            }
            result = doAfterNotNullFilter();
        }
        refreshItemCountLabel();
        return result;
    }

    public boolean isFiltered() {
        return this.filter.isFiltered();
    }

    protected abstract V buildViewer(AbstractFilteredView<T> paramAbstractFilteredView);

    protected abstract AbstractFilteredFilter buildFilter(V paramV);

    protected Object doAfterEmptyFilter() {
        return null;
    }

    protected void doBeforeNotNullFilter() {
    }

    protected Object doAfterNotNullFilter() {
        return null;
    }

    public AbstractFilteredView<T> getControl() {
        return this.widget;
    }

    public void setInput(Object input) {
        this.viewer.setInput(input);
    }

    public Object getInput() {
        return this.viewer.getInput();
    }

    public void setSelection(ISelection selection, boolean reveal) {
        this.viewer.setSelection(selection, reveal);
    }

    public ISelection getSelection() {
        return this.viewer.getSelection();
    }

    public void refresh() {
        UIUtil.safeRefreshViewer(this.viewer);
    }

    public void setLabelProvider(IBaseLabelProvider provider) {
        this.viewer.setLabelProvider(provider);
    }

    protected void setContentProvider(IFilteredContentProvider provider) {
        this.provider = provider;
        this.viewer.setContentProvider(provider);
    }

    public IFilteredContentProvider getContentProvider() {
        return this.provider;
    }

    public void setDisplayFilteredRowCount(boolean enabled) {
        if (this.displayFilteredRowCount != enabled) {
            this.displayFilteredRowCount = enabled;
            refreshItemCountLabel();
        }
    }

    protected void refreshItemCountLabel() {
        if (this.displayFilteredRowCount) {
        }
    }

    public void setFilterPatternFactory(IPattern pattern) {
        this.filterPatternFactory = pattern;
    }

    protected AbstractFilteredView<T> getWidget() {
        return this.widget;
    }

    public V getViewer() {
        return this.viewer;
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        this.viewer.addDoubleClickListener(listener);
    }

    public void removeDoubleClickListener(IDoubleClickListener listener) {
        this.viewer.removeDoubleClickListener(listener);
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        this.viewer.addSelectionChangedListener(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        this.viewer.removeSelectionChangedListener(listener);
    }

    public void addFilteredTextListener(Listener listener) {
        this.filterDoneListeners.add(listener);
    }

    public FilteredViewerComparator getComparator() {
        return this.comparator;
    }
}


