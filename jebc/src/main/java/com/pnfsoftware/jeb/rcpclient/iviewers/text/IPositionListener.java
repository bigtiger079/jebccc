package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.ICoordinates;

public interface IPositionListener {
    public abstract void positionChanged(ITextDocumentViewer paramITextDocumentViewer, ICoordinates paramICoordinates, int paramInt);

    public abstract void positionUnchangedAttemptBreakout(ITextDocumentViewer paramITextDocumentViewer, int paramInt);
}


