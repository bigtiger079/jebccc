
package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.asm.decompiler.INativeSourceUnit;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.DecompDynamicOptionsDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.NativeCodeBaseHandler;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class FileAdvancedUnitOptionsHandler
        extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(FileAdvancedUnitOptionsHandler.class);

    public FileAdvancedUnitOptionsHandler() {
        super(null, "Advanced Unit Options...", null, null);
    }

    public boolean canExecute() {
        return getActiveUnit(this.part) != null;
    }

    public void execute() {
        IUnit unit = getActiveUnit(this.part);
        String address = getActiveAddress(this.part);
        if ((unit instanceof INativeSourceUnit)) {
            INativeSourceUnit src = (INativeSourceUnit) unit;
            DecompDynamicOptionsDialog dlg = new DecompDynamicOptionsDialog(this.shell, src, address);
            boolean optionsChanged = dlg.open().booleanValue();
            if (optionsChanged) {
                INativeDecompilerUnit<?> decompiler = src.getDecompiler();
                decompiler.resetDecompilation(address, false);
                HandlerUtil.decompileAsync(this.shell, this.context, decompiler, address);
            }
            NativeCodeBaseHandler.postExecute(this.shell);
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\file\FileAdvancedUnitOptionsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */