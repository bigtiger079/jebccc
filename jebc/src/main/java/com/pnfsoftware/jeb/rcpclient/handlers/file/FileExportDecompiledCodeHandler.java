package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.tree.ICodeNode;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeItem;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.core.util.DecompilerHelper;
import com.pnfsoftware.jeb.rcpclient.dialogs.ExportDecompiledCodeDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.ProgressMonitorHideableDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.export.FileExportDecompileAllJob;
import com.pnfsoftware.jeb.rcpclient.handlers.file.export.FileExportDecompiledDecompJob;
import com.pnfsoftware.jeb.rcpclient.handlers.file.export.FileExportFactory;
import com.pnfsoftware.jeb.rcpclient.handlers.file.export.IFileExport;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.ProjectExplorerPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class FileExportDecompiledCodeHandler extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(FileExportDecompiledCodeHandler.class);
    private static ProgressMonitorHideableDialog progressBar = null;

    public FileExportDecompiledCodeHandler() {
        super(null, S.s(476), null, null);
    }

    public boolean canExecute() {
        if (this.part == null) {
            return false;
        }
        Object object = this.part.getManager();
        if ((object instanceof UnitPartManager)) {
            IUnit unit = ((UnitPartManager) object).getUnit();
            return isDecompilable(unit);
        }
        if ((object instanceof ProjectExplorerPartManager)) {
            Object unit = ((ProjectExplorerPartManager) object).getSelectedNode();
            return isDecompilable(unit);
        }
        return false;
    }

    private static boolean isDecompilable(Object unit) {
        return ((unit instanceof ICodeUnit)) || ((unit instanceof ISourceUnit)) || ((unit instanceof IDecompilerUnit));
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
        Object object = this.part.getManager();
        UnitPartManager unitPart = null;
        IUnit unit = null;
        if ((object instanceof UnitPartManager)) {
            unitPart = (UnitPartManager) object;
            unit = unitPart.getUnit();
        } else if ((object instanceof ProjectExplorerPartManager)) {
            Object potentialUnit = ((ProjectExplorerPartManager) object).getSelectedNode();
            if (isDecompilable(potentialUnit)) {
                unit = (IUnit) potentialUnit;
            }
        }
        if (unit == null) {
            return;
        }
        ISourceUnit sourceUnit = null;
        ICodeUnit codeUnit = null;
        IDecompilerUnit decompiler = null;
        if ((unit instanceof ISourceUnit)) {
            sourceUnit = (ISourceUnit) unit;
            IUnitCreator parent = unit.getParent();
            if (!(parent instanceof IDecompilerUnit)) {
                return;
            }
            decompiler = (IDecompilerUnit) parent;
            parent = decompiler.getParent();
            if (!(parent instanceof IUnit)) {
                return;
            }
            codeUnit = (ICodeUnit) parent;
        } else if ((unit instanceof ICodeUnit)) {
            codeUnit = (ICodeUnit) unit;
            decompiler = DecompilerHelper.getDecompiler(codeUnit);
            if (decompiler == null) {
                logger.warn("No decompiler available: can not export");
                if (((unit.getParent() instanceof IUnit)) && (unit.getParent().getName().equals("decompiler"))) {
                }
            }
        } else if ((unit instanceof IDecompilerUnit)) {
            decompiler = (IDecompilerUnit) unit;
            IUnitCreator parent = decompiler.getParent();
            if (!(parent instanceof IUnit)) {
                return;
            }
            codeUnit = (ICodeUnit) parent;
        }
        if (codeUnit != null) {
            IFileExport<? extends ICodeItem> fileExport = FileExportFactory.get(codeUnit);
            if (fileExport == null) {
                MessageDialog.openError(this.shell, "No methods or classes found", "There are no methods nor classes defined for this unit");
                return;
            }
            ExportDecompiledCodeDialog.ExportStatus initialStatus = getDefaultFilter(unitPart, fileExport, sourceUnit);
            ExportDecompiledCodeDialog dlg = new ExportDecompiledCodeDialog(this.shell);
            dlg.setInitialState(initialStatus);
            ExportDecompiledCodeDialog.ExportStatus responseStatus = dlg.open();
            if (responseStatus == null) {
                return;
            }
            FileExportWriter fileWriter = new FileExportWriter(this.shell, dlg.getOutputDirectory(), dlg.getOutputFile(), dlg.isMergeFiles());
            Pattern pattern = null;
            if ((responseStatus.getState() == ExportDecompiledCodeDialog.State.FILTER) && (!Strings.isBlank(responseStatus.getFilter()))) {
                pattern = Pattern.compile(responseStatus.getFilter());
            } else if (responseStatus.getState() == ExportDecompiledCodeDialog.State.CURRENT) {
                pattern = Pattern.compile(initialStatus.getFilter());
            }
            progressBar = new ProgressMonitorHideableDialog(this.shell);
            IRunnableWithProgress runnable = null;
            if (responseStatus.getState() == ExportDecompiledCodeDialog.State.DECOMPILED) {
                List<? extends IUnit> sourceUnits = decompiler.getChildren();
                runnable = new FileExportDecompiledDecompJob(fileExport, sourceUnits, fileWriter);
            } else {
                runnable = new FileExportDecompileAllJob(this.context, fileExport, pattern, decompiler, fileWriter);
            }
            PartManager pman = this.context.getPartManager();
            pman.setRedraw(codeUnit, false);
            try {
                progressBar.run(true, true, runnable);
                UI.info(this.shell, "Export successful", "Decompiled files can be found in " + dlg.getOutputDirectory() + (dlg.isMergeFiles() ? File.separator + dlg.getOutputFile() : ""));
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
            } catch (InvocationTargetException e) {
                UI.error(this.shell, "Export failed", e.getMessage());
                logger.catching(e);
            } finally {
                pman.setRedraw(codeUnit, true);
            }
        }
    }

    private static <T> ExportDecompiledCodeDialog.ExportStatus getDefaultFilter(UnitPartManager unitPart, IFileExport<T> fileExport, ISourceUnit sourceUnit) {
        if (sourceUnit != null) {
            return new ExportDecompiledCodeDialog.ExportStatus(ExportDecompiledCodeDialog.State.CURRENT, fileExport.getNameFromSourceUnit(sourceUnit));
        }
        String filter = null;
        if (unitPart == null) {
            return new ExportDecompiledCodeDialog.ExportStatus(ExportDecompiledCodeDialog.State.ALL, filter);
        }
        String address = unitPart.getActiveAddress();
        if (address != null) {
            for (T c : fileExport.getItems()) {
                if ((fileExport.canProcess(c)) && (fileExport.isAtAddress(c, address))) {
                    filter = fileExport.getFullName(c);
                    break;
                }
            }
        } else {
            IItem item = unitPart.getActiveItem();
            if ((item instanceof ICodeNode)) {
                ICodeNode node = (ICodeNode) item;
                filter = getFullPath(node) + ".*";
            }
        }
        return new ExportDecompiledCodeDialog.ExportStatus(ExportDecompiledCodeDialog.State.ALL, filter);
    }

    private static String getFullPath(ICodeNode p) {
        List<String> packages = new ArrayList<>();
        while (p != null) {
            if (p.getLabel() != null) {
                packages.add(0, p.getLabel());
            }
            p = p.getParent();
        }
        return StringUtils.join(packages, '.');
    }
}


