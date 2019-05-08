package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.IMetadataManager;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.FindTextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.BufferPoint;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.IPositionListener;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.InteractiveTextFindResult;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.InteractiveTextViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.TextDocumentLocationGenerator;
import com.pnfsoftware.jeb.rcpclient.parts.IViewNavigator;
import com.pnfsoftware.jeb.rcpclient.parts.UIState;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public class InteractiveTextView extends AbstractInteractiveTextView {
    private static final ILogger logger = GlobalLog.getLogger(InteractiveTextView.class);
    private List<ILocationListener> locationListeners = new ArrayList();
    private UnitTextAnnotator textAnnotator;
    private ItemStyleProvider tsa;
    private GraphicalTextFinder<InteractiveTextFindResult> finder;

    public InteractiveTextView(Composite parent, int style, RcpClientContext context, IUnit unit, IRcpUnitView unitView, ITextDocument idoc) {
        super(parent, style, unit, unitView, context, idoc);
        setLayout(new FillLayout());
        int flags = 0;
        if (!(unit instanceof ISourceUnit)) {
            flags |= 0x1;
        }
        if ((!Licensing.isDebugBuild()) && (!context.isDevelopmentMode())) {
            flags |= 0x2;
        }
        this.iviewer = new InteractiveTextViewer(this, flags, idoc, context.getPropertyManager(), getMetadataManager(unit));
        context.getFontManager().registerWidget(this.iviewer.getTextWidget());
        this.tsa = new ItemStyleProvider(context.getStyleManager());
        this.tsa.registerTextViewer(this.iviewer);
        this.iviewer.setStyleAdapter(this.tsa);
        this.finder = new GraphicalTextFinder(this.iviewer, context);
        UIState uiState = context.getUIState(unit);
        this.textAnnotator = new UnitTextAnnotator(uiState, this.iviewer);
        this.iviewer.initialize(true);
        this.iviewer.getTextWidget().setDoubleClickEnabled(false);
        this.iviewer.getTextWidget().addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                InteractiveTextView.this.requestOperation(new OperationRequest(Operation.ITEM_FOLLOW));
            }
        });
        setPrimaryWidget(this.iviewer.getTextWidget());
        this.iviewer.addPositionListener(new IPositionListener() {
            public void positionChanged(ITextDocumentViewer viewer, ICoordinates coordinates, int focusChange) {
                Assert.a(viewer == InteractiveTextView.this.iviewer);
                InteractiveTextView.this.onPositionChanged(coordinates);
            }

            public void positionUnchangedAttemptBreakout(ITextDocumentViewer viewer, int direction) {
            }
        });
        this.iviewer.refreshStyles();
        this.iviewer.setHoverText(new TextHoverableProvider(context, unit, this.iviewer));
        addStandardContextMenu(new int[0]);
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                InteractiveTextView.this.iviewer.dispose();
                InteractiveTextView.this.textAnnotator.dispose();
            }
        });
    }

    private IMetadataManager getMetadataManager(IUnit unit) {
        IMetadataManager mm = null;
        if ((unit instanceof IInteractiveUnit)) {
            mm = ((IInteractiveUnit) unit).getMetadataManager();
        }
        return mm;
    }

    private void onPositionChanged(ICoordinates coord) {
        this.context.refreshHandlersStates();
        TextDocumentLocationGenerator locationGenerator = new TextDocumentLocationGenerator(this.unit, this.iviewer);
        String address = locationGenerator.getAddress(coord);
        String status = locationGenerator.generateStatus(coord);
        this.context.getStatusIndicator().setText(status);
        for (ILocationListener listener : this.locationListeners) {
            listener.locationChanged(address);
        }
    }

    public void addLocationListener(ILocationListener listener) {
        this.locationListeners.add(listener);
    }

    public void removeLocationListener(ILocationListener listener) {
        this.locationListeners.remove(listener);
    }

    public ITextDocument getDocument() {
        return this.idoc;
    }

    public ICoordinates getCurrentCoordinates() {
        return this.iviewer.getCaretCoordinates();
    }

    public boolean isActiveItem(IItem item) {
        return this.tsa.isActiveItem(item);
    }

    public IItem getActiveItem() {
        return this.tsa.getActiveItem();
    }

    public String getActiveAddress() {
        return getActiveAddress(AddressConversionPrecision.FINE);
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        ICoordinates coord = this.iviewer.getCaretCoordinates();
        return getAddressAt(precision, coord);
    }

    public String getActiveItemAsText() {
        ICoordinates coords = getCurrentCoordinates();
        if ((getActiveItem() != null) && (coords != null)) {
            ILine line = TextPartUtil.getLineAt(this.iviewer.getCurrentDocumentPart(), coords);
            if (line != null) {
                ITextItem item = TextPartUtil.getItemAt(line, coords.getColumnOffset());
                if (item != null) {
                    return line.getText().subSequence(item.getOffset(), item.getOffsetEnd()).toString();
                }
            }
        }
        return null;
    }

    private ITextDocumentPart lastKnownPart = null;
    private ICoordinates lastKnownCoordinates = null;
    private String lastKnownAddress = null;

    public String getAddressAt(AddressConversionPrecision precision, ICoordinates coord) {
        if (coord == null) {
            return null;
        }
        if ((this.lastKnownPart != this.iviewer.getCurrentDocumentPart()) || (!coord.equals(this.lastKnownCoordinates))) {
            this.lastKnownPart = this.iviewer.getCurrentDocumentPart();
            this.lastKnownCoordinates = coord;
            this.lastKnownAddress = this.idoc.coordinatesToAddress(coord, precision);
        }
        return this.lastKnownAddress;
    }

    public Position getActivePosition() {
        String address = getActiveAddress();
        if (address == null) {
            return null;
        }
        BufferPoint vp = this.iviewer.getCaretViewportPoint();
        return new Position(address, vp);
    }

    public boolean isValidActiveAddress(String address, Object object) {
        try {
            ICoordinates coord = this.idoc.addressToCoordinates(address);
            if (coord != null) {
                return true;
            }
        } catch (Exception e) {
            logger.catching(e);
        }
        if (this.viewNavigatorHelper != null) {
            return this.viewNavigatorHelper.canHandleAddress(address);
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
        GlobalPosition pos0 = null;
        IViewManager viewManager = getViewManager();
        if ((viewManager != null) && (record)) {
            pos0 = viewManager.getCurrentGlobalPosition();
        }
        boolean success;
        if (coord == null) {
            success = (this.viewNavigatorHelper != null) && (this.viewNavigatorHelper.navigateTo(address, viewManager, false));
        } else {
            BufferPoint vp = null;
            if ((extra instanceof BufferPoint)) {
                vp = (BufferPoint) extra;
            }
            success = this.iviewer.setCaretCoordinates(coord, vp, true);
        }
        if ((success) && (pos0 != null)) {
            viewManager.recordGlobalPosition(pos0);
            this.context.refreshHandlersStates();
        }
        return success;
    }

    public boolean verifyOperation(OperationRequest req) {
        if (this.iviewer.verifyOperation(req)) {
            return true;
        }
        switch (req.getOperation()) {
            case FIND:
                return true;
            case FIND_NEXT:
                return this.finder != null;
            case JUMP_TO:
                return true;
            case ITEM_FOLLOW:
            case ITEM_PREVIOUS:
            case ITEM_NEXT:
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
            case FIND:
                FindTextDialog dlg = FindTextDialog.getInstance(this);
                if (dlg != null) {
                    dlg.setFocus();
                    return true;
                }
                FindTextOptions opt = this.iviewer.getFindTextOptions(true);
                if (Strings.isBlank(opt.getSearchString())) {
                    String selection = this.iviewer.getTextWidget().getSelectionText();
                    if (selection != null) {
                        int endline = selection.indexOf("\n");
                        if (endline == 0) {
                            endline = selection.indexOf("\n", 1);
                            if (endline >= 0) {
                                selection = selection.substring(1, endline);
                            } else {
                                selection = selection.substring(1);
                            }
                        } else if (endline > 0) {
                            selection = selection.substring(0, endline);
                        }
                        opt.setSearchString(selection);
                    }
                }
                TextHistory history = RcpClientContext.getStandardFindTextHistory(this.context);
                dlg = new FindTextDialog(getShell(), this.finder, history, false, this, getUnit().getName());
                dlg.open(this);
                return true;
            case FIND_NEXT:
                this.finder.search(null);
                return true;
            case JUMP_TO:
                return doJumpTo();
            case ITEM_FOLLOW:
                return doItemFollow();
            case ITEM_NEXT:
                IItem item = getActiveItem();
                if (!(item instanceof IActionableItem)) {
                    return false;
                }
                return nextItem((IActionableItem) item);
            case ITEM_PREVIOUS:
                item = getActiveItem();
                if (!(item instanceof IActionableItem)) {
                    return false;
                }
                return previousItem((IActionableItem) item);
        }
        return false;
    }

    private boolean nextItem(IActionableItem _item) {
        if ((_item == null) || (_item.getItemId() == 0L)) {
            return false;
        }
        long _id = _item.getItemId();
        logger.debug("Searching for next item in current part: %s", new Object[]{_item});
        GlobalPosition pos0 = getViewManager() == null ? null : getViewManager().getCurrentGlobalPosition();
        ITextDocumentPart part = this.iviewer.getCurrentDocumentPart();
        long anchorId = TextPartUtil.getFirstAnchorId(part);
        if (anchorId < 0L) {
            return false;
        }
        ICoordinates _coord = this.iviewer.getCaretCoordinates();
        int _line = TextPartUtil.coordinatesToLineIndex(part, _coord);
        int _column = _coord.getColumnOffset();
        List<? extends ILine> lines = part.getLines();
        for (int lineIndex = _line; lineIndex < lines.size(); lineIndex++) {
            ILine line = (ILine) lines.get(lineIndex);
            for (ITextItem item : line.getItems()) {
                if (((item instanceof IActionableItem)) && (((IActionableItem) item).getItemId() == _id) && ((lineIndex != _line) || (item.getOffset() > _column))) {
                    if (pos0 != null) {
                        getViewManager().recordGlobalPosition(pos0);
                    }
                    Coordinates coord = new Coordinates(anchorId, lineIndex, item.getOffset());
                    return this.iviewer.setCaretCoordinates(coord, null, true);
                }
            }
        }
        logger.debug("Next item in current part was not found", new Object[0]);
        return false;
    }

    private boolean previousItem(IActionableItem _item) {
        if ((_item == null) || (_item.getItemId() == 0L)) {
            return false;
        }
        long _id = _item.getItemId();
        logger.debug("Searching for previous item in current part: %s", new Object[]{_item});
        GlobalPosition pos0 = getViewManager() == null ? null : getViewManager().getCurrentGlobalPosition();
        ITextDocumentPart part = this.iviewer.getCurrentDocumentPart();
        long anchorId = TextPartUtil.getFirstAnchorId(part);
        if (anchorId < 0L) {
            return false;
        }
        ICoordinates _coord = this.iviewer.getCaretCoordinates();
        int _line = TextPartUtil.coordinatesToLineIndex(part, _coord);
        int _column = _coord.getColumnOffset();
        List<? extends ILine> lines = part.getLines();
        for (int lineIndex = _line; lineIndex >= 0; lineIndex--) {
            ILine line = (ILine) lines.get(lineIndex);
            for (ITextItem item : line.getItems()) {
                if (((item instanceof IActionableItem)) && (((IActionableItem) item).getItemId() == _id) && ((lineIndex != _line) || (item.getOffset() + item.getLength() <= _column))) {
                    if (pos0 != null) {
                        getViewManager().recordGlobalPosition(pos0);
                    }
                    Coordinates coord = new Coordinates(anchorId, lineIndex, item.getOffset());
                    return this.iviewer.setCaretCoordinates(coord, null, true);
                }
            }
        }
        logger.debug("Previous item in current part was not found", new Object[0]);
        return false;
    }

    public List<ICoordinates> collectItemCoordinates(long itemId) {
        List<ICoordinates> r = new ArrayList();
        long anchorId = TextPartUtil.getFirstAnchorId(this.iviewer.getCurrentDocumentPart());
        int lineIndex;
        if (anchorId >= 0L) {
            lineIndex = 0;
            for (ILine line : this.iviewer.getCurrentDocumentPart().getLines()) {
                for (ITextItem item0 : line.getItems()) {
                    if (((item0 instanceof IActionableItem)) && (((IActionableItem) item0).getItemId() == itemId)) {
                        Coordinates coord = new Coordinates(anchorId, lineIndex, item0.getOffset());
                        r.add(coord);
                    }
                }
                lineIndex++;
            }
        }
        return r;
    }

    public void setCaretCoordinates(ICoordinates coord) {
        this.iviewer.setCaretCoordinates(coord, null, true);
    }

    public void setMenu(Menu menu) {
        logger.error("onSetMenuuuuuuuuu");
        super.setMenu(menu);
        this.iviewer.getTextWidget().setMenu(menu);
    }

    public boolean setFocus() {
        return this.iviewer.getTextWidget().setFocus();
    }

    public byte[] export() {
        ITextDocumentPart wholePart = this.idoc.getDocumentPart(this.idoc.getFirstAnchor(), Integer.MAX_VALUE);
        return Strings.encodeUTF8(TextPartUtil.buildRawTextFromPart(wholePart));
    }

    public AbstractUnitFragment.FragmentType getFragmentType() {
        return AbstractUnitFragment.FragmentType.TEXT;
    }
}


