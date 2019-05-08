package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import com.pnfsoftware.jeb.util.net.Net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CustomSurveyDialog
        extends TitleAreaDialog {
    private static final ILogger logger = GlobalLog.getLogger(CustomSurveyDialog.class);
    RcpClientContext context;
    Text txtSuggestion;

    public CustomSurveyDialog(Shell shell, RcpClientContext context) {
        super(shell);
        this.context = context;
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText("Feedback");
        setTitle("What would you like to have in JEB");
        setMessage("This is an optional, single-question survey we ask our regular users.", 1);
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, 0);
        container.setLayoutData(new GridData(4, 4, true, true));
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);
        Label label = new Label(container, 0);
        label.setText("What is the most important feature you would like to have in JEB next?\nFeel free to be as brief or as elaborate as you want.");
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        data.grabExcessVerticalSpace = true;
        data.verticalAlignment = 4;
        this.txtSuggestion = new Text(container, 2050);
        this.txtSuggestion.setLayoutData(data);
        return area;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, 0, "Submit", true);
        createButton(parent, 1, "Dismiss", false);
    }

    protected void okPressed() {
        final String suggestion = this.txtSuggestion.getText();
        if (Strings.countNonBlankCharacters(suggestion) < 3) {
            UI.error("Cannot submit a blank answer!");
            this.txtSuggestion.setFocus();
            return;
        }
        String r = (String) this.context.executeNetworkTask(new Callable() {
            public String call() throws Exception {
                try {
                    String url = "https://www.pnfsoftware.com/submitsurvey?survey=productSurvey1&licenseid=" + Licensing.license_id;
                    Map<String, String> params = new HashMap();
                    params.put("suggestion", suggestion);
                    return CustomSurveyDialog.this.context.getNetworkUtility().post(url, null, params);
                } catch (IOException e) {
                    UI.error("An error occurred.\n\nException: " + e.getMessage());
                }
                return null;
            }
        });
        logger.i("Server response: %s", new Object[]{r});
        UI.info("Thank you!");
        setReturnCode(0);
        close();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\CustomSurveyDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */