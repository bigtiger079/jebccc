package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.parts.UIState;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import java.util.List;
import org.eclipse.swt.SWT;

public class DebuggerRunToLineHandler extends DebuggerBaseHandler {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerRunToLineHandler.execute():void, dex: 
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
        	at jadx.core.ProcessClass.process(ProcessClass.java:29)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.base/java.lang.Iterable.forEach(Unknown Source)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.getJavaMethodByNode(JadxDecompiler.java:322)
        	at jadx.api.JavaClass.convertNode(JavaClass.java:163)
        	at jadx.api.JavaClass.getJavaNodeAtPosition(JavaClass.java:181)
        Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:581)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:74)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
        	... 11 more
        */
    public void execute() {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerRunToLineHandler.execute():void, dex: 
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerRunToLineHandler.execute():void");
    }

    public DebuggerRunToLineHandler() {
        super("dbgRunToLine", S.s(555), null, "eclipse/runtoline_co.png", SWT.MOD1 | 82);
    }

    public boolean canExecute() {
        return canStepOperation(this.part) && getCodeUnitAddress(this.part) != null;
    }

    private boolean setTemporaryBp(IMPart part) {
        UnitPartManager unitPart = (UnitPartManager) part.getManager();
        ICodeUnit unit = (ICodeUnit) unitPart.getUnit();
        String address = unitPart.getActiveAddress();
        List<IDebuggerUnit> debuggers = RuntimeProjectUtil.findUnitsByType(this.context.getOpenedProject(), IDebuggerUnit.class, false);
        for (IDebuggerUnit dbg : debuggers) {
            if (dbg.getPotentialDebuggees().contains(unit) && dbg.getBreakpoint(address, unit) != null) {
                return false;
            }
        }
        UIState uiState = this.context.getUIState(unit);
        for (IDebuggerUnit dbg2 : debuggers) {
            if (dbg2.getPotentialDebuggees().contains(unit)) {
                dbg2.setBreakpoint(address, unit);
                uiState.setTemporaryBreakpoint(address, 1);
                return true;
            }
        }
        if (uiState.isBreakpoint(address)) {
            return false;
        }
        uiState.setTemporaryBreakpoint(address, 1);
        return true;
    }
}