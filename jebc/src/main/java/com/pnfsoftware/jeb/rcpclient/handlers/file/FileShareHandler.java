
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.jebio.JebIoUtil;
import com.pnfsoftware.jeb.client.jebio.UserCredentials;
import com.pnfsoftware.jeb.core.IArtifact;
import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.input.FileInput;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.jebio.JebIoLoginDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.jebio.JebIoShareDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.util.encoding.Hash;
import com.pnfsoftware.jeb.util.format.Formatter;
import com.pnfsoftware.jeb.util.io.IO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

public class FileShareHandler
        extends JebBaseHandler {
    public FileShareHandler() {
        super("share", "Share", "Share a sample with the JEB community", "eclipse/internal_browser.png");
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        IArtifact target = getTarget(this.context, this.part);
        openDialog(this.shell, this.context, target);
    }

    public static void openDialog(Shell shell, RcpClientContext context, IArtifact target) {
        boolean loginDone = false;
        UserCredentials creds = JebIoUtil.retrieveCredentials(context);
        if (!creds.lookValid()) {
            UI.info(shell, String.format("Welcome to %s!", new Object[]{"JEB Malware Sharing Network"}), "The JEB Malware Sharing Network is an optional service available to all JEB users.\n\nCreate your account to have the opportunity to anonymously share malware samples and files, at your own discretion.\n\nParticipants will receive samples that other users have been sharing. The samples you will receive are determined algorithmically based on the quantity and quality of your contributions.\n\nContinue to log in or sign up. (This service is optional and disabled by default.)");
            JebIoLoginDialog dlg = new JebIoLoginDialog(shell, context);
            if (dlg.open() == 1) {
                return;
            }
            loginDone = true;
        }
        IRuntimeProject prj = context.getOpenedProject();
        if (prj == null) {
            if (!loginDone) {
                new JebIoLoginDialog(shell, context).open();
            }
            return;
        }
        if (target != null) {
            JebIoShareDialog dlg = new JebIoShareDialog(shell, context, target);
            dlg.open();
        }
    }

    public static IArtifact getTarget(RcpClientContext context, IMPart part) {
        IRuntimeProject prj = context.getOpenedProject();
        if (prj == null) {
            return null;
        }
        List<? extends ILiveArtifact> alist = prj.getLiveArtifacts();
        if (alist.isEmpty()) {
            return null;
        }
        IArtifact target = null;
        Object object = part == null ? null : part.getManager();
        if (!(object instanceof UnitPartManager)) {
            target = ((ILiveArtifact) alist.get(0)).getArtifact();
        } else {
            IUnit unit = ((UnitPartManager) object).getUnit();
            if (unit != null) {
                IUnitCreator c = unit.getParent();
                while ((c != null) && (!(c instanceof IArtifact))) {
                    c = c.getParent();
                }
                target = (IArtifact) c;
            }
        }
        return target;
    }

    public static File getArtifactFile(IArtifact artifact) throws IOException {
        IInput input = artifact.getInput();
        if ((input instanceof FileInput)) {
            return ((FileInput) input).getFile();
        }
        InputStream in = input.getStream();
        Throwable localThrowable3 = null;
        try {
            File f = IO.createTempFile();
            f.deleteOnExit();
            IO.writeFile(f, IO.readInputStream(in));
            return f;
        } catch (Throwable localThrowable1) {
            localThrowable3 = localThrowable1;
            throw localThrowable1;
        } finally {
            if (in != null) if (localThrowable3 != null) try {
                in.close();
            } catch (Throwable localThrowable2) {
                localThrowable3.addSuppressed(localThrowable2);
            }
            else in.close();
        }
    }

    public static String calculateArtifactHash(IArtifact artifact) throws IOException {
        InputStream input = artifact.getInput().getStream();
        Throwable localThrowable3 = null;
        try {
            return Formatter.byteArrayToHexString(Hash.calculateSHA256(IO.readInputStream(input)));
        } catch (Throwable localThrowable4) {
            localThrowable3 = localThrowable4;
            throw localThrowable4;
        } finally {
            if (input != null) if (localThrowable3 != null) try {
                input.close();
            } catch (Throwable localThrowable2) {
                localThrowable3.addSuppressed(localThrowable2);
            }
            else input.close();
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileShareHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */