
package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
import com.pnfsoftware.jeb.core.units.IAddressableUnit;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.AllHandlers;
import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.IViewNavigator;
import com.pnfsoftware.jeb.util.format.TokenExtractor;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractInteractiveTextView
        extends AbstractUnitFragment<IUnit> {
    private static final ILogger logger = GlobalLog.getLogger(AbstractInteractiveTextView.class);
    protected ITextDocument idoc;
    protected ITextDocumentViewer iviewer;

    public AbstractInteractiveTextView(Composite parent, int style, IUnit unit, IRcpUnitView unitView, RcpClientContext context, ITextDocument idoc) {
        super(parent, style, unit, unitView, context);
        this.idoc = idoc;
    }

    public AbstractInteractiveTextView(Composite parent, int style, IUnit unit, IRcpUnitView unitView, IViewManager viewManager, IStatusIndicator statusIndicator, ITextDocument idoc) {
        super(parent, style, unit, unitView, viewManager, statusIndicator);
        this.idoc = idoc;
    }

    public ITextDocument getDocument() {
        return this.idoc;
    }

    public ITextDocumentViewer getViewer() {
        return this.iviewer;
    }

    protected void addStandardContextMenu(final int... additionalGroups) {
        new ContextMenu(this).addContextMenu(new IContextMenu() {
            public void fillContextMenu(IMenuManager menuMgr) {
                if (AbstractInteractiveTextView.this.getContext() == null) {
                    return;
                }
                AllHandlers.getInstance().fillManager(menuMgr, 6);
                if ((AbstractInteractiveTextView.this.unit instanceof INativeCodeUnit)) {
                    AllHandlers.getInstance().fillManager(menuMgr, 7);
                }
                for (int grp : additionalGroups) {
                    AllHandlers.getInstance().fillManager(menuMgr, grp);
                }
            }
        });
    }

    protected boolean doJumpTo() {
        if (this.context == null) {
            return false;
        }
        JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(this.context));
        String selection = this.iviewer.getTextWidget().getSelectionText();
        if (selection != null) {
            if (selection.length() > 20) {
                selection = selection.substring(0, 20);
            }
            dlg.setInitialValue(selection);
        }
        String address = dlg.open();
        if (address == null) {
            return false;
        }
        return setActiveAddress(address);
    }

    protected boolean doItemFollow() {
        IItem item = getActiveItem();
        if (((item instanceof IActionableItem)) && (followItem((IActionableItem) item))) {
            return true;
        }
        StyledText widget = this.iviewer.getTextWidget();
        int offset = widget.getCaretOffset();
        int lineIndex = widget.getLineAtOffset(offset);
        String line = widget.getLine(lineIndex);
        int col = offset - widget.getOffsetAtLine(lineIndex);
        String token1 = new TokenExtractor(TokenExtractor.DF_WhiteSpace).extract(line, col);
        if ((token1 != null) && (setActiveAddress(token1, null, true))) {
            return true;
        }
        String token2 = new TokenExtractor(TokenExtractor.DF_CommonSymbolChars).extract(line, col);
        if ((token2 != null) && (setActiveAddress(token2, null, true))) {
            return true;
        }
        String token3 = new TokenExtractor(TokenExtractor.DF_NonAlphaNum).extract(line, col);
        if ((token3 != null) && (setActiveAddress(token3, null, true))) {
            return true;
        }
        return false;
    }

    protected boolean followItem(IActionableItem targetItem) {
        if ((targetItem == null) || (targetItem.getItemId() == 0L)) {
            return false;
        }
        long targetItemId = targetItem.getItemId();
        logger.debug("Following item: %s", new Object[]{targetItem});
        GlobalPosition pos0 = null;
        IViewManager viewManager = getViewManager();
        if (viewManager != null) {
            pos0 = viewManager.getCurrentGlobalPosition();
        }
        ITextDocumentPart part = this.iviewer.getCurrentDocumentPart();
        ICoordinates coord = findRootItem(part, targetItemId);
        if (coord != null) {
            if (pos0 != null) {
                viewManager.recordGlobalPosition(pos0);
            }
            return this.iviewer.setCaretCoordinates(coord, null, true);
        }
        logger.debug("No master item found in the current part, checking if the part is addressable", new Object[0]);
        if ((getUnit() instanceof IAddressableUnit)) {
            IAddressableUnit iunit = (IAddressableUnit) getUnit();
            String address = iunit.getAddressOfItem(targetItemId);
            if ((address != null) && (!address.equals(getActiveAddress())) &&
                    (setActiveAddress(address, null, true))) {
                logger.debug("Item to address conversion was successful, jumping to %s", new Object[]{address});
                return true;
            }
        }
        if ((this.viewNavigatorHelper != null) && (viewManager != null) && (this.viewNavigatorHelper.navigateTo(targetItem, viewManager, false))) {
            logger.debug("Jump was successful using the view navigator", new Object[0]);
            if (pos0 != null) {
                viewManager.recordGlobalPosition(pos0);
            }
            return true;
        }
        logger.debug("The jump failed", new Object[0]);
        return false;
    }

    public static ICoordinates findRootItem(ITextDocumentPart part, long itemId) {
        long anchorId = TextPartUtil.getFirstAnchorId(part);
        int lineIndex;
        if (anchorId >= 0L) {
            lineIndex = 0;
            for (ILine line : part.getLines()) {
                for (ITextItem item0 : line.getItems()) {
                    if (((item0 instanceof IActionableItem)) &&
                            (((IActionableItem) item0).getItemId() == itemId)) {
                        int flags = ((IActionableItem) item0).getItemFlags();
                        if ((flags & 0x1) != 0) {
                            Coordinates coord = new Coordinates(anchorId, lineIndex, item0.getOffset());
                            return coord;
                        }
                    }
                }
                lineIndex++;
            }
        }
        return null;
    }
}


