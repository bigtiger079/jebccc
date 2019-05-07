
package com.pnfsoftware.jeb.rcpclient.handlers.edition;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.OptionsDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;


public class EditOptionsHandler
        extends JebBaseHandler {

    public EditOptionsHandler() {

        super(null, S.s(533), "Edit the front-end and back-end options for JEB", "jeb1/icon-options.png");

        setAccelerator(SWT.MOD1 | SWT.MOD2 | SWT.MOD3 | 0x4F);

    }


    public boolean canExecute() {

        return true;

    }


    public void execute() {

        this.context.getTelemetry().record("handlerGeneralOptions");


        boolean lazyInit = !this.context.getPropertyManager().getBoolean("ui.AlwaysLoadFragments");

        boolean expandOnFiltering = this.context.getPropertyManager().getBoolean(".ui.ExpandTreeNodesOnFiltering");


        IPropertyManager clientPM = this.context.getPropertyManager();

        IPropertyManager corePM = this.context.getEnginesContext().getPropertyManager();


        IPropertyManager prjPM = this.context.getOpenedProject() == null ? null : this.context.getOpenedProject().getPropertyManager();

        OptionsDialog dlg = new OptionsDialog(this.shell, this.context.getProperties(), clientPM, corePM, prjPM);


        dlg.setLazyInit(lazyInit);

        dlg.setExpandOnFiltering(expandOnFiltering);

        if (dlg.open().booleanValue()) {

            MessageDialog.openWarning(this.shell, S.s(821), "The engines settings were modified.\n\nPlease restart JEB for all changes to take effect.");

        }

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\edition\EditOptionsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */