
package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.rcpclient.extensions.SwtRegistry;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;

public class NodeContentsText
        extends Composite
        implements IGraphNodeContents {
    private static final ILogger logger = GlobalLog.getLogger(NodeContentsText.class);
    private static final int minFontHeight = 1;
    private static final int maxFontHeight = 80;
    private int defaultFontHeight;
    private StyledText w;

    public NodeContentsText(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout());
        setBackgroundMode(2);
        this.w = new StyledText(this, 0);
        Color bgcol = SwtRegistry.getInstance().getColor(14737632);
        setBackground(bgcol);
        FontData[] fdlist = getFont().getFontData();
        this.defaultFontHeight = fdlist[0].getHeight();
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
            }
        });
    }

    public boolean setFocus() {
        return this.w.setFocus();
    }

    public void setFont(Font font) {
        super.setFont(font);
        this.w.setFont(font);
    }

    public Font getFont() {
        return this.w.getFont();
    }

    public void setForeground(Color color) {
        super.setForeground(color);
        this.w.setForeground(color);
    }

    public Color getForeground() {
        return this.w.getForeground();
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        this.w.setBackground(color);
    }

    public Color getBackground() {
        return this.w.getBackground();
    }

    public void setText(String text) {
        this.w.setText(text);
    }

    public String getText() {
        return this.w.getText();
    }

    public void setEditable(boolean editable) {
        this.w.setEditable(editable);
    }

    public void setCaret(Caret caret) {
        this.w.setCaret(caret);
    }

    public Caret getCaret() {
        return this.w.getCaret();
    }

    public int getZoomLevel() {
        return 0;
    }

    public boolean applyZoom(int zoom, boolean dryRun) {
        return false;
    }
}


