/*    */
package com.pnfsoftware.jeb.rcpclient.util.regex;
/*    */
/*    */

import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;

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
/*    */ public class SimplePattern
        /*    */ implements IPattern
        /*    */ {
    /*    */   private Pattern pattern;

    /*    */
    /*    */
    public SimplePattern(String filterString)
    /*    */ {
        /* 22 */
        this.pattern = Pattern.compile(filterString, 98);
        /*    */
    }

    /*    */
    /*    */
    public boolean match(Object element, Object[] list)
    /*    */ {
        /* 27 */
        if (list != null) {
            /* 28 */
            for (Object o : list) {
                /* 29 */
                if ((o != null) && (this.pattern.matcher(o.toString()).find())) {
                    /* 30 */
                    return true;
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /* 34 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    public IPattern createInstance(String filterString)
    /*    */ {
        /* 39 */
        return new SimplePattern(filterString);
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\regex\SimplePattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */