package com.pnfsoftware.jeb.rcpclient.parts.units.code.impl;

import com.pnfsoftware.jeb.core.units.code.ICodeItem;
import com.pnfsoftware.jeb.core.units.code.ICodePackage;

import java.util.List;

public class SimpleCodePackage implements ICodePackage {
    private String name;
    private String address;

    public SimpleCodePackage(String name) {
        this(name, null);
    }

    public SimpleCodePackage(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public long getItemId() {
        return 0L;
    }

    public int getIndex() {
        return 0;
    }

    public String getAddress() {
        return this.address;
    }

    public String getName(boolean effective) {
        return this.name;
    }

    public String getSignature(boolean effective) {
        return null;
    }

    public boolean isInternal() {
        return true;
    }

    public boolean isArtificial() {
        return true;
    }

    public int getGenericFlags() {
        return 0;
    }

    public boolean isRootPackage() {
        return false;
    }

    public ICodePackage getParentPackage() {
        return null;
    }

    public List<? extends ICodePackage> getChildrenPackages() {
        return null;
    }

    public List<? extends ICodeItem> getChildren() {
        return null;
    }
}


