package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.ICoordinates;

public abstract interface IPositionListener {
    public abstract void positionChanged(ITextDocumentViewer paramITextDocumentViewer, ICoordinates paramICoordinates, int paramInt);

    public abstract void positionUnchangedAttemptBreakout(ITextDocumentViewer paramITextDocumentViewer, int paramInt);
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\IPositionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */