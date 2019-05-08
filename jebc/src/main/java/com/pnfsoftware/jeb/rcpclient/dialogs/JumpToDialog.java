package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.HistoryAssistedTextField;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class JumpToDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(JumpToDialog.class);
    private TextHistory textHistory;
    private HistoryAssistedTextField text;
    private String input;
    private Font font;
    private String initialValue;
    protected Button btnOk;

    public JumpToDialog(Shell parent, TextHistory textHistory) {
        super(parent, S.s(420), true, true, null);
        this.scrolledContainer = true;
        this.textHistory = textHistory;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public String open() {
        super.open();
        return this.input;
    }

    public void createContents(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        Group c0 = new Group(parent, 0);
        c0.setText("Jump target");
        c0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        c0.setLayout(new GridLayout(2, false));
        this.text = new HistoryAssistedTextField(c0, S.s(52) + ":", this.textHistory, true);
        this.text.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.text.getWidget().setFont(this.font);
        if (this.initialValue != null) {
            this.text.getWidget().setText(this.initialValue);
            this.text.getWidget().selectAll();
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\JumpToDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */