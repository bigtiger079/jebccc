/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;
/*    */
/*    */

import org.eclipse.jface.text.IDocument;
/*    */ import org.eclipse.jface.text.source.AnnotationModel;

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
/*    */ public class AnnotationModelEx
        /*    */ extends AnnotationModel
        /*    */ {
    /*    */
    public boolean isConnected()
    /*    */ {
        /* 24 */
        return this.fDocument != null;
        /*    */
    }

    /*    */
    /*    */
    public IDocument getConnectedDocument() {
        /* 28 */
        return this.fDocument;
        /*    */
    }

    /*    */
    /*    */
    public void disconnectSafe() {
        /* 32 */
        if (this.fDocument != null) {
            /* 33 */
            disconnect(this.fDocument);
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\anno\AnnotationModelEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */