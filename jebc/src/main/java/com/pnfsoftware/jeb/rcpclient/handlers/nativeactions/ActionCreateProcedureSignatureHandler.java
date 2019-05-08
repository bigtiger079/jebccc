
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeAnalyzer;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.core.units.code.asm.sig.INativeSignature;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureGenerator;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageEntry;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.format.Strings;

public class ActionCreateProcedureSignatureHandler
        extends NativeCodeBaseHandler {
    public ActionCreateProcedureSignatureHandler() {
        super("signProcedure", "Create Signature for Procedure...", 83);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true, false, true);
    }

    public void execute() {
        this.context.getTelemetry().record("actionCreateProcedureSignature");
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        long a = getActiveMemoryAddress(this.part, pbcu);
        INativeMethodItem m = pbcu.getInternalMethod(a, false);
        if (m == null) {
            UI.error("No routine was found at this address");
            return;
        }
        INativeCodeAnalyzer<IInstruction> analyzer = (INativeCodeAnalyzer<IInstruction>) pbcu.getCodeAnalyzer();
        NativeSignatureDBManager nsdbManager = pbcu.getSignatureManager();
        NativeSignaturePackageEntry selectedPackage = nsdbManager.getUserSelectedPackage(analyzer);
        if (selectedPackage == null) {
            UI.warn("A signature package must be selected before signatures can be created.");
            new ActionSelectSignaturePackageHandler().execute();
            selectedPackage = nsdbManager.getUserSelectedPackage(analyzer);
        }
        if (selectedPackage == null) {
            UI.error("A signature package must be selected before signatures can be created.");
            return;
        }
        String msg = Strings.f("A signature is about to be created for the procedure, please note that:%n%n- the signature will identify only the exact same procedure%n%n- the signature will be written on-disk in the selected package when JEB project is saved%n%n- to match the signature on another file, the corresponding package has to be manually loaded (File>Engines>Signature Libraries...)", new Object[0]);
        UI.infoOptional(this.shell, "Procedure signature creation", msg, "dlgSignatureCreation");
        NativeSignatureGenerator sigGen = nsdbManager.getSignatureGenerator();
        if (sigGen != null) {
            INativeSignature newSig = sigGen.generateSignature(analyzer, m, null, null);
            selectedPackage.addSignatureToWrite(newSig);
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionCreateProcedureSignatureHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */