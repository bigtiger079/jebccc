package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedText;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class NavigationEventManager {
    private static final ILogger logger = GlobalLog.getLogger(NavigationEventManager.class);
    public static final int defaultScrollLineSize = 2;
    public static final int defaultPageLineSize = 5;
    public static final int defaultPageMultiplier = 3;
    private static final int MAXLINE = 21474836;
    private List<IItemListener> itemListeners = new ArrayList<>();
    private List<IPositionListener> positionListeners = new ArrayList<>();
    private List<VerifyKeyListener> unhandledVerifyKeyListeners = new ArrayList<>();
    private ITextItem itemOnCaret;
    private boolean documentBeingResized;
    private ITextDocumentViewer textViewer;
    private ScrollBufferManager bufferManager;
    private StyledText text;
    private WrappedText wrappedText;
    private int scrollLineSize = 2;
    private int pageLineSize = 5;
    private int pageMultiplier = 3;
    private boolean caretBehaviorViewportStatic = false;
    private int relativePosition = 0;
    boolean cancelSimpleMouseWheels;
    private boolean navKeyPressed;
    private int pendingCaretRefreshBufferOffset = -1;

    public NavigationEventManager(WrappedText wrappedText, ScrollBufferManager bufferManager, ITextDocumentViewer textViewer) {
        this.wrappedText = wrappedText;
        this.bufferManager = bufferManager;
        this.textViewer = textViewer;
        this.text = wrappedText.getTextWidget();
    }

    public void setScrollLineSize(int scrollLineSize) {
        if (scrollLineSize <= 0) {
            scrollLineSize = 2;
        }
        this.scrollLineSize = scrollLineSize;
    }

    public int getScrollLineSize() {
        return this.scrollLineSize;
    }

    public void setPageLineSize(int pageLineSize) {
        if (pageLineSize <= 0) {
            pageLineSize = 5;
        }
        this.pageLineSize = pageLineSize;
    }

    public int getPageLineSize() {
        return this.pageLineSize;
    }

    public void setPageMultiplier(int pageMultiplier) {
        if (pageMultiplier <= 0) {
            pageMultiplier = 3;
        }
        this.pageMultiplier = pageMultiplier;
    }

    public int getPageMultiplier() {
        return this.pageMultiplier;
    }

    public void setCaretBehaviorViewportStatic(boolean caretBehaviorViewportStatic) {
        this.caretBehaviorViewportStatic = caretBehaviorViewportStatic;
    }

    public boolean getCaretBehaviorViewportStatic() {
        return this.caretBehaviorViewportStatic;
    }

    public void setCancelSimpleMouseWheels(boolean cancelSimpleMouseWheels) {
        this.cancelSimpleMouseWheels = cancelSimpleMouseWheels;
    }

    public boolean isCancelSimpleMouseWheels() {
        return this.cancelSimpleMouseWheels;
    }

    void addNavigationHandlers() {
        this.text.addControlListener(new ControlListener() {
            public void controlResized(ControlEvent e) {
                NavigationEventManager.this.handleControlResized(e);
            }

            public void controlMoved(ControlEvent e) {
            }
        });
        this.text.addCaretListener(new CaretListener() {
            public void caretMoved(CaretEvent event) {
                NavigationEventManager.this.handleCaretEvent(event);
            }
        });
        this.text.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                NavigationEventManager.this.handleKeyEvent(e, true);
            }

            public void keyReleased(KeyEvent e) {
                NavigationEventManager.this.handleKeyEvent(e, false);
            }
        });
        this.text.addVerifyKeyListener(new VerifyKeyListener() {
            public void verifyKey(VerifyEvent event) {
                NavigationEventManager.this.handleVerifyKeyEvent(event);
            }
        });
        this.text.addListener(37, new Listener() {
            public void handleEvent(Event event) {
                NavigationEventManager.this.handleMouseWheelEvent(event);
            }
        });
        this.text.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                NavigationEventManager.this.handleSelectionEvent(e);
            }
        });
        this.text.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                NavigationEventManager.this.handleFocusEvent(false, e);
            }

            public void focusGained(FocusEvent e) {
                NavigationEventManager.this.handleFocusEvent(true, e);
            }
        });
    }

    private void handleFocusEvent(boolean focusGained, FocusEvent e) {
        if (!focusGained) {
            this.navKeyPressed = false;
        }
        processCaretPosition(this.wrappedText.getCaretOffset(), false, focusGained ? 1 : -1);
    }

    private void handleSelectionEvent(SelectionEvent e) {
        int index = this.wrappedText.getCaretLine();
        if ((index <= 0) || (index >= this.wrappedText.getLineCount() - 1)) {
            logger.debug("selectionChanged: %s - need to fetch data", new Object[]{e});
            this.bufferManager.scroll(0, 0, false, true);
        }
    }

    private void handleMouseWheelEvent(Event e) {
        int delta = this.scrollLineSize;
        if ((e.stateMask & SWT.MOD1) != 0) {
            int maxline = this.wrappedText.getMaxVisibleLineCount();
            delta = maxline - 1;
            int lastLine = this.wrappedText.getLineCount();
            if (lastLine <= this.wrappedText.getTopIndex() + maxline) {
                delta = this.wrappedText.getTopIndex() + this.relativePosition - this.wrappedText.getCaretLine();
            } else if (this.wrappedText.getTopIndex() == 0) {
                if (this.wrappedText.getCaretLine() == this.relativePosition) {
                    delta = 0;
                } else {
                    delta = this.wrappedText.getCaretLine() - this.relativePosition;
                }
            }
            if (delta != 0) {
                if (e.count > 0) {
                    this.bufferManager.scroll(0, -delta, false, true);
                } else if (e.count < 0) {
                    this.bufferManager.scroll(0, delta, false, true);
                }
            }
        } else {
            int caretMove = 0;
            boolean selecting = true;
            boolean shiftPressed = (e.stateMask & 0x20000) != 0;
            if (((shiftPressed) && (!this.caretBehaviorViewportStatic)) || ((!shiftPressed) && (this.caretBehaviorViewportStatic))) {
                caretMove = delta;
                selecting = false;
            }
            if (e.count > 0) {
                this.bufferManager.scroll(-delta, -caretMove, selecting, true);
            } else if (e.count < 0) {
                this.bufferManager.scroll(delta, caretMove, selecting, true);
            }
            if (this.cancelSimpleMouseWheels) {
                e.doit = false;
            }
        }
    }

    private void handleControlResized(ControlEvent e) {
        if (this.documentBeingResized) {
            return;
        }
        try {
            this.documentBeingResized = true;
            int linecnt1 = this.wrappedText.getFullyVisibleLineCount();
            int linecnt2 = this.wrappedText.getVisibleLineCount();
            logger.debug("controlResized: max line count=%d, current line count=%d", new Object[]{Integer.valueOf(linecnt1), Integer.valueOf(linecnt2)});
            this.bufferManager.viewAtBufferLine(this.wrappedText.getTopIndex());
            acknowledgeCaretPositionChange(false, this.wrappedText.getCaretOffset());
        } finally {
            this.documentBeingResized = false;
        }
    }

    private void handleKeyEvent(KeyEvent e, boolean pressed) {
        switch (e.keyCode) {
            case 16777217:
            case 16777218:
            case 16777219:
            case 16777220:
            case 16777221:
            case 16777222:
                if ((this.navKeyPressed) && (!pressed) && (this.pendingCaretRefreshBufferOffset != -1)) {
                    this.navKeyPressed = false;
                    processCaretPosition(this.pendingCaretRefreshBufferOffset, true, 2);
                    this.pendingCaretRefreshBufferOffset = -1;
                } else if (this.navKeyPressed != pressed) {
                    if (!pressed) {
                        this.navKeyPressed = false;
                    } else if (this.text.isFocusControl()) {
                        this.navKeyPressed = true;
                    }
                }
                break;
        }
    }

    private void handleVerifyKeyEvent(VerifyEvent e) {
        int caretOffset = this.wrappedText.getCaretOffset();
        int lineFirstVisible = this.wrappedText.getTopIndex();
        int lineLastVisible = this.wrappedText.getBottomIndex();
        int caretLine = this.wrappedText.getLineAtOffset(caretOffset);
        int caretOffsetInLine = caretOffset - this.wrappedText.getOffsetAtLine(caretLine);
        boolean selecting = (e.stateMask & 0x20000) != 0;
        boolean moveViewport = (e.stateMask & SWT.MOD1) != 0;
        switch (e.keyCode) {
            case 16777217:
            case 16777218:
            case 16777219:
            case 16777220:
            case 16777221:
            case 16777222:
            case 16777223:
            case 16777224:
                caretLine = this.bufferManager.sanitizeTopLine(caretLine, selecting, moveViewport);
                break;
        }
        int atttemptGoOut = 0;
        int delta;
        switch (e.keyCode) {
            case 16777217:
            case 16777218:
                atttemptGoOut = 0;
                if ((e.keyCode == 16777218) && (this.wrappedText.getCaretLine() == this.wrappedText.getBottomIndex())) {
                    atttemptGoOut = 1;
                } else if ((e.keyCode == 16777217) && (this.wrappedText.getCaretLine() == 0)) atttemptGoOut = -1;
                boolean leavingViewport;
                if (e.keyCode == 16777217) {
                    delta = -1;
                    leavingViewport = caretLine <= lineFirstVisible;
                } else {
                    delta = 1;
                    leavingViewport = caretLine >= lineLastVisible;
                }
                if ((leavingViewport) || (moveViewport)) {
                    int windowScroll = delta;
                    int caretScroll = delta;
                    if (((moveViewport) && (selecting)) || ((this.wrappedText.hasSelection()) && (!selecting))) {
                        caretScroll = 0;
                    }
                    this.bufferManager.scroll(windowScroll, caretScroll, selecting, true);
                    e.doit = false;
                } else if ((e.stateMask & 0x20000) == 0) {
                    this.bufferManager.moveCaretWithToplineUpdate(delta, selecting);
                    e.doit = false;
                }
                break;
            case 16777221:
            case 16777222:
                delta = e.keyCode == 16777221 ? -this.pageLineSize : this.pageLineSize;
                if ((e.stateMask & SWT.MOD1) != 0) {
                    delta *= this.pageMultiplier;
                }
                this.bufferManager.scroll(delta, delta, selecting, false);
                e.doit = false;
                break;
            case 16777223:
                if ((e.stateMask & SWT.MOD1) != 0) {
                    if ((e.stateMask & 0x20000) != 0) {
                        this.bufferManager.scroll(-21474836, -21474836, true, false);
                    } else {
                        this.bufferManager.viewAtStartOfDocument();
                    }
                    e.doit = false;
                }
                break;
            case 16777224:
                if ((e.stateMask & SWT.MOD1) != 0) {
                    if ((e.stateMask & 0x20000) != 0) {
                        this.bufferManager.scroll(21474836, 21474836, true, false);
                    } else {
                        this.bufferManager.viewAtEndOfDocument();
                    }
                    e.doit = false;
                }
                break;
            case 16777219:
                if (caretOffsetInLine <= 0) {
                    if ((e.stateMask & SWT.MOD1) == 0) {
                        e.doit = false;
                    } else {
                        this.bufferManager.scroll(0, 0, selecting, true);
                    }
                }
                break;
            case 16777220:
                if (caretOffsetInLine >= this.wrappedText.getLine(caretLine).length()) {
                    if ((e.stateMask & SWT.MOD1) == 0) {
                        e.doit = false;
                    } else {
                        this.bufferManager.scroll(0, 0, selecting, true);
                    }
                }
                break;
            case 262144:
            case 4194304:
                this.relativePosition = (this.wrappedText.getCaretLine() - this.wrappedText.getTopIndex());
                logger.i("ControlKey: e=%s", new Object[]{e});
                break;
            default:
                for (VerifyKeyListener listener : this.unhandledVerifyKeyListeners) {
                    listener.verifyKey(e);
                }
        }
        if (atttemptGoOut != 0) {
            for (IPositionListener listener : this.positionListeners) {
                listener.positionUnchangedAttemptBreakout(this.textViewer, atttemptGoOut);
            }
        }
    }

    private void handleCaretEvent(CaretEvent e) {
        acknowledgeCaretPositionChange(true, e.caretOffset);
    }

    public void acknowledgeCaretPositionChange(boolean realPositionChange, int bufferOffset) {
        if (realPositionChange) {
            this.wrappedText.updateCaretColumn();
        }
        processCaretPosition(bufferOffset, realPositionChange, 0);
    }

    private void processCaretPosition(int bufferOffset, boolean positionChanged, int focusChange) {
        logger.i("processCaretChange: viewer=%d line=%d (posChanged=%b focusChange=%d)", new Object[]{Integer.valueOf(this.text.hashCode()), Integer.valueOf(this.wrappedText.getCaretLine()), Boolean.valueOf(positionChanged), Integer.valueOf(focusChange)});
        if ((!positionChanged) && (focusChange == 0) && (this.itemOnCaret == null)) {
            return;
        }
        if ((!positionChanged) && (focusChange == 2)) {
            logger.i("(positionChanged is false && always focused -> aborting processCaretPosition)", new Object[0]);
            return;
        }
        if (this.navKeyPressed) {
            logger.i("(navKeyPressed is true, aborting processCaretPosition)", new Object[0]);
            this.pendingCaretRefreshBufferOffset = bufferOffset;
            return;
        }
        ITextItem itemCurrent = null;
        if ((positionChanged) || (focusChange >= 0)) {
            itemCurrent = this.textViewer.getItemAt(bufferOffset);
        }
        logger.i("    itemCurrent=%s (saved=%s)", new Object[]{itemCurrent, this.itemOnCaret});
        ItemEvent itemEvent;
        if (itemCurrent == null) {
            if (this.itemOnCaret != null) {
                itemEvent = new ItemEvent(this.itemOnCaret, 0);
                for (IItemListener listener : this.itemListeners) {
                    listener.notifyItemEvent(this.textViewer, itemEvent);
                }
            }
        } else if ((itemCurrent != this.itemOnCaret) || (focusChange == 1)) {
            if (this.itemOnCaret != null) {
                itemEvent = new ItemEvent(this.itemOnCaret, 0);
                for (IItemListener listener : this.itemListeners) {
                    listener.notifyItemEvent(this.textViewer, itemEvent);
                }
            }
            itemEvent = new ItemEvent(itemCurrent, 1);
            for (IItemListener listener : this.itemListeners) {
                listener.notifyItemEvent(this.textViewer, itemEvent);
            }
        }
        this.itemOnCaret = itemCurrent;
        ICoordinates coordinates = this.wrappedText.getCoordinates(bufferOffset);
        for (IPositionListener listener : this.positionListeners) {
            listener.positionChanged(this.textViewer, coordinates, focusChange);
        }
    }

    public void addItemListener(IItemListener listener) {
        if (!this.itemListeners.contains(listener)) {
            this.itemListeners.add(listener);
        }
    }

    public void removeItemListener(IItemListener listener) {
        this.itemListeners.remove(listener);
    }

    public void addPositionListener(IPositionListener listener) {
        if (!this.positionListeners.contains(listener)) {
            this.positionListeners.add(listener);
        }
    }

    public void removePositionListener(IPositionListener listener) {
        this.positionListeners.remove(listener);
    }

    public void addUnhandledVerifyKeyListener(VerifyKeyListener listener) {
        if (!this.unhandledVerifyKeyListeners.contains(listener)) {
            this.unhandledVerifyKeyListeners.add(listener);
        }
    }

    public void removeUnhandledVerifyKeyListener(VerifyKeyListener listener) {
        this.unhandledVerifyKeyListeners.remove(listener);
    }
}


