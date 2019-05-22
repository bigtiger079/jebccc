package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ProjectPropertiesDialog extends JebDialog {
    private IRuntimeProject project;
    private Text widgetName;
    private StyledText widgetNotes;

    public ProjectPropertiesDialog(Shell parent, IRuntimeProject project) {
        super(parent, S.s(658), true, true);
        this.scrolledContainer = true;
        if (project == null) {
            throw new NullPointerException();
        }
        this.project = project;
    }

    public Object open() {
        super.open();
        return null;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);
        new Label(parent, 0).setText(S.s(657) + ": ");
        this.widgetName = new Text(parent, 2052);
        this.widgetName.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetName.setText(this.project.getName());
        this.widgetName.selectAll();
        this.widgetName.setFocus();
        new Label(parent, 0).setText(S.s(214) + ": ");
        Text widgetCtime = new Text(parent, 2060);
        widgetCtime.setLayoutData(UIUtil.createGridDataFillHorizontally());
        DateFormat df = DateFormat.getDateTimeInstance();
        String str_ctime = df.format(new Date(this.project.getCreationTimestamp()));
        String str_tz = df.getTimeZone().getDisplayName(false, 0);
        widgetCtime.setText(str_ctime + " " + str_tz);
        widgetCtime.selectAll();
        new Label(parent, 0).setText(S.s(428) + ": ");
        Text widgetMtime = new Text(parent, 2060);
        widgetMtime.setLayoutData(UIUtil.createGridDataFillHorizontally());
        long ts = this.project.getRecordedTimestamp();
        if (ts == 0L) {
            widgetMtime.setText("N/A");
        } else {
            String str_mtime = df.format(new Date(ts));
            widgetMtime.setText(str_mtime + " " + str_tz);
        }
        widgetMtime.selectAll();
        new Label(parent, 0).setText(S.s(422) + ": ");
        Text widgetKey = new Text(parent, 2060);
        widgetKey.setLayoutData(UIUtil.createGridDataFillHorizontally());
        widgetKey.setText(this.project.getKey());
        widgetKey.selectAll();
        new Label(parent, 0).setText(S.s(78) + ": ");
        new Label(parent, 0).setText("" + this.project.getLiveArtifacts().size());
        new Label(parent, 0).setText(S.s(599) + ": ");
        new Label(parent, 0).setText("");
        this.widgetNotes = new StyledText(parent, 2818);
        this.widgetNotes.setAlwaysShowScrollBars(false);
        this.widgetNotes.setText(this.project.getNotes());
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
        this.project.setName(this.widgetName.getText());
        this.project.setNotes(this.widgetNotes.getText());
        super.onConfirm();
    }
}


