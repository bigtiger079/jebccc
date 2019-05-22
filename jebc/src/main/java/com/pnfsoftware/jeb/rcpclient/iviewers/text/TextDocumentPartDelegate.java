package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.IAnchor;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;

import java.util.ArrayList;
import java.util.List;

public class TextDocumentPartDelegate implements ITextDocumentPart {
    private ITextDocumentPart part;
    private List<ILine> lines;

    public TextDocumentPartDelegate(ITextDocumentPart part, int maxCharsPerLine, int charsEndLine) {
        this.part = part;
        this.lines = new ArrayList<>(part.getLines());
        for (int i = 0; i < this.lines.size(); i++) {
            ILine line = this.lines.get(i);
            if (line.getText().length() > maxCharsPerLine) {
                this.lines.remove(i);
                this.lines.add(i, new LineDelegate(line, maxCharsPerLine, charsEndLine));
            }
        }
    }

    public List<ILine> getLines() {
        return this.lines;
    }

    public List<? extends IAnchor> getAnchors() {
        return this.part.getAnchors();
    }
}


