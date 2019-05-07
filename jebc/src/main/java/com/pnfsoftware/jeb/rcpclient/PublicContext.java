/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.ButtonGroupType;
/*     */ import com.pnfsoftware.jeb.client.api.IGraphicalClientContext;
/*     */ import com.pnfsoftware.jeb.client.api.IconType;
/*     */ import com.pnfsoftware.jeb.core.IEnginesContext;
/*     */ import com.pnfsoftware.jeb.core.Version;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.InputDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import org.eclipse.swt.widgets.MessageBox;

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
/*     */
/*     */ public class PublicContext
        /*     */ implements IGraphicalClientContext
        /*     */ {
    /*  39 */   private static final ILogger logger = GlobalLog.getLogger(PublicContext.class);
    /*     */ RcpClientContext ctx;

    /*     */
    /*     */
    public PublicContext(RcpClientContext ctx)
    /*     */ {
        /*  44 */
        this.ctx = ctx;
        /*     */
    }

    /*     */
    /*     */
    public RcpClientContext getContext() {
        /*  48 */
        return this.ctx;
        /*     */
    }

    /*     */
    /*     */
    public String[] getArguments()
    /*     */ {
        /*  53 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public String getBaseDirectory()
    /*     */ {
        /*  59 */
        return this.ctx.getBaseDirectory();
        /*     */
    }

    /*     */
    /*     */
    public String getProgramDirectory()
    /*     */ {
        /*  64 */
        return this.ctx.getProgramDirectory();
        /*     */
    }

    /*     */
    /*     */
    public Version getSoftwareVersion()
    /*     */ {
        /*  69 */
        return new Version(RcpClientContext.app_ver);
        /*     */
    }

    /*     */
    /*     */
    public IEnginesContext getEnginesContext()
    /*     */ {
        /*  74 */
        return this.ctx.getEnginesContext();
        /*     */
    }

    /*     */
    /*     */
    public String displayQuestionBox(String caption, String message, String defaultValue)
    /*     */ {
        /*  79 */
        InputDialog dlg = new InputDialog(UI.getShellTracker().get(), caption, defaultValue);
        /*  80 */
        dlg.setMessage(message);
        /*  81 */
        String value = dlg.open();
        /*  82 */
        return value;
        /*     */
    }

    /*     */
    /*     */
    public int displayMessageBox(String caption, String message, IconType iconType, ButtonGroupType bgType)
    /*     */ {
        /*  87 */
        int flags = 0;
        /*  88 */
        if (iconType != null) {
            /*  89 */
            switch (iconType) {
                /*     */
                case QUESTION:
                    /*  91 */
                    flags |= 0x4;
                    /*  92 */
                    break;
                /*     */
                case INFORMATION:
                    /*  94 */
                    flags |= 0x2;
                    /*  95 */
                    break;
                /*     */
                case WARNING:
                    /*  97 */
                    flags |= 0x8;
                    /*  98 */
                    break;
                /*     */
                case ERROR:
                    /* 100 */
                    flags |= 0x1;
                    /* 101 */
                    break;
                /*     */
            }
            /*     */
            /*     */
        }
        /*     */
        /* 106 */
        if (bgType != null) {
            /* 107 */
            switch (bgType) {
                /*     */
                case OK:
                    /* 109 */
                    flags |= 0x20;
                    /* 110 */
                    break;
                /*     */
                case OK_CANCEL:
                    /* 112 */
                    flags |= 0x120;
                    /* 113 */
                    break;
                /*     */
                case YES_NO:
                    /* 115 */
                    flags |= 0xC0;
                    /* 116 */
                    break;
                /*     */
                case YES_NO_CANCEL:
                    /* 118 */
                    flags |= 0x1C0;
                    /* 119 */
                    break;
                /*     */
            }
            /*     */
            /*     */
        }
        /*     */
        /*     */
        /* 125 */
        MessageBox dlg = new MessageBox(UI.getShellTracker().get(), flags);
        /* 126 */
        if (caption != null) {
            /* 127 */
            dlg.setText(caption);
            /*     */
        }
        /* 129 */
        if (message != null) {
            /* 130 */
            dlg.setMessage(message);
            /*     */
        }
        /* 132 */
        int r = dlg.open();
        /*     */
        /* 134 */
        switch (r) {
            /*     */
            case 256:
                /* 136 */
                return 0;
            /*     */
            case 32:
                /* 138 */
                return 1;
            /*     */
            case 64:
                /* 140 */
                return 2;
            /*     */
            case 128:
                /* 142 */
                return 3;
            /*     */
        }
        /*     */
        /*     */
        /* 146 */
        return 0;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean executeAsync(String taskName, Runnable runnable)
    /*     */ {
        /* 152 */
        return this.ctx.executeTask(taskName, runnable);
        /*     */
    }

    /*     */
    /*     */
    public <T> T executeAsyncWithReturn(String taskName, Callable<T> callable)
    /*     */ {
        /* 157 */
        return (T) this.ctx.executeTask(taskName, callable);
        /*     */
    }

    /*     */
    /*     */
    public List<UnitPartManager> getViews()
    /*     */ {
        /* 162 */
        return getViews(null);
        /*     */
    }

    /*     */
    /*     */
    public List<UnitPartManager> getViews(IUnit targetUnit)
    /*     */ {
        /* 167 */
        List<UnitPartManager> r = new ArrayList();
        /* 168 */
        PartManager upm = this.ctx.getPartManager();
        /* 169 */
        for (IMPart part : upm.getUnitParts()) {
            /* 170 */
            UnitPartManager unitPart = upm.getUnitPartManager(part);
            /* 171 */
            if (unitPart != null) {
                /* 172 */
                IUnit unit = unitPart.getUnit();
                /* 173 */
                if ((unit != null) && (
                        /* 174 */           (targetUnit == null) || (targetUnit == unit))) {
                    /* 175 */
                    r.add(unitPart);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 179 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public UnitPartManager getFocusedView()
    /*     */ {
        /* 184 */
        PartManager upm = this.ctx.getPartManager();
        /* 185 */
        IMPart part = upm.getActivePart();
        /* 186 */
        if ((part != null) && (PartManager.isUnitPart(part))) {
            /* 187 */
            return upm.getUnitPartManager(part);
            /*     */
        }
        /* 189 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public boolean openView(IUnit unit)
    /*     */ {
        /* 194 */
        List<IMPart> r = this.ctx.getPartManager().create(unit, true);
        /* 195 */
        return (r != null) && (!r.isEmpty());
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\PublicContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */