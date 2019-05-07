/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class HoverableData
        /*    */ {
    /*    */   private String text;
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */   private Object data;

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public HoverableData(String text)
    /*    */ {
        /* 27 */
        this.text = text;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public HoverableData(String text, Object data)
    /*    */ {
        /* 35 */
        this.text = text;
        /* 36 */
        this.data = data;
        /*    */
    }

    /*    */
    /*    */
    public String getText() {
        /* 40 */
        return this.text;
        /*    */
    }

    /*    */
    /*    */
    public Object getData() {
        /* 44 */
        return this.data;
        /*    */
    }

    /*    */
    /*    */
    public String toString()
    /*    */ {
        /* 49 */
        return this.text;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\hover\extend\HoverableData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */