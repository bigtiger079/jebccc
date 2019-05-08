package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.StatusIndicatorData;
import com.pnfsoftware.jeb.rcpclient.parts.IViewNavigator;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractUnitFragment<T extends IUnit> extends Composite implements IRcpUnitFragment {
    private static final ILogger logger = GlobalLog.getLogger(AbstractUnitFragment.class);
    public static final int DEFAULT_PRIORITY = 100;
    protected T unit;
    protected RcpClientContext context;
    protected IRcpUnitView unitView;
    protected IViewNavigator viewNavigatorHelper;
    protected IOperable master;
    private IViewManager viewManager;
    private IStatusIndicator statusIndicator;
    private Control primaryWidget;
    private FocusListener focusListener;
    private StatusIndicatorData statusData;
    private boolean defaultFragment;
    private int priority;

    public static enum FragmentType {
        TEXT, TABLE, TREE, IMAGE, BINARY, UNKNOWN;

        private FragmentType() {
        }
    }

    public AbstractUnitFragment(Composite parent, int style, T unit, IRcpUnitView unitView, IViewManager viewManager, IStatusIndicator statusIndicator) {
        super(parent, style);
        this.priority = 100;
        this.unit = unit;
        this.unitView = unitView;
        this.viewManager = viewManager;
        this.statusIndicator = statusIndicator;
    }

    public AbstractUnitFragment(Composite parent, int style, T unit, IRcpUnitView unitView, RcpClientContext context) {
        super(parent, style);
        this.priority = 100;
        this.unit = unit;
        this.unitView = unitView;
        this.context = context;
    }

    protected Control getPrimaryWidget() {
        return this.primaryWidget;
    }

    protected void setPrimaryWidget(Control widget) {
        if (this.primaryWidget != null) {
            if (this.focusListener != null) {
                this.primaryWidget.removeFocusListener(this.focusListener);
                this.focusListener = null;
            }
            this.primaryWidget = null;
        }
        if (widget == null) {
            return;
        }
        this.primaryWidget = widget;
        this.primaryWidget.addFocusListener(this.focusListener = new FocusListener() {
            public void focusLost(FocusEvent e) {
                AbstractUnitFragment.this.onFocusLost(e);
            }

            public void focusGained(FocusEvent e) {
                AbstractUnitFragment.this.onFocusGained(e);
            }
        });
        this.primaryWidget.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent e) {
                logger.error("mouseUp");
                Operation op = null;
                if (e.button == 4) {
                    op = Operation.NAVIGATE_BACKWARD;
                } else if (e.button == 5) {
                    op = Operation.NAVIGATE_FORWARD;
                }
                if (op != null) {
                    OperationRequest req = new OperationRequest(op);
                    if (AbstractUnitFragment.this.unitView != null) {
                        if (AbstractUnitFragment.this.unitView.verifyOperation(req)) {
                            AbstractUnitFragment.this.unitView.doOperation(req);
                        }
                    } else if (AbstractUnitFragment.this.verifyOperation(req)) {
                        AbstractUnitFragment.this.doOperation(req);
                    }
                }
            }
        });
    }

    protected void onFocusLost(FocusEvent e) {
        if (getStatusIndicator() != null) {
            this.statusData = getStatusIndicator().save();
            getStatusIndicator().clear();
        }
    }

    protected void onFocusGained(FocusEvent e) {
        if ((getStatusIndicator() != null) && (this.statusData != null)) {
            getStatusIndicator().restore(this.statusData);
        }
    }

    public RcpClientContext getContext() {
        return this.context;
    }

    public Control getFragmentControl() {
        return this;
    }

    public IStatusIndicator getStatusIndicator() {
        return this.context == null ? this.statusIndicator : this.context.getStatusIndicator();
    }

    public IViewManager getViewManager() {
        return this.context == null ? this.viewManager : this.context.getPartManager();
    }

    public void setViewNavigatorHelper(IViewNavigator viewNavigatorHelper) {
        this.viewNavigatorHelper = viewNavigatorHelper;
    }

    public IViewNavigator getViewNavigatorHelper() {
        return this.viewNavigatorHelper;
    }

    public T getUnit() {
        return this.unit;
    }

    public boolean verifyOperation(OperationRequest req) {
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        return false;
    }

    protected void addOperationsToContextMenu(IMenuManager menuMgr) {
        menuMgr.add(new Separator());
        for (final Operation op : Operation.values()) {
            OperationRequest req = new OperationRequest(op);
            if (verifyOperation(req)) {
                menuMgr.add(new Action(op.toString()) {
                    public void run() {
                        AbstractUnitFragment.this.doOperation(new OperationRequest(op));
                    }
                });
            }
        }
    }

    public boolean isActiveItem(IItem item) {
        return false;
    }

    public IItem getActiveItem() {
        return null;
    }

    public String getActiveItemAsText() {
        return null;
    }

    public String getActiveAddress() {
        return getActiveAddress(AddressConversionPrecision.DEFAULT);
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        return null;
    }

    public Position getActivePosition() {
        return new Position(getActiveAddress(), null);
    }

    public boolean isValidActiveAddress(String address, Object object) {
        return false;
    }

    public boolean setActiveAddress(String address) {
        return setActiveAddress(address, null, true);
    }

    public boolean setActiveAddress(String address, Object extraAddressDetails, boolean recordPosition) {
        return false;
    }

    public String getComment() {
        return null;
    }

    public void setDefaultFragment(boolean defaultFragment) {
        this.defaultFragment = defaultFragment;
    }

    public boolean isDefaultFragment() {
        return this.defaultFragment;
    }

    public byte[] export() {
        return null;
    }

    public String getExportExtension() {
        switch (getFragmentType()) {
            case TEXT:
                return ".txt";
            case TABLE:
                return ".csv";
            case TREE:
                return ".xml";
            case IMAGE:
                return ".bmp";
            case BINARY:
                return ".bin";
        }
        return null;
    }

    public FragmentType getFragmentType() {
        return FragmentType.UNKNOWN;
    }

    public void setFocusPriority(int priority) {
        this.priority = priority;
    }

    public int getFocusPriority() {
        return this.priority;
    }

    protected boolean requestOperation(OperationRequest req) {
        if (this.unitView != null) {
            if (this.unitView.verifyOperation(req)) {
                return this.unitView.doOperation(req);
            }
            return false;
        }
        if ((this.master != null) && (this.master != this)) {
            if (this.master.verifyOperation(req)) {
                return this.master.doOperation(req);
            }
            return false;
        }
        if (verifyOperation(req)) {
            return doOperation(req);
        }
        return false;
    }
}


