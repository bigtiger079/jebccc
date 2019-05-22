package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.IAnchor;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.core.output.text.TextPartUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentManager {
    private final ITextDocument doc;
    private ITextDocumentPart docPart;
    private boolean displayEolAtEod;
    public static final int defaultCharactersWrap = 80;

    static class RenderedItem {
        public ITextItem item;
        public int offset;

        public RenderedItem(ITextItem item, int offset) {
            this.item = item;
            this.offset = offset;
        }
    }

    private int hintMaxCharsPerLine = -1;
    private int maxDeltaForWSP = 10;
    private String wrapIndentString = "        ";
    private List<int[]> wrappedData = new ArrayList<>();
    private Map<Integer, List<Integer>> wrapIndexes = new HashMap();
    private String docText;
    private List<RenderedItem> renderedItems = new ArrayList<>();

    public DocumentManager(ITextDocument doc) {
        this(doc, false);
    }

    public DocumentManager(ITextDocument doc, boolean displayEolAtEod) {
        this.doc = doc;
        this.displayEolAtEod = displayEolAtEod;
    }

    public void setCharactersWrap(int charactersWrap) {
        if (charactersWrap == 0) {
            charactersWrap = 80;
        }
        this.hintMaxCharsPerLine = charactersWrap;
    }

    public int getCharactersWrap() {
        return this.hintMaxCharsPerLine;
    }

    public ITextDocumentPart getCurrentPart() {
        return this.docPart;
    }

    public void setCurrentPart(ITextDocumentPart docPart) {
        this.docPart = docPart;
        this.docText = buildTextInternal();
    }

    public String getText() {
        return this.docText;
    }

    public ITextDocument getDocument() {
        return this.doc;
    }

    public boolean isSingleAnchorDocument() {
        return this.doc.getAnchorCount() == 1L;
    }

    public long getAnchorRange() {
        return this.doc.getAnchorCount();
    }

    public long getAnchorFirst() {
        return this.doc.getFirstAnchor();
    }

    public long getAnchorEnd() {
        return this.doc.getFirstAnchor() + this.doc.getAnchorCount();
    }

    public ITextDocumentPart getPart(long anchorId, int linesAfter) {
        return this.doc.getDocumentPart(anchorId, linesAfter);
    }

    public ITextDocumentPart getPart(long anchorId, int linesAfter, int linesBefore) {
        return this.doc.getDocumentPart(anchorId, linesAfter, linesBefore);
    }

    public IAnchor getAnchorById(long anchorId) {
        if (getCurrentPart() == null) {
            return null;
        }
        return TextPartUtil.getAnchorById(getCurrentPart(), anchorId);
    }

    public int unwrapLine(int wrappedLineIndex) {
        if ((wrappedLineIndex < 0) || (wrappedLineIndex >= this.wrappedData.size())) {
            return -1;
        }
        return this.wrappedData.get(wrappedLineIndex)[0];
    }

    public UnwrappedBufferPoint unwrap(BufferPoint p) {
        int wrappedLineIndex = p.lineIndex;
        if ((wrappedLineIndex < 0) || (wrappedLineIndex >= this.wrappedData.size())) {
            return null;
        }
        int[] data = this.wrappedData.get(wrappedLineIndex);
        int lineIndex = data[0];
        int wrappedColumnOffset = p.columnOffset;
        boolean eol = wrappedColumnOffset == data[2];
        int columnOffset;
        if (data[1] == 0) {
            columnOffset = wrappedColumnOffset;
        } else {
            int currentWrapIndentSize = data[3];
            columnOffset = data[1] + Math.max(currentWrapIndentSize, wrappedColumnOffset) - currentWrapIndentSize;
        }
        return new UnwrappedBufferPoint(columnOffset, lineIndex, eol);
    }

    public int wrapLine(int lineIndex) {
        BufferPoint p = wrap(new UnwrappedBufferPoint(0, lineIndex, false));
        if (p == null) {
            return -1;
        }
        return p.lineIndex;
    }

    public BufferPoint wrap(UnwrappedBufferPoint p) {
        int lineIndex = p.lineIndex;
        int columnOffset = p.columnOffset;
        boolean eol = p.eol;
        List<Integer> indexes = this.wrapIndexes.get(p.lineIndex);
        if ((indexes == null) || (indexes.isEmpty())) {
            return null;
        }
        int i = (Integer) indexes.get(0);
        int[] data = this.wrappedData.get(i);
        if ((isEmptyLine(indexes)) || (indexes.size() == 1) || (columnOffset < data[2]) || ((eol) && (columnOffset == data[2]))) {
            return new BufferPoint(columnOffset, i);
        }
        for (int j = 1; j < indexes.size(); j++) {
            i = (Integer) indexes.get(j);
            data = this.wrappedData.get(i);
            if (data[0] != lineIndex) {
                break;
            }
            if ((columnOffset >= data[1]) && ((columnOffset < data[2]) || ((eol) && (columnOffset == data[2])))) {
                return new BufferPoint(columnOffset - data[1] + data[3], i);
            }
        }
        return null;
    }

    private boolean isEmptyLine(List<Integer> indexes) {
        int i = (Integer) indexes.get(0);
        int[] data = this.wrappedData.get(i);
        return (indexes.size() == 1) && (data.length == 3);
    }

    List<RenderedItem> getRenderedItems() {
        return this.renderedItems;
    }

    public int getLineCount() {
        return this.docPart.getLines().size();
    }

    public int getWrappedLineCount() {
        return this.wrappedData.size();
    }

    private String buildTextInternal() {
        this.wrappedData.clear();
        this.renderedItems.clear();
        this.wrapIndexes.clear();
        boolean appendTrailingEol = false;
        if (this.displayEolAtEod) {
            IAnchor lastAnchor = this.docPart.getAnchors().get(this.docPart.getAnchors().size() - 1);
            appendTrailingEol = lastAnchor.getIdentifier() >= getAnchorEnd() - 1L;
        }
        return buildText(this.docPart.getLines(), appendTrailingEol);
    }

    public String buildText(List<? extends ILine> lines) {
        return buildText(lines, true);
    }

    private String buildText(List<? extends ILine> lines, boolean appendTrailingEol) {
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < lines.size(); index++) {
            ILine line = lines.get(index);
            CharSequence text = line.getText();
            String currentWrapIndentString = this.wrapIndentString;
            int currentWrapIndentSize = -1;
            if (text.length() == 0) {
                addIndex(index, this.wrappedData.size());
                this.wrappedData.add(new int[]{index, 0, 0});
            } else {
                int pos = 0;
                while (pos < text.length()) {
                    int pos2;
                    if (this.hintMaxCharsPerLine > 0) {
                        pos2 = pos + this.hintMaxCharsPerLine;
                    } else {
                        pos2 = text.length();
                    }
                    if (pos2 < text.length()) {
                        int wspPos = findWhiteSpace(text, pos2);
                        if ((wspPos >= 0) && (wspPos - pos2 <= this.maxDeltaForWSP)) {
                            pos2 = wspPos;
                        }
                    }
                    int itemThreshold;
                    if (pos2 >= text.length()) {
                        pos2 = text.length();
                    } else {
                        itemThreshold = -1;
                        int nextItemThreshold = -1;
                        List<? extends ITextItem> items = line.getItems();
                        for (int i = 0; i < items.size(); i++) {
                            ITextItem item = items.get(i);
                            if ((pos2 >= item.getOffset()) && (pos2 < item.getOffset() + item.getLength())) {
                                itemThreshold = item.getOffset() + item.getLength();
                                nextItemThreshold = i + 1 < items.size() ? items.get(i + 1).getOffset() : -1;
                                pos2 = findWhiteSpace(text, itemThreshold, nextItemThreshold);
                                if (pos2 < 0) {
                                    pos2 = itemThreshold;
                                }
                            } else {
                                if (pos2 < item.getOffset()) {
                                    break;
                                }
                            }
                        }
                    }
                    if (pos > 0) {
                        if (currentWrapIndentSize < 0) {
                            currentWrapIndentString = extractEmptyPrefix(text) + this.wrapIndentString;
                            currentWrapIndentSize = currentWrapIndentString.length();
                        }
                        sb.append('\n');
                        sb.append(currentWrapIndentString);
                    }
                    for (ITextItem item : line.getItems()) {
                        if (item.getOffset() >= pos) {
                            if (item.getOffset() >= pos2) {
                                break;
                            }
                            int renderedItemOffset = sb.length() + (item.getOffset() - pos);
                            this.renderedItems.add(new RenderedItem(item, renderedItemOffset));
                        }
                    }
                    sb.append(text.subSequence(pos, pos2));
                    addIndex(index, this.wrappedData.size());
                    this.wrappedData.add(new int[]{index, pos, pos2, currentWrapIndentSize});
                    pos = pos2;
                }
            }
            if ((index + 1 < lines.size()) || (appendTrailingEol)) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private void addIndex(int key, int index) {
        List<Integer> indexes = this.wrapIndexes.get(key);
        if (indexes == null) {
            indexes = new ArrayList<>();
            this.wrapIndexes.put(key, indexes);
        }
        indexes.add(index);
    }

    private int findWhiteSpace(CharSequence text, int pos) {
        return findWhiteSpace(text, pos, -1);
    }

    private int findWhiteSpace(CharSequence text, int pos, int end) {
        if ((end < 0) || (end > text.length())) {
            end = text.length();
        }
        while (pos < end) {
            char c = text.charAt(pos);
            if ((c == ' ') || (c == '\t')) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    private CharSequence extractEmptyPrefix(CharSequence text) {
        int pos = 0;
        while (pos < text.length()) {
            char c = text.charAt(pos);
            if ((c != ' ') && (c != '\t')) {
                break;
            }
            pos++;
        }
        return text.subSequence(0, pos);
    }
}


