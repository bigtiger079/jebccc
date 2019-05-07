
package com.pnfsoftware.jeb.rcpclient.handlers.file;


import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IArtifact;
import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.exceptions.JebException;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.units.IBinaryUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.WellKnownUnitTypes;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;


public class FileExportAllBinaryUnitsHandler
        extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(FileExportAllBinaryUnitsHandler.class);


    public FileExportAllBinaryUnitsHandler() {

        super(null, "All Binary Units...", null, null);

    }


    public boolean canExecute() {

        IRuntimeProject prj = this.context.getOpenedProject();

        return (prj != null) && (prj.getLiveArtifacts() != null);

    }


    public void execute() {

        if (Licensing.isDemoBuild()) {

            MessageDialog.openWarning(this.shell, S.s(249), S.s(265));

            return;

        }


        IRuntimeProject prj = this.context.getOpenedProject();


        DirectoryDialog dlg = new DirectoryDialog(this.shell, 8192);

        dlg.setText(S.s(340));

        String filepath = dlg.open();

        if (filepath == null) {

            return;

        }


        logger.i("%s: %s", new Object[]{S.s(340), filepath});


        File path = new File(filepath);

        List<ILiveArtifact> artifacts = prj.getLiveArtifacts();

        try {

            for (ILiveArtifact art : artifacts) {

                String dirName = IO.escapeFileName(art.getArtifact().getName());

                int dotIndex = dirName.lastIndexOf('.');

                if (dotIndex != -1) {

                    dirName = dirName.substring(0, dotIndex);

                }

                processUnits(art.getUnits(), new File(path, dirName + "-export"));

            }

        } catch (JebException e) {

            MessageDialog.openError(this.shell, "Can not export binaries", e.getMessage());

            return;

        }

        UI.info(this.shell, "Export successful", "Binary files can be found in " + filepath + File.separator + "${artifactName}-export");

    }


    private void processUnits(List<? extends IUnit> units, File initPath) throws JebException {

        File path = getNewPathName(initPath, "_", true);

        if (units != null) {

            for (IUnit unit : units) {

                String subDir = IO.escapeFileName(unit.getName());

                if ((unit instanceof IBinaryUnit)) {

                    try {

                        String fileName = subDir;


                        if ((!fileName.contains(".")) || ((unit.getChildren() != null) && (unit.getChildren().size() > 0))) {

                            String extension = '.' + WellKnownUnitTypes.toCommonExtension(unit.getFormatType());

                            if ((!extension.isEmpty()) && (fileName.endsWith(extension))) {

                                subDir = subDir.substring(0, subDir.length() - extension.length());

                            } else {

                                fileName = fileName + IO.escapeFileName(extension);

                            }

                        }


                        if ((!path.exists()) && (!path.mkdirs())) {

                            path = new File(initPath.getParentFile(), IO.escapeFileNameStrict(initPath.getName()));

                            path = getNewPathName(path, "_", true);

                            if ((!path.exists()) && (!path.mkdirs())) {

                                throw new JebException("Can not write to directory " + path);

                            }

                        }


                        File target = new File(path, fileName);


                        target = getNewPathName(target, ".", false);

                        if (!target.createNewFile()) {

                            target = new File(path, IO.escapeFileNameStrict(fileName));

                            target = getNewPathName(target, ".", false);

                            if (!target.createNewFile()) {

                                throw new JebException("Can not write to directory " + path);

                            }

                        }


                        FileUtils.copyInputStreamToFile(((IBinaryUnit) unit).getInput().getStream(), target);

                    } catch (IOException e) {

                        logger.catching(e);

                    }

                }

                processUnits(unit.getChildren(), new File(path, subDir));

            }

        }

    }


    private File getNewPathName(File path, String separator, boolean checkDirectory) {

        int i = 0;

        while ((path.exists()) && ((!checkDirectory) || (!path.isDirectory()))) {

            path = new File(path.getParentFile(), path.getName() + separator + i);

            i++;

        }

        return path;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileExportAllBinaryUnitsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */