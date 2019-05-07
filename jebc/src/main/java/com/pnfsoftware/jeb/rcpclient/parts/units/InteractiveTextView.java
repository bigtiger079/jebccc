/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.client.Licensing;
/*     */ import com.pnfsoftware.jeb.client.api.Operation;
/*     */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IActionableItem;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
/*     */ import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IMetadataManager;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.FontManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
/*     */ import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
/*     */ import com.pnfsoftware.jeb.rcpclient.IViewManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.FindTextDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.BufferPoint;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.IPositionListener;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.InteractiveTextFindResult;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.InteractiveTextViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.TextDocumentLocationGenerator;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.IViewNavigator;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UIState;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
/*     */ import com.pnfsoftware.jeb.util.base.Assert;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Menu;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class InteractiveTextView
        /*     */ extends AbstractInteractiveTextView
        /*     */ {
    /*  63 */   private static final ILogger logger = GlobalLog.getLogger(InteractiveTextView.class);
    /*     */
    /*  65 */   private List<ILocationListener> locationListeners = new ArrayList();
    /*     */   private UnitTextAnnotator textAnnotator;
    /*     */   private ItemStyleProvider tsa;
    /*     */   private GraphicalTextFinder<InteractiveTextFindResult> finder;

    /*     */
    /*     */
    public InteractiveTextView(Composite parent, int style, RcpClientContext context, IUnit unit, IRcpUnitView unitView, ITextDocument idoc)
    /*     */ {
        /*  72 */
        super(parent, style, unit, unitView, context, idoc);
        /*  73 */
        setLayout(new FillLayout());
        /*     */
        /*  75 */
        int flags = 0;
        /*     */
        /*  77 */
        if (!(unit instanceof ISourceUnit)) {
            /*  78 */
            flags |= 0x1;
            /*     */
        }
        /*  80 */
        if ((!Licensing.isDebugBuild()) && (!context.isDevelopmentMode())) {
            /*  81 */
            flags |= 0x2;
            /*     */
        }
        /*  83 */
        this.iviewer = new InteractiveTextViewer(this, flags, idoc, context.getPropertyManager(), getMetadataManager(unit));
        /*     */
        /*     */
        /*  86 */
        context.getFontManager().registerWidget(this.iviewer.getTextWidget());
        /*     */
        /*     */
        /*  89 */
        this.tsa = new ItemStyleProvider(context.getStyleManager());
        /*  90 */
        this.tsa.registerTextViewer(this.iviewer);
        /*  91 */
        this.iviewer.setStyleAdapter(this.tsa);
        /*     */
        /*     */
        /*  94 */
        this.finder = new GraphicalTextFinder(this.iviewer, context);
        /*     */
        /*     */
        /*  97 */
        UIState uiState = context.getUIState(unit);
        /*  98 */
        this.textAnnotator = new UnitTextAnnotator(uiState, this.iviewer);
        /*     */
        /* 100 */
        this.iviewer.initialize(true);
        /*     */
        /*     */
        /* 103 */
        this.iviewer.getTextWidget().setDoubleClickEnabled(false);
        /* 104 */
        this.iviewer.getTextWidget().addMouseListener(new MouseAdapter()
                /*     */ {
            /*     */
            public void mouseDoubleClick(MouseEvent e) {
                /* 107 */
                InteractiveTextView.this.requestOperation(new OperationRequest(Operation.ITEM_FOLLOW));
                /*     */
            }
            /*     */
            /* 110 */
        });
        /* 111 */
        setPrimaryWidget(this.iviewer.getTextWidget());
        /*     */
        /*     */
        /* 114 */
        this.iviewer.addPositionListener(new IPositionListener()
                /*     */ {
            /*     */
            public void positionChanged(ITextDocumentViewer viewer, ICoordinates coordinates, int focusChange) {
                /* 117 */
                Assert.a(viewer == InteractiveTextView.this.iviewer);
                /* 118 */
                InteractiveTextView.this.onPositionChanged(coordinates);
                /*     */
            }

            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            public void positionUnchangedAttemptBreakout(ITextDocumentViewer viewer, int direction) {
            }
            /* 126 */
        });
        /* 127 */
        this.iviewer.refreshStyles();
        /*     */
        /* 129 */
        this.iviewer.setHoverText(new TextHoverableProvider(context, unit, this.iviewer));
        /*     */
        /* 131 */
        addStandardContextMenu(new int[0]);
        /*     */
        /*     */
        /* 134 */
        addDisposeListener(new DisposeListener()
                /*     */ {
            /*     */
            public void widgetDisposed(DisposeEvent e) {
                /* 137 */
                InteractiveTextView.this.iviewer.dispose();
                /* 138 */
                InteractiveTextView.this.textAnnotator.dispose();
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    private IMetadataManager getMetadataManager(IUnit unit) {
        /* 144 */
        IMetadataManager mm = null;
        /* 145 */
        if ((unit instanceof IInteractiveUnit)) {
            /* 146 */
            mm = ((IInteractiveUnit) unit).getMetadataManager();
            /*     */
        }
        /* 148 */
        return mm;
        /*     */
    }

    /*     */
    /*     */
    private void onPositionChanged(ICoordinates coord)
    /*     */ {
        /* 153 */
        this.context.refreshHandlersStates();
        /*     */
        /* 155 */
        TextDocumentLocationGenerator locationGenerator = new TextDocumentLocationGenerator(this.unit, this.iviewer);
        /* 156 */
        String address = locationGenerator.getAddress(coord);
        /*     */
        /*     */
        /* 159 */
        String status = locationGenerator.generateStatus(coord);
        /* 160 */
        this.context.getStatusIndicator().setText(status);
        /*     */
        /*     */
        /* 163 */
        for (ILocationListener listener : this.locationListeners) {
            /* 164 */
            listener.locationChanged(address);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void addLocationListener(ILocationListener listener) {
        /* 169 */
        this.locationListeners.add(listener);
        /*     */
    }

    /*     */
    /*     */
    public void removeLocationListener(ILocationListener listener) {
        /* 173 */
        this.locationListeners.remove(listener);
        /*     */
    }

    /*     */
    /*     */
    public ITextDocument getDocument()
    /*     */ {
        /* 178 */
        return this.idoc;
        /*     */
    }

    /*     */
    /*     */
    public ICoordinates getCurrentCoordinates()
    /*     */ {
        /* 183 */
        return this.iviewer.getCaretCoordinates();
        /*     */
    }

    /*     */
    /*     */
    public boolean isActiveItem(IItem item)
    /*     */ {
        /* 188 */
        return this.tsa.isActiveItem(item);
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 193 */
        return this.tsa.getActiveItem();
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress()
    /*     */ {
        /* 198 */
        return getActiveAddress(AddressConversionPrecision.FINE);
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 203 */
        ICoordinates coord = this.iviewer.getCaretCoordinates();
        /* 204 */
        return getAddressAt(precision, coord);
        /*     */
    }

    /*     */
    /*     */
    public String getActiveItemAsText()
    /*     */ {
        /* 209 */
        ICoordinates coords = getCurrentCoordinates();
        /* 210 */
        if ((getActiveItem() != null) && (coords != null)) {
            /* 211 */
            ILine line = TextPartUtil.getLineAt(this.iviewer.getCurrentDocumentPart(), coords);
            /* 212 */
            if (line != null) {
                /* 213 */
                ITextItem item = TextPartUtil.getItemAt(line, coords.getColumnOffset());
                /* 214 */
                if (item != null) {
                    /* 215 */
                    return line.getText().subSequence(item.getOffset(), item.getOffsetEnd()).toString();
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 219 */
        return null;
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
    /* 234 */   private ITextDocumentPart lastKnownPart = null;
    /* 235 */   private ICoordinates lastKnownCoordinates = null;
    /* 236 */   private String lastKnownAddress = null;

    /*     */
    /*     */
    public String getAddressAt(AddressConversionPrecision precision, ICoordinates coord) {
        /* 239 */
        if (coord == null) {
            /* 240 */
            return null;
            /*     */
        }
        /* 242 */
        if ((this.lastKnownPart != this.iviewer.getCurrentDocumentPart()) || (!coord.equals(this.lastKnownCoordinates)))
            /*     */ {
            /* 244 */
            this.lastKnownPart = this.iviewer.getCurrentDocumentPart();
            /* 245 */
            this.lastKnownCoordinates = coord;
            /* 246 */
            this.lastKnownAddress = this.idoc.coordinatesToAddress(coord, precision);
            /*     */
        }
        /* 248 */
        return this.lastKnownAddress;
        /*     */
    }

    /*     */
    /*     */
    public Position getActivePosition()
    /*     */ {
        /* 253 */
        String address = getActiveAddress();
        /* 254 */
        if (address == null) {
            /* 255 */
            return null;
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 260 */
        BufferPoint vp = this.iviewer.getCaretViewportPoint();
        /* 261 */
        return new Position(address, vp);
        /*     */
    }

    /*     */
    /*     */
    public boolean isValidActiveAddress(String address, Object object)
    /*     */ {
        /*     */
        try {
            /* 267 */
            ICoordinates coord = this.idoc.addressToCoordinates(address);
            /* 268 */
            if (coord != null) {
                /* 269 */
                return true;
                /*     */
            }
            /*     */
        }
        /*     */ catch (Exception e) {
            /* 273 */
            logger.catching(e);
            /*     */
        }
        /*     */
        /* 276 */
        if (this.viewNavigatorHelper != null) {
            /* 277 */
            return this.viewNavigatorHelper.canHandleAddress(address);
            /*     */
        }
        /*     */
        /* 280 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean setActiveAddress(String address, Object extra, boolean record)
    /*     */ {
        /* 286 */
        ICoordinates coord = null;
        /*     */
        try {
            /* 288 */
            coord = this.idoc.addressToCoordinates(address);
            /*     */
        }
        /*     */ catch (Exception e) {
            /* 291 */
            logger.catching(e);
            /*     */
        }
        /*     */
        /* 294 */
        GlobalPosition pos0 = null;
        /* 295 */
        IViewManager viewManager = getViewManager();
        /* 296 */
        if ((viewManager != null) && (record)) {
            /* 297 */
            pos0 = viewManager.getCurrentGlobalPosition();
            /*     */
        }
        /*     */
        boolean success;
        /*     */
        boolean success;
        /* 301 */
        if (coord == null) {
            /* 302 */
            success = (this.viewNavigatorHelper != null) && (this.viewNavigatorHelper.navigateTo(address, viewManager, false));
            /*     */
        }
        /*     */
        else {
            /* 305 */
            BufferPoint vp = null;
            /* 306 */
            if ((extra instanceof BufferPoint)) {
                /* 307 */
                vp = (BufferPoint) extra;
                /*     */
            }
            /* 309 */
            success = this.iviewer.setCaretCoordinates(coord, vp, true);
            /*     */
        }
        /*     */
        /* 312 */
        if ((success) && (pos0 != null)) {
            /* 313 */
            viewManager.recordGlobalPosition(pos0);
            /*     */
            /*     */
            /* 316 */
            this.context.refreshHandlersStates();
            /*     */
        }
        /* 318 */
        return success;
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 323 */
        if (this.iviewer.verifyOperation(req)) {
            /* 324 */
            return true;
            /*     */
        }
        /*     */
        /* 327 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 329 */
                return true;
            /*     */
            case FIND_NEXT:
                /* 331 */
                return this.finder != null;
            /*     */
            case JUMP_TO:
                /* 333 */
                return true;
            /*     */
            case ITEM_FOLLOW:
                /*     */
            case ITEM_PREVIOUS:
                /*     */
            case ITEM_NEXT:
                /* 337 */
                return getActiveItem() instanceof IActionableItem;
            /*     */
        }
        /* 339 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 345 */
        if (this.iviewer.doOperation(req)) {
            /* 346 */
            return true;
            /*     */
        }
        /* 348 */
        if (!req.proceed()) {
            /* 349 */
            return false;
            /*     */
        }
        /*     */
        /* 352 */
        switch (req.getOperation()) {
            /*     */
            case FIND:
                /* 354 */
                FindTextDialog dlg = FindTextDialog.getInstance(this);
                /* 355 */
                if (dlg != null) {
                    /* 356 */
                    dlg.setFocus();
                    /* 357 */
                    return true;
                    /*     */
                }
                /* 359 */
                FindTextOptions opt = this.iviewer.getFindTextOptions(true);
                /* 360 */
                if (Strings.isBlank(opt.getSearchString()))
                    /*     */ {
                    /* 362 */
                    String selection = this.iviewer.getTextWidget().getSelectionText();
                    /* 363 */
                    if (selection != null) {
                        /* 364 */
                        int endline = selection.indexOf("\n");
                        /* 365 */
                        if (endline == 0) {
                            /* 366 */
                            endline = selection.indexOf("\n", 1);
                            /* 367 */
                            if (endline >= 0) {
                                /* 368 */
                                selection = selection.substring(1, endline);
                                /*     */
                            }
                            /*     */
                            else {
                                /* 371 */
                                selection = selection.substring(1);
                                /*     */
                            }
                            /*     */
                        }
                        /* 374 */
                        else if (endline > 0) {
                            /* 375 */
                            selection = selection.substring(0, endline);
                            /*     */
                        }
                        /* 377 */
                        opt.setSearchString(selection);
                        /*     */
                    }
                    /*     */
                }
                /* 380 */
                TextHistory history = RcpClientContext.getStandardFindTextHistory(this.context);
                /* 381 */
                dlg = new FindTextDialog(getShell(), this.finder, history, false, this, getUnit().getName());
                /* 382 */
                dlg.open(this);
                /* 383 */
                return true;
            /*     */
            /*     */
            case FIND_NEXT:
                /* 386 */
                this.finder.search(null);
                /* 387 */
                return true;
            /*     */
            /*     */
            case JUMP_TO:
                /* 390 */
                return doJumpTo();
            /*     */
            /*     */
            case ITEM_FOLLOW:
                /* 393 */
                return doItemFollow();
            /*     */
            /*     */
            case ITEM_NEXT:
                /* 396 */
                IItem item = getActiveItem();
                /* 397 */
                if (!(item instanceof IActionableItem)) {
                    /* 398 */
                    return false;
                    /*     */
                }
                /* 400 */
                return nextItem((IActionableItem) item);
            /*     */
            /*     */
            case ITEM_PREVIOUS:
                /* 403 */
                IItem item = getActiveItem();
                /* 404 */
                if (!(item instanceof IActionableItem)) {
                    /* 405 */
                    return false;
                    /*     */
                }
                /* 407 */
                return previousItem((IActionableItem) item);
            /*     */
        }
        /*     */
        /* 410 */
        return false;
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
    private boolean nextItem(IActionableItem _item)
    /*     */ {
        /* 422 */
        if ((_item == null) || (_item.getItemId() == 0L)) {
            /* 423 */
            return false;
            /*     */
        }
        /* 425 */
        long _id = _item.getItemId();
        /* 426 */
        logger.debug("Searching for next item in current part: %s", new Object[]{_item});
        /*     */
        /* 428 */
        GlobalPosition pos0 = getViewManager() == null ? null : getViewManager().getCurrentGlobalPosition();
        /*     */
        /* 430 */
        ITextDocumentPart part = this.iviewer.getCurrentDocumentPart();
        /* 431 */
        long anchorId = TextPartUtil.getFirstAnchorId(part);
        /* 432 */
        if (anchorId < 0L) {
            /* 433 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /* 437 */
        ICoordinates _coord = this.iviewer.getCaretCoordinates();
        /* 438 */
        int _line = TextPartUtil.coordinatesToLineIndex(part, _coord);
        /* 439 */
        int _column = _coord.getColumnOffset();
        /*     */
        /* 441 */
        List<? extends ILine> lines = part.getLines();
        /* 442 */
        for (int lineIndex = _line; lineIndex < lines.size(); lineIndex++) {
            /* 443 */
            ILine line = (ILine) lines.get(lineIndex);
            /*     */
            /* 445 */
            for (ITextItem item : line.getItems()) {
                /* 446 */
                if (((item instanceof IActionableItem)) && (((IActionableItem) item).getItemId() == _id) && (
                        /*     */
                        /*     */
                        /* 449 */           (lineIndex != _line) || (item.getOffset() > _column)))
                    /*     */ {
                    /*     */
                    /*     */
                    /* 453 */
                    if (pos0 != null) {
                        /* 454 */
                        getViewManager().recordGlobalPosition(pos0);
                        /*     */
                    }
                    /* 456 */
                    Coordinates coord = new Coordinates(anchorId, lineIndex, item.getOffset());
                    /* 457 */
                    return this.iviewer.setCaretCoordinates(coord, null, true);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 462 */
        logger.debug("Next item in current part was not found", new Object[0]);
        /* 463 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private boolean previousItem(IActionableItem _item) {
        /* 467 */
        if ((_item == null) || (_item.getItemId() == 0L)) {
            /* 468 */
            return false;
            /*     */
        }
        /* 470 */
        long _id = _item.getItemId();
        /* 471 */
        logger.debug("Searching for previous item in current part: %s", new Object[]{_item});
        /*     */
        /* 473 */
        GlobalPosition pos0 = getViewManager() == null ? null : getViewManager().getCurrentGlobalPosition();
        /*     */
        /* 475 */
        ITextDocumentPart part = this.iviewer.getCurrentDocumentPart();
        /* 476 */
        long anchorId = TextPartUtil.getFirstAnchorId(part);
        /* 477 */
        if (anchorId < 0L) {
            /* 478 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /* 482 */
        ICoordinates _coord = this.iviewer.getCaretCoordinates();
        /* 483 */
        int _line = TextPartUtil.coordinatesToLineIndex(part, _coord);
        /* 484 */
        int _column = _coord.getColumnOffset();
        /*     */
        /* 486 */
        List<? extends ILine> lines = part.getLines();
        /* 487 */
        for (int lineIndex = _line; lineIndex >= 0; lineIndex--) {
            /* 488 */
            ILine line = (ILine) lines.get(lineIndex);
            /*     */
            /* 490 */
            for (ITextItem item : line.getItems()) {
                /* 491 */
                if (((item instanceof IActionableItem)) && (((IActionableItem) item).getItemId() == _id) && (
                        /*     */
                        /*     */
                        /* 494 */           (lineIndex != _line) || (item.getOffset() + item.getLength() <= _column)))
                    /*     */ {
                    /*     */
                    /*     */
                    /* 498 */
                    if (pos0 != null) {
                        /* 499 */
                        getViewManager().recordGlobalPosition(pos0);
                        /*     */
                    }
                    /* 501 */
                    Coordinates coord = new Coordinates(anchorId, lineIndex, item.getOffset());
                    /* 502 */
                    return this.iviewer.setCaretCoordinates(coord, null, true);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 507 */
        logger.debug("Previous item in current part was not found", new Object[0]);
        /* 508 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public List<ICoordinates> collectItemCoordinates(long itemId) {
        /* 512 */
        List<ICoordinates> r = new ArrayList();
        /* 513 */
        long anchorId = TextPartUtil.getFirstAnchorId(this.iviewer.getCurrentDocumentPart());
        /* 514 */
        int lineIndex;
        if (anchorId >= 0L) {
            /* 515 */
            lineIndex = 0;
            /* 516 */
            for (ILine line : this.iviewer.getCurrentDocumentPart().getLines()) {
                /* 517 */
                for (ITextItem item0 : line.getItems()) {
                    /* 518 */
                    if (((item0 instanceof IActionableItem)) &&
                            /* 519 */             (((IActionableItem) item0).getItemId() == itemId)) {
                        /* 520 */
                        Coordinates coord = new Coordinates(anchorId, lineIndex, item0.getOffset());
                        /* 521 */
                        r.add(coord);
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 525 */
                lineIndex++;
                /*     */
            }
            /*     */
        }
        /* 528 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public void setCaretCoordinates(ICoordinates coord) {
        /* 532 */
        this.iviewer.setCaretCoordinates(coord, null, true);
        /*     */
    }

    /*     */
    /*     */
    public void setMenu(Menu menu)
    /*     */ {
        /* 537 */
        super.setMenu(menu);
        /* 538 */
        this.iviewer.getTextWidget().setMenu(menu);
        /*     */
    }

    /*     */
    /*     */
    public boolean setFocus()
    /*     */ {
        /* 543 */
        return this.iviewer.getTextWidget().setFocus();
        /*     */
    }

    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 548 */
        ITextDocumentPart wholePart = this.idoc.getDocumentPart(this.idoc.getFirstAnchor(), Integer.MAX_VALUE);
        /* 549 */
        return Strings.encodeUTF8(TextPartUtil.buildRawTextFromPart(wholePart));
        /*     */
    }

    /*     */
    /*     */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*     */ {
        /* 554 */
        return AbstractUnitFragment.FragmentType.TEXT;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\InteractiveTextView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */