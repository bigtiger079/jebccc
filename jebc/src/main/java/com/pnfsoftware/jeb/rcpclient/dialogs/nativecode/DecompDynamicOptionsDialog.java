package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.IOptimizerInfo;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.DecompDynamicOptionsView;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;


public class DecompDynamicOptionsDialog
        extends JebDialog {
    INativeSourceUnit src;
    String address;
    DecompDynamicOptionsView v;
    boolean success;

    public DecompDynamicOptionsDialog(Shell parent, INativeSourceUnit src, String address) {
        super(parent, "Decompiler Dynamic Options", true, true);

        if (src == null) {
            throw new NullPointerException();
        }
        this.src = src;
        this.address = address;
    }

    public Boolean open() {
        super.open();
        if ((this.success) && (!this.v.getChanges().isEmpty())) {
            for (Map.Entry<IOptimizerInfo, Boolean> e : this.v.getChanges().entrySet()) {
                ((IOptimizerInfo) e.getKey()).setEnabled(((Boolean) e.getValue()).booleanValue());
            }
            return Boolean.valueOf(true);
        }
        return Boolean.valueOf(false);
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);

        this.v = new DecompDynamicOptionsView(parent, 0, this.src, this.address);
        this.v.setLayoutData(UIUtil.createGridDataFill(true, true));

        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.success = true;
        super.onConfirm();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\nativecode\DecompDynamicOptionsDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */