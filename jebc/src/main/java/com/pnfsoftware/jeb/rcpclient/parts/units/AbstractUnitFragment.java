/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.IOperable;
/*     */ import com.pnfsoftware.jeb.client.api.Operation;
/*     */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
/*     */ import com.pnfsoftware.jeb.rcpclient.IViewManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.StatusIndicatorData;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.IViewNavigator;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import org.eclipse.jface.action.Action;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.action.Separator;
/*     */ import org.eclipse.swt.events.FocusEvent;
/*     */ import org.eclipse.swt.events.FocusListener;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;

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
/*     */ public abstract class AbstractUnitFragment<T extends IUnit>
        /*     */ extends Composite
        /*     */ implements IRcpUnitFragment
        /*     */ {
    /*  41 */   private static final ILogger logger = GlobalLog.getLogger(AbstractUnitFragment.class);
    /*     */
    /*     */   public static final int DEFAULT_PRIORITY = 100;
    /*     */
    /*     */   protected T unit;
    /*     */
    /*     */   protected RcpClientContext context;
    /*     */
    /*     */   protected IRcpUnitView unitView;
    /*     */
    /*     */   protected IViewNavigator viewNavigatorHelper;
    /*     */
    /*     */   protected IOperable master;
    /*     */
    /*     */   private IViewManager viewManager;
    /*     */
    /*     */   private IStatusIndicator statusIndicator;
    /*     */
    /*     */   private Control primaryWidget;
    /*     */
    /*     */   private FocusListener focusListener;
    /*     */
    /*     */   private StatusIndicatorData statusData;
    /*     */   private boolean defaultFragment;
    /*     */   private int priority;

    /*     */
    /*     */   public static enum FragmentType
            /*     */ {
        /*  69 */     TEXT, TABLE, TREE, IMAGE, BINARY, UNKNOWN;

        /*     */
        /*     */
        private FragmentType() {
        }
        /*     */
    }

    /*     */
    /*  74 */
    public AbstractUnitFragment(Composite parent, int style, T unit, IRcpUnitView unitView, IViewManager viewManager, IStatusIndicator statusIndicator) {
        super(parent, style);
        /*  75 */
        this.priority = 100;
        /*  76 */
        this.unit = unit;
        /*  77 */
        this.unitView = unitView;
        /*  78 */
        this.viewManager = viewManager;
        /*  79 */
        this.statusIndicator = statusIndicator;
        /*     */
    }

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
    public AbstractUnitFragment(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context)
    /*     */ {
        /*  94 */
        super(parent, style);
        /*  95 */
        this.priority = 100;
        /*  96 */
        this.unit = unit;
        /*  97 */
        this.unitView = unitView;
        /*  98 */
        this.context = context;
        /*     */
    }

    /*     */
    /*     */
    protected Control getPrimaryWidget() {
        /* 102 */
        return this.primaryWidget;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    protected void setPrimaryWidget(Control widget)
    /*     */ {
        /* 114 */
        if (this.primaryWidget != null) {
            /* 115 */
            if (this.focusListener != null) {
                /* 116 */
                this.primaryWidget.removeFocusListener(this.focusListener);
                /* 117 */
                this.focusListener = null;
                /*     */
            }
            /* 119 */
            this.primaryWidget = null;
            /*     */
        }
        /*     */
        /* 122 */
        if (widget == null) {
            /* 123 */
            return;
            /*     */
        }
        /* 125 */
        this.primaryWidget = widget;
        /*     */
        /* 127 */
        this.primaryWidget.addFocusListener(this. = new FocusListener()
                /*     */ {
            /*     */
            public void focusLost(FocusEvent e) {
                /* 130 */
                AbstractUnitFragment.this.onFocusLost(e);
                /*     */
            }

            /*     */
            /*     */
            public void focusGained(FocusEvent e)
            /*     */ {
                /* 135 */
                AbstractUnitFragment.this.onFocusGained(e);
                /*     */
            }
            /*     */
            /* 138 */
        });
        /* 139 */
        this.primaryWidget.addMouseListener(new MouseAdapter()
                /*     */ {
            /*     */
            public void mouseUp(MouseEvent e)
            /*     */ {
                /* 143 */
                Operation op = null;
                /*     */
                /*     */
                /* 146 */
                if (e.button == 4) {
                    /* 147 */
                    op = Operation.NAVIGATE_BACKWARD;
                    /*     */
                    /*     */
                }
                /* 150 */
                else if (e.button == 5) {
                    /* 151 */
                    op = Operation.NAVIGATE_FORWARD;
                    /*     */
                }
                /*     */
                /*     */
                /* 155 */
                if (op != null) {
                    /* 156 */
                    OperationRequest req = new OperationRequest(op);
                    /*     */
                    /* 158 */
                    if (AbstractUnitFragment.this.unitView != null) {
                        /* 159 */
                        if (AbstractUnitFragment.this.unitView.verifyOperation(req)) {
                            /* 160 */
                            AbstractUnitFragment.this.unitView.doOperation(req);
                            /*     */
                        }
                        /*     */
                        /*     */
                    }
                    /* 164 */
                    else if (AbstractUnitFragment.this.verifyOperation(req)) {
                        /* 165 */
                        AbstractUnitFragment.this.doOperation(req);
                        /*     */
                    }
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
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    protected void onFocusLost(FocusEvent e)
    /*     */ {
        /* 182 */
        if (getStatusIndicator() != null) {
            /* 183 */
            this.statusData = getStatusIndicator().save();
            /* 184 */
            getStatusIndicator().clear();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    protected void onFocusGained(FocusEvent e)
    /*     */ {
        /* 197 */
        if ((getStatusIndicator() != null) && (this.statusData != null)) {
            /* 198 */
            getStatusIndicator().restore(this.statusData);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public RcpClientContext getContext() {
        /* 203 */
        return this.context;
        /*     */
    }

    /*     */
    /*     */
    public Control getFragmentControl()
    /*     */ {
        /* 208 */
        return this;
        /*     */
    }

    /*     */
    /*     */
    public IStatusIndicator getStatusIndicator()
    /*     */ {
        /* 213 */
        return this.context == null ? this.statusIndicator : this.context.getStatusIndicator();
        /*     */
    }

    /*     */
    /*     */
    public IViewManager getViewManager()
    /*     */ {
        /* 218 */
        return this.context == null ? this.viewManager : this.context.getPartManager();
        /*     */
    }

    /*     */
    /*     */
    public void setViewNavigatorHelper(IViewNavigator viewNavigatorHelper)
    /*     */ {
        /* 223 */
        this.viewNavigatorHelper = viewNavigatorHelper;
        /*     */
    }

    /*     */
    /*     */
    public IViewNavigator getViewNavigatorHelper()
    /*     */ {
        /* 228 */
        return this.viewNavigatorHelper;
        /*     */
    }

    /*     */
    /*     */
    public T getUnit()
    /*     */ {
        /* 233 */
        return this.unit;
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 238 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 243 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    protected void addOperationsToContextMenu(IMenuManager menuMgr)
    /*     */ {
        /* 253 */
        menuMgr.add(new Separator());
        /* 254 */
        for (final Operation op : Operation.values()) {
            /* 255 */
            OperationRequest req = new OperationRequest(op);
            /* 256 */
            if (verifyOperation(req)) {
                /* 257 */
                menuMgr.add(new Action(op.toString())
                        /*     */ {
                    /*     */
                    public void run() {
                        /* 260 */
                        AbstractUnitFragment.this.doOperation(new OperationRequest(op));
                        /*     */
                    }
                    /*     */
                });
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public boolean isActiveItem(IItem item)
    /*     */ {
        /* 269 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 274 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public String getActiveItemAsText()
    /*     */ {
        /* 279 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress()
    /*     */ {
        /* 284 */
        return getActiveAddress(AddressConversionPrecision.DEFAULT);
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 289 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public Position getActivePosition()
    /*     */ {
        /* 294 */
        return new Position(getActiveAddress(), null);
        /*     */
    }

    /*     */
    /*     */
    public boolean isValidActiveAddress(String address, Object object)
    /*     */ {
        /* 299 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public boolean setActiveAddress(String address)
    /*     */ {
        /* 304 */
        return setActiveAddress(address, null, true);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean setActiveAddress(String address, Object extraAddressDetails, boolean recordPosition)
    /*     */ {
        /* 310 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public String getComment()
    /*     */ {
        /* 315 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public void setDefaultFragment(boolean defaultFragment) {
        /* 319 */
        this.defaultFragment = defaultFragment;
        /*     */
    }

    /*     */
    /*     */
    public boolean isDefaultFragment()
    /*     */ {
        /* 324 */
        return this.defaultFragment;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 336 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public String getExportExtension()
    /*     */ {
        /* 345 */
        switch (getFragmentType()) {
            /*     */
            case TEXT:
                /* 347 */
                return ".txt";
            /*     */
            case TABLE:
                /* 349 */
                return ".csv";
            /*     */
            case TREE:
                /* 351 */
                return ".xml";
            /*     */
            case IMAGE:
                /* 353 */
                return ".bmp";
            /*     */
            case BINARY:
                /* 355 */
                return ".bin";
            /*     */
        }
        /*     */
        /* 358 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public FragmentType getFragmentType()
    /*     */ {
        /* 368 */
        return FragmentType.UNKNOWN;
        /*     */
    }

    /*     */
    /*     */
    public void setFocusPriority(int priority) {
        /* 372 */
        this.priority = priority;
        /*     */
    }

    /*     */
    /*     */
    public int getFocusPriority()
    /*     */ {
        /* 377 */
        return this.priority;
        /*     */
    }

    /*     */
    /*     */
    protected boolean requestOperation(OperationRequest req)
    /*     */ {
        /* 382 */
        if (this.unitView != null) {
            /* 383 */
            if (this.unitView.verifyOperation(req)) {
                /* 384 */
                return this.unitView.doOperation(req);
                /*     */
            }
            /* 386 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /* 390 */
        if ((this.master != null) && (this.master != this)) {
            /* 391 */
            if (this.master.verifyOperation(req)) {
                /* 392 */
                return this.master.doOperation(req);
                /*     */
            }
            /* 394 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /* 398 */
        if (verifyOperation(req)) {
            /* 399 */
            return doOperation(req);
            /*     */
        }
        /*     */
        /*     */
        /* 403 */
        return false;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\AbstractUnitFragment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */