package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

public abstract interface IFilteredTableContentProvider
        extends IFilteredContentProvider {
    public abstract boolean isChecked(Object paramObject);
}


