
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


public abstract class JebBaseHandler
        extends JebAction {
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
        // Byte code:
        //   0: aload_0
        //   1: getfield 27	com/pnfsoftware/jeb/rcpclient/handlers/JebBaseHandler:executing	Z
        //   4: ifeq +4 -> 8
        //   7: return
        //   8: aload_0
        //   9: iconst_1
        //   10: putfield 27	com/pnfsoftware/jeb/rcpclient/handlers/JebBaseHandler:executing	Z
        //   13: aload_0
        //   14: invokevirtual 41	com/pnfsoftware/jeb/rcpclient/handlers/JebBaseHandler:initialize	()V
        //   17: aload_0
        //   18: invokevirtual 38	com/pnfsoftware/jeb/rcpclient/handlers/JebBaseHandler:execute	()V
        //   21: aload_0
        //   22: iconst_0
        //   23: putfield 27	com/pnfsoftware/jeb/rcpclient/handlers/JebBaseHandler:executing	Z
        //   26: goto +11 -> 37
        //   29: astore_1
        //   30: aload_0
        //   31: iconst_0
        //   32: putfield 27	com/pnfsoftware/jeb/rcpclient/handlers/JebBaseHandler:executing	Z
        //   35: aload_1
        //   36: athrow
        //   37: return
        // Line number table:
        //   Java source line #98	-> byte code offset #0
        //   Java source line #100	-> byte code offset #7
        //   Java source line #103	-> byte code offset #8
        //   Java source line #105	-> byte code offset #13
        //   Java source line #106	-> byte code offset #17
        //   Java source line #109	-> byte code offset #21
        //   Java source line #110	-> byte code offset #26
        //   Java source line #109	-> byte code offset #29
        //   Java source line #110	-> byte code offset #35
        //   Java source line #111	-> byte code offset #37
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	38	0	this	JebBaseHandler
        //   29	7	1	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   13	21	29	finally

    }


    public abstract boolean canExecute();


    public abstract void execute();


    public boolean isDisableHandlers(IMPart part) {

        Control ctl = this.context.getDisplay().getFocusControl();

        if ((((ctl instanceof Text)) && (((Text) ctl).getEditable())) || (((ctl instanceof StyledText)) &&
                (((StyledText) ctl).getEditable()))) {

            return true;

        }


        if (FilterText.isSelected()) {

            return true;

        }


        AbstractUnitFragment<?> fragment = getActiveFragment(part);

        if ((!(fragment instanceof InteractiveTextView)) && (!(fragment instanceof InteractiveTableView)) && (!(fragment instanceof InteractiveTreeView)) && (!(fragment instanceof CodeHierarchyView)) && (!(fragment instanceof AbstractLocalGraphView))) {


            return true;

        }


        return false;

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

        long itemId = (item instanceof IActionableItem) ? ((IActionableItem) item).getItemId() : 0L;

        return itemId;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\JebBaseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */