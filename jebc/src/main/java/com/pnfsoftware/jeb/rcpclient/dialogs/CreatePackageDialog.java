package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.HistoryAssistedTextField;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreatePackageDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(CreatePackageDialog.class);
    private TextHistory textHistory;
    private HistoryAssistedTextField text;
    private String input;
    String description;
    private String initialValue;
    protected Button btn_ok;

    public CreatePackageDialog(Shell parent, TextHistory textHistory) {
        super(parent, S.s(471), true, true);
        this.scrolledContainer = true;
        this.textHistory = textHistory;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public String open() {
        super.open();
        return this.input;
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        Label hint = new Label(parent, 64);
        hint.setLayoutData(UIUtil.createGridDataFillHorizontally());
        hint.setText(this.description != null ? this.description : S.s(391));
        this.text = new HistoryAssistedTextField(parent, S.s(591) + ":", this.textHistory, true);
        GridData griddata = UIUtil.createGridDataForText(this.text, 50, 0, false);
        griddata.grabExcessHorizontalSpace = true;
        griddata.horizontalAlignment = 4;
        this.text.setLayoutData(griddata);
        if (this.initialValue != null) {
            this.text.getWidget().setText(this.initialValue);
            this.text.getWidget().selectAll();
        }
        UIUtil.disableTabOutput(this.text);
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.text.confirm();
        this.input = this.text.getText();
        super.onConfirm();
    }
}


