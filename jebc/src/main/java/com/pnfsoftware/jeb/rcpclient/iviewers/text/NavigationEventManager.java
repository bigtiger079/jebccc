/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextItem;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedText;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.custom.CaretEvent;
/*     */ import org.eclipse.swt.custom.CaretListener;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.custom.VerifyKeyListener;
/*     */ import org.eclipse.swt.events.ControlEvent;
/*     */ import org.eclipse.swt.events.ControlListener;
/*     */ import org.eclipse.swt.events.FocusEvent;
/*     */ import org.eclipse.swt.events.FocusListener;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.VerifyEvent;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;

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
/*     */ public class NavigationEventManager
        /*     */ {
    /*  45 */   private static final ILogger logger = GlobalLog.getLogger(NavigationEventManager.class);
    /*     */
    /*     */
    /*     */   public static final int defaultScrollLineSize = 2;
    /*     */
    /*     */
    /*     */   public static final int defaultPageLineSize = 5;
    /*     */
    /*     */   public static final int defaultPageMultiplier = 3;
    /*     */
    /*     */   private static final int MAXLINE = 21474836;
    /*     */
    /*  57 */   private List<IItemListener> itemListeners = new ArrayList();
    /*  58 */   private List<IPositionListener> positionListeners = new ArrayList();
    /*  59 */   private List<VerifyKeyListener> unhandledVerifyKeyListeners = new ArrayList();
    /*     */
    /*     */   private ITextItem itemOnCaret;
    /*     */
    /*     */   private boolean documentBeingResized;
    /*     */
    /*     */   private ITextDocumentViewer textViewer;
    /*     */   private ScrollBufferManager bufferManager;
    /*     */   private StyledText text;
    /*     */   private WrappedText wrappedText;
    /*  69 */   private int scrollLineSize = 2;
    /*  70 */   private int pageLineSize = 5;
    /*  71 */   private int pageMultiplier = 3;
    /*  72 */   private boolean caretBehaviorViewportStatic = false;
    /*  73 */   private int relativePosition = 0;
    /*     */
    /*     */ boolean cancelSimpleMouseWheels;
    /*     */
    /*     */   private boolean navKeyPressed;
    /*     */
    /*  79 */   private int pendingCaretRefreshBufferOffset = -1;

    /*     */
    /*     */
    public NavigationEventManager(WrappedText wrappedText, ScrollBufferManager bufferManager, ITextDocumentViewer textViewer)
    /*     */ {
        /*  83 */
        this.wrappedText = wrappedText;
        /*  84 */
        this.bufferManager = bufferManager;
        /*  85 */
        this.textViewer = textViewer;
        /*  86 */
        this.text = wrappedText.getTextWidget();
        /*     */
    }

    /*     */
    /*     */
    public void setScrollLineSize(int scrollLineSize) {
        /*  90 */
        if (scrollLineSize <= 0) {
            /*  91 */
            scrollLineSize = 2;
            /*     */
        }
        /*     */
        /*  94 */
        this.scrollLineSize = scrollLineSize;
        /*     */
    }

    /*     */
    /*     */
    public int getScrollLineSize() {
        /*  98 */
        return this.scrollLineSize;
        /*     */
    }

    /*     */
    /*     */
    public void setPageLineSize(int pageLineSize) {
        /* 102 */
        if (pageLineSize <= 0) {
            /* 103 */
            pageLineSize = 5;
            /*     */
        }
        /*     */
        /* 106 */
        this.pageLineSize = pageLineSize;
        /*     */
    }

    /*     */
    /*     */
    public int getPageLineSize() {
        /* 110 */
        return this.pageLineSize;
        /*     */
    }

    /*     */
    /*     */
    public void setPageMultiplier(int pageMultiplier) {
        /* 114 */
        if (pageMultiplier <= 0) {
            /* 115 */
            pageMultiplier = 3;
            /*     */
        }
        /*     */
        /* 118 */
        this.pageMultiplier = pageMultiplier;
        /*     */
    }

    /*     */
    /*     */
    public int getPageMultiplier() {
        /* 122 */
        return this.pageMultiplier;
        /*     */
    }

    /*     */
    /*     */
    public void setCaretBehaviorViewportStatic(boolean caretBehaviorViewportStatic) {
        /* 126 */
        this.caretBehaviorViewportStatic = caretBehaviorViewportStatic;
        /*     */
    }

    /*     */
    /*     */
    public boolean getCaretBehaviorViewportStatic() {
        /* 130 */
        return this.caretBehaviorViewportStatic;
        /*     */
    }

    /*     */
    /*     */
    public void setCancelSimpleMouseWheels(boolean cancelSimpleMouseWheels) {
        /* 134 */
        this.cancelSimpleMouseWheels = cancelSimpleMouseWheels;
        /*     */
    }

    /*     */
    /*     */
    public boolean isCancelSimpleMouseWheels() {
        /* 138 */
        return this.cancelSimpleMouseWheels;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   void addNavigationHandlers()
    /*     */ {
        /* 146 */
        this.text.addControlListener(new ControlListener()
                /*     */ {
            /*     */
            public void controlResized(ControlEvent e) {
                /* 149 */
                NavigationEventManager.this.handleControlResized(e);
                /*     */
            }

            /*     */
            /*     */
            /*     */
            /*     */
            public void controlMoved(ControlEvent e) {
            }
            /* 155 */
        });
        /* 156 */
        this.text.addCaretListener(new CaretListener()
                /*     */ {
            /*     */
            public void caretMoved(CaretEvent event) {
                /* 159 */
                NavigationEventManager.this.handleCaretEvent(event);
                /*     */
            }
            /*     */
            /* 162 */
        });
        /* 163 */
        this.text.addKeyListener(new KeyListener()
                /*     */ {
            /*     */
            public void keyPressed(KeyEvent e) {
                /* 166 */
                NavigationEventManager.this.handleKeyEvent(e, true);
                /*     */
            }

            /*     */
            /*     */
            public void keyReleased(KeyEvent e) {
                /* 170 */
                NavigationEventManager.this.handleKeyEvent(e, false);
                /*     */
            }
            /* 172 */
        });
        /* 173 */
        this.text.addVerifyKeyListener(new VerifyKeyListener()
                /*     */ {
            /*     */
            public void verifyKey(VerifyEvent event) {
                /* 176 */
                NavigationEventManager.this.handleVerifyKeyEvent(event);
                /*     */
            }
            /*     */
            /*     */
            /* 180 */
        });
        /* 181 */
        this.text.addListener(37, new Listener()
                /*     */ {
            /*     */
            public void handleEvent(Event event) {
                /* 184 */
                NavigationEventManager.this.handleMouseWheelEvent(event);
                /*     */
            }
            /*     */
            /* 187 */
        });
        /* 188 */
        this.text.addSelectionListener(new SelectionAdapter()
                /*     */ {
            /*     */
            public void widgetSelected(SelectionEvent e) {
                /* 191 */
                NavigationEventManager.this.handleSelectionEvent(e);
                /*     */
            }
            /*     */
            /* 194 */
        });
        /* 195 */
        this.text.addFocusListener(new FocusListener()
                /*     */ {
            /*     */
            public void focusLost(FocusEvent e) {
                /* 198 */
                NavigationEventManager.this.handleFocusEvent(false, e);
                /*     */
            }

            /*     */
            /*     */
            public void focusGained(FocusEvent e)
            /*     */ {
                /* 203 */
                NavigationEventManager.this.handleFocusEvent(true, e);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    private void handleFocusEvent(boolean focusGained, FocusEvent e) {
        /* 209 */
        if (!focusGained)
            /*     */ {
            /*     */
            /* 212 */
            this.navKeyPressed = false;
            /*     */
        }
        /*     */
        /* 215 */
        processCaretPosition(this.wrappedText.getCaretOffset(), false, focusGained ? 1 : -1);
        /*     */
    }

    /*     */
    /*     */
    private void handleSelectionEvent(SelectionEvent e) {
        /* 219 */
        int index = this.wrappedText.getCaretLine();
        /* 220 */
        if ((index <= 0) || (index >= this.wrappedText.getLineCount() - 1))
            /*     */ {
            /* 222 */
            logger.debug("selectionChanged: %s - need to fetch data", new Object[]{e});
            /* 223 */
            this.bufferManager.scroll(0, 0, false, true);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    private void handleMouseWheelEvent(Event e)
    /*     */ {
        /* 231 */
        int delta = this.scrollLineSize;
        /* 232 */
        if ((e.stateMask & SWT.MOD1) != 0)
            /*     */ {
            /*     */
            /* 235 */
            int maxline = this.wrappedText.getMaxVisibleLineCount();
            /* 236 */
            delta = maxline - 1;
            /*     */
            /* 238 */
            int lastLine = this.wrappedText.getLineCount();
            /* 239 */
            if (lastLine <= this.wrappedText.getTopIndex() + maxline)
                /*     */ {
                /* 241 */
                delta = this.wrappedText.getTopIndex() + this.relativePosition - this.wrappedText.getCaretLine();
                /*     */
            }
            /* 243 */
            else if (this.wrappedText.getTopIndex() == 0)
                /*     */ {
                /* 245 */
                if (this.wrappedText.getCaretLine() == this.relativePosition)
                    /*     */ {
                    /* 247 */
                    delta = 0;
                    /*     */
                }
                /*     */
                else {
                    /* 250 */
                    delta = this.wrappedText.getCaretLine() - this.relativePosition;
                    /*     */
                }
                /*     */
            }
            /* 253 */
            if (delta != 0) {
                /* 254 */
                if (e.count > 0) {
                    /* 255 */
                    this.bufferManager.scroll(0, -delta, false, true);
                    /*     */
                }
                /* 257 */
                else if (e.count < 0) {
                    /* 258 */
                    this.bufferManager.scroll(0, delta, false, true);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        else {
            /* 263 */
            int caretMove = 0;
            /* 264 */
            boolean selecting = true;
            /* 265 */
            boolean shiftPressed = (e.stateMask & 0x20000) != 0;
            /* 266 */
            if (((shiftPressed) && (!this.caretBehaviorViewportStatic)) || ((!shiftPressed) && (this.caretBehaviorViewportStatic))) {
                /* 267 */
                caretMove = delta;
                /* 268 */
                selecting = false;
                /*     */
            }
            /*     */
            /* 271 */
            if (e.count > 0) {
                /* 272 */
                this.bufferManager.scroll(-delta, -caretMove, selecting, true);
                /*     */
                /*     */
            }
            /* 275 */
            else if (e.count < 0) {
                /* 276 */
                this.bufferManager.scroll(delta, caretMove, selecting, true);
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
            /* 286 */
            if (this.cancelSimpleMouseWheels) {
                /* 287 */
                e.doit = false;
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void handleControlResized(ControlEvent e)
    /*     */ {
        /* 294 */
        if (this.documentBeingResized) {
            /* 295 */
            return;
            /*     */
        }
        /*     */
        try
            /*     */ {
            /* 299 */
            this.documentBeingResized = true;
            /* 300 */
            int linecnt1 = this.wrappedText.getFullyVisibleLineCount();
            /* 301 */
            int linecnt2 = this.wrappedText.getVisibleLineCount();
            /* 302 */
            logger.debug("controlResized: max line count=%d, current line count=%d", new Object[]{Integer.valueOf(linecnt1), Integer.valueOf(linecnt2)});
            /*     */
            /*     */
            /* 305 */
            this.bufferManager.viewAtBufferLine(this.wrappedText.getTopIndex());
            /*     */
            /* 307 */
            acknowledgeCaretPositionChange(false, this.wrappedText.getCaretOffset());
            /*     */
        }
        /*     */ finally {
            /* 310 */
            this.documentBeingResized = false;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    private void handleKeyEvent(KeyEvent e, boolean pressed)
    /*     */ {
        /* 317 */
        switch (e.keyCode)
            /*     */ {
            /*     */
            /*     */
            case 16777217:
                /*     */
            case 16777218:
                /*     */
            case 16777219:
                /*     */
            case 16777220:
                /*     */
            case 16777221:
                /*     */
            case 16777222:
                /* 326 */
                if ((this.navKeyPressed) && (!pressed) && (this.pendingCaretRefreshBufferOffset != -1)) {
                    /* 327 */
                    this.navKeyPressed = false;
                    /* 328 */
                    processCaretPosition(this.pendingCaretRefreshBufferOffset, true, 2);
                    /* 329 */
                    this.pendingCaretRefreshBufferOffset = -1;
                    /*     */
                }
                /* 331 */
                else if (this.navKeyPressed != pressed)
                    /*     */ {
                    /* 333 */
                    if (!pressed) {
                        /* 334 */
                        this.navKeyPressed = false;
                        /*     */
                    }
                    /* 336 */
                    else if (this.text.isFocusControl()) {
                        /* 337 */
                        this.navKeyPressed = true;
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /*     */
                break;
            /*     */
        }
        /*     */
        /*     */
    }

    /*     */
    /*     */
    private void handleVerifyKeyEvent(VerifyEvent e)
    /*     */ {
        /* 348 */
        int caretOffset = this.wrappedText.getCaretOffset();
        /* 349 */
        int lineFirstVisible = this.wrappedText.getTopIndex();
        /* 350 */
        int lineLastVisible = this.wrappedText.getBottomIndex();
        /* 351 */
        int caretLine = this.wrappedText.getLineAtOffset(caretOffset);
        /* 352 */
        int caretOffsetInLine = caretOffset - this.wrappedText.getOffsetAtLine(caretLine);
        /*     */
        /* 354 */
        boolean selecting = (e.stateMask & 0x20000) != 0;
        /*     */
        /* 356 */
        boolean moveViewport = (e.stateMask & SWT.MOD1) != 0;
        /*     */
        /*     */
        /*     */
        /* 360 */
        switch (e.keyCode) {
            /*     */
            case 16777217:
                /*     */
            case 16777218:
                /*     */
            case 16777219:
                /*     */
            case 16777220:
                /*     */
            case 16777221:
                /*     */
            case 16777222:
                /*     */
            case 16777223:
                /*     */
            case 16777224:
                /* 369 */
                caretLine = this.bufferManager.sanitizeTopLine(caretLine, selecting, moveViewport);
                /* 370 */
                break;
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 375 */
        int atttemptGoOut = 0;
        /*     */
        int delta;
        /* 377 */
        switch (e.keyCode)
            /*     */ {
            /*     */
            /*     */
            /*     */
            /*     */
            case 16777217:
                /*     */
            case 16777218:
                /* 384 */
                atttemptGoOut = 0;
                /* 385 */
                if ((e.keyCode == 16777218) && (this.wrappedText.getCaretLine() == this.wrappedText.getBottomIndex())) {
                    /* 386 */
                    atttemptGoOut = 1;
                    /*     */
                }
                /* 388 */
                else if ((e.keyCode == 16777217) && (this.wrappedText.getCaretLine() == 0))
                    /* 389 */ atttemptGoOut = -1;
                /*     */
                boolean leavingViewport;
                /*     */
                int delta;
                /* 392 */
                boolean leavingViewport;
                if (e.keyCode == 16777217) {
                    /* 393 */
                    int delta = -1;
                    /* 394 */
                    leavingViewport = caretLine <= lineFirstVisible;
                    /*     */
                }
                /*     */
                else {
                    /* 397 */
                    delta = 1;
                    /* 398 */
                    leavingViewport = caretLine >= lineLastVisible;
                    /*     */
                }
                /*     */
                /*     */
                /*     */
                /* 403 */
                if ((leavingViewport) || (moveViewport)) {
                    /* 404 */
                    int windowScroll = delta;
                    /*     */
                    /* 406 */
                    int caretScroll = delta;
                    /* 407 */
                    if (((moveViewport) && (selecting)) || ((this.wrappedText.hasSelection()) && (!selecting)))
                        /*     */ {
                        /*     */
                        /* 410 */
                        caretScroll = 0;
                        /*     */
                    }
                    /* 412 */
                    this.bufferManager.scroll(windowScroll, caretScroll, selecting, true);
                    /* 413 */
                    e.doit = false;
                    /*     */
                }
                /* 415 */
                else if ((e.stateMask & 0x20000) == 0)
                    /*     */ {
                    /* 417 */
                    this.bufferManager.moveCaretWithToplineUpdate(delta, selecting);
                    /* 418 */
                    e.doit = false;
                    /*     */
                }
                /*     */
                /*     */
                /*     */
                break;
            /*     */
            case 16777221:
                /*     */
            case 16777222:
                /* 425 */
                delta = e.keyCode == 16777221 ? -this.pageLineSize : this.pageLineSize;
                /* 426 */
                if ((e.stateMask & SWT.MOD1) != 0)
                    /*     */ {
                    /* 428 */
                    delta *= this.pageMultiplier;
                    /*     */
                }
                /* 430 */
                this.bufferManager.scroll(delta, delta, selecting, false);
                /* 431 */
                e.doit = false;
                /* 432 */
                break;
            /*     */
            /*     */
            /*     */
            /*     */
            case 16777223:
                /* 437 */
                if ((e.stateMask & SWT.MOD1) != 0) {
                    /* 438 */
                    if ((e.stateMask & 0x20000) != 0) {
                        /* 439 */
                        this.bufferManager.scroll(-21474836, -21474836, true, false);
                        /*     */
                    }
                    /*     */
                    else
                        /*     */ {
                        /* 443 */
                        this.bufferManager.viewAtStartOfDocument();
                        /*     */
                    }
                    /* 445 */
                    e.doit = false;
                    /*     */
                }
                /*     */
                /*     */
                /*     */
                break;
            /*     */
            case 16777224:
                /* 451 */
                if ((e.stateMask & SWT.MOD1) != 0) {
                    /* 452 */
                    if ((e.stateMask & 0x20000) != 0) {
                        /* 453 */
                        this.bufferManager.scroll(21474836, 21474836, true, false);
                        /*     */
                    }
                    /*     */
                    else {
                        /* 456 */
                        this.bufferManager.viewAtEndOfDocument();
                        /*     */
                    }
                    /* 458 */
                    e.doit = false;
                    /*     */
                }
                /*     */
                /*     */
                /*     */
                break;
            /*     */
            case 16777219:
                /* 464 */
                if (caretOffsetInLine <= 0) {
                    /* 465 */
                    if ((e.stateMask & SWT.MOD1) == 0) {
                        /* 466 */
                        e.doit = false;
                        /*     */
                    }
                    /*     */
                    else
                        /*     */ {
                        /* 470 */
                        this.bufferManager.scroll(0, 0, selecting, true);
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /*     */
                break;
            /*     */
            case 16777220:
                /* 476 */
                if (caretOffsetInLine >= this.wrappedText.getLine(caretLine).length()) {
                    /* 477 */
                    if ((e.stateMask & SWT.MOD1) == 0) {
                        /* 478 */
                        e.doit = false;
                        /*     */
                    }
                    /*     */
                    else
                        /*     */ {
                        /* 482 */
                        this.bufferManager.scroll(0, 0, selecting, true);
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                break;
            /*     */
            case 262144:
                /*     */
            case 4194304:
                /* 492 */
                this.relativePosition = (this.wrappedText.getCaretLine() - this.wrappedText.getTopIndex());
                /* 493 */
                logger.i("ControlKey: e=%s", new Object[]{e});
                /* 494 */
                break;
            /*     */
            /*     */
            /*     */
            default:
                /* 498 */
                for (VerifyKeyListener listener : this.unhandledVerifyKeyListeners) {
                    /* 499 */
                    listener.verifyKey(e);
                    /*     */
                }
                /*     */
        }
        /*     */
        /* 503 */
        if (atttemptGoOut != 0) {
            /* 504 */
            for (IPositionListener listener : this.positionListeners) {
                /* 505 */
                listener.positionUnchangedAttemptBreakout(this.textViewer, atttemptGoOut);
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void handleCaretEvent(CaretEvent e) {
        /* 511 */
        acknowledgeCaretPositionChange(true, e.caretOffset);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void acknowledgeCaretPositionChange(boolean realPositionChange, int bufferOffset)
    /*     */ {
        /* 520 */
        if (realPositionChange) {
            /* 521 */
            this.wrappedText.updateCaretColumn();
            /*     */
        }
        /* 523 */
        processCaretPosition(bufferOffset, realPositionChange, 0);
        /*     */
    }

    /*     */
    /*     */
    private void processCaretPosition(int bufferOffset, boolean positionChanged, int focusChange) {
        /* 527 */
        logger.i("processCaretChange: viewer=%d line=%d (posChanged=%b focusChange=%d)", new Object[]{
/* 528 */       Integer.valueOf(this.text.hashCode()), Integer.valueOf(this.wrappedText.getCaretLine()), Boolean.valueOf(positionChanged), Integer.valueOf(focusChange)});
        /*     */
        /*     */
        /* 531 */
        if ((!positionChanged) && (focusChange == 0) && (this.itemOnCaret == null)) {
            /* 532 */
            return;
            /*     */
        }
        /*     */
        /* 535 */
        if ((!positionChanged) && (focusChange == 2)) {
            /* 536 */
            logger.i("(positionChanged is false && always focused -> aborting processCaretPosition)", new Object[0]);
            /* 537 */
            return;
            /*     */
        }
        /*     */
        /* 540 */
        if (this.navKeyPressed) {
            /* 541 */
            logger.i("(navKeyPressed is true, aborting processCaretPosition)", new Object[0]);
            /* 542 */
            this.pendingCaretRefreshBufferOffset = bufferOffset;
            /* 543 */
            return;
            /*     */
        }
        /*     */
        /* 546 */
        ITextItem itemCurrent = null;
        /* 547 */
        if ((positionChanged) || (focusChange >= 0)) {
            /* 548 */
            itemCurrent = this.textViewer.getItemAt(bufferOffset);
            /*     */
        }
        /* 550 */
        logger.i("    itemCurrent=%s (saved=%s)", new Object[]{itemCurrent, this.itemOnCaret});
        /*     */
        ItemEvent itemEvent;
        /* 552 */
        ItemEvent itemEvent;
        if (itemCurrent == null) {
            /* 553 */
            if (this.itemOnCaret != null) {
                /* 554 */
                itemEvent = new ItemEvent(this.itemOnCaret, 0);
                /* 555 */
                for (IItemListener listener : this.itemListeners) {
                    /* 556 */
                    listener.notifyItemEvent(this.textViewer, itemEvent);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 560 */
        else if ((itemCurrent != this.itemOnCaret) || (focusChange == 1)) {
            ItemEvent itemEvent;
            /* 561 */
            if (this.itemOnCaret != null) {
                /* 562 */
                itemEvent = new ItemEvent(this.itemOnCaret, 0);
                /* 563 */
                for (IItemListener listener : this.itemListeners) {
                    /* 564 */
                    listener.notifyItemEvent(this.textViewer, itemEvent);
                    /*     */
                }
                /*     */
            }
            /* 567 */
            itemEvent = new ItemEvent(itemCurrent, 1);
            /* 568 */
            for (IItemListener listener : this.itemListeners) {
                /* 569 */
                listener.notifyItemEvent(this.textViewer, itemEvent);
                /*     */
            }
            /*     */
        }
        /*     */
        /* 573 */
        this.itemOnCaret = itemCurrent;
        /*     */
        /*     */
        /*     */
        /* 577 */
        ICoordinates coordinates = this.wrappedText.getCoordinates(bufferOffset);
        /* 578 */
        for (IPositionListener listener : this.positionListeners) {
            /* 579 */
            listener.positionChanged(this.textViewer, coordinates, focusChange);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void addItemListener(IItemListener listener) {
        /* 584 */
        if (!this.itemListeners.contains(listener)) {
            /* 585 */
            this.itemListeners.add(listener);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void removeItemListener(IItemListener listener) {
        /* 590 */
        this.itemListeners.remove(listener);
        /*     */
    }

    /*     */
    /*     */
    public void addPositionListener(IPositionListener listener) {
        /* 594 */
        if (!this.positionListeners.contains(listener)) {
            /* 595 */
            this.positionListeners.add(listener);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void removePositionListener(IPositionListener listener) {
        /* 600 */
        this.positionListeners.remove(listener);
        /*     */
    }

    /*     */
    /*     */
    public void addUnhandledVerifyKeyListener(VerifyKeyListener listener) {
        /* 604 */
        if (!this.unhandledVerifyKeyListeners.contains(listener)) {
            /* 605 */
            this.unhandledVerifyKeyListeners.add(listener);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void removeUnhandledVerifyKeyListener(VerifyKeyListener listener) {
        /* 610 */
        this.unhandledVerifyKeyListeners.remove(listener);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\NavigationEventManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */