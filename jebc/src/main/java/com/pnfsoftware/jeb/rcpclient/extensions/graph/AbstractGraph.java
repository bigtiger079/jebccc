
package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class AbstractGraph
        extends Canvas
        implements IGraph {
    private static final ILogger logger = GlobalLog.getLogger(AbstractGraph.class);
    protected static final int DEFAULT_MOUSE_WHEEL_MULTIPLIER = 15;
    private boolean kbModifier1Pressed;

    public AbstractGraph(Composite parent, int style) {
        super(parent, style);
        UI.initialize();
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseScrolled(MouseEvent e) {
                if (!AbstractGraph.this.mouseControls) {
                    return;
                }
                int delta = e.count;
                if (!AbstractGraph.this.isPrimaryModifierKeyPressed()) {
                    AbstractGraph.this.dragGraph(0, delta * 15);
                } else if (delta != 0) {
                    AbstractGraph.this.zoomGraph(delta, new Point(e.x, e.y));
                }
            }
        });
        addListener(38, new Listener() {
            public void handleEvent(Event e) {
                if (!AbstractGraph.this.mouseControls) {
                    return;
                }
                int delta = e.count * 15;
                AbstractGraph.this.dragGraph(delta, 0);
            }
        });
        addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent e) {
                if ((AbstractGraph.this.kbModifier1Pressed) && ((e.stateMask & SWT.MOD1) != 0)) {
                    AbstractGraph.this.kbModifier1Pressed = false;
                }
            }

            public void keyPressed(KeyEvent e) {
                AbstractGraph.this.kbModifier1Pressed = (e.keyCode == SWT.MOD1);
                if (!AbstractGraph.this.keyboardControls) {
                    e.doit = false;
                    return;
                }
                if (e.character == '\\') {
                    AbstractGraph.this.centerGraph();
                } else if (e.character == '[') {
                    AbstractGraph.this.zoomGraph(-1);
                } else if (e.character == ']') {
                    AbstractGraph.this.zoomGraph(1);
                } else if (((e.stateMask & SWT.MOD1) != 0) && (e.keyCode == 92)) {
                    AbstractGraph.this.zoomGraph(0);
                } else if ((e.stateMask == 0) && (e.keyCode == 16777220)) {
                    AbstractGraph.this.dragGraph(-10, 0);
                } else if ((e.stateMask == 0) && (e.keyCode == 16777219)) {
                    AbstractGraph.this.dragGraph(10, 0);
                } else if ((e.stateMask == 0) && (e.keyCode == 16777217)) {
                    AbstractGraph.this.dragGraph(0, 10);
                } else if ((e.stateMask == 0) && (e.keyCode == 16777218)) {
                    AbstractGraph.this.dragGraph(0, -10);
                } else {
                    e.doit = false;
                }
            }
        });
    }

    private boolean isPrimaryModifierKeyPressed() {
        return (this.kbModifier1Pressed) || ((UI.getKeyboardModifiersState() & SWT.MOD1) != 0);
    }

    private boolean mouseControls = true;
    private boolean keyboardControls = true;

    public void setMouseControls(boolean mouseControls) {
        this.mouseControls = mouseControls;
    }

    public boolean isMouseControls() {
        return this.mouseControls;
    }

    public void setKeyboardControls(boolean keyboardControls) {
        this.keyboardControls = keyboardControls;
    }

    public boolean isKeyboardControls() {
        return this.keyboardControls;
    }

    private List<GraphChangeListener> graphChangeListeners = new ArrayList();

    public void addGraphChangeListener(GraphChangeListener listener) {
        this.graphChangeListeners.add(listener);
    }

    public void removeGraphChangeListener(GraphChangeListener listener) {
        this.graphChangeListeners.remove(listener);
    }

    protected void notifyGraphChange() {
        for (GraphChangeListener listener : this.graphChangeListeners) {
            listener.onGraphChange(this);
        }
    }

    private List<GraphMode> modes = new ArrayList();
    private GraphMode currentMode;

    public List<GraphMode> getSupportedModes() {
        return this.modes;
    }

    protected void addSupportedMode(GraphMode mode) {
        this.modes.add(mode);
    }

    public GraphMode getMode() {
        return this.currentMode;
    }

    public int getModeId() {
        return this.currentMode == null ? 0 : this.currentMode.getId();
    }

    public GraphMode setMode(GraphMode mode) {
        if ((mode != null) && (!this.modes.contains(mode))) {
            throw new IllegalArgumentException();
        }
        GraphMode previousMode = this.currentMode;
        if (mode != this.currentMode) {
            this.currentMode = mode;
            refreshGraph();
        }
        return previousMode;
    }

    public GraphMode cycleMode() {
        if (this.modes.size() == 0) {
            return null;
        }
        if (this.currentMode == null) {
            return setMode((GraphMode) this.modes.get(0));
        }
        int index = (this.modes.indexOf(this.currentMode) + 1) % this.modes.size();
        return setMode((GraphMode) this.modes.get(index));
    }

    protected abstract Rectangle getContainerArea();

    protected abstract Rectangle generatePreview(GC paramGC, Rectangle paramRectangle, GraphStyleData paramGraphStyleData, boolean paramBoolean);

    public abstract void zoomGraph(int paramInt, Point paramPoint);
}


