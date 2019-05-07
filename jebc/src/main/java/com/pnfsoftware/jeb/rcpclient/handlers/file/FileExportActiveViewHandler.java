/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*     */
/*     */

import com.pnfsoftware.jeb.client.Licensing;
/*     */ import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ProgressMonitorHideableDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabFolderView;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
/*     */ import com.pnfsoftware.jeb.util.io.IO;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.eclipse.jface.dialogs.MessageDialog;
/*     */ import org.eclipse.swt.widgets.FileDialog;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class FileExportActiveViewHandler
        /*     */ extends JebBaseHandler
        /*     */ {
    /*  34 */   private static final ILogger logger = GlobalLog.getLogger(FileExportActiveViewHandler.class);
    /*     */
    /*  36 */   private static ProgressMonitorHideableDialog progressBar = null;

    /*     */
    /*     */
    public FileExportActiveViewHandler() {
        /*  39 */
        super(null, "Active View...", null, null);
        /*     */
    }

    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  44 */
        if (this.part == null) {
            /*  45 */
            return false;
            /*     */
        }
        /*     */
        /*  48 */
        UnitPartManager pman = this.context.getPartManager().getUnitPartManager(this.part);
        /*  49 */
        if (pman == null) {
            /*  50 */
            return false;
            /*     */
        }
        /*     */
        /*  53 */
        return canExport(pman.getActiveFragment());
        /*     */
    }

    /*     */
    /*     */
    public void execute()
    /*     */ {
        /*  58 */
        if (Licensing.isDemoBuild()) {
            /*  59 */
            MessageDialog.openWarning(this.shell, S.s(249), S.s(265));
            /*  60 */
            return;
            /*     */
        }
        /*     */
        /*  63 */
        if ((progressBar != null) && (progressBar.isAlive())) {
            /*  64 */
            progressBar.setVisible(true);
            /*  65 */
            return;
            /*     */
        }
        /*     */
        /*  68 */
        UnitPartManager pman = this.context.getPartManager().getUnitPartManager(this.part);
        /*  69 */
        if (pman == null) {
            /*  70 */
            return;
            /*     */
        }
        /*     */
        /*  73 */
        AbstractUnitFragment<?> fragment = pman.getActiveFragment();
        /*  74 */
        FileDialog dlg = new FileDialog(this.shell, 8192);
        /*  75 */
        dlg.setText(S.s(339));
        /*  76 */
        dlg.setOverwrite(true);
        /*  77 */
        dlg.setFileName(pman.getFolder().getCurrentEntryName() + getExtension(fragment));
        /*  78 */
        String filepath = dlg.open();
        /*  79 */
        if (filepath == null) {
            /*  80 */
            return;
            /*     */
        }
        /*     */
        /*  83 */
        logger.i("%s: %s", new Object[]{S.s(340), filepath});
        /*     */
        /*  85 */
        byte[] content = fragment.export();
        /*  86 */
        if (content != null) {
            /*     */
            try {
                /*  88 */
                IO.writeFile(new File(filepath), content);
                /*  89 */
                UI.info(this.shell, "Export successful", "Exported file: " + filepath);
                /*     */
            }
            /*     */ catch (IOException e) {
                /*  92 */
                logger.catching(e);
                /*  93 */
                UI.error(this.shell, "Export failed", "Can not write file " + filepath);
                /*     */
            }
            /*     */
            /*     */
        } else {
            /*  97 */
            UI.error(this.shell, "Export not supported", "Export is not supported for the current view.");
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private boolean canExport(AbstractUnitFragment<?> fragment) {
        /* 102 */
        if (fragment == null) {
            /* 103 */
            return false;
            /*     */
        }
        /*     */
        /* 106 */
        switch (fragment.getFragmentType()) {
            /*     */
            case TEXT:
                /*     */
            case TABLE:
                /*     */
            case TREE:
                /*     */
            case IMAGE:
                /* 111 */
                return true;
            /*     */
        }
        /*     */
        /* 114 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private String getExtension(AbstractUnitFragment<?> fragment) {
        /* 118 */
        String extension = fragment.getExportExtension();
        /* 119 */
        if (extension == null) {
            /* 120 */
            return ".bin";
            /*     */
        }
        /* 122 */
        return extension;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileExportActiveViewHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */