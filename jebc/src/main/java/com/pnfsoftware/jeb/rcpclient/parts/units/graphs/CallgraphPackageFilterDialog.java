package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CallgraphPackageFilterDialog extends JebDialog {
    private StyledText wPlist;
    private String input;
    private String plist;

    public CallgraphPackageFilterDialog(Shell parent) {
        super(parent, "Callgraph Package Filters", true, true);
        this.scrolledContainer = true;
    }

    public void setInitialPackageList(String plist) {
        this.plist = plist;
    }

    public String open() {
        super.open();
        return this.input;
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        Label wDesc = new Label(parent, 0);
        wDesc.setText("List of packages to be used for callgraph generation");
        this.wPlist = new StyledText(parent, 2818);
        this.wPlist.setAlwaysShowScrollBars(false);
        if (this.plist != null) {
            this.wPlist.setText(this.plist);
        }
        this.wPlist.selectAll();
        this.wPlist.setFont(JFaceResources.getTextFont());
        GridData griddata = UIUtil.createGridDataForText(this.wPlist, 50, 6, false);
        griddata.grabExcessHorizontalSpace = true;
        griddata.horizontalAlignment = 4;
        griddata.grabExcessVerticalSpace = true;
        griddata.verticalAlignment = 4;
        this.wPlist.setLayoutData(griddata);
        UIUtil.disableTabOutput(this.wPlist);
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.input = this.wPlist.getText();
        super.onConfirm();
    }
}


