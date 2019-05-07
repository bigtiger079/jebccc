package com.pnfsoftware.jeb.rcpclient.extensions.ui;

import com.pnfsoftware.jeb.util.base.IProgressCallback;

public abstract interface ITaskProgressMonitor
        extends IProgressCallback {
    public abstract void setTaskName(String paramString);

    public abstract void setSubtaskName(String paramString);

    public abstract void progress(long paramLong1, long paramLong2);

    public abstract void setCanceled(boolean paramBoolean);

    public abstract boolean isCanceled();

    public abstract void done();
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extension\\ui\ITaskProgressMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */