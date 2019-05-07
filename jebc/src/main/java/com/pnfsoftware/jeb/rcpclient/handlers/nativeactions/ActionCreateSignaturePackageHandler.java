/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.SignaturePackageCreationDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.SignaturePackageSetupInformation;

/*    */
/*    */
/*    */ public class ActionCreateSignaturePackageHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /*    */
    public ActionCreateSignaturePackageHandler()
    /*    */ {
        /* 17 */
        super("createSignaturePackage", "Create Signature Package...", 0);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 22 */
        return canExecuteAndNativeCheck(this.part, false, false, false);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 27 */
        this.context.getTelemetry().record("actionCreateSignaturePackage");
        /*    */
        /* 29 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 30 */
        NativeSignatureDBManager nsdbManager = pbcu.getSignatureManager();
        /* 31 */
        SignaturePackageCreationDialog dlg = new SignaturePackageCreationDialog(this.shell, pbcu);
        /* 32 */
        SignaturePackageSetupInformation info = dlg.open();
        /* 33 */
        if (info == null) {
            /* 34 */
            return;
            /*    */
        }
        /* 36 */
        nsdbManager.createUserPackage(pbcu.getProcessor().getType(), info.getName(), info.getDescription(), info
/* 37 */.getAuthor());
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionCreateSignaturePackageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */