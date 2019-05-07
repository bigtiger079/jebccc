/*     */
package com.pnfsoftware.jeb.rcpclient.handlers;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*     */ import com.pnfsoftware.jeb.core.IUnitCreator;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.TargetProperties;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.TargetProperties.Builder;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.core.util.DebuggerHelper;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import java.util.concurrent.Callable;
/*     */ import org.eclipse.swt.widgets.MessageBox;
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
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class HandlerUtil
        /*     */ {
    /*     */
    public static IDebuggerUnit getCurrentDebugger(RcpClientContext context, IUnit unit)
    /*     */ {
        /*  42 */
        if ((unit instanceof IDebuggerUnit)) {
            /*  43 */
            return (IDebuggerUnit) unit;
            /*     */
        }
        /*  45 */
        if ((unit instanceof ICodeUnit)) {
            /*  46 */
            return DebuggerHelper.getDebuggerForUnit(context.getOpenedProject(), (ICodeUnit) unit);
            /*     */
        }
        /*     */
        /*  49 */
        return getCurrentDebuggerOld(context, unit);
        /*     */
    }

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
    @Deprecated
    /*     */ public static IDebuggerUnit getCurrentDebuggerOld(RcpClientContext context, IUnit baseUnit)
    /*     */ {
        /*  64 */
        if (baseUnit != null) {
            /*  65 */
            if ((baseUnit instanceof IDebuggerUnit)) {
                /*  66 */
                return (IDebuggerUnit) baseUnit;
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /*  71 */
            IUnit unit = baseUnit;
            /*     */
            for (; ; ) {
                /*  73 */
                for (IUnit c : unit.getChildren()) {
                    /*  74 */
                    if ((c instanceof IDebuggerUnit)) {
                        /*  75 */
                        return (IDebuggerUnit) c;
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /*  79 */
                IUnitCreator parent = unit.getParent();
                /*  80 */
                if (!(parent instanceof IUnit)) {
                    /*     */
                    break;
                    /*     */
                }
                /*     */
                /*  84 */
                unit = (IUnit) parent;
                /*     */
            }
            /*     */
        }
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
        /*  98 */
        return null;
        /*     */
    }

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
    /*     */
    /*     */
    /*     */
    public static boolean processUnit(Shell shell, RcpClientContext context, IUnit unit, boolean async)
    /*     */ {
        /* 119 */
        if (unit.isProcessed()) {
            /* 120 */
            return true;
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 125 */
        if (unit.getStatus() != null) {
            /* 126 */
            String msg = String.format("Processing of unit \"%s\" (%s) was attempted and failed.\n\nWould you like to try again?", new Object[]{unit
                    /*     */
                    /* 128 */.getName(), unit.getFormatType()});
            /* 129 */
            MessageBox mb = new MessageBox(shell, 200);
            /* 130 */
            mb.setText(S.s(304));
            /* 131 */
            mb.setMessage(msg);
            /* 132 */
            int r = mb.open();
            /* 133 */
            if (r != 64) {
                /* 134 */
                return false;
                /*     */
            }
            /*     */
        }
        /*     */
        Boolean success;
        /*     */
        Boolean success;
        /* 139 */
        if (!async) {
            /* 140 */
            success = Boolean.valueOf(unit.process());
            /*     */
        }
        /*     */
        else {
            /* 143 */
            String taskName = String.format("Processing: %s...", new Object[]{unit.getName()});
            /* 144 */
            success = (Boolean) context.executeTask(taskName, new Callable()
                    /*     */ {
                /*     */
                public Boolean call() {
                    /* 147 */
                    return Boolean.valueOf(this.val$unit.process());
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /*     */
        /* 152 */
        if ((success == null) || (!success.booleanValue())) {
            /* 153 */
            String msg = String.format("%s. %s:\n\"%s\"\n\n", new Object[]{S.s(789), S.s(748), unit
                    /* 154 */.getStatus()});
            /* 155 */
            msg = msg + String.format("%s (%s)", new Object[]{S.s(662), S.s(603)});
            /* 156 */
            MessageBox mb = new MessageBox(shell, 200);
            /* 157 */
            mb.setText(S.s(304));
            /* 158 */
            mb.setMessage(msg);
            /* 159 */
            int r = mb.open();
            /* 160 */
            if (r != 64) {
                /* 161 */
                return false;
                /*     */
            }
            /*     */
        }
        /* 164 */
        return true;
        /*     */
    }

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
    public static ISourceUnit decompileAsync(Shell shell, RcpClientContext context, IDecompilerUnit decompiler, final String address)
    /*     */ {
        /* 180 */
        if (!decompiler.canDecompile(address)) {
            /* 181 */
            return null;
            /*     */
        }
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
        /* 193 */
        context.getTelemetry().record("handlerDecompile", "decompilerUnitType", decompiler.getFormatType());
        /*     */
        /* 195 */
        String taskName = String.format("%s: %s...", new Object[]{S.s(246), address});
        /* 196 */
        ISourceUnit r = (ISourceUnit) context.executeTaskWithPopupDelay(1000, taskName, false, new Callable()
                /*     */ {
            /*     */
            public ISourceUnit call() {
                /* 199 */
                if ((this.val$decompiler instanceof INativeDecompilerUnit)) {
                    /* 200 */
                    TargetProperties properties = TargetProperties.create().setDiscardable(Boolean.valueOf(false)).build();
                    /* 201 */
                    return ((INativeDecompilerUnit) this.val$decompiler).decompile(address, properties);
                    /*     */
                }
                /* 203 */
                return this.val$decompiler.decompile(address);
                /*     */
            }
            /* 205 */
        });
        /* 206 */
        return r;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\HandlerUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */