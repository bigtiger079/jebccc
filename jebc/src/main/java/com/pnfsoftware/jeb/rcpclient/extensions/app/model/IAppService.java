package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

public abstract interface IAppService {
    public abstract IMDock createDock();

    public abstract IMDock createDock(boolean paramBoolean, Rectangle paramRectangle);

    public abstract IMPart createPart(IMFolder paramIMFolder, IMPartManager paramIMPartManager);

    public abstract void hidePart(IMPart paramIMPart);

    public abstract void unhidePart(IMPart paramIMPart);

    public abstract void clearPart(IMPart paramIMPart);

    public abstract boolean isPartVisible(IMPart paramIMPart);

    public abstract IMPart getActivePart();

    public abstract Collection<IMPart> getParts();

    public abstract void activate(IMPart paramIMPart);

    public abstract void activate(IMPart paramIMPart, boolean paramBoolean);

    public abstract <T extends IMElement> List<T> findElements(IMElement paramIMElement, String paramString, Class<T> paramClass, Collection<String> paramCollection, int paramInt);
}
