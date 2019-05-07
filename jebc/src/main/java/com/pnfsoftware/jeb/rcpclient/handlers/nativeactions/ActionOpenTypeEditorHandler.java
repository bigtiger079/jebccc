/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IArrayType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IReferenceType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.TypeUtil;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.NativeTypeEditorDialog;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
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
/*    */
/*    */ public class ActionOpenTypeEditorHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /* 33 */   private static final ILogger logger = GlobalLog.getLogger(ActionOpenTypeEditorHandler.class);

    /*    */
    /*    */
    public ActionOpenTypeEditorHandler() {
        /* 36 */
        super("openTypeEditor", S.s(531), SWT.MOD1 | SWT.MOD3 | 0x54);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 41 */
        return canExecuteAndNativeCheck(this.part, false, true);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 46 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part, true);
        /*    */
        /* 48 */
        INativeType type0 = null;
        /* 49 */
        long itemId = getActiveItemId(this.part);
        /* 50 */
        if (itemId != 0L) {
            /* 51 */
            INativeItem item = pbcu.getItemObject(itemId);
            /* 52 */
            if ((item instanceof INativeType)) {
                /* 53 */
                type0 = (INativeType) item;
                /*    */
            }
            /*    */
        }
        /*    */
        /* 57 */
        if (type0 == null) {
            /* 58 */
            long a = getActiveMemoryAddress(this.part, pbcu, true);
            /* 59 */
            if (a != -1L) {
                /* 60 */
                INativeContinuousItem item = pbcu.getNativeItemAt(a);
                /* 61 */
                if ((item instanceof INativeDataItem)) {
                    /* 62 */
                    type0 = ((INativeDataItem) item).getType();
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /*    */
        /* 67 */
        if ((type0 != null) && (!(type0 instanceof IStructureType))) {
            /* 68 */
            type0 = TypeUtil.getNonAlias(type0);
            /* 69 */
            if ((type0 instanceof IReferenceType)) {
                /* 70 */
                type0 = ((IReferenceType) type0).getMainType();
                /*    */
            }
            /* 72 */
            else if ((type0 instanceof IArrayType)) {
                /* 73 */
                type0 = ((IArrayType) type0).getElementType();
                /*    */
            }
            /* 75 */
            type0 = TypeUtil.getNonAlias(type0);
            /*    */
        }
        /*    */
        /* 78 */
        IStructureType type = null;
        /* 79 */
        if ((type0 instanceof IStructureType)) {
            /* 80 */
            type = (IStructureType) type0;
            /*    */
        }
        /*    */
        /* 83 */
        NativeTypeEditorDialog dlg = new NativeTypeEditorDialog(this.shell, pbcu, type, this.context.getFontManager());
        /* 84 */
        dlg.open();
        /*    */
        /* 86 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionOpenTypeEditorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */