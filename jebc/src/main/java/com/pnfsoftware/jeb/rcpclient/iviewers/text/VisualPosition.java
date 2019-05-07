
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\VisualPosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */