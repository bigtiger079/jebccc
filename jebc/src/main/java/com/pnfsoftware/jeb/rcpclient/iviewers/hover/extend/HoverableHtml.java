
package com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend;

public class HoverableHtml {
    public String html;
    public String text;
    public String anchor;

    public HoverableHtml(String html) {
        this(html, html, null);
    }

    public HoverableHtml(String html, String anchor) {
        this(html, html, anchor);
    }

    public HoverableHtml(String html, String text, String anchor) {
        this.html = html;
        this.text = text;
        this.anchor = anchor;
    }

    public String getHtml() {
        return this.html;
    }

    public String getAnchor() {
        return this.anchor;
    }

    public String toString() {
        return this.text;
    }
}


