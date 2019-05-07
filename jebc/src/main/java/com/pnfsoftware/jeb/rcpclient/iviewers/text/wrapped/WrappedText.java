/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.text.IAnchor;
/*     */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.BufferPoint;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.DocumentManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.UnwrappedBufferPoint;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.text.JFaceTextUtil;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.ControlAdapter;
/*     */ import org.eclipse.swt.events.ControlEvent;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;

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
/*     */
/*     */
/*     */
/*     */
/*     */ public class WrappedText
        /*     */ {
    /*  43 */   private static final ILogger logger = GlobalLog.getLogger(WrappedText.class);
    /*     */   private DocumentManager docManager;
    /*     */   private StyledText text;
    /*     */   private Integer maxVisibleLineCount;

    /*     */
    /*     */   public class SelectionData {
        /*     */     private Point boundOffsets;

        /*     */
        /*  51 */
        public SelectionData() {
            this.boundOffsets = WrappedText.this.text.getSelection();
            /*  52 */
            this.selectionLength = (WrappedText.this.text.getCaretOffset() != this.boundOffsets.x ? this.boundOffsets.y - this.boundOffsets.x : this.boundOffsets.x - this.boundOffsets.y);
            /*     */
            /*  54 */
            this.selectionStartCoord = WrappedText.this.getCoordinates(this.selectionLength > 0 ? this.boundOffsets.x : this.boundOffsets.y);
            /*     */
        }

        /*     */
        /*     */
        public int getSelectionLength() {
            /*  58 */
            return this.selectionLength;
            /*     */
        }

        /*     */
        /*     */
        public ICoordinates getSelectionStartCoord() {
            /*  62 */
            return this.selectionStartCoord;
            /*     */
        }

        /*     */
        /*     */
        public int getStartLine() {
            /*  66 */
            return WrappedText.this.text.getLineAtOffset(this.boundOffsets.x);
            /*     */
        }

        /*     */
        /*     */
        public int getEndLine() {
            /*  70 */
            return WrappedText.this.text.getLineAtOffset(this.boundOffsets.y);
            /*     */
        }

        /*     */
        /*     */
        /*     */
        /*     */
        /*     */     private int selectionLength;
        /*     */
        /*     */
        /*     */
        /*     */     private ICoordinates selectionStartCoord;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*  86 */   private int columnAffinity = -1;
    /*     */
    /*     */
    /*     */
    /*     */
    /*  91 */   private boolean navigationMovedCaret = false;

    /*     */
    /*     */
    public WrappedText(DocumentManager docManager, StyledText text)
    /*     */ {
        /*  95 */
        this.docManager = docManager;
        /*  96 */
        this.text = text;
        /*     */
        /*  98 */
        text.addControlListener(new ControlAdapter()
                /*     */ {
            /*     */
            public void controlResized(ControlEvent e) {
                /* 101 */
                WrappedText.this.maxVisibleLineCount = null;
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public DocumentManager getDocumentManager() {
        /* 107 */
        return this.docManager;
        /*     */
    }

    /*     */
    /*     */
    public StyledText getTextWidget() {
        /* 111 */
        return this.text;
        /*     */
    }

    /*     */
    /*     */   int sanitizeOffset(int bufferOffset) {
        /* 115 */
        return sanitizeOffset(bufferOffset, true);
        /*     */
    }

    /*     */
    /*     */   int sanitizeOffset(int bufferOffset, boolean includeEnd) {
        /* 119 */
        if (bufferOffset < 0) {
            /* 120 */
            bufferOffset = 0;
            /*     */
        }
        /*     */
        else {
            /* 123 */
            int len = this.text.getCharCount();
            /* 124 */
            if (len == 0) {
                /* 125 */
                bufferOffset = 0;
                /*     */
            }
            /* 127 */
            else if (bufferOffset >= len) {
                /* 128 */
                bufferOffset = includeEnd ? len : len - 1;
                /*     */
            }
            /*     */
        }
        /* 131 */
        return bufferOffset;
        /*     */
    }

    /*     */
    /*     */   int sanitizeLine(int lineIndex) {
        /* 135 */
        if (lineIndex < 0) {
            /* 136 */
            lineIndex = 0;
            /*     */
        }
        /*     */
        else {
            /* 139 */
            int len = this.text.getLineCount();
            /*     */
            /* 141 */
            if (len == 0) {
                /* 142 */
                throw new IllegalStateException("StyledText");
                /*     */
            }
            /* 144 */
            if (lineIndex >= len) {
                /* 145 */
                lineIndex = len - 1;
                /*     */
            }
            /*     */
        }
        /* 148 */
        return lineIndex;
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
    public int getCaretOffset()
    /*     */ {
        /* 159 */
        return this.text.getCaretOffset();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void setCaretOffset(int offset)
    /*     */ {
        /* 167 */
        this.text.setCaretOffset(sanitizeOffset(offset));
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public int getCaretLine()
    /*     */ {
        /* 174 */
        return this.text.getLineAtOffset(this.text.getCaretOffset());
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public int getLineCount()
    /*     */ {
        /* 181 */
        return this.text.getLineCount();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public int getFullyVisibleLineCount()
    /*     */ {
        /* 189 */
        return this.text.getClientArea().height / this.text.getLineHeight();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public int getVisibleLineCount()
    /*     */ {
        /* 197 */
        return getBottomIndex() - getTopIndex() + 1;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public String getLine(int lineIndex)
    /*     */ {
        /* 205 */
        return this.text.getLine(lineIndex);
        /*     */
    }

    /*     */
    /*     */
    public int getCharCount() {
        /* 209 */
        return this.text.getCharCount();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public int getTopIndex()
    /*     */ {
        /* 216 */
        return this.text.getTopIndex();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public int getBottomIndex()
    /*     */ {
        /* 223 */
        return JFaceTextUtil.getBottomIndex(this.text);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public void setTopIndex(int lineIndex)
    /*     */ {
        /* 230 */
        this.text.setTopIndex(sanitizeLine(lineIndex));
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public int getHorizontalIndex()
    /*     */ {
        /* 237 */
        return this.text.getHorizontalIndex();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public void setHorizontalIndex(int column)
    /*     */ {
        /* 244 */
        this.text.setHorizontalIndex(column);
        /*     */
    }

    /*     */
    /*     */
    public void setTopIndex(WrappedAnchor anchor) {
        /* 248 */
        int lineIndex = 0;
        /* 249 */
        if (anchor != null) {
            /* 250 */
            lineIndex = anchor.getWrappedLineIndex();
            /*     */
        }
        /* 252 */
        setTopIndex(lineIndex);
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
    public ICoordinates getCoordinates(int bufferOffset)
    /*     */ {
        /* 263 */
        int wrappedLineIndex = this.text.getLineAtOffset(bufferOffset);
        /* 264 */
        int wrappedColumnOffset = bufferOffset - this.text.getOffsetAtLine(wrappedLineIndex);
        /*     */
        /* 266 */
        BufferPoint p = this.docManager.unwrap(new BufferPoint(wrappedColumnOffset, wrappedLineIndex));
        /* 267 */
        if (p == null) {
            /* 268 */
            return null;
            /*     */
        }
        /*     */
        /* 271 */
        int lineIndex = p.lineIndex;
        /* 272 */
        int columnOffset = p.columnOffset;
        /*     */
        /* 274 */
        WrappedAnchor bestAnchor = getAnchorAtLine(lineIndex);
        /* 275 */
        if (bestAnchor == null) {
            /* 276 */
            return null;
            /*     */
        }
        /*     */
        /*     */
        /* 280 */
        int lineDelta = lineIndex - bestAnchor.getUnwrappedLineIndex();
        /* 281 */
        return new Coordinates(bestAnchor.getIdentifier(), lineDelta, columnOffset);
        /*     */
    }

    /*     */
    /*     */
    public ICoordinates getTopIndexCoordinates() {
        /* 285 */
        return getCoordinates(this.text.getOffsetAtLine(this.text.getTopIndex()));
        /*     */
    }

    /*     */
    /*     */
    public ICoordinates getCaretCoordinates() {
        /* 289 */
        return getCoordinates(this.text.getCaretOffset());
        /*     */
    }

    /*     */
    /*     */
    public void setCaretCoordinates(ICoordinates caret, boolean eol) {
        /* 293 */
        if (caret != null) {
            /* 294 */
            int caretUnwrappedOffset = getBufferOffset(caret, eol);
            /* 295 */
            if (caretUnwrappedOffset >= 0) {
                /* 296 */
                this.text.setCaretOffset(caretUnwrappedOffset);
                /* 297 */
                return;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 301 */
        this.text.setCaretOffset(getLastLineOffset());
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public BufferPoint getCaretViewportPoint()
    /*     */ {
        /* 310 */
        int deltaX = getHorizontalIndex();
        /* 311 */
        int deltaY = getTopIndex();
        /* 312 */
        int caretOffset = getCaretOffset();
        /* 313 */
        int caretLine = getLineAtOffset(caretOffset);
        /* 314 */
        int caretOffsetInLine = caretOffset - getOffsetAtLine(caretLine);
        /* 315 */
        return new BufferPoint(caretOffsetInLine - deltaX, caretLine - deltaY);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public int getLastLineOffset()
    /*     */ {
        /* 322 */
        int len = this.text.getLineCount();
        /* 323 */
        if (len == 0) {
            /* 324 */
            throw new IllegalStateException("StyledText");
            /*     */
        }
        /* 326 */
        return this.text.getOffsetAtLine(len - 1);
        /*     */
    }

    /*     */
    /*     */
    public boolean hasSelection() {
        /* 330 */
        return this.text.getSelectionCount() > 0;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void setSelection(int start, int end)
    /*     */ {
        /* 338 */
        this.text.setSelection(start, end);
        /*     */
    }

    /*     */
    /*     */
    public void selectAll() {
        /* 342 */
        this.text.selectAll();
        /*     */
    }

    /*     */
    /*     */
    public SelectionData getSelectionData() {
        /* 346 */
        return new SelectionData();
        /*     */
    }

    /*     */
    /*     */
    public Point getSelectionRange() {
        /* 350 */
        return this.text.getSelectionRange();
        /*     */
    }

    /*     */
    /*     */
    public void setSelectionRange(ICoordinates startCoords, boolean eol, int selectionLength) {
        /* 354 */
        int selectionStartOffset = 0;
        /* 355 */
        if (startCoords != null) {
            /* 356 */
            selectionStartOffset = getBufferOffset(startCoords, eol);
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 360 */
            selectionStartOffset = getLastLineOffset();
            /*     */
        }
        /* 362 */
        if (selectionStartOffset >= 0) {
            /* 363 */
            setNavigationMovedCaret(true);
            /* 364 */
            this.text.setSelectionRange(selectionStartOffset, selectionLength);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void resetSelection() {
        /* 369 */
        SelectionData sel = getSelectionData();
        /* 370 */
        this.text.setSelectionRange(sel.getSelectionLength() >= 0 ? sel.boundOffsets.y : sel.boundOffsets.x, 0);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public int getMaxVisibleLineCount()
    /*     */ {
        /* 379 */
        if (this.maxVisibleLineCount == null) {
            /* 380 */
            this.maxVisibleLineCount = Integer.valueOf(this.text.getClientArea().height / this.text.getLineHeight());
            /* 381 */
            if (this.maxVisibleLineCount.intValue() == 0)
                /*     */ {
                /* 383 */
                this.maxVisibleLineCount = Integer.valueOf(100);
                /*     */
            }
            /*     */
        }
        /* 386 */
        return this.maxVisibleLineCount.intValue();
        /*     */
    }

    /*     */
    /*     */
    public int getWrappedLineCount() {
        /* 390 */
        return this.docManager.getWrappedLineCount();
        /*     */
    }

    /*     */
    /*     */
    public ITextDocumentPart getCurrentPart() {
        /* 394 */
        return this.docManager.getCurrentPart();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public int getBufferOffset(ICoordinates coord, boolean eol)
    /*     */ {
        /* 404 */
        ITextDocumentPart part = this.docManager.getCurrentPart();
        /* 405 */
        if (!TextPartUtil.isInsidePart(part, coord)) {
            /* 406 */
            return -1;
            /*     */
        }
        /* 408 */
        int index = TextPartUtil.coordinatesToLineIndex(part, coord);
        /* 409 */
        BufferPoint p = this.docManager.wrap(new UnwrappedBufferPoint(coord.getColumnOffset(), index, eol));
        /* 410 */
        return this.text.getOffsetAtLine(p.lineIndex) + p.columnOffset;
        /*     */
    }

    /*     */
    /*     */
    public WrappedAnchor wrap(IAnchor anchor) {
        /* 414 */
        if (anchor == null) {
            /* 415 */
            return null;
            /*     */
        }
        /* 417 */
        return new WrappedAnchor(this.docManager, anchor);
        /*     */
    }

    /*     */
    /*     */
    public long getAnchorFirst() {
        /* 421 */
        return this.docManager.getAnchorFirst();
        /*     */
    }

    /*     */
    /*     */
    public WrappedAnchor getFirstVisibleAnchor() {
        /* 425 */
        return wrap(TextPartUtil.getFirstAnchor(this.docManager.getCurrentPart()));
        /*     */
    }

    /*     */
    /*     */
    public boolean isAnchorFirst(IAnchor anchor) {
        /* 429 */
        return anchor.getIdentifier() == this.docManager.getAnchorFirst();
        /*     */
    }

    /*     */
    /*     */
    public boolean isAnchorFirstDisplayed() {
        /* 433 */
        WrappedAnchor anchor = getFirstVisibleAnchor();
        /* 434 */
        if (anchor == null) {
            /* 435 */
            return false;
            /*     */
        }
        /* 437 */
        return isAnchorFirst(anchor.unwrap());
        /*     */
    }

    /*     */
    /*     */
    public long getAnchorEnd() {
        /* 441 */
        return this.docManager.getAnchorEnd();
        /*     */
    }

    /*     */
    /*     */
    public WrappedAnchor getEndVisibleAnchor() {
        /* 445 */
        return wrap(TextPartUtil.getLastAnchor(this.docManager.getCurrentPart()));
        /*     */
    }

    /*     */
    /*     */
    public boolean isAnchorEnd(IAnchor anchor)
    /*     */ {
        /* 450 */
        long anchorEnd = this.docManager.getAnchorEnd();
        /*     */
        /* 452 */
        List<? extends IAnchor> anchorsDisplayed = this.docManager.getCurrentPart().getAnchors();
        /* 453 */
        if (((IAnchor) anchorsDisplayed.get(anchorsDisplayed.size() - 1)).getIdentifier() == anchorEnd)
            /*     */ {
            /* 455 */
            return ((IAnchor) anchorsDisplayed.get(anchorsDisplayed.size() - 2)).getIdentifier() == anchor.getIdentifier();
            /*     */
        }
        /* 457 */
        if (anchorsDisplayed.contains(anchor))
            /*     */ {
            /* 459 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 467 */
        return this.docManager.getAnchorEnd() - 1L == anchor.getIdentifier();
        /*     */
    }

    /*     */
    /*     */
    public boolean isAnchorEndDisplayed() {
        /* 471 */
        ITextDocumentPart part = this.docManager.getCurrentPart();
        /* 472 */
        if (part == null) {
            /* 473 */
            return false;
            /*     */
        }
        /* 475 */
        int cnt = part.getAnchors().size();
        /* 476 */
        if (cnt == 0) {
            /* 477 */
            return false;
            /*     */
        }
        /* 479 */
        IAnchor a = (IAnchor) part.getAnchors().get(cnt - 1);
        /* 480 */
        return a.getIdentifier() == this.docManager.getAnchorEnd();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public WrappedAnchor getAnchorAtBufferOffset(int offset)
    /*     */ {
        /* 489 */
        int wrappedLineIndex = this.text.getLineAtOffset(offset);
        /* 490 */
        return getAnchorAtBufferLine(wrappedLineIndex);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public WrappedAnchor getAnchorAtBufferLine(int line)
    /*     */ {
        /* 500 */
        int lineIndex = this.docManager.unwrapLine(line);
        /* 501 */
        if (lineIndex < 0) {
            /* 502 */
            return null;
            /*     */
        }
        /* 504 */
        return getAnchorAtLine(lineIndex);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public WrappedAnchor getAnchorAtLine(int lineIndex)
    /*     */ {
        /* 514 */
        IAnchor anchor = TextPartUtil.getAnchorAtLine(this.docManager.getCurrentPart(), lineIndex);
        /* 515 */
        if (anchor == null) {
            /* 516 */
            return null;
            /*     */
        }
        /* 518 */
        return new WrappedAnchor(this.docManager, anchor);
        /*     */
    }

    /*     */
    /*     */
    public int getLineAtOffset(int offset) {
        /* 522 */
        offset = sanitizeOffset(offset);
        /* 523 */
        return this.text.getLineAtOffset(offset);
        /*     */
    }

    /*     */
    /*     */
    public int getOffsetAtLine(int lineIndex) {
        /* 527 */
        if (lineIndex < 0) {
            /* 528 */
            return 0;
            /*     */
        }
        /* 530 */
        if (lineIndex >= this.text.getLineCount()) {
            /* 531 */
            return this.text.getLineCount() - 1;
            /*     */
        }
        /* 533 */
        return this.text.getOffsetAtLine(lineIndex);
        /*     */
    }

    /*     */
    /*     */
    public int getOffsetAtTopLine() {
        /* 537 */
        return getOffsetAtLine(getTopIndex());
        /*     */
    }

    /*     */
    /*     */
    public int getLineLength(int lineIndex) {
        /* 541 */
        return this.text.getLine(lineIndex).length();
        /*     */
    }

    /*     */
    /*     */
    public UnwrappedBufferPoint unwrap(int offset) {
        /* 545 */
        offset = sanitizeOffset(offset);
        /* 546 */
        int wrappedLineIndex = getLineAtOffset(offset);
        /* 547 */
        int wrappedColumnOffset = offset - getOffsetAtLine(wrappedLineIndex);
        /* 548 */
        return this.docManager.unwrap(new BufferPoint(wrappedColumnOffset, wrappedLineIndex));
        /*     */
    }

    /*     */
    /*     */
    public UnwrappedBufferPoint unwrap(BufferPoint point) {
        /* 552 */
        return this.docManager.unwrap(point);
        /*     */
    }

    /*     */
    /*     */
    public BufferPoint wrap(UnwrappedBufferPoint point) {
        /* 556 */
        return this.docManager.wrap(point);
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
    public void moveCaret(int delta, boolean selecting)
    /*     */ {
        /* 567 */
        Point sel = this.text.getSelection();
        /* 568 */
        int currentOffset = sel.x;
        /* 569 */
        int initialLength = 0;
        /* 570 */
        int selectionLength = 0;
        /* 571 */
        if (selecting) {
            /* 572 */
            if (this.text.getSelectionCount() != 0) {
                /* 573 */
                selectionLength = this.text.getCaretOffset() != sel.x ? sel.y - sel.x : sel.x - sel.y;
                /* 574 */
                initialLength = selectionLength;
                /*     */
            }
            /*     */
            /*     */
            /* 578 */
            if (initialLength > 0)
                /*     */ {
                /* 580 */
                currentOffset = sel.y;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 584 */
        int currentLine = this.text.getLineAtOffset(currentOffset);
        /* 585 */
        int lineStartOffset = this.text.getOffsetAtLine(currentLine);
        /*     */
        /* 587 */
        int columnOffset = currentOffset - lineStartOffset;
        /*     */
        /*     */
        /* 590 */
        currentOffset = lineStartOffset;
        /* 591 */
        if (selecting) {
            /* 592 */
            selectionLength -= columnOffset;
            /*     */
        }
        /*     */
        /* 595 */
        while (delta != 0) {
            /* 596 */
            currentLine = this.text.getLineAtOffset(currentOffset);
            /* 597 */
            if (delta > 0) {
                /* 598 */
                if (currentLine + 1 >= this.text.getLineCount()) {
                    /*     */
                    break;
                    /*     */
                }
                /* 601 */
                currentOffset = this.text.getOffsetAtLine(currentLine + 1);
                /* 602 */
                if (selecting) {
                    /* 603 */
                    selectionLength += this.text.getLine(currentLine).length() + 1;
                    /*     */
                }
                /* 605 */
                delta--;
                /*     */
            }
            /*     */
            else {
                /* 608 */
                if (currentLine - 1 < 0) {
                    /*     */
                    break;
                    /*     */
                }
                /* 611 */
                currentOffset = this.text.getOffsetAtLine(currentLine - 1);
                /* 612 */
                if (selecting) {
                    /* 613 */
                    selectionLength -= this.text.getLine(currentLine - 1).length() + 1;
                    /*     */
                }
                /* 615 */
                delta++;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 619 */
        currentLine = this.text.getLineAtOffset(currentOffset);
        /*     */
        /*     */
        /* 622 */
        if (columnOffset > this.text.getLine(currentLine).length()) {
            /* 623 */
            columnOffset = this.text.getLine(currentLine).length();
            /*     */
        }
        /* 625 */
        if (selecting) {
            /* 626 */
            this.text.setSelectionRange(initialLength >= 0 ? sel.x : sel.y, selectionLength + columnOffset);
            /*     */
        }
        /*     */
        else {
            /* 629 */
            setCaretWithColumnAffinity(currentOffset + columnOffset);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public void forceSelectionOffset(int offset)
    /*     */ {
        /* 637 */
        Point sel = this.text.getSelection();
        /* 638 */
        int selectionLength = 0;
        /* 639 */
        if (this.text.getSelectionCount() != 0) {
            /* 640 */
            selectionLength = this.text.getCaretOffset() != sel.x ? sel.y - sel.x : sel.x - sel.y;
            /*     */
        }
        /* 642 */
        if (selectionLength > 0)
            /*     */ {
            /* 644 */
            this.text.setSelectionRange(sel.x, offset - sel.x);
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 648 */
            this.text.setSelectionRange(sel.y, offset - sel.y);
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
    public void setCaretWithColumnAffinity(int offset)
    /*     */ {
        /* 660 */
        if (this.columnAffinity >= 0) {
            /* 661 */
            int line = getLineAtOffset(offset);
            /* 662 */
            int lineStartOffset = getOffsetAtLine(line);
            /* 663 */
            int currentColumnPosition = offset - lineStartOffset;
            /* 664 */
            if (currentColumnPosition < this.columnAffinity)
                /*     */ {
                /* 666 */
                offset = lineStartOffset + Math.min(this.columnAffinity, this.text.getLine(line).length());
                /*     */
            }
            /*     */
        }
        /* 669 */
        setNavigationMovedCaret(true);
        /* 670 */
        setCaretOffset(offset);
        /*     */
    }

    /*     */
    /*     */
    public boolean isNavigationMovedCaret() {
        /* 674 */
        return this.navigationMovedCaret;
        /*     */
    }

    /*     */
    /*     */
    public void setNavigationMovedCaret(boolean moved) {
        /* 678 */
        this.navigationMovedCaret = moved;
        /*     */
    }

    /*     */
    /*     */
    public void updateColumnAffinityFromCaret() {
        /* 682 */
        int caretOffset = this.text.getCaretOffset();
        /* 683 */
        int caretLine = this.text.getLineAtOffset(caretOffset);
        /* 684 */
        int lineStartOffset = this.text.getOffsetAtLine(caretLine);
        /* 685 */
        this.columnAffinity = (caretOffset - lineStartOffset);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean updateCaretColumn()
    /*     */ {
        /* 691 */
        if (isNavigationMovedCaret()) {
            /* 692 */
            setNavigationMovedCaret(false);
            /* 693 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /* 697 */
        updateColumnAffinityFromCaret();
        /* 698 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public boolean isCaretEol() {
        /* 702 */
        int caretOffset = this.text.getCaretOffset();
        /* 703 */
        if (this.text.getSelectionCount() != 0) {
            /* 704 */
            SelectionData sel = getSelectionData();
            /* 705 */
            caretOffset = sel.selectionLength >= 0 ? sel.boundOffsets.x : sel.boundOffsets.y;
            /*     */
        }
        /* 707 */
        int caretLine = this.text.getLineAtOffset(caretOffset);
        /* 708 */
        return getLineLength(caretLine) == caretOffset - this.text.getOffsetAtLine(caretLine);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public boolean isCurrentPartLastLineDisplayed()
    /*     */ {
        /* 718 */
        return getMaxVisibleLineCount() + this.text.getTopIndex() >= this.text.getLineCount();
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\wrapped\WrappedText.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */