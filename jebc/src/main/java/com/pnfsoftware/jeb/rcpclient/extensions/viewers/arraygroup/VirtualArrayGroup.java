package com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup;

import java.util.ArrayList;
import java.util.List;

public class VirtualArrayGroup implements IArrayGroup {
    int firstIndex;
    Object element;
    String label;

    public VirtualArrayGroup(int firstIndex, Object element) {
        this(firstIndex, element, null);
    }

    public VirtualArrayGroup(int firstIndex, Object element, String label) {
        this.firstIndex = firstIndex;
        this.element = element;
        this.label = label;
    }

    public int getFirstElementIndex() {
        return this.firstIndex;
    }

    public int getLastElementIndex() {
        return this.firstIndex;
    }

    public Object getFirstElement() {
        return this.element;
    }

    public Object getLastElement() {
        return this.element;
    }

    public boolean isSingle() {
        return true;
    }

    public List<Object> getChildren() {
        List<Object> list = new ArrayList<>();
        list.add(this.element);
        return list;
    }

    public int size() {
        return 1;
    }

    public String getGroupName() {
        return this.label;
    }
}


