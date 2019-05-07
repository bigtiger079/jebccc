/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.debugger;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*    */ import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
/*    */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerBreakpoint;
/*    */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.UIState;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*    */ import java.util.List;
/*    */ import org.eclipse.swt.SWT;

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
/*    */ public class DebuggerToggleBreakpointHandler
        /*    */ extends DebuggerBaseHandler
        /*    */ {
    /*    */
    public DebuggerToggleBreakpointHandler()
    /*    */ {
        /* 31 */
        super("dbgToggleBreakpoint", S.s(578), null, null, SWT.MOD1 | 0x42);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 38 */
        return getCodeUnitAddress(this.part) != null;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 44 */
        UnitPartManager unitPart = (UnitPartManager) this.part.getManager();
        /* 45 */
        ICodeUnit unit = (ICodeUnit) unitPart.getUnit();
        /*    */
        /* 47 */
        String address = unitPart.getActiveAddress();
        /*    */
        /*    */
        /* 50 */
        IRuntimeProject prj = this.context.getOpenedProject();
        /* 51 */
        List<IDebuggerUnit> debuggers = RuntimeProjectUtil.findUnitsByType(prj, IDebuggerUnit.class, false);
        /* 52 */
        for (IDebuggerUnit dbg : debuggers) {
            /* 53 */
            if ((dbg.getPotentialDebuggees().contains(unit)) &&
                    /* 54 */         (dbg.isAttached())) {
                /* 55 */
                IDebuggerBreakpoint bp = dbg.getBreakpoint(address, unit);
                /* 56 */
                if (bp == null) {
                    /* 57 */
                    dbg.setBreakpoint(address, unit);
                    /*    */
                }
                /*    */
                else {
                    /* 60 */
                    dbg.clearBreakpoint(bp);
                    /*    */
                }
                /* 62 */
                return;
                /*    */
            }
            /*    */
        }
        /*    */
        /*    */
        /*    */
        /* 68 */
        UIState uiState = this.context.getUIState(unit);
        /* 69 */
        if (!uiState.isBreakpoint(address)) {
            /* 70 */
            uiState.setBreakpoint(address, true);
            /*    */
        }
        /*    */
        else {
            /* 73 */
            uiState.removeBreakpoint(address);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\debugger\DebuggerToggleBreakpointHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */