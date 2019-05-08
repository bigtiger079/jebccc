
package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.IVisualItem;
import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.core.output.text.IActionableTextItem;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
import com.pnfsoftware.jeb.rcpclient.iviewers.StyleManager;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.IItemListener;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.IPositionListener;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ItemEvent;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.Color;

public class ItemStyleProvider
        implements IStyleProvider {
    private static final ILogger logger = GlobalLog.getLogger(ItemStyleProvider.class);
    private StyleManager styleman;
    private IItem activeItem;
    private Set<IActionableItem> relatedItems = new HashSet();

    public ItemStyleProvider(StyleManager styleman) {
        this.styleman = styleman;
    }

    public Style getStyle(ItemClassIdentifiers classId, boolean active) {
        return this.styleman.getStyle(classId, active);
    }

    public Style getStyle(IItem item) {
        if (!(item instanceof IVisualItem)) {
            return null;
        }
        ItemClassIdentifiers classId = ((IVisualItem) item).getClassId();
        return getStyle(classId, isActiveItem(item));
    }

    public Color getOnCaretBackgroundColor() {
        return this.styleman.getOnCaretBackground();
    }

    public void registerTextViewer(final ITextDocumentViewer iviewer) {
        this.styleman.addListener(new IEventListener() {
            public void onEvent(IEvent e) {
                iviewer.refresh();
            }
        });
        iviewer.addItemListener(new IItemListener() {
            public void notifyItemEvent(ITextDocumentViewer viewer, ItemEvent e) {
                if (e.type == 1) {
                    ItemStyleProvider.logger.debug("On-caret Item: %s", new Object[]{e.item});
                    ItemStyleProvider.this.activeItem = e.item;
                    if ((ItemStyleProvider.this.activeItem instanceof IActionableTextItem)) {
                        Set<IActionableItem> similarItems = ItemStyleProvider.this.findSimilarActionableItems(iviewer,
                                (IActionableTextItem) ItemStyleProvider.this.activeItem);
                        ItemStyleProvider.this.relatedItems.addAll(similarItems);
                    }
                } else if (e.type == 0) {
                    ItemStyleProvider.this.activeItem = null;
                    ItemStyleProvider.this.relatedItems.clear();
                }
                iviewer.refreshStyles();
            }
        });
        iviewer.addPositionListener(new IPositionListener() {
            public void positionUnchangedAttemptBreakout(ITextDocumentViewer viewer, int direction) {
            }

            public void positionChanged(ITextDocumentViewer viewer, ICoordinates coordinates, int focusChange) {
                if (focusChange < 0) {
                    return;
                }
                viewer.activateCurrentLine(true);
            }
        });
    }

    private Set<IActionableItem> findSimilarActionableItems(ITextDocumentViewer iviewer, IActionableItem target) {
        Set<IActionableItem> r = new HashSet();
        long targetId = target.getItemId();
        if (targetId != 0L) {
            for (ILine line : iviewer.getCurrentDocumentPart().getLines()) {
                for (IItem item : line.getItems()) {
                    if (((item instanceof IActionableItem)) &&
                            (((IActionableItem) item).getItemId() == targetId)) {
                        r.add((IActionableItem) item);
                    }
                }
            }
        }
        return r;
    }

    public boolean isActiveItem(IItem item) {
        return (this.activeItem == item) || (this.relatedItems.contains(item));
    }

    public IItem getActiveItem() {
        return this.activeItem;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\ItemStyleProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */