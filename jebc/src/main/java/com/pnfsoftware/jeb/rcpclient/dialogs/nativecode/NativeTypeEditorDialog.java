package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.NativeTypeEditorView;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class NativeTypeEditorDialog
        extends JebDialog {
    INativeCodeUnit<?> unit;
    IStructureType initialType;
    FontManager fontman;

    public NativeTypeEditorDialog(Shell parent, INativeCodeUnit<?> unit, IStructureType initialType, FontManager fontman) {
        super(parent, "Type Editor", true, true);
        this.unit = unit;
        this.initialType = initialType;
        this.fontman = fontman;
    }

    public Object open() {
        super.open();
        return null;
    }

    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout());
        NativeTypeEditorView v = new NativeTypeEditorView(parent, 0, this.unit);
        GridData data = new GridData(4, 4, true, true);
        data.minimumHeight = 300;
        v.setLayoutData(data);
        if (this.fontman != null) {
            v.setCodefont(this.fontman.getCodeFont());
        }
        v.setInput(this.initialType);
        createOkayButton(parent);
    }
}


