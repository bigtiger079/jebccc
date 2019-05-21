package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.InputField;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SignaturePackageCreationDialog extends JebDialog {
    private INativeCodeUnit<?> unit;
    private boolean confirmed;
    private SignaturePackageSetupInformation info;
    private InputField packageName;
    private InputField packageAuthor;
    private InputField packageDescription;

    public SignaturePackageCreationDialog(Shell parent, INativeCodeUnit<?> unit) {
        super(parent, "Create Signature Package", true, false);
        this.unit = unit;
    }

    public SignaturePackageSetupInformation open() {
        super.open();
        if (!this.confirmed) {
            return null;
        }
        return this.info;
    }

    protected void onConfirm() {
        if (this.packageName.getText().isEmpty()) {
            UI.error("Package name cannot be empty");
            return;
        }
        NativeSignatureDBManager nsdbManager = this.unit.getSignatureManager();
        if (nsdbManager.getUserCreatedPackageFolder() == null) {
            UI.error(String.format("Folder for user-created signatures has not been set. Please create the folder 'siglibs%s%s' in your JEB client folder (and re-start the client).", File.separator, "custom"));
            return;
        }
        File packageFile = new File(nsdbManager.getUserCreatedPackageFolder() + File.separator + this.packageName.getText() + ".siglib");
        if (packageFile.exists()) {
            MessageBox mb = new MessageBox(this.shell, 200);
            mb.setText(S.s(821));
            mb.setMessage("Package already exists, would you like to continue and remove the previous package?");
            int r = mb.open();
            if (r != 64) {
                return;
            }
            packageFile.delete();
        }
        this.info = new SignaturePackageSetupInformation();
        this.info.name = this.packageName.getText();
        this.info.author = this.packageAuthor.getText();
        this.info.description = this.packageDescription.getText();
        this.confirmed = true;
        super.onConfirm();
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);
        new Label(parent, 0).setText("Name: ");
        this.packageName = new InputField(parent, "(cannot be empty)", null, 30);
        new Label(parent, 0).setText("Author: ");
        this.packageAuthor = new InputField(parent, null, Licensing.user_name, 30);
        new Label(parent, 0).setText("Description: ");
        this.packageDescription = new InputField(parent, null, null, 40);
        new Label(parent, 0).setText("Architecture: ");
        Text packageProcMode = new Text(parent, 2052);
        packageProcMode.setLayoutData(UIUtil.createGridDataFillHorizontally());
        packageProcMode.setText(this.unit.getProcessor().getType().toString());
        packageProcMode.setEditable(false);
        UIUtil.setStandardLayout(parent, 1);
        new Label(parent, 0).setText(String.format("Note: package will be created under 'siglibs%s%s' in JEB client folder", File.separator, "custom"));
        createOkayCancelButtons(parent);
    }
}