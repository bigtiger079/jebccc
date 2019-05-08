
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

public class FileAddHandler
        extends JebBaseHandler {
    public FileAddHandler() {
        super(null, S.s(460), 0, "Add an artifact to the existing project", "eclipse/add_obj.png", SWT.MOD1 | SWT.MOD2 | 0x4F);
    }

    public boolean canExecute() {
        return this.context.getOpenedProject() != null;
    }

    public void execute() {
        FileDialog dlg = new FileDialog(this.shell, 4096);
        dlg.setText(S.s(460));
        dlg.setFilterPath(this.context.getDefaultPathForDialog("dlgJebFileAdd"));
        String path = dlg.open();
        if (path != null) {
            this.context.getTelemetry().record("handlerAddArtifact", "previousCount", this.context.getOpenedProject().getArtifactCount() + "");
            String newDefaultDir = new File(path).getParent();
            this.context.setDefaultPathForDialog("dlgJebFileAdd", newDefaultDir);
            IRuntimeProject project = this.context.getOpenedProject();
            this.context.processFileArtifact(this.shell, project, path);
        }
    }
}


