package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.ICoordinates;

public interface IPositionListener {
    void positionChanged(ITextDocumentViewer paramITextDocumentViewer, ICoordinates paramICoordinates, int paramInt);

    void positionUnchangedAttemptBreakout(ITextDocumentViewer paramITextDocumentViewer, int paramInt);
}


