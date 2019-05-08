
package com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup;

import java.util.ArrayList;
import java.util.List;

public class ArrayGroup
        implements IArrayGroup {
    int firstIndex;
    List<Object> children = new ArrayList();

    public ArrayGroup(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public void add(Object child) {
        this.children.add(child);
    }

    public int getLastElementIndex() {
        Object lastElement = this.children.get(this.children.size() - 1);
        if ((lastElement instanceof ArrayGroup)) {
            return ((ArrayGroup) lastElement).getLastElementIndex();
        }
        return getFirstElementIndex() + this.children.size() - 1;
    }

    public int getFirstElementIndex() {
        return this.firstIndex;
    }

    public Object getFirstElement() {
        Object first = this.children.get(0);
        if ((first instanceof IArrayGroup)) {
            return ((IArrayGroup) first).getFirstElement();
        }
        return first;
    }

    public Object getLastElement() {
        Object last = this.children.get(this.children.size() - 1);
        if ((last instanceof IArrayGroup)) {
            return ((IArrayGroup) last).getLastElement();
        }
        return last;
    }

    public boolean isSingle() {
        return this.children.size() == 1;
    }

    public List<Object> getChildren() {
        return this.children;
    }

    public int size() {
        return this.children.size();
    }

    public String getGroupName() {
        return null;
    }
}


