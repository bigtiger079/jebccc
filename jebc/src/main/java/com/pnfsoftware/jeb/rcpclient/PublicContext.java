
package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.client.api.ButtonGroupType;
import com.pnfsoftware.jeb.client.api.IGraphicalClientContext;
import com.pnfsoftware.jeb.client.api.IconType;
import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.Version;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.dialogs.InputDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.MessageBox;

public class PublicContext
        implements IGraphicalClientContext {
    private static final ILogger logger = GlobalLog.getLogger(PublicContext.class);
    RcpClientContext ctx;

    public PublicContext(RcpClientContext ctx) {
        this.ctx = ctx;
    }

    public RcpClientContext getContext() {
        return this.ctx;
    }

    public String[] getArguments() {
        return null;
    }

    public String getBaseDirectory() {
        return this.ctx.getBaseDirectory();
    }

    public String getProgramDirectory() {
        return this.ctx.getProgramDirectory();
    }

    public Version getSoftwareVersion() {
        return new Version(RcpClientContext.app_ver);
    }

    public IEnginesContext getEnginesContext() {
        return this.ctx.getEnginesContext();
    }

    public String displayQuestionBox(String caption, String message, String defaultValue) {
        InputDialog dlg = new InputDialog(UI.getShellTracker().get(), caption, defaultValue);
        dlg.setMessage(message);
        String value = dlg.open();
        return value;
    }

    public int displayMessageBox(String caption, String message, IconType iconType, ButtonGroupType bgType) {
        int flags = 0;
        if (iconType != null) {
            switch (iconType) {
                case QUESTION:
                    flags |= 0x4;
                    break;
                case INFORMATION:
                    flags |= 0x2;
                    break;
                case WARNING:
                    flags |= 0x8;
                    break;
                case ERROR:
                    flags |= 0x1;
                    break;
            }
        }
        if (bgType != null) {
            switch (bgType) {
                case OK:
                    flags |= 0x20;
                    break;
                case OK_CANCEL:
                    flags |= 0x120;
                    break;
                case YES_NO:
                    flags |= 0xC0;
                    break;
                case YES_NO_CANCEL:
                    flags |= 0x1C0;
                    break;
            }
        }
        MessageBox dlg = new MessageBox(UI.getShellTracker().get(), flags);
        if (caption != null) {
            dlg.setText(caption);
        }
        if (message != null) {
            dlg.setMessage(message);
        }
        int r = dlg.open();
        switch (r) {
            case 256:
                return 0;
            case 32:
                return 1;
            case 64:
                return 2;
            case 128:
                return 3;
        }
        return 0;
    }

    public boolean executeAsync(String taskName, Runnable runnable) {
        return this.ctx.executeTask(taskName, runnable);
    }

    public <T> T executeAsyncWithReturn(String taskName, Callable<T> callable) {
        return (T) this.ctx.executeTask(taskName, callable);
    }

    public List<UnitPartManager> getViews() {
        return getViews(null);
    }

    public List<UnitPartManager> getViews(IUnit targetUnit) {
        List<UnitPartManager> r = new ArrayList();
        PartManager upm = this.ctx.getPartManager();
        for (IMPart part : upm.getUnitParts()) {
            UnitPartManager unitPart = upm.getUnitPartManager(part);
            if (unitPart != null) {
                IUnit unit = unitPart.getUnit();
                if ((unit != null) && (
                        (targetUnit == null) || (targetUnit == unit))) {
                    r.add(unitPart);
                }
            }
        }
        return r;
    }

    public UnitPartManager getFocusedView() {
        PartManager upm = this.ctx.getPartManager();
        IMPart part = upm.getActivePart();
        if ((part != null) && (PartManager.isUnitPart(part))) {
            return upm.getUnitPartManager(part);
        }
        return null;
    }

    public boolean openView(IUnit unit) {
        List<IMPart> r = this.ctx.getPartManager().create(unit, true);
        return (r != null) && (!r.isEmpty());
    }
}


