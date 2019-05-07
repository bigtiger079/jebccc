/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*     */
/*     */

import com.pnfsoftware.jeb.core.events.J;
/*     */ import com.pnfsoftware.jeb.core.events.JebEvent;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
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
/*     */ public abstract class NativeCodeBaseHandler
        /*     */ extends JebBaseHandler
        /*     */ {
    /*     */
    public NativeCodeBaseHandler(String id, String name, int accelerator)
    /*     */ {
        /*  31 */
        super(id, name, 0, null, null, accelerator);
        /*     */
    }

    /*     */
    /*     */
    protected boolean canExecuteAndNativeCheck(IMPart part) {
        /*  35 */
        return canExecuteAndNativeCheck(part, false);
        /*     */
    }

    /*     */
    /*     */
    protected boolean canExecuteAndNativeCheck(IMPart part, boolean needValidAddress) {
        /*  39 */
        return canExecuteAndNativeCheck(part, needValidAddress, false);
        /*     */
    }

    /*     */
    /*     */
    protected boolean canExecuteAndNativeCheck(IMPart part, boolean needValidAddress, boolean exploreParents) {
        /*  43 */
        return canExecuteAndNativeCheck(part, needValidAddress, exploreParents, false);
        /*     */
    }

    /*     */
    /*     */
    protected boolean canExecuteAndNativeCheck(IMPart part, boolean needValidAddress, boolean exploreParents, boolean needExistingMethod)
    /*     */ {
        /*  48 */
        if ((part == null) || (isDisableHandlers(part))) {
            /*  49 */
            return false;
            /*     */
        }
        /*     */
        /*  52 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(part, exploreParents);
        /*  53 */
        if (pbcu == null) {
            /*  54 */
            return false;
            /*     */
        }
        /*     */
        /*  57 */
        if (needValidAddress) {
            /*  58 */
            long memAddr = getActiveMemoryAddress(part, pbcu);
            /*  59 */
            if ((memAddr == -1L) || (!canExecuteAt(pbcu, memAddr))) {
                /*  60 */
                return false;
                /*     */
            }
            /*  62 */
            if ((needExistingMethod) && (pbcu.getInternalMethod(memAddr, false) == null)) {
                /*  63 */
                return false;
                /*     */
            }
            /*     */
        }
        /*     */
        /*  67 */
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
    public boolean canExecuteAt(INativeCodeUnit<?> pbcu, long memAddress)
    /*     */ {
        /*  79 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public INativeCodeUnit<?> getNativeCodeUnit(IMPart part) {
        /*  83 */
        return getNativeCodeUnit(part, false);
        /*     */
    }

    /*     */
    /*     */
    public INativeCodeUnit<?> getNativeCodeUnit(IMPart part, boolean exploreParents) {
        /*  87 */
        if (part != null) {
            /*  88 */
            Object object = part.getManager();
            /*  89 */
            if ((object instanceof UnitPartManager)) {
                /*  90 */
                IUnit unit = ((UnitPartManager) object).getUnit();
                /*  91 */
                if ((unit instanceof INativeCodeUnit)) {
                    /*  92 */
                    return (INativeCodeUnit) unit;
                    /*     */
                }
                /*  94 */
                if (exploreParents) {
                    /*  95 */
                    if ((unit instanceof INativeDecompilerUnit)) {
                        /*  96 */
                        return ((INativeDecompilerUnit) unit).getCodeUnit();
                        /*     */
                    }
                    /*  98 */
                    if ((unit instanceof INativeSourceUnit)) {
                        /*  99 */
                        INativeDecompilerUnit<?> decomp = ((INativeSourceUnit) unit).getDecompiler();
                        /* 100 */
                        if (decomp != null) {
                            /* 101 */
                            return decomp.getCodeUnit();
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 107 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public long getActiveMemoryAddress(IMPart part, INativeCodeUnit<?> unit)
    /*     */ {
        /* 115 */
        return getActiveMemoryAddress(part, unit, false);
        /*     */
    }

    /*     */
    /*     */
    public long getActiveMemoryAddress(IMPart part, INativeCodeUnit<?> unit, boolean preferItemAddress) {
        /* 119 */
        String address = getActiveAddress(part);
        /* 120 */
        long a = unit.getCanonicalMemoryAddress(address);
        /* 121 */
        if ((a == -1L) || (preferItemAddress))
            /*     */ {
            /* 123 */
            long itemId = getActiveItemId(part);
            /* 124 */
            if (itemId != 0L) {
                /* 125 */
                String itemAddr = unit.getAddressOfItem(itemId);
                /* 126 */
                if (itemAddr != null) {
                    /* 127 */
                    long a1 = unit.getCanonicalMemoryAddress(itemAddr);
                    /* 128 */
                    if (a1 != -1L) {
                        /* 129 */
                        a = a1;
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 134 */
        return a;
        /*     */
    }

    /*     */
    /*     */
    public void notifyUnit(IUnit unit) {
        /* 138 */
        unit.notifyListeners(new JebEvent(J.UnitChange));
        /*     */
    }

    /*     */
    /*     */
    public static void postExecute(Shell shell) {
        /* 142 */
        NativeActionUtil.redecompileStaleSourceUnits(shell);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\NativeCodeBaseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */