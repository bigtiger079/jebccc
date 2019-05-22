package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

public interface IDndProvider {
    boolean canDrag(Object paramObject);

    boolean canDrop(String paramString, Object paramObject, int paramInt);

    boolean performDrop(String paramString, Object paramObject, int paramInt);

    Object getSelectedElements();

    String getDragData();

    boolean shouldExpand(String paramString, Object paramObject);
}


