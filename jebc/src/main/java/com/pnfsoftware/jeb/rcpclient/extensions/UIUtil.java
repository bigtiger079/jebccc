
package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class UIUtil {
    private static final ILogger logger = GlobalLog.getLogger(UIUtil.class);
    private static boolean warnedArraySort;

    public static boolean isUIThread(Display display) {
        return Thread.currentThread() == display.getThread();
    }

    public static Button createPushbox(Composite parent, String name, SelectionListener listener) {
        Button btn = new Button(parent, 8);
        btn.setText("     " + name + "     ");
        if (listener != null) {
            btn.addSelectionListener(listener);
        }
        return btn;
    }

    public static Button createTightPushbox(Composite parent, String name, SelectionListener listener) {
        Button btn = new Button(parent, 8);
        btn.setText(name);
        if (listener != null) {
            btn.addSelectionListener(listener);
        }
        return btn;
    }

    public static Button createCheckbox(Composite parent, String name, SelectionListener listener) {
        Button btn = new Button(parent, 32);
        btn.setText(name);
        if (listener != null) {
            btn.addSelectionListener(listener);
        }
        return btn;
    }

    public static Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, 16896);
        label.setText(text);
        return label;
    }

    public static Label createWrappedLabelInGridLayout(Composite parent, int addStyle, String text, int columns) {
        Label label = new Label(parent, addStyle | 0x4000 | 0x200 | 0x40);
        label.setLayoutData(createGridDataFillHorizontally());
        label.setText(text);
        return label;
    }

    public static Text createTextbox(Composite parent, int length, String init, SelectionListener listener) {
        Text text = new Text(parent, 2052);
        if ((parent.getLayout() instanceof RowLayout)) {
            RowData data = null;
            GC gc = new GC(text);
            try {
                gc.setFont(text.getFont());
                FontMetrics fm = gc.getFontMetrics();
                data = new RowData(length * fm.getAverageCharWidth(), 1 * fm.getHeight());
            } finally {
                gc.dispose();
            }
            text.setLayoutData(data);
        }
        if (init != null) {
            text.setText(init);
            text.selectAll();
        }
        text.pack(false);
        if (listener != null) {
            text.addSelectionListener(listener);
        }
        return text;
    }

    public static Text createTextboxInGrid(Composite parent, int flags, int columns, int lines) {
        return createTextboxInGrid(parent, flags, columns, lines, false, false);
    }

    public static Text createTextboxInGrid(Composite parent, int flags, int columns, int lines, boolean fillHorizontally, boolean fillVertically) {
        Text text = new Text(parent, flags);
        if ((parent.getLayout() instanceof GridLayout)) {
            GridData data = null;
            GC gc = new GC(text);
            try {
                gc.setFont(text.getFont());
                FontMetrics fm = gc.getFontMetrics();
                data = new GridData();
                if (columns >= 1) {
                    data.widthHint = (columns * fm.getAverageCharWidth());
                }
                if (lines >= 1) {
                    data.heightHint = (lines * fm.getHeight());
                }
            } finally {
                gc.dispose();
            }
            if (fillHorizontally) {
                data.horizontalAlignment = 4;
                data.grabExcessHorizontalSpace = true;
            }
            if (fillVertically) {
                data.verticalAlignment = 4;
                data.grabExcessVerticalSpace = true;
            }
            text.setLayoutData(data);
        }
        text.pack(false);
        return text;
    }

    public static void disableTabOutput(Control textControl) {
        disableTabOutput(textControl, null);
    }

    public static void disableTabOutput(Control textControl, Control nextFocusControl) {
        textControl.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if ((e.detail == 16) || (e.detail == 8)) {
                    e.doit = true;
                    if (nextFocusControl != null) {
                        nextFocusControl.setFocus();
                    }
                }
            }
        });
    }

    public static RowLayout createRowLayout(boolean vertical, boolean wrap) {
        RowLayout l = new RowLayout();
        l.type = (vertical ? 512 : 256);
        l.wrap = wrap;
        return l;
    }

    public static void setStandardLayout(Composite composite) {
        setStandardLayout(composite, 1);
    }

    public static void setStandardLayout(Composite composite, int numColumns) {
        setStandardLayout(composite, numColumns, 5);
    }

    public static void setStandardLayout(Composite composite, int numColumns, int marginSize) {
        GridLayout layout = new GridLayout(numColumns, false);
        layout.marginLeft = marginSize;
        layout.marginRight = marginSize;
        layout.marginTop = marginSize;
        layout.marginBottom = marginSize;
        composite.setLayout(layout);
    }

    public static GridData createGridDataFillHorizontally() {
        return createGridDataFill(true, false);
    }

    public static GridData createGridDataFill(boolean fillHorizontally, boolean fillVertically) {
        GridData data = new GridData();
        if (fillHorizontally) {
            data.horizontalAlignment = 4;
            data.grabExcessHorizontalSpace = true;
        }
        if (fillVertically) {
            data.verticalAlignment = 4;
            data.grabExcessVerticalSpace = true;
        }
        return data;
    }

    public static GridData createGridDataSpanHorizontally(int span) {
        return createGridDataSpanHorizontally(span, false, false);
    }

    public static GridData createGridDataSpanHorizontally(int span, boolean fillHorizontally, boolean fillVertically) {
        GridData data = new GridData();
        data.horizontalSpan = span;
        if (fillHorizontally) {
            data.horizontalAlignment = 4;
            data.grabExcessHorizontalSpace = true;
        }
        if (fillVertically) {
            data.verticalAlignment = 4;
            data.grabExcessVerticalSpace = true;
        }
        return data;
    }

    public static GridData createGridDataForText(Control ctl, int charCount) {
        return createGridDataForText(ctl, charCount, 1, true);
    }

    public static GridData createGridDataForText(Control ctl, int columnCount, int lineCount, boolean fillHorizontally) {
        GridData data = null;
        GC gc = new GC(ctl);
        try {
            gc.setFont(ctl.getFont());
            FontMetrics fm = gc.getFontMetrics();
            int width = columnCount <= 0 ? -1 : columnCount * fm.getAverageCharWidth();
            int height = lineCount <= 0 ? -1 : lineCount * fm.getHeight();
            data = new GridData(width, height);
            if (fillHorizontally) {
                data.horizontalAlignment = 4;
                data.grabExcessHorizontalSpace = true;
            }
        } finally {
            gc.dispose();
        }
        return data;
    }

    public static Group createGroup(Composite parent, String label) {
        Group general = new Group(parent, 0);
        general.setText(label);
        general.setLayoutData(createGridDataFillHorizontally());
        general.setLayout(new GridLayout(2, false));
        return general;
    }

    public static Group createGroupGrid(Composite parent, String label, int span, int cols) {
        Group c = new Group(parent, 0);
        c.setText(label);
        c.setLayoutData(createGridDataSpanHorizontally(span, true, false));
        c.setLayout(new GridLayout(cols, false));
        return c;
    }

    public static Composite createCompositeGrid(Composite parent, int span, int cols) {
        Composite c = new Composite(parent, 0);
        c.setLayoutData(createGridDataSpanHorizontally(span, true, false));
        c.setLayout(new GridLayout(cols, false));
        return c;
    }

    public static int determineTextWidth(Control ctl, int columnCount) {
        int width = 0;
        GC gc = new GC(ctl);
        try {
            gc.setFont(ctl.getFont());
            FontMetrics fm = gc.getFontMetrics();
            width = columnCount <= 0 ? -1 : columnCount * fm.getAverageCharWidth();
        } finally {
            gc.dispose();
        }
        return width;
    }

    public static int determineTextHeight(Control ctl, int lineCount) {
        int height = 0;
        GC gc = new GC(ctl);
        try {
            gc.setFont(ctl.getFont());
            FontMetrics fm = gc.getFontMetrics();
            height = lineCount <= 0 ? -1 : lineCount * fm.getHeight();
        } finally {
            gc.dispose();
        }
        return height;
    }

    public static FormData createFormData(Object top, Object bottom, Object left, Object right) {
        FormData data = new FormData();
        data.top = createFormAttachment(top);
        data.bottom = createFormAttachment(bottom);
        data.left = createFormAttachment(left);
        data.right = createFormAttachment(right);
        return data;
    }

    public static FormAttachment createFormAttachment(Object v) {
        if (v == null) {
            return null;
        }
        if ((v instanceof Integer)) {
            return new FormAttachment(((Integer) v).intValue());
        }
        if ((v instanceof Control)) {
            return new FormAttachment((Control) v);
        }
        throw new RuntimeException();
    }

    public static RowLayout createVerticalLayout() {
        return createRowLayout(true, false);
    }

    public static RowLayout createHorizontalLayout() {
        return createRowLayout(false, false);
    }

    public static Rectangle rectangleFromString(String s) {
        String[] elts = s.split(";");
        if (elts.length == 4) {
            try {
                return new Rectangle(Integer.parseInt(elts[0]), Integer.parseInt(elts[1]), Integer.parseInt(elts[2]),
                        Integer.parseInt(elts[3]));
            } catch (Exception localException) {
            }
        }
        return null;
    }

    public static String rectangleToString(Rectangle r) {
        return String.format("%d;%d;%d;%d", new Object[]{Integer.valueOf(r.x), Integer.valueOf(r.y), Integer.valueOf(r.width), Integer.valueOf(r.height)});
    }

    public static void showOperationResultDialog(Shell parent, boolean success) {
        int flags = 0x20 | (success ? 2 : 1);
        MessageBox mb = new MessageBox(parent, flags);
        mb.setText(S.s(280));
        if (success) {
            mb.setMessage(S.s(206));
        } else {
            mb.setMessage(S.s(205));
        }
        mb.open();
    }

    public static void setWidgetName(Widget widget, String name) {
        widget.setData("widgetName", name);
    }

    public static String getWidgetName(Widget widget) {
        Object o = widget.getData("widgetName");
        if ((o != null) && (!(o instanceof String))) {
            throw new IllegalStateException("The widgetName UI property must be a String");
        }
        return (String) o;
    }

    public static int getWidgetId(Object object) {
        if ((object instanceof JebDialog)) {
            object = ((JebDialog) object).getShell();
        }
        if ((object instanceof Control)) {
            String fqname = "";
            Control control = (Control) object;
            if (!(control instanceof Shell)) {
                Object objName = control.getData("widgetName");
                if (!(objName instanceof String)) {
                    logger.i("control: widgetName is missing", new Object[0]);
                    return 0;
                }
                fqname = objName + "/" + fqname;
                control = control.getShell();
                if (control == null) {
                    logger.i("non-shell control has no parent shell, wth?", new Object[0]);
                    return 0;
                }
            }
            while (control != null) {
                if (!(control instanceof Shell)) {
                    logger.i("was expecting a shell instance, got: %s", new Object[]{control.getClass().getName()});
                    return 0;
                }
                Object objName = control.getData("widgetName");
                if (!(objName instanceof String)) {
                    logger.i("shell: widgetName is missing: %s", new Object[]{((Shell) control).getText()});
                    return 0;
                }
                fqname = objName + "/" + fqname;
                control = control.getParent();
            }
            return fqname.hashCode();
        }
        return 0;
    }

    public static Shell getParentShell(Composite ctl) {
        do {
            ctl = ctl.getParent();
            if (ctl == null) {
                return null;
            }
        } while (!(ctl instanceof Shell));
        return (Shell) ctl;
    }

    public static void copyTextToClipboard(String text) {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        TextTransfer textTransfer = TextTransfer.getInstance();
        clipboard.setContents(new String[]{text}, new Transfer[]{textTransfer});
        clipboard.dispose();
    }

    public static String getTextFromClipboard() {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        TextTransfer textTransfer = TextTransfer.getInstance();
        String s = (String) clipboard.getContents(textTransfer, 1);
        clipboard.dispose();
        return s;
    }

    public static boolean isArrowKey(int key) {
        switch (key) {
            case 16777217:
            case 16777218:
            case 16777219:
            case 16777220:
                return true;
        }
        return false;
    }

    public static void safeRefreshViewer(Viewer viewer) {
        try {
            viewer.refresh();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Comparison method violates its general contract!")) {
                if (!Boolean.parseBoolean(System.getProperty("java.util.Arrays.useLegacyMergeSort"))) {
                    logger.error("A comparison error occurred, the viewer cannot be sorted!", new Object[0]);
                    if (!warnedArraySort) {
                        warnedArraySort = true;
                        logger.error("It may be a well-known issue in the UI framework: consider editing jeb.ini, and add the following line:\n  -Djava.util.Arrays.useLegacyMergeSort=true\nafter the line (add it if necessary):\n  -vmargs\nRefer to the FAQ for details: https://www.pnfsoftware.com/jeb/manual/faq/", new Object[0]);
                    }
                    return;
                }
            }
            throw e;
        }
    }

    public static boolean isContained(Point p, Rectangle r) {
        return (p.x >= r.x) && (p.x < r.x + r.width) && (p.y >= r.y) && (p.y < r.y + r.height);
    }

    public static boolean intersect(Rectangle a, Rectangle b) {
        return intersectOrContain(a, b, false);
    }

    public static boolean contains(Rectangle a, Rectangle b) {
        return intersectOrContain(a, b, true);
    }

    private static boolean intersectOrContain(Rectangle a, Rectangle b, boolean requestFullInclusion) {
        int left0 = a.x;
        int right0 = a.x + a.width;
        int top0 = a.y;
        int bottom0 = a.y + a.height;
        int left1 = b.x;
        int right1 = b.x + b.width;
        int top1 = b.y;
        int bottom1 = b.y + b.height;
        if ((left1 >= left0) && (right1 <= right0) && (top1 >= top0) && (bottom1 <= bottom0)) {
            return true;
        }
        if (requestFullInclusion) {
            return false;
        }
        left0 -= b.width;
        right0 += b.width;
        top0 -= b.height;
        bottom0 += b.height;
        if ((left1 > left0) && (right1 < right0) && (top1 > top0) && (bottom1 < bottom0)) {
            return true;
        }
        return false;
    }

    public static Point getRectangleCenter(Rectangle r) {
        return new Point(r.x + r.width / 2, r.y + r.height / 2);
    }

    public static String formatControlHierarchy(Control ctl) {
        String s = "";
        while (ctl != null) {
            s = ctl.getClass().getSimpleName() + " >> " + s;
            ctl = ctl.getParent();
        }
        return s;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\UIUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */