/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.edition;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.core.IEnginesContext;
/*    */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*    */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.OptionsDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import org.eclipse.jface.dialogs.MessageDialog;
/*    */ import org.eclipse.swt.SWT;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class EditOptionsHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public EditOptionsHandler()
    /*    */ {
        /* 26 */
        super(null, S.s(533), "Edit the front-end and back-end options for JEB", "jeb1/icon-options.png");
        /* 27 */
        setAccelerator(SWT.MOD1 | SWT.MOD2 | SWT.MOD3 | 0x4F);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 32 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 37 */
        this.context.getTelemetry().record("handlerGeneralOptions");
        /*    */
        /* 39 */
        boolean lazyInit = !this.context.getPropertyManager().getBoolean("ui.AlwaysLoadFragments");
        /* 40 */
        boolean expandOnFiltering = this.context.getPropertyManager().getBoolean(".ui.ExpandTreeNodesOnFiltering");
        /*    */
        /* 42 */
        IPropertyManager clientPM = this.context.getPropertyManager();
        /* 43 */
        IPropertyManager corePM = this.context.getEnginesContext().getPropertyManager();
        /*    */
        /* 45 */
        IPropertyManager prjPM = this.context.getOpenedProject() == null ? null : this.context.getOpenedProject().getPropertyManager();
        /* 46 */
        OptionsDialog dlg = new OptionsDialog(this.shell, this.context.getProperties(), clientPM, corePM, prjPM);
        /*    */
        /* 48 */
        dlg.setLazyInit(lazyInit);
        /* 49 */
        dlg.setExpandOnFiltering(expandOnFiltering);
        /* 50 */
        if (dlg.open().booleanValue()) {
            /* 51 */
            MessageDialog.openWarning(this.shell, S.s(821), "The engines settings were modified.\n\nPlease restart JEB for all changes to take effect.");
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditOptionsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */