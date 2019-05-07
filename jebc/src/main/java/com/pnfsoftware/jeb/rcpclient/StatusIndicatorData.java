/*    */
package com.pnfsoftware.jeb.rcpclient;
/*    */
/*    */

import java.util.Arrays;
/*    */ import java.util.List;

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
/*    */ public class StatusIndicatorData
        /*    */ {
    /*    */   private List<String> elts;

    /*    */
    /*    */
    public StatusIndicatorData(String... elements)
    /*    */ {
        /* 22 */
        this.elts = Arrays.asList(elements);
        /*    */
    }

    /*    */
    /*    */
    public List<String> getElements() {
        /* 26 */
        return this.elts;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\StatusIndicatorData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */