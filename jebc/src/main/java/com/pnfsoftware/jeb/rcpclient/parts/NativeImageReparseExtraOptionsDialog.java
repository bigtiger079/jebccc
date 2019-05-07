/*    */
package com.pnfsoftware.jeb.rcpclient.parts;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*    */ import com.pnfsoftware.jeb.util.encoding.Conversion;
/*    */ import org.eclipse.swt.widgets.Button;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Shell;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ class NativeImageReparseExtraOptionsDialog
        /*    */ extends TextDialog
        /*    */ {
    /*    */ Button widgetEndianness;
    /*    */ boolean bigEndian;

    /*    */
    /*    */
    public NativeImageReparseExtraOptionsDialog(Shell parent)
    /*    */ {
        /* 28 */
        super(parent, "Native Options", "0h", null);
        /*    */
    }

    /*    */
    /*    */
    public long getImageBase() {
        /* 32 */
        String s = getInputText();
        /* 33 */
        if (s == null) {
            /* 34 */
            return 0L;
            /*    */
        }
        /* 36 */
        return Conversion.stringToLong(s, 0L);
        /*    */
    }

    /*    */
    /*    */
    public boolean isBigEndian() {
        /* 40 */
        return this.bigEndian;
        /*    */
    }

    /*    */
    /*    */
    protected void createAfterText(Composite parent)
    /*    */ {
        /* 45 */
        super.createAfterText(parent);
        /* 46 */
        this.widgetEndianness = UIUtil.createCheckbox(parent, "Big Endian", null);
        /*    */
    }

    /*    */
    /*    */
    protected void onConfirm()
    /*    */ {
        /* 51 */
        this.bigEndian = this.widgetEndianness.getSelection();
        /* 52 */
        super.onConfirm();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\NativeImageReparseExtraOptionsDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */