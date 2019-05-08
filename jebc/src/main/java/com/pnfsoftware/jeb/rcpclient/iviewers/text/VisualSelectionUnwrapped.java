
package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.ICoordinates;

public class VisualSelectionUnwrapped {
    public ICoordinates docCoord;
    public boolean eol;
    public int selectionLength = 0;

    public VisualSelectionUnwrapped(ICoordinates docCoord, boolean eol, int selectionLength) {
        this.docCoord = docCoord;
        this.eol = eol;
        this.selectionLength = selectionLength;
    }

    public VisualSelectionUnwrapped(ICoordinates docCoord, boolean eol) {
        this.docCoord = docCoord;
        this.eol = eol;
    }
}


