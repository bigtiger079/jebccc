/*    */
package com.pnfsoftware.jeb.rcpclient;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.units.Position;
/*    */ import java.lang.ref.WeakReference;

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
/*    */
/*    */ public class GlobalPosition
        /*    */ {
    /*    */   private WeakReference<IUnit> unitRef;
    /*    */   private int partId;
    /*    */   private long fragmentId;
    /*    */   private Position position;

    /*    */
    /*    */
    public GlobalPosition(IUnit unit, int partId, long fragmentId, Position position)
    /*    */ {
        /* 28 */
        this.unitRef = new WeakReference(unit);
        /* 29 */
        this.partId = partId;
        /* 30 */
        this.fragmentId = fragmentId;
        /* 31 */
        this.position = position;
        /*    */
    }

    /*    */
    /*    */
    public IUnit getUnit() {
        /* 35 */
        return (IUnit) this.unitRef.get();
        /*    */
    }

    /*    */
    /*    */
    public int getPartId() {
        /* 39 */
        return this.partId;
        /*    */
    }

    /*    */
    /*    */
    public long getFragmentId() {
        /* 43 */
        return this.fragmentId;
        /*    */
    }

    /*    */
    /*    */
    public Position getPosition() {
        /* 47 */
        return this.position;
        /*    */
    }

    /*    */
    /*    */
    public String toString()
    /*    */ {
        /* 52 */
        return String.format("%s @ %s", new Object[]{this.unitRef.get(), this.position});
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\GlobalPosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */