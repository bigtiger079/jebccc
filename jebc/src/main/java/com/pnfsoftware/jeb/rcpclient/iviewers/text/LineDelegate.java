package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.IActionableTextItem;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.core.output.text.ITextMark;
import com.pnfsoftware.jeb.core.output.text.impl.TextItem;
import com.pnfsoftware.jeb.util.base.CharSequenceList;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineDelegate implements ILine {
    private static final ILogger logger = GlobalLog.getLogger(LineDelegate.class);
    private ILine line;
    private CharSequence text;
    List<ITextItem> items = new ArrayList();

    public LineDelegate(ILine line, int maxCharsPerLine, int charsEndLine) {
        this.line = line;
        this.text = CharSequenceList.getLine(line.getText(), maxCharsPerLine, charsEndLine);
        int lineLength = line.getText().length();
        for (ITextItem item : line.getItems()) {
            int length = item.getLength();
            int offset = item.getOffset();
            int endOfFirstSection = maxCharsPerLine - charsEndLine - 5;
            int startEndSection = lineLength - charsEndLine;
            int itemEndOffset = item.getOffset() + item.getLength();
            if (item.getOffset() < endOfFirstSection) {
                if (itemEndOffset >= endOfFirstSection) {
                    if (itemEndOffset >= startEndSection) {
                        length = endOfFirstSection - item.getOffset() + 5 + (itemEndOffset - startEndSection);
                    } else {
                        length = endOfFirstSection - item.getOffset();
                    }
                    this.items.add(buildNewItem(offset, length, (IActionableTextItem) item));
                } else {
                    this.items.add(item);
                }
            } else if (itemEndOffset >= startEndSection) {
                if (item.getOffset() < startEndSection) {
                    offset = maxCharsPerLine - charsEndLine;
                    length -= startEndSection - item.getOffset();
                } else {
                    offset = maxCharsPerLine - charsEndLine + (item.getOffset() - startEndSection);
                }
                this.items.add(buildNewItem(offset, length, (IActionableTextItem) item));
            }
        }
    }

    private static TextItem buildNewItem(int offset, int length, IActionableTextItem item) {
        return new TextItem(offset, length, item.getClassId(), item.getItemId(), item.getItemFlags());
    }

    public CharSequence getText() {
        return this.text;
    }

    public List<? extends ITextItem> getItems() {
        return this.items;
    }

    public List<? extends ITextMark> getMarks() {
        logger.error("Marks retrieval in delegated lines is currently not implemented; will not fail, returning empty list instead", new Object[0]);
        return Collections.emptyList();
    }

    public ILine getDelegate() {
        return this.line;
    }
}


