package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.NativeRoutinesView;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class NativeRoutineChooserDialog extends JebDialog {
    private INativeCodeUnit<?> unit;
    private NativeRoutinesView v;
    private INativeMethodItem selectedRoutine;

    public NativeRoutineChooserDialog(Shell parent, INativeCodeUnit<?> unit) {
        super(parent, "Choose a routine", true, false);
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
        if (unit == null) {
            throw new NullPointerException();
        }
        this.unit = unit;
    }

    public INativeMethodItem open() {
        super.open();
        return this.selectedRoutine;
    }

    public void createContents(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        this.v = new NativeRoutinesView(parent, 0, null, this.unit, null, 3);
        this.v.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, true));
        this.v.getViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                NativeRoutineChooserDialog.this.onConfirm();
            }
        });
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.selectedRoutine = this.v.getSelectedRow();
        super.onConfirm();
    }
}