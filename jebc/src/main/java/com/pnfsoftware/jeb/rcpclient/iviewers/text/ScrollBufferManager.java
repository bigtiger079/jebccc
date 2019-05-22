package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.IAnchor;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedAnchor;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedText;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.custom.StyledText;

public class ScrollBufferManager {
    private static final ILogger logger = GlobalLog.getLogger(ScrollBufferManager.class, Integer.MAX_VALUE);
    private WrappedText wrappedText;
    private INavigableViewer viewer;
    private DocumentManager docManager;
    private StyledText textWidget;

    public ScrollBufferManager(WrappedText wrappedText, INavigableViewer viewer) {
        this.wrappedText = wrappedText;
        this.viewer = viewer;
        this.docManager = wrappedText.getDocumentManager();
        this.textWidget = wrappedText.getTextWidget();
    }

    WrappedText getWrappedText() {
        return this.wrappedText;
    }

    public void viewAtAnchor(long anchorId) {
        viewAtAnchor(anchorId, false);
    }

    public void viewAtAnchor(long anchorId, boolean doNotSetCaret) {
        int reqAfter = 2 * this.wrappedText.getMaxVisibleLineCount() + 1;
        int reqBefore = 2 * this.wrappedText.getMaxVisibleLineCount();
        viewAtAnchor(anchorId, reqAfter, reqBefore, doNotSetCaret);
    }

    private void viewAtAnchor(long anchorId, int reqAfter, int reqBefore, boolean doNotSetCaret) {
        if (isSinglePartLoaded()) {
            return;
        }
        long t0 = System.currentTimeMillis();
        logger.debug("viewAtAnchor(%d)", anchorId);
        boolean goToEndOfFile = false;
        if (anchorId < this.wrappedText.getAnchorFirst()) {
            anchorId = 0L;
        } else if (anchorId >= this.wrappedText.getAnchorEnd()) {
            anchorId = this.wrappedText.getAnchorEnd() - 1L;
            goToEndOfFile = true;
        }
        WrappedAnchor currentAnchor = null;
        ITextDocumentPart part = this.wrappedText.getCurrentPart();
        if (part != null) {
            currentAnchor = this.wrappedText.wrap(TextPartUtil.getApproximateAnchorById(part, anchorId, 0));
        }
        if (currentAnchor != null) {
            anchorId = currentAnchor.getIdentifier();
            if ((!goToEndOfFile) && (this.wrappedText.getTopIndex() == currentAnchor.getWrappedLineIndex())) {
                return;
            }
        }
        BufferPoint caret = this.wrappedText.getCaretViewportPoint();
        if ((caret.lineIndex >= this.wrappedText.getMaxVisibleLineCount()) || (caret.lineIndex < 0)) {
            caret.lineIndex = 0;
        }
        try {
            this.textWidget.setRedraw(false);
            if (!this.viewer.updateDocument(anchorId, reqAfter, reqBefore)) {
                return;
            }
            if (!goToEndOfFile) {
                currentAnchor = null;
                part = this.wrappedText.getCurrentPart();
                if (part != null) {
                    currentAnchor = this.wrappedText.wrap(TextPartUtil.getNearestAnchorById(part, anchorId));
                }
                this.wrappedText.setTopIndex(currentAnchor);
                this.textWidget.update();
            } else {
                logger.debug("scrolling to EOF");
                viewAtAnchor(anchorId, doNotSetCaret);
                this.wrappedText.setTopIndex(this.wrappedText.getLineCount());
            }
            if (!doNotSetCaret) {
                setCaretViewportPoint(caret);
            }
        } finally {
            this.textWidget.setRedraw(true);
        }
        long t1 = System.currentTimeMillis();
        logger.debug("viewAtAnchor took %dms", t1 - t0);
    }

    private boolean isSinglePartLoaded() {
        return (this.docManager.getDocument().getAnchorCount() == 1L) && (this.docManager.getCurrentPart() != null);
    }

    public void viewAtBufferLine(int reqLine) {
        int delta = reqLine - this.wrappedText.getTopIndex();
        scroll(delta, delta, false, true, false);
    }

    public void viewAtStartOfDocument() {
        viewAtAnchor(this.docManager.getDocument().getFirstAnchor());
        this.wrappedText.setCaretOffset(0);
    }

    public void viewAtEndOfDocument() {
        viewAtAnchor(this.docManager.getAnchorEnd());
        this.wrappedText.setCaretOffset(this.wrappedText.getCharCount());
    }

    public void scroll(int delta) {
        scroll(delta, delta, false, true, false);
    }

    public void scroll(int windowScroll, int caretScroll, boolean selecting, boolean lockCaretOnBounds) {
        scroll(windowScroll, caretScroll, selecting, lockCaretOnBounds, false);
    }

    public void scroll(int windowScroll, int caretScroll, boolean selecting, boolean lockCaretOnBounds, boolean forceFetch) {
        if (this.wrappedText.getCurrentPart() == null) {
            return;
        }
        logger.debug("scroll: window(%d) caret(%d) selecting(%b) lockCaretOnBounds(%b) forceFetch(%b)", windowScroll, caretScroll, selecting, lockCaretOnBounds, forceFetch);
        ICoordinates oldTopLine = this.wrappedText.getTopIndexCoordinates();
        VisualSelectionUnwrapped caret = getVisualSelection();
        Integer reqLine = this.wrappedText.getTopIndex() + windowScroll;
        if ((reqLine < 0) || (reqLine >= this.wrappedText.getLineCount())) {
            forceFetch = false;
        }
        if (!forceFetch) {
            reqLine = sanitizeScroll(reqLine);
        }
        UpdateDocumentData updateData = null;
        if (reqLine != null) {
            if ((!forceFetch) && (isSinglePartLoaded())) {
                updateData = new UpdateDocumentData(-1L, 0, 0, 0);
            } else {
                updateData = getUpdateData(reqLine, forceFetch, selecting);
            }
        }
        try {
            this.textWidget.setRedraw(false);
            if (updateData != null) {
                updateDocument(reqLine, updateData);
            }
            setVisualSelection(caret);
            ICoordinates newTopLine = this.wrappedText.getTopIndexCoordinates();
            if ((windowScroll != 0) && (newTopLine != null) && (newTopLine.equals(oldTopLine)) && (lockCaretOnBounds)) {
                return;
            }
            if (caretScroll != 0) {
                moveCaretWithToplineUpdate(caretScroll, selecting);
            }
        } finally {
            this.textWidget.setRedraw(true);
        }
        if (selecting) {
            WrappedText.SelectionData sel = this.wrappedText.getSelectionData();
            logger.debug("New Selection Start(%s), selection length(%d)", sel.getSelectionStartCoord(), sel.getSelectionLength());
            if (caretScroll < 0) {
                this.wrappedText.forceSelectionOffset(0);
            }
        } else {
            ICoordinates newCaret = this.wrappedText.getCaretCoordinates();
            logger.debug("oldCaret=%s, newCaret=%s", caret.docCoord, newCaret);
        }
    }

    private static class MaxLine {
        public int before;
        public int after;

        public MaxLine(int maxlinecnt) {
            this.before = maxlinecnt;
            this.after = maxlinecnt;
        }
    }

    private Integer sanitizeScroll(int reqLine) {
        int bufferLineCount = this.wrappedText.getLineCount();
        int maxlineCount = this.wrappedText.getMaxVisibleLineCount();
        if ((this.wrappedText.isAnchorFirstDisplayed()) && (this.wrappedText.getTopIndex() == 0)) {
            if (bufferLineCount >= 2 * maxlineCount) {
                if (reqLine < 0) {
                    logger.debug("Top document reached");
                    return null;
                }
            }
        }
        if ((this.wrappedText.isAnchorEndDisplayed()) && (this.wrappedText.getTopIndex() < reqLine)) {
            int reqMaxAllowed = this.wrappedText.getWrappedLineCount() - maxlineCount + 1;
            if (reqLine > reqMaxAllowed) {
                reqLine = reqMaxAllowed;
            }
            if (this.wrappedText.isCurrentPartLastLineDisplayed()) {
                logger.debug("Bottom document reached");
                return null;
            }
        }
        return reqLine;
    }

