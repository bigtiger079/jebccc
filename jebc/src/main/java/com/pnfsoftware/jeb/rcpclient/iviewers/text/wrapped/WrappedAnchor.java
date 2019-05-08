
package com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped;

import com.pnfsoftware.jeb.core.output.text.IAnchor;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.DocumentManager;

public class WrappedAnchor {
    DocumentManager docManager;
    IAnchor anchor;

    public WrappedAnchor(DocumentManager docManager, IAnchor anchor) {
        this.docManager = docManager;
        this.anchor = anchor;
    }

    public int getWrappedLineIndex() {
        return this.docManager.wrapLine(this.anchor.getLineIndex());
    }

    public int getUnwrappedLineIndex() {
        return this.anchor.getLineIndex();
    }

    public long getIdentifier() {
        return this.anchor.getIdentifier();
    }

    public IAnchor unwrap() {
        return this.anchor;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\wrapped\WrappedAnchor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */