
package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;

public class AnnotationModelEx
        extends AnnotationModel {
    public boolean isConnected() {
        return this.fDocument != null;
    }

    public IDocument getConnectedDocument() {
        return this.fDocument;
    }

    public void disconnectSafe() {
        if (this.fDocument != null) {
            disconnect(this.fDocument);
        }
    }
}


