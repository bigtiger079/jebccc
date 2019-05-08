package com.pnfsoftware.jeb.rcpclient.dialogs.jebio;

import com.pnfsoftware.jeb.client.jebio.JebIoApiHelper;
import com.pnfsoftware.jeb.client.jebio.JebIoObjectFile;
import com.pnfsoftware.jeb.client.jebio.JebIoObjectFile.UserDetails;
import com.pnfsoftware.jeb.client.jebio.JebIoObjectUser;
import com.pnfsoftware.jeb.client.jebio.JebIoUtil;
import com.pnfsoftware.jeb.client.jebio.SampleDetermination;
import com.pnfsoftware.jeb.client.jebio.UserCredentials;
import com.pnfsoftware.jeb.core.IArtifact;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileShareHandler;
import com.pnfsoftware.jeb.util.format.PluralFormatter;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class JebIoShareDialog
        extends TitleAreaDialog {
    private static final ILogger logger = GlobalLog.getLogger(JebIoShareDialog.class);
    private static final int SETTINGS_ID = 1025;
    RcpClientContext context;
    IArtifact artifact;
    String sha256;
    Text txtHash;
    Text txtName;
    Text txtComments;
    Combo wDetermination;

    public JebIoShareDialog(Shell shell, RcpClientContext context, IArtifact artifact) {
        super(shell);
        this.context = context;
        this.artifact = artifact;
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
                new JebIoHelpDialog(JebIoShareDialog.this.getShell()).open();
            }
        });
    }

    protected Control createDialogArea(Composite parent) {
        getShell().setText("Share");
        setTitle("Share a sample on the JEB Malware Sharing Network");
        setMessage("The name and comments fields are optional, feel free to redact them.", 1);
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, 0);
        container.setLayoutData(new GridData(4, 4, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        try {
            this.sha256 = FileShareHandler.calculateArtifactHash(this.artifact);
        } catch (IOException e) {
            UI.error("The sample data cannot be retrieved");
            close();
        }
        String filename = this.artifact.getName();
        String comments = "";
        SampleDetermination determination = SampleDetermination.UNKNOWN;
        JebIoObjectFile fileinfo = getUploadedFileInfo(this.context, this.sha256);
        if ((fileinfo != null) && (fileinfo.getUserdetails() != null)) {
            JebIoObjectFile.UserDetails ud = fileinfo.getUserdetails();
            setMessage("You shared this sample on " + ud.getCreated(), 1);
            filename = ud.getFilename();
            comments = ud.getComments();
            determination = ud.getDetermination();
        }
        createHashField(container, this.sha256);
        createNameField(container, filename);
        createCommentsField(container, comments);
        createDeterminationField(container, determination);
        this.txtName.setFocus();
        this.txtName.selectAll();
        return area;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        parent.setLayoutData(new GridData(4, 16777216, true, false));
        createButton(parent, 1025, "Settings", false);
        Label spacer = new Label(parent, 0);
        spacer.setLayoutData(new GridData(4, 16777216, true, false));
        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns += 1;
        layout.makeColumnsEqualWidth = false;
        createButton(parent, 0, "Share", true);
        createButton(parent, 1, "Cancel", false);
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == 1025) {
            JebIoLoginDialog dlg = new JebIoLoginDialog(getShell(), this.context);
            dlg.open();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    private void createHashField(Composite container, String defValue) {
        Label lbtFirstName = new Label(container, 0);
        lbtFirstName.setText("SHA-256:");
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        this.txtHash = new Text(container, 2056);
        this.txtHash.setLayoutData(data);
        this.txtHash.setText(Strings.safe(defValue));
    }

    private void createNameField(Composite container, String defValue) {
        Label lbtFirstName = new Label(container, 0);
        lbtFirstName.setText("Sample name:");
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        this.txtName = new Text(container, 2048);
        this.txtName.setLayoutData(data);
        this.txtName.setText(Strings.safe(defValue));
    }

    private void createCommentsField(Composite container, String defValue) {
        Label lbtFirstName = new Label(container, 0);
        lbtFirstName.setText("Comments:");
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        this.txtComments = new Text(container, 2048);
        this.txtComments.setLayoutData(data);
        this.txtComments.setText(Strings.safe(defValue));
    }

    private void createDeterminationField(Composite container, SampleDetermination determination) {
        Label lbtFirstName = new Label(container, 0);
        lbtFirstName.setText("Determination:");
        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = 4;
        this.wDetermination = new Combo(container, 12);
        this.wDetermination.setLayoutData(data);
        int defaultIndex = -1;
        int i = 0;
        for (SampleDetermination det : SampleDetermination.values()) {
            if (det == determination) {
                defaultIndex = i;
            }
            this.wDetermination.add(det.getMessage());
            i++;
        }
        this.wDetermination.select(defaultIndex);
    }

    protected void okPressed() {
        UserCredentials creds = JebIoUtil.retrieveCredentials(this.context);
        if (!creds.lookValid()) {
            UI.error("Your credentials seem invalid");
            return;
        }
        File f = null;
        try {
            f = FileShareHandler.getArtifactFile(this.artifact);
        } catch (IOException e) {
            UI.error("Cannot process the sample file");
            return;
        }
        SampleDetermination det = SampleDetermination.UNKNOWN;
        if ((this.wDetermination.getSelectionIndex() >= 0) &&
                (this.wDetermination.getSelectionIndex() < SampleDetermination.values().length)) {
            det = SampleDetermination.values()[this.wDetermination.getSelectionIndex()];
        }
        final File _f = f;
        final String _name = this.txtName.getText();
        final String _comments = this.txtComments.getText();
        final SampleDetermination _det = det;
        final JebIoApiHelper helper = new JebIoApiHelper(this.context.getNetworkUtility(), creds);
        Integer retcode = (Integer) this.context.executeNetworkTask(new Callable() {
            public Integer call() throws Exception {
                try {
                    return Integer.valueOf(helper.shareFile(_f, _name, _comments, _det, false));
                } catch (IOException e) {
                    UI.error("An error occurred.\n\nException: " + e.getMessage());
                }
                return null;
            }
        });
        if (retcode == null) {
            return;
        }
        if (retcode.intValue() < 0) {
            UI.error("The sample was not successfully shared. Are your credentials valid?\n\nResponse code: " + retcode);
            return;
        }
        try {
            JebIoObjectUser user = helper.getUser();
            long sharecount = user.getSharecount();
            UI.info(String.format("Thank you!\n\nYou have shared a total of %d %s.", new Object[]{Long.valueOf(sharecount),
                    PluralFormatter.countS(Long.valueOf(sharecount), "sample")}));
        } catch (Exception e) {
            UI.info("Thank you!");
        }
        super.okPressed();
    }

    private static JebIoObjectFile getUploadedFileInfo(RcpClientContext context, final String sha256) {
        UserCredentials creds = JebIoUtil.retrieveCredentials(context);
        if (!creds.lookValid()) {
            UI.error("Your credentials seem invalid");
            return null;
        }
        final JebIoApiHelper helper = new JebIoApiHelper(context.getNetworkUtility(), creds);
        return (JebIoObjectFile) context.executeNetworkTask(new Callable() {
            public JebIoObjectFile call() throws Exception {
                try {
                    return helper.getFile(sha256);
                } catch (IOException e) {
                    UI.error("An error occurred.\n\nException: " + e.getMessage());
                }
                return null;
            }
        });
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\jebio\JebIoShareDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */