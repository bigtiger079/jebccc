package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

public interface IInfiniTableSectionProvider extends IFilteredTableContentProvider {
    public abstract Object[] get(Object paramObject, long paramLong, int paramInt);
}


