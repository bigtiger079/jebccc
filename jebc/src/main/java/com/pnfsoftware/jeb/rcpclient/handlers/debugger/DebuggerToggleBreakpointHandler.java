
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerBreakpoint;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.parts.UIState;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;

import java.util.List;

import org.eclipse.swt.SWT;

public class DebuggerToggleBreakpointHandler
        extends DebuggerBaseHandler {
    public DebuggerToggleBreakpointHandler() {
        super("dbgToggleBreakpoint", S.s(578), null, null, SWT.MOD1 | 0x42);
    }

    public boolean canExecute() {
        return getCodeUnitAddress(this.part) != null;
    }

    public void execute() {
        UnitPartManager unitPart = (UnitPartManager) this.part.getManager();
        ICodeUnit unit = (ICodeUnit) unitPart.getUnit();
        String address = unitPart.getActiveAddress();
        IRuntimeProject prj = this.context.getOpenedProject();
        List<IDebuggerUnit> debuggers = RuntimeProjectUtil.findUnitsByType(prj, IDebuggerUnit.class, false);
        for (IDebuggerUnit dbg : debuggers) {
            if ((dbg.getPotentialDebuggees().contains(unit)) &&
                    (dbg.isAttached())) {
                IDebuggerBreakpoint bp = dbg.getBreakpoint(address, unit);
                if (bp == null) {
                    dbg.setBreakpoint(address, unit);
                } else {
                    dbg.clearBreakpoint(bp);
                }
                return;
            }
        }
        UIState uiState = this.context.getUIState(unit);
        if (!uiState.isBreakpoint(address)) {
            uiState.setBreakpoint(address, true);
        } else {
            uiState.removeBreakpoint(address);
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerToggleBreakpointHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */