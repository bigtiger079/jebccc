
package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IBasicInformation;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.IOptimizerInfo;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.ITableEventListener;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCheckStateProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.collect.ArrayUtil;

import java.util.IdentityHashMap;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class DecompDynamicOptionsView
        extends Composite {
    INativeSourceUnit src;
    String address;
    FilteredTableView ft;
    IdentityHashMap<IOptimizerInfo, Boolean> changes = new IdentityHashMap();

    public DecompDynamicOptionsView(Composite parent, int style, INativeSourceUnit src, String address) {
        super(parent, style);
        setLayout(new GridLayout(1, false));
        if (src == null) {
            throw new NullPointerException();
        }
        this.src = src;
        this.address = address;
        this.ft = new FilteredTableView(this, 160, new String[]{S.s(591), S.s(268)});
        this.ft.setLayoutData(UIUtil.createGridDataFill(true, true));
        FilteredTableViewer ftv = new FilteredTableViewer(this.ft);
        ContentProviderListener p = new ContentProviderListener();
        ftv.setContentProvider(p);
        ftv.setCheckStateProvider(new DefaultCheckStateProvider(p));
        ftv.setLabelProvider(new DefaultCellLabelProvider(p));
        this.ft.addTableEventListener(p);
        ftv.setInput(src);
    }

    public IdentityHashMap<IOptimizerInfo, Boolean> getChanges() {
        return this.changes;
    }

    class ContentProviderListener implements ITableEventListener, IFilteredTableContentProvider {
        private INativeSourceUnit input;

        ContentProviderListener() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.input = ((INativeSourceUnit) newInput);
        }

        public void dispose() {
        }

        public Object[] getElements(Object elt) {
            Assert.a(this.input == elt);
            if (this.input != null) {
                List<? extends IOptimizerInfo> optInfoList = this.input.getDecompiler().getOnDemandIROptimizers(DecompDynamicOptionsView.this.address);
                return optInfoList.toArray();
            }
            return ArrayUtil.NO_OBJECT;
        }

        public Object[] getRowElements(Object row) {
            if ((row instanceof IOptimizerInfo)) {
                IOptimizerInfo e = (IOptimizerInfo) row;
                String name = e.getInformation().getName();
                String description = e.getInformation().getDescription();
                return new Object[]{name, description};
            }
            return ArrayUtil.NO_OBJECT;
        }

        public boolean isChecked(Object row) {
            if ((row instanceof IOptimizerInfo)) {
                IOptimizerInfo e = (IOptimizerInfo) row;
                Boolean value = (Boolean) DecompDynamicOptionsView.this.changes.get(e);
                if (value == null) {
                    return e.isEnabled();
                }
                return value.booleanValue();
            }
            return false;
        }

        public void onTableEvent(Object row, boolean isSelected, boolean isChecked) {
            if ((row instanceof IOptimizerInfo)) {
                IOptimizerInfo e = (IOptimizerInfo) row;
                boolean original = e.isEnabled();
                if (original != isChecked) {
                    DecompDynamicOptionsView.this.changes.put(e, Boolean.valueOf(isChecked));
                } else {
                    DecompDynamicOptionsView.this.changes.remove(e);
                }
            }
        }
    }
}


