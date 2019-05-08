package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;

class ItemEntry {
    int offset;
    int size;
    String name;
    INativeType type;
    String comment;
    boolean slack;

    ItemEntry() {
    }

    ItemEntry(int offset, int size, String name, INativeType type, String comment, boolean slack) {
        this.offset = offset;
        this.size = size;
        this.name = name;
        this.type = type;
        this.comment = comment;
        this.slack = slack;
    }
}


