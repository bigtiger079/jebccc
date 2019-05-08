
package com.pnfsoftware.jeb.rcpclient.handlers;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.TargetProperties;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.TargetProperties.Builder;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.core.util.DebuggerHelper;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class HandlerUtil {
    public static IDebuggerUnit getCurrentDebugger(RcpClientContext context, IUnit unit) {
        if ((unit instanceof IDebuggerUnit)) {
            return (IDebuggerUnit) unit;
        }
        if ((unit instanceof ICodeUnit)) {
            return DebuggerHelper.getDebuggerForUnit(context.getOpenedProject(), (ICodeUnit) unit);
        }
        return getCurrentDebuggerOld(context, unit);
    }

    @Deprecated
    public static IDebuggerUnit getCurrentDebuggerOld(RcpClientContext context, IUnit baseUnit) {
        if (baseUnit != null) {
            if ((baseUnit instanceof IDebuggerUnit)) {
                return (IDebuggerUnit) baseUnit;
            }
            IUnit unit = baseUnit;
            for (; ; ) {
                for (IUnit c : unit.getChildren()) {
                    if ((c instanceof IDebuggerUnit)) {
                        return (IDebuggerUnit) c;
                    }
                }
                IUnitCreator parent = unit.getParent();
                if (!(parent instanceof IUnit)) {
                    break;
                }
                unit = (IUnit) parent;
            }
        }
        return null;
    }

    public static boolean processUnit(Shell shell, RcpClientContext context, IUnit unit, boolean async) {
        if (unit.isProcessed()) {
            return true;
        }
        if (unit.getStatus() != null) {
            String msg = String.format("Processing of unit \"%s\" (%s) was attempted and failed.\n\nWould you like to try again?", new Object[]{unit
                    .getName(), unit.getFormatType()});
            MessageBox mb = new MessageBox(shell, 200);
            mb.setText(S.s(304));
            mb.setMessage(msg);
            int r = mb.open();
            if (r != 64) {
                return false;
            }
        }
        Boolean success;
        if (!async) {
            success = Boolean.valueOf(unit.process());
        } else {
            String taskName = String.format("Processing: %s...", new Object[]{unit.getName()});
            success = (Boolean) context.executeTask(taskName, new Callable() {
                public Boolean call() {
                    return unit.process();
                }
            });
        }
        if ((success == null) || (!success.booleanValue())) {
            String msg = String.format("%s. %s:\n\"%s\"\n\n", S.s(789), S.s(748), unit.getStatus());
            msg = msg + String.format("%s (%s)", S.s(662), S.s(603));
            MessageBox mb = new MessageBox(shell, 200);
            mb.setText(S.s(304));
            mb.setMessage(msg);
            int r = mb.open();
            if (r != 64) {
                return false;
            }
        }
        return true;
    }

    public static ISourceUnit decompileAsync(Shell shell, RcpClientContext context, IDecompilerUnit decompiler, final String address) {
        if (!decompiler.canDecompile(address)) {
            return null;
        }
        context.getTelemetry().record("handlerDecompile", "decompilerUnitType", decompiler.getFormatType());
        String taskName = String.format("%s: %s...", S.s(246), address);
        ISourceUnit r = (ISourceUnit) context.executeTaskWithPopupDelay(1000, taskName, false, new Callable() {
            public ISourceUnit call() {
                if ((decompiler instanceof INativeDecompilerUnit)) {
                    TargetProperties properties = TargetProperties.create().setDiscardable(Boolean.valueOf(false)).build();
                    return ((INativeDecompilerUnit) decompiler).decompile(address, properties);
                }
                return decompiler.decompile(address);
            }
        });
        return r;
    }
}
