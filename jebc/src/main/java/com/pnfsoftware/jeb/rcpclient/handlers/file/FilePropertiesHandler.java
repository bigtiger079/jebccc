package com.pnfsoftware.jeb.rcpclient.handlers.file;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.rcpclient.handlers.OperationHandler;
import org.eclipse.swt.SWT;

public class FilePropertiesHandler extends OperationHandler {
    public FilePropertiesHandler() {
        super(Operation.PROPERTIES, null, S.s(543), null, null);
        setAccelerator(SWT.MOD3 | 0xD);
    }
}


