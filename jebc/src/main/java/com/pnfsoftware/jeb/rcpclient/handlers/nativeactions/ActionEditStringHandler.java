/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.DefineStringDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.StringSetupInformation;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import org.eclipse.swt.SWT;

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
/*    */
/*    */ public class ActionEditStringHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /* 25 */   private static final ILogger logger = GlobalLog.getLogger(ActionEditStringHandler.class);

    /*    */
    /*    */
    public ActionEditStringHandler() {
        /* 28 */
        super("editString", S.s(494), SWT.MOD1 | SWT.MOD3 | 0x41);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 33 */
        return canExecuteAndNativeCheck(this.part, true);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 38 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 39 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*    */
        /* 41 */
        DefineStringDialog dlg = new DefineStringDialog(this.shell, a, pbcu);
        /* 42 */
        StringSetupInformation info = dlg.open();
        /* 43 */
        if (info == null) {
            /* 44 */
            return;
            /*    */
        }
        /*    */
        /* 47 */
        if (!pbcu.setStringAt(info.address, info.addressMax, info.stringType, info.minChars, info.maxChars)) {
            /* 48 */
            logger.error("Failed to define string at address %Xh", new Object[]{Long.valueOf(info.address)});
            /*    */
        }
        /*    */
        /* 51 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionEditStringHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */