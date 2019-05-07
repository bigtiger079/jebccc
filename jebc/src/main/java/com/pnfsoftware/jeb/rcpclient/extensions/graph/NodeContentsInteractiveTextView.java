
package com.pnfsoftware.jeb.rcpclient.extensions.graph;


import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.properties.impl.ConfigurationMemoryMap;
import com.pnfsoftware.jeb.core.properties.impl.SimplePropertyManager;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.ZoomableUtil;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.BufferPoint;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.IPositionListener;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.InteractiveTextViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.TextDocumentLocationGenerator;
import com.pnfsoftware.jeb.rcpclient.parts.UIState;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractInteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.parts.units.ItemStyleProvider2;
import com.pnfsoftware.jeb.rcpclient.parts.units.TextHoverableProvider;
import com.pnfsoftware.jeb.rcpclient.parts.units.UnitTextAnnotator;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;


public class NodeContentsInteractiveTextView
        extends AbstractInteractiveTextView
        implements IGraphNodeContents {
    private static final ILogger logger = GlobalLog.getLogger(NodeContentsInteractiveTextView.class);
    private StyledText w;
    private ItemStyleProvider2 styleProvider;
    private UnitTextAnnotator textAnnotator;
    private int zoomLevel;
    private Integer defaultHeight;


    public NodeContentsInteractiveTextView(Composite parent, int style, ITextDocument idoc, FontManager fontManager, ItemStyleProvider2 styleProvider, IUnit unit, final IStatusIndicator statusIndicator, IOperable master, final IGraphController controller, final RcpClientContext context) {

        super(parent, style, unit, null, context, idoc);

        setLayout(new FillLayout());


        this.master = master;


        this.styleProvider = styleProvider;


        IPropertyManager propManager = new SimplePropertyManager(new ConfigurationMemoryMap());

        propManager.setBoolean(".ui.text.ShowVerticalScrollbar", Boolean.valueOf(false));

        propManager.setBoolean(".ui.text.ShowHorizontalScrollbar", Boolean.valueOf(false));

        propManager.setBoolean(".ui.text.DisplayEolAtEod", Boolean.valueOf(false));

        propManager.setInteger(".ui.text.CharactersPerLineMax", Integer.valueOf(4000));

        propManager.setInteger(".ui.text.CharactersPerLineAtEnd", Integer.valueOf(100));

        propManager.setBoolean(".ui.text.AllowLineWrapping", Boolean.valueOf(false));

        propManager.setInteger(".ui.text.CharactersWrap", Integer.valueOf(-1));

        propManager.setInteger(".ui.text.ScrollLineSize", Integer.valueOf(0));

        propManager.setInteger(".ui.text.PageLineSize", Integer.valueOf(0));

        propManager.setInteger(".ui.text.PageMultiplier", Integer.valueOf(0));

        propManager.setBoolean(".ui.text.CaretBehaviorViewportStatic", Boolean.valueOf(false));

        propManager.setInteger(".ui.NavigationBarPosition", Integer.valueOf(0));

        propManager.setInteger(".ui.NavigationBarThickness", Integer.valueOf(2));


        int flags = 2;

        this.iviewer = new InteractiveTextViewer(this, flags, idoc, propManager, null);

        this.w = this.iviewer.getTextWidget();


        if (fontManager != null) {

            this.w.setFont(fontManager.getCodeFont());

        }


        if (styleProvider != null) {

            styleProvider.registerTextViewer(this.iviewer);

            this.iviewer.setStyleAdapter(styleProvider);

        }


        final TextDocumentLocationGenerator locationGenerator = new TextDocumentLocationGenerator(unit, this.iviewer);

        this.iviewer.addPositionListener(new IPositionListener() {

            public void positionChanged(ITextDocumentViewer viewer, ICoordinates coordinates, int focusChange) {

                if (context != null) {

                    context.refreshHandlersStates();

                }


                if (statusIndicator != null) {

                    String status = locationGenerator.generateStatus(coordinates);

                    statusIndicator.setText(status);

                }

            }


            public void positionUnchangedAttemptBreakout(ITextDocumentViewer viewer, int direction) {

                if (controller != null) {

                    controller.onNodeBreakoutAttempt(NodeContentsInteractiveTextView.this, direction);

                }

            }

        });


        if (context != null) {

            UIState uiState = context.getUIState(unit);

            this.textAnnotator = new UnitTextAnnotator(uiState, this.iviewer);

        }


        this.iviewer.initialize(false);


        this.w.setDoubleClickEnabled(false);

        this.w.addMouseListener(new MouseAdapter() {

            public void mouseDoubleClick(MouseEvent e) {

                NodeContentsInteractiveTextView.this.requestOperation(new OperationRequest(Operation.ITEM_FOLLOW));

            }

        });


        if (context != null) {

            this.iviewer.setHoverText(new TextHoverableProvider(context, unit, this.iviewer));

        }


        addStandardContextMenu(new int[]{4});


        addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {

                NodeContentsInteractiveTextView.this.iviewer.dispose();

                if (NodeContentsInteractiveTextView.this.textAnnotator != null) {

                    NodeContentsInteractiveTextView.this.textAnnotator.dispose();

                }

            }

        });

    }


    public Point computeSize(int wHint, int hHint, boolean changed) {

        if (this.w.isDisposed()) {

            return new Point(0, 0);

        }

        return this.iviewer.computeIdealSize();

    }


    public boolean setFocus() {

        if (this.w.isDisposed()) {

            return false;

        }

        return this.w.setFocus();

    }


    public boolean forceFocus() {

        if (this.w.isDisposed()) {

            return false;

        }

        return this.w.forceFocus();

    }


    public boolean isFocusControl() {

        if (this.w.isDisposed()) {

            return false;

        }

        return this.w.isFocusControl();

    }


    public void addFocusListener(FocusListener listener) {

        if (this.w.isDisposed()) {

            return;

        }

        super.addFocusListener(listener);

        this.w.addFocusListener(listener);

    }


    public void removeFocusListener(FocusListener listener) {

        if (this.w.isDisposed()) {

            return;

        }

        super.removeFocusListener(listener);

        this.w.removeFocusListener(listener);

    }


    public void setFont(Font font) {

        if (this.w.isDisposed()) {

            return;

        }

        super.setFont(font);

        this.w.setFont(font);

    }


    public Color getForeground() {

        return this.w.getForeground();

    }


    public void setForeground(Color color) {

        if (this.w.isDisposed()) {

            return;

        }

        super.setForeground(color);

        this.w.setForeground(color);

    }


    public Color getBackground() {

        return this.w.getBackground();

    }


    public void setBackground(Color color) {

        if (this.w.isDisposed()) {

            return;

        }

        super.setBackground(color);

        this.w.setBackground(color);

    }


    public void setEnabled(boolean enabled) {

        if (this.w.isDisposed()) {

            return;

        }

        super.setEnabled(enabled);

        this.w.setEnabled(enabled);

    }


    public void setMenu(Menu menu) {

        if (this.w.isDisposed()) {

            return;

        }

        super.setMenu(menu);

        this.w.setMenu(menu);

    }


    public void setToolTipText(String string) {

        if (this.w.isDisposed()) {

            return;

        }

        super.setToolTipText(string);

        this.w.setToolTipText(string);

    }


    public void setCursor(Cursor cursor) {

        if (this.w.isDisposed()) {

            return;

        }

        super.setCursor(cursor);

        this.w.setCursor(cursor);

    }


    public void setText(String text) {

        if (this.w.isDisposed()) {

            return;

        }

        this.w.setText(text);

    }


    public String getText() {

        if (this.w.isDisposed()) {

            return "";

        }

        return this.w.getText();

    }


    public void setEditable(boolean editable) {

        if (this.w.isDisposed()) {

            return;

        }

        this.w.setEditable(editable);

    }


    public void setCaret(Caret caret) {

        if (this.w.isDisposed()) {

            return;

        }

        this.w.setCaret(caret);

    }


    public Caret getCaret() {

        if (this.w.isDisposed()) {

            return null;

        }

        return this.w.getCaret();

    }


    public boolean verifyOperation(OperationRequest req) {

        if (this.iviewer.verifyOperation(req)) {

            return true;

        }


        switch (req.getOperation()) {

            case JUMP_TO:

                return true;

            case ITEM_FOLLOW:

                return getActiveItem() instanceof IActionableItem;

        }

        return false;

    }


    public boolean doOperation(OperationRequest req) {

        if (this.iviewer.doOperation(req)) {

            return true;

        }

        if (!req.proceed()) {

            return false;

        }

        switch (req.getOperation()) {

            case JUMP_TO:

                return false;

            case ITEM_FOLLOW:

                return doItemFollow();

        }

        return false;

    }


    public IUnit getUnit() {

        return this.unit;

    }


    public boolean isActiveItem(IItem item) {

        return this.styleProvider == null ? false : this.styleProvider.isActiveItem(item);

    }


    public IItem getActiveItem() {

        return this.styleProvider == null ? null : this.styleProvider.getActiveItem();

    }


    public String getActiveItemAsText() {

        IItem item = getActiveItem();

        if (!(item instanceof ITextItem)) {

            return null;

        }

        return ((ITextItem) item).getText();

    }


    public String getActiveAddress(AddressConversionPrecision precision) {

        ICoordinates coords = this.iviewer.getCaretCoordinates();

        if (coords != null) {

            return this.idoc.coordinatesToAddress(coords, AddressConversionPrecision.FINE);

        }

        return null;

    }


    public boolean isValidActiveAddress(String address, Object object) {

        try {

            ICoordinates coord = this.idoc.addressToCoordinates(address);

            return coord != null;

        } catch (Exception e) {

            logger.catching(e);
        }

        return false;

    }


    public boolean setActiveAddress(String address, Object extra, boolean record) {

        ICoordinates coord = null;

        try {

            coord = this.idoc.addressToCoordinates(address);

        } catch (Exception e) {

            logger.catching(e);

        }


        if (coord == null) {

            return false;

        }


        GlobalPosition pos0 = null;

        if (record) {

            pos0 = getViewManager() == null ? null : getViewManager().getCurrentGlobalPosition();

        }


        BufferPoint vp = null;

        if ((extra instanceof BufferPoint)) {

            vp = (BufferPoint) extra;

        }


        if (!this.iviewer.setCaretCoordinates(coord, vp, false)) {

            return false;

        }


        if (pos0 != null) {

            getViewManager().recordGlobalPosition(pos0);

        }

        return true;

    }


    public int getZoomLevel() {

        return this.zoomLevel;

    }


    public boolean applyZoom(int zoom, boolean dryRun) {

        zoom = ZoomableUtil.sanitizeZoom(zoom);

        if (ZoomableUtil.updateZoom(this.zoomLevel, zoom) == 0) {

            zoom = 0;

        }


        FontData[] fdlist = this.w.getFont().getFontData();

        FontData fd0 = fdlist[0];

        int currentHeight = fd0.getHeight();


        if (this.defaultHeight == null) {

            this.defaultHeight = Integer.valueOf(currentHeight);

        }


        int newHeight = determineNextFontHeight(currentHeight, zoom);

        if ((newHeight == currentHeight) || (newHeight <= 1)) {

            return false;

        }


        if (!dryRun) {

            logger.i("New font height after zoom: %d", new Object[]{Integer.valueOf(newHeight)});

            FontDescriptor desc = FontDescriptor.createFrom(fdlist).setHeight(newHeight);

            Font f = createFont(getDisplay(), desc);

            this.w.setFont(f);

            this.zoomLevel = ZoomableUtil.updateZoom(this.zoomLevel, zoom);

        }

        return true;

    }


    private static Map<FontDescriptor, Font> fontmap = new HashMap();


    private static Font createFont(Display display, FontDescriptor desc) {

        Font f = (Font) fontmap.get(desc);

        if (f == null) {

            f = desc.createFont(display);

            fontmap.put(desc, f);

        }

        return f;

    }


    private static final int[] wkhsuite = {1, 2, 4, 6, 8, 10, 14, 20};
    private static final int wkhLastIndex = wkhsuite.length - 1;


    private int determineNextFontHeight(int h, int zoom) {

        if (zoom == 0) {

            return this.defaultHeight.intValue();

        }


        int i = 0;

        for (int v : wkhsuite) {

            if (v >= h) {

                break;

            }

            i++;

        }

        if (i < wkhsuite.length) {

            int v = wkhsuite[i];

            if (v > h) {

                i--;

            }

        }


        if (i <= 0) {

            return wkhsuite[0];

        }


        if (zoom > 0) {

            if (i >= wkhLastIndex) {

                return (int) (1.5D * h);

            }

            return wkhsuite[(i + 1)];

        }


        if (i >= wkhsuite.length) {

            return wkhsuite[wkhLastIndex];

        }

        return wkhsuite[(i - 1)];

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\NodeContentsInteractiveTextView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */