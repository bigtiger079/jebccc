/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.codeobject;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
/*    */ import com.pnfsoftware.jeb.core.output.table.IVisualCell;
/*    */ import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
/*    */ import com.pnfsoftware.jeb.core.units.codeobject.ILoaderInformation;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class CodeLoaderCellItem
        /*    */ implements IVisualCell
        /*    */ {
    /*    */   private ICodeObjectUnit unit;
    /*    */   private String name;
    /*    */   private long offset;
    /*    */   private boolean relative;

    /*    */
    /*    */
    public CodeLoaderCellItem(ICodeObjectUnit unit, String name, String offset)
    /*    */ {
        /* 26 */
        this(unit, name, offset, true);
        /*    */
    }

    /*    */
    /*    */
    public CodeLoaderCellItem(ICodeObjectUnit unit, String name, String offset, boolean relative) {
        /* 30 */
        this.unit = unit;
        /* 31 */
        this.name = name;
        /* 32 */
        if (offset.endsWith("h")) {
            /* 33 */
            offset = offset.substring(0, offset.length() - 1);
            /*    */
        }
        /*    */
        try {
            /* 36 */
            this.offset = Long.parseLong(offset, 16);
            /*    */
        }
        /*    */ catch (NumberFormatException e) {
            /* 39 */
            this.offset = -1L;
            /*    */
        }
        /* 41 */
        this.relative = relative;
        /*    */
    }

    /*    */
    /*    */
    public String getLabel()
    /*    */ {
        /* 46 */
        return this.name;
        /*    */
    }

    /*    */
    /*    */
    public ItemClassIdentifiers getClassId()
    /*    */ {
        /* 51 */
        return ItemClassIdentifiers.DEFAULT;
        /*    */
    }

    /*    */
    /*    */
    public String getAddress() {
        /* 55 */
        if (this.offset < 0L) {
            /* 56 */
            return null;
            /*    */
        }
        /* 58 */
        long address = this.offset;
        /* 59 */
        if ((this.relative) && (this.unit != null)) {
            /* 60 */
            address += this.unit.getLoaderInformation().getImageBase();
            /*    */
        }
        /*    */
        /*    */
        /* 64 */
        return String.format("%Xh", new Object[]{Long.valueOf(address)});
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\codeobject\CodeLoaderCellItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */