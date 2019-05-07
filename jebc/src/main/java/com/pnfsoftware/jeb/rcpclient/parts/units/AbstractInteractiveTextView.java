/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.IActionableItem;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
/*     */ import com.pnfsoftware.jeb.core.units.IAddressableUnit;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.AllHandlers;
/*     */ import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
/*     */ import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
/*     */ import com.pnfsoftware.jeb.rcpclient.IViewManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.IViewNavigator;
/*     */ import com.pnfsoftware.jeb.util.format.TokenExtractor;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.widgets.Composite;

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
/*     */ public abstract class AbstractInteractiveTextView
        /*     */ extends AbstractUnitFragment<IUnit>
        /*     */ {
    /*  45 */   private static final ILogger logger = GlobalLog.getLogger(AbstractInteractiveTextView.class);
    /*     */
    /*     */   protected ITextDocument idoc;
    /*     */   protected ITextDocumentViewer iviewer;

    /*     */
    /*     */
    public AbstractInteractiveTextView(Composite parent, int style, IUnit unit, IRcpUnitView unitView, RcpClientContext context, ITextDocument idoc)
    /*     */ {
        /*  52 */
        super(parent, style, unit, unitView, context);
        /*  53 */
        this.idoc = idoc;
        /*     */
    }

    /*     */
    /*     */
    public AbstractInteractiveTextView(Composite parent, int style, IUnit unit, IRcpUnitView unitView, IViewManager viewManager, IStatusIndicator statusIndicator, ITextDocument idoc)
    /*     */ {
        /*  58 */
        super(parent, style, unit, unitView, viewManager, statusIndicator);
        /*  59 */
        this.idoc = idoc;
        /*     */
    }

    /*     */
    /*     */
    public ITextDocument getDocument() {
        /*  63 */
        return this.idoc;
        /*     */
    }

    /*     */
    /*     */
    public ITextDocumentViewer getViewer() {
        /*  67 */
        return this.iviewer;
        /*     */
    }

    /*     */
    /*     */
    protected void addStandardContextMenu(final int... additionalGroups) {
        /*  71 */
        new ContextMenu(this).addContextMenu(new IContextMenu()
                /*     */ {
            /*     */
            public void fillContextMenu(IMenuManager menuMgr) {
                /*  74 */
                if (AbstractInteractiveTextView.this.getContext() == null) {
                    /*  75 */
                    return;
                    /*     */
                }
                /*  77 */
                AllHandlers.getInstance().fillManager(menuMgr, 6);
                /*  78 */
                if ((AbstractInteractiveTextView.this.unit instanceof INativeCodeUnit)) {
                    /*  79 */
                    AllHandlers.getInstance().fillManager(menuMgr, 7);
                    /*     */
                }
                /*  81 */
                for (int grp : additionalGroups) {
                    /*  82 */
                    AllHandlers.getInstance().fillManager(menuMgr, grp);
                    /*     */
                }
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    protected boolean doJumpTo() {
        /*  89 */
        if (this.context == null) {
            /*  90 */
            return false;
            /*     */
        }
        /*  92 */
        JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(this.context));
        /*  93 */
        String selection = this.iviewer.getTextWidget().getSelectionText();
        /*  94 */
        if (selection != null)
            /*     */ {
            /*  96 */
            if (selection.length() > 20) {
                /*  97 */
                selection = selection.substring(0, 20);
                /*     */
            }
            /*  99 */
            dlg.setInitialValue(selection);
            /*     */
        }
        /* 101 */
        String address = dlg.open();
        /* 102 */
        if (address == null) {
            /* 103 */
            return false;
            /*     */
        }
        /* 105 */
        return setActiveAddress(address);
        /*     */
    }

    /*     */
    /*     */
    protected boolean doItemFollow()
    /*     */ {
        /* 110 */
        IItem item = getActiveItem();
        /* 111 */
        if (((item instanceof IActionableItem)) && (followItem((IActionableItem) item))) {
            /* 112 */
            return true;
            /*     */
        }
        /*     */
        /*     */
        /* 116 */
        StyledText widget = this.iviewer.getTextWidget();
        /* 117 */
        int offset = widget.getCaretOffset();
        /* 118 */
        int lineIndex = widget.getLineAtOffset(offset);
        /* 119 */
        String line = widget.getLine(lineIndex);
        /* 120 */
        int col = offset - widget.getOffsetAtLine(lineIndex);
        /*     */
        /*     */
        /* 123 */
        String token1 = new TokenExtractor(TokenExtractor.DF_WhiteSpace).extract(line, col);
        /* 124 */
        if ((token1 != null) && (setActiveAddress(token1, null, true))) {
            /* 125 */
            return true;
            /*     */
        }
        /* 127 */
        String token2 = new TokenExtractor(TokenExtractor.DF_CommonSymbolChars).extract(line, col);
        /* 128 */
        if ((token2 != null) && (setActiveAddress(token2, null, true))) {
            /* 129 */
            return true;
            /*     */
        }
        /* 131 */
        String token3 = new TokenExtractor(TokenExtractor.DF_NonAlphaNum).extract(line, col);
        /* 132 */
        if ((token3 != null) && (setActiveAddress(token3, null, true))) {
            /* 133 */
            return true;
            /*     */
        }
        /*     */
        /*     */
        /* 137 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    protected boolean followItem(IActionableItem targetItem) {
        /* 141 */
        if ((targetItem == null) || (targetItem.getItemId() == 0L)) {
            /* 142 */
            return false;
            /*     */
        }
        /* 144 */
        long targetItemId = targetItem.getItemId();
        /* 145 */
        logger.debug("Following item: %s", new Object[]{targetItem});
        /*     */
        /* 147 */
        GlobalPosition pos0 = null;
        /* 148 */
        IViewManager viewManager = getViewManager();
        /* 149 */
        if (viewManager != null) {
            /* 150 */
            pos0 = viewManager.getCurrentGlobalPosition();
            /*     */
        }
        /*     */
        /*     */
        /* 154 */
        ITextDocumentPart part = this.iviewer.getCurrentDocumentPart();
        /* 155 */
        ICoordinates coord = findRootItem(part, targetItemId);
        /* 156 */
        if (coord != null) {
            /* 157 */
            if (pos0 != null) {
                /* 158 */
                viewManager.recordGlobalPosition(pos0);
                /*     */
            }
            /* 160 */
            return this.iviewer.setCaretCoordinates(coord, null, true);
            /*     */
        }
        /*     */
        /*     */
        /* 164 */
        logger.debug("No master item found in the current part, checking if the part is addressable", new Object[0]);
        /* 165 */
        if ((getUnit() instanceof IAddressableUnit)) {
            /* 166 */
            IAddressableUnit iunit = (IAddressableUnit) getUnit();
            /* 167 */
            String address = iunit.getAddressOfItem(targetItemId);
            /*     */
            /*     */
            /* 170 */
            if ((address != null) && (!address.equals(getActiveAddress())) &&
                    /* 171 */         (setActiveAddress(address, null, true))) {
                /* 172 */
                logger.debug("Item to address conversion was successful, jumping to %s", new Object[]{address});
                /* 173 */
                return true;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 179 */
        if ((this.viewNavigatorHelper != null) && (viewManager != null) && (this.viewNavigatorHelper.navigateTo(targetItem, viewManager, false))) {
            /* 180 */
            logger.debug("Jump was successful using the view navigator", new Object[0]);
            /* 181 */
            if (pos0 != null) {
                /* 182 */
                viewManager.recordGlobalPosition(pos0);
                /*     */
            }
            /* 184 */
            return true;
            /*     */
        }
        /*     */
        /*     */
        /* 188 */
        logger.debug("The jump failed", new Object[0]);
        /* 189 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public static ICoordinates findRootItem(ITextDocumentPart part, long itemId) {
        /* 193 */
        long anchorId = TextPartUtil.getFirstAnchorId(part);
        /* 194 */
        int lineIndex;
        if (anchorId >= 0L) {
            /* 195 */
            lineIndex = 0;
            /* 196 */
            for (ILine line : part.getLines()) {
                /* 197 */
                for (ITextItem item0 : line.getItems()) {
                    /* 198 */
                    if (((item0 instanceof IActionableItem)) &&
                            /* 199 */             (((IActionableItem) item0).getItemId() == itemId)) {
                        /* 200 */
                        int flags = ((IActionableItem) item0).getItemFlags();
                        /* 201 */
                        if ((flags & 0x1) != 0) {
                            /* 202 */
                            Coordinates coord = new Coordinates(anchorId, lineIndex, item0.getOffset());
                            /* 203 */
                            return coord;
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 208 */
                lineIndex++;
                /*     */
            }
            /*     */
        }
        /* 211 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\AbstractInteractiveTextView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */