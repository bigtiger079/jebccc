package com.pnfsoftware.jeb.rcpclient.extensions.ui;

import com.pnfsoftware.jeb.util.base.IProgressCallback;

public interface ITaskProgressMonitor extends IProgressCallback {
    void setTaskName(String paramString);

    void setSubtaskName(String paramString);

    void progress(long paramLong1, long paramLong2);

    void setCanceled(boolean paramBoolean);

    boolean isCanceled();

    void done();
}


