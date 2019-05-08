package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.IVisualItem;
import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.core.output.text.IActionableTextItem;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Color;

public class ItemStyleProvider2 implements IStyleProvider {
    private static final ILogger logger = GlobalLog.getLogger(ItemStyleProvider2.class);
    private StyleManager styleman;
    private IItem activeItem;
    private Set<IActionableItem> relatedItems = new HashSet();
    private List<ITextDocumentViewer> viewers = new ArrayList();

    public ItemStyleProvider2(StyleManager styleman) {
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

    public void registerTextViewer(final ITextDocumentViewer viewer) {
        this.viewers.add(viewer);
        this.styleman.addListener(new IEventListener() {
            public void onEvent(IEvent e) {
                viewer.refresh();
            }
        });
        viewer.addItemListener(this.itemListener);
        viewer.addPositionListener(this.positionListener);
    }

    private Set<IActionableItem> findSimilarActionableItems(List<ITextDocumentViewer> viewers, IActionableItem target) {
        Set<IActionableItem> r = new HashSet();
        long targetId = target.getItemId();
        if (targetId != 0L) {
            for (ITextDocumentViewer viewer : viewers) {
                for (ITextItem item : viewer.getCurrentItems()) {
                    if (((item instanceof IActionableItem)) && (((IActionableItem) item).getItemId() == targetId)) {
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

    private IItemListener itemListener = new IItemListener() {
        public void notifyItemEvent(ITextDocumentViewer viewer, ItemEvent e) {
            Set<IActionableItem> similarItems;
            if (e.type == 1) {
                ItemStyleProvider2.logger.debug("On-caret Item: %s", new Object[]{e.item});
                ItemStyleProvider2.this.activeItem = e.item;
                if ((ItemStyleProvider2.this.activeItem instanceof IActionableTextItem)) {
                    similarItems = ItemStyleProvider2.this.findSimilarActionableItems(ItemStyleProvider2.this.viewers, (IActionableTextItem) ItemStyleProvider2.this.activeItem);
                    ItemStyleProvider2.this.relatedItems.addAll(similarItems);
                }
            } else if (e.type == 0) {
                ItemStyleProvider2.this.activeItem = null;
                ItemStyleProvider2.this.relatedItems.clear();
            }
            for (ITextDocumentViewer v : ItemStyleProvider2.this.viewers) {
                v.refreshStyles();
            }
        }
    };
    private IPositionListener positionListener = new IPositionListener() {
        public void positionUnchangedAttemptBreakout(ITextDocumentViewer viewer, int direction) {
        }

        public void positionChanged(ITextDocumentViewer viewer, ICoordinates coordinates, int focusChange) {
            if (focusChange < 0) {
                return;
            }
            viewer.activateCurrentLine(true);
            for (ITextDocumentViewer v : ItemStyleProvider2.this.viewers) {
                if (v != viewer) {
                    v.activateCurrentLine(false);
                }
            }
        }
    };
}


