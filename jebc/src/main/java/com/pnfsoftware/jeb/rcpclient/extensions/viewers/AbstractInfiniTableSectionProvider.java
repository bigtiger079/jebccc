
package com.pnfsoftware.jeb.rcpclient.extensions.viewers;


public abstract class AbstractInfiniTableSectionProvider
        implements IInfiniTableSectionProvider {

    public Object[] getElements(Object inputElement) {

        throw new RuntimeException();

    }


    public boolean isChecked(Object row) {

        return false;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\viewers\AbstractInfiniTableSectionProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */