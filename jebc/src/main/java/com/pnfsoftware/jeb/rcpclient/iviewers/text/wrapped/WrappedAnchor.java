/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.text.IAnchor;
/*    */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.DocumentManager;

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
/*    */ public class WrappedAnchor
        /*    */ {
    /*    */ DocumentManager docManager;
    /*    */ IAnchor anchor;

    /*    */
    /*    */
    public WrappedAnchor(DocumentManager docManager, IAnchor anchor)
    /*    */ {
        /* 23 */
        this.docManager = docManager;
        /* 24 */
        this.anchor = anchor;
        /*    */
    }

    /*    */
    /*    */
    public int getWrappedLineIndex() {
        /* 28 */
        return this.docManager.wrapLine(this.anchor.getLineIndex());
        /*    */
    }

    /*    */
    /*    */
    public int getUnwrappedLineIndex() {
        /* 32 */
        return this.anchor.getLineIndex();
        /*    */
    }

    /*    */
    /*    */
    public long getIdentifier() {
        /* 36 */
        return this.anchor.getIdentifier();
        /*    */
    }

    /*    */
    /*    */
    public IAnchor unwrap() {
        /* 40 */
        return this.anchor;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\wrapped\WrappedAnchor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */