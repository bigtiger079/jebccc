package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

class NativeImageReparseExtraOptionsDialog extends TextDialog {
    Button widgetEndianness;
    boolean bigEndian;

    public NativeImageReparseExtraOptionsDialog(Shell parent) {
        super(parent, "Native Options", "0h", null);
    }

    public long getImageBase() {
        String s = getInputText();
        if (s == null) {
            return 0L;
        }
        return Conversion.stringToLong(s, 0L);
    }

    public boolean isBigEndian() {
        return this.bigEndian;
    }

    protected void createAfterText(Composite parent) {
        super.createAfterText(parent);
        this.widgetEndianness = UIUtil.createCheckbox(parent, "Big Endian", null);
    }

    protected void onConfirm() {
        this.bigEndian = this.widgetEndianness.getSelection();
        super.onConfirm();
    }
}


