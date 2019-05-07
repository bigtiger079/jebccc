package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.HistoryAssistedTextField;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.util.base.OSType;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class RenameItemDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(RenameItemDialog.class);
    private TextHistory textHistory;
    private HistoryAssistedTextField text;
    private String input;
    private String description;
    private String initialValue;
    private String originalValue;

    public RenameItemDialog(Shell parent, TextHistory textHistory) {
        super(parent, S.s(678), true, true);
        this.scrolledContainer = true;

        this.textHistory = textHistory;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String open() {
        super.open();
        return this.input;
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);

        Label hint = new Label(parent, 64);
        hint.setLayoutData(UIUtil.createGridDataFillHorizontally());
        hint.setText(this.description != null ? this.description : "Rename the selected item. Leave empty to reset to original value.");

        if (this.originalValue != null) {

            HistoryAssistedTextField text0 = new HistoryAssistedTextField(parent, "Original:\t", null, false);
            text0.setLayoutData(UIUtil.createGridDataFillHorizontally());
            text0.setText(this.originalValue);
            text0.getWidget().setEditable(false);
        }

        this.text = new HistoryAssistedTextField(parent, S.s(591) + ":\t", this.textHistory, true);
        this.text.setLayoutData(UIUtil.createGridDataFillHorizontally());


        if (this.initialValue != null) {
            this.text.getWidget().setText(this.initialValue);
            this.text.getWidget().forceFocus();

            if (!OSType.determine().isWindows()) {
                this.text.getWidget().selectAll();
            } else {
                parent.getDisplay().timerExec(5, new Runnable() {
                    public void run() {
                        try {
                            RenameItemDialog.this.text.selectAll();
                        } catch (Exception localException) {
                        }
                    }
                });
            }
        }


        UIUtil.disableTabOutput(this.text);


        Composite c1 = new Composite(parent, 0);
        c1.setLayout(new RowLayout(256));


        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.text.confirm();
        this.input = this.text.getText();
        super.onConfirm();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\RenameItemDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */