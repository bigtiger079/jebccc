package com.pnfsoftware.jeb.rcpclient.handlers.file.export;

import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeItem;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportWriter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class FileExportDecompiledDecompJob implements IRunnableWithProgress {
    private IFileExport<? extends ICodeItem> fileExport;
    private List<? extends IUnit> sourceUnits;
    private FileExportWriter fileWriter;

    public FileExportDecompiledDecompJob(IFileExport<? extends ICodeItem> fileExport, List<? extends IUnit> sourceUnits, FileExportWriter fileWriter) {
        this.fileExport = fileExport;
        this.sourceUnits = sourceUnits;
        this.fileWriter = fileWriter;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        monitor.beginTask("Exporting decompiled code", this.sourceUnits.size());
        for (IUnit fileSourceUnit : this.sourceUnits) {
            if (((fileSourceUnit instanceof ISourceUnit)) && (fileSourceUnit.isProcessed())) {
                String fullNameWithPackage = this.fileExport.getNameFromSourceUnit((ISourceUnit) fileSourceUnit);
                monitor.subTask("Saving " + fullNameWithPackage);
                String[] fullName = fullNameWithPackage.split("\\.");
                String[] packages = new String[fullName.length - 1];
                System.arraycopy(fullName, 0, packages, 0, packages.length);
                String className = fullName[(fullName.length - 1)];
                try {
                    this.fileWriter.writeFile((ISourceUnit) fileSourceUnit, packages, className);
                } catch (IOException e) {
                    throw new InvocationTargetException(e, "Can not write to file " + this.fileWriter.getTargetFile((ISourceUnit) fileSourceUnit, packages, className));
                }
                monitor.worked(1);
                if (monitor.isCanceled()) {
                    throw new InterruptedException("Export decompiled code cancelled");
                }
            }
        }
    }
}


