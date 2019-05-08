
package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.ITextItem;

public class ItemEvent {
    public static final int CaretOut = 0;
    public static final int CaretIn = 1;
    public static final int DblClick = 2;
    public ITextItem item;
    public int type;

    public ItemEvent(ITextItem item, int type) {
        this.item = item;
        this.type = type;
    }
}


