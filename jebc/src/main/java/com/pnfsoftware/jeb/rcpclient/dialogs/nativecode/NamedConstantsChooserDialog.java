package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.type.CodeConstant;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.NamedConstantsView;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class NamedConstantsChooserDialog extends JebDialog {
    private INativeCodeUnit<?> unit;
    private NamedConstantsView v;
    private Object sourceValue;
    private CodeConstant selectedConstant;

    public NamedConstantsChooserDialog(Shell parent, INativeCodeUnit<?> unit, Object sourceValue) {
        super(parent, "Choose a constant", true, false);
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
        if (unit == null) {
            throw new NullPointerException();
        }
        this.unit = unit;
        this.sourceValue = sourceValue;
    }

    public CodeConstant open() {
        super.open();
        return this.selectedConstant;
    }

    public void createContents(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        this.v = new NamedConstantsView(parent, 0, null, this.unit, null, this.sourceValue);
        this.v.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, true));
        this.v.getViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                NamedConstantsChooserDialog.this.onConfirm();
            }
        });
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.selectedConstant = this.v.getSelectedRow();
        super.onConfirm();
    }
}


