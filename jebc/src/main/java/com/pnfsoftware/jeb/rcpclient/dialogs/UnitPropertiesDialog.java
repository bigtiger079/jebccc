package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UnitPropertiesDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(UnitPropertiesDialog.class);
    private IUnit unit;
    private Text widgetName;
    private StyledText widgetNotes;

    public UnitPropertiesDialog(Shell parent, IUnit unit) {
        super(parent, S.s(787), true, true);
        this.scrolledContainer = true;
        this.unit = unit;
    }

    public Object open() {
        super.open();
        return null;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);
        new Label(parent, 0).setText(S.s(786) + ": ");
        this.widgetName = new Text(parent, 2052);
        this.widgetName.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetName.setText(this.unit.getName());
        this.widgetName.selectAll();
        this.widgetName.setFocus();
        new Label(parent, 0).setText("Original name: ");
        Text widgetName0 = new Text(parent, 2060);
        widgetName0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        widgetName0.setText(Strings.safe(this.unit.getRealName(), "N/A"));
        widgetName0.selectAll();
        new Label(parent, 0).setText(S.s(788) + ": ");
        Text widgetType = new Text(parent, 2060);
        widgetType.setLayoutData(UIUtil.createGridDataFillHorizontally());
        widgetType.setText(this.unit.getFormatType());
        widgetType.selectAll();
        new Label(parent, 0).setText(S.s(214) + ": ");
        Text widgetCtime = new Text(parent, 2060);
        widgetCtime.setLayoutData(UIUtil.createGridDataFillHorizontally());
        DateFormat df = DateFormat.getDateTimeInstance();
        String str_ctime = df.format(new Date(this.unit.getCreationTimestamp()));
        String str_tz = df.getTimeZone().getDisplayName(false, 0);
        widgetCtime.setText(str_ctime + " " + str_tz);
        widgetCtime.selectAll();
        new Label(parent, 0).setText(S.s(748) + ": ");
        Text widgetStatus = new Text(parent, 2060);
        widgetStatus.setLayoutData(UIUtil.createGridDataFillHorizontally());
        String status = this.unit.getStatus();
        if (status == null) {
            status = "N/A";
        }
        if (!this.unit.isProcessed()) {
            status = status + " (" + S.s(794) + ")";
        }
        widgetStatus.setText(status);
        new Label(parent, 0).setText(S.s(599) + ": ");
        new Label(parent, 0).setText("");
        this.widgetNotes = new StyledText(parent, 2818);
        this.widgetNotes.setAlwaysShowScrollBars(false);
        this.widgetNotes.setText(this.unit.getNotes());
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
        this.unit.setName(this.widgetName.getText());
        this.unit.setNotes(this.widgetNotes.getText());
        super.onConfirm();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\UnitPropertiesDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */