
package com.pnfsoftware.jeb.rcpclient.handlers.windows;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import com.pnfsoftware.jeb.util.base.OSType;

public class WindowRefreshHandler
        extends OperationHandler {
    public WindowRefreshHandler() {
        super(Operation.REFRESH, "refresh", S.s(546), null, null);
        setAccelerator(OSType.determine().isMac() ? 4194386 : 16777230);
    }
}


