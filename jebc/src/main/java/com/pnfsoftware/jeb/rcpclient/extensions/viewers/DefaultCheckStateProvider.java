
package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import org.eclipse.jface.viewers.ICheckStateProvider;

public class DefaultCheckStateProvider
        implements ICheckStateProvider {
    private IFilteredTableContentProvider provider;

    public DefaultCheckStateProvider(IFilteredTableContentProvider provider) {
        this.provider = provider;
    }

    public boolean isChecked(Object element) {
        return this.provider.isChecked(element);
    }

    public boolean isGrayed(Object element) {
        return false;
    }
}


