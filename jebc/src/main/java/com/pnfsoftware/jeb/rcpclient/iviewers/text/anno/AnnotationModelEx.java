
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\anno\AnnotationModelEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */