/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*    */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import java.io.File;
/*    */ import org.eclipse.swt.SWT;
/*    */ import org.eclipse.swt.widgets.FileDialog;

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
/*    */ public class FileAddHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /*    */
    public FileAddHandler()
    /*    */ {
        /* 27 */
        super(null, S.s(460), 0, "Add an artifact to the existing project", "eclipse/add_obj.png", SWT.MOD1 | SWT.MOD2 | 0x4F);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 32 */
        return this.context.getOpenedProject() != null;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 37 */
        FileDialog dlg = new FileDialog(this.shell, 4096);
        /* 38 */
        dlg.setText(S.s(460));
        /* 39 */
        dlg.setFilterPath(this.context.getDefaultPathForDialog("dlgJebFileAdd"));
        /*    */
        /* 41 */
        String path = dlg.open();
        /* 42 */
        if (path != null) {
            /* 43 */
            this.context.getTelemetry().record("handlerAddArtifact", "previousCount", this.context.getOpenedProject().getArtifactCount() + "");
            /*    */
            /* 45 */
            String newDefaultDir = new File(path).getParent();
            /* 46 */
            this.context.setDefaultPathForDialog("dlgJebFileAdd", newDefaultDir);
            /*    */
            /* 48 */
            IRuntimeProject project = this.context.getOpenedProject();
            /* 49 */
            this.context.processFileArtifact(this.shell, project, path);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileAddHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */