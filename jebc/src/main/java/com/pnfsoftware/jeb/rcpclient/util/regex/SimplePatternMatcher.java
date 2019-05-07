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
/*    */ public class SimplePatternMatcher
        /*    */ implements IPatternMatcher
        /*    */ {
    /*    */   private IValueProvider valueProvider;

    /*    */
    /*    */
    public SimplePatternMatcher(IValueProvider valueProvider)
    /*    */ {
        /* 22 */
        this.valueProvider = valueProvider;
        /*    */
    }

    /*    */
    /*    */
    public IValueProvider getValueProvider()
    /*    */ {
        /* 27 */
        return this.valueProvider;
        /*    */
    }

    /*    */
    /*    */
    public boolean match(Pattern pattern, Object element)
    /*    */ {
        /* 32 */
        String value = getValueProvider().getString(element);
        /* 33 */
        if (value == null) {
            /* 34 */
            return false;
            /*    */
        }
        /* 36 */
        return pattern.matcher(value).find();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\regex\SimplePatternMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */