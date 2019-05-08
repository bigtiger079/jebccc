package com.pnfsoftware.jeb.rcpclient.iviewers.hover;

import com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend.IHoverableWidget;
import com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend.JebInformationControl;
import com.pnfsoftware.jeb.util.base.OSType;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class InteractiveTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {
    private static final ILogger logger = GlobalLog.getLogger(InteractiveTextHover.class);
    IHoverableWidget iHoverable;
    IHoverableProvider iHoverableProvider;

    public InteractiveTextHover(IHoverableProvider iHoverableProvider) {
        this.iHoverableProvider = iHoverableProvider;
    }

    public InteractiveTextHover(IHoverableProvider iHoverableProvider, IHoverableWidget iHoverable) {
        this.iHoverableProvider = iHoverableProvider;
        this.iHoverable = iHoverable;
    }

    @Deprecated
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        return null;
    }

    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        Point selection = textViewer.getSelectedRange();
        if ((selection.x <= offset) && (offset < selection.x + selection.y))
            return new Region(selection.x, selection.y);
        return new Region(offset, 0);
    }

    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        return this.iHoverableProvider.getHoverInfo2(textViewer, hoverRegion);
    }

    public IInformationControlCreator getHoverControlCreator() {
        return new IInformationControlCreator() {
            public IInformationControl createInformationControl(Shell parent) {
                try {
                    JebInformationControl jic = new JebInformationControl(parent, false, InteractiveTextHover.this.iHoverable);
                    jic.addLocationListener(InteractiveTextHover.this.iHoverableProvider);
                    return jic;
                } catch (SWTError e) {
                    String advice = OSType.determine().isWindows() ? "Is a WebKit browser installed (Chrome for example)?" : "Is libwebkitgtk-1.0-0 package installed?";
                    InteractiveTextHover.logger.warn("HoverWidget can not be loaded due to %s. %s", new Object[]{e.getMessage(), advice});
                }
                return new DefaultInformationControl(parent, false);
            }
        };
    }
}


