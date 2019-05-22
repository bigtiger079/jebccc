package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

public interface IFilteredTableContentProvider extends IFilteredContentProvider {
    public abstract boolean isChecked(Object paramObject);
}