    private static class UpdateDocumentData {
        long reqAnchorId;
        int reqBefore;
        int reqAfter;
        int delta;

        public UpdateDocumentData(long reqAnchorId, int reqBefore, int reqAfter, int delta) {
            this.reqAnchorId = reqAnchorId;
            this.reqBefore = reqBefore;
            this.reqAfter = reqAfter;
            this.delta = delta;
        }
    }

    private UpdateDocumentData getUpdateData(int reqLine, boolean forceFetch, boolean selecting) {
        int bufferLineCount = this.wrappedText.getLineCount();
        int maxVisibleLineCount = this.wrappedText.getMaxVisibleLineCount();
        int maxlineCount = 2 * this.wrappedText.getMaxVisibleLineCount();
        UpdateDocumentData updateData = new UpdateDocumentData(-1L, 0, 0, 0);
        if (forceFetch) {
            BufferPoint p = this.wrappedText.unwrap(new BufferPoint(0, reqLine));
            if (p == null) {
                return null;
            }
            int line = p.lineIndex;
            WrappedAnchor a = this.wrappedText.getAnchorAtLine(line);
            int anchorLineIndex = a.getWrappedLineIndex();
            updateData.reqAnchorId = a.getIdentifier();
            updateData.reqAfter = (reqLine - anchorLineIndex + maxlineCount);
            updateData.reqBefore = maxlineCount;
            updateData.delta = (reqLine - anchorLineIndex);
        } else {
            WrappedAnchor a = null;
            MaxLine maxLine = new MaxLine(maxlineCount);
            if ((this.wrappedText.hasSelection()) || (selecting)) {
                WrappedText.SelectionData sel = this.wrappedText.getSelectionData();
                int selStartLine = sel.getStartLine();
                int selEndLine = sel.getEndLine();
                a = this.wrappedText.getAnchorAtBufferLine(selStartLine);
                maxLine.after = (selEndLine - selStartLine + maxlineCount);
            }
            if (((reqLine < 0) || (reqLine - maxVisibleLineCount < 0)) && (!this.wrappedText.isAnchorFirstDisplayed())) {
                logger.debug("Requesting more data backward");
                if (a == null) {
                    a = this.wrappedText.getFirstVisibleAnchor();
                }
                int anchorLineIndex = a.getWrappedLineIndex();
                updateData.reqAnchorId = a.getIdentifier();
                updateData.reqAfter = (Math.max(reqLine - anchorLineIndex, 0) + maxLine.after);
                updateData.reqBefore = Math.max(maxLine.before, anchorLineIndex - reqLine);
                updateData.delta = (reqLine - anchorLineIndex);
            } else if ((reqLine + maxlineCount >= bufferLineCount) && (!this.wrappedText.isAnchorEndDisplayed())) {
                logger.debug("Requesting more data forward, maxlinecntAfter(%d), bufferLineCount(%d)", maxLine.after, bufferLineCount);
                if (a == null) {
                    a = this.wrappedText.getEndVisibleAnchor();
                    if (a == null) {
                        logger.i("");
                    }
                }
                int anchorLineIndex = a.getWrappedLineIndex();
                updateData.reqAnchorId = a.getIdentifier();
                updateData.reqAfter = (Math.max(reqLine - anchorLineIndex, 0) + maxLine.after);
                updateData.reqBefore = maxLine.before;
                updateData.delta = (reqLine - anchorLineIndex);
            }
        }
        return updateData;
    }

    private void updateDocument(int reqLine, UpdateDocumentData updateData) {
        int horizontalIndex = this.wrappedText.getHorizontalIndex();
        if (updateData.reqAnchorId >= 0L) {
            if (!this.viewer.updateDocument(updateData.reqAnchorId, updateData.reqAfter, updateData.reqBefore)) {
                return;
            }
            int newReqLine = -1;
            for (IAnchor anchor : this.wrappedText.getCurrentPart().getAnchors()) {
                if (anchor.getIdentifier() == updateData.reqAnchorId) {
                    newReqLine = this.wrappedText.wrap(anchor).getWrappedLineIndex() + updateData.delta;
                    break;
                }
            }
            if (newReqLine >= 0) {
                logger.debug("New top line index to %d", newReqLine);
                this.wrappedText.setTopIndex(newReqLine);
                this.wrappedText.setHorizontalIndex(horizontalIndex);
            }
        } else {
            logger.debug("Setting top line index to %d", reqLine);
            this.wrappedText.setTopIndex(reqLine);
            this.wrappedText.setHorizontalIndex(horizontalIndex);
            this.viewer.updateAnnotationBar();
        }
    }

    void moveCaretWithToplineUpdate(int delta, boolean selecting) {
        this.wrappedText.moveCaret(delta, selecting);
        int caretLine = this.wrappedText.getLineAtOffset(this.wrappedText.getCaretOffset());
        int topIndex = this.wrappedText.getTopIndex();
        if (caretLine < topIndex) {
            this.wrappedText.setTopIndex(caretLine);
        } else if (caretLine > topIndex + this.wrappedText.getMaxVisibleLineCount()) {
            this.wrappedText.setTopIndex(caretLine - this.wrappedText.getMaxVisibleLineCount() + 1);
        }
    }

    int sanitizeTopLine(int caretLine, boolean selecting, boolean moveViewport) {
        if ((this.wrappedText.hasSelection()) && (!selecting) && (!moveViewport)) {
            int lineFirstVisible = this.wrappedText.getTopIndex();
            int lineLastVisible = this.wrappedText.getBottomIndex();
            removeSelection();
            caretLine = this.wrappedText.getLineAtOffset(this.wrappedText.getSelectionRange().x);
            if (caretLine < lineFirstVisible) {
                this.wrappedText.setTopIndex(caretLine);
            } else if (caretLine > lineLastVisible) {
                this.wrappedText.setTopIndex(caretLine - this.wrappedText.getMaxVisibleLineCount() + 1);
            }
        }
        return caretLine;
    }

    void updateDocPart() {
        this.wrappedText.setNavigationMovedCaret(true);
    }

    void forceRefresh() {
        scroll(0, 0, false, true, true);
    }

    void setCaretViewportPoint(BufferPoint p) {
        setCaretViewportPoint(p, false);
    }

    void setCaretViewportPoint(BufferPoint p, boolean moveViewport) {
        if ((moveViewport) && (this.wrappedText.isCurrentPartLastLineDisplayed()) && (p.lineIndex > 0)) {
            moveViewport = false;
        }
        if (moveViewport) {
            int caretOffset = this.wrappedText.getCaretOffset();
            int caretLine = this.wrappedText.getLineAtOffset(caretOffset);
            int caretPosition = caretOffset - this.wrappedText.getOffsetAtLine(caretLine);
            this.wrappedText.setTopIndex(caretLine - p.lineIndex);
            this.wrappedText.setCaretOffset(this.wrappedText.getOffsetAtLine(this.wrappedText.getCaretLine()) + p.columnOffset);
        } else {
            int deltaX = this.wrappedText.getHorizontalIndex();
            int deltaY = this.wrappedText.getTopIndex();
            int lineIndex = deltaY + p.lineIndex;
            if ((lineIndex < 0) || (lineIndex >= this.wrappedText.getLineCount())) {
                lineIndex = 0;
            }
            int offset = this.wrappedText.getOffsetAtLine(lineIndex);
            int offsetMax = offset + this.wrappedText.getLineLength(lineIndex);
            offset += deltaX + p.columnOffset;
            if (offset > offsetMax) {
                logger.debug("Attempting to position caret beyond EOL by %d chars, truncating", offset - offsetMax);
                offset = offsetMax;
            }
            if ((offset == 0) && (this.wrappedText.getCaretOffset() == 0)) {
                this.wrappedText.setCaretWithColumnAffinity(this.wrappedText.getLineLength(0));
            }
            this.wrappedText.setCaretWithColumnAffinity(offset);
            logger.debug("Set caret at offset %d (deltaY=%d)", offset, deltaY);
        }
    }

    protected VisualSelectionUnwrapped getVisualSelection() {
        boolean eol = this.wrappedText.isCaretEol();
        if (this.wrappedText.hasSelection()) {
            WrappedText.SelectionData sel = this.wrappedText.getSelectionData();
            logger.debug("current Selection Start(%s), selection length(%d)", sel.getSelectionStartCoord(), sel.getSelectionLength());
            return new VisualSelectionUnwrapped(sel.getSelectionStartCoord(), eol, sel.getSelectionLength());
        }
        return new VisualSelectionUnwrapped(this.wrappedText.getCaretCoordinates(), eol);
    }

    protected void setVisualSelection(VisualSelectionUnwrapped selection) {
        if (selection.selectionLength != 0) {
            this.wrappedText.setSelectionRange(selection.docCoord, selection.eol, selection.selectionLength);
        } else {
            this.wrappedText.setCaretCoordinates(selection.docCoord, selection.eol);
            if ((selection.docCoord != null) && (!selection.docCoord.equals(this.wrappedText.getCaretCoordinates()))) {
                logger.error("Restore caret failed: caret are not equals %s:%s", selection.docCoord, this.wrappedText.getCaretCoordinates());
            }
        }
    }

    public void removeSelection() {
        this.wrappedText.resetSelection();
    }

    public int getBufferOffset(ICoordinates coord, boolean eol) {
        return this.wrappedText.getBufferOffset(coord, eol);
    }

    public boolean setVisualPosition(VisualPosition pos) {
        ICoordinates coord = pos.docCoord;
        if (coord == null) {
            return false;
        }
        boolean fetched = false;
        for (; ; ) {
            if (this.docManager.getCurrentPart() != null) {
                int lineIndex = TextPartUtil.coordinatesToLineIndex(this.docManager.getCurrentPart(), coord);
                if (lineIndex < 0) {
                    if ((TextPartUtil.isAnchorDisplayed(this.docManager.getCurrentPart(), coord.getAnchorId())) && (coord.getLineDelta() > 0)) {
                        lineIndex = this.wrappedText.getLineCount() - 2;
                    }
                }
                if (lineIndex >= 0) {
                    int columnOffset = coord.getColumnOffset();
                    BufferPoint p = this.docManager.wrap(new UnwrappedBufferPoint(columnOffset, lineIndex, false));
                    if (p == null) {
                        return false;
                    }
                    int wrappedLineIndex = p.lineIndex;
                    int wrappedColumnOffset = p.columnOffset;
                    int offset = this.wrappedText.getOffsetAtLine(wrappedLineIndex) + wrappedColumnOffset;
                    int top = this.wrappedText.getTopIndex();
                    int bottom = this.wrappedText.getBottomIndex();
                    try {
                        this.textWidget.setRedraw(false);
                        this.wrappedText.setCaretOffset(offset);
                        if ((wrappedLineIndex < top) || (wrappedLineIndex > bottom) || (fetched)) {
                            int index = wrappedLineIndex - (bottom - top) / 2;
                            this.wrappedText.setTopIndex(index);
                        }
                        if (pos.viewportCoord != null) {
                            setCaretViewportPoint(pos.viewportCoord, true);
                        }
                    } finally {
                        this.textWidget.setRedraw(true);
                    }
                    this.viewer.updateAnnotationBar();
                    return true;
                }
            }
            if (fetched) {
                return false;
            }
            viewAtAnchor(coord.getAnchorId());
            fetched = true;
        }
    }
}


