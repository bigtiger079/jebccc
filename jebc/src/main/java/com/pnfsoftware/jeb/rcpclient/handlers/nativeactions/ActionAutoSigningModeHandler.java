/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeAnalyzer;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageEntry;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;

/*    */
/*    */
/*    */
/*    */ public class ActionAutoSigningModeHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /*    */
    public ActionAutoSigningModeHandler()
    /*    */ {
        /* 20 */
        super("autoSigningModeHandler", "Turn Procedure Auto-Signing On/Off...", 0);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 25 */
        return canExecuteAndNativeCheck(this.part, false, false, false);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 30 */
        this.context.getTelemetry().record("actionAutoSigningMode");
        /* 31 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /*    */
        /* 33 */
        INativeCodeAnalyzer<IInstruction> analyzer = pbcu.getCodeAnalyzer();
        /* 34 */
        NativeSignatureDBManager nsdbManager = pbcu.getSignatureManager();
        /*    */
        /* 36 */
        if (!nsdbManager.isAutoSigningModeActivated(analyzer))
            /*    */ {
            /* 38 */
            NativeSignaturePackageEntry selectedPackage = nsdbManager.getUserSelectedPackage(analyzer);
            /* 39 */
            if (selectedPackage == null) {
                /* 40 */
                UI.warn("A signature package must be selected before signatures can be created.");
                /*    */
                /*    */
                /* 43 */
                new ActionSelectSignaturePackageHandler().execute();
                /* 44 */
                selectedPackage = nsdbManager.getUserSelectedPackage(analyzer);
                /*    */
            }
            /* 46 */
            if (selectedPackage == null) {
                /* 47 */
                UI.error("A signature package must be selected before signatures can be created.");
                /* 48 */
                return;
                /*    */
            }
            /*    */
            /*    */
            /* 52 */
            String msg = Strings.f("Automatic signature creation mode is about to be activated, please note that:%n%n- a signature will be created/updated each time a procedure is modified (renaming, comments, etc.)%n%n- a signature will identify only the exact same procedure%n%n- the signatures will be automatically written in the selected on-disk package regularly, and when JEB project is saved%n%n- to match the signatures on another file, the corresponding package has to be manually loaded (File>Engines>Signature Libraries...)", new Object[0]);
            /*    */
            /*    */
            /*    */
            /*    */
            /*    */
            /*    */
            /*    */
            /* 60 */
            UI.infoOptional(this.shell, "Automatic Signing Mode", msg, "dlgAutoModeActivation");
            /*    */
            /* 62 */
            nsdbManager.activateAutoSigningMode(analyzer);
            /*    */
        }
        /*    */
        else {
            /* 65 */
            UI.info("Procedure Auto-Signing Off");
            /* 66 */
            nsdbManager.deactivateAutoSigningMode(analyzer);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionAutoSigningModeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */