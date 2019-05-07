/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.search.IFindTextResult;

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
/*    */ public class InteractiveTextFindResult
        /*    */ implements IFindTextResult
        /*    */ {
    /* 19 */   public static InteractiveTextFindResult EOS = new InteractiveTextFindResult(-1);
    /*    */   private ICoordinates begin;
    /*    */   private ICoordinates end;
    /*    */   private int flag;

    /*    */
    /*    */
    public InteractiveTextFindResult(int flag)
    /*    */ {
        /* 26 */
        this.flag = flag;
        /*    */
    }

    /*    */
    /*    */
    public InteractiveTextFindResult(ICoordinates begin, ICoordinates end, boolean wrappedAround) {
        /* 30 */
        this.begin = begin;
        /* 31 */
        this.end = end;
        /* 32 */
        this.flag = (wrappedAround ? 1 : 0);
        /*    */
    }

    /*    */
    /*    */
    public boolean isEndOfSearch()
    /*    */ {
        /* 37 */
        return this.flag == -1;
        /*    */
    }

    /*    */
    /*    */
    public boolean isWrappedAround()
    /*    */ {
        /* 42 */
        return this.flag == 1;
        /*    */
    }

    /*    */
    /*    */
    public ICoordinates getBegin() {
        /* 46 */
        return this.begin;
        /*    */
    }

    /*    */
    /*    */
    public ICoordinates getEnd() {
        /* 50 */
        return this.end;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\InteractiveTextFindResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */