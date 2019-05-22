package com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup;

import java.util.List;

public interface IArrayGroup {
    int getFirstElementIndex();

    int getLastElementIndex();

    Object getFirstElement();

    Object getLastElement();

    boolean isSingle();

    List<Object> getChildren();

    int size();

    String getGroupName();
}


