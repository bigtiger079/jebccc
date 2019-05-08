package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.NativeTypesView;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class NativeTypeChooserDialog
        extends JebDialog {
    private INativeCodeUnit<?> unit;
    private NativeTypesView v;
    private INativeType selectedType;

    public NativeTypeChooserDialog(Shell parent, INativeCodeUnit<?> unit) {
        super(parent, "Choose a type", true, false);
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
        if (unit == null) {
            throw new NullPointerException();
        }
        this.unit = unit;
    }

    public INativeType open() {
        super.open();
        return this.selectedType;
    }

    public void createContents(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        this.v = new NativeTypesView(parent, 0, null, this.unit, null, 3);
        this.v.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, true));
        this.v.getViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                NativeTypeChooserDialog.this.onConfirm();
            }
        });
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.selectedType = this.v.getSelectedRow();
        super.onConfirm();
    }
}


