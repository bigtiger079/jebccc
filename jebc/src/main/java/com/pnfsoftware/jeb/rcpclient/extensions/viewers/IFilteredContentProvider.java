package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public interface IFilteredContentProvider extends IStructuredContentProvider {
    public abstract Object[] getRowElements(Object paramObject);
}


