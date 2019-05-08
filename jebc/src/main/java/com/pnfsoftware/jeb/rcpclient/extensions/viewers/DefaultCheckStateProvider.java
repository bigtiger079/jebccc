
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\viewers\DefaultCheckStateProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */