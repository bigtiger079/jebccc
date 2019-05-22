package com.pnfsoftware.jeb.rcpclient.handlers.windows;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import org.eclipse.swt.SWT;

public class WindowClosepartHandler extends JebBaseHandler {
    public WindowClosepartHandler() {
        super(null, S.s(467), null, null);
        setAccelerator(SWT.MOD1 | 0x57);
    }

    public boolean canExecute() {
        return (this.part != null) && (this.part.isHideable());
    }

    public void execute() {
        this.context.getPartManager().closePart(this.part);
    }
}


