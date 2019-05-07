/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.hover;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend.IHoverableWidget;
/*    */ import com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend.JebInformationControl;
/*    */ import com.pnfsoftware.jeb.util.base.OSType;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import org.eclipse.jface.text.DefaultInformationControl;
/*    */ import org.eclipse.jface.text.IInformationControl;
/*    */ import org.eclipse.jface.text.IInformationControlCreator;
/*    */ import org.eclipse.jface.text.IRegion;
/*    */ import org.eclipse.jface.text.ITextHover;
/*    */ import org.eclipse.jface.text.ITextHoverExtension;
/*    */ import org.eclipse.jface.text.ITextHoverExtension2;
/*    */ import org.eclipse.jface.text.ITextViewer;
/*    */ import org.eclipse.jface.text.Region;
/*    */ import org.eclipse.swt.SWTError;
/*    */ import org.eclipse.swt.graphics.Point;
/*    */ import org.eclipse.swt.widgets.Shell;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class InteractiveTextHover
        /*    */ implements ITextHover, ITextHoverExtension, ITextHoverExtension2
        /*    */ {
    /* 39 */   private static final ILogger logger = GlobalLog.getLogger(InteractiveTextHover.class);
    /*    */
    /*    */ IHoverableWidget iHoverable;
    /*    */ IHoverableProvider iHoverableProvider;

    /*    */
    /*    */
    public InteractiveTextHover(IHoverableProvider iHoverableProvider)
    /*    */ {
        /* 46 */
        this.iHoverableProvider = iHoverableProvider;
        /*    */
    }

    /*    */
    /*    */
    public InteractiveTextHover(IHoverableProvider iHoverableProvider, IHoverableWidget iHoverable) {
        /* 50 */
        this.iHoverableProvider = iHoverableProvider;
        /* 51 */
        this.iHoverable = iHoverable;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    @Deprecated
    /*    */ public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
    /*    */ {
        /* 63 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public IRegion getHoverRegion(ITextViewer textViewer, int offset)
    /*    */ {
        /* 68 */
        Point selection = textViewer.getSelectedRange();
        /* 69 */
        if ((selection.x <= offset) && (offset < selection.x + selection.y))
            /* 70 */ return new Region(selection.x, selection.y);
        /* 71 */
        return new Region(offset, 0);
        /*    */
    }

    /*    */
    /*    */
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
    /*    */ {
        /* 76 */
        return this.iHoverableProvider.getHoverInfo2(textViewer, hoverRegion);
        /*    */
    }

    /*    */
    /*    */
    public IInformationControlCreator getHoverControlCreator()
    /*    */ {
        /* 81 */
        new IInformationControlCreator()
                /*    */ {
            /*    */
            public IInformationControl createInformationControl(Shell parent) {
                /*    */
                try {
                    /* 85 */
                    JebInformationControl jic = new JebInformationControl(parent, false, InteractiveTextHover.this.iHoverable);
                    /* 86 */
                    jic.addLocationListener(InteractiveTextHover.this.iHoverableProvider);
                    /* 87 */
                    return jic;
                    /*    */
                    /*    */
                }
                /*    */ catch (SWTError e)
                    /*    */ {
                    /*    */
                    /* 93 */
                    String advice = OSType.determine().isWindows() ? "Is a WebKit browser installed (Chrome for example)?" : "Is libwebkitgtk-1.0-0 package installed?";
                    /*    */
                    /*    */
                    /* 96 */
                    InteractiveTextHover.logger.warn("HoverWidget can not be loaded due to %s. %s", new Object[]{e.getMessage(), advice});
                }
                /* 97 */
                return new DefaultInformationControl(parent, false);
                /*    */
            }
            /*    */
        };
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\hover\InteractiveTextHover.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */