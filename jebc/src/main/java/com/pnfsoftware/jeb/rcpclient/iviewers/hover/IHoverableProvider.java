package com.pnfsoftware.jeb.rcpclient.iviewers.hover;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

public interface IHoverableProvider {
    Object getHoverInfo2(ITextViewer paramITextViewer, IRegion paramIRegion);

    Object getHoverInfoOnLocationRequest(String paramString, boolean paramBoolean);
}


