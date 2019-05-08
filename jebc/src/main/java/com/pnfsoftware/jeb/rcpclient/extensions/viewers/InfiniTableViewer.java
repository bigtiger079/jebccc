
package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import com.pnfsoftware.jeb.rcpclient.extensions.controls.IOutOfRangeHelper;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.InfiniTableView;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

public class InfiniTableViewer
        extends Viewer {
    private static final ILogger logger = GlobalLog.getLogger(InfiniTableViewer.class);
    private InfiniTableView ctl;
    private Table table;
    private TableViewer wrappedViewer;
    private IInfiniTableSectionProvider clientContentProvider;
    private long minAllowedId = Long.MIN_VALUE;
    private long sectionBase;
    private int sectionSize;

    public InfiniTableViewer(final InfiniTableView ctl) {
        this.ctl = ctl;
        this.table = ctl.getTable();
        ctl.setRequestOutOfRangeHandler(new IOutOfRangeHelper() {
            public void onResetRange(int delta) {
                InfiniTableViewer.this.sectionBase = (InfiniTableViewer.this.sectionBase + delta);
                InfiniTableViewer.this.sectionSize = 0;
                Object input = InfiniTableViewer.this.wrappedViewer.getInput();
                InfiniTableViewer.this.wrappedViewer.setInput(null);
                InfiniTableViewer.this.wrappedViewer.setInput(input);
                InfiniTableViewer.this.table.setTopIndex(0);
            }

            public void onRequestOutOfRange(int selDelta, int topDelta) {
                int selIndex = InfiniTableViewer.this.table.getSelectionIndex();
                int topIndex = InfiniTableViewer.this.table.getTopIndex();
                int newSelIndex = selIndex + selDelta;
                int newTopIndex = topIndex + topDelta;
                if ((selDelta != 0) && (topDelta == 0)) {
                    if (newSelIndex < 0) {
                        if (InfiniTableViewer.this.sectionBase + selDelta < InfiniTableViewer.this.minAllowedId) {
                            return;
                        }
                        InfiniTableViewer.this.sectionBase = (InfiniTableViewer.this.sectionBase + selDelta);
                        InfiniTableViewer.this.wrappedViewer.refresh();
                    } else if (newSelIndex >= InfiniTableViewer.this.table.getItemCount()) {
                        int gap = InfiniTableViewer.this.table.getItemCount() - selIndex - 1;
                        InfiniTableViewer.this.sectionBase = (InfiniTableViewer.this.sectionBase + (selDelta - gap));
                        InfiniTableViewer.this.wrappedViewer.refresh();
                    }
                    InfiniTableViewer.this.table.setSelection(selIndex);
                    selIndex = newSelIndex;
                    selDelta = 0;
                } else if ((selDelta == 0) && (topDelta != 0)) {
                    if (newTopIndex > topIndex) {
                        InfiniTableViewer.this.sectionSize = (InfiniTableViewer.this.sectionSize + topDelta);
                        InfiniTableViewer.this.wrappedViewer.refresh();
                        InfiniTableViewer.this.table.setSelection(newSelIndex);
                        InfiniTableViewer.this.table.setTopIndex(newTopIndex);
                    } else if (newTopIndex < topIndex) {
                        if (InfiniTableViewer.this.sectionBase + topDelta < InfiniTableViewer.this.minAllowedId) {
                            return;
                        }
                        InfiniTableViewer.this.sectionBase = (InfiniTableViewer.this.sectionBase + topDelta);
                        InfiniTableViewer.this.sectionSize = (InfiniTableViewer.this.sectionSize - topDelta);
                        InfiniTableViewer.this.wrappedViewer.refresh();
                        InfiniTableViewer.this.table.setSelection(selIndex - topDelta);
                        InfiniTableViewer.this.table.setTopIndex(topIndex);
                    }
                }
            }
        });
        ctl.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                InfiniTableViewer.this.wrappedViewer.refresh();
            }
        });
        this.wrappedViewer = new TableViewer(this.table);
        this.wrappedViewer.setContentProvider(new IStructuredContentProvider() {
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                InfiniTableViewer.this.clientContentProvider.inputChanged(viewer, oldInput, newInput);
            }

            public void dispose() {
                InfiniTableViewer.this.clientContentProvider.dispose();
            }

            public Object[] getElements(Object inputElement) {
                int visicount = ctl.getMaximumVisibleRowCount(true);
                Object[] data = InfiniTableViewer.this.clientContentProvider.get(inputElement, InfiniTableViewer.this.sectionBase, InfiniTableViewer.this.sectionSize + visicount);
                return data;
            }
        });
    }

    public void setTopId(long startId, boolean doRefresh) {
        this.sectionBase = startId;
        if (doRefresh) {
            refresh();
        }
    }

    public void setMinimumAllowedId(long minAllowedId) {
        this.minAllowedId = minAllowedId;
    }

    public long getMinimumAllowedId() {
        return this.minAllowedId;
    }

    public void setContentProvider(IInfiniTableSectionProvider provider) {
        this.clientContentProvider = provider;
    }

    public void setLabelProvider(IBaseLabelProvider provider) {
        this.wrappedViewer.setLabelProvider(provider);
    }

    public Control getControl() {
        return this.ctl;
    }

    public void setInput(Object input) {
        this.wrappedViewer.setInput(input);
    }

    public Object getInput() {
        return this.wrappedViewer.getInput();
    }

    public void refresh() {
        this.wrappedViewer.refresh();
    }

    public ISelection getSelection() {
        return this.wrappedViewer.getSelection();
    }

    public void setSelection(ISelection selection, boolean reveal) {
        throw new RuntimeException("TBI");
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\viewers\InfiniTableViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */