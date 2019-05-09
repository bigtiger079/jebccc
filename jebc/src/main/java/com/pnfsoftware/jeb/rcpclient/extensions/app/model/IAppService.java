package com.pnfsoftware.jeb.rcpclient.extensions.app.model;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

public interface IAppService {
    IMDock createDock();

    IMDock createDock(boolean paramBoolean, Rectangle paramRectangle);

    IMPart createPart(IMFolder paramIMFolder, IMPartManager paramIMPartManager);

    void hidePart(IMPart paramIMPart);

    void unhidePart(IMPart paramIMPart);

    void clearPart(IMPart paramIMPart);

    boolean isPartVisible(IMPart paramIMPart);

    IMPart getActivePart();

    Collection<IMPart> getParts();

    void activate(IMPart paramIMPart);

    void activate(IMPart paramIMPart, boolean paramBoolean);

    <T extends IMElement> List<T> findElements(IMElement paramIMElement, String paramString, Class<T> paramClass, Collection<String> paramCollection, int paramInt);
}
