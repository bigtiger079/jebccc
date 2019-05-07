/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.IGenericDocument;
/*     */ import com.pnfsoftware.jeb.core.output.IUnitDocumentPresentation;
/*     */ import com.pnfsoftware.jeb.core.output.IUnitFormatter;
/*     */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.AdaptivePopupDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.List;
/*     */ import org.apache.commons.collections4.CollectionUtils;
/*     */ import org.eclipse.swt.widgets.Shell;

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
/*     */ public class FileExportWriter
        /*     */ {
    /*  38 */   private static final ILogger logger = GlobalLog.getLogger(FileExportWriter.class);
    /*     */
    /*     */   private static final int MAX_BUFFER_SIZE = 16384;
    /*     */
    /*     */   private Shell shell;
    /*     */   private String outputDirectory;
    /*     */   private String filename;
    /*     */   private boolean mergeFiles;
    /*     */   private boolean filesAlreadyProcessed;
    /*  47 */ int overwrite = -1;

    /*     */
    /*     */
    public FileExportWriter(Shell shell, String outputDirectory, String filename, boolean mergeFiles)
    /*     */ {
        /*  51 */
        this.shell = shell;
        /*  52 */
        this.outputDirectory = outputDirectory;
        /*  53 */
        this.filename = filename;
        /*  54 */
        this.mergeFiles = mergeFiles;
        /*     */
    }

    /*     */
    /*     */
    public Path getTargetDirectory(String[] packages) {
        /*  58 */
        if (this.mergeFiles) {
            /*  59 */
            return Paths.get(this.outputDirectory, new String[0]);
            /*     */
        }
        /*  61 */
        return Paths.get(this.outputDirectory, packages);
        /*     */
    }

    /*     */
    /*     */
    public Path getTargetFile(ISourceUnit fileSourceUnit, String[] packages, String className)
    /*     */ {
        /*  66 */
        return getTargetFile(fileSourceUnit, getTargetDirectory(packages), className);
        /*     */
    }

    /*     */
    /*     */
    public Path getTargetFile(ISourceUnit fileSourceUnit, Path dir, String className) {
        /*  70 */
        if (this.mergeFiles) {
            /*  71 */
            return dir.resolve(this.filename);
            /*     */
        }
        /*  73 */
        String extension = "";
        /*  74 */
        if (fileSourceUnit.getFileExtension() != null) {
            /*  75 */
            extension = '.' + fileSourceUnit.getFileExtension();
            /*     */
        }
        /*  77 */
        return dir.resolve(className + extension);
        /*     */
    }

    /*     */
    /*     */
    public void writeFile(ISourceUnit fileSourceUnit, List<String> packages, String className) throws IOException {
        /*  81 */
        writeFile(fileSourceUnit, (String[]) packages.toArray(new String[packages.size()]), className);
        /*     */
    }

    /*     */
    /*     */   class OverwritePopup implements Runnable {
        /*     */ File file;
        /*  86 */ boolean doNotShow = false;

        /*     */
        /*     */
        public OverwritePopup(File file) {
            /*  89 */
            this.file = file;
            /*     */
        }

        /*     */
        /*     */
        public void run()
        /*     */ {
            /*  94 */
            String msg = Strings.f("File %s already exists.\nDo you want to overwrite?", new Object[]{this.file});
            /*  95 */
            AdaptivePopupDialog popup = new AdaptivePopupDialog(FileExportWriter.this.shell, 2, "Overwrite File?", msg, null);
            /*     */
            /*  97 */
            FileExportWriter.this.overwrite = popup.open().intValue();
            /*  98 */
            this.doNotShow = popup.isDoNotShow();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void writeFile(ISourceUnit fileSourceUnit, String[] packages, String className) throws IOException
    /*     */ {
        /* 104 */
        Path dir = getTargetDirectory(packages);
        /* 105 */
        File dirFile = dir.toFile();
        /* 106 */
        if (((dirFile.exists()) && (!dirFile.isDirectory())) || ((!dirFile.exists()) && (!dirFile.mkdirs()))) {
            /* 107 */
            throw new IOException("Can not create directory " + dir);
            /*     */
        }
        /* 109 */
        Path file = getTargetFile(fileSourceUnit, packages, className);
        /* 110 */
        StandardOpenOption[] options = {StandardOpenOption.APPEND};
        /* 111 */
        if ((this.overwrite == 0) && (file.toFile().exists()))
            /*     */ {
            /* 113 */
            return;
            /*     */
        }
        /* 115 */
        if ((!this.mergeFiles) || (!this.filesAlreadyProcessed)) {
            /* 116 */
            this.filesAlreadyProcessed = true;
            /*     */
            /* 118 */
            if ((file.toFile().exists()) && (this.overwrite < 0)) {
                /* 119 */
                OverwritePopup pu = new OverwritePopup(file.toFile());
                /* 120 */
                UIExecutor.sync(this.shell.getDisplay(), pu);
                /* 121 */
                if (this.overwrite == 0) {
                    /* 122 */
                    resetOverwrite(pu.doNotShow);
                    /* 123 */
                    return;
                    /*     */
                }
                /* 125 */
                resetOverwrite(pu.doNotShow);
                /*     */
            }
            /* 127 */
            options = new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            /* 128 */
            logger.info("Writing to %s", new Object[]{file});
            /*     */
        }
        /*     */
        /* 131 */
        writeFile(file, getTextDocument(fileSourceUnit), options);
        /*     */
    }

    /*     */
    /*     */
    private void resetOverwrite(boolean doNotShow) {
        /* 135 */
        if (!doNotShow) {
            /* 136 */
            this.overwrite = -1;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private synchronized void writeFile(Path f, ITextDocument textDocument, StandardOpenOption[] options) throws IOException
    /*     */ {
        /* 142 */
        BufferedWriter writer = Files.newBufferedWriter(f, Charset.forName("UTF-8"), options);
        Throwable localThrowable3 = null;
        /* 143 */
        try {
            long i = textDocument.getFirstAnchor();
            /* 144 */
            long end = i + textDocument.getAnchorCount();
            /* 145 */
            for (; i < end; i += 1L) {
                /* 146 */
                List<? extends ILine> lines = textDocument.getDocumentPart(i, 0).getLines();
                /* 147 */
                for (ILine line : lines) {
                    /* 148 */
                    CharSequence s = line.getText();
                    /* 149 */
                    if (s.length() > 16384)
                        /*     */ {
                        /* 151 */
                        int offset = 0;
                        /* 152 */
                        while (offset < s.length()) {
                            /* 153 */
                            writer.write(s.subSequence(offset, Math.min(s.length(), offset + 16384))
/* 154 */.toString());
                            /* 155 */
                            offset += 16384;
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                    else {
                        /* 159 */
                        writer.write(s.toString());
                        /*     */
                    }
                    /* 161 */
                    writer.newLine();
                    /*     */
                }
                /*     */
            }
            /* 164 */
            if (this.mergeFiles) {
                /* 165 */
                writer.newLine();
                /*     */
            }
            /*     */
        }
        /*     */ catch (Throwable localThrowable1)
            /*     */ {
            /* 142 */
            localThrowable3 = localThrowable1;
            throw localThrowable1;
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
            /*     */
        }
        /*     */ finally
            /*     */ {
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
            /* 167 */
            if (writer != null) if (localThrowable3 != null) try {
                writer.close();
            } catch (Throwable localThrowable2) {
                localThrowable3.addSuppressed(localThrowable2);
            }
            else writer.close();
            /*     */
        }
        /*     */
    }

    /*     */
    /* 171 */
    private static ITextDocument getTextDocument(ISourceUnit sourceUnit) {
        IUnitFormatter formatter = sourceUnit.getFormatter();
        /* 172 */
        if ((formatter != null) && (CollectionUtils.isNotEmpty(formatter.getPresentations()))) {
            /* 173 */
            IGenericDocument genericDoc = ((IUnitDocumentPresentation) formatter.getPresentations().get(0)).getDocument();
            /* 174 */
            if ((genericDoc instanceof ITextDocument)) {
                /* 175 */
                return (ITextDocument) genericDoc;
                /*     */
            }
            /*     */
        }
        /* 178 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileExportWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */