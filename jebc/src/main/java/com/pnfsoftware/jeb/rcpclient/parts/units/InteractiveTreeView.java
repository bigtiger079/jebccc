package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.input.IInputLocation;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.tree.INode;
import com.pnfsoftware.jeb.core.output.tree.INodeCoordinates;
import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
import com.pnfsoftware.jeb.core.output.tree.impl.NodeCoordinates;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
import com.pnfsoftware.jeb.rcpclient.iviewers.tree.InteractiveTreeViewer;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

public class InteractiveTreeView extends AbstractUnitFragment<IUnit> {
    private static final ILogger logger = GlobalLog.getLogger(InteractiveTreeView.class);
    private ITreeDocument idoc;
    private InteractiveTreeViewer iviewer;

    public InteractiveTreeView(Composite parent, int flags, final RcpClientContext context, final IUnit unit, ITreeDocument idoc, IRcpUnitView unitView) {
        super(parent, flags, unit, unitView, context);
        setLayout(new FillLayout());
        this.idoc = idoc;
        this.iviewer = new InteractiveTreeViewer(this, 0, idoc, context.getPropertyManager());
        this.iviewer.setStyleAdapter(new ItemStyleProvider(context.getStyleManager()));
        this.iviewer.initialize();
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                InteractiveTreeView.this.iviewer.dispose();
            }
        });
        setPrimaryWidget(this.iviewer.getTreeWidget());
        this.iviewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                context.refreshHandlersStates();
                NodeCoordinates coord = InteractiveTreeView.this.iviewer.getPosition();
                String address = null;
                IInputLocation location = null;
                if (coord != null) {
                    address = InteractiveTreeView.this.getAddressAt(coord);
                    if ((address != null) && ((unit instanceof IInteractiveUnit))) {
                        IInteractiveUnit iunit = (IInteractiveUnit) unit;
                        location = iunit.addressToLocation(address);
                    }
                }
                String statusText = String.format("coord: %s | addr: %s | loc: %s", new Object[]{Strings.safe(coord, "?"), Strings.safe(address, "?"), Strings.safe(location, "?")});
                context.getStatusIndicator().setText(statusText);
            }
        });
    }

    public boolean isActiveItem(IItem item) {
        return (item != null) && (getActiveItem() == item);
    }

    public IItem getActiveItem() {
        return this.iviewer.getSelectedNode();
    }

    public String getActiveAddress(AddressConversionPrecision precision) {
        INodeCoordinates coord = this.iviewer.getPosition();
        return getAddressAt(coord);
    }

    public String getAddressAt(INodeCoordinates coord) {
        if (coord == null) {
            return null;
        }
        if ((this.iviewer.getFilteredTreeViewer().isFiltered()) || (this.iviewer.getTreeWidget().getSortColumn() != null)) {
            return null;
        }
        return this.idoc.coordinatesToAddress(coord);
    }

    public boolean isValidActiveAddress(String address, Object object) {
        try {
            INodeCoordinates coord = this.idoc.addressToCoordinates(address);
            return coord != null;
        } catch (Exception e) {
            logger.catching(e);
        }
        return false;
    }

    public boolean setActiveAddress(String address, Object extra, boolean record) {
        INodeCoordinates coord;
        try {
            coord = this.idoc.addressToCoordinates(address);
        } catch (Exception e) {
            logger.catching(e);
            return false;
        }
        if (coord == null) {
            return false;
        }
        this.iviewer.setPosition(coord, true);
        return true;
    }

    public boolean verifyOperation(OperationRequest req) {
        if (!this.iviewer.getTreeWidget().isFocusControl()) {
            return false;
        }
        if (this.iviewer.verifyOperation(req)) {
            return false;
        }
        switch (req.getOperation()) {
            case JUMP_TO:
                return true;
            case ITEM_FOLLOW:
                return ((getActiveItem() instanceof IActionableItem)) && (!isMaster((IActionableItem) getActiveItem()));
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
            case JUMP_TO:
                JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(getContext()));
                String address = dlg.open();
                if (address != null) {
                    return setActiveAddress(address);
                }
                return false;
            case ITEM_FOLLOW:
                IItem item = getActiveItem();
                if (!(item instanceof IActionableItem)) {
                    return false;
                }
                logger.debug("Following item: %s", new Object[]{item});
                return followItem((IActionableItem) item);
        }
        return false;
    }

    private boolean followItem(IActionableItem item) {
        logger.debug("Following item: %s", new Object[]{item});
        if (item == null) {
            return false;
        }
        List<? extends INode> roots = this.iviewer.getInfiniDocument().getRoots();
        return followItem(item.getItemId(), roots, new ArrayList<>());
    }

    private boolean followItem(long itemId, List<? extends INode> nodes, List<Integer> coordinates) {
        int index = 0;
        for (INode root : nodes) {
            if ((root instanceof IActionableItem)) {
                IActionableItem actionableRoot = (IActionableItem) root;
                if ((actionableRoot.getItemId() == itemId) && (isMaster(actionableRoot))) {
                    return this.iviewer.setPosition(new NodeCoordinates(Arrays.asList(new Integer[]{Integer.valueOf(index)})), true);
                }
            }
            List<Integer> children = new ArrayList<>(coordinates);
            children.add(Integer.valueOf(index));
            boolean followChild = followItem(itemId, root.getChildren(), children);
            if (followChild) {
                return followChild;
            }
            index++;
        }
        return false;
    }

    private boolean isMaster(IActionableItem item) {
        return (item.getItemFlags() & 0x1) != 0;
    }

    public byte[] export() {
        return Strings.encodeUTF8(this.iviewer.exportToString());
    }

    public AbstractUnitFragment.FragmentType getFragmentType() {
        return AbstractUnitFragment.FragmentType.TREE;
    }
}


