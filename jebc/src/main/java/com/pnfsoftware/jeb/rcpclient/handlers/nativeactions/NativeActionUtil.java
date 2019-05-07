/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*     */
/*     */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
/*     */ import com.pnfsoftware.jeb.core.util.DecompilerHelper;
/*     */ import com.pnfsoftware.jeb.rcpclient.DecompilerListener;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ public class NativeActionUtil
        /*     */ {
    /*  32 */   private static final ILogger logger = GlobalLog.getLogger(NativeActionUtil.class);

    /*     */
    /*     */
    public static INativeType rotateType(ITypeManager typeman, INativeType type) {
        /*  35 */
        INativeType tUINT8 = typeman.getType("unsigned char");
        /*  36 */
        INativeType tCHAR = typeman.getType("char");
        /*  37 */
        INativeType tUINT16 = typeman.getType("unsigned short");
        /*  38 */
        INativeType tUINT32 = typeman.getType("unsigned long");
        /*  39 */
        INativeType tUINT64 = typeman.getType("unsigned long long");
        /*     */
        /*  41 */
        if (type == null) {
            /*  42 */
            type = tUINT8;
            /*     */
        }
        /*  44 */
        else if (type == tUINT8) {
            /*  45 */
            type = tCHAR;
            /*     */
        }
        /*  47 */
        else if (type == tCHAR) {
            /*  48 */
            type = tUINT16;
            /*     */
        }
        /*  50 */
        else if (type == tUINT16) {
            /*  51 */
            type = tUINT32;
            /*     */
        }
        /*  53 */
        else if (type == tUINT32) {
            /*  54 */
            type = tUINT64;
            /*     */
        }
        /*  56 */
        else if (type == tUINT64) {
            /*  57 */
            type = tUINT8;
            /*     */
        }
        /*     */
        else {
            /*  60 */
            type = tUINT8;
            /*     */
        }
        /*  62 */
        return type;
        /*     */
    }

    /*     */
    /*     */
    public static INativeType getItemType(INativeItem item) {
        /*  66 */
        if ((item instanceof INativeDataItem)) {
            /*  67 */
            return ((INativeDataItem) item).getType();
            /*     */
        }
        /*  69 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public static int redecompileStaleSourceUnits(Shell shell) {
        /*  73 */
        int cnt = 0;
        /*     */
        /*  75 */
        for (Iterator localIterator1 = DecompilerListener.getAll().iterator(); localIterator1.hasNext(); ) {
            listener = (DecompilerListener) localIterator1.next();
            /*  76 */
            for (ISourceUnit srcUnit : listener.pullResetUnits()) {
                /*  77 */
                String s = srcUnit.getFullyQualifiedName();
                /*  78 */
                if (s == null) {
                    /*  79 */
                    logger.warn("Cannot recompile unit %s, fully-qualified name is missing", new Object[]{srcUnit});
                    /*     */
                }
                /*     */
                else
                    /*     */ {
                    /*  83 */
                    HandlerUtil.decompileAsync(shell, listener.getContext(), listener.getDecompiler(), s);
                    /*  84 */
                    cnt++;
                    /*     */
                }
            }
        }
        /*     */
        DecompilerListener listener;
        /*  87 */
        return cnt;
        /*     */
    }

    /*     */
    /*     */
    public static INativeCodeUnit<?> getRelatedNativeCodeUnit(IUnit unit) {
        /*  91 */
        if ((unit instanceof INativeCodeUnit)) {
            /*  92 */
            return (INativeCodeUnit) unit;
            /*     */
        }
        /*     */
        /*  95 */
        ICodeUnit code = DecompilerHelper.getRelatedCodeUnit(unit);
        /*  96 */
        if ((code instanceof INativeCodeUnit)) {
            /*  97 */
            return (INativeCodeUnit) code;
            /*     */
        }
        /*     */
        /* 100 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\NativeActionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */