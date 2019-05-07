package com.pnfsoftware.jeb.rcpclient.dialogs.jebio;

import com.pnfsoftware.jeb.client.jebio.JebIoApiHelper;
import com.pnfsoftware.jeb.client.jebio.JebIoObjectUser;
import com.pnfsoftware.jeb.client.jebio.JebIoUtil;
import com.pnfsoftware.jeb.client.jebio.UserCredentials;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.format.PluralFormatter;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.format.Validator;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class JebIoLoginDialog
        extends TitleAreaDialog {
    private static final ILogger logger = GlobalLog.getLogger(JebIoLoginDialog.class);

    private static final int SIGNUP_ID = 1025;
    RcpClientContext context;
    UserCredentials previousCredentials;
    Text txtEmail;
    Text txtPassword;
    Text textApikey;
    Button btnVerify;
    Button btnSignup;
    boolean verified;

    public JebIoLoginDialog(Shell shell, RcpClientContext context) {
        super(shell);
        this.context = context;
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
                new JebIoHelpDialog(JebIoLoginDialog.this.getShell()).open();
            }
        });
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText("Login");
        setTitle("JEB Malware Sharing Network");
        if (!JebIoUtil.retrieveCredentials(this.context).lookValid()) {
            setMessage("Click the \"Create an Account\" button to get started, or log in using your credentials.", 2);
        } else {
            setMessage("Your account on the JEB Malware Sharing Network", 1);
        }

        Composite area = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(area, 0);
        container.setLayoutData(new GridData(4, 4, true, true));
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);

        Group container2 = new Group(container, 0);
        container2.setText("Log in");
        container2.setLayoutData(new GridData(4, 4, true, true));
        container2.setLayout(new GridLayout(2, false));

        this.previousCredentials = JebIoUtil.retrieveCredentials(this.context);
        createEmailField(container2, this.previousCredentials.getEmail());
        createPasswordField(container2, this.previousCredentials.getPassword());
        createApiKeyField(container2, this.previousCredentials.getApikey());
        createVerifyButton(container2);

        this.txtEmail.setFocus();
        this.txtEmail.selectAll();
        return area;
    }


    protected void createButtonsForButtonBar(Composite parent) {
        parent.setLayoutData(new GridData(4, 16777216, true, false));

        createButton(parent, 1025, "Create an Account", false)
                .addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        if ((JebIoLoginDialog.this.previousCredentials.lookValid()) &&
                                (!MessageDialog.openQuestion(JebIoLoginDialog.this.getShell(), "Proceed", "It looks like you have an account already.\n\nWould you like to sign up for another account?"))) {
                            return;
                        }

                        JebIoSignupDialog dlg = new JebIoSignupDialog(JebIoLoginDialog.this.getShell(), JebIoLoginDialog.this.context, JebIoLoginDialog.this.txtEmail.getText());
                        int retcode = dlg.open();
                        if (retcode == 0) {
                            JebIoLoginDialog.this.txtEmail.setText(dlg.getEmail());
                            JebIoLoginDialog.this.txtPassword.setText(dlg.getPassword());
                            JebIoLoginDialog.this.textApikey.setText(dlg.getApiKey());
                        }

                    }

                });
        Label spacer = new Label(parent, 0);
        spacer.setLayoutData(new GridData(4, 16777216, true, false));


        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns += 1;
        layout.makeColumnsEqualWidth = false;

        createButton(parent, 0, "OK", true);
        createButton(parent, 1, "Cancel", false);
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
        this.txtEmail.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                JebIoLoginDialog.this.verified = false;
            }
        });
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
        this.txtPassword.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                JebIoLoginDialog.this.verified = false;
            }
        });
    }

    private void createApiKeyField(Composite container, String defValue) {
        Label lbtLastName = new Label(container, 0);
        lbtLastName.setText("API Key:");

        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        this.textApikey = new Text(container, 2056);
        this.textApikey.setLayoutData(data);
        this.textApikey.setText(Strings.safe(defValue));
    }

    private void createVerifyButton(Composite container) {
        Label lbtLastName = new Label(container, 0);
        lbtLastName.setText("");

        GridData data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.horizontalAlignment = 1;
        this.btnVerify = new Button(container, 8);
        this.btnVerify.setFont(JFaceResources.getDialogFont());
        this.btnVerify.setLayoutData(data);
        this.btnVerify.setText("View my Profile");

        this.btnVerify.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                JebIoObjectUser user = JebIoLoginDialog.this.getUser(JebIoLoginDialog.this.txtEmail.getText(), JebIoLoginDialog.this.txtPassword.getText());
                if (user != null) {
                    StringBuilder sb = new StringBuilder();
                    int level = 30;

                    if (!user.isConfirmed()) {
                        sb.append("[[ Your account has not been confirmed yet. Check for an email from PNF Software to confirm it. ]]\n\n");
                        level = 40;
                    }

                    int score = user.getScore();
                    int sharecount = user.getSharecount();
                    String lastsharets = user.getLastsharets();
                    int receivecount = user.getReceivecount();


                    sb.append(String.format("Your score: %s\n\n", new Object[]{score == 0 ? "N/A" : Integer.toString(score)}));

                    if (sharecount == 0) {
                        sb.append("You haven't shared any sample yet!\n\n");
                    } else {
                        sb.append(String.format("You have shared a total of %d %s.\nYour last contribution was made on %s.\n\n", new Object[]{Integer.valueOf(sharecount),
                                PluralFormatter.countS(Integer.valueOf(sharecount), "sample"), lastsharets}));
                    }

                    if (receivecount == 0) {
                        if (sharecount == 0) {
                            sb.append("You haven't received samples yet.");
                        } else {
                            sb.append("You haven't received samples yet.");
                        }
                    } else {
                        sb.append(String.format("You have received a total of %d %s in exchange for your contribution.", new Object[]{
                                Integer.valueOf(receivecount), PluralFormatter.countS(Integer.valueOf(receivecount), "sample")}));
                    }

                    UI.log(level, JebIoLoginDialog.this.getShell(), "My Profile", sb.toString());
                }
            }
        });
    }

    boolean verify(String email, String password, boolean silentOnSuccess) {
        if (!Validator.isLegalEmail(email)) {
            UI.warn("The provided email is illegal");
            return false;
        }
        if (password.isEmpty()) {
            UI.warn("The password is blank");
            return false;
        }

        JebIoObjectUser user = getUser(email, password);
        if (user == null) {
            return false;
        }

        String apikey = user.getApikey();
        this.textApikey.setText(apikey);
        if (!silentOnSuccess) {
            UI.info("Log in was successful.");
        }
        JebIoUtil.saveCredentials(this.context, new UserCredentials(email, password, apikey));

        this.verified = true;
        return true;
    }

    JebIoObjectUser getUser(String email, String password) {
        final JebIoApiHelper helper = new JebIoApiHelper(this.context.getNetworkUtility(), new UserCredentials(email, password, ""));
        JebIoObjectUser user = (JebIoObjectUser) this.context.executeNetworkTask(new Callable() {
            public JebIoObjectUser call() throws Exception {
                try {
                    return helper.getUser();
                } catch (IOException e) {
                    UI.error("An error occurred.\n\nException: " + e.getMessage());
                }
                return null;
            }
        });

        if (user == null) {
            UI.error("Login failed.");
            return null;
        }
        if (user.getCode() != 0L) {
            UI.error("Login failed.\n\nResponse code: " + user.getCode());
            return null;
        }
        return user;
    }

    protected void okPressed() {
        String email = this.txtEmail.getText();
        String password = this.txtPassword.getText();
        if ((!this.verified) && (!verify(email, password, this.previousCredentials.lookValid()))) {
            this.txtEmail.setFocus();
            return;
        }
        super.okPressed();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\jebio\JebIoLoginDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */