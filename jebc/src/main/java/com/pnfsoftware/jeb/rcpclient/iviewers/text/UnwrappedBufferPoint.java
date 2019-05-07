/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class UnwrappedBufferPoint
        /*    */ extends BufferPoint
        /*    */ {
    /*    */   public boolean eol;

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public UnwrappedBufferPoint(int columnOffset, int lineIndex, boolean eol)
    /*    */ {
        /* 23 */
        super(columnOffset, lineIndex);
        /* 24 */
        this.eol = eol;
        /*    */
    }

    /*    */
    /*    */
    public int hashCode()
    /*    */ {
        /* 29 */
        int prime = 31;
        /* 30 */
        int result = super.hashCode();
        /* 31 */
        result = 31 * result + (this.eol ? 1231 : 1237);
        /* 32 */
        return result;
        /*    */
    }

    /*    */
    /*    */
    public boolean equals(Object obj)
    /*    */ {
        /* 37 */
        if (this == obj)
            /* 38 */ return true;
        /* 39 */
        if (!super.equals(obj))
            /* 40 */ return false;
        /* 41 */
        if (getClass() != obj.getClass())
            /* 42 */ return false;
        /* 43 */
        UnwrappedBufferPoint other = (UnwrappedBufferPoint) obj;
        /* 44 */
        if (this.eol != other.eol)
            /* 45 */ return false;
        /* 46 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public String toString()
    /*    */ {
        /* 51 */
        return String.format("unwrapped_%s[eol=%b]", new Object[]{super.toString(), Boolean.valueOf(this.eol)});
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\UnwrappedBufferPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */