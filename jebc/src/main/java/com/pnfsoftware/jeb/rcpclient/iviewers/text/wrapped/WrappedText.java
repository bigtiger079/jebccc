
package com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped;

import com.pnfsoftware.jeb.core.output.text.IAnchor;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.BufferPoint;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.DocumentManager;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.UnwrappedBufferPoint;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;

import org.eclipse.jface.text.JFaceTextUtil;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class WrappedText {
    private static final ILogger logger = GlobalLog.getLogger(WrappedText.class);
    private DocumentManager docManager;
    private StyledText text;
    private Integer maxVisibleLineCount;

    public class SelectionData {
        private Point boundOffsets;

        public SelectionData() {
            this.boundOffsets = WrappedText.this.text.getSelection();
            this.selectionLength = (WrappedText.this.text.getCaretOffset() != this.boundOffsets.x ? this.boundOffsets.y - this.boundOffsets.x : this.boundOffsets.x - this.boundOffsets.y);
            this.selectionStartCoord = WrappedText.this.getCoordinates(this.selectionLength > 0 ? this.boundOffsets.x : this.boundOffsets.y);
        }

        public int getSelectionLength() {
            return this.selectionLength;
        }

        public ICoordinates getSelectionStartCoord() {
            return this.selectionStartCoord;
        }

        public int getStartLine() {
            return WrappedText.this.text.getLineAtOffset(this.boundOffsets.x);
        }

        public int getEndLine() {
            return WrappedText.this.text.getLineAtOffset(this.boundOffsets.y);
        }

        private int selectionLength;
        private ICoordinates selectionStartCoord;
    }

    private int columnAffinity = -1;
    private boolean navigationMovedCaret = false;

    public WrappedText(DocumentManager docManager, StyledText text) {
        this.docManager = docManager;
        this.text = text;
        text.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                WrappedText.this.maxVisibleLineCount = null;
            }
        });
    }

    public DocumentManager getDocumentManager() {
        return this.docManager;
    }

    public StyledText getTextWidget() {
        return this.text;
    }

    int sanitizeOffset(int bufferOffset) {
        return sanitizeOffset(bufferOffset, true);
    }

    int sanitizeOffset(int bufferOffset, boolean includeEnd) {
        if (bufferOffset < 0) {
            bufferOffset = 0;
        } else {
            int len = this.text.getCharCount();
            if (len == 0) {
                bufferOffset = 0;
            } else if (bufferOffset >= len) {
                bufferOffset = includeEnd ? len : len - 1;
            }
        }
        return bufferOffset;
    }

    int sanitizeLine(int lineIndex) {
        if (lineIndex < 0) {
            lineIndex = 0;
        } else {
            int len = this.text.getLineCount();
            if (len == 0) {
                throw new IllegalStateException("StyledText");
            }
            if (lineIndex >= len) {
                lineIndex = len - 1;
            }
        }
        return lineIndex;
    }

    public int getCaretOffset() {
        return this.text.getCaretOffset();
    }

    public void setCaretOffset(int offset) {
        this.text.setCaretOffset(sanitizeOffset(offset));
    }

    public int getCaretLine() {
        return this.text.getLineAtOffset(this.text.getCaretOffset());
    }

    public int getLineCount() {
        return this.text.getLineCount();
    }

    public int getFullyVisibleLineCount() {
        return this.text.getClientArea().height / this.text.getLineHeight();
    }

    public int getVisibleLineCount() {
        return getBottomIndex() - getTopIndex() + 1;
    }

    public String getLine(int lineIndex) {
        return this.text.getLine(lineIndex);
    }

    public int getCharCount() {
        return this.text.getCharCount();
    }

    public int getTopIndex() {
        return this.text.getTopIndex();
    }

    public int getBottomIndex() {
        return JFaceTextUtil.getBottomIndex(this.text);
    }

    public void setTopIndex(int lineIndex) {
        this.text.setTopIndex(sanitizeLine(lineIndex));
    }

    public int getHorizontalIndex() {
        return this.text.getHorizontalIndex();
    }

    public void setHorizontalIndex(int column) {
        this.text.setHorizontalIndex(column);
    }

    public void setTopIndex(WrappedAnchor anchor) {
        int lineIndex = 0;
        if (anchor != null) {
            lineIndex = anchor.getWrappedLineIndex();
        }
        setTopIndex(lineIndex);
    }

    public ICoordinates getCoordinates(int bufferOffset) {
        int wrappedLineIndex = this.text.getLineAtOffset(bufferOffset);
        int wrappedColumnOffset = bufferOffset - this.text.getOffsetAtLine(wrappedLineIndex);
        BufferPoint p = this.docManager.unwrap(new BufferPoint(wrappedColumnOffset, wrappedLineIndex));
        if (p == null) {
            return null;
        }
        int lineIndex = p.lineIndex;
        int columnOffset = p.columnOffset;
        WrappedAnchor bestAnchor = getAnchorAtLine(lineIndex);
        if (bestAnchor == null) {
            return null;
        }
        int lineDelta = lineIndex - bestAnchor.getUnwrappedLineIndex();
        return new Coordinates(bestAnchor.getIdentifier(), lineDelta, columnOffset);
    }

    public ICoordinates getTopIndexCoordinates() {
        return getCoordinates(this.text.getOffsetAtLine(this.text.getTopIndex()));
    }

    public ICoordinates getCaretCoordinates() {
        return getCoordinates(this.text.getCaretOffset());
    }

    public void setCaretCoordinates(ICoordinates caret, boolean eol) {
        if (caret != null) {
            int caretUnwrappedOffset = getBufferOffset(caret, eol);
            if (caretUnwrappedOffset >= 0) {
                this.text.setCaretOffset(caretUnwrappedOffset);
                return;
            }
        }
        this.text.setCaretOffset(getLastLineOffset());
    }

    public BufferPoint getCaretViewportPoint() {
        int deltaX = getHorizontalIndex();
        int deltaY = getTopIndex();
        int caretOffset = getCaretOffset();
        int caretLine = getLineAtOffset(caretOffset);
        int caretOffsetInLine = caretOffset - getOffsetAtLine(caretLine);
        return new BufferPoint(caretOffsetInLine - deltaX, caretLine - deltaY);
    }

    public int getLastLineOffset() {
        int len = this.text.getLineCount();
        if (len == 0) {
            throw new IllegalStateException("StyledText");
        }
        return this.text.getOffsetAtLine(len - 1);
    }

    public boolean hasSelection() {
        return this.text.getSelectionCount() > 0;
    }

    public void setSelection(int start, int end) {
        this.text.setSelection(start, end);
    }

    public void selectAll() {
        this.text.selectAll();
    }

    public SelectionData getSelectionData() {
        return new SelectionData();
    }

    public Point getSelectionRange() {
        return this.text.getSelectionRange();
    }

    public void setSelectionRange(ICoordinates startCoords, boolean eol, int selectionLength) {
        int selectionStartOffset = 0;
        if (startCoords != null) {
            selectionStartOffset = getBufferOffset(startCoords, eol);
        } else {
            selectionStartOffset = getLastLineOffset();
        }
        if (selectionStartOffset >= 0) {
            setNavigationMovedCaret(true);
            this.text.setSelectionRange(selectionStartOffset, selectionLength);
        }
    }

    public void resetSelection() {
        SelectionData sel = getSelectionData();
        this.text.setSelectionRange(sel.getSelectionLength() >= 0 ? sel.boundOffsets.y : sel.boundOffsets.x, 0);
    }

    public int getMaxVisibleLineCount() {
        if (this.maxVisibleLineCount == null) {
            this.maxVisibleLineCount = Integer.valueOf(this.text.getClientArea().height / this.text.getLineHeight());
            if (this.maxVisibleLineCount.intValue() == 0) {
                this.maxVisibleLineCount = Integer.valueOf(100);
            }
        }
        return this.maxVisibleLineCount.intValue();
    }

    public int getWrappedLineCount() {
        return this.docManager.getWrappedLineCount();
    }

    public ITextDocumentPart getCurrentPart() {
        return this.docManager.getCurrentPart();
    }

    public int getBufferOffset(ICoordinates coord, boolean eol) {
        ITextDocumentPart part = this.docManager.getCurrentPart();
        if (!TextPartUtil.isInsidePart(part, coord)) {
            return -1;
        }
        int index = TextPartUtil.coordinatesToLineIndex(part, coord);
        BufferPoint p = this.docManager.wrap(new UnwrappedBufferPoint(coord.getColumnOffset(), index, eol));
        return this.text.getOffsetAtLine(p.lineIndex) + p.columnOffset;
    }

    public WrappedAnchor wrap(IAnchor anchor) {
        if (anchor == null) {
            return null;
        }
        return new WrappedAnchor(this.docManager, anchor);
    }

    public long getAnchorFirst() {
        return this.docManager.getAnchorFirst();
    }

    public WrappedAnchor getFirstVisibleAnchor() {
        return wrap(TextPartUtil.getFirstAnchor(this.docManager.getCurrentPart()));
    }

    public boolean isAnchorFirst(IAnchor anchor) {
        return anchor.getIdentifier() == this.docManager.getAnchorFirst();
    }

    public boolean isAnchorFirstDisplayed() {
        WrappedAnchor anchor = getFirstVisibleAnchor();
        if (anchor == null) {
            return false;
        }
        return isAnchorFirst(anchor.unwrap());
    }

    public long getAnchorEnd() {
        return this.docManager.getAnchorEnd();
    }

    public WrappedAnchor getEndVisibleAnchor() {
        return wrap(TextPartUtil.getLastAnchor(this.docManager.getCurrentPart()));
    }

    public boolean isAnchorEnd(IAnchor anchor) {
        long anchorEnd = this.docManager.getAnchorEnd();
        List<? extends IAnchor> anchorsDisplayed = this.docManager.getCurrentPart().getAnchors();
        if (((IAnchor) anchorsDisplayed.get(anchorsDisplayed.size() - 1)).getIdentifier() == anchorEnd) {
            return ((IAnchor) anchorsDisplayed.get(anchorsDisplayed.size() - 2)).getIdentifier() == anchor.getIdentifier();
        }
        if (anchorsDisplayed.contains(anchor)) {
            return false;
        }
        return this.docManager.getAnchorEnd() - 1L == anchor.getIdentifier();
    }

    public boolean isAnchorEndDisplayed() {
        ITextDocumentPart part = this.docManager.getCurrentPart();
        if (part == null) {
            return false;
        }
        int cnt = part.getAnchors().size();
        if (cnt == 0) {
            return false;
        }
        IAnchor a = (IAnchor) part.getAnchors().get(cnt - 1);
        return a.getIdentifier() == this.docManager.getAnchorEnd();
    }

    public WrappedAnchor getAnchorAtBufferOffset(int offset) {
        int wrappedLineIndex = this.text.getLineAtOffset(offset);
        return getAnchorAtBufferLine(wrappedLineIndex);
    }

    public WrappedAnchor getAnchorAtBufferLine(int line) {
        int lineIndex = this.docManager.unwrapLine(line);
        if (lineIndex < 0) {
            return null;
        }
        return getAnchorAtLine(lineIndex);
    }

    public WrappedAnchor getAnchorAtLine(int lineIndex) {
        IAnchor anchor = TextPartUtil.getAnchorAtLine(this.docManager.getCurrentPart(), lineIndex);
        if (anchor == null) {
            return null;
        }
        return new WrappedAnchor(this.docManager, anchor);
    }

    public int getLineAtOffset(int offset) {
        offset = sanitizeOffset(offset);
        return this.text.getLineAtOffset(offset);
    }

    public int getOffsetAtLine(int lineIndex) {
        if (lineIndex < 0) {
            return 0;
        }
        if (lineIndex >= this.text.getLineCount()) {
            return this.text.getLineCount() - 1;
        }
        return this.text.getOffsetAtLine(lineIndex);
    }

    public int getOffsetAtTopLine() {
        return getOffsetAtLine(getTopIndex());
    }

    public int getLineLength(int lineIndex) {
        return this.text.getLine(lineIndex).length();
    }

    public UnwrappedBufferPoint unwrap(int offset) {
        offset = sanitizeOffset(offset);
        int wrappedLineIndex = getLineAtOffset(offset);
        int wrappedColumnOffset = offset - getOffsetAtLine(wrappedLineIndex);
        return this.docManager.unwrap(new BufferPoint(wrappedColumnOffset, wrappedLineIndex));
    }

    public UnwrappedBufferPoint unwrap(BufferPoint point) {
        return this.docManager.unwrap(point);
    }

    public BufferPoint wrap(UnwrappedBufferPoint point) {
        return this.docManager.wrap(point);
    }

    public void moveCaret(int delta, boolean selecting) {
        Point sel = this.text.getSelection();
        int currentOffset = sel.x;
        int initialLength = 0;
        int selectionLength = 0;
        if (selecting) {
            if (this.text.getSelectionCount() != 0) {
                selectionLength = this.text.getCaretOffset() != sel.x ? sel.y - sel.x : sel.x - sel.y;
                initialLength = selectionLength;
            }
            if (initialLength > 0) {
                currentOffset = sel.y;
            }
        }
        int currentLine = this.text.getLineAtOffset(currentOffset);
        int lineStartOffset = this.text.getOffsetAtLine(currentLine);
        int columnOffset = currentOffset - lineStartOffset;
        currentOffset = lineStartOffset;
        if (selecting) {
            selectionLength -= columnOffset;
        }
        while (delta != 0) {
            currentLine = this.text.getLineAtOffset(currentOffset);
            if (delta > 0) {
                if (currentLine + 1 >= this.text.getLineCount()) {
                    break;
                }
                currentOffset = this.text.getOffsetAtLine(currentLine + 1);
                if (selecting) {
                    selectionLength += this.text.getLine(currentLine).length() + 1;
                }
                delta--;
            } else {
                if (currentLine - 1 < 0) {
                    break;
                }
                currentOffset = this.text.getOffsetAtLine(currentLine - 1);
                if (selecting) {
                    selectionLength -= this.text.getLine(currentLine - 1).length() + 1;
                }
                delta++;
            }
        }
        currentLine = this.text.getLineAtOffset(currentOffset);
        if (columnOffset > this.text.getLine(currentLine).length()) {
            columnOffset = this.text.getLine(currentLine).length();
        }
        if (selecting) {
            this.text.setSelectionRange(initialLength >= 0 ? sel.x : sel.y, selectionLength + columnOffset);
        } else {
            setCaretWithColumnAffinity(currentOffset + columnOffset);
        }
    }

    public void forceSelectionOffset(int offset) {
        Point sel = this.text.getSelection();
        int selectionLength = 0;
        if (this.text.getSelectionCount() != 0) {
            selectionLength = this.text.getCaretOffset() != sel.x ? sel.y - sel.x : sel.x - sel.y;
        }
        if (selectionLength > 0) {
            this.text.setSelectionRange(sel.x, offset - sel.x);
        } else {
            this.text.setSelectionRange(sel.y, offset - sel.y);
        }
    }

    public void setCaretWithColumnAffinity(int offset) {
        if (this.columnAffinity >= 0) {
            int line = getLineAtOffset(offset);
            int lineStartOffset = getOffsetAtLine(line);
            int currentColumnPosition = offset - lineStartOffset;
            if (currentColumnPosition < this.columnAffinity) {
                offset = lineStartOffset + Math.min(this.columnAffinity, this.text.getLine(line).length());
            }
        }
        setNavigationMovedCaret(true);
        setCaretOffset(offset);
    }

    public boolean isNavigationMovedCaret() {
        return this.navigationMovedCaret;
    }

    public void setNavigationMovedCaret(boolean moved) {
        this.navigationMovedCaret = moved;
    }

    public void updateColumnAffinityFromCaret() {
        int caretOffset = this.text.getCaretOffset();
        int caretLine = this.text.getLineAtOffset(caretOffset);
        int lineStartOffset = this.text.getOffsetAtLine(caretLine);
        this.columnAffinity = (caretOffset - lineStartOffset);
    }

    public boolean updateCaretColumn() {
        if (isNavigationMovedCaret()) {
            setNavigationMovedCaret(false);
            return false;
        }
        updateColumnAffinityFromCaret();
        return true;
    }

    public boolean isCaretEol() {
        int caretOffset = this.text.getCaretOffset();
        if (this.text.getSelectionCount() != 0) {
            SelectionData sel = getSelectionData();
            caretOffset = sel.selectionLength >= 0 ? sel.boundOffsets.x : sel.boundOffsets.y;
        }
        int caretLine = this.text.getLineAtOffset(caretOffset);
        return getLineLength(caretLine) == caretOffset - this.text.getOffsetAtLine(caretLine);
    }

    public boolean isCurrentPartLastLineDisplayed() {
        return getMaxVisibleLineCount() + this.text.getTopIndex() >= this.text.getLineCount();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\wrapped\WrappedText.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */