package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CommentDialog extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(CommentDialog.class);
    private StyledText widgetComment;
    private String input;
    private String address;
    private String comment;
    private String description;

    public CommentDialog(Shell parent, String address) {
        super(parent, S.s(203), true, true);
        this.scrolledContainer = true;
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setInitialComment(String comment) {
        this.comment = comment;
    }

    public String open() {
        super.open();
        return this.input;
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        new Label(parent, 0).setText(String.format("Comment at address: %s", this.address));
        this.widgetComment = new StyledText(parent, 2818);
        this.widgetComment.setAlwaysShowScrollBars(false);
        if (this.comment != null) {
            this.widgetComment.setText(this.comment);
        }
        this.widgetComment.selectAll();
        this.widgetComment.setFont(JFaceResources.getTextFont());
        GridData griddata = UIUtil.createGridDataForText(this.widgetComment, 50, 3, false);
        griddata.grabExcessHorizontalSpace = true;
        griddata.horizontalAlignment = 4;
        griddata.grabExcessVerticalSpace = true;
        griddata.verticalAlignment = 4;
        this.widgetComment.setLayoutData(griddata);
        UIUtil.disableTabOutput(this.widgetComment);
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.input = this.widgetComment.getText();
        super.onConfirm();
    }
}


