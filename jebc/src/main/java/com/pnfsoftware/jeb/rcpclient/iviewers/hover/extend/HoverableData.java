
package com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend;


public class HoverableData {
    private String text;


    private Object data;


    public HoverableData(String text) {

        this.text = text;

    }


    public HoverableData(String text, Object data) {

        this.text = text;

        this.data = data;

    }


    public String getText() {

        return this.text;

    }


    public Object getData() {

        return this.data;

    }


    public String toString() {

        return this.text;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\hover\extend\HoverableData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */