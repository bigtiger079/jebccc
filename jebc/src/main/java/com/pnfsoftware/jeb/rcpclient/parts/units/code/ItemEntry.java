/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ class ItemEntry
        /*    */ {
    /*    */ int offset;
    /*    */ int size;
    /*    */ String name;
    /*    */ INativeType type;
    /*    */ String comment;
    /*    */ boolean slack;

    /*    */
    /*    */   ItemEntry() {
    }

    /*    */
    /*    */   ItemEntry(int offset, int size, String name, INativeType type, String comment, boolean slack)
    /*    */ {
        /* 29 */
        this.offset = offset;
        /* 30 */
        this.size = size;
        /* 31 */
        this.name = name;
        /* 32 */
        this.type = type;
        /* 33 */
        this.comment = comment;
        /* 34 */
        this.slack = slack;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\ItemEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */