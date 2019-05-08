package com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup;

public class ArrayLogicalGroup extends ArrayGroup {
    private String groupName;
    boolean packaged = false;

    public ArrayLogicalGroup(int firstIndex, String groupName) {
        super(firstIndex);
        this.groupName = groupName;
    }

    public ArrayLogicalGroup(int firstIndex, String groupName, boolean packaged) {
        super(firstIndex);
        this.groupName = groupName;
        this.packaged = packaged;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int size() {
        if (this.packaged) {
            int size = 0;
            for (Object child : this.children) {
                if ((child instanceof ArrayLogicalGroup)) {
                    size += ((ArrayLogicalGroup) child).size();
                } else {
                    size++;
                }
            }
            return size;
        }
        return super.size();
    }

    public boolean isPackaged() {
        return this.packaged;
    }

    public boolean isSingle() {
        return false;
    }
}


