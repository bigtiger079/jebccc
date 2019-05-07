/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*    */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*    */ import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
/*    */ import com.pnfsoftware.jeb.core.output.text.impl.StaticTextDocument;
/*    */ import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*    */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
/*    */ import com.pnfsoftware.jeb.util.encoding.Conversion;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class StaticCodeTextDocument
        /*    */ extends StaticTextDocument
        /*    */ {
    /*    */   private BasicBlock<IInstruction> b;
    /*    */   private String methodName;

    /*    */
    /*    */
    public StaticCodeTextDocument(IUnit unit, BasicBlock<IInstruction> b, String text)
    /*    */ {
        /* 29 */
        super(text);
        /* 30 */
        this.b = b;
        /* 31 */
        if ((unit instanceof ISourceUnit)) {
            /* 32 */
            this.methodName = (((ISourceUnit) unit).getFullyQualifiedName() + ":");
            /*    */
        }
        /*    */
        else {
            /* 35 */
            this.methodName = "unk:";
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    public String coordinatesToAddress(ICoordinates coordinates, AddressConversionPrecision precision)
    /*    */ {
        /* 41 */
        return this.methodName + Long.toHexString(this.b.getAddressOfInstruction((int) coordinates.getAnchorId())) + "h";
        /*    */
    }

    /*    */
    /*    */
    public ICoordinates addressToCoordinates(String address)
    /*    */ {
        /* 46 */
        if (address.length() < this.methodName.length()) {
            /* 47 */
            return super.addressToCoordinates(address);
            /*    */
        }
        /* 49 */
        address = address.substring(this.methodName.length());
        /* 50 */
        long addr = Conversion.toLong(address, -1L);
        /* 51 */
        if (addr < 0L) {
            /* 52 */
            return super.addressToCoordinates(address);
            /*    */
        }
        /* 54 */
        return new Coordinates(this.b.getIndexOfInstruction(addr));
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\StaticCodeTextDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */