package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public abstract interface IFilteredContentProvider extends IStructuredContentProvider {
    public abstract Object[] getRowElements(Object paramObject);
}


