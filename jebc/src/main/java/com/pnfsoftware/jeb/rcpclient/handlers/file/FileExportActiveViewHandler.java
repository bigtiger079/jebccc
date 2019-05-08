package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.ProgressMonitorHideableDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabFolderView;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;

public class FileExportActiveViewHandler extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(FileExportActiveViewHandler.class);
    private static ProgressMonitorHideableDialog progressBar = null;

    public FileExportActiveViewHandler() {
        super(null, "Active View...", null, null);
    }

    public boolean canExecute() {
        if (this.part == null) {
            return false;
        }
        UnitPartManager pman = this.context.getPartManager().getUnitPartManager(this.part);
        if (pman == null) {
            return false;
        }
        return canExport(pman.getActiveFragment());
    }

    public void execute() {
        if (Licensing.isDemoBuild()) {
            MessageDialog.openWarning(this.shell, S.s(249), S.s(265));
            return;
        }
        if ((progressBar != null) && (progressBar.isAlive())) {
            progressBar.setVisible(true);
            return;
        }
        UnitPartManager pman = this.context.getPartManager().getUnitPartManager(this.part);
        if (pman == null) {
            return;
        }
        AbstractUnitFragment<?> fragment = pman.getActiveFragment();
        FileDialog dlg = new FileDialog(this.shell, 8192);
        dlg.setText(S.s(339));
        dlg.setOverwrite(true);
        dlg.setFileName(pman.getFolder().getCurrentEntryName() + getExtension(fragment));
        String filepath = dlg.open();
        if (filepath == null) {
            return;
        }
        logger.i("%s: %s", new Object[]{S.s(340), filepath});
        byte[] content = fragment.export();
        if (content != null) {
            try {
                IO.writeFile(new File(filepath), content);
                UI.info(this.shell, "Export successful", "Exported file: " + filepath);
            } catch (IOException e) {
                logger.catching(e);
                UI.error(this.shell, "Export failed", "Can not write file " + filepath);
            }
        } else {
            UI.error(this.shell, "Export not supported", "Export is not supported for the current view.");
        }
    }

    private boolean canExport(AbstractUnitFragment<?> fragment) {
        if (fragment == null) {
            return false;
        }
        switch (fragment.getFragmentType()) {
            case TEXT:
            case TABLE:
            case TREE:
            case IMAGE:
                return true;
        }
        return false;
    }

    private String getExtension(AbstractUnitFragment<?> fragment) {
        String extension = fragment.getExportExtension();
        if (extension == null) {
            return ".bin";
        }
        return extension;
    }
}


