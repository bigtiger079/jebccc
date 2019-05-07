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
/*     */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
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
/*     */ import java.util.HashSet;
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
/*     */ public class ItemStyleProvider
        /*     */ implements IStyleProvider
        /*     */ {
    /*  40 */   private static final ILogger logger = GlobalLog.getLogger(ItemStyleProvider.class);
    /*     */
    /*     */   private StyleManager styleman;
    /*     */   private IItem activeItem;
    /*  44 */   private Set<IActionableItem> relatedItems = new HashSet();

    /*     */
    /*     */
    public ItemStyleProvider(StyleManager styleman) {
        /*  47 */
        this.styleman = styleman;
        /*     */
    }

    /*     */
    /*     */
    public Style getStyle(ItemClassIdentifiers classId, boolean active)
    /*     */ {
        /*  52 */
        return this.styleman.getStyle(classId, active);
        /*     */
    }

    /*     */
    /*     */
    public Style getStyle(IItem item)
    /*     */ {
        /*  57 */
        if (!(item instanceof IVisualItem)) {
            /*  58 */
            return null;
            /*     */
        }
        /*  60 */
        ItemClassIdentifiers classId = ((IVisualItem) item).getClassId();
        /*  61 */
        return getStyle(classId, isActiveItem(item));
        /*     */
    }

    /*     */
    /*     */
    public Color getOnCaretBackgroundColor()
    /*     */ {
        /*  66 */
        return this.styleman.getOnCaretBackground();
        /*     */
    }

    /*     */
    /*     */
    public void registerTextViewer(final ITextDocumentViewer iviewer)
    /*     */ {
        /*  71 */
        this.styleman.addListener(new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e) {
                /*  74 */
                iviewer.refresh();
                /*     */
            }
            /*     */
            /*     */
            /*  78 */
        });
        /*  79 */
        iviewer.addItemListener(new IItemListener()
                /*     */ {
            /*     */
            public void notifyItemEvent(ITextDocumentViewer viewer, ItemEvent e) {
                /*  82 */
                if (e.type == 1) {
                    /*  83 */
                    ItemStyleProvider.logger.debug("On-caret Item: %s", new Object[]{e.item});
                    /*  84 */
                    ItemStyleProvider.this.activeItem = e.item;
                    /*  85 */
                    if ((ItemStyleProvider.this.activeItem instanceof IActionableTextItem)) {
                        /*  86 */
                        Set<IActionableItem> similarItems = ItemStyleProvider.this.findSimilarActionableItems(iviewer,
                                /*  87 */               (IActionableTextItem) ItemStyleProvider.this.activeItem);
                        /*  88 */
                        ItemStyleProvider.this.relatedItems.addAll(similarItems);
                        /*     */
                    }
                    /*     */
                }
                /*  91 */
                else if (e.type == 0) {
                    /*  92 */
                    ItemStyleProvider.this.activeItem = null;
                    /*  93 */
                    ItemStyleProvider.this.relatedItems.clear();
                    /*     */
                }
                /*  95 */
                iviewer.refreshStyles();
                /*     */
            }
            /*     */
            /*  98 */
        });
        /*  99 */
        iviewer.addPositionListener(new IPositionListener()
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
                /* 107 */
                if (focusChange < 0) {
                    /* 108 */
                    return;
                    /*     */
                }
                /* 110 */
                viewer.activateCurrentLine(true);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    private Set<IActionableItem> findSimilarActionableItems(ITextDocumentViewer iviewer, IActionableItem target) {
        /* 116 */
        Set<IActionableItem> r = new HashSet();
        /* 117 */
        long targetId = target.getItemId();
        /* 118 */
        if (targetId != 0L) {
            /* 119 */
            for (ILine line : iviewer.getCurrentDocumentPart().getLines()) {
                /* 120 */
                for (IItem item : line.getItems()) {
                    /* 121 */
                    if (((item instanceof IActionableItem)) &&
                            /* 122 */             (((IActionableItem) item).getItemId() == targetId)) {
                        /* 123 */
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
        /* 129 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public boolean isActiveItem(IItem item)
    /*     */ {
        /* 134 */
        return (this.activeItem == item) || (this.relatedItems.contains(item));
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem() {
        /* 138 */
        return this.activeItem;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\ItemStyleProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */