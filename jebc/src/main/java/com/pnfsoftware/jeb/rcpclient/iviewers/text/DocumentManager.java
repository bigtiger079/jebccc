/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.text.IAnchor;
/*     */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;

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
/*     */ public class DocumentManager
        /*     */ {
    /*     */   private final ITextDocument doc;
    /*     */   private ITextDocumentPart docPart;
    /*     */   private boolean displayEolAtEod;
    /*     */   public static final int defaultCharactersWrap = 80;

    /*     */
    /*     */   static class RenderedItem
            /*     */ {
        /*     */     public ITextItem item;
        /*     */     public int offset;

        /*     */
        /*     */
        public RenderedItem(ITextItem item, int offset)
        /*     */ {
            /*  40 */
            this.item = item;
            /*  41 */
            this.offset = offset;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*  53 */   private int hintMaxCharsPerLine = -1;
    /*  54 */   private int maxDeltaForWSP = 10;
    /*  55 */   private String wrapIndentString = "        ";
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
    /*  68 */   private List<int[]> wrappedData = new ArrayList();
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*  75 */   private Map<Integer, List<Integer>> wrapIndexes = new HashMap();
    /*     */   private String docText;
    /*  77 */   private List<RenderedItem> renderedItems = new ArrayList();

    /*     */
    /*     */
    public DocumentManager(ITextDocument doc) {
        /*  80 */
        this(doc, false);
        /*     */
    }

    /*     */
    /*     */
    public DocumentManager(ITextDocument doc, boolean displayEolAtEod) {
        /*  84 */
        this.doc = doc;
        /*  85 */
        this.displayEolAtEod = displayEolAtEod;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void setCharactersWrap(int charactersWrap)
    /*     */ {
        /*  96 */
        if (charactersWrap == 0) {
            /*  97 */
            charactersWrap = 80;
            /*     */
        }
        /*  99 */
        this.hintMaxCharsPerLine = charactersWrap;
        /*     */
    }

    /*     */
    /*     */
    public int getCharactersWrap() {
        /* 103 */
        return this.hintMaxCharsPerLine;
        /*     */
    }

    /*     */
    /*     */
    public ITextDocumentPart getCurrentPart() {
        /* 107 */
        return this.docPart;
        /*     */
    }

    /*     */
    /*     */
    public void setCurrentPart(ITextDocumentPart docPart) {
        /* 111 */
        this.docPart = docPart;
        /* 112 */
        this.docText = buildTextInternal();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public String getText()
    /*     */ {
        /* 121 */
        return this.docText;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public ITextDocument getDocument()
    /*     */ {
        /* 130 */
        return this.doc;
        /*     */
    }

    /*     */
    /*     */
    public boolean isSingleAnchorDocument() {
        /* 134 */
        return this.doc.getAnchorCount() == 1L;
        /*     */
    }

    /*     */
    /*     */
    public long getAnchorRange() {
        /* 138 */
        return this.doc.getAnchorCount();
        /*     */
    }

    /*     */
    /*     */
    public long getAnchorFirst() {
        /* 142 */
        return this.doc.getFirstAnchor();
        /*     */
    }

    /*     */
    /*     */
    public long getAnchorEnd() {
        /* 146 */
        return this.doc.getFirstAnchor() + this.doc.getAnchorCount();
        /*     */
    }

    /*     */
    /*     */
    public ITextDocumentPart getPart(long anchorId, int linesAfter) {
        /* 150 */
        ITextDocumentPart newDocPart = this.doc.getDocumentPart(anchorId, linesAfter);
        /* 151 */
        return newDocPart;
        /*     */
    }

    /*     */
    /*     */
    public ITextDocumentPart getPart(long anchorId, int linesAfter, int linesBefore) {
        /* 155 */
        ITextDocumentPart newDocPart = this.doc.getDocumentPart(anchorId, linesAfter, linesBefore);
        /* 156 */
        return newDocPart;
        /*     */
    }

    /*     */
    /*     */
    public IAnchor getAnchorById(long anchorId) {
        /* 160 */
        if (getCurrentPart() == null) {
            /* 161 */
            return null;
            /*     */
        }
        /*     */
        /* 164 */
        return TextPartUtil.getAnchorById(getCurrentPart(), anchorId);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public int unwrapLine(int wrappedLineIndex)
    /*     */ {
        /* 174 */
        if ((wrappedLineIndex < 0) || (wrappedLineIndex >= this.wrappedData.size())) {
            /* 175 */
            return -1;
            /*     */
        }
        /*     */
        /* 178 */
        return ((int[]) this.wrappedData.get(wrappedLineIndex))[0];
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public UnwrappedBufferPoint unwrap(BufferPoint p)
    /*     */ {
        /* 188 */
        int wrappedLineIndex = p.lineIndex;
        /* 189 */
        if ((wrappedLineIndex < 0) || (wrappedLineIndex >= this.wrappedData.size())) {
            /* 190 */
            return null;
            /*     */
        }
        /*     */
        /* 193 */
        int[] data = (int[]) this.wrappedData.get(wrappedLineIndex);
        /* 194 */
        int lineIndex = data[0];
        /*     */
        /* 196 */
        int wrappedColumnOffset = p.columnOffset;
        /* 197 */
        boolean eol = wrappedColumnOffset == data[2];
        /*     */
        int columnOffset;
        /*     */
        int columnOffset;
        /* 200 */
        if (data[1] == 0) {
            /* 201 */
            columnOffset = wrappedColumnOffset;
            /*     */
        }
        /*     */
        else {
            /* 204 */
            int currentWrapIndentSize = data[3];
            /* 205 */
            columnOffset = data[1] + Math.max(currentWrapIndentSize, wrappedColumnOffset) - currentWrapIndentSize;
            /*     */
        }
        /* 207 */
        return new UnwrappedBufferPoint(columnOffset, lineIndex, eol);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public int wrapLine(int lineIndex)
    /*     */ {
        /* 217 */
        BufferPoint p = wrap(new UnwrappedBufferPoint(0, lineIndex, false));
        /* 218 */
        if (p == null) {
            /* 219 */
            return -1;
            /*     */
        }
        /*     */
        /* 222 */
        return p.lineIndex;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public BufferPoint wrap(UnwrappedBufferPoint p)
    /*     */ {
        /* 232 */
        int lineIndex = p.lineIndex;
        /* 233 */
        int columnOffset = p.columnOffset;
        /* 234 */
        boolean eol = p.eol;
        /*     */
        /* 236 */
        List<Integer> indexes = (List) this.wrapIndexes.get(Integer.valueOf(p.lineIndex));
        /* 237 */
        if ((indexes == null) || (indexes.isEmpty())) {
            /* 238 */
            return null;
            /*     */
        }
        /*     */
        /* 241 */
        int i = ((Integer) indexes.get(0)).intValue();
        /* 242 */
        int[] data = (int[]) this.wrappedData.get(i);
        /* 243 */
        if ((isEmptyLine(indexes)) || (indexes.size() == 1) || (columnOffset < data[2]) || ((eol) && (columnOffset == data[2]))) {
            /* 244 */
            return new BufferPoint(columnOffset, i);
            /*     */
        }
        /*     */
        /* 247 */
        for (int j = 1; j < indexes.size(); j++) {
            /* 248 */
            i = ((Integer) indexes.get(j)).intValue();
            /* 249 */
            data = (int[]) this.wrappedData.get(i);
            /* 250 */
            if (data[0] != lineIndex) {
                /*     */
                break;
                /*     */
            }
            /* 253 */
            if ((columnOffset >= data[1]) && ((columnOffset < data[2]) || ((eol) && (columnOffset == data[2])))) {
                /* 254 */
                return new BufferPoint(columnOffset - data[1] + data[3], i);
                /*     */
            }
            /*     */
        }
        /* 257 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    private boolean isEmptyLine(List<Integer> indexes) {
        /* 261 */
        int i = ((Integer) indexes.get(0)).intValue();
        /* 262 */
        int[] data = (int[]) this.wrappedData.get(i);
        /* 263 */
        return (indexes.size() == 1) && (data.length == 3);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   List<RenderedItem> getRenderedItems()
    /*     */ {
        /* 272 */
        return this.renderedItems;
        /*     */
    }

    /*     */
    /*     */
    public int getLineCount() {
        /* 276 */
        return this.docPart.getLines().size();
        /*     */
    }

    /*     */
    /*     */
    public int getWrappedLineCount() {
        /* 280 */
        return this.wrappedData.size();
        /*     */
    }

    /*     */
    /*     */
    private String buildTextInternal() {
        /* 284 */
        this.wrappedData.clear();
        /* 285 */
        this.renderedItems.clear();
        /* 286 */
        this.wrapIndexes.clear();
        /*     */
        /* 288 */
        boolean appendTrailingEol = false;
        /* 289 */
        if (this.displayEolAtEod) {
            /* 290 */
            IAnchor lastAnchor = (IAnchor) this.docPart.getAnchors().get(this.docPart.getAnchors().size() - 1);
            /* 291 */
            appendTrailingEol = lastAnchor.getIdentifier() >= getAnchorEnd() - 1L;
            /*     */
        }
        /*     */
        /* 294 */
        return buildText(this.docPart.getLines(), appendTrailingEol);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public String buildText(List<? extends ILine> lines)
    /*     */ {
        /* 301 */
        return buildText(lines, true);
        /*     */
    }

    /*     */
    /*     */
    private String buildText(List<? extends ILine> lines, boolean appendTrailingEol) {
        /* 305 */
        StringBuilder sb = new StringBuilder();
        /* 306 */
        for (int index = 0; index < lines.size(); index++) {
            /* 307 */
            ILine line = (ILine) lines.get(index);
            /* 308 */
            CharSequence text = line.getText();
            /*     */
            /* 310 */
            String currentWrapIndentString = this.wrapIndentString;
            /* 311 */
            int currentWrapIndentSize = -1;
            /*     */
            /* 313 */
            if (text.length() == 0) {
                /* 314 */
                addIndex(index, this.wrappedData.size());
                /* 315 */
                this.wrappedData.add(new int[]{index, 0, 0});
                /*     */
            }
            /*     */
            else {
                /* 318 */
                int pos = 0;
                /* 319 */
                while (pos < text.length()) {
                    int pos2;
                    /*     */
                    int pos2;
                    /* 321 */
                    if (this.hintMaxCharsPerLine > 0) {
                        /* 322 */
                        pos2 = pos + this.hintMaxCharsPerLine;
                        /*     */
                    }
                    /*     */
                    else {
                        /* 325 */
                        pos2 = text.length();
                        /*     */
                    }
                    /*     */
                    /*     */
                    /* 329 */
                    if (pos2 < text.length()) {
                        /* 330 */
                        int wspPos = findWhiteSpace(text, pos2);
                        /* 331 */
                        if ((wspPos >= 0) && (wspPos - pos2 <= this.maxDeltaForWSP)) {
                            /* 332 */
                            pos2 = wspPos;
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                    int itemThreshold;
                    /* 336 */
                    if (pos2 >= text.length()) {
                        /* 337 */
                        pos2 = text.length();
                        /*     */
                    }
                    /*     */
                    else
                        /*     */ {
                        /* 341 */
                        itemThreshold = -1;
                        /* 342 */
                        int nextItemThreshold = -1;
                        /* 343 */
                        List<? extends ITextItem> items = line.getItems();
                        /* 344 */
                        for (int i = 0; i < items.size(); i++) {
                            /* 345 */
                            ITextItem item = (ITextItem) items.get(i);
                            /* 346 */
                            if ((pos2 >= item.getOffset()) && (pos2 < item.getOffset() + item.getLength())) {
                                /* 347 */
                                itemThreshold = item.getOffset() + item.getLength();
                                /* 348 */
                                nextItemThreshold = i + 1 < items.size() ? ((ITextItem) items.get(i + 1)).getOffset() : -1;
                                /* 349 */
                                pos2 = findWhiteSpace(text, itemThreshold, nextItemThreshold);
                                /* 350 */
                                if (pos2 < 0) {
                                    /* 351 */
                                    pos2 = itemThreshold;
                                    /*     */
                                }
                                /*     */
                            }
                            /*     */
                            else {
                                /* 355 */
                                if (pos2 < item.getOffset()) {
                                    /*     */
                                    break;
                                    /*     */
                                }
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                    /* 362 */
                    if (pos > 0) {
                        /* 363 */
                        if (currentWrapIndentSize < 0) {
                            /* 364 */
                            currentWrapIndentString = extractEmptyPrefix(text) + this.wrapIndentString;
                            /* 365 */
                            currentWrapIndentSize = currentWrapIndentString.length();
                            /*     */
                        }
                        /*     */
                        /* 368 */
                        sb.append('\n');
                        /* 369 */
                        sb.append(currentWrapIndentString);
                        /*     */
                    }
                    /*     */
                    /*     */
                    /* 373 */
                    for (ITextItem item : line.getItems()) {
                        /* 374 */
                        if (item.getOffset() >= pos) {
                            /* 375 */
                            if (item.getOffset() >= pos2) {
                                /*     */
                                break;
                                /*     */
                            }
                            /* 378 */
                            int renderedItemOffset = sb.length() + (item.getOffset() - pos);
                            /* 379 */
                            this.renderedItems.add(new RenderedItem(item, renderedItemOffset));
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                    /*     */
                    /* 384 */
                    sb.append(text.subSequence(pos, pos2));
                    /*     */
                    /* 386 */
                    addIndex(index, this.wrappedData.size());
                    /* 387 */
                    this.wrappedData.add(new int[]{index, pos, pos2, currentWrapIndentSize});
                    /*     */
                    /*     */
                    /* 390 */
                    pos = pos2;
                    /*     */
                }
                /*     */
            }
            /*     */
            /*     */
            /* 395 */
            if ((index + 1 < lines.size()) || (appendTrailingEol)) {
                /* 396 */
                sb.append('\n');
                /*     */
            }
            /*     */
        }
        /*     */
        /* 400 */
        return sb.toString();
        /*     */
    }

    /*     */
    /*     */
    private void addIndex(int key, int index) {
        /* 404 */
        List<Integer> indexes = (List) this.wrapIndexes.get(Integer.valueOf(key));
        /* 405 */
        if (indexes == null) {
            /* 406 */
            indexes = new ArrayList();
            /* 407 */
            this.wrapIndexes.put(Integer.valueOf(key), indexes);
            /*     */
        }
        /* 409 */
        indexes.add(Integer.valueOf(index));
        /*     */
    }

    /*     */
    /*     */
    private int findWhiteSpace(CharSequence text, int pos) {
        /* 413 */
        return findWhiteSpace(text, pos, -1);
        /*     */
    }

    /*     */
    /*     */
    private int findWhiteSpace(CharSequence text, int pos, int end) {
        /* 417 */
        if ((end < 0) || (end > text.length())) {
            /* 418 */
            end = text.length();
            /*     */
        }
        /*     */
        /* 421 */
        while (pos < end) {
            /* 422 */
            char c = text.charAt(pos);
            /* 423 */
            if ((c == ' ') || (c == '\t')) {
                /* 424 */
                return pos;
                /*     */
            }
            /* 426 */
            pos++;
            /*     */
        }
        /* 428 */
        return -1;
        /*     */
    }

    /*     */
    /*     */
    private CharSequence extractEmptyPrefix(CharSequence text) {
        /* 432 */
        int pos = 0;
        /* 433 */
        while (pos < text.length()) {
            /* 434 */
            char c = text.charAt(pos);
            /* 435 */
            if ((c != ' ') && (c != '\t')) {
                /*     */
                break;
                /*     */
            }
            /*     */
            /* 439 */
            pos++;
            /*     */
        }
        /* 441 */
        return text.subSequence(0, pos);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\DocumentManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */