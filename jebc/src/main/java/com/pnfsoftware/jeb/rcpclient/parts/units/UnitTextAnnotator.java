package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationFactory;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationService;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.TextAnnotation;
import com.pnfsoftware.jeb.rcpclient.parts.UIState;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;

import java.util.Map;

public class UnitTextAnnotator implements IEventListener {
    UIState uiState;
    ITextDocumentViewer viewer;

    public UnitTextAnnotator(UIState uiState, ITextDocumentViewer viewer) {
        this.uiState = uiState;
        this.viewer = viewer;
        uiState.addListener(this);
    }

    public void dispose() {
        this.uiState.removeListener(this);
    }

    public void onEvent(IEvent e) {
        this.viewer.unregisterAnnotations();
        for (String address : this.uiState.getBreakpoints().keySet()) {
            ICoordinates coord = this.viewer.getDocument().addressToCoordinates(address);
            if (coord != null) {
                String annoType = null;
                if (this.uiState.isBreakpointEnabled(address)) {
                    annoType = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgBP";
                } else {
                    annoType = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgBPDisabled";
                }
                TextAnnotation anno = AnnotationService.getInstance().getFactory(annoType).create(coord);
                this.viewer.registerAnnotation(anno);
            }
        }
        String pcAddress = this.uiState.getProgramCounter();
        if (pcAddress != null) {
            ICoordinates coord = this.viewer.getDocument().addressToCoordinates(pcAddress);
            if (coord != null) {
                TextAnnotation anno = AnnotationService.getInstance().getFactory("com.pnfsoftware.jeb.rcpclient.textAnno.dbgPC").create(coord);
                this.viewer.registerAnnotation(anno);
            }
        }
    }
}


