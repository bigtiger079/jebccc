/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageEntry;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageMetadata;
/*    */ import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.DataFrameDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*    */ import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;
/*    */ import java.util.List;

/*    */
/*    */
/*    */
/*    */ public class ActionSelectSignaturePackageHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /*    */
    public ActionSelectSignaturePackageHandler()
    /*    */ {
        /* 25 */
        super("selectSignaturePackage", "Select Signature Package...", 0);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 30 */
        return canExecuteAndNativeCheck(this.part, false, false, false);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 36 */
        this.context.getTelemetry().record("actionSelectSignaturePackage");
        /*    */
        /* 38 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 39 */
        NativeSignatureDBManager nsdbManager = pbcu.getSignatureManager();
        /*    */
        /* 41 */
        List<NativeSignaturePackageEntry> userPackages = nsdbManager.getUserCreatedPackages(pbcu.getProcessor().getType());
        /* 42 */
        NativeSignaturePackageEntry selectedEntry = null;
        /*    */
        /* 44 */
        if (userPackages.isEmpty()) {
            /* 45 */
            UI.warn("No compatible signature packages found, please create one first.");
            /*    */
            /*    */
            /* 48 */
            new ActionCreateSignaturePackageHandler().execute();
            /*    */
        }
        /*    */
        /* 51 */
        userPackages = nsdbManager.getUserCreatedPackages(pbcu.getProcessor().getType());
        /* 52 */
        if (userPackages.isEmpty()) {
            /* 53 */
            UI.error("No compatible signature packages found, please create one first.");
            /* 54 */
            return;
            /*    */
        }
        /*    */
        /* 57 */
        DataFrame df = new DataFrame(new String[]{S.s(591), S.s(268), S.s(86)});
        /* 58 */
        for (NativeSignaturePackageEntry entry : userPackages) {
            /* 59 */
            String name = Strings.safe(entry.getMetadata().getName());
            /* 60 */
            String author = Strings.safe(entry.getMetadata().getAuthor());
            /* 61 */
            String description = Strings.safe(entry.getMetadata().getDescription());
            /*    */
            /* 63 */
            df.addRow(new Object[]{name, description, author});
            /*    */
        }
        /*    */
        /* 66 */
        DataFrameDialog dlg = new DataFrameDialog(this.shell, String.format("Signature Packages (%s processor)", new Object[]{pbcu.getProcessor().getType().toString()}), true, "sigPackagesListDialog");
        /*    */
        /* 68 */
        dlg.setDataFrame(df);
        /* 69 */
        int index = dlg.open().intValue();
        /* 70 */
        if ((index >= 0) && (index < userPackages.size())) {
            /* 71 */
            selectedEntry = (NativeSignaturePackageEntry) userPackages.get(index);
            /*    */
        }
        /*    */
        /* 74 */
        if (selectedEntry == null) {
            /* 75 */
            return;
            /*    */
        }
        /* 77 */
        nsdbManager.setUserSelectedPackage(pbcu.getCodeAnalyzer(), selectedEntry);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionSelectSignaturePackageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */