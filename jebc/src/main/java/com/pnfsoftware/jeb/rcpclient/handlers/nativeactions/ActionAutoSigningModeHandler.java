
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;


import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeAnalyzer;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageEntry;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.format.Strings;


public class ActionAutoSigningModeHandler
        extends NativeCodeBaseHandler {

    public ActionAutoSigningModeHandler() {

        super("autoSigningModeHandler", "Turn Procedure Auto-Signing On/Off...", 0);

    }


    public boolean canExecute() {

        return canExecuteAndNativeCheck(this.part, false, false, false);

    }


    public void execute() {

        this.context.getTelemetry().record("actionAutoSigningMode");

        INativeCodeUnit<IInstruction> nativeCodeUnit = (INativeCodeUnit<IInstruction>) getNativeCodeUnit(this.part);


        INativeCodeAnalyzer<IInstruction> analyzer = nativeCodeUnit.getCodeAnalyzer();

        NativeSignatureDBManager nsdbManager = nativeCodeUnit.getSignatureManager();


        if (!nsdbManager.isAutoSigningModeActivated(analyzer)) {

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


            String msg = Strings.f("Automatic signature creation mode is about to be activated, please note that:%n%n- a signature will be created/updated each time a procedure is modified (renaming, comments, etc.)%n%n- a signature will identify only the exact same procedure%n%n- the signatures will be automatically written in the selected on-disk package regularly, and when JEB project is saved%n%n- to match the signatures on another file, the corresponding package has to be manually loaded (File>Engines>Signature Libraries...)", new Object[0]);


            UI.infoOptional(this.shell, "Automatic Signing Mode", msg, "dlgAutoModeActivation");


            nsdbManager.activateAutoSigningMode(analyzer);

        } else {

            UI.info("Procedure Auto-Signing Off");

            nsdbManager.deactivateAutoSigningMode(analyzer);

        }

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionAutoSigningModeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */