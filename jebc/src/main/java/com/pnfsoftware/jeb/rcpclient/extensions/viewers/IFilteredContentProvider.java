package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public abstract interface IFilteredContentProvider
        extends IStructuredContentProvider {
    public abstract Object[] getRowElements(Object paramObject);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\viewers\IFilteredContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */