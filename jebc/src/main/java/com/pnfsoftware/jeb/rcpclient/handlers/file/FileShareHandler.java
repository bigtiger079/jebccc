/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*     */
/*     */

import com.pnfsoftware.jeb.client.jebio.JebIoUtil;
/*     */ import com.pnfsoftware.jeb.client.jebio.UserCredentials;
/*     */ import com.pnfsoftware.jeb.core.IArtifact;
/*     */ import com.pnfsoftware.jeb.core.ILiveArtifact;
/*     */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*     */ import com.pnfsoftware.jeb.core.IUnitCreator;
/*     */ import com.pnfsoftware.jeb.core.input.FileInput;
/*     */ import com.pnfsoftware.jeb.core.input.IInput;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.jebio.JebIoLoginDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.jebio.JebIoShareDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*     */ import com.pnfsoftware.jeb.util.encoding.Hash;
/*     */ import com.pnfsoftware.jeb.util.format.Formatter;
/*     */ import com.pnfsoftware.jeb.util.io.IO;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.List;
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
/*     */
/*     */
/*     */
/*     */
/*     */ public class FileShareHandler
        /*     */ extends JebBaseHandler
        /*     */ {
    /*     */
    public FileShareHandler()
    /*     */ {
        /*  46 */
        super("share", "Share", "Share a sample with the JEB community", "eclipse/internal_browser.png");
        /*     */
    }

    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  51 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public void execute()
    /*     */ {
        /*  56 */
        IArtifact target = getTarget(this.context, this.part);
        /*  57 */
        openDialog(this.shell, this.context, target);
        /*     */
    }

    /*     */
    /*     */
    public static void openDialog(Shell shell, RcpClientContext context, IArtifact target) {
        /*  61 */
        boolean loginDone = false;
        /*  62 */
        UserCredentials creds = JebIoUtil.retrieveCredentials(context);
        /*  63 */
        if (!creds.lookValid()) {
            /*  64 */
            UI.info(shell, String.format("Welcome to %s!", new Object[]{"JEB Malware Sharing Network"}), "The JEB Malware Sharing Network is an optional service available to all JEB users.\n\nCreate your account to have the opportunity to anonymously share malware samples and files, at your own discretion.\n\nParticipants will receive samples that other users have been sharing. The samples you will receive are determined algorithmically based on the quantity and quality of your contributions.\n\nContinue to log in or sign up. (This service is optional and disabled by default.)");
            /*  65 */
            JebIoLoginDialog dlg = new JebIoLoginDialog(shell, context);
            /*  66 */
            if (dlg.open() == 1) {
                /*  67 */
                return;
                /*     */
            }
            /*  69 */
            loginDone = true;
            /*     */
        }
        /*     */
        /*  72 */
        IRuntimeProject prj = context.getOpenedProject();
        /*  73 */
        if (prj == null) {
            /*  74 */
            if (!loginDone) {
                /*  75 */
                new JebIoLoginDialog(shell, context).open();
                /*     */
            }
            /*  77 */
            return;
            /*     */
        }
        /*     */
        /*  80 */
        if (target != null) {
            /*  81 */
            JebIoShareDialog dlg = new JebIoShareDialog(shell, context, target);
            /*  82 */
            dlg.open();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public static IArtifact getTarget(RcpClientContext context, IMPart part) {
        /*  87 */
        IRuntimeProject prj = context.getOpenedProject();
        /*  88 */
        if (prj == null) {
            /*  89 */
            return null;
            /*     */
        }
        /*     */
        /*  92 */
        List<? extends ILiveArtifact> alist = prj.getLiveArtifacts();
        /*  93 */
        if (alist.isEmpty()) {
            /*  94 */
            return null;
            /*     */
        }
        /*     */
        /*  97 */
        IArtifact target = null;
        /*  98 */
        Object object = part == null ? null : part.getManager();
        /*  99 */
        if (!(object instanceof UnitPartManager)) {
            /* 100 */
            target = ((ILiveArtifact) alist.get(0)).getArtifact();
            /*     */
        }
        /*     */
        else {
            /* 103 */
            IUnit unit = ((UnitPartManager) object).getUnit();
            /* 104 */
            if (unit != null) {
                /* 105 */
                IUnitCreator c = unit.getParent();
                /* 106 */
                while ((c != null) && (!(c instanceof IArtifact))) {
                    /* 107 */
                    c = c.getParent();
                    /*     */
                }
                /* 109 */
                target = (IArtifact) c;
                /*     */
            }
            /*     */
        }
        /* 112 */
        return target;
        /*     */
    }

    /*     */
    /*     */
    public static File getArtifactFile(IArtifact artifact) throws IOException {
        /* 116 */
        IInput input = artifact.getInput();
        /* 117 */
        if ((input instanceof FileInput)) {
            /* 118 */
            return ((FileInput) input).getFile();
            /*     */
        }
        /* 120 */
        InputStream in = input.getStream();
        Throwable localThrowable3 = null;
        /* 121 */
        try {
            File f = IO.createTempFile();
            /* 122 */
            f.deleteOnExit();
            /* 123 */
            IO.writeFile(f, IO.readInputStream(in));
            /* 124 */
            return f;
            /*     */
        }
        /*     */ catch (Throwable localThrowable1)
            /*     */ {
            /* 120 */
            localThrowable3 = localThrowable1;
            throw localThrowable1;
            /*     */
            /*     */
        }
        /*     */ finally
            /*     */ {
            /* 125 */
            if (in != null) if (localThrowable3 != null) try {
                in.close();
            } catch (Throwable localThrowable2) {
                localThrowable3.addSuppressed(localThrowable2);
            }
            else in.close();
            /*     */
        }
        /*     */
    }

    /*     */
    /* 129 */
    public static String calculateArtifactHash(IArtifact artifact) throws IOException {
        InputStream input = artifact.getInput().getStream();
        Throwable localThrowable3 = null;
        /* 130 */
        try {
            return Formatter.byteArrayToHexString(Hash.calculateSHA256(IO.readInputStream(input)));
            /*     */
        }
        /*     */ catch (Throwable localThrowable4)
            /*     */ {
            /* 129 */
            localThrowable3 = localThrowable4;
            throw localThrowable4;
            /*     */
        } finally {
            /* 131 */
            if (input != null) if (localThrowable3 != null) try {
                input.close();
            } catch (Throwable localThrowable2) {
                localThrowable3.addSuppressed(localThrowable2);
            }
            else input.close();
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileShareHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */