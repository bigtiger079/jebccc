
package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.ICoordinates;

public class VisualPosition {
    public ICoordinates docCoord;
    public BufferPoint viewportCoord;

    public VisualPosition(ICoordinates docCoord, BufferPoint viewportCoord) {
        this.docCoord = docCoord;
        this.viewportCoord = viewportCoord;
    }
}


