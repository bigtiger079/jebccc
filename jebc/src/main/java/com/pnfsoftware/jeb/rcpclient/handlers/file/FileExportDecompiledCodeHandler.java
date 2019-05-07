/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*     */
/*     */

import com.pnfsoftware.jeb.client.Licensing;
/*     */ import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.IUnitCreator;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.tree.ICodeNode;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*     */ import com.pnfsoftware.jeb.core.util.DecompilerHelper;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ExportDecompiledCodeDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ExportDecompiledCodeDialog.ExportStatus;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ExportDecompiledCodeDialog.State;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.ProgressMonitorHideableDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.export.FileExportDecompileAllJob;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.export.FileExportDecompiledDecompJob;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.export.FileExportFactory;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.export.IFileExport;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.ProjectExplorerPartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.commons.lang3.StringUtils;
/*     */ import org.eclipse.jface.dialogs.MessageDialog;
/*     */ import org.eclipse.jface.operation.IRunnableWithProgress;

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
/*     */ public class FileExportDecompiledCodeHandler
        /*     */ extends JebBaseHandler
        /*     */ {
    /*  54 */   private static final ILogger logger = GlobalLog.getLogger(FileExportDecompiledCodeHandler.class);
    /*     */
    /*  56 */   private static ProgressMonitorHideableDialog progressBar = null;

    /*     */
    /*     */
    public FileExportDecompiledCodeHandler() {
        /*  59 */
        super(null, S.s(476), null, null);
        /*     */
    }

    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  64 */
        if (this.part == null) {
            /*  65 */
            return false;
            /*     */
        }
        /*  67 */
        Object object = this.part.getManager();
        /*  68 */
        if ((object instanceof UnitPartManager)) {
            /*  69 */
            IUnit unit = ((UnitPartManager) object).getUnit();
            /*  70 */
            return isDecompilable(unit);
            /*     */
        }
        /*     */
        /*  73 */
        if ((object instanceof ProjectExplorerPartManager)) {
            /*  74 */
            Object unit = ((ProjectExplorerPartManager) object).getSelectedNode();
            /*  75 */
            return isDecompilable(unit);
            /*     */
        }
        /*  77 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private static boolean isDecompilable(Object unit) {
        /*  81 */
        return ((unit instanceof ICodeUnit)) || ((unit instanceof ISourceUnit)) || ((unit instanceof IDecompilerUnit));
        /*     */
    }

    /*     */
    /*     */
    public void execute()
    /*     */ {
        /*  86 */
        if (Licensing.isDemoBuild()) {
            /*  87 */
            MessageDialog.openWarning(this.shell, S.s(249), S.s(265));
            /*  88 */
            return;
            /*     */
        }
        /*     */
        /*  91 */
        if ((progressBar != null) && (progressBar.isAlive())) {
            /*  92 */
            progressBar.setVisible(true);
            /*  93 */
            return;
            /*     */
        }
        /*  95 */
        Object object = this.part.getManager();
        /*  96 */
        UnitPartManager unitPart = null;
        /*  97 */
        IUnit unit = null;
        /*  98 */
        if ((object instanceof UnitPartManager)) {
            /*  99 */
            unitPart = (UnitPartManager) object;
            /* 100 */
            unit = unitPart.getUnit();
            /*     */
        }
        /* 102 */
        else if ((object instanceof ProjectExplorerPartManager)) {
            /* 103 */
            Object potentialUnit = ((ProjectExplorerPartManager) object).getSelectedNode();
            /* 104 */
            if (isDecompilable(potentialUnit)) {
                /* 105 */
                unit = (IUnit) potentialUnit;
                /*     */
            }
            /*     */
        }
        /* 108 */
        if (unit == null) {
            /* 109 */
            return;
            /*     */
        }
        /*     */
        /* 112 */
        ISourceUnit sourceUnit = null;
        /* 113 */
        ICodeUnit codeUnit = null;
        /* 114 */
        IDecompilerUnit decompiler = null;
        /* 115 */
        if ((unit instanceof ISourceUnit)) {
            /* 116 */
            sourceUnit = (ISourceUnit) unit;
            /* 117 */
            IUnitCreator parent = unit.getParent();
            /* 118 */
            if (!(parent instanceof IDecompilerUnit)) {
                /* 119 */
                return;
                /*     */
            }
            /* 121 */
            decompiler = (IDecompilerUnit) parent;
            /* 122 */
            parent = decompiler.getParent();
            /* 123 */
            if (!(parent instanceof IUnit)) {
                /* 124 */
                return;
                /*     */
            }
            /* 126 */
            codeUnit = (ICodeUnit) parent;
            /*     */
        }
        /* 128 */
        else if ((unit instanceof ICodeUnit)) {
            /* 129 */
            codeUnit = (ICodeUnit) unit;
            /* 130 */
            decompiler = DecompilerHelper.getDecompiler(codeUnit);
            /* 131 */
            if (decompiler == null) {
                /* 132 */
                logger.warn("No decompiler available: can not export", new Object[0]);
                /* 133 */
                if (((unit.getParent() instanceof IUnit)) && (((IUnit) unit.getParent()).getName().equals("decompiler"))) {
                }
                /*     */
            }
            /*     */
            /*     */
            /*     */
        }
        /* 138 */
        else if ((unit instanceof IDecompilerUnit)) {
            /* 139 */
            decompiler = (IDecompilerUnit) unit;
            /* 140 */
            IUnitCreator parent = decompiler.getParent();
            /* 141 */
            if (!(parent instanceof IUnit)) {
                /* 142 */
                return;
                /*     */
            }
            /* 144 */
            codeUnit = (ICodeUnit) parent;
            /*     */
        }
        /*     */
        /* 147 */
        if (codeUnit != null) {
            /* 148 */
            IFileExport<? extends ICodeItem> fileExport = FileExportFactory.get(codeUnit);
            /* 149 */
            if (fileExport == null) {
                /* 150 */
                MessageDialog.openError(this.shell, "No methods or classes found", "There are no methods nor classes defined for this unit");
                /*     */
                /* 152 */
                return;
                /*     */
            }
            /*     */
            /* 155 */
            ExportDecompiledCodeDialog.ExportStatus initialStatus = getDefaultFilter(unitPart, fileExport, sourceUnit);
            /* 156 */
            ExportDecompiledCodeDialog dlg = new ExportDecompiledCodeDialog(this.shell);
            /* 157 */
            dlg.setInitialState(initialStatus);
            /*     */
            /*     */
            /* 160 */
            ExportDecompiledCodeDialog.ExportStatus responseStatus = dlg.open();
            /* 161 */
            if (responseStatus == null) {
                /* 162 */
                return;
                /*     */
            }
            /*     */
            /* 165 */
            FileExportWriter fileWriter = new FileExportWriter(this.shell, dlg.getOutputDirectory(), dlg.getOutputFile(), dlg.isMergeFiles());
            /*     */
            /*     */
            /* 168 */
            Pattern pattern = null;
            /* 169 */
            if ((responseStatus.getState() == ExportDecompiledCodeDialog.State.FILTER) && (!Strings.isBlank(responseStatus.getFilter()))) {
                /* 170 */
                pattern = Pattern.compile(responseStatus.getFilter());
                /*     */
            }
            /* 172 */
            else if (responseStatus.getState() == ExportDecompiledCodeDialog.State.CURRENT) {
                /* 173 */
                pattern = Pattern.compile(initialStatus.getFilter());
                /*     */
            }
            /*     */
            /* 176 */
            progressBar = new ProgressMonitorHideableDialog(this.shell);
            /* 177 */
            IRunnableWithProgress runnable = null;
            /* 178 */
            if (responseStatus.getState() == ExportDecompiledCodeDialog.State.DECOMPILED) {
                /* 179 */
                List<? extends IUnit> sourceUnits = decompiler.getChildren();
                /* 180 */
                runnable = new FileExportDecompiledDecompJob(fileExport, sourceUnits, fileWriter);
                /*     */
            }
            /*     */
            else {
                /* 183 */
                runnable = new FileExportDecompileAllJob(this.context, fileExport, pattern, decompiler, fileWriter);
                /*     */
            }
            /*     */
            /*     */
            /* 187 */
            PartManager pman = this.context.getPartManager();
            /* 188 */
            pman.setRedraw(codeUnit, false);
            /*     */
            try {
                /* 190 */
                progressBar.run(true, true, runnable);
                /* 191 */
                UI.info(this.shell, "Export successful", "Decompiled files can be found in " + dlg
/* 192 */.getOutputDirectory() + (dlg
/* 193 */.isMergeFiles() ? File.separator + dlg.getOutputFile() : ""));
                /*     */
            }
            /*     */ catch (InterruptedException e)
                /*     */ {
                /* 197 */
                logger.warn(e.getMessage(), new Object[0]);
                /* 198 */
                return;
                /*     */
            }
            /*     */ catch (InvocationTargetException e) {
                /* 201 */
                UI.error(this.shell, "Export failed", e.getMessage());
                /* 202 */
                logger.catching(e);
                /*     */
            }
            /*     */ finally {
                /* 205 */
                pman.setRedraw(codeUnit, true);
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private static <T> ExportDecompiledCodeDialog.ExportStatus getDefaultFilter(UnitPartManager unitPart, IFileExport<T> fileExport, ISourceUnit sourceUnit)
    /*     */ {
        /* 212 */
        if (sourceUnit != null) {
            /* 213 */
            return new ExportDecompiledCodeDialog.ExportStatus(ExportDecompiledCodeDialog.State.CURRENT, fileExport.getNameFromSourceUnit(sourceUnit));
            /*     */
        }
        /* 215 */
        String filter = null;
        /* 216 */
        if (unitPart == null) {
            /* 217 */
            return new ExportDecompiledCodeDialog.ExportStatus(ExportDecompiledCodeDialog.State.ALL, filter);
            /*     */
        }
        /* 219 */
        String address = unitPart.getActiveAddress();
        /* 220 */
        if (address != null)
            /*     */ {
            /* 222 */
            for (T c : fileExport.getItems()) {
                /* 223 */
                if ((fileExport.canProcess(c)) &&
                        /* 224 */           (fileExport.isAtAddress(c, address))) {
                    /* 225 */
                    filter = fileExport.getFullName(c);
                    /* 226 */
                    break;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 232 */
            IItem item = unitPart.getActiveItem();
            /* 233 */
            if ((item instanceof ICodeNode)) {
                /* 234 */
                ICodeNode node = (ICodeNode) item;
                /* 235 */
                filter = getFullPath(node) + ".*";
                /*     */
            }
            /*     */
        }
        /* 238 */
        return new ExportDecompiledCodeDialog.ExportStatus(ExportDecompiledCodeDialog.State.ALL, filter);
        /*     */
    }

    /*     */
    /*     */
    private static String getFullPath(ICodeNode p) {
        /* 242 */
        List<String> packages = new ArrayList();
        /* 243 */
        while (p != null) {
            /* 244 */
            if (p.getLabel() != null) {
                /* 245 */
                packages.add(0, p.getLabel());
                /*     */
            }
            /* 247 */
            p = p.getParent();
            /*     */
        }
        /* 249 */
        return StringUtils.join(packages, '.');
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileExportDecompiledCodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */