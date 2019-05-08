package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IArtifact;
import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.input.FileInput;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.encoding.HashCalculator;
import com.pnfsoftware.jeb.util.format.Formatter;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ArtifactPropertiesDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(ArtifactPropertiesDialog.class);
    private ILiveArtifact liveArtifact;
    private IArtifact artifact;
    private Text widgetName;
    private StyledText widgetNotes;

    public ArtifactPropertiesDialog(Shell parent, ILiveArtifact liveArtifact) {
        super(parent, S.s(76), true, true);
        this.scrolledContainer = true;
        this.liveArtifact = liveArtifact;
        this.artifact = liveArtifact.getArtifact();
    }

    public Object open() {
        super.open();
        return null;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);
        IInput input = this.artifact.getInput();
        if ((input instanceof FileInput)) {
            FileInput fileInput = (FileInput) input;
            File f = fileInput.getFile();
            if (f == null) {
                String msg = String.format("It appears the input file artifact does not exist.\n\nWould you like to update the artifact path?", new Object[0]);
                if (MessageDialog.openQuestion(this.shell, "Invalid input artifact", msg)) {
                    FileDialog dlg2 = new FileDialog(this.shell, 4096);
                    dlg2.setText("Artifact Path");
                    String path2 = dlg2.open();
                    if (path2 != null) {
                        try {
                            fileInput.setFile(new File(path2));
                        } catch (IOException e1) {
                            UI.error("Cannot set the input artifact.");
                        }
                    }
                }
            }
        }
        new Label(parent, 0).setText(S.s(73) + ": ");
        this.widgetName = new Text(parent, 2052);
        this.widgetName.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetName.setText(this.artifact.getName());
        this.widgetName.selectAll();
        this.widgetName.setFocus();
        new Label(parent, 0).setText(S.s(214) + ": ");
        Text widgetCtime = new Text(parent, 2060);
        widgetCtime.setLayoutData(UIUtil.createGridDataFillHorizontally());
        DateFormat df = DateFormat.getDateTimeInstance();
        String str_ctime = df.format(new Date(this.artifact.getCreationTimestamp()));
        String str_tz = df.getTimeZone().getDisplayName(false, 0);
        widgetCtime.setText(str_ctime + " " + str_tz);
        widgetCtime.selectAll();
        String hashMd5 = "?";
        String hashSha1 = "?";
        String hashSha256 = "?";
        long inputSize = input.getCurrentSize();
        int warnSizeMb = 256;
        boolean skipHashComp = false;
        if (inputSize >= 268435456L) {
            skipHashComp = MessageDialog.openQuestion(this.shell, S.s(821), "The input size is very large. Computing message digests may take a long time.\n\nWould you like to skip hash computations?");
        }
        if (!skipHashComp) {
            try {
                InputStream stream = input.getStream();
                Throwable localThrowable3 = null;
                try {
                    HashCalculator h = new HashCalculator(stream, 28);
                    if ((h.compute()) && (h.getSize() == inputSize)) {
                        hashMd5 = Formatter.byteArrayToHexString(h.getMd5());
                        hashSha1 = Formatter.byteArrayToHexString(h.getSha1());
                        hashSha256 = Formatter.byteArrayToHexString(h.getSha256());
                    }
                } catch (Throwable localThrowable1) {
                    localThrowable3 = localThrowable1;
                    throw localThrowable1;
                } finally {
                    if (stream != null) if (localThrowable3 != null) try {
                        stream.close();
                    } catch (Throwable localThrowable2) {
                        localThrowable3.addSuppressed(localThrowable2);
                    }
                    else stream.close();
                }
            } catch (IOException e) {
                logger.catching(e);
            }
        }
        new Label(parent, 0).setText(S.s(232) + ": ");
        new Label(parent, 0).setText(inputSize + " bytes");
        Text widgetData = new Text(parent, 2058);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("MD5       %s\n", new Object[]{hashMd5}));
        sb.append(String.format("SHA-1     %s\n", new Object[]{hashSha1}));
        sb.append(String.format("SHA-256   %s", new Object[]{hashSha256}));
        widgetData.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
        widgetData.setText(sb.toString());
        widgetData.setFont(JFaceResources.getTextFont());
        new Label(parent, 0).setText(S.s(685) + ": ");
        new Label(parent, 0).setText("" + this.liveArtifact.getUnits().size());
        new Label(parent, 0).setText(S.s(599) + ": ");
        new Label(parent, 0).setText("");
        this.widgetNotes = new StyledText(parent, 2818);
        this.widgetNotes.setAlwaysShowScrollBars(false);
        this.widgetNotes.setText(this.artifact.getNotes());
        this.widgetNotes.setFont(JFaceResources.getTextFont());
        GridData griddata = UIUtil.createGridDataForText(this.widgetNotes, 50, 3, false);
        griddata.horizontalSpan = 2;
        griddata.grabExcessHorizontalSpace = true;
        griddata.horizontalAlignment = 4;
        griddata.grabExcessVerticalSpace = true;
        griddata.verticalAlignment = 4;
        this.widgetNotes.setLayoutData(griddata);
        UIUtil.disableTabOutput(this.widgetNotes);
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.artifact.setName(this.widgetName.getText());
        this.artifact.setNotes(this.widgetNotes.getText());
        super.onConfirm();
    }
}


