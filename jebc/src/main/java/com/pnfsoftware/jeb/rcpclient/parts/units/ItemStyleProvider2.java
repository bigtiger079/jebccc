/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.IActionableItem;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.IVisualItem;
/*     */ import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
/*     */ import com.pnfsoftware.jeb.core.output.text.IActionableTextItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextItem;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.StyleManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.IItemListener;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.IPositionListener;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.ItemEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.graphics.Color;

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
/*     */
/*     */
/*     */ public class ItemStyleProvider2
        /*     */ implements IStyleProvider
        /*     */ {
    /*  42 */   private static final ILogger logger = GlobalLog.getLogger(ItemStyleProvider2.class);
    /*     */
    /*     */   private StyleManager styleman;
    /*     */   private IItem activeItem;
    /*  46 */   private Set<IActionableItem> relatedItems = new HashSet();
    /*     */
    /*  48 */   private List<ITextDocumentViewer> viewers = new ArrayList();

    /*     */
    /*     */
    public ItemStyleProvider2(StyleManager styleman) {
        /*  51 */
        this.styleman = styleman;
        /*     */
    }

    /*     */
    /*     */
    public Style getStyle(ItemClassIdentifiers classId, boolean active)
    /*     */ {
        /*  56 */
        return this.styleman.getStyle(classId, active);
        /*     */
    }

    /*     */
    /*     */
    public Style getStyle(IItem item)
    /*     */ {
        /*  61 */
        if (!(item instanceof IVisualItem)) {
            /*  62 */
            return null;
            /*     */
        }
        /*  64 */
        ItemClassIdentifiers classId = ((IVisualItem) item).getClassId();
        /*  65 */
        return getStyle(classId, isActiveItem(item));
        /*     */
    }

    /*     */
    /*     */
    public Color getOnCaretBackgroundColor()
    /*     */ {
        /*  70 */
        return this.styleman.getOnCaretBackground();
        /*     */
    }

    /*     */
    /*     */
    public void registerTextViewer(final ITextDocumentViewer viewer) {
        /*  74 */
        this.viewers.add(viewer);
        /*     */
        /*     */
        /*  77 */
        this.styleman.addListener(new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e) {
                /*  80 */
                viewer.refresh();
                /*     */
            }
            /*     */
            /*  83 */
        });
        /*  84 */
        viewer.addItemListener(this.itemListener);
        /*     */
        /*  86 */
        viewer.addPositionListener(this.positionListener);
        /*     */
    }

    /*     */
    /*     */
    private Set<IActionableItem> findSimilarActionableItems(List<ITextDocumentViewer> viewers, IActionableItem target) {
        /*  90 */
        Set<IActionableItem> r = new HashSet();
        /*  91 */
        long targetId = target.getItemId();
        /*  92 */
        if (targetId != 0L) {
            /*  93 */
            for (ITextDocumentViewer viewer : viewers) {
                /*  94 */
                for (ITextItem item : viewer.getCurrentItems()) {
                    /*  95 */
                    if (((item instanceof IActionableItem)) &&
                            /*  96 */             (((IActionableItem) item).getItemId() == targetId)) {
                        /*  97 */
                        r.add((IActionableItem) item);
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 103 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public boolean isActiveItem(IItem item)
    /*     */ {
        /* 108 */
        return (this.activeItem == item) || (this.relatedItems.contains(item));
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem() {
        /* 112 */
        return this.activeItem;
        /*     */
    }

    /*     */
    /* 115 */   private IItemListener itemListener = new IItemListener() {
        /*     */
        public void notifyItemEvent(ITextDocumentViewer viewer, ItemEvent e) {
            /*     */
            Set<IActionableItem> similarItems;
            /* 118 */
            if (e.type == 1) {
                /* 119 */
                ItemStyleProvider2.logger.debug("On-caret Item: %s", new Object[]{e.item});
                /* 120 */
                ItemStyleProvider2.this.activeItem = e.item;
                /* 121 */
                if ((ItemStyleProvider2.this.activeItem instanceof IActionableTextItem)) {
                    /* 122 */
                    similarItems = ItemStyleProvider2.this.findSimilarActionableItems(ItemStyleProvider2.this.viewers,
                            /* 123 */             (IActionableTextItem) ItemStyleProvider2.this.activeItem);
                    /* 124 */
                    ItemStyleProvider2.this.relatedItems.addAll(similarItems);
                    /*     */
                }
                /*     */
            }
            /* 127 */
            else if (e.type == 0) {
                /* 128 */
                ItemStyleProvider2.this.activeItem = null;
                /* 129 */
                ItemStyleProvider2.this.relatedItems.clear();
                /*     */
            }
            /*     */
            /* 132 */
            for (ITextDocumentViewer v : ItemStyleProvider2.this.viewers) {
                /* 133 */
                v.refreshStyles();
                /*     */
            }
            /*     */
        }
        /*     */
    };
    /*     */
    /* 138 */   private IPositionListener positionListener = new IPositionListener()
            /*     */ {
        /*     */
        public void positionUnchangedAttemptBreakout(ITextDocumentViewer viewer, int direction) {
        }

        /*     */
        /*     */
        /*     */
        /*     */
        public void positionChanged(ITextDocumentViewer viewer, ICoordinates coordinates, int focusChange)
        /*     */ {
            /* 146 */
            if (focusChange < 0) {
                /* 147 */
                return;
                /*     */
            }
            /* 149 */
            viewer.activateCurrentLine(true);
            /* 150 */
            for (ITextDocumentViewer v : ItemStyleProvider2.this.viewers) {
                /* 151 */
                if (v != viewer)
                    /*     */ {
                    /*     */
                    /* 154 */
                    v.activateCurrentLine(false);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
    };
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\ItemStyleProvider2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */