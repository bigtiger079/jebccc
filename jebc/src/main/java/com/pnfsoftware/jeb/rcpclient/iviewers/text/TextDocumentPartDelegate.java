/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.text.IAnchor;
/*    */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*    */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;

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
/*    */ public class TextDocumentPartDelegate
        /*    */ implements ITextDocumentPart
        /*    */ {
    /*    */   private ITextDocumentPart part;
    /*    */   private List<ILine> lines;

    /*    */
    /*    */
    public TextDocumentPartDelegate(ITextDocumentPart part, int maxCharsPerLine, int charsEndLine)
    /*    */ {
        /* 27 */
        this.part = part;
        /* 28 */
        this.lines = new ArrayList(part.getLines());
        /* 29 */
        for (int i = 0; i < this.lines.size(); i++) {
            /* 30 */
            ILine line = (ILine) this.lines.get(i);
            /* 31 */
            if (line.getText().length() > maxCharsPerLine)
                /*    */ {
                /* 33 */
                this.lines.remove(i);
                /* 34 */
                this.lines.add(i, new LineDelegate(line, maxCharsPerLine, charsEndLine));
                /*    */
            }
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    public List<ILine> getLines()
    /*    */ {
        /* 41 */
        return this.lines;
        /*    */
    }

    /*    */
    /*    */
    public List<? extends IAnchor> getAnchors()
    /*    */ {
        /* 46 */
        return this.part.getAnchors();
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\TextDocumentPartDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */