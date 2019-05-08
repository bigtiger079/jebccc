
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeAnalyzer;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageEntry;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageMetadata;
import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.DataFrameDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.List;

public class ActionSelectSignaturePackageHandler
        extends NativeCodeBaseHandler {
    public ActionSelectSignaturePackageHandler() {
        super("selectSignaturePackage", "Select Signature Package...", 0);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, false, false, false);
    }

    public void execute() {
        this.context.getTelemetry().record("actionSelectSignaturePackage");
        INativeCodeUnit<IInstruction> pbcu = (INativeCodeUnit<IInstruction>) getNativeCodeUnit(this.part);
        NativeSignatureDBManager nsdbManager = pbcu.getSignatureManager();
        List<NativeSignaturePackageEntry> userPackages = nsdbManager.getUserCreatedPackages(pbcu.getProcessor().getType());
        NativeSignaturePackageEntry selectedEntry = null;
        if (userPackages.isEmpty()) {
            UI.warn("No compatible signature packages found, please create one first.");
            new ActionCreateSignaturePackageHandler().execute();
        }
        userPackages = nsdbManager.getUserCreatedPackages(pbcu.getProcessor().getType());
        if (userPackages.isEmpty()) {
            UI.error("No compatible signature packages found, please create one first.");
            return;
        }
        DataFrame df = new DataFrame(S.s(591), S.s(268), S.s(86));
        for (NativeSignaturePackageEntry entry : userPackages) {
            String name = Strings.safe(entry.getMetadata().getName());
            String author = Strings.safe(entry.getMetadata().getAuthor());
            String description = Strings.safe(entry.getMetadata().getDescription());
            df.addRow(name, description, author);
        }
        DataFrameDialog dlg = new DataFrameDialog(this.shell, String.format("Signature Packages (%s processor)", pbcu.getProcessor().getType().toString()), true, "sigPackagesListDialog");
        dlg.setDataFrame(df);
        int index = dlg.open();
        if ((index >= 0) && (index < userPackages.size())) {
            selectedEntry = userPackages.get(index);
        }
        if (selectedEntry == null) {
            return;
        }
        nsdbManager.setUserSelectedPackage(pbcu.getCodeAnalyzer(), selectedEntry);
    }
}

/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionSelectSignaturePackageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */