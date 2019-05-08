package com.pnfsoftware.jeb.rcpclient.handlers.windows;

import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;

public class WindowFocusPartHandler extends JebBaseHandler {
    IMPart part0;

    public WindowFocusPartHandler(IMPart part0) {
        super(null, part0.getLabel(), null, null);
        this.part0 = part0;
    }

    public boolean canExecute() {
        return true;
    }

    public void execute() {
        this.context.getPartManager().activatePart(this.part0, true);
    }
}


