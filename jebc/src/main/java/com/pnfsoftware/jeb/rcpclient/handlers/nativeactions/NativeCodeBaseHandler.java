package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.core.events.JebEvent;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import org.eclipse.swt.widgets.Shell;

public abstract class NativeCodeBaseHandler extends JebBaseHandler {
    public NativeCodeBaseHandler(String id, String name, int accelerator) {
        super(id, name, 0, null, null, accelerator);
    }

    protected boolean canExecuteAndNativeCheck(IMPart part) {
        return canExecuteAndNativeCheck(part, false);
    }

    protected boolean canExecuteAndNativeCheck(IMPart part, boolean needValidAddress) {
        return canExecuteAndNativeCheck(part, needValidAddress, false);
    }

    protected boolean canExecuteAndNativeCheck(IMPart part, boolean needValidAddress, boolean exploreParents) {
        return canExecuteAndNativeCheck(part, needValidAddress, exploreParents, false);
    }

    protected boolean canExecuteAndNativeCheck(IMPart part, boolean needValidAddress, boolean exploreParents, boolean needExistingMethod) {
        if ((part == null) || (isDisableHandlers(part))) {
            return false;
        }
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(part, exploreParents);
        if (pbcu == null) {
            return false;
        }
        if (needValidAddress) {
            long memAddr = getActiveMemoryAddress(part, pbcu);
            if ((memAddr == -1L) || (!canExecuteAt(pbcu, memAddr))) {
                return false;
            }
            if ((needExistingMethod) && (pbcu.getInternalMethod(memAddr, false) == null)) {
                return false;
            }
        }
        return true;
    }

    public boolean canExecuteAt(INativeCodeUnit<?> pbcu, long memAddress) {
        return true;
    }

    public INativeCodeUnit<? extends IInstruction> getNativeCodeUnit(IMPart part) {
        return getNativeCodeUnit(part, false);
    }

    public INativeCodeUnit<? extends IInstruction> getNativeCodeUnit(IMPart part, boolean exploreParents) {
        if (part != null) {
            Object object = part.getManager();
            if ((object instanceof UnitPartManager)) {
                IUnit unit = ((UnitPartManager) object).getUnit();
                if ((unit instanceof INativeCodeUnit)) {
                    return (INativeCodeUnit) unit;
                }
                if (exploreParents) {
                    if ((unit instanceof INativeDecompilerUnit)) {
                        return ((INativeDecompilerUnit) unit).getCodeUnit();
                    }
                    if ((unit instanceof INativeSourceUnit)) {
                        INativeDecompilerUnit<?> decomp = ((INativeSourceUnit) unit).getDecompiler();
                        if (decomp != null) {
                            return decomp.getCodeUnit();
                        }
                    }
                }
            }
        }
        return null;
    }

    public long getActiveMemoryAddress(IMPart part, INativeCodeUnit<?> unit) {
        return getActiveMemoryAddress(part, unit, false);
    }

    public long getActiveMemoryAddress(IMPart part, INativeCodeUnit<?> unit, boolean preferItemAddress) {
        String address = getActiveAddress(part);
        long a = unit.getCanonicalMemoryAddress(address);
        if ((a == -1L) || (preferItemAddress)) {
            long itemId = getActiveItemId(part);
            if (itemId != 0L) {
                String itemAddr = unit.getAddressOfItem(itemId);
                if (itemAddr != null) {
                    long a1 = unit.getCanonicalMemoryAddress(itemAddr);
                    if (a1 != -1L) {
                        a = a1;
                    }
                }
            }
        }
        return a;
    }

    public void notifyUnit(IUnit unit) {
        unit.notifyListeners(new JebEvent(J.UnitChange));
    }

    public static void postExecute(Shell shell) {
        NativeActionUtil.redecompileStaleSourceUnits(shell);
    }
}


