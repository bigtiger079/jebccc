/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.input.IInputLocation;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IActionableItem;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.tree.INode;
/*     */ import com.pnfsoftware.jeb.core.output.tree.INodeCoordinates;
/*     */ import com.pnfsoftware.jeb.core.output.tree.ITreeDocument;
/*     */ import com.pnfsoftware.jeb.core.output.tree.impl.NodeCoordinates;
/*     */ import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.JumpToDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTreeViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.tree.InteractiveTreeViewer;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.viewers.ISelectionChangedListener;
/*     */ import org.eclipse.jface.viewers.SelectionChangedEvent;
/*     */ import org.eclipse.jface.viewers.TreeViewer;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Tree;

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
/*     */
/*     */
/*     */ public class InteractiveTreeView
        /*     */ extends AbstractUnitFragment<IUnit>
        /*     */ {
    /*  51 */   private static final ILogger logger = GlobalLog.getLogger(InteractiveTreeView.class);
    /*     */
    /*     */   private ITreeDocument idoc;
    /*     */   private InteractiveTreeViewer iviewer;

    /*     */
    /*     */
    public InteractiveTreeView(Composite parent, int flags, final RcpClientContext context, final IUnit unit, ITreeDocument idoc, IRcpUnitView unitView)
    /*     */ {
        /*  58 */
        super(parent, flags, unit, unitView, context);
        /*  59 */
        setLayout(new FillLayout());
        /*     */
        /*  61 */
        this.idoc = idoc;
        /*     */
        /*  63 */
        this.iviewer = new InteractiveTreeViewer(this, 0, idoc, context.getPropertyManager());
        /*  64 */
        this.iviewer.setStyleAdapter(new ItemStyleProvider(context.getStyleManager()));
        /*  65 */
        this.iviewer.initialize();
        /*     */
        /*  67 */
        addDisposeListener(new DisposeListener()
                /*     */ {
            /*     */
            public void widgetDisposed(DisposeEvent e) {
                /*  70 */
                InteractiveTreeView.this.iviewer.dispose();
                /*     */
            }
            /*     */
            /*  73 */
        });
        /*  74 */
        setPrimaryWidget(this.iviewer.getTreeWidget());
        /*     */
        /*     */
        /*  77 */
        this.iviewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener()
                /*     */ {
            /*     */
            public void selectionChanged(SelectionChangedEvent event)
            /*     */ {
                /*  81 */
                context.refreshHandlersStates();
                /*     */
                /*  83 */
                NodeCoordinates coord = InteractiveTreeView.this.iviewer.getPosition();
                /*  84 */
                String address = null;
                /*  85 */
                IInputLocation location = null;
                /*  86 */
                if (coord != null) {
                    /*  87 */
                    address = InteractiveTreeView.this.getAddressAt(coord);
                    /*  88 */
                    if ((address != null) && ((unit instanceof IInteractiveUnit))) {
                        /*  89 */
                        IInteractiveUnit iunit = (IInteractiveUnit) unit;
                        /*  90 */
                        location = iunit.addressToLocation(address);
                        /*     */
                    }
                    /*     */
                }
                /*  93 */
                String statusText = String.format("coord: %s | addr: %s | loc: %s", new Object[]{Strings.safe(coord, "?"),
/*  94 */           Strings.safe(address, "?"), Strings.safe(location, "?")});
                /*  95 */
                context.getStatusIndicator().setText(statusText);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public boolean isActiveItem(IItem item)
    /*     */ {
        /* 102 */
        return (item != null) && (getActiveItem() == item);
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 107 */
        return this.iviewer.getSelectedNode();
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 112 */
        INodeCoordinates coord = this.iviewer.getPosition();
        /* 113 */
        return getAddressAt(coord);
        /*     */
    }

    /*     */
    /*     */
    public String getAddressAt(INodeCoordinates coord) {
        /* 117 */
        if (coord == null) {
            /* 118 */
            return null;
            /*     */
        }
        /* 120 */
        if ((this.iviewer.getFilteredTreeViewer().isFiltered()) || (this.iviewer.getTreeWidget().getSortColumn() != null))
            /*     */ {
            /*     */
            /*     */
            /* 124 */
            return null;
            /*     */
        }
        /* 126 */
        return this.idoc.coordinatesToAddress(coord);
        /*     */
    }

    /*     */
    /*     */
    public boolean isValidActiveAddress(String address, Object object)
    /*     */ {
        /*     */
        try {
            /* 132 */
            INodeCoordinates coord = this.idoc.addressToCoordinates(address);
            /* 133 */
            return coord != null;
            /*     */
        }
        /*     */ catch (Exception e) {
            /* 136 */
            logger.catching(e);
        }
        /* 137 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public boolean setActiveAddress(String address, Object extra, boolean record)
    /*     */ {
        /*     */
        try
            /*     */ {
            /* 146 */
            coord = this.idoc.addressToCoordinates(address);
            /*     */
        } catch (Exception e) {
            /*     */
            INodeCoordinates coord;
            /* 149 */
            logger.catching(e);
            /* 150 */
            return false;
            /*     */
        }
        /*     */
        INodeCoordinates coord;
        /* 153 */
        if (coord == null) {
            /* 154 */
            return false;
            /*     */
        }
        /*     */
        /* 157 */
        this.iviewer.setPosition(coord, true);
        /* 158 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 163 */
        if (!this.iviewer.getTreeWidget().isFocusControl()) {
            /* 164 */
            return false;
            /*     */
        }
        /* 166 */
        if (this.iviewer.verifyOperation(req)) {
            /* 167 */
            return false;
            /*     */
        }
        /*     */
        /* 170 */
        switch (req.getOperation()) {
            /*     */
            case JUMP_TO:
                /* 172 */
                return true;
            /*     */
            /*     */
            case ITEM_FOLLOW:
                /* 175 */
                return ((getActiveItem() instanceof IActionableItem)) && (!isMaster((IActionableItem) getActiveItem()));
            /*     */
        }
        /* 177 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 183 */
        if (this.iviewer.doOperation(req)) {
            /* 184 */
            return true;
            /*     */
        }
        /* 186 */
        if (!req.proceed()) {
            /* 187 */
            return false;
            /*     */
        }
        /* 189 */
        switch (req.getOperation()) {
            /*     */
            case JUMP_TO:
                /* 191 */
                JumpToDialog dlg = new JumpToDialog(getShell(), RcpClientContext.getStandardAddressHistory(getContext()));
                /* 192 */
                String address = dlg.open();
                /* 193 */
                if (address != null) {
                    /* 194 */
                    return setActiveAddress(address);
                    /*     */
                }
                /* 196 */
                return false;
            /*     */
            /*     */
            case ITEM_FOLLOW:
                /* 199 */
                IItem item = getActiveItem();
                /* 200 */
                if (!(item instanceof IActionableItem)) {
                    /* 201 */
                    return false;
                    /*     */
                }
                /* 203 */
                logger.debug("Following item: %s", new Object[]{item});
                /* 204 */
                return followItem((IActionableItem) item);
            /*     */
        }
        /*     */
        /* 207 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private boolean followItem(IActionableItem item)
    /*     */ {
        /* 212 */
        logger.debug("Following item: %s", new Object[]{item});
        /* 213 */
        if (item == null) {
            /* 214 */
            return false;
            /*     */
        }
        /* 216 */
        List<? extends INode> roots = this.iviewer.getInfiniDocument().getRoots();
        /* 217 */
        return followItem(item.getItemId(), roots, new ArrayList());
        /*     */
    }

    /*     */
    /*     */
    private boolean followItem(long itemId, List<? extends INode> nodes, List<Integer> coordinates) {
        /* 221 */
        int index = 0;
        /* 222 */
        for (INode root : nodes) {
            /* 223 */
            if ((root instanceof IActionableItem)) {
                /* 224 */
                IActionableItem actionableRoot = (IActionableItem) root;
                /* 225 */
                if ((actionableRoot.getItemId() == itemId) &&
                        /* 226 */           (isMaster(actionableRoot))) {
                    /* 227 */
                    return this.iviewer.setPosition(new NodeCoordinates(Arrays.asList(new Integer[]{Integer.valueOf(index)})), true);
                    /*     */
                }
                /*     */
            }
            /*     */
            /* 231 */
            List<Integer> children = new ArrayList(coordinates);
            /* 232 */
            children.add(Integer.valueOf(index));
            /* 233 */
            boolean followChild = followItem(itemId, root.getChildren(), children);
            /* 234 */
            if (followChild) {
                /* 235 */
                return followChild;
                /*     */
            }
            /* 237 */
            index++;
            /*     */
        }
        /* 239 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private boolean isMaster(IActionableItem item) {
        /* 243 */
        return (item.getItemFlags() & 0x1) != 0;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 250 */
        return Strings.encodeUTF8(this.iviewer.exportToString());
        /*     */
    }

    /*     */
    /*     */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*     */ {
        /* 255 */
        return AbstractUnitFragment.FragmentType.TREE;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\InteractiveTreeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */