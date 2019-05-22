package com.pnfsoftware.jeb.rcpclient.extensions.app;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;

public class CTabFolderEventLogger implements CTabFolder2Listener {
    private static final ILogger logger = GlobalLog.getLogger(CTabFolderEventLogger.class);

    public void close(CTabFolderEvent event) {
        logger.info("Close: %s", event);
    }

    public void minimize(CTabFolderEvent event) {
        logger.i("Min: %s", event);
    }

    public void maximize(CTabFolderEvent event) {
        logger.i("Max: %s", event);
    }

    public void restore(CTabFolderEvent event) {
        logger.i("Restore: %s", event);
    }

    public void showList(CTabFolderEvent event) {
        logger.i("List: %s", event);
    }
}


