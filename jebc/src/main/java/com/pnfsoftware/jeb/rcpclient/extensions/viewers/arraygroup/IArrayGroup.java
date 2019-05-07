package com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup;

import java.util.List;

public abstract interface IArrayGroup {
    public abstract int getFirstElementIndex();

    public abstract int getLastElementIndex();

    public abstract Object getFirstElement();

    public abstract Object getLastElement();

    public abstract boolean isSingle();

    public abstract List<Object> getChildren();

    public abstract int size();

    public abstract String getGroupName();
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\viewers\arraygroup\IArrayGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */