/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file.export;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.ICodeItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportWriter;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.util.List;
/*    */ import org.eclipse.core.runtime.IProgressMonitor;
/*    */ import org.eclipse.jface.operation.IRunnableWithProgress;

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
/*    */
/*    */
/*    */
/*    */ public class FileExportDecompiledDecompJob
        /*    */ implements IRunnableWithProgress
        /*    */ {
    /*    */   private IFileExport<? extends ICodeItem> fileExport;
    /*    */   private List<? extends IUnit> sourceUnits;
    /*    */   private FileExportWriter fileWriter;

    /*    */
    /*    */
    public FileExportDecompiledDecompJob(IFileExport<? extends ICodeItem> fileExport, List<? extends IUnit> sourceUnits, FileExportWriter fileWriter)
    /*    */ {
        /* 35 */
        this.fileExport = fileExport;
        /* 36 */
        this.sourceUnits = sourceUnits;
        /* 37 */
        this.fileWriter = fileWriter;
        /*    */
    }

    /*    */
    /*    */
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    /*    */ {
        /* 42 */
        monitor.beginTask("Exporting decompiled code", this.sourceUnits.size());
        /* 43 */
        for (IUnit fileSourceUnit : this.sourceUnits) {
            /* 44 */
            if (((fileSourceUnit instanceof ISourceUnit)) && (fileSourceUnit.isProcessed())) {
                /* 45 */
                String fullNameWithPackage = this.fileExport.getNameFromSourceUnit((ISourceUnit) fileSourceUnit);
                /* 46 */
                monitor.subTask("Saving " + fullNameWithPackage);
                /* 47 */
                String[] fullName = fullNameWithPackage.split("\\.");
                /* 48 */
                String[] packages = new String[fullName.length - 1];
                /* 49 */
                System.arraycopy(fullName, 0, packages, 0, packages.length);
                /* 50 */
                String className = fullName[(fullName.length - 1)];
                /*    */
                try {
                    /* 52 */
                    this.fileWriter.writeFile((ISourceUnit) fileSourceUnit, packages, className);
                    /*    */
                    /*    */
                }
                /*    */ catch (IOException e)
                    /*    */ {
                    /* 57 */
                    throw new InvocationTargetException(e, "Can not write to file " + this.fileWriter.getTargetFile((ISourceUnit) fileSourceUnit, packages, className));
                    /*    */
                }
                /* 59 */
                monitor.worked(1);
                /* 60 */
                if (monitor.isCanceled()) {
                    /* 61 */
                    throw new InterruptedException("Export decompiled code cancelled");
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\export\FileExportDecompiledDecompJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */