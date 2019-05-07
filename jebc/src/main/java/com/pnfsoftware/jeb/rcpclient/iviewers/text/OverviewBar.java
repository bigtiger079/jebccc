/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
/*     */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
/*     */ import com.pnfsoftware.jeb.core.units.IMetadataGroup;
/*     */ import com.pnfsoftware.jeb.core.units.IMetadataManager;
/*     */ import com.pnfsoftware.jeb.core.units.MetadataGroupType;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedText;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import com.pnfsoftware.jeb.util.math.MathUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.Action;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.swt.custom.CaretEvent;
/*     */ import org.eclipse.swt.custom.CaretListener;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.events.MouseTrackListener;
/*     */ import org.eclipse.swt.events.MouseWheelListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.ToolTip;

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
/*     */ public class OverviewBar
        /*     */ extends Canvas
        /*     */ {
    /*  62 */   private static final ILogger logger = GlobalLog.getLogger(OverviewBar.class);
    /*     */
    /*     */   private InteractiveTextViewer textViewer;
    /*     */   private ITextDocument idoc;
    /*     */   private IMetadataManager metadataManager;
    /*     */   private boolean vertical;
    /*     */   private int lastKnownBarSize;
    /*  69 */   private List<int[]> arrays = new ArrayList();
    /*     */
    /*     */   private boolean mouseButtonDown;
    /*     */
    /*     */   private int mousePosition;
    /*     */   private boolean moving;
    /*     */   private ToolTip tip;
    /*     */   private boolean zoomLevelChanged;
    /*     */   private int barZoomLevel;
    /*     */   private long barAnchorFirst;
    /*     */   private long barAnchorEnd;
    /*  80 */   private final int emptyColor = 13684944;
    /*  81 */   private final int defaultColor = 6316128;
    /*     */
    /*  83 */   private final Color colorZoomRange = UIAssetManager.getInstance().getColor(255, 0, 0);
    /*  84 */   private final Color colorPartRange = UIAssetManager.getInstance().getColor(255, 153, 0);
    /*  85 */   private final Color colorIndicator = UIAssetManager.getInstance().getColor(255, 255, 0);
    /*  86 */   private final Color colorViewport = UIAssetManager.getInstance().getColor(102, 255, 0);
    /*     */
    /*     */
    /*     */   private boolean ignoreNextNegativeValue;
    /*     */
    /*     */
    /*     */   private boolean overflowDone;

    /*     */
    /*     */
    /*     */
    public OverviewBar(Composite parent, int styles, ITextDocument idoc, IMetadataManager metadataManager)
    /*     */ {
        /*  97 */
        super(parent, 0x40000 | styles & 0x800);
        /*     */
        /*  99 */
        if (idoc == null) {
            /* 100 */
            throw new IllegalArgumentException();
            /*     */
        }
        /*     */
        /* 103 */
        this.idoc = idoc;
        /* 104 */
        this.metadataManager = metadataManager;
        /*     */
        /* 106 */
        this.vertical = ((styles & 0x200) != 0);
        /*     */
        /*     */
        /* 109 */
        this.barZoomLevel = 0;
        /* 110 */
        this.barAnchorFirst = idoc.getFirstAnchor();
        /* 111 */
        this.barAnchorEnd = (idoc.getFirstAnchor() + idoc.getAnchorCount());
        /*     */
        /* 113 */
        addPaintListener(new PaintListener()
                /*     */ {
            /*     */
            public void paintControl(PaintEvent e) {
                /* 116 */
                OverviewBar.this.onPaintControl(e);
                /*     */
            }
            /*     */
            /* 119 */
        });
        /* 120 */
        addMouseTrackListener(new MouseTrackListener()
                /*     */ {
            /*     */
            public void mouseHover(MouseEvent e) {
                /* 123 */
                OverviewBar.this.onMouseHover(e);
                /*     */
            }

            /*     */
            /*     */
            public void mouseExit(MouseEvent e)
            /*     */ {
                /* 128 */
                OverviewBar.this.onMouseExit(e);
                /*     */
            }

            /*     */
            /*     */
            public void mouseEnter(MouseEvent e)
            /*     */ {
                /* 133 */
                OverviewBar.this.onMouseEnter(e);
                /*     */
            }
            /*     */
            /* 136 */
        });
        /* 137 */
        addMouseListener(new MouseListener()
                /*     */ {
            /*     */
            public void mouseUp(MouseEvent e) {
                /* 140 */
                OverviewBar.this.onMouseUp(e);
                /*     */
            }

            /*     */
            /*     */
            public void mouseDown(MouseEvent e)
            /*     */ {
                /* 145 */
                OverviewBar.this.onMouseDown(e);
                /*     */
            }

            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            public void mouseDoubleClick(MouseEvent e) {
            }
            /* 152 */
        });
        /* 153 */
        addMouseMoveListener(new MouseMoveListener()
                /*     */ {
            /*     */
            public void mouseMove(MouseEvent e) {
                /* 156 */
                OverviewBar.this.onMouseMove(e);
                /*     */
            }
            /*     */
            /* 159 */
        });
        /* 160 */
        addMouseWheelListener(new MouseWheelListener()
                /*     */ {
            /*     */
            public void mouseScrolled(MouseEvent e) {
                /* 163 */
                OverviewBar.this.onMouseWheelEvent(e);
                /*     */
            }
            /*     */
            /* 166 */
        });
        /* 167 */
        new ContextMenu(this).addContextMenu(new IContextMenu()
                /*     */ {
            /*     */
            public void fillContextMenu(IMenuManager menuMgr) {
                /* 170 */
                menuMgr.add(new OverviewBar.ActionZoom(OverviewBar.this, 1));
                /* 171 */
                if (OverviewBar.this.barZoomLevel >= 1) {
                    /* 172 */
                    menuMgr.add(new OverviewBar.ActionZoom(OverviewBar.this, -1));
                    /* 173 */
                    menuMgr.add(new OverviewBar.ActionZoom(OverviewBar.this, 0));
                    /*     */
                }
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */   void connectToViewer(InteractiveTextViewer textViewer) {
        /* 180 */
        this.textViewer = textViewer;
        /*     */
        /*     */
        /* 183 */
        textViewer.getTextWidget().addCaretListener(new CaretListener()
                /*     */ {
            /*     */
            public void caretMoved(CaretEvent e) {
                /* 186 */
                OverviewBar.this.redraw();
                /*     */
            }
            /* 188 */
        });
        /* 189 */
        textViewer.getTextWidget().addMouseWheelListener(new MouseWheelListener()
                /*     */ {
            /*     */
            public void mouseScrolled(MouseEvent e) {
                /* 192 */
                OverviewBar.this.redraw();
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */   void refresh() {
        /* 198 */
        this.lastKnownBarSize = -1;
        /* 199 */
        redraw();
        /*     */
    }

    /*     */
    /*     */
    private void onPaintControl(PaintEvent e) {
        /* 203 */
        Rectangle client = getClientArea();
        /* 204 */
        int size = this.vertical ? client.height : client.width;
        /* 205 */
        int width = this.vertical ? client.width : client.height;
        /*     */
        /*     */
        /* 208 */
        if ((!this.moving) && ((this.lastKnownBarSize != size) || (this.zoomLevelChanged))) {
            /* 209 */
            this.lastKnownBarSize = size;
            /* 210 */
            this.zoomLevelChanged = false;
            /* 211 */
            buildBars(size);
            /*     */
        }
        /*     */
        /*     */
        /* 215 */
        drawBars(e.gc);
        /*     */
        /*     */
        /* 218 */
        if (this.textViewer != null) {
            /* 219 */
            drawCaretTriangle(e.gc, size);
            /* 220 */
            drawViewport(e.gc, size, width);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void drawCaretTriangle(GC gc, int size) {
        /* 225 */
        ICoordinates coord = this.textViewer.getCaretCoordinates();
        /* 226 */
        int pos = 0;
        /* 227 */
        if (coord != null) {
            /* 228 */
            pos = coordToPixel(coord, size);
            /*     */
        }
        /*     */
        else {
            /* 231 */
            pos = size - 1;
            /*     */
        }
        /* 233 */
        if ((pos >= 0) && (pos < size)) {
            /* 234 */
            gc.setForeground(this.colorIndicator);
            /* 235 */
            gc.setBackground(this.colorIndicator);
            /* 236 */
            if (!this.vertical)
                /*     */ {
                /*     */
                /* 239 */
                gc.fillPolygon(new int[]{pos - 5, 0, pos + 5, 0, pos, 7});
                /*     */
            }
            /*     */
            else {
                /* 242 */
                gc.fillPolygon(new int[]{0, pos - 5, 0, pos + 5, 7, pos});
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void drawViewport(GC gc, int size, int width) {
        /* 248 */
        ICoordinates coord = this.textViewer.getWrappedText().getTopIndexCoordinates();
        /* 249 */
        if (coord != null) {
            /* 250 */
            int pos = coordToPixel(coord, size);
            /* 251 */
            if ((pos >= 0) && (pos < size)) {
                /* 252 */
                gc.setForeground(this.colorViewport);
                /* 253 */
                gc.setBackground(this.colorViewport);
                /*     */
                /* 255 */
                drawBar(gc, pos, pos + 2, this.vertical, width);
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
    private void buildBars(int size)
    /*     */ {
        /* 264 */
        this.arrays.clear();
        /*     */
        /* 266 */
        if (this.metadataManager != null) {
            /* 267 */
            for (IMetadataGroup grp : this.metadataManager.getGroups()) {
                /* 268 */
                int i = -2;
                /* 269 */
                int[] array = new int[size * 2 + 1];
                /*     */
                /* 271 */
                for (int pos = 0; pos < size; pos++) {
                    /* 272 */
                    MetadataGroupType grpType = grp.getType();
                    /*     */
                    /* 274 */
                    String address = this.idoc.coordinatesToAddress(pixelToCoord(pos, size), AddressConversionPrecision.COARSE);
                    /*     */
                    /* 276 */
                    Object o = grp.getData(address);
                    /* 277 */
                    int rgb = determineColor(o, grpType);
                    /*     */
                    /* 279 */
                    if ((i < 0) || (array[(i + 1)] != rgb)) {
                        /* 280 */
                        i += 2;
                        /* 281 */
                        array[i] = 1;
                        /* 282 */
                        array[(i + 1)] = rgb;
                        /*     */
                    }
                    /*     */
                    else {
                        /* 285 */
                        array[i] += 1;
                        /*     */
                    }
                    /*     */
                }
                /* 288 */
                array[(i + 2)] = -1;
                /*     */
                /* 290 */
                this.arrays.add(array);
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 296 */
        if (this.arrays.isEmpty()) {
            /* 297 */
            int i = -2;
            /* 298 */
            int[] array = new int[size * 2 + 1];
            /* 299 */
            int rgb = determineColor(null, null);
            /* 300 */
            for (int pos = 0; pos < size; pos++) {
                /* 301 */
                if ((i < 0) || (array[(i + 1)] != rgb)) {
                    /* 302 */
                    i += 2;
                    /* 303 */
                    array[i] = 1;
                    /* 304 */
                    array[(i + 1)] = rgb;
                    /*     */
                }
                /*     */
                else {
                    /* 307 */
                    array[i] += 1;
                    /*     */
                }
                /*     */
            }
            /* 310 */
            array[(i + 2)] = -1;
            /* 311 */
            this.arrays.add(array);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    private int determineColor(Object object, MetadataGroupType grpType)
    /*     */ {
        /* 319 */
        if (object == null) {
            /* 320 */
            return 13684944;
            /*     */
        }
        /*     */
        /* 323 */
        switch (grpType) {
            /*     */
            case CLASSID:
                /* 325 */
                if (((object instanceof Integer)) && (this.textViewer != null) && (this.textViewer.styleAdapter != null)) {
                    /* 326 */
                    int id = ((Integer) object).intValue();
                    /* 327 */
                    ItemClassIdentifiers classId = ItemClassIdentifiers.getById(id);
                    /* 328 */
                    Style s = this.textViewer.styleAdapter.getStyle(classId, false);
                    /* 329 */
                    if ((s != null) && (s.getColor() != null)) {
                        /* 330 */
                        RGB r = s.getColor().getRGB();
                        /* 331 */
                        return r.red << 16 | r.green << 8 | r.blue;
                        /*     */
                    }
                    /*     */
                }
                /* 334 */
                return 6316128;
            /*     */
            /*     */
            case RGB:
                /* 337 */
                if ((object instanceof Integer)) {
                    /* 338 */
                    return ((Integer) object).intValue();
                    /*     */
                }
                /* 340 */
                return 6316128;
            /*     */
        }
        /*     */
        /* 343 */
        return 6316128;
        /*     */
    }

    /*     */
    /*     */
    private void drawBars(GC gc)
    /*     */ {
        /* 348 */
        Rectangle ca = getClientArea();
        /* 349 */
        int fillsize = this.vertical ? ca.width : ca.height;
        /* 350 */
        int maxSize = this.vertical ? ca.height : ca.width;
        /*     */
        /* 352 */
        int x1 = -1;
        /* 353 */
        int x2 = 0;
        /*     */
        /* 355 */
        for (int[] array : this.arrays) {
            /* 356 */
            x1 = x2;
            /* 357 */
            x2 = x1 + fillsize / this.arrays.size();
            /*     */
            /* 359 */
            int y = 0;
            /* 360 */
            int i = 0;
            /*     */
            for (; ; ) {
                /* 362 */
                int n = array[i];
                /* 363 */
                if (n < 0) {
                    /*     */
                    break;
                    /*     */
                }
                /*     */
                /* 367 */
                Color c = UIAssetManager.getInstance().getColor(array[(i + 1)]);
                /* 368 */
                gc.setForeground(c);
                /* 369 */
                if (n == 1) {
                    /* 370 */
                    drawLine(gc, x1, x2, y);
                    /* 371 */
                    y++;
                    /*     */
                }
                /*     */
                else {
                    /* 374 */
                    gc.setBackground(c);
                    /* 375 */
                    if (this.vertical) {
                        /* 376 */
                        gc.fillRectangle(x1, y, x2 - x1, n);
                        /*     */
                    }
                    /*     */
                    else {
                        /* 379 */
                        gc.fillRectangle(y, x1, n, x2 - x1);
                        /*     */
                    }
                    /*     */
                    /* 382 */
                    y += n;
                    /*     */
                }
                /* 384 */
                i += 2;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 389 */
        drawPartRange(gc, maxSize);
        /*     */
        /*     */
        /* 392 */
        drawZoomRange(gc, maxSize);
        /*     */
    }

    /*     */
    /*     */
    private void drawPartRange(GC gc, int size) {
        /* 396 */
        ITextDocumentPart part = this.textViewer.getCurrentDocumentPart();
        /* 397 */
        if (part != null) {
            /* 398 */
            long partAnchor0 = TextPartUtil.getFirstAnchorId(part);
            /* 399 */
            long partAnchor1 = TextPartUtil.getNextAnchorId(part);
            /* 400 */
            gc.setBackground(this.colorPartRange);
            /* 401 */
            long x0 = (partAnchor0 - this.idoc.getFirstAnchor()) * size / this.idoc.getAnchorCount();
            /* 402 */
            long x1 = (partAnchor1 - this.idoc.getFirstAnchor()) * size / this.idoc.getAnchorCount();
            /* 403 */
            drawBar(gc, x0, x1);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void drawZoomRange(GC gc, int size) {
        /* 408 */
        if (this.barZoomLevel != 0) {
            /* 409 */
            gc.setBackground(this.colorZoomRange);
            /* 410 */
            long x0 = (this.barAnchorFirst - this.idoc.getFirstAnchor()) * size / this.idoc.getAnchorCount();
            /* 411 */
            long x1 = (this.barAnchorEnd - this.idoc.getFirstAnchor()) * size / this.idoc.getAnchorCount();
            /* 412 */
            drawBar(gc, x0, x1);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    private void drawBar(GC gc, long x0, long x1)
    /*     */ {
        /* 420 */
        drawBar(gc, x0, x1, this.vertical, 2);
        /*     */
    }

    /*     */
    /*     */
    private void drawBar(GC gc, long x0, long x1, boolean vertical, int length) {
        /* 424 */
        if (vertical) {
            /* 425 */
            gc.fillRectangle(0, (int) x0, length, (int) (x1 - x0));
            /*     */
        }
        /*     */
        else {
            /* 428 */
            gc.fillRectangle((int) x0, 0, (int) (x1 - x0), length);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    private void drawLine(GC gc, int x1, int x2, int pos)
    /*     */ {
        /* 436 */
        if (this.vertical)
            /*     */ {
            /* 438 */
            gc.drawLine(x1, pos, x2, pos);
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 442 */
            gc.drawLine(pos, x1, pos, x2);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void onMouseHover(MouseEvent e) {
        /* 447 */
        disposeTooltip();
        /*     */
        /* 449 */
        ICoordinates coord = pixelToCoord(getMousePixelPosition(e));
        /* 450 */
        String address = this.idoc.coordinatesToAddress(coord, AddressConversionPrecision.FINE);
        /*     */
        /* 452 */
        this.tip = new ToolTip(getShell(), 0);
        /* 453 */
        Point p = Display.getCurrent().getCursorLocation();
        /* 454 */
        Point widgetPosition = toDisplay(0, 0);
        /* 455 */
        Point size = getSize();
        /*     */
        /* 457 */
        if ((MathUtil.betweenInclusive(p.x, widgetPosition.x, widgetPosition.x + size.x)) &&
                /* 458 */       (MathUtil.betweenInclusive(p.y, widgetPosition.y, widgetPosition.y + size.y))) {
            /* 459 */
            p.x += 5;
            /* 460 */
            p.y += 5;
            /* 461 */
            this.tip.setLocation(p);
            /* 462 */
            this.tip.setText("Use the mouse wheel to zoom in and out");
            /* 463 */
            this.tip.setMessage(Strings.safe(address));
            /* 464 */
            this.tip.setAutoHide(true);
            /* 465 */
            this.tip.setVisible(true);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    private void onMouseExit(MouseEvent e) {
    }

    /*     */
    /*     */
    /*     */
    private void onMouseEnter(MouseEvent e) {
    }

    /*     */
    /*     */
    /*     */
    private void disposeTooltip()
    /*     */ {
        /* 478 */
        if (this.tip != null) {
            /* 479 */
            this.tip.setVisible(false);
            /* 480 */
            this.tip.dispose();
            /* 481 */
            this.tip = null;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   ICoordinates pixelToCoord(int pixel) {
        /* 486 */
        return pixelToCoord(pixel, -1);
        /*     */
    }

    /*     */
    /*     */   boolean isMovingBottomEOF(int pixel) {
        /* 490 */
        int pixelMax = this.vertical ? getClientArea().height : getClientArea().width;
        /*     */
        /* 492 */
        if ((this.textViewer.getWrappedText().isAnchorEndDisplayed()) &&
                /* 493 */       (this.textViewer.getWrappedText().isCurrentPartLastLineDisplayed()) && (pixel > pixelMax))
            /*     */ {
            /* 495 */
            return true;
            /*     */
        }
        /* 497 */
        return false;
        /*     */
    }

    /*     */
    /*     */   ICoordinates pixelToCoord(int pixel, int pixelMax) {
        /* 501 */
        if (pixelMax < 0) {
            /* 502 */
            pixelMax = this.vertical ? getClientArea().height : getClientArea().width;
            /*     */
        }
        /*     */
        /* 505 */
        long barAnchorRange = this.barAnchorEnd - this.barAnchorFirst;
        /* 506 */
        if (barAnchorRange == 1L) {
            /* 507 */
            ITextDocumentPart part = this.textViewer.getCurrentDocumentPart();
            /* 508 */
            List<? extends ILine> lines = TextPartUtil.getLinesOfAnchor(part, this.barAnchorFirst);
            /* 509 */
            if (lines != null) {
                /* 510 */
                return new Coordinates(this.barAnchorFirst, pixel * lines.size() / pixelMax);
                /*     */
            }
            /* 512 */
            return new Coordinates(this.barAnchorFirst);
            /*     */
        }
        /*     */
        /* 515 */
        return new Coordinates(this.barAnchorFirst + (pixel * (barAnchorRange / pixelMax)));
        /*     */
    }

    /*     */
    /*     */
    /*     */   int coordToPixel(ICoordinates coord)
    /*     */ {
        /* 521 */
        return coordToPixel(coord, this.vertical ? getClientArea().height : getClientArea().width);
        /*     */
    }

    /*     */
    /*     */   int coordToPixel(ICoordinates coord, int pixelMax) {
        /* 525 */
        long barAnchorRange = this.barAnchorEnd - this.barAnchorFirst;
        /* 526 */
        if (barAnchorRange == 1L) {
            /* 527 */
            if (coord.getAnchorId() == this.barAnchorFirst) {
                /* 528 */
                int lineDelta = coord.getLineDelta();
                /* 529 */
                ITextDocumentPart part = this.textViewer.getCurrentDocumentPart();
                /* 530 */
                List<? extends ILine> lines = TextPartUtil.getLinesOfAnchor(part, this.barAnchorFirst);
                /* 531 */
                if ((lines != null) && (lineDelta >= 0) && (lineDelta < lines.size())) {
                    /* 532 */
                    return lineDelta * pixelMax / lines.size();
                    /*     */
                }
                /*     */
            }
            /* 535 */
            return -1;
            /*     */
        }
        /*     */
        /* 538 */
        double r = (coord.getAnchorId() - this.barAnchorFirst) / barAnchorRange;
        /* 539 */
        return (int) (pixelMax * r);
        /*     */
    }

    /*     */
    /*     */
    /*     */   void onMouseDown(MouseEvent e)
    /*     */ {
        /* 545 */
        this.mousePosition = getMousePixelPosition(e);
        /*     */
        /*     */
        /* 548 */
        if (e.button != 1) {
            /* 549 */
            return;
            /*     */
        }
        /*     */
        /* 552 */
        this.mouseButtonDown = true;
        /* 553 */
        setPosition(getMousePixelPosition(e));
        /*     */
    }

    /*     */
    /*     */   void onMouseUp(MouseEvent e) {
        /* 557 */
        this.mouseButtonDown = false;
        /* 558 */
        this.moving = false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */   void onMouseMove(MouseEvent e)
    /*     */ {
        /* 565 */
        if (this.mouseButtonDown) {
            /* 566 */
            this.moving = true;
            /* 567 */
            int pixel = getMousePixelPosition(e);
            /* 568 */
            int pixelMax = this.vertical ? getClientArea().height : getClientArea().width;
            /* 569 */
            if (pixel > pixelMax)
                /*     */ {
                /*     */
                /* 572 */
                if (this.overflowDone) {
                    /* 573 */
                    this.ignoreNextNegativeValue = false;
                    /*     */
                }
                /*     */
                else {
                    /* 576 */
                    this.ignoreNextNegativeValue = true;
                    /* 577 */
                    this.overflowDone = true;
                    /*     */
                }
                /*     */
            }
            /*     */
            else {
                /* 581 */
                this.overflowDone = false;
                /* 582 */
                if ((pixel < 0) && (this.ignoreNextNegativeValue))
                    /*     */ {
                    /* 584 */
                    this.ignoreNextNegativeValue = false;
                    /* 585 */
                    return;
                    /*     */
                }
                /* 587 */
                this.ignoreNextNegativeValue = false;
                /*     */
            }
            /* 589 */
            if (isMovingBottomEOF(pixel)) {
                /* 590 */
                return;
                /*     */
            }
            /* 592 */
            setPosition(pixel);
            /*     */
        }
        /*     */
        /* 595 */
        disposeTooltip();
        /*     */
    }

    /*     */
    /*     */   void onMouseWheelEvent(MouseEvent e) {
        /* 599 */
        ICoordinates coord = pixelToCoord(getMousePixelPosition(e));
        /* 600 */
        logger.i("wheel @ coord: %s", new Object[]{coord});
        /* 601 */
        applyZoom(e.count, coord.getAnchorId());
        /*     */
    }

    /*     */
    /*     */   int getMousePixelPosition(MouseEvent e) {
        /* 605 */
        return this.vertical ? e.y : e.x;
        /*     */
    }

    /*     */
    /*     */
    private synchronized void setPosition(int pixel)
    /*     */ {
        /* 610 */
        if (this.textViewer == null) {
            /* 611 */
            return;
            /*     */
        }
        /*     */
        /* 614 */
        ICoordinates coord = pixelToCoord(pixel);
        /* 615 */
        ICoordinates carCoords = this.textViewer.getCaretCoordinates();
        /* 616 */
        if (coord.getAnchorId() >= this.barAnchorEnd) {
            /* 617 */
            this.textViewer.bufferManager.viewAtEndOfDocument();
            /*     */
        }
        /* 619 */
        else if ((carCoords != null) && (
                /* 620 */       (coord.getLineDelta() == 0) || ((coord.getAnchorId() == this.barAnchorFirst) && (coord.getLineDelta() < 0)) ||
                /* 621 */       (coord.getAnchorId() < this.barAnchorFirst))) {
            /* 622 */
            if (coord.getAnchorId() <= this.barAnchorFirst)
                /*     */ {
                /* 624 */
                if ((carCoords.getAnchorId() != this.barAnchorFirst) || (carCoords.getLineDelta() != 0)) {
                    /* 625 */
                    this.textViewer.bufferManager.viewAtStartOfDocument();
                    /*     */
                }
                /*     */
            }
            /*     */
            else {
                /* 629 */
                this.textViewer.bufferManager.viewAtAnchor(coord.getAnchorId());
                /*     */
            }
            /*     */
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 635 */
            BufferPoint point = this.textViewer.getCaretViewportPoint();
            /* 636 */
            if (this.textViewer.bufferManager.getWrappedText().isCurrentPartLastLineDisplayed())
                /*     */ {
                /*     */
                /* 639 */
                point = null;
                /*     */
            }
            /* 641 */
            this.textViewer.setCaretCoordinates(coord, point, false);
            /*     */
        }
        /*     */
        /*     */
        /* 645 */
        redraw();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   void applyZoom(int delta, long anchorPivot)
    /*     */ {
        /* 655 */
        long _barAnchorFirst = this.barAnchorFirst;
        /* 656 */
        long _barAnchorEnd = this.barAnchorEnd;
        /* 657 */
        int _barZoomLevel = this.barZoomLevel;
        /*     */
        /* 659 */
        long range = _barAnchorEnd - _barAnchorFirst;
        /*     */
        /*     */
        long newRange;
        /*     */
        /* 663 */
        if (delta > 0) {
            /* 664 */
            _barZoomLevel++;
            /* 665 */
            newRange = range / 2L;
            /*     */
        } else {
            /*     */
            long newRange;
            /* 668 */
            if (_barZoomLevel > 0) {
                /*     */
                long newRange;
                /* 670 */
                if (delta == 0) {
                    /* 671 */
                    _barZoomLevel = 0;
                    /* 672 */
                    newRange = -1L;
                    /*     */
                }
                /*     */
                else {
                    /* 675 */
                    _barZoomLevel--;
                    /* 676 */
                    newRange = range * 2L;
                    /*     */
                }
                /*     */
            } else {
                /*     */
                return;
                /*     */
            }
            /*     */
        }
        /*     */
        long newRange;
        /* 683 */
        if (_barZoomLevel == 0) {
            /* 684 */
            _barAnchorFirst = this.idoc.getFirstAnchor();
            /* 685 */
            _barAnchorEnd = this.idoc.getFirstAnchor() + this.idoc.getAnchorCount();
            /*     */
        }
        /*     */
        else {
            /* 688 */
            long halfRange = newRange / 2L;
            /* 689 */
            _barAnchorFirst = anchorPivot - halfRange;
            /* 690 */
            if (_barAnchorFirst < this.idoc.getFirstAnchor()) {
                /* 691 */
                _barAnchorFirst = this.idoc.getFirstAnchor();
                /* 692 */
                _barAnchorEnd = _barAnchorFirst + newRange;
                /*     */
            }
            /*     */
            else {
                /* 695 */
                _barAnchorEnd = anchorPivot + halfRange;
                /* 696 */
                if (_barAnchorEnd >= this.idoc.getFirstAnchor() + this.idoc.getAnchorCount()) {
                    /* 697 */
                    _barAnchorEnd = this.idoc.getFirstAnchor() + this.idoc.getAnchorCount();
                    /* 698 */
                    _barAnchorFirst = _barAnchorEnd - newRange;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 703 */
        if (_barAnchorEnd <= _barAnchorFirst) {
            /* 704 */
            return;
            /*     */
        }
        /*     */
        /*     */
        /* 708 */
        this.barZoomLevel = _barZoomLevel;
        /* 709 */
        this.barAnchorFirst = _barAnchorFirst;
        /* 710 */
        this.barAnchorEnd = _barAnchorEnd;
        /*     */
        /* 712 */
        this.zoomLevelChanged = true;
        /* 713 */
        logger.i("Bar range: %Xh - %Xh", new Object[]{Long.valueOf(this.barAnchorFirst), Long.valueOf(this.barAnchorEnd)});
        /* 714 */
        redraw();
        /*     */
    }

    /*     */
    /*     */   class ActionZoom extends Action
            /*     */ {
        /*     */ int delta;

        /*     */
        /*     */
        public ActionZoom(int delta)
        /*     */ {
            /*     */
            boolean enabled;
            /* 724 */
            if (delta == 1) {
                /* 725 */
                String text = "Zoom In";
                /* 726 */
                enabled = true;
                /*     */
            } else {
                boolean enabled;
                /* 728 */
                if (delta == -1) {
                    /* 729 */
                    String text = "Zoom Out";
                    /* 730 */
                    enabled = OverviewBar.this.barZoomLevel >= 1;
                    /*     */
                } else {
                    boolean enabled;
                    /* 732 */
                    if (delta == 0) {
                        /* 733 */
                        String text = "Reset Zoom";
                        /* 734 */
                        enabled = OverviewBar.this.barZoomLevel >= 1;
                        /*     */
                    }
                    /*     */
                    else {
                        /* 737 */
                        throw new IllegalArgumentException();
                    }
                }
            }
            /*     */
            boolean enabled;
            /*     */
            String text;
            /* 740 */
            setText(text);
            /* 741 */
            setEnabled(enabled);
            /*     */
            /* 743 */
            this.delta = delta;
            /*     */
        }

        /*     */
        /*     */
        public void run()
        /*     */ {
            /* 748 */
            ICoordinates coord = OverviewBar.this.pixelToCoord(OverviewBar.this.mousePosition);
            /* 749 */
            OverviewBar.this.applyZoom(this.delta, coord.getAnchorId());
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\OverviewBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */