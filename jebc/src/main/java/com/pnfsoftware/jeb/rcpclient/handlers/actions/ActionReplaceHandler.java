
package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import org.eclipse.swt.SWT;

public class ActionReplaceHandler
        extends ActionGenericHandler {
    public ActionReplaceHandler() {
        super(6, "replace", "Replace...", null, "eclipse/correction_cast.png", SWT.MOD1 | 0x4E);
    }
}


