package com.pnfsoftware.jeb.rcpclient.extensions.ui;

import com.pnfsoftware.jeb.rcpclient.extensions.SwtRegistry;
import com.pnfsoftware.jeb.util.base.Couple;
import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Toast {
    private static final ILogger logger = GlobalLog.getLogger(Toast.class);
    private static final String TEXTPAD = "  ";
    private static final int MARGIN = 70;
    private int state;
    private Display display;
    private Composite topLevelContainer;
    private List<Couple<Control, PaintListener>> paintListeners = new ArrayList();
    private Point toastSize;
    private Rectangle toastRectangle;
    private int position;
    private long duration;
    private String text;
    private Font font;
    private Color fgcolor;
    private Color bgcolor;

    public Toast(Composite topLevelContainer, String text) {
        this.topLevelContainer = topLevelContainer;
        this.display = topLevelContainer.getDisplay();
        setText(text);
        this.position = 1024;
        this.duration = 1500L;
    }

    public static Toast normal(Composite topLevelContainer, String text) {
        return new Toast(topLevelContainer, text);
    }

    public static Toast inverted(Composite topLevelContainer, String text) {
        return new Toast(topLevelContainer, text).setForegroundColor(topLevelContainer.getBackground()).setBackgroundColor(topLevelContainer.getForeground());
    }

    public static Toast urgent(Composite topLevelContainer, String text) {
        return new Toast(topLevelContainer, text).setForegroundColor(topLevelContainer.getDisplay().getSystemColor(3)).setBackgroundColor(topLevelContainer.getDisplay().getSystemColor(7)).setFont(SwtRegistry.getInstance().getFont(topLevelContainer.getFont(), null, Integer.valueOf(1)));
    }

    public Display getDisplay() {
        return this.display;
    }

    public Composite getTopLevelContainer() {
        return this.topLevelContainer;
    }

    private void prepareGC(GC gc) {
        if (this.bgcolor != null) {
            gc.setBackground(this.bgcolor);
        }
        if (this.fgcolor != null) {
            gc.setForeground(this.fgcolor);
        }
        if (this.font != null) {
            gc.setFont(this.font);
        }
    }

    private void verifyState() {
        if (this.state != 0) {
            throw new IllegalStateException();
        }
    }

    public void show() {
        verifyState();
        this.state = 1;
        this.topLevelContainer.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Toast.this.updateToastBounds();
            }
        });
        updateToastBounds();
        prepare(this.topLevelContainer);
        ThreadUtil.start(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(Toast.this.duration);
                } catch (InterruptedException localInterruptedException) {
                }
                Toast.this.display.asyncExec(new Runnable() {
                    public void run() {
                        Toast.this.dispose();
                    }
                });
            }
        });
        this.topLevelContainer.redraw();
    }

    public Toast setPosition(int position) {
        this.position = position;
        return this;
    }

    public Toast setDuration(long duration) {
        verifyState();
        this.duration = duration;
        return this;
    }

    public Toast setText(String text) {
        verifyState();
        this.text = ("  " + Strings.safe(text) + "  ");
        return this;
    }

    public Toast setFont(Font font) {
        verifyState();
        this.font = font;
        return this;
    }

    public Toast setForegroundColor(Color fgcolor) {
        verifyState();
        this.fgcolor = fgcolor;
        return this;
    }

    public Toast setBackgroundColor(Color bgcolor) {
        verifyState();
        this.bgcolor = bgcolor;
        return this;
    }

    final PaintListener paintListener = new PaintListener() {
        public void paintControl(PaintEvent e) {
            if (!(e.widget instanceof Control)) {
                return;
            }
            Control ctl = (Control) e.widget;
            GC gc = e.gc;
            Rectangle r = e.display.map(Toast.this.topLevelContainer, ctl, Toast.this.toastRectangle);
            Toast.this.prepareGC(gc);
            gc.drawText(Toast.this.text, r.x, r.y);
        }
    };

    private void updateToastBounds() {
        // Byte code:
        //   0: aload_0
        //   1: getfield 45	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:toastSize	Lorg/eclipse/swt/graphics/Point;
        //   4: ifnonnull +46 -> 50
        //   7: new 25	org/eclipse/swt/graphics/GC
        //   10: dup
        //   11: aload_0
        //   12: getfield 46	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:topLevelContainer	Lorg/eclipse/swt/widgets/Composite;
        //   15: invokespecial 81	org/eclipse/swt/graphics/GC:<init>	(Lorg/eclipse/swt/graphics/Drawable;)V
        //   18: astore_1
        //   19: aload_0
        //   20: aload_1
        //   21: invokespecial 56	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:prepareGC	(Lorg/eclipse/swt/graphics/GC;)V
        //   24: aload_0
        //   25: aload_1
        //   26: aload_0
        //   27: getfield 43	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:text	Ljava/lang/String;
        //   30: invokevirtual 86	org/eclipse/swt/graphics/GC:textExtent	(Ljava/lang/String;)Lorg/eclipse/swt/graphics/Point;
        //   33: putfield 45	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:toastSize	Lorg/eclipse/swt/graphics/Point;
        //   36: aload_1
        //   37: invokevirtual 82	org/eclipse/swt/graphics/GC:dispose	()V
        //   40: goto +10 -> 50
        //   43: astore_2
        //   44: aload_1
        //   45: invokevirtual 82	org/eclipse/swt/graphics/GC:dispose	()V
        //   48: aload_2
        //   49: athrow
        //   50: aload_0
        //   51: getfield 46	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:topLevelContainer	Lorg/eclipse/swt/widgets/Composite;
        //   54: invokevirtual 91	org/eclipse/swt/widgets/Composite:getClientArea	()Lorg/eclipse/swt/graphics/Rectangle;
        //   57: astore_1
        //   58: aload_1
        //   59: getfield 50	org/eclipse/swt/graphics/Rectangle:width	I
        //   62: aload_0
        //   63: getfield 45	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:toastSize	Lorg/eclipse/swt/graphics/Point;
        //   66: getfield 47	org/eclipse/swt/graphics/Point:x	I
        //   69: isub
        //   70: iconst_2
        //   71: idiv
        //   72: istore_2
        //   73: aload_0
        //   74: getfield 41	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:position	I
        //   77: ldc 2
        //   79: if_icmpne +21 -> 100
        //   82: aload_1
        //   83: getfield 49	org/eclipse/swt/graphics/Rectangle:height	I
        //   86: aload_0
        //   87: getfield 45	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:toastSize	Lorg/eclipse/swt/graphics/Point;
        //   90: getfield 48	org/eclipse/swt/graphics/Point:y	I
        //   93: isub
        //   94: iconst_2
        //   95: idiv
        //   96: istore_3
        //   97: goto +35 -> 132
        //   100: aload_0
        //   101: getfield 41	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:position	I
        //   104: sipush 128
        //   107: if_icmpne +9 -> 116
        //   110: bipush 70
        //   112: istore_3
        //   113: goto +19 -> 132
        //   116: aload_1
        //   117: getfield 49	org/eclipse/swt/graphics/Rectangle:height	I
        //   120: aload_0
        //   121: getfield 45	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:toastSize	Lorg/eclipse/swt/graphics/Point;
        //   124: getfield 48	org/eclipse/swt/graphics/Point:y	I
        //   127: isub
        //   128: bipush 70
        //   130: isub
        //   131: istore_3
        //   132: aload_0
        //   133: new 27	org/eclipse/swt/graphics/Rectangle
        //   136: dup
        //   137: iload_2
        //   138: iload_3
        //   139: aload_0
        //   140: getfield 45	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:toastSize	Lorg/eclipse/swt/graphics/Point;
        //   143: getfield 47	org/eclipse/swt/graphics/Point:x	I
        //   146: aload_0
        //   147: getfield 45	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:toastSize	Lorg/eclipse/swt/graphics/Point;
        //   150: getfield 48	org/eclipse/swt/graphics/Point:y	I
        //   153: invokespecial 87	org/eclipse/swt/graphics/Rectangle:<init>	(IIII)V
        //   156: putfield 44	com/pnfsoftware/jeb/rcpclient/extensions/ui/Toast:toastRectangle	Lorg/eclipse/swt/graphics/Rectangle;
        //   159: return
        // Line number table:
        //   Java source line #215	-> byte code offset #0
        //   Java source line #216	-> byte code offset #7
        //   Java source line #218	-> byte code offset #19
        //   Java source line #219	-> byte code offset #24
        //   Java source line #222	-> byte code offset #36
        //   Java source line #223	-> byte code offset #40
        //   Java source line #222	-> byte code offset #43
        //   Java source line #223	-> byte code offset #48
        //   Java source line #226	-> byte code offset #50
        //   Java source line #228	-> byte code offset #58
        //   Java source line #231	-> byte code offset #73
        //   Java source line #232	-> byte code offset #82
        //   Java source line #235	-> byte code offset #100
        //   Java source line #236	-> byte code offset #110
        //   Java source line #240	-> byte code offset #116
        //   Java source line #242	-> byte code offset #132
        //   Java source line #243	-> byte code offset #159
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	160	0	this	Toast
        //   18	27	1	gc	GC
        //   57	60	1	containerBounds	Rectangle
        //   43	6	2	localObject	Object
        //   72	66	2	toastX	int
        //   96	2	3	toastY	int
        //   112	2	3	toastY	int
        //   131	8	3	toastY	int
        // Exception table:
        //   from	to	target	type
        //   19	36	43	finally
    }

    private void prepare(Control ctl) {
        ctl.addPaintListener(this.paintListener);
        this.paintListeners.add(new Couple(ctl, this.paintListener));
        if ((ctl instanceof Composite)) {
            for (Control c : ((Composite) ctl).getChildren()) {
                prepare(c);
            }
        }
    }

    private void redrawAll() {
        if (this.topLevelContainer != null) {
            redraw(this.topLevelContainer);
        } else {
            for (Shell shell : this.display.getShells()) {
                redraw(shell);
            }
        }
    }

    private void redraw(Control ctl) {
        Point size = ctl.getSize();
        ctl.redraw(0, 0, size.x, size.y, true);
        ctl.update();
    }

    private void dispose() {
        for (Couple<Control, PaintListener> e : this.paintListeners) {
            Control ctl = (Control) e.getFirst();
            PaintListener listener = (PaintListener) e.getSecond();
            if (!ctl.isDisposed()) {
                ctl.removePaintListener(listener);
            }
        }
        redrawAll();
    }
}


