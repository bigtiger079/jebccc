/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.text.IActionableTextItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextMark;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.TextItem;
/*     */ import com.pnfsoftware.jeb.util.base.CharSequenceList;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class LineDelegate
        /*     */ implements ILine
        /*     */ {
    /*  34 */   private static final ILogger logger = GlobalLog.getLogger(LineDelegate.class);
    /*     */
    /*     */   private ILine line;
    /*     */   private CharSequence text;
    /*  38 */ List<ITextItem> items = new ArrayList();

    /*     */
    /*     */
    public LineDelegate(ILine line, int maxCharsPerLine, int charsEndLine) {
        /*  41 */
        this.line = line;
        /*  42 */
        this.text = CharSequenceList.getLine(line.getText(), maxCharsPerLine, charsEndLine);
        /*     */
        /*  44 */
        int lineLength = line.getText().length();
        /*  45 */
        for (ITextItem item : line.getItems()) {
            /*  46 */
            int length = item.getLength();
            /*  47 */
            int offset = item.getOffset();
            /*     */
            /*  49 */
            int endOfFirstSection = maxCharsPerLine - charsEndLine - 5;
            /*     */
            /*  51 */
            int startEndSection = lineLength - charsEndLine;
            /*  52 */
            int itemEndOffset = item.getOffset() + item.getLength();
            /*  53 */
            if (item.getOffset() < endOfFirstSection) {
                /*  54 */
                if (itemEndOffset >= endOfFirstSection)
                    /*     */ {
                    /*  56 */
                    if (itemEndOffset >= startEndSection)
                        /*     */ {
                        /*  58 */
                        length = endOfFirstSection - item.getOffset() + 5 + (itemEndOffset - startEndSection);
                        /*     */
                    }
                    /*     */
                    else {
                        /*  61 */
                        length = endOfFirstSection - item.getOffset();
                        /*     */
                    }
                    /*  63 */
                    this.items.add(buildNewItem(offset, length, (IActionableTextItem) item));
                    /*     */
                }
                /*     */
                else
                    /*     */ {
                    /*  67 */
                    this.items.add(item);
                    /*     */
                }
                /*     */
            }
            /*  70 */
            else if (itemEndOffset >= startEndSection)
                /*     */ {
                /*     */
                /*     */
                /*     */
                /*  75 */
                if (item.getOffset() < startEndSection) {
                    /*  76 */
                    offset = maxCharsPerLine - charsEndLine;
                    /*  77 */
                    length -= startEndSection - item.getOffset();
                    /*     */
                }
                /*     */
                else {
                    /*  80 */
                    offset = maxCharsPerLine - charsEndLine + (item.getOffset() - startEndSection);
                    /*     */
                }
                /*  82 */
                this.items.add(buildNewItem(offset, length, (IActionableTextItem) item));
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private static TextItem buildNewItem(int offset, int length, IActionableTextItem item) {
        /*  88 */
        return new TextItem(offset, length, item.getClassId(), item.getItemId(), item.getItemFlags());
        /*     */
    }

    /*     */
    /*     */
    public CharSequence getText()
    /*     */ {
        /*  93 */
        return this.text;
        /*     */
    }

    /*     */
    /*     */
    public List<? extends ITextItem> getItems()
    /*     */ {
        /*  98 */
        return this.items;
        /*     */
    }

    /*     */
    /*     */
    public List<? extends ITextMark> getMarks()
    /*     */ {
        /* 103 */
        logger.error("Marks retrieval in delegated lines is currently not implemented; will not fail, returning empty list instead", new Object[0]);
        /* 104 */
        return Collections.emptyList();
        /*     */
    }

    /*     */
    /*     */
    public ILine getDelegate() {
        /* 108 */
        return this.line;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\LineDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */