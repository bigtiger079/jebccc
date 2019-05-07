
package com.pnfsoftware.jeb.rcpclient.extensions.graph;


import com.pnfsoftware.jeb.rcpclient.extensions.SwtRegistry;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.IZoomable;
import com.pnfsoftware.jeb.util.base.Flags;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


public final class GraphNode
        extends Composite
        implements IZoomable, IGraphNode {
    private static final ILogger logger = GlobalLog.getLogger(GraphNode.class);

    int id = -1;

    Graph graph;

    String name;

    int incount;

    int outcount;

    Composite wBar;

    Composite wTitle;

    Composite wDragger;
    Composite wResizer;
    Composite contents;
    boolean dragging;
    int draggingX;
    int draggingY;
    boolean resizing;
    int resizingX;
    int resizingY;
    private final int bw = 2;
    private final int bh = 2;
    private final int bsw = 1;
    private final int bsh = 1;

    private Color borderColor;

    private Color borderShadeColor;

    private Color activeBorderColor;
    private Color activeBorderShadeColor;
    boolean customBorders;
    boolean hasTitle;
    boolean isResizable;
    boolean isDraggable;
    boolean hasControls;
    private final double titleHeight = 10.0D;
    private final double handleSize = 8.0D;


    boolean active;


    public GraphNode(Graph parent) {

        this(parent, 2065, null);

    }


    public GraphNode(Graph parent, String name) {

        this(parent, 2065, name);

    }


    public GraphNode(Graph parent, int styles) {

        this(parent, styles, null);

    }


    public GraphNode(Graph parent, int style, String name) {

        super(parent, 0x20000000 | style & 0x800);

        this.customBorders = ((style & 0x800) == 0);

        this.hasTitle = ((style & 0x20) == 32);

        this.isResizable = ((style & 0x10) == 16);

        this.isDraggable = ((style & 0x1) == 1);

        this.hasControls = ((this.isResizable) || (this.isDraggable));


        this.graph = parent;

        this.name = name;


        if (this.customBorders) {

            this.borderColor = this.graph.getStyleData().cBorder;

            this.borderShadeColor = this.graph.getStyleData().cBorderShade;

            this.activeBorderColor = this.graph.getStyleData().cActiveBorder;

            this.activeBorderShadeColor = this.graph.getStyleData().cActiveBorderShade;

        }


        setBackgroundMode(2);


        FormLayout layout = new FormLayout();

        setLayout(layout);


        this.wBar = new Composite(this, 0);

        this.wBar.setLayout(new FormLayout());

        this.wBar.setBackgroundMode(2);

        FormData formData = new FormData();

        formData.top = new FormAttachment(0, 2);

        formData.right = new FormAttachment(100, -3);

        formData.bottom = new FormAttachment(100, -3);

        formData.width = (this.hasControls ? 8 : 0);

        this.wBar.setLayoutData(formData);

        this.wBar.addPaintListener(new PaintListener() {


            public void paintControl(PaintEvent e) {
            }


        });

        this.wTitle = new Composite(this, 0);

        this.wTitle.setLayout(new FillLayout());

        this.wTitle.setBackground(SwtRegistry.getInstance().getColor(0, 0, 255));

        formData = new FormData();

        formData.left = new FormAttachment(0, 2);

        formData.top = new FormAttachment(0, 2);

        formData.right = new FormAttachment(this.wBar);

        formData.height = (this.hasTitle ? 10 : 0);

        this.wTitle.setLayoutData(formData);

        this.wTitle.addPaintListener(new PaintListener() {


            public void paintControl(PaintEvent e) {
            }


        });

        this.wResizer = new Composite(this.wBar, 0);

        this.wResizer.setLayout(new FillLayout());

        formData = new FormData();

        formData.bottom = new FormAttachment(100, 0);

        formData.right = new FormAttachment(100, 0);

        formData.height = 8;

        formData.width = 8;

        this.wResizer.setLayoutData(formData);

        this.wResizer.setCursor(new Cursor(getDisplay(), 15));

        this.wResizer.addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {

                Rectangle r = GraphNode.this.wResizer.getClientArea();

                if (GraphNode.this.customBorders) {

                    e.gc.setBackground(GraphNode.this.active ? GraphNode.this.activeBorderColor : GraphNode.this.borderColor);

                } else {

                    e.gc.setBackground(e.display.getSystemColor(16));

                }

                e.gc.fillPolygon(new int[]{0, r.height, r.width, 0, r.width, r.height});

            }

        });

        this.wResizer.setVisible(canResize());


        this.wDragger = new Composite(this.wBar, 0);

        this.wDragger.setLayout(new FillLayout());

        formData = new FormData();

        formData.top = new FormAttachment(0, 0);

        formData.right = new FormAttachment(100, 0);

        formData.height = 8;

        formData.width = 8;

        this.wDragger.setLayoutData(formData);

        this.wDragger.setCursor(new Cursor(getDisplay(), 21));

        this.wDragger.addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {

                Rectangle r = GraphNode.this.wDragger.getClientArea();

                if (GraphNode.this.customBorders) {

                    e.gc.setBackground(GraphNode.this.active ? GraphNode.this.activeBorderColor : GraphNode.this.borderColor);

                } else {

                    e.gc.setBackground(e.display.getSystemColor(16));

                }

                e.gc.fillPolygon(new int[]{0, 0, r.width, 0, r.width, r.height});

            }

        });

        this.wDragger.setVisible(canDrag());


        setBounds(0, 0, 200, 200);


        layout();


        this.wDragger.addMouseListener(new MouseListener() {

            public void mouseUp(MouseEvent e) {

                GraphNode.this.dragging = false;

            }


            public void mouseDown(MouseEvent e) {

                GraphNode.this.graph.bringNodeForward(GraphNode.this);

                GraphNode.logger.i("(%d,%d)", new Object[]{Integer.valueOf(e.x), Integer.valueOf(e.y)});

                GraphNode.this.dragging = true;

                GraphNode.this.draggingX = e.x;

                GraphNode.this.draggingY = e.y;

            }


            public void mouseDoubleClick(MouseEvent e) {
            }

        });

        this.wDragger.addMouseMoveListener(new MouseMoveListener() {

            public void mouseMove(MouseEvent e) {

                if (GraphNode.this.dragging) {

                    int deltaX = e.x - GraphNode.this.draggingX;

                    int deltaY = e.y - GraphNode.this.draggingY;

                    GraphNode.this.graph.dragNode(GraphNode.this, deltaX, deltaY);

                }


            }


        });

        this.wResizer.addMouseListener(new MouseListener() {

            public void mouseUp(MouseEvent e) {

                GraphNode.this.resizing = false;

            }


            public void mouseDown(MouseEvent e) {

                GraphNode.this.graph.bringNodeForward(GraphNode.this);

                GraphNode.logger.i("(%d,%d)", new Object[]{Integer.valueOf(e.x), Integer.valueOf(e.y)});

                GraphNode.this.resizing = true;

                GraphNode.this.resizingX = e.x;

                GraphNode.this.resizingY = e.y;

            }


            public void mouseDoubleClick(MouseEvent e) {
            }

        });

        this.wResizer.addMouseMoveListener(new MouseMoveListener() {

            public void mouseMove(MouseEvent e) {

                if (GraphNode.this.resizing) {

                    int deltaX = e.x - GraphNode.this.draggingX;

                    int deltaY = e.y - GraphNode.this.draggingY;

                    GraphNode.this.graph.resizeNode(GraphNode.this, deltaX, deltaY);

                }


            }

        });

        addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {

                if (GraphNode.this.customBorders) {

                    Point p = GraphNode.this.getSize();

                    int w = p.x;

                    int h = p.y;


                    e.gc.setBackground(GraphNode.this.active ? GraphNode.this.activeBorderColor : GraphNode.this.borderColor);

                    e.gc.fillRectangle(0, 0, w - 1, 2);

                    e.gc.fillRectangle(0, 0, 2, h - 1);

                    e.gc.fillRectangle(0, h - 2 - 1, w - 1, 2);

                    e.gc.fillRectangle(w - 2 - 1, 0, 2, h - 1);


                    e.gc.setBackground(GraphNode.this.active ? GraphNode.this.activeBorderShadeColor : GraphNode.this.borderShadeColor);

                    e.gc.fillRectangle(2, h - 1, w - 2, 1);

                    e.gc.fillRectangle(w - 1, 2, 1, h - 2);

                }

            }

        });

    }


    public int getId() {

        return this.id;

    }


    public void setNodeName(String name) {

        this.name = name;

    }


    public String getNodeName() {

        return this.name;

    }


    public void setNodeInputEdgeCount(int incount) {

        this.incount = incount;

    }


    public int getNodeInputEdgeCount() {

        return this.incount;

    }


    public void setNodeOutputEdgeCount(int outcount) {

        this.outcount = outcount;

    }


    public int getNodeOutputEdgeCount() {

        return this.outcount;

    }


    public void acknowledgeContents(boolean notifyGraph) {

        int childcnt = getChildren().length;

        if (childcnt <= 2) {

            return;

        }

        if (childcnt >= 4) {

            throw new IllegalStateException("Illegal number of composite children");

        }


        Control child1 = getChildren()[2];

        if ((!(child1 instanceof Composite)) || (!(child1 instanceof IGraphNodeContents))) {

            throw new IllegalArgumentException();

        }

        this.contents = ((Composite) child1);


        FormData formData = new FormData();

        formData.left = new FormAttachment(0, 2);

        formData.right = new FormAttachment(this.wBar);

        formData.bottom = new FormAttachment(100, -3);

        formData.top = new FormAttachment(this.wTitle);

        this.contents.setLayoutData(formData);


        this.wBar.setBackground(this.contents.getBackground());


        this.contents.addFocusListener(new FocusListener() {

            public void focusLost(FocusEvent e) {

                GraphNode.this.graph.reportNodeFocusChange(GraphNode.this, false);

            }


            public void focusGained(FocusEvent e) {

                GraphNode.this.graph.reportNodeFocusChange(GraphNode.this, true);

            }

        });


        if (notifyGraph) {

            this.graph.onNodeContentsUpdate(this);

        }

    }


    public NodeContentsText setTextContents(String text) {

        if (getChildren().length >= 3) {

            throw new IllegalStateException("Node already has a contents");

        }


        NodeContentsText t1 = new NodeContentsText(this, 0);

        t1.setText(text);

        t1.setEditable(false);

        t1.setCaret(null);

        t1.setText(text);

        acknowledgeContents(false);

        return t1;

    }


    public int getZoomLevel() {

        return this.graph.getZoomLevel();

    }


    public boolean applyZoom(int zoom, boolean dryRun) {

        if ((canResize()) || (canDrag())) {

            if (this.graph.getNodeFlags().has(1)) {

                this.wResizer.setVisible(false);

                this.wDragger.setVisible(false);

                this.wBar.setVisible(false);

            } else {

                this.wResizer.setVisible(canResize());

                this.wDragger.setVisible(canDrag());

                this.wBar.setVisible(true);

            }

        }


        this.contents.setVisible(!this.graph.getNodeFlags().has(2));


        boolean success = ((IGraphNodeContents) this.contents).applyZoom(zoom, dryRun);

        if ((!dryRun) && (success)) {

            this.graph.onNodeContentsUpdate(this);

        }

        return success;

    }


    public boolean hasTitle() {

        return this.hasTitle;

    }


    public Composite getTitle() {

        return this.wTitle;

    }


    public boolean canResize() {

        return this.isResizable;

    }


    public boolean canDrag() {

        return this.isDraggable;

    }


    public IGraphNodeContents getContents() {

        return (IGraphNodeContents) this.contents;

    }


    public boolean setFocus() {

        return this.contents.setFocus();

    }


    public boolean forceFocus() {

        return this.contents.forceFocus();

    }


    public boolean isFocusControl() {

        return this.contents.isFocusControl();

    }


    public void addFocusListener(FocusListener listener) {

        this.contents.addFocusListener(listener);

    }


    public void removeFocusListener(FocusListener listener) {

        this.contents.removeFocusListener(listener);

    }


    public void setFont(Font font) {

        this.contents.setFont(font);

    }


    public Font getFont() {

        return this.contents.getFont();

    }


    public String toString() {

        return String.format("Node{%s}", new Object[]{this.name});

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\GraphNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */