/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*    */ import org.eclipse.jface.resource.JFaceResources;
/*    */ import org.eclipse.swt.custom.StyledText;
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.eclipse.swt.widgets.Shell;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class CallgraphPackageFilterDialog
        /*    */ extends JebDialog
        /*    */ {
    /*    */   private StyledText wPlist;
    /*    */   private String input;
    /*    */   private String plist;

    /*    */
    /*    */
    public CallgraphPackageFilterDialog(Shell parent)
    /*    */ {
        /* 32 */
        super(parent, "Callgraph Package Filters", true, true);
        /* 33 */
        this.scrolledContainer = true;
        /*    */
    }

    /*    */
    /*    */
    public void setInitialPackageList(String plist) {
        /* 37 */
        this.plist = plist;
        /*    */
    }

    /*    */
    /*    */
    public String open()
    /*    */ {
        /* 42 */
        super.open();
        /* 43 */
        return this.input;
        /*    */
    }

    /*    */
    /*    */
    public void createContents(Composite parent)
    /*    */ {
        /* 48 */
        UIUtil.setStandardLayout(parent);
        /*    */
        /* 50 */
        Label wDesc = new Label(parent, 0);
        /* 51 */
        wDesc.setText("List of packages to be used for callgraph generation");
        /*    */
        /* 53 */
        this.wPlist = new StyledText(parent, 2818);
        /* 54 */
        this.wPlist.setAlwaysShowScrollBars(false);
        /* 55 */
        if (this.plist != null) {
            /* 56 */
            this.wPlist.setText(this.plist);
            /*    */
        }
        /* 58 */
        this.wPlist.selectAll();
        /* 59 */
        this.wPlist.setFont(JFaceResources.getTextFont());
        /* 60 */
        GridData griddata = UIUtil.createGridDataForText(this.wPlist, 50, 6, false);
        /* 61 */
        griddata.grabExcessHorizontalSpace = true;
        /* 62 */
        griddata.horizontalAlignment = 4;
        /* 63 */
        griddata.grabExcessVerticalSpace = true;
        /* 64 */
        griddata.verticalAlignment = 4;
        /* 65 */
        this.wPlist.setLayoutData(griddata);
        /* 66 */
        UIUtil.disableTabOutput(this.wPlist);
        /*    */
        /* 68 */
        createOkayCancelButtons(parent);
        /*    */
    }

    /*    */
    /*    */
    protected void onConfirm()
    /*    */ {
        /* 73 */
        this.input = this.wPlist.getText();
        /* 74 */
        super.onConfirm();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\CallgraphPackageFilterDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */