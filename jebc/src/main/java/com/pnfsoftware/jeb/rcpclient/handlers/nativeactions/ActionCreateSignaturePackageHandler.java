
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;


import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.SignaturePackageCreationDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.SignaturePackageSetupInformation;


public class ActionCreateSignaturePackageHandler
        extends NativeCodeBaseHandler {

    public ActionCreateSignaturePackageHandler() {

        super("createSignaturePackage", "Create Signature Package...", 0);

    }


    public boolean canExecute() {

        return canExecuteAndNativeCheck(this.part, false, false, false);

    }


    public void execute() {

        this.context.getTelemetry().record("actionCreateSignaturePackage");


        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);

        NativeSignatureDBManager nsdbManager = pbcu.getSignatureManager();

        SignaturePackageCreationDialog dlg = new SignaturePackageCreationDialog(this.shell, pbcu);

        SignaturePackageSetupInformation info = dlg.open();

        if (info == null) {

            return;

        }

        nsdbManager.createUserPackage(pbcu.getProcessor().getType(), info.getName(), info.getDescription(), info
                .getAuthor());

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionCreateSignaturePackageHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */