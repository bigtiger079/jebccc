package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

public abstract class AbstractInfiniTableSectionProvider implements IInfiniTableSectionProvider {
    public Object[] getElements(Object inputElement) {
        throw new RuntimeException();
    }

    public boolean isChecked(Object row) {
        return false;
    }
}


