/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*    */
/*    */

import com.pnfsoftware.jeb.client.S;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.IArrayType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
/*    */ import com.pnfsoftware.jeb.util.encoding.Conversion;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;

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
/*    */ public class ActionEditArrayHandler
        /*    */ extends NativeCodeBaseHandler
        /*    */ {
    /* 25 */   private static final ILogger logger = GlobalLog.getLogger(ActionEditArrayHandler.class);

    /*    */
    /*    */
    public ActionEditArrayHandler() {
        /* 28 */
        super("editArray", "Create/" + S.s(488), 42);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 33 */
        return canExecuteAndNativeCheck(this.part, true);
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 38 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /* 39 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*    */
        /* 41 */
        INativeType type = pbcu.getDataTypeAt(a);
        /* 42 */
        if (type != null) {
            /* 43 */
            int count = 0;
            /* 44 */
            INativeType basetype = type;
            /* 45 */
            if ((type instanceof IArrayType)) {
                /* 46 */
                count = ((IArrayType) type).getElementCount();
                /* 47 */
                basetype = ((IArrayType) type).getElementType();
                /*    */
            }
            /*    */
            /*    */
            /* 51 */
            String currentCount = String.format("%d", new Object[]{Integer.valueOf(count)});
            /* 52 */
            String caption = String.format("Edit array length at %Xh", new Object[]{Long.valueOf(a)});
            /* 53 */
            TextDialog dlg = new TextDialog(this.shell, caption, currentCount, null);
            /* 54 */
            dlg.setLineCount(1);
            /* 55 */
            dlg.setSelected(true);
            /*    */
            /* 57 */
            String text = dlg.open();
            /* 58 */
            if (text != null) {
                /* 59 */
                int count2 = Conversion.stringToInt(text, -1);
                /* 60 */
                if (count2 >= 0) {
                    /* 61 */
                    if (count2 == 0) {
                        /* 62 */
                        type = basetype;
                        /*    */
                    }
                    /*    */
                    else {
                        /* 65 */
                        type = pbcu.getTypeManager().createArray(basetype, count2);
                        /*    */
                    }
                    /*    */
                    /* 68 */
                    if (!pbcu.setDataTypeAt(a, type)) {
                        /* 69 */
                        logger.error("Failed to define array at address %Xh", new Object[]{Long.valueOf(a)});
                        /*    */
                    }
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /*    */
        /* 75 */
        postExecute(this.shell);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionEditArrayHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */