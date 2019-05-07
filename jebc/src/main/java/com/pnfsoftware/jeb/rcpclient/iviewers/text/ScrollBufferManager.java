/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.text.IAnchor;
/*     */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedAnchor;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedText;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedText.SelectionData;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.graphics.Point;

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
/*     */
/*     */
/*     */ public class ScrollBufferManager
        /*     */ {
    /*  40 */   private static final ILogger logger = GlobalLog.getLogger(ScrollBufferManager.class, Integer.MAX_VALUE);
    /*     */   private WrappedText wrappedText;
    /*     */   private INavigableViewer viewer;
    /*     */   private DocumentManager docManager;
    /*     */   private StyledText textWidget;

    /*     */
    /*     */
    public ScrollBufferManager(WrappedText wrappedText, INavigableViewer viewer)
    /*     */ {
        /*  48 */
        this.wrappedText = wrappedText;
        /*  49 */
        this.viewer = viewer;
        /*     */
        /*  51 */
        this.docManager = wrappedText.getDocumentManager();
        /*  52 */
        this.textWidget = wrappedText.getTextWidget();
        /*     */
    }

    /*     */
    /*     */   WrappedText getWrappedText() {
        /*  56 */
        return this.wrappedText;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void viewAtAnchor(long anchorId)
    /*     */ {
        /*  64 */
        viewAtAnchor(anchorId, false);
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
    public void viewAtAnchor(long anchorId, boolean doNotSetCaret)
    /*     */ {
        /*  75 */
        int reqAfter = 2 * this.wrappedText.getMaxVisibleLineCount() + 1;
        /*  76 */
        int reqBefore = 2 * this.wrappedText.getMaxVisibleLineCount();
        /*  77 */
        viewAtAnchor(anchorId, reqAfter, reqBefore, doNotSetCaret);
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
    /*     */
    private void viewAtAnchor(long anchorId, int reqAfter, int reqBefore, boolean doNotSetCaret)
    /*     */ {
        /*  90 */
        if (isSinglePartLoaded()) {
            /*  91 */
            return;
            /*     */
        }
        /*     */
        /*  94 */
        long t0 = System.currentTimeMillis();
        /*  95 */
        logger.debug("viewAtAnchor(%d)", new Object[]{Long.valueOf(anchorId)});
        /*     */
        /*  97 */
        boolean goToEndOfFile = false;
        /*  98 */
        if (anchorId < this.wrappedText.getAnchorFirst()) {
            /*  99 */
            anchorId = 0L;
            /*     */
        }
        /* 101 */
        else if (anchorId >= this.wrappedText.getAnchorEnd()) {
            /* 102 */
            anchorId = this.wrappedText.getAnchorEnd() - 1L;
            /* 103 */
            goToEndOfFile = true;
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 108 */
        WrappedAnchor currentAnchor = null;
        /* 109 */
        ITextDocumentPart part = this.wrappedText.getCurrentPart();
        /* 110 */
        if (part != null) {
            /* 111 */
            currentAnchor = this.wrappedText.wrap(TextPartUtil.getApproximateAnchorById(part, anchorId, 0));
            /*     */
        }
        /*     */
        /*     */
        /* 115 */
        if (currentAnchor != null) {
            /* 116 */
            anchorId = currentAnchor.getIdentifier();
            /* 117 */
            if ((!goToEndOfFile) && (this.wrappedText.getTopIndex() == currentAnchor.getWrappedLineIndex())) {
                /* 118 */
                return;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 123 */
        BufferPoint caret = this.wrappedText.getCaretViewportPoint();
        /* 124 */
        if ((caret.lineIndex >= this.wrappedText.getMaxVisibleLineCount()) || (caret.lineIndex < 0))
            /*     */ {
            /* 126 */
            caret.lineIndex = 0;
            /*     */
        }
        /*     */
        /*     */
        try
            /*     */ {
            /* 131 */
            this.textWidget.setRedraw(false);
            /*     */
            /* 133 */
            if (!this.viewer.updateDocument(anchorId, reqAfter, reqBefore)) {
                /* 134 */
                return;
                /*     */
            }
            /*     */
            /*     */
            /* 138 */
            if (!goToEndOfFile)
                /*     */ {
                /* 140 */
                currentAnchor = null;
                /* 141 */
                part = this.wrappedText.getCurrentPart();
                /* 142 */
                if (part != null) {
                    /* 143 */
                    currentAnchor = this.wrappedText.wrap(TextPartUtil.getNearestAnchorById(part, anchorId));
                    /*     */
                }
                /*     */
                /* 146 */
                this.wrappedText.setTopIndex(currentAnchor);
                /* 147 */
                this.textWidget.update();
                /*     */
            }
            /*     */
            else
                /*     */ {
                /* 151 */
                logger.debug("scrolling to EOF", new Object[0]);
                /* 152 */
                viewAtAnchor(anchorId, doNotSetCaret);
                /*     */
                /* 154 */
                this.wrappedText.setTopIndex(this.wrappedText.getLineCount());
                /*     */
            }
            /*     */
            /*     */
            /* 158 */
            if (!doNotSetCaret) {
                /* 159 */
                setCaretViewportPoint(caret);
                /*     */
            }
            /*     */
        }
        /*     */ finally {
            /* 163 */
            this.textWidget.setRedraw(true);
            /*     */
        }
        /*     */
        /* 166 */
        long t1 = System.currentTimeMillis();
        /* 167 */
        logger.debug("viewAtAnchor took %dms", new Object[]{Long.valueOf(t1 - t0)});
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private boolean isSinglePartLoaded()
    /*     */ {
        /* 177 */
        if ((this.docManager.getDocument().getAnchorCount() == 1L) && (this.docManager.getCurrentPart() != null))
            /*     */ {
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /* 185 */
            return true;
            /*     */
        }
        /*     */
        /* 188 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void viewAtBufferLine(int reqLine)
    /*     */ {
        /* 197 */
        int delta = reqLine - this.wrappedText.getTopIndex();
        /* 198 */
        scroll(delta, delta, false, true, false);
        /*     */
    }

    /*     */
    /*     */
    public void viewAtStartOfDocument() {
        /* 202 */
        viewAtAnchor(this.docManager.getDocument().getFirstAnchor());
        /* 203 */
        this.wrappedText.setCaretOffset(0);
        /*     */
    }

    /*     */
    /*     */
    public void viewAtEndOfDocument()
    /*     */ {
        /* 208 */
        viewAtAnchor(this.docManager.getAnchorEnd());
        /* 209 */
        this.wrappedText.setCaretOffset(this.wrappedText.getCharCount());
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void scroll(int delta)
    /*     */ {
        /* 219 */
        scroll(delta, delta, false, true, false);
        /*     */
    }

    /*     */
    /*     */
    public void scroll(int windowScroll, int caretScroll, boolean selecting, boolean lockCaretOnBounds) {
        /* 223 */
        scroll(windowScroll, caretScroll, selecting, lockCaretOnBounds, false);
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
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void scroll(int windowScroll, int caretScroll, boolean selecting, boolean lockCaretOnBounds, boolean forceFetch)
    /*     */ {
        /* 240 */
        if (this.wrappedText.getCurrentPart() == null)
            /*     */ {
            /* 242 */
            return;
            /*     */
        }
        /* 244 */
        logger.debug("scroll: window(%d) caret(%d) selecting(%b) lockCaretOnBounds(%b) forceFetch(%b)", new Object[]{Integer.valueOf(windowScroll),
/* 245 */       Integer.valueOf(caretScroll), Boolean.valueOf(selecting), Boolean.valueOf(lockCaretOnBounds), Boolean.valueOf(forceFetch)});
        /* 246 */
        ICoordinates oldTopLine = this.wrappedText.getTopIndexCoordinates();
        /*     */
        /*     */
        /* 249 */
        VisualSelectionUnwrapped caret = getVisualSelection();
        /*     */
        /* 251 */
        Integer reqLine = Integer.valueOf(this.wrappedText.getTopIndex() + windowScroll);
        /* 252 */
        if ((reqLine.intValue() < 0) || (reqLine.intValue() >= this.wrappedText.getLineCount())) {
            /* 253 */
            forceFetch = false;
            /*     */
        }
        /* 255 */
        if (!forceFetch) {
            /* 256 */
            reqLine = sanitizeScroll(reqLine.intValue());
            /*     */
        }
        /*     */
        /* 259 */
        UpdateDocumentData updateData = null;
        /* 260 */
        if (reqLine != null) {
            /* 261 */
            if ((!forceFetch) && (isSinglePartLoaded())) {
                /* 262 */
                updateData = new UpdateDocumentData(-1L, 0, 0, 0);
                /*     */
            }
            /*     */
            else {
                /* 265 */
                updateData = getUpdateData(reqLine.intValue(), forceFetch, selecting);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        try
            /*     */ {
            /* 271 */
            this.textWidget.setRedraw(false);
            /* 272 */
            if (updateData != null) {
                /* 273 */
                updateDocument(reqLine.intValue(), updateData);
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /* 278 */
            setVisualSelection(caret);
            /*     */
            /* 280 */
            ICoordinates newTopLine = this.wrappedText.getTopIndexCoordinates();
            /* 281 */
            if ((windowScroll != 0) && (newTopLine != null) && (newTopLine.equals(oldTopLine)) && (lockCaretOnBounds))
                /*     */ {
                /* 283 */
                return;
                /*     */
            }
            /*     */
            /* 286 */
            if (caretScroll != 0) {
                /* 287 */
                moveCaretWithToplineUpdate(caretScroll, selecting);
                /*     */
            }
            /*     */
        }
        /*     */ finally {
            /* 291 */
            this.textWidget.setRedraw(true);
            /*     */
        }
        /*     */
        /* 294 */
        if (selecting) {
            /* 295 */
            WrappedText.SelectionData sel = this.wrappedText.getSelectionData();
            /* 296 */
            logger.debug("New Selection Start(%s), selection length(%d)", new Object[]{sel.getSelectionStartCoord(),
/* 297 */         Integer.valueOf(sel.getSelectionLength())});
            /*     */
            /* 299 */
            if (caretScroll < 0) {
                /* 300 */
                this.wrappedText.forceSelectionOffset(0);
                /*     */
            }
            /*     */
        }
        /*     */
        else {
            /* 304 */
            ICoordinates newCaret = this.wrappedText.getCaretCoordinates();
            /* 305 */
            logger.debug("oldCaret=%s, newCaret=%s", new Object[]{caret.docCoord, newCaret});
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */   private static class MaxLine
            /*     */ {
        /*     */     public int before;
        /*     */
        /*     */
        /*     */     public int after;

        /*     */
        /*     */
        /*     */
        /*     */
        public MaxLine(int maxlinecnt)
        /*     */ {
            /* 322 */
            this.before = maxlinecnt;
            /* 323 */
            this.after = maxlinecnt;
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
    private Integer sanitizeScroll(int reqLine)
    /*     */ {
        /* 335 */
        int bufferLineCount = this.wrappedText.getLineCount();
        /* 336 */
        int maxlineCount = this.wrappedText.getMaxVisibleLineCount();
        /*     */
        /* 338 */
        if ((this.wrappedText.isAnchorFirstDisplayed()) && (this.wrappedText.getTopIndex() == 0))
            /*     */ {
            /* 340 */
            if (bufferLineCount >= 2 * maxlineCount)
                /*     */ {
                /*     */
                /*     */
                /*     */
                /* 345 */
                if (reqLine < 0) {
                    /* 346 */
                    logger.debug("Top document reached", new Object[0]);
                    /* 347 */
                    return null;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 352 */
        if ((this.wrappedText.isAnchorEndDisplayed()) && (this.wrappedText.getTopIndex() < reqLine))
            /*     */ {
            /* 354 */
            int reqMaxAllowed = this.wrappedText.getWrappedLineCount() - maxlineCount + 1;
            /* 355 */
            if (reqLine > reqMaxAllowed)
                /*     */ {
                /*     */
                /* 358 */
                reqLine = reqMaxAllowed;
                /*     */
            }
            /*     */
            /*     */
            /* 362 */
            if (this.wrappedText.isCurrentPartLastLineDisplayed()) {
                /* 363 */
                logger.debug("Bottom document reached", new Object[0]);
                /* 364 */
                return null;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 368 */
        return Integer.valueOf(reqLine);
        /*     */
    }

    /*     */
    /*     */   private static class UpdateDocumentData {
        /*     */ long reqAnchorId;
        /*     */ int reqBefore;
        /*     */ int reqAfter;
        /*     */ int delta;

        /*     */
        /*     */
        public UpdateDocumentData(long reqAnchorId, int reqBefore, int reqAfter, int delta) {
            /* 378 */
            this.reqAnchorId = reqAnchorId;
            /* 379 */
            this.reqBefore = reqBefore;
            /* 380 */
            this.reqAfter = reqAfter;
            /* 381 */
            this.delta = delta;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private UpdateDocumentData getUpdateData(int reqLine, boolean forceFetch, boolean selecting) {
        /* 386 */
        int bufferLineCount = this.wrappedText.getLineCount();
        /* 387 */
        int maxVisibleLineCount = this.wrappedText.getMaxVisibleLineCount();
        /* 388 */
        int maxlineCount = 2 * this.wrappedText.getMaxVisibleLineCount();
        /* 389 */
        UpdateDocumentData updateData = new UpdateDocumentData(-1L, 0, 0, 0);
        /*     */
        /*     */
        /*     */
        /* 393 */
        if (forceFetch)
            /*     */ {
            /* 395 */
            BufferPoint p = this.wrappedText.unwrap(new BufferPoint(0, reqLine));
            /* 396 */
            if (p == null) {
                /* 397 */
                return null;
                /*     */
            }
            /*     */
            /* 400 */
            int line = p.lineIndex;
            /*     */
            /* 402 */
            WrappedAnchor a = this.wrappedText.getAnchorAtLine(line);
            /* 403 */
            int anchorLineIndex = a.getWrappedLineIndex();
            /*     */
            /* 405 */
            updateData.reqAnchorId = a.getIdentifier();
            /* 406 */
            updateData.reqAfter = (reqLine - anchorLineIndex + maxlineCount);
            /* 407 */
            updateData.reqBefore = maxlineCount;
            /* 408 */
            updateData.delta = (reqLine - anchorLineIndex);
            /*     */
            /*     */
            /*     */
        }
        /*     */
        else
            /*     */ {
            /*     */
            /* 415 */
            WrappedAnchor a = null;
            /* 416 */
            MaxLine maxLine = new MaxLine(maxlineCount);
            /*     */
            /*     */
            /* 419 */
            if ((this.wrappedText.hasSelection()) || (selecting)) {
                /* 420 */
                WrappedText.SelectionData sel = this.wrappedText.getSelectionData();
                /* 421 */
                int selStartLine = sel.getStartLine();
                /* 422 */
                int selEndLine = sel.getEndLine();
                /*     */
                /*     */
                /* 425 */
                a = this.wrappedText.getAnchorAtBufferLine(selStartLine);
                /* 426 */
                maxLine.after = (selEndLine - selStartLine + maxlineCount);
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /* 431 */
            if (((reqLine < 0) || (reqLine - maxVisibleLineCount < 0)) && (!this.wrappedText.isAnchorFirstDisplayed()))
                /*     */ {
                /*     */
                /* 434 */
                logger.debug("Requesting more data backward", new Object[0]);
                /* 435 */
                if (a == null) {
                    /* 436 */
                    a = this.wrappedText.getFirstVisibleAnchor();
                    /*     */
                }
                /* 438 */
                int anchorLineIndex = a.getWrappedLineIndex();
                /*     */
                /* 440 */
                updateData.reqAnchorId = a.getIdentifier();
                /* 441 */
                updateData.reqAfter = (Math.max(reqLine - anchorLineIndex, 0) + maxLine.after);
                /* 442 */
                updateData.reqBefore = Math.max(maxLine.before, anchorLineIndex - reqLine);
                /* 443 */
                updateData.delta = (reqLine - anchorLineIndex);
                /*     */
            }
            /* 445 */
            else if ((reqLine + maxlineCount >= bufferLineCount) && (!this.wrappedText.isAnchorEndDisplayed())) {
                /* 446 */
                logger.debug("Requesting more data forward, maxlinecntAfter(%d), bufferLineCount(%d)", new Object[]{Integer.valueOf(maxLine.after),
/* 447 */           Integer.valueOf(bufferLineCount)});
                /*     */
                /*     */
                /* 450 */
                if (a == null) {
                    /* 451 */
                    a = this.wrappedText.getEndVisibleAnchor();
                    /* 452 */
                    if (a == null) {
                        /* 453 */
                        logger.i("", new Object[0]);
                        /*     */
                    }
                    /*     */
                }
                /* 456 */
                int anchorLineIndex = a.getWrappedLineIndex();
                /*     */
                /* 458 */
                updateData.reqAnchorId = a.getIdentifier();
                /*     */
                /* 460 */
                updateData.reqAfter = (Math.max(reqLine - anchorLineIndex, 0) + maxLine.after);
                /* 461 */
                updateData.reqBefore = maxLine.before;
                /* 462 */
                updateData.delta = (reqLine - anchorLineIndex);
                /*     */
            }
            /*     */
        }
        /* 465 */
        return updateData;
        /*     */
    }

    /*     */
    /*     */
    private void updateDocument(int reqLine, UpdateDocumentData updateData) {
        /* 469 */
        int horizontalIndex = this.wrappedText.getHorizontalIndex();
        /*     */
        /*     */
        /* 472 */
        if (updateData.reqAnchorId >= 0L)
            /*     */ {
            /*     */
            /* 475 */
            if (!this.viewer.updateDocument(updateData.reqAnchorId, updateData.reqAfter, updateData.reqBefore)) {
                /* 476 */
                return;
                /*     */
            }
            /*     */
            /*     */
            /* 480 */
            int newReqLine = -1;
            /* 481 */
            for (IAnchor anchor : this.wrappedText.getCurrentPart().getAnchors()) {
                /* 482 */
                if (anchor.getIdentifier() == updateData.reqAnchorId) {
                    /* 483 */
                    newReqLine = this.wrappedText.wrap(anchor).getWrappedLineIndex() + updateData.delta;
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /* 489 */
                    break;
                    /*     */
                }
                /*     */
            }
            /* 492 */
            if (newReqLine >= 0) {
                /* 493 */
                logger.debug("New top line index to %d", new Object[]{Integer.valueOf(newReqLine)});
                /* 494 */
                this.wrappedText.setTopIndex(newReqLine);
                /* 495 */
                this.wrappedText.setHorizontalIndex(horizontalIndex);
                /*     */
            }
            /*     */
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 501 */
            logger.debug("Setting top line index to %d", new Object[]{Integer.valueOf(reqLine)});
            /*     */
            /* 503 */
            this.wrappedText.setTopIndex(reqLine);
            /* 504 */
            this.wrappedText.setHorizontalIndex(horizontalIndex);
            /*     */
            /*     */
            /* 507 */
            this.viewer.updateAnnotationBar();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   void moveCaretWithToplineUpdate(int delta, boolean selecting) {
        /* 512 */
        this.wrappedText.moveCaret(delta, selecting);
        /*     */
        /*     */
        /* 515 */
        int caretLine = this.wrappedText.getLineAtOffset(this.wrappedText.getCaretOffset());
        /* 516 */
        int topIndex = this.wrappedText.getTopIndex();
        /* 517 */
        if (caretLine < topIndex) {
            /* 518 */
            this.wrappedText.setTopIndex(caretLine == 0 ? 0 : caretLine);
            /*     */
        }
        /* 520 */
        else if (caretLine > topIndex + this.wrappedText.getMaxVisibleLineCount()) {
            /* 521 */
            this.wrappedText.setTopIndex(caretLine - this.wrappedText.getMaxVisibleLineCount() + 1);
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
    /*     */   int sanitizeTopLine(int caretLine, boolean selecting, boolean moveViewport)
    /*     */ {
        /* 535 */
        if ((this.wrappedText.hasSelection()) && (!selecting) && (!moveViewport)) {
            /* 536 */
            int lineFirstVisible = this.wrappedText.getTopIndex();
            /* 537 */
            int lineLastVisible = this.wrappedText.getBottomIndex();
            /*     */
            /*     */
            /* 540 */
            removeSelection();
            /*     */
            /*     */
            /* 543 */
            caretLine = this.wrappedText.getLineAtOffset(this.wrappedText.getSelectionRange().x);
            /*     */
            /* 545 */
            if (caretLine < lineFirstVisible) {
                /* 546 */
                this.wrappedText.setTopIndex(caretLine);
                /*     */
            }
            /* 548 */
            else if (caretLine > lineLastVisible) {
                /* 549 */
                this.wrappedText.setTopIndex(caretLine - this.wrappedText.getMaxVisibleLineCount() + 1);
                /*     */
            }
            /*     */
        }
        /* 552 */
        return caretLine;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   void updateDocPart()
    /*     */ {
        /* 560 */
        this.wrappedText.setNavigationMovedCaret(true);
        /*     */
    }

    /*     */
    /*     */   void forceRefresh() {
        /* 564 */
        scroll(0, 0, false, true, true);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   void setCaretViewportPoint(BufferPoint p)
    /*     */ {
        /* 573 */
        setCaretViewportPoint(p, false);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   void setCaretViewportPoint(BufferPoint p, boolean moveViewport)
    /*     */ {
        /* 584 */
        if ((moveViewport) && (this.wrappedText.isCurrentPartLastLineDisplayed()) &&
                /* 585 */       (p.lineIndex > 0))
            /*     */ {
            /* 587 */
            moveViewport = false;
            /*     */
        }
        /*     */
        /* 590 */
        if (moveViewport) {
            /* 591 */
            int caretOffset = this.wrappedText.getCaretOffset();
            /* 592 */
            int caretLine = this.wrappedText.getLineAtOffset(caretOffset);
            /* 593 */
            int caretPosition = caretOffset - this.wrappedText.getOffsetAtLine(caretLine);
            /* 594 */
            this.wrappedText.setTopIndex(caretLine - p.lineIndex);
            /*     */
            /* 596 */
            this.wrappedText.setCaretOffset(this.wrappedText.getOffsetAtLine(this.wrappedText.getCaretLine()) + p.columnOffset);
            /*     */
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 601 */
            int deltaX = this.wrappedText.getHorizontalIndex();
            /* 602 */
            int deltaY = this.wrappedText.getTopIndex();
            /* 603 */
            int lineIndex = deltaY + p.lineIndex;
            /*     */
            /* 605 */
            if ((lineIndex < 0) || (lineIndex >= this.wrappedText.getLineCount())) {
                /* 606 */
                lineIndex = 0;
                /*     */
            }
            /* 608 */
            int offset = this.wrappedText.getOffsetAtLine(lineIndex);
            /* 609 */
            int offsetMax = offset + this.wrappedText.getLineLength(lineIndex);
            /* 610 */
            offset += deltaX + p.columnOffset;
            /* 611 */
            if (offset > offsetMax)
                /*     */ {
                /* 613 */
                logger.debug("Attempting to position caret beyond EOL by %d chars, truncating", new Object[]{Integer.valueOf(offset - offsetMax)});
                /* 614 */
                offset = offsetMax;
                /*     */
            }
            /* 616 */
            if ((offset == 0) && (this.wrappedText.getCaretOffset() == 0))
                /*     */ {
                /*     */
                /* 619 */
                this.wrappedText.setCaretWithColumnAffinity(this.wrappedText.getLineLength(0));
                /*     */
            }
            /* 621 */
            this.wrappedText.setCaretWithColumnAffinity(offset);
            /* 622 */
            logger.debug("Set caret at offset %d (deltaY=%d)", new Object[]{Integer.valueOf(offset), Integer.valueOf(deltaY)});
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    protected VisualSelectionUnwrapped getVisualSelection()
    /*     */ {
        /* 628 */
        boolean eol = this.wrappedText.isCaretEol();
        /* 629 */
        if (this.wrappedText.hasSelection()) {
            /* 630 */
            WrappedText.SelectionData sel = this.wrappedText.getSelectionData();
            /* 631 */
            logger.debug("current Selection Start(%s), selection length(%d)", new Object[]{sel.getSelectionStartCoord(),
/* 632 */         Integer.valueOf(sel.getSelectionLength())});
            /* 633 */
            return new VisualSelectionUnwrapped(sel.getSelectionStartCoord(), eol, sel.getSelectionLength());
            /*     */
        }
        /* 635 */
        return new VisualSelectionUnwrapped(this.wrappedText.getCaretCoordinates(), eol);
        /*     */
    }

    /*     */
    /*     */
    protected void setVisualSelection(VisualSelectionUnwrapped selection) {
        /* 639 */
        if (selection.selectionLength != 0) {
            /* 640 */
            this.wrappedText.setSelectionRange(selection.docCoord, selection.eol, selection.selectionLength);
            /*     */
        }
        /*     */
        else {
            /* 643 */
            this.wrappedText.setCaretCoordinates(selection.docCoord, selection.eol);
            /* 644 */
            if ((selection.docCoord != null) && (!selection.docCoord.equals(this.wrappedText.getCaretCoordinates()))) {
                /* 645 */
                logger.error("Restore caret failed: caret are not equals %s:%s", new Object[]{selection.docCoord, this.wrappedText
                        /* 646 */.getCaretCoordinates()});
                /*     */
            }
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
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void removeSelection()
    /*     */ {
        /* 667 */
        this.wrappedText.resetSelection();
        /*     */
    }

    /*     */
    /*     */
    public int getBufferOffset(ICoordinates coord, boolean eol) {
        /* 671 */
        return this.wrappedText.getBufferOffset(coord, eol);
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
    /*     */
    /*     */
    /*     */
    /*     */
    public boolean setVisualPosition(VisualPosition pos)
    /*     */ {
        /* 687 */
        ICoordinates coord = pos.docCoord;
        /* 688 */
        if (coord == null) {
            /* 689 */
            return false;
            /*     */
        }
        /*     */
        /* 692 */
        boolean fetched = false;
        /*     */
        for (; ; )
            /*     */ {
            /* 695 */
            if (this.docManager.getCurrentPart() != null) {
                /* 696 */
                int lineIndex = TextPartUtil.coordinatesToLineIndex(this.docManager.getCurrentPart(), coord);
                /* 697 */
                if (lineIndex < 0)
                    /*     */ {
                    /* 699 */
                    if ((TextPartUtil.isAnchorDisplayed(this.docManager.getCurrentPart(), coord.getAnchorId())) &&
                            /* 700 */             (coord.getLineDelta() > 0)) {
                        /* 701 */
                        lineIndex = this.wrappedText.getLineCount() - 2;
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 705 */
                if (lineIndex >= 0) {
                    /* 706 */
                    int columnOffset = coord.getColumnOffset();
                    /* 707 */
                    BufferPoint p = this.docManager.wrap(new UnwrappedBufferPoint(columnOffset, lineIndex, false));
                    /* 708 */
                    if (p == null) {
                        /* 709 */
                        return false;
                        /*     */
                    }
                    /*     */
                    /* 712 */
                    int wrappedLineIndex = p.lineIndex;
                    /* 713 */
                    int wrappedColumnOffset = p.columnOffset;
                    /*     */
                    /* 715 */
                    int offset = this.wrappedText.getOffsetAtLine(wrappedLineIndex) + wrappedColumnOffset;
                    /*     */
                    /* 717 */
                    int top = this.wrappedText.getTopIndex();
                    /* 718 */
                    int bottom = this.wrappedText.getBottomIndex();
                    /*     */
                    try {
                        /* 720 */
                        this.textWidget.setRedraw(false);
                        /* 721 */
                        this.wrappedText.setCaretOffset(offset);
                        /* 722 */
                        if ((wrappedLineIndex < top) || (wrappedLineIndex > bottom) || (fetched)) {
                            /* 723 */
                            int index = wrappedLineIndex - (bottom - top) / 2;
                            /* 724 */
                            this.wrappedText.setTopIndex(index);
                            /*     */
                        }
                        /*     */
                        /* 727 */
                        if (pos.viewportCoord != null)
                            /*     */ {
                            /*     */
                            /* 730 */
                            setCaretViewportPoint(pos.viewportCoord, true);
                            /*     */
                        }
                        /*     */
                    }
                    /*     */ finally {
                        /* 734 */
                        this.textWidget.setRedraw(true);
                        /*     */
                    }
                    /* 736 */
                    this.viewer.updateAnnotationBar();
                    /*     */
                    /* 738 */
                    return true;
                    /*     */
                }
                /*     */
            }
            /*     */
            /* 742 */
            if (fetched) {
                /* 743 */
                return false;
                /*     */
            }
            /*     */
            /*     */
            /* 747 */
            viewAtAnchor(coord.getAnchorId());
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /* 753 */
            fetched = true;
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\ScrollBufferManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */