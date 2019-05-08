
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractFilteredView<T extends Composite>
        extends Composite {
    private Composite container;
    private FilterText filterText;
    private String previousFilterText;
    private T mainElement;

    public AbstractFilteredView(Composite parent, int style, String[] columnNames) {
        this(parent, style, columnNames, null, false);
    }

    public AbstractFilteredView(Composite parent, int style, String[] columnNames, int[] columnWidths, boolean displayIndex) {
        super(parent, 0);
        setLayout(new FillLayout());
        this.container = new Composite(this, 0);
        GridLayout layout = new GridLayout(1, false);
        this.container.setLayout(layout);
        final boolean filterOnTop = (style & 0x80) != 0;
        if (filterOnTop) {
            style &= 0xFF7F;
            this.filterText = new FilterText(this.container);
        }
        this.mainElement = buildElement(this.container, style);
        if (!filterOnTop) {
            this.filterText = new FilterText(this.container);
        }
        this.filterText.setLayoutData(UIUtil.createGridDataFillHorizontally());
        boolean singleColumn = (!displayIndex) && (columnNames != null) && (columnNames.length == 1);
        if (displayIndex) {
            buildColumn(this.mainElement, "Index", singleColumn ? 0 : 50);
        }
        if (columnNames != null) {
            int i = 0;
            for (String name : columnNames) {
                buildColumn(this.mainElement, name, columnWidths != null ? columnWidths[i] : singleColumn ? 0 : 100);
                i++;
            }
        }
        this.filterText.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if ((e.keyCode == 16777218) && (filterOnTop) && (AbstractFilteredView.this.getItemCount() > 0)) {
                    AbstractFilteredView.this.setSelection(0);
                    AbstractFilteredView.this.mainElement.setFocus();
                    e.doit = false;
                } else if ((e.keyCode == 16777217) && (!filterOnTop) && (AbstractFilteredView.this.getItemCount() > 0)) {
                    AbstractFilteredView.this.setSelection(AbstractFilteredView.this.getItemCount() - 1);
                    AbstractFilteredView.this.mainElement.setFocus();
                    e.doit = false;
                } else if (((e.stateMask & SWT.MOD1) == SWT.MOD1) && (e.keyCode == 8)) {
                    AbstractFilteredView.this.filterText.setText("");
                    e.doit = false;
                } else if (e.keyCode == 27) {
                    AbstractFilteredView.this.setFilterVisibility(false, false);
                    AbstractFilteredView.this.mainElement.setFocus();
                    e.doit = false;
                }
            }
        });
        this.mainElement.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (((e.keyCode == 16777217) && (filterOnTop) && (AbstractFilteredView.this.getSelectionIndex() == 0)) || ((e.keyCode == 16777218) && (!filterOnTop) &&
                        (AbstractFilteredView.this.getSelectionIndex() == AbstractFilteredView.this.getItemCount() - 1))) {
                    if (AbstractFilteredView.this.filterText.isVisible()) {
                        AbstractFilteredView.this.filterText.setFocus();
                        e.doit = false;
                    }
                } else if (((e.stateMask & SWT.MOD1) == SWT.MOD1) && (e.keyCode == 102)) {
                    AbstractFilteredView.this.setFilterVisibility(true, true);
                }
            }
        });
    }

    public IFilterText getFilterText() {
        return this.filterText;
    }

    public T getElement() {
        return this.mainElement;
    }

    public boolean setFocus() {
        return this.mainElement.setFocus();
    }

    public void setFilterVisibility(boolean visible, boolean focus) {
        if (this.filterText.isVisible() != visible) {
            Object layoutData = this.filterText.getLayoutData();
            if ((layoutData instanceof GridData)) {
                ((GridData) layoutData).exclude = (!visible);
            }
            this.filterText.setVisible(visible);
            this.filterText.getParent().layout();
            if (!visible) {
                this.previousFilterText = this.filterText.getText();
                this.filterText.setText("");
            } else if (this.previousFilterText != null) {
                this.filterText.setText(this.previousFilterText);
                this.previousFilterText = null;
            }
        }
        if (focus) {
            this.filterText.setFocus();
            this.filterText.selectAll();
        }
    }

    public void setFilterVisibility(boolean visible) {
        setFilterVisibility(visible, visible);
    }

    protected abstract T buildElement(Composite paramComposite, int paramInt);

    protected abstract void buildColumn(T paramT, String paramString, int paramInt);

    public abstract int getSelectionIndex();

    public abstract void setSelection(int paramInt);

    public abstract int getItemCount();
}


