package com.pnfsoftware.jeb.rcpclient.handlers;

import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilterText;
import com.pnfsoftware.jeb.rcpclient.operations.JebAction;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTableView;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTreeView;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;
import com.pnfsoftware.jeb.rcpclient.parts.units.graphs.AbstractLocalGraphView;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class JebBaseHandler extends JebAction {
    private static final ILogger logger = GlobalLog.getLogger(JebBaseHandler.class);
    protected RcpClientContext context;
    protected Shell shell;
    protected IMPart part;
    private boolean executing;

    public JebBaseHandler(String id, String name, String tooltip, String icon) {
        this(id, name, 0, tooltip, icon, 0);
    }

    public JebBaseHandler(String id, String name, int style, String tooltip, String icon, int accelerator) {
        super(id, name, style, tooltip, icon, accelerator);
        initialize();
    }

    protected void initialize() {
        if (this.executing) {
            return;
        }
        this.context = RcpClientContext.getInstance();
        if (this.context != null) {
            this.shell = this.context.getActiveShell();
            this.part = this.context.getPartManager().getActivePart();
        }
    }

    public boolean isEnabled() {
        if (this.executing) {
            return true;
        }
        initialize();
        return canExecute();
    }

    public void run() {
        if (!this.executing) {
            this.executing = true;
            try {
                initialize();
                execute();
            } finally {
                this.executing = false;
            }
        }
    }

    public abstract boolean canExecute();

    public abstract void execute();

    public boolean isDisableHandlers(IMPart part) {
        Control ctl = this.context.getDisplay().getFocusControl();
        if ((((ctl instanceof Text)) && (((Text) ctl).getEditable())) || (((ctl instanceof StyledText)) && (((StyledText) ctl).getEditable()))) {
            return true;
        }
        if (FilterText.isSelected()) {
            return true;
        }
        AbstractUnitFragment<?> fragment = getActiveFragment(part);
        return (!(fragment instanceof InteractiveTextView)) && (!(fragment instanceof InteractiveTableView)) && (!(fragment instanceof InteractiveTreeView)) && (!(fragment instanceof CodeHierarchyView)) && (!(fragment instanceof AbstractLocalGraphView));
    }

    public static AbstractUnitFragment<?> getActiveFragment(IMPart part) {
        Object object = part == null ? null : part.getManager();
        return !(object instanceof UnitPartManager) ? null : ((UnitPartManager) object).getActiveFragment();
    }

    public static IUnit getActiveUnit(IMPart part) {
        Object object = part == null ? null : part.getManager();
        return !(object instanceof UnitPartManager) ? null : ((UnitPartManager) object).getUnit();
    }

    public static String getActiveAddress(IMPart part) {
        Object object = part == null ? null : part.getManager();
        return !(object instanceof UnitPartManager) ? null : ((UnitPartManager) object).getActiveAddress();
    }

    public static IItem getActiveItem(IMPart part) {
        Object object = part == null ? null : part.getManager();
        return !(object instanceof UnitPartManager) ? null : ((UnitPartManager) object).getActiveItem();
    }

    public static long getActiveItemId(IMPart part) {
        IItem item = getActiveItem(part);
        if (item == null) {
            return 0L;
        }
        return (item instanceof IActionableItem) ? ((IActionableItem) item).getItemId() : 0L;
    }
}


