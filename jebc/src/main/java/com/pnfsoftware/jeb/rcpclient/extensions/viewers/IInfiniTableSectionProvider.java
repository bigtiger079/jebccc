package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

public interface IInfiniTableSectionProvider extends IFilteredTableContentProvider {
    Object[] get(Object paramObject, long paramLong, int paramInt);
}


