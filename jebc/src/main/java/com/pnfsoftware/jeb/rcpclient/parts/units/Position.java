package com.pnfsoftware.jeb.rcpclient.parts.units;

public class Position {
    String address;
    Object extra;

    public Position(String address, Object extra) {
        this.address = address;
        this.extra = extra;
    }

    public String getAddress() {
        return this.address;
    }

    public Object getExtra() {
        return this.extra;
    }

    public String toString() {
        return String.format("%s(%s)", this.address, this.extra);
    }
}


