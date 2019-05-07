package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ManualSoftwareUpdateDialog
        extends JebDialog {
    private static final String strNoUpdateFile = "<No update file selected>";
    private boolean success;
    private String _filename;
    private String _password;
    private Text textFilename;
    private Text textPassword;
    private Button btnInstall;

    public ManualSoftwareUpdateDialog(Shell parent) {
        super(parent, S.s(454), true, true);
        this.scrolledContainer = true;
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.NONE;
    }

    public String[] open() {
        super.open();
        if (!this.success) {
            return null;
        }

        return new String[]{this._filename, this._password};
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);

        parent.setLayout(new GridLayout(1, false));

        Group g0 = new Group(parent, 0);
        g0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        g0.setLayout(new GridLayout(2, false));
        g0.setText("File");

        Label label = new Label(g0, 0);
        label.setText(S.s(453));
        label.setLayoutData(UIUtil.createGridDataSpanHorizontally(2));

        UIUtil.createPushbox(g0, "Select a file", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                FileDialog d = new FileDialog(ManualSoftwareUpdateDialog.this.shell);
                d.setText(S.s(724));
                d.setFilterExtensions(new String[]{"*.zip", "*.*"});
                String filename = d.open();
                if (filename != null) {
                    ManualSoftwareUpdateDialog.this._filename = filename;
                }

                ManualSoftwareUpdateDialog.this.textFilename.setText(Strings.safe(ManualSoftwareUpdateDialog.this._filename, "<No update file selected>"));
                ManualSoftwareUpdateDialog.this.btnInstall.setEnabled(ManualSoftwareUpdateDialog.this._filename != null);
            }

        });
        this.textFilename = new Text(g0, 2060);
        this.textFilename.setText("<No update file selected>");
        this.textFilename.setLayoutData(UIUtil.createGridDataForText(this.textFilename, 40));

        Group g1 = new Group(parent, 0);
        g1.setLayoutData(UIUtil.createGridDataFillHorizontally());
        g1.setLayout(new GridLayout(1, false));
        g1.setText(S.s(631));

        new Label(g1, 0).setText(S.s(673));

        this.textPassword = new Text(g1, 2052);
        this.textPassword.setLayoutData(UIUtil.createGridDataFillHorizontally());

        createOkayCancelButtons(parent);
        this.btnInstall = getButtonByStyle(32);
        this.btnInstall.setText(S.s(392));
        this.btnInstall.setEnabled(false);
    }

    protected void onConfirm() {
        this._password = this.textPassword.getText();
        this.success = (this._filename != null);
        super.onConfirm();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\ManualSoftwareUpdateDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */