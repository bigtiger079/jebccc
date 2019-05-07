/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class BufferPoint
        /*    */ {
    /*    */   public int lineIndex;
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */   public int columnOffset;

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public BufferPoint(int columnOffset, int lineIndex)
    /*    */ {
        /* 21 */
        this.lineIndex = lineIndex;
        /* 22 */
        this.columnOffset = columnOffset;
        /*    */
    }

    /*    */
    /*    */
    public int hashCode()
    /*    */ {
        /* 27 */
        int prime = 31;
        /* 28 */
        int result = 1;
        /* 29 */
        result = 31 * result + this.columnOffset;
        /* 30 */
        result = 31 * result + this.lineIndex;
        /* 31 */
        return result;
        /*    */
    }

    /*    */
    /*    */
    public boolean equals(Object obj)
    /*    */ {
        /* 36 */
        if (this == obj)
            /* 37 */ return true;
        /* 38 */
        if (obj == null)
            /* 39 */ return false;
        /* 40 */
        if (getClass() != obj.getClass())
            /* 41 */ return false;
        /* 42 */
        BufferPoint other = (BufferPoint) obj;
        /* 43 */
        if (this.columnOffset != other.columnOffset)
            /* 44 */ return false;
        /* 45 */
        if (this.lineIndex != other.lineIndex)
            /* 46 */ return false;
        /* 47 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public String toString()
    /*    */ {
        /* 52 */
        return String.format("(%d,%d)", new Object[]{Integer.valueOf(this.lineIndex), Integer.valueOf(this.columnOffset)});
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\BufferPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */