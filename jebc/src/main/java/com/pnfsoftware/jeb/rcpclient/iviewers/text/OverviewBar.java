package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
import com.pnfsoftware.jeb.core.units.IMetadataGroup;
import com.pnfsoftware.jeb.core.units.IMetadataManager;
import com.pnfsoftware.jeb.core.units.MetadataGroupType;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedText;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import com.pnfsoftware.jeb.util.math.MathUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolTip;

public class OverviewBar extends Canvas {
    private static final ILogger logger = GlobalLog.getLogger(OverviewBar.class);
    private InteractiveTextViewer textViewer;
    private ITextDocument idoc;
    private IMetadataManager metadataManager;
    private boolean vertical;
    private int lastKnownBarSize;
    private List<int[]> arrays = new ArrayList();
    private boolean mouseButtonDown;
    private int mousePosition;
    private boolean moving;
    private ToolTip tip;
    private boolean zoomLevelChanged;
    private int barZoomLevel;
    private long barAnchorFirst;
    private long barAnchorEnd;
    private final int emptyColor = 13684944;
    private final int defaultColor = 6316128;
    private final Color colorZoomRange = UIAssetManager.getInstance().getColor(255, 0, 0);
    private final Color colorPartRange = UIAssetManager.getInstance().getColor(255, 153, 0);
    private final Color colorIndicator = UIAssetManager.getInstance().getColor(255, 255, 0);
    private final Color colorViewport = UIAssetManager.getInstance().getColor(102, 255, 0);
    private boolean ignoreNextNegativeValue;
    private boolean overflowDone;

    public OverviewBar(Composite parent, int styles, ITextDocument idoc, IMetadataManager metadataManager) {
        super(parent, 0x40000 | styles & 0x800);
        if (idoc == null) {
            throw new IllegalArgumentException();
        }
        this.idoc = idoc;
        this.metadataManager = metadataManager;
        this.vertical = ((styles & 0x200) != 0);
        this.barZoomLevel = 0;
        this.barAnchorFirst = idoc.getFirstAnchor();
        this.barAnchorEnd = (idoc.getFirstAnchor() + idoc.getAnchorCount());
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                OverviewBar.this.onPaintControl(e);
            }
        });
        addMouseTrackListener(new MouseTrackListener() {
            public void mouseHover(MouseEvent e) {
                OverviewBar.this.onMouseHover(e);
            }

            public void mouseExit(MouseEvent e) {
                OverviewBar.this.onMouseExit(e);
            }

            public void mouseEnter(MouseEvent e) {
                OverviewBar.this.onMouseEnter(e);
            }
        });
        addMouseListener(new MouseListener() {
            public void mouseUp(MouseEvent e) {
                OverviewBar.this.onMouseUp(e);
            }

            public void mouseDown(MouseEvent e) {
                OverviewBar.this.onMouseDown(e);
            }

            public void mouseDoubleClick(MouseEvent e) {
            }
        });
        addMouseMoveListener(new MouseMoveListener() {
            public void mouseMove(MouseEvent e) {
                OverviewBar.this.onMouseMove(e);
            }
        });
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseScrolled(MouseEvent e) {
                OverviewBar.this.onMouseWheelEvent(e);
            }
        });
        new ContextMenu(this).addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                menuMgr.add(new OverviewBar.ActionZoom(1));
                if (OverviewBar.this.barZoomLevel >= 1) {
                    menuMgr.add(new OverviewBar.ActionZoom(-1));
                    menuMgr.add(new OverviewBar.ActionZoom(0));
                }
            }
        });
    }

    void connectToViewer(InteractiveTextViewer textViewer) {
        this.textViewer = textViewer;
        textViewer.getTextWidget().addCaretListener(new CaretListener() {
            public void caretMoved(CaretEvent e) {
                OverviewBar.this.redraw();
            }
        });
        textViewer.getTextWidget().addMouseWheelListener(new MouseWheelListener() {
            public void mouseScrolled(MouseEvent e) {
                OverviewBar.this.redraw();
            }
        });
    }

    void refresh() {
        this.lastKnownBarSize = -1;
        redraw();
    }

    private void onPaintControl(PaintEvent e) {
        Rectangle client = getClientArea();
        int size = this.vertical ? client.height : client.width;
        int width = this.vertical ? client.width : client.height;
        if ((!this.moving) && ((this.lastKnownBarSize != size) || (this.zoomLevelChanged))) {
            this.lastKnownBarSize = size;
            this.zoomLevelChanged = false;
            buildBars(size);
        }
        drawBars(e.gc);
        if (this.textViewer != null) {
            drawCaretTriangle(e.gc, size);
            drawViewport(e.gc, size, width);
        }
    }

    private void drawCaretTriangle(GC gc, int size) {
        ICoordinates coord = this.textViewer.getCaretCoordinates();
        int pos = 0;
        if (coord != null) {
            pos = coordToPixel(coord, size);
        } else {
            pos = size - 1;
        }
        if ((pos >= 0) && (pos < size)) {
            gc.setForeground(this.colorIndicator);
            gc.setBackground(this.colorIndicator);
            if (!this.vertical) {
                gc.fillPolygon(new int[]{pos - 5, 0, pos + 5, 0, pos, 7});
            } else {
                gc.fillPolygon(new int[]{0, pos - 5, 0, pos + 5, 7, pos});
            }
        }
    }

    private void drawViewport(GC gc, int size, int width) {
        ICoordinates coord = this.textViewer.getWrappedText().getTopIndexCoordinates();
        if (coord != null) {
            int pos = coordToPixel(coord, size);
            if ((pos >= 0) && (pos < size)) {
                gc.setForeground(this.colorViewport);
                gc.setBackground(this.colorViewport);
                drawBar(gc, pos, pos + 2, this.vertical, width);
            }
        }
    }

    private void buildBars(int size) {
        this.arrays.clear();
        if (this.metadataManager != null) {
            for (IMetadataGroup grp : this.metadataManager.getGroups()) {
                int i = -2;
                int[] array = new int[size * 2 + 1];
                for (int pos = 0; pos < size; pos++) {
                    MetadataGroupType grpType = grp.getType();
                    String address = this.idoc.coordinatesToAddress(pixelToCoord(pos, size), AddressConversionPrecision.COARSE);
                    Object o = grp.getData(address);
                    int rgb = determineColor(o, grpType);
                    if ((i < 0) || (array[(i + 1)] != rgb)) {
                        i += 2;
                        array[i] = 1;
                        array[(i + 1)] = rgb;
                    } else {
                        array[i] += 1;
                    }
                }
                array[(i + 2)] = -1;
                this.arrays.add(array);
            }
        }
        if (this.arrays.isEmpty()) {
            int i = -2;
            int[] array = new int[size * 2 + 1];
            int rgb = determineColor(null, null);
            for (int pos = 0; pos < size; pos++) {
                if ((i < 0) || (array[(i + 1)] != rgb)) {
                    i += 2;
                    array[i] = 1;
                    array[(i + 1)] = rgb;
                } else {
                    array[i] += 1;
                }
            }
            array[(i + 2)] = -1;
            this.arrays.add(array);
        }
    }

    private int determineColor(Object object, MetadataGroupType grpType) {
        if (object == null) {
            return 13684944;
        }
        switch (grpType) {
            case CLASSID:
                if (((object instanceof Integer)) && (this.textViewer != null) && (this.textViewer.styleAdapter != null)) {
                    int id = ((Integer) object).intValue();
                    ItemClassIdentifiers classId = ItemClassIdentifiers.getById(id);
                    Style s = this.textViewer.styleAdapter.getStyle(classId, false);
                    if ((s != null) && (s.getColor() != null)) {
                        RGB r = s.getColor().getRGB();
                        return r.red << 16 | r.green << 8 | r.blue;
                    }
                }
                return 6316128;
            case RGB:
                if ((object instanceof Integer)) {
                    return ((Integer) object).intValue();
                }
                return 6316128;
        }
        return 6316128;
    }

    private void drawBars(GC gc) {
        Rectangle ca = getClientArea();
        int fillsize = this.vertical ? ca.width : ca.height;
        int maxSize = this.vertical ? ca.height : ca.width;
        int x1 = -1;
        int x2 = 0;
        for (int[] array : this.arrays) {
            x1 = x2;
            x2 = x1 + fillsize / this.arrays.size();
            int y = 0;
            int i = 0;
            for (; ; ) {
                int n = array[i];
                if (n < 0) {
                    break;
                }
                Color c = UIAssetManager.getInstance().getColor(array[(i + 1)]);
                gc.setForeground(c);
                if (n == 1) {
                    drawLine(gc, x1, x2, y);
                    y++;
                } else {
                    gc.setBackground(c);
                    if (this.vertical) {
                        gc.fillRectangle(x1, y, x2 - x1, n);
                    } else {
                        gc.fillRectangle(y, x1, n, x2 - x1);
                    }
                    y += n;
                }
                i += 2;
            }
        }
        drawPartRange(gc, maxSize);
        drawZoomRange(gc, maxSize);
    }

    private void drawPartRange(GC gc, int size) {
        ITextDocumentPart part = this.textViewer.getCurrentDocumentPart();
        if (part != null) {
            long partAnchor0 = TextPartUtil.getFirstAnchorId(part);
            long partAnchor1 = TextPartUtil.getNextAnchorId(part);
            gc.setBackground(this.colorPartRange);
            long x0 = (partAnchor0 - this.idoc.getFirstAnchor()) * size / this.idoc.getAnchorCount();
            long x1 = (partAnchor1 - this.idoc.getFirstAnchor()) * size / this.idoc.getAnchorCount();
            drawBar(gc, x0, x1);
        }
    }

    private void drawZoomRange(GC gc, int size) {
        if (this.barZoomLevel != 0) {
            gc.setBackground(this.colorZoomRange);
            long x0 = (this.barAnchorFirst - this.idoc.getFirstAnchor()) * size / this.idoc.getAnchorCount();
            long x1 = (this.barAnchorEnd - this.idoc.getFirstAnchor()) * size / this.idoc.getAnchorCount();
            drawBar(gc, x0, x1);
        }
    }

    private void drawBar(GC gc, long x0, long x1) {
        drawBar(gc, x0, x1, this.vertical, 2);
    }

    private void drawBar(GC gc, long x0, long x1, boolean vertical, int length) {
        if (vertical) {
            gc.fillRectangle(0, (int) x0, length, (int) (x1 - x0));
        } else {
            gc.fillRectangle((int) x0, 0, (int) (x1 - x0), length);
        }
    }

    private void drawLine(GC gc, int x1, int x2, int pos) {
        if (this.vertical) {
            gc.drawLine(x1, pos, x2, pos);
        } else {
            gc.drawLine(pos, x1, pos, x2);
        }
    }

    private void onMouseHover(MouseEvent e) {
        disposeTooltip();
        ICoordinates coord = pixelToCoord(getMousePixelPosition(e));
        String address = this.idoc.coordinatesToAddress(coord, AddressConversionPrecision.FINE);
        this.tip = new ToolTip(getShell(), 0);
        Point p = Display.getCurrent().getCursorLocation();
        Point widgetPosition = toDisplay(0, 0);
        Point size = getSize();
        if ((MathUtil.betweenInclusive(p.x, widgetPosition.x, widgetPosition.x + size.x)) && (MathUtil.betweenInclusive(p.y, widgetPosition.y, widgetPosition.y + size.y))) {
            p.x += 5;
            p.y += 5;
            this.tip.setLocation(p);
            this.tip.setText("Use the mouse wheel to zoom in and out");
            this.tip.setMessage(Strings.safe(address));
            this.tip.setAutoHide(true);
            this.tip.setVisible(true);
        }
    }

    private void onMouseExit(MouseEvent e) {
    }

    private void onMouseEnter(MouseEvent e) {
    }

    private void disposeTooltip() {
        if (this.tip != null) {
            this.tip.setVisible(false);
            this.tip.dispose();
            this.tip = null;
        }
    }

    ICoordinates pixelToCoord(int pixel) {
        return pixelToCoord(pixel, -1);
    }

    boolean isMovingBottomEOF(int pixel) {
        int pixelMax = this.vertical ? getClientArea().height : getClientArea().width;
        if ((this.textViewer.getWrappedText().isAnchorEndDisplayed()) && (this.textViewer.getWrappedText().isCurrentPartLastLineDisplayed()) && (pixel > pixelMax)) {
            return true;
        }
        return false;
    }

    ICoordinates pixelToCoord(int pixel, int pixelMax) {
        if (pixelMax < 0) {
            pixelMax = this.vertical ? getClientArea().height : getClientArea().width;
        }
        long barAnchorRange = this.barAnchorEnd - this.barAnchorFirst;
        if (barAnchorRange == 1L) {
            ITextDocumentPart part = this.textViewer.getCurrentDocumentPart();
            List<? extends ILine> lines = TextPartUtil.getLinesOfAnchor(part, this.barAnchorFirst);
            if (lines != null) {
                return new Coordinates(this.barAnchorFirst, pixel * lines.size() / pixelMax);
            }
            return new Coordinates(this.barAnchorFirst);
        }
        return new Coordinates(this.barAnchorFirst + (pixel * (barAnchorRange / pixelMax)));
    }

    int coordToPixel(ICoordinates coord) {
        return coordToPixel(coord, this.vertical ? getClientArea().height : getClientArea().width);
    }

    int coordToPixel(ICoordinates coord, int pixelMax) {
        long barAnchorRange = this.barAnchorEnd - this.barAnchorFirst;
        if (barAnchorRange == 1L) {
            if (coord.getAnchorId() == this.barAnchorFirst) {
                int lineDelta = coord.getLineDelta();
                ITextDocumentPart part = this.textViewer.getCurrentDocumentPart();
                List<? extends ILine> lines = TextPartUtil.getLinesOfAnchor(part, this.barAnchorFirst);
                if ((lines != null) && (lineDelta >= 0) && (lineDelta < lines.size())) {
                    return lineDelta * pixelMax / lines.size();
                }
            }
            return -1;
        }
        double r = (coord.getAnchorId() - this.barAnchorFirst) / barAnchorRange;
        return (int) (pixelMax * r);
    }

    void onMouseDown(MouseEvent e) {
        this.mousePosition = getMousePixelPosition(e);
        if (e.button != 1) {
            return;
        }
        this.mouseButtonDown = true;
        setPosition(getMousePixelPosition(e));
    }

    void onMouseUp(MouseEvent e) {
        this.mouseButtonDown = false;
        this.moving = false;
    }

    void onMouseMove(MouseEvent e) {
        if (this.mouseButtonDown) {
            this.moving = true;
            int pixel = getMousePixelPosition(e);
            int pixelMax = this.vertical ? getClientArea().height : getClientArea().width;
            if (pixel > pixelMax) {
                if (this.overflowDone) {
                    this.ignoreNextNegativeValue = false;
                } else {
                    this.ignoreNextNegativeValue = true;
                    this.overflowDone = true;
                }
            } else {
                this.overflowDone = false;
                if ((pixel < 0) && (this.ignoreNextNegativeValue)) {
                    this.ignoreNextNegativeValue = false;
                    return;
                }
                this.ignoreNextNegativeValue = false;
            }
            if (isMovingBottomEOF(pixel)) {
                return;
            }
            setPosition(pixel);
        }
        disposeTooltip();
    }

    void onMouseWheelEvent(MouseEvent e) {
        ICoordinates coord = pixelToCoord(getMousePixelPosition(e));
        logger.i("wheel @ coord: %s", new Object[]{coord});
        applyZoom(e.count, coord.getAnchorId());
    }

    int getMousePixelPosition(MouseEvent e) {
        return this.vertical ? e.y : e.x;
    }

    private synchronized void setPosition(int pixel) {
        if (this.textViewer == null) {
            return;
        }
        ICoordinates coord = pixelToCoord(pixel);
        ICoordinates carCoords = this.textViewer.getCaretCoordinates();
        if (coord.getAnchorId() >= this.barAnchorEnd) {
            this.textViewer.bufferManager.viewAtEndOfDocument();
        } else if ((carCoords != null) && ((coord.getLineDelta() == 0) || ((coord.getAnchorId() == this.barAnchorFirst) && (coord.getLineDelta() < 0)) || (coord.getAnchorId() < this.barAnchorFirst))) {
            if (coord.getAnchorId() <= this.barAnchorFirst) {
                if ((carCoords.getAnchorId() != this.barAnchorFirst) || (carCoords.getLineDelta() != 0)) {
                    this.textViewer.bufferManager.viewAtStartOfDocument();
                }
            } else {
                this.textViewer.bufferManager.viewAtAnchor(coord.getAnchorId());
            }
        } else {
            BufferPoint point = this.textViewer.getCaretViewportPoint();
            if (this.textViewer.bufferManager.getWrappedText().isCurrentPartLastLineDisplayed()) {
                point = null;
            }
            this.textViewer.setCaretCoordinates(coord, point, false);
        }
        redraw();
    }

    void applyZoom(int delta, long anchorPivot) {
        long _barAnchorFirst = this.barAnchorFirst;
        long _barAnchorEnd = this.barAnchorEnd;
        int _barZoomLevel = this.barZoomLevel;
        long range = _barAnchorEnd - _barAnchorFirst;
        long newRange;
        if (delta > 0) {
            _barZoomLevel++;
            newRange = range / 2L;
        } else {
            if (_barZoomLevel > 0) {
                if (delta == 0) {
                    _barZoomLevel = 0;
                    newRange = -1L;
                } else {
                    _barZoomLevel--;
                    newRange = range * 2L;
                }
            } else {
                return;
            }
        }
        if (_barZoomLevel == 0) {
            _barAnchorFirst = this.idoc.getFirstAnchor();
            _barAnchorEnd = this.idoc.getFirstAnchor() + this.idoc.getAnchorCount();
        } else {
            long halfRange = newRange / 2L;
            _barAnchorFirst = anchorPivot - halfRange;
            if (_barAnchorFirst < this.idoc.getFirstAnchor()) {
                _barAnchorFirst = this.idoc.getFirstAnchor();
                _barAnchorEnd = _barAnchorFirst + newRange;
            } else {
                _barAnchorEnd = anchorPivot + halfRange;
                if (_barAnchorEnd >= this.idoc.getFirstAnchor() + this.idoc.getAnchorCount()) {
                    _barAnchorEnd = this.idoc.getFirstAnchor() + this.idoc.getAnchorCount();
                    _barAnchorFirst = _barAnchorEnd - newRange;
                }
            }
        }
        if (_barAnchorEnd <= _barAnchorFirst) {
            return;
        }
        this.barZoomLevel = _barZoomLevel;
        this.barAnchorFirst = _barAnchorFirst;
        this.barAnchorEnd = _barAnchorEnd;
        this.zoomLevelChanged = true;
        logger.i("Bar range: %Xh - %Xh", new Object[]{Long.valueOf(this.barAnchorFirst), Long.valueOf(this.barAnchorEnd)});
        redraw();
    }

    class ActionZoom extends Action {
        int delta;
        String text;

        public ActionZoom(int delta) {
            boolean enabled;
            if (delta == 1) {
                text = "Zoom In";
                enabled = true;
            } else {
                if (delta == -1) {
                    text = "Zoom Out";
                    enabled = OverviewBar.this.barZoomLevel >= 1;
                } else {
                    if (delta == 0) {
                        text = "Reset Zoom";
                        enabled = OverviewBar.this.barZoomLevel >= 1;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            }
            setText(text);
            setEnabled(enabled);
            this.delta = delta;
        }

        public void run() {
            ICoordinates coord = OverviewBar.this.pixelToCoord(OverviewBar.this.mousePosition);
            OverviewBar.this.applyZoom(this.delta, coord.getAnchorId());
        }
    }
}


