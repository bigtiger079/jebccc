package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;


public class DataFrameDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(DataFrameDialog.class);

    private String msg;
    private boolean displayIndex = false;
    private DataFrame df;
    private DataFrameView dfv;
    private int selectedIndex = -1;

    public DataFrameDialog(Shell parent, String caption, boolean modal, String widgetName) {
        super(parent, caption, true, modal, widgetName);


        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
    }

    public void setDisplayIndex(boolean displayIndex) {
        this.displayIndex = displayIndex;
    }

    public void setDataFrame(DataFrame df) {
        this.df = df;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }


    public Integer open() {
        if (this.df == null) {
            throw new IllegalStateException("The dataframe model was not set");
        }

        super.open();
        return Integer.valueOf(this.selectedIndex);
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);

        if (this.msg != null) {
            UIUtil.createWrappedLabelInGridLayout(parent, 0, this.msg, 1);
        }

        this.dfv = new DataFrameView(parent, this.df, this.displayIndex);
        this.dfv.addExtraEntriesToContextMenu();
        this.dfv.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, true));


        this.dfv.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                DataFrameDialog.this.onConfirm();
            }

        });
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.selectedIndex = this.dfv.getSelectedRow();
        super.onConfirm();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\DataFrameDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */