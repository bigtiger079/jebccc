/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*     */
/*     */

import com.pnfsoftware.jeb.client.Licensing;
/*     */ import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.IArtifact;
/*     */ import com.pnfsoftware.jeb.core.ILiveArtifact;
/*     */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*     */ import com.pnfsoftware.jeb.core.exceptions.JebException;
/*     */ import com.pnfsoftware.jeb.core.input.IInput;
/*     */ import com.pnfsoftware.jeb.core.units.IBinaryUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.WellKnownUnitTypes;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*     */ import com.pnfsoftware.jeb.util.io.IO;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import org.apache.commons.io.FileUtils;
/*     */ import org.eclipse.jface.dialogs.MessageDialog;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;

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
/*     */ public class FileExportAllBinaryUnitsHandler
        /*     */ extends JebBaseHandler
        /*     */ {
    /*  39 */   private static final ILogger logger = GlobalLog.getLogger(FileExportAllBinaryUnitsHandler.class);

    /*     */
    /*     */
    public FileExportAllBinaryUnitsHandler() {
        /*  42 */
        super(null, "All Binary Units...", null, null);
        /*     */
    }

    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  47 */
        IRuntimeProject prj = this.context.getOpenedProject();
        /*  48 */
        return (prj != null) && (prj.getLiveArtifacts() != null);
        /*     */
    }

    /*     */
    /*     */
    public void execute()
    /*     */ {
        /*  53 */
        if (Licensing.isDemoBuild()) {
            /*  54 */
            MessageDialog.openWarning(this.shell, S.s(249), S.s(265));
            /*  55 */
            return;
            /*     */
        }
        /*     */
        /*  58 */
        IRuntimeProject prj = this.context.getOpenedProject();
        /*     */
        /*     */
        /*     */
        /*  62 */
        DirectoryDialog dlg = new DirectoryDialog(this.shell, 8192);
        /*  63 */
        dlg.setText(S.s(340));
        /*  64 */
        String filepath = dlg.open();
        /*  65 */
        if (filepath == null) {
            /*  66 */
            return;
            /*     */
        }
        /*     */
        /*  69 */
        logger.i("%s: %s", new Object[]{S.s(340), filepath});
        /*     */
        /*  71 */
        File path = new File(filepath);
        /*  72 */
        List<ILiveArtifact> artifacts = prj.getLiveArtifacts();
        /*     */
        try {
            /*  74 */
            for (ILiveArtifact art : artifacts) {
                /*  75 */
                String dirName = IO.escapeFileName(art.getArtifact().getName());
                /*  76 */
                int dotIndex = dirName.lastIndexOf('.');
                /*  77 */
                if (dotIndex != -1) {
                    /*  78 */
                    dirName = dirName.substring(0, dotIndex);
                    /*     */
                }
                /*  80 */
                processUnits(art.getUnits(), new File(path, dirName + "-export"));
                /*     */
            }
            /*     */
        }
        /*     */ catch (JebException e) {
            /*  84 */
            MessageDialog.openError(this.shell, "Can not export binaries", e.getMessage());
            /*  85 */
            return;
            /*     */
        }
        /*  87 */
        UI.info(this.shell, "Export successful", "Binary files can be found in " + filepath + File.separator + "${artifactName}-export");
        /*     */
    }

    /*     */
    /*     */
    private void processUnits(List<? extends IUnit> units, File initPath) throws JebException
    /*     */ {
        /*  92 */
        File path = getNewPathName(initPath, "_", true);
        /*  93 */
        if (units != null) {
            /*  94 */
            for (IUnit unit : units) {
                /*  95 */
                String subDir = IO.escapeFileName(unit.getName());
                /*  96 */
                if ((unit instanceof IBinaryUnit)) {
                    /*     */
                    try {
                        /*  98 */
                        String fileName = subDir;
                        /*     */
                        /* 100 */
                        if ((!fileName.contains(".")) || ((unit.getChildren() != null) && (unit.getChildren().size() > 0)))
                            /*     */ {
                            /* 102 */
                            String extension = '.' + WellKnownUnitTypes.toCommonExtension(unit.getFormatType());
                            /* 103 */
                            if ((!extension.isEmpty()) && (fileName.endsWith(extension)))
                                /*     */ {
                                /* 105 */
                                subDir = subDir.substring(0, subDir.length() - extension.length());
                                /*     */
                            }
                            /*     */
                            else {
                                /* 108 */
                                fileName = fileName + IO.escapeFileName(extension);
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                        /*     */
                        /* 113 */
                        if ((!path.exists()) && (!path.mkdirs()))
                            /*     */ {
                            /* 115 */
                            path = new File(initPath.getParentFile(), IO.escapeFileNameStrict(initPath.getName()));
                            /* 116 */
                            path = getNewPathName(path, "_", true);
                            /* 117 */
                            if ((!path.exists()) && (!path.mkdirs()))
                                /*     */ {
                                /* 119 */
                                throw new JebException("Can not write to directory " + path);
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                        /* 123 */
                        File target = new File(path, fileName);
                        /*     */
                        /*     */
                        /* 126 */
                        target = getNewPathName(target, ".", false);
                        /* 127 */
                        if (!target.createNewFile())
                            /*     */ {
                            /* 129 */
                            target = new File(path, IO.escapeFileNameStrict(fileName));
                            /* 130 */
                            target = getNewPathName(target, ".", false);
                            /* 131 */
                            if (!target.createNewFile())
                                /*     */ {
                                /* 133 */
                                throw new JebException("Can not write to directory " + path);
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                        /* 137 */
                        FileUtils.copyInputStreamToFile(((IBinaryUnit) unit).getInput().getStream(), target);
                        /*     */
                    }
                    /*     */ catch (IOException e) {
                        /* 140 */
                        logger.catching(e);
                        /*     */
                    }
                    /*     */
                }
                /* 143 */
                processUnits(unit.getChildren(), new File(path, subDir));
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private File getNewPathName(File path, String separator, boolean checkDirectory) {
        /* 149 */
        int i = 0;
        /* 150 */
        while ((path.exists()) && ((!checkDirectory) || (!path.isDirectory()))) {
            /* 151 */
            path = new File(path.getParentFile(), path.getName() + separator + i);
            /* 152 */
            i++;
            /*     */
        }
        /* 154 */
        return path;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileExportAllBinaryUnitsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */