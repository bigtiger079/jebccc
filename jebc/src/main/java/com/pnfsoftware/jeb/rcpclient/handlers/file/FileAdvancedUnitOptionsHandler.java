/*    */
package com.pnfsoftware.jeb.rcpclient.handlers.file;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilerUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.DecompDynamicOptionsDialog;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.NativeCodeBaseHandler;
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
/*    */
/*    */
/*    */ public class FileAdvancedUnitOptionsHandler
        /*    */ extends JebBaseHandler
        /*    */ {
    /* 27 */   private static final ILogger logger = GlobalLog.getLogger(FileAdvancedUnitOptionsHandler.class);

    /*    */
    /*    */
    public FileAdvancedUnitOptionsHandler() {
        /* 30 */
        super(null, "Advanced Unit Options...", null, null);
        /*    */
    }

    /*    */
    /*    */
    public boolean canExecute()
    /*    */ {
        /* 35 */
        return getActiveUnit(this.part) != null;
        /*    */
    }

    /*    */
    /*    */
    public void execute()
    /*    */ {
        /* 40 */
        IUnit unit = getActiveUnit(this.part);
        /* 41 */
        String address = getActiveAddress(this.part);
        /*    */
        /*    */
        /* 44 */
        if ((unit instanceof INativeSourceUnit)) {
            /* 45 */
            INativeSourceUnit src = (INativeSourceUnit) unit;
            /*    */
            /* 47 */
            DecompDynamicOptionsDialog dlg = new DecompDynamicOptionsDialog(this.shell, src, address);
            /* 48 */
            boolean optionsChanged = dlg.open().booleanValue();
            /* 49 */
            if (optionsChanged) {
                /* 50 */
                INativeDecompilerUnit<?> decompiler = src.getDecompiler();
                /* 51 */
                decompiler.resetDecompilation(address, false);
                /*    */
                /*    */
                /* 54 */
                HandlerUtil.decompileAsync(this.shell, this.context, decompiler, address);
                /*    */
            }
            /*    */
            /* 57 */
            NativeCodeBaseHandler.postExecute(this.shell);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileAdvancedUnitOptionsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */