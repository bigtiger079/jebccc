package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.IPluginInformation;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.units.IUnitIdentifier;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.ITableEventListener;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCheckStateProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.util.collect.ArrayUtil;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ListParsersDialog extends JebDialog {
    private Object input;

    public ListParsersDialog(Shell parent) {
        super(parent, S.s(648), true, true, "enginesParsersDialog");
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
    }

    public void setInput(IRuntimeProject prj) {
        this.input = prj;
    }

    public void setInput(IEnginesContext engctx) {
        this.input = engctx;
    }

    public Object open() {
        if (this.input == null) {
            throw new IllegalStateException("Invalid input: please provide a runtime project or an engines context");
        }
        return super.open();
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        int style = (this.input instanceof IEnginesContext) ? 32 : 0;
        FilteredTableView ft = new FilteredTableView(parent, style, new String[]{S.s(779), S.s(656), S.s(591), S.s(268), S.s(818), S.s(86)});
        ft.setLayoutData(UIUtil.createGridDataFill(true, true));
        FilteredTableViewer ftv = new FilteredTableViewer(ft);
        ContentProviderListener p = new ContentProviderListener();
        ftv.setContentProvider(p);
        if ((style & 0x20) != 0) {
            ftv.setCheckStateProvider(new DefaultCheckStateProvider(p));
        }
        ftv.setLabelProvider(new DefaultCellLabelProvider(p));
        ft.addTableEventListener(p);
        ftv.setInput(this.input);
        createOkayButton(parent);
        if (getStandardWidgetManager() != null) {
            getStandardWidgetManager().wrapWidget(ft, "listParsers");
        }
    }

    static class ContentProviderListener implements ITableEventListener, IFilteredTableContentProvider {
        private Object input0;

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.input0 = newInput;
        }

        public void dispose() {
        }

        public Object[] getElements(Object inputElement) {
            if ((inputElement instanceof IEnginesContext)) {
                List<IUnitIdentifier> unitIdentifiers = ((IEnginesContext) inputElement).getUnitIdentifiers();
                return unitIdentifiers.toArray();
            }
            if ((inputElement instanceof IRuntimeProject)) {
                List<IUnitIdentifier> unitIdentifiers = ((IRuntimeProject) inputElement).getProcessor().getUnitIdentifiers();
                return unitIdentifiers.toArray();
            }
            return ArrayUtil.NO_OBJECT;
        }

        public Object[] getRowElements(Object row) {
            if ((row instanceof IUnitIdentifier)) {
                IUnitIdentifier p = (IUnitIdentifier) row;
                String name = p.getClass().getName();
                String description = null;
                String author = null;
                String version = null;
                IPluginInformation pi = p.getPluginInformation();
                if (pi != null) {
                    name = pi.getName();
                    description = pi.getDescription();
                    author = pi.getAuthor();
                    version = pi.getVersion().toString();
                }
                return new Object[]{p.getFormatType(), p.getPriority(), name, description, version, author};
            }
            return ArrayUtil.NO_OBJECT;
        }

        public boolean isChecked(Object row) {
            if ((row instanceof IUnitIdentifier)) {
                IUnitIdentifier id = (IUnitIdentifier) row;
                if ((this.input0 instanceof IEnginesContext)) {
                    return ((IEnginesContext) this.input0).isIdentifierEnabled(id);
                }
            }
            return false;
        }

        public void onTableEvent(Object row, boolean isSelected, boolean isChecked) {
            if ((row instanceof IUnitIdentifier)) {
                IUnitIdentifier id = (IUnitIdentifier) row;
                if ((this.input0 instanceof IEnginesContext)) {
                    ((IEnginesContext) this.input0).setIdentifierEnabled(id, isChecked);
                }
            }
        }
    }
}


