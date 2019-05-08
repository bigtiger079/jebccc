
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.type.IArrayType;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public class ActionEditArrayHandler
        extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionEditArrayHandler.class);

    public ActionEditArrayHandler() {
        super("editArray", "Create/" + S.s(488), 42);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true);
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        long a = getActiveMemoryAddress(this.part, pbcu);
        INativeType type = pbcu.getDataTypeAt(a);
        if (type != null) {
            int count = 0;
            INativeType basetype = type;
            if ((type instanceof IArrayType)) {
                count = ((IArrayType) type).getElementCount();
                basetype = ((IArrayType) type).getElementType();
            }
            String currentCount = String.format("%d", new Object[]{Integer.valueOf(count)});
            String caption = String.format("Edit array length at %Xh", new Object[]{Long.valueOf(a)});
            TextDialog dlg = new TextDialog(this.shell, caption, currentCount, null);
            dlg.setLineCount(1);
            dlg.setSelected(true);
            String text = dlg.open();
            if (text != null) {
                int count2 = Conversion.stringToInt(text, -1);
                if (count2 >= 0) {
                    if (count2 == 0) {
                        type = basetype;
                    } else {
                        type = pbcu.getTypeManager().createArray(basetype, count2);
                    }
                    if (!pbcu.setDataTypeAt(a, type)) {
                        logger.error("Failed to define array at address %Xh", new Object[]{Long.valueOf(a)});
                    }
                }
            }
        }
        postExecute(this.shell);
    }
}


