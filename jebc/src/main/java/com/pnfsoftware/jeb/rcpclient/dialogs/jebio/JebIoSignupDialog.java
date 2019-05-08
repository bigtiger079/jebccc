package com.pnfsoftware.jeb.rcpclient.dialogs.jebio;

import com.pnfsoftware.jeb.client.jebio.JebIoApiHelper;
import com.pnfsoftware.jeb.client.jebio.JebIoObjectUser;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.format.Validator;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class JebIoSignupDialog
        extends TitleAreaDialog {
    private static final ILogger logger = GlobalLog.getLogger(JebIoSignupDialog.class);
    RcpClientContext context;
    String emailHint;
    Text txtEmail;
    Text txtPassword;
    Text txtPassword2;
    Button btnSignup;
    String email;
    String password;
    String apikey;

    public JebIoSignupDialog(Shell shell, RcpClientContext context, String emailHint) {
        super(shell);
        this.context = context;
        this.emailHint = emailHint;
    }

    protected boolean isResizable() {
        return true;
    }

    public boolean isHelpAvailable() {
        return true;
    }

    public void create() {
        super.create();
        getShell().addHelpListener(new HelpListener() {
            public void helpRequested(HelpEvent e) {
                new JebIoHelpDialog(JebIoSignupDialog.this.getShell()).open();
            }
        });
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText("Sign up");
        setTitle("JEB Malware Sharing Network");
        setMessage("Fill out the following form to create a new account", 1);
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, 0);
        container.setLayoutData(new GridData(4, 4, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        createEmailField(container, this.emailHint);
        createPasswordField(container, null);
        createPasswordConfirmationField(container, null);
        this.txtEmail.setFocus();
        this.txtEmail.selectAll();
        return area;
    }

    private void createEmailField(Composite container, String defValue) {
        Label lbtFirstName = new Label(container, 0);
        lbtFirstName.setText("Email Address:");
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        this.txtEmail = new Text(container, 2048);
        this.txtEmail.setLayoutData(data);
        this.txtEmail.setText(Strings.safe(defValue));
    }

    private void createPasswordField(Composite container, String defValue) {
        Label lbtLastName = new Label(container, 0);
        lbtLastName.setText("Password:");
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        this.txtPassword = new Text(container, 4196352);
        this.txtPassword.setLayoutData(data);
        this.txtPassword.setText(Strings.safe(defValue));
    }

    private void createPasswordConfirmationField(Composite container, String defValue) {
        Label lbtLastName = new Label(container, 0);
        lbtLastName.setText("Confirm your Password:");
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        this.txtPassword2 = new Text(container, 4196352);
        this.txtPassword2.setLayoutData(data);
        this.txtPassword2.setText(Strings.safe(defValue));
    }

    protected void okPressed() {
        this.email = this.txtEmail.getText();
        if (!Validator.isLegalEmail(this.email)) {
            UI.warn("The provided email is illegal");
            this.txtEmail.setFocus();
            return;
        }
        this.password = this.txtPassword.getText();
        if (Strings.isBlank(this.password)) {
            UI.warn("The password field is empty");
            this.txtPassword.setFocus();
            return;
        }
        if (!this.password.equals(this.txtPassword2.getText())) {
            UI.warn("The password does not match the confirmation password");
            this.txtPassword2.setFocus();
            return;
        }
        final JebIoApiHelper helper = new JebIoApiHelper(this.context.getNetworkUtility(), null);
        JebIoObjectUser user = (JebIoObjectUser) this.context.executeNetworkTask(new Callable() {
            public JebIoObjectUser call() throws Exception {
                try {
                    return helper.createUser(JebIoSignupDialog.this.email, JebIoSignupDialog.this.password);
                } catch (IOException e) {
                    UI.error("An error occurred.\n\nException: " + e.getMessage());
                }
                return null;
            }
        });
        if (user == null) {
            return;
        }
        if (user.getCode() != 0L) {
            UI.error("The account was not created.\n\nResponse code: " + user.getCode());
            return;
        }
        this.apikey = user.getApikey();
        String msg = String.format("Your account was created.\n\nA confirmation email was sent to %s", new Object[]{this.email});
        UI.info(getShell(), "Congratulations!", msg);
        setReturnCode(0);
        close();
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getApiKey() {
        return this.apikey;
    }
}


