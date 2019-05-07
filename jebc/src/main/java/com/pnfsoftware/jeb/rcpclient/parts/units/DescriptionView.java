/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.events.J;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.FontManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ViewerRefresher;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.text.Document;
/*     */ import org.eclipse.jface.text.TextViewer;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class DescriptionView
        /*     */ extends AbstractTextView<IUnit>
        /*     */ implements IContextMenu
        /*     */ {
    /*     */   private ViewerRefresher refresher;
    /*     */   private IEventListener unitListener;

    /*     */
    /*     */
    public DescriptionView(Composite parent, int flags, RcpClientContext context, final IUnit unit)
    /*     */ {
        /*  40 */
        super(parent, flags, context, unit);
        /*  41 */
        setLayout(new FillLayout());
        /*     */
        /*  43 */
        String description = unit.getDescription();
        /*  44 */
        final Document doc = new Document(description);
        /*     */
        /*  46 */
        TextViewer viewer = buildSimple(this, 768);
        /*     */
        /*     */
        /*  49 */
        this.refresher = new ViewerRefresher(parent.getDisplay(), viewer)
                /*     */ {
            /*     */
            protected void performRefresh() {
                /*  52 */
                doc.set(unit.getDescription());
                /*     */
            }
            /*     */
            /*     */
            /*  56 */
        };
        /*  57 */
        unit.addListener(this. = new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e) {
                /*  60 */
                if (J.isUnitEvent(e)) {
                    /*  61 */
                    DescriptionView.this.refresher.request();
                    /*     */
                }
                /*     */
                /*     */
            }
            /*  65 */
        });
        /*  66 */
        addDisposeListener(new DisposeListener()
                /*     */ {
            /*     */
            public void widgetDisposed(DisposeEvent e) {
                /*  69 */
                unit.removeListener(DescriptionView.this.unitListener);
                /*     */
            }
            /*     */
            /*     */
            /*  73 */
        });
        /*  74 */
        viewer.setDocument(doc);
        /*     */
        /*  76 */
        Control ctl = viewer.getControl();
        /*     */
        /*  78 */
        context.getFontManager().registerWidget(ctl);
        /*     */
        /*  80 */
        new ContextMenu(viewer.getControl()).addContextMenu(this);
        /*     */
    }

    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /*  85 */
        addOperationsToContextMenu(menuMgr);
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /*  90 */
        switch (req.getOperation()) {
            /*     */
            case REFRESH:
                /*  92 */
                return true;
            /*     */
        }
        /*  94 */
        return super.verifyOperation(req);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 100 */
        switch (req.getOperation()) {
            /*     */
            case REFRESH:
                /* 102 */
                this.refresher.request();
                /* 103 */
                return true;
            /*     */
        }
        /* 105 */
        return super.doOperation(req);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\DescriptionView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */