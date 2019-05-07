package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

public abstract interface IDndProvider {
    public abstract boolean canDrag(Object paramObject);

    public abstract boolean canDrop(String paramString, Object paramObject, int paramInt);

    public abstract boolean performDrop(String paramString, Object paramObject, int paramInt);

    public abstract Object getSelectedElements();

    public abstract String getDragData();

    public abstract boolean shouldExpand(String paramString, Object paramObject);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\viewers\IDndProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */