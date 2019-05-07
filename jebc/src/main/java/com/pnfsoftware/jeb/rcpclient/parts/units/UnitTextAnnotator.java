/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*    */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*    */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
/*    */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationFactory;
/*    */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationService;
/*    */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.TextAnnotation;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.UIState;
/*    */ import com.pnfsoftware.jeb.util.events.IEvent;
/*    */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*    */ import java.util.Map;

/*    */
/*    */
/*    */
/*    */
/*    */ public class UnitTextAnnotator
        /*    */ implements IEventListener
        /*    */ {
    /*    */ UIState uiState;
    /*    */ ITextDocumentViewer viewer;

    /*    */
    /*    */
    public UnitTextAnnotator(UIState uiState, ITextDocumentViewer viewer)
    /*    */ {
        /* 25 */
        this.uiState = uiState;
        /* 26 */
        this.viewer = viewer;
        /*    */
        /* 28 */
        uiState.addListener(this);
        /*    */
    }

    /*    */
    /*    */
    public void dispose() {
        /* 32 */
        this.uiState.removeListener(this);
        /*    */
    }

    /*    */
    /*    */
    public void onEvent(IEvent e)
    /*    */ {
        /* 37 */
        this.viewer.unregisterAnnotations();
        /*    */
        /* 39 */
        for (String address : this.uiState.getBreakpoints().keySet()) {
            /* 40 */
            ICoordinates coord = this.viewer.getDocument().addressToCoordinates(address);
            /* 41 */
            if (coord != null) {
                /* 42 */
                String annoType = null;
                /* 43 */
                if (this.uiState.isBreakpointEnabled(address)) {
                    /* 44 */
                    annoType = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgBP";
                    /*    */
                }
                /*    */
                else {
                    /* 47 */
                    annoType = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgBPDisabled";
                    /*    */
                }
                /* 49 */
                TextAnnotation anno = AnnotationService.getInstance().getFactory(annoType).create(coord);
                /* 50 */
                this.viewer.registerAnnotation(anno);
                /*    */
            }
            /*    */
        }
        /*    */
        /* 54 */
        String pcAddress = this.uiState.getProgramCounter();
        /* 55 */
        if (pcAddress != null) {
            /* 56 */
            ICoordinates coord = this.viewer.getDocument().addressToCoordinates(pcAddress);
            /* 57 */
            if (coord != null)
                /*    */ {
                /* 59 */
                TextAnnotation anno = AnnotationService.getInstance().getFactory("com.pnfsoftware.jeb.rcpclient.textAnno.dbgPC").create(coord);
                /* 60 */
                this.viewer.registerAnnotation(anno);
                /*    */
            }
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\UnitTextAnnotator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */