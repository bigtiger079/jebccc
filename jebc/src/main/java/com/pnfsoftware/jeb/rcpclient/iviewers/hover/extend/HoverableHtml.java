/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class HoverableHtml
        /*    */ {
    /*    */   public String html;
    /*    */
    /*    */
    /*    */
    /*    */   public String text;
    /*    */
    /*    */
    /*    */
    /*    */   public String anchor;

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public HoverableHtml(String html)
    /*    */ {
        /* 24 */
        this(html, html, null);
        /*    */
    }

    /*    */
    /*    */
    public HoverableHtml(String html, String anchor) {
        /* 28 */
        this(html, html, anchor);
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public HoverableHtml(String html, String text, String anchor)
    /*    */ {
        /* 40 */
        this.html = html;
        /* 41 */
        this.text = text;
        /* 42 */
        this.anchor = anchor;
        /*    */
    }

    /*    */
    /*    */
    public String getHtml() {
        /* 46 */
        return this.html;
        /*    */
    }

    /*    */
    /*    */
    public String getAnchor() {
        /* 50 */
        return this.anchor;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    public String toString()
    /*    */ {
        /* 56 */
        return this.text;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\hover\extend\HoverableHtml.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */