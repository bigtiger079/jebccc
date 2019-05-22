package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

public interface IDndProvider {
    public abstract boolean canDrag(Object paramObject);

    public abstract boolean canDrop(String paramString, Object paramObject, int paramInt);

    public abstract boolean performDrop(String paramString, Object paramObject, int paramInt);

    public abstract Object getSelectedElements();

    public abstract String getDragData();

    public abstract boolean shouldExpand(String paramString, Object paramObject);
}


