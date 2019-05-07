
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;


import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
import com.pnfsoftware.jeb.core.util.DecompilerHelper;
import com.pnfsoftware.jeb.rcpclient.DecompilerListener;
import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Shell;


public class NativeActionUtil {
    private static final ILogger logger = GlobalLog.getLogger(NativeActionUtil.class);


    public static INativeType rotateType(ITypeManager typeman, INativeType type) {

        INativeType tUINT8 = typeman.getType("unsigned char");

        INativeType tCHAR = typeman.getType("char");

        INativeType tUINT16 = typeman.getType("unsigned short");

        INativeType tUINT32 = typeman.getType("unsigned long");

        INativeType tUINT64 = typeman.getType("unsigned long long");


        if (type == null) {

            type = tUINT8;

        } else if (type == tUINT8) {

            type = tCHAR;

        } else if (type == tCHAR) {

            type = tUINT16;

        } else if (type == tUINT16) {

            type = tUINT32;

        } else if (type == tUINT32) {

            type = tUINT64;

        } else if (type == tUINT64) {

            type = tUINT8;

        } else {

            type = tUINT8;

        }

        return type;

    }


    public static INativeType getItemType(INativeItem item) {

        if ((item instanceof INativeDataItem)) {

            return ((INativeDataItem) item).getType();

        }

        return null;

    }


    public static int redecompileStaleSourceUnits(Shell shell) {

        int cnt = 0;

        DecompilerListener listener;
        for (Iterator localIterator1 = DecompilerListener.getAll().iterator(); localIterator1.hasNext(); ) {
            listener = (DecompilerListener) localIterator1.next();

            for (ISourceUnit srcUnit : listener.pullResetUnits()) {

                String s = srcUnit.getFullyQualifiedName();

                if (s == null) {

                    logger.warn("Cannot recompile unit %s, fully-qualified name is missing", new Object[]{srcUnit});

                } else {

                    HandlerUtil.decompileAsync(shell, listener.getContext(), listener.getDecompiler(), s);

                    cnt++;

                }
            }
        }

        return cnt;
    }


    public static INativeCodeUnit<?> getRelatedNativeCodeUnit(IUnit unit) {

        if ((unit instanceof INativeCodeUnit)) {

            return (INativeCodeUnit) unit;

        }


        ICodeUnit code = DecompilerHelper.getRelatedCodeUnit(unit);

        if ((code instanceof INativeCodeUnit)) {

            return (INativeCodeUnit) code;

        }


        return null;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\NativeActionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */