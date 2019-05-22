package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.core.output.IGenericDocument;
import com.pnfsoftware.jeb.core.output.IUnitDocumentPresentation;
import com.pnfsoftware.jeb.core.output.IUnitFormatter;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.rcpclient.dialogs.AdaptivePopupDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.swt.widgets.Shell;

public class FileExportWriter {
    private static final ILogger logger = GlobalLog.getLogger(FileExportWriter.class);
    private static final int MAX_BUFFER_SIZE = 16384;
    private Shell shell;
    private String outputDirectory;
    private String filename;
    private boolean mergeFiles;
    private boolean filesAlreadyProcessed;
    int overwrite = -1;

    public FileExportWriter(Shell shell, String outputDirectory, String filename, boolean mergeFiles) {
        this.shell = shell;
        this.outputDirectory = outputDirectory;
        this.filename = filename;
        this.mergeFiles = mergeFiles;
    }

    public Path getTargetDirectory(String[] packages) {
        if (this.mergeFiles) {
            return Paths.get(this.outputDirectory);
        }
        return Paths.get(this.outputDirectory, packages);
    }

    public Path getTargetFile(ISourceUnit fileSourceUnit, String[] packages, String className) {
        return getTargetFile(fileSourceUnit, getTargetDirectory(packages), className);
    }

    public Path getTargetFile(ISourceUnit fileSourceUnit, Path dir, String className) {
        if (this.mergeFiles) {
            return dir.resolve(this.filename);
        }
        String extension = "";
        if (fileSourceUnit.getFileExtension() != null) {
            extension = '.' + fileSourceUnit.getFileExtension();
        }
        return dir.resolve(className + extension);
    }

    public void writeFile(ISourceUnit fileSourceUnit, List<String> packages, String className) throws IOException {
        writeFile(fileSourceUnit, packages.toArray(new String[packages.size()]), className);
    }

    class OverwritePopup implements Runnable {
        File file;
        boolean doNotShow = false;

        public OverwritePopup(File file) {
            this.file = file;
        }

        public void run() {
            String msg = Strings.f("File %s already exists.\nDo you want to overwrite?", this.file);
            AdaptivePopupDialog popup = new AdaptivePopupDialog(FileExportWriter.this.shell, 2, "Overwrite File?", msg, null);
            FileExportWriter.this.overwrite = popup.open();
            this.doNotShow = popup.isDoNotShow();
        }
    }

    public void writeFile(ISourceUnit fileSourceUnit, String[] packages, String className) throws IOException {
        Path dir = getTargetDirectory(packages);
        File dirFile = dir.toFile();
        if (((dirFile.exists()) && (!dirFile.isDirectory())) || ((!dirFile.exists()) && (!dirFile.mkdirs()))) {
            throw new IOException("Can not create directory " + dir);
        }
        Path file = getTargetFile(fileSourceUnit, packages, className);
        StandardOpenOption[] options = {StandardOpenOption.APPEND};
        if ((this.overwrite == 0) && (file.toFile().exists())) {
            return;
        }
        if ((!this.mergeFiles) || (!this.filesAlreadyProcessed)) {
            this.filesAlreadyProcessed = true;
            if ((file.toFile().exists()) && (this.overwrite < 0)) {
                OverwritePopup pu = new OverwritePopup(file.toFile());
                UIExecutor.sync(this.shell.getDisplay(), pu);
                if (this.overwrite == 0) {
                    resetOverwrite(pu.doNotShow);
                    return;
                }
                resetOverwrite(pu.doNotShow);
            }
            options = new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};
            logger.info("Writing to %s", file);
        }
        writeFile(file, getTextDocument(fileSourceUnit), options);
    }

    private void resetOverwrite(boolean doNotShow) {
        if (!doNotShow) {
            this.overwrite = -1;
        }
    }

    private synchronized void writeFile(Path f, ITextDocument textDocument, StandardOpenOption[] options) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(f, Charset.forName("UTF-8"), options);
        Throwable localThrowable3 = null;
        try {
            long i = textDocument.getFirstAnchor();
            long end = i + textDocument.getAnchorCount();
            for (; i < end; i += 1L) {
                List<? extends ILine> lines = textDocument.getDocumentPart(i, 0).getLines();
                for (ILine line : lines) {
                    CharSequence s = line.getText();
                    if (s.length() > 16384) {
                        int offset = 0;
                        while (offset < s.length()) {
                            writer.write(s.subSequence(offset, Math.min(s.length(), offset + 16384)).toString());
                            offset += 16384;
                        }
                    } else {
                        writer.write(s.toString());
                    }
                    writer.newLine();
                }
            }
            if (this.mergeFiles) {
                writer.newLine();
            }
        } catch (Throwable localThrowable1) {
            localThrowable3 = localThrowable1;
            throw localThrowable1;
        } finally {
            if (writer != null) if (localThrowable3 != null) try {
                writer.close();
            } catch (Throwable localThrowable2) {
                localThrowable3.addSuppressed(localThrowable2);
            }
            else writer.close();
        }
    }

    private static ITextDocument getTextDocument(ISourceUnit sourceUnit) {
        IUnitFormatter formatter = sourceUnit.getFormatter();
        if ((formatter != null) && (CollectionUtils.isNotEmpty(formatter.getPresentations()))) {
            IGenericDocument genericDoc = formatter.getPresentations().get(0).getDocument();
            if ((genericDoc instanceof ITextDocument)) {
                return (ITextDocument) genericDoc;
            }
        }
        return null;
    }
}


