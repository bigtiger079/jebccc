/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeAnalyzer;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.INativeSignature;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureGenerator;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageEntry;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;

/*    */
/*    */
/*    */
/*    */ public class ActionCreateProcedureSignatureHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /*    */
    public ActionCreateProcedureSignatureHandler()
    /*    */ {
        /* 23 */
        super("signProcedure", "Create Signature for Procedure...", 83);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 28 */
        return canExecuteAndNativeCheck(this.part, true, false, true);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 33 */
        this.context.getTelemetry().record("actionCreateProcedureSignature");
        /*    */
        /* 35 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 36 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*    */
        /* 38 */
        INativeMethodItem m = pbcu.getInternalMethod(a, false);
        /* 39 */
        if (m == null) {
            /* 40 */
            UI.error("No routine was found at this address");
            /* 41 */
            return;
            /*    */
        }
        /*    */
        /*    */
        /* 45 */
        INativeCodeAnalyzer<IInstruction> analyzer = pbcu.getCodeAnalyzer();
        /* 46 */
        NativeSignatureDBManager nsdbManager = pbcu.getSignatureManager();
        /*    */
        /*    */
        /* 49 */
        NativeSignaturePackageEntry selectedPackage = nsdbManager.getUserSelectedPackage(analyzer);
        /* 50 */
        if (selectedPackage == null) {
            /* 51 */
            UI.warn("A signature package must be selected before signatures can be created.");
            /*    */
            /*    */
            /* 54 */
            new ActionSelectSignaturePackageHandler().execute();
            /* 55 */
            selectedPackage = nsdbManager.getUserSelectedPackage(analyzer);
            /*    */
        }
        /*    */
        /* 58 */
        if (selectedPackage == null) {
            /* 59 */
            UI.error("A signature package must be selected before signatures can be created.");
            /* 60 */
            return;
            /*    */
        }
        /*    */
        /*    */
        /*    */
        /*    */
        /* 66 */
        String msg = Strings.f("A signature is about to be created for the procedure, please note that:%n%n- the signature will identify only the exact same procedure%n%n- the signature will be written on-disk in the selected package when JEB project is saved%n%n- to match the signature on another file, the corresponding package has to be manually loaded (File>Engines>Signature Libraries...)", new Object[0]);
        /*    */
        /*    */
        /*    */
        /*    */
        /*    */
        /*    */
        /* 73 */
        UI.infoOptional(this.shell, "Procedure signature creation", msg, "dlgSignatureCreation");
        /*    */
        /* 75 */
        NativeSignatureGenerator sigGen = nsdbManager.getSignatureGenerator();
        /* 76 */
        if (sigGen != null) {
            /* 77 */
            INativeSignature newSig = sigGen.generateSignature(analyzer, m, null, null);
            /*    */
            /* 79 */
            selectedPackage.addSignatureToWrite(newSig);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionCreateProcedureSignatureHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */