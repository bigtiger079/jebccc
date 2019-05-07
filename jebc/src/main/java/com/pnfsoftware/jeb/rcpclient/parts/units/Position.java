/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class Position
        /*    */ {
    /*    */ String address;
    /*    */
    /*    */
    /*    */
    /*    */ Object extra;

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public Position(String address, Object extra)
    /*    */ {
        /* 20 */
        this.address = address;
        /* 21 */
        this.extra = extra;
        /*    */
    }

    /*    */
    /*    */
    public String getAddress() {
        /* 25 */
        return this.address;
        /*    */
    }

    /*    */
    /*    */
    public Object getExtra() {
        /* 29 */
        return this.extra;
        /*    */
    }

    /*    */
    /*    */
    public String toString()
    /*    */ {
        /* 34 */
        return String.format("%s(%s)", new Object[]{this.address, this.extra});
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\Position.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */