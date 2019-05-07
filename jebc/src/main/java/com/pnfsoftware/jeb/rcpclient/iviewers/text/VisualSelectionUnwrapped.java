/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.text.ICoordinates;

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
/*    */ public class VisualSelectionUnwrapped
        /*    */ {
    /*    */   public ICoordinates docCoord;
    /*    */   public boolean eol;
    /* 20 */   public int selectionLength = 0;

    /*    */
    /*    */
    public VisualSelectionUnwrapped(ICoordinates docCoord, boolean eol, int selectionLength)
    /*    */ {
        /* 24 */
        this.docCoord = docCoord;
        /* 25 */
        this.eol = eol;
        /* 26 */
        this.selectionLength = selectionLength;
        /*    */
    }

    /*    */
    /*    */
    public VisualSelectionUnwrapped(ICoordinates docCoord, boolean eol)
    /*    */ {
        /* 31 */
        this.docCoord = docCoord;
        /* 32 */
        this.eol = eol;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\VisualSelectionUnwrapped.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */