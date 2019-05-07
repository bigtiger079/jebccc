package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.StackEditorView;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;


public class StackframeEditorDialog
        extends JebDialog {
    INativeCodeUnit<?> unit;
    INativeMethodItem initialRoutine;
    FontManager fontman;

    public StackframeEditorDialog(Shell parent, INativeCodeUnit<?> unit, INativeMethodItem initialRoutine, FontManager fontman) {
        super(parent, String.format("Edit stackframe of method \"%s\"", new Object[]{initialRoutine.getName(true)}), true, true);
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;

        this.unit = unit;
        this.initialRoutine = initialRoutine;
        this.fontman = fontman;
    }

    public Object open() {
        super.open();
        return null;
    }

    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout());

        StackEditorView v = new StackEditorView(parent, 0, this.unit);
        GridData data = new GridData(4, 4, true, true);
        data.minimumHeight = 300;
        v.setLayoutData(data);
        if (this.fontman != null) {
            v.setCodefont(this.fontman.getCodeFont());
        }
        v.setInputRoutine(this.initialRoutine);

        createOkayButton(parent);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\nativecode\StackframeEditorDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */