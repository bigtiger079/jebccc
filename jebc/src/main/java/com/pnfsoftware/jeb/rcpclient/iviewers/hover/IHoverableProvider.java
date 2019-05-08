package com.pnfsoftware.jeb.rcpclient.iviewers.hover;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

public abstract interface IHoverableProvider {
    public abstract Object getHoverInfo2(ITextViewer paramITextViewer, IRegion paramIRegion);

    public abstract Object getHoverInfoOnLocationRequest(String paramString, boolean paramBoolean);
}


