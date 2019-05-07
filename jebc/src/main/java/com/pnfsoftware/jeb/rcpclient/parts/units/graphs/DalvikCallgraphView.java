/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.android.IDexUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import org.eclipse.swt.widgets.Composite;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class DalvikCallgraphView
        /*    */ extends AbstractGlobalGraphView<IDexUnit>
        /*    */ {
    /* 19 */   private static final ILogger logger = GlobalLog.getLogger(DalvikCallgraphView.class);

    /*    */
    /*    */
    /*    */
    /*    */
    public DalvikCallgraphView(Composite parent, int style, RcpClientContext context, IDexUnit unit, IRcpUnitView unitView)
    /*    */ {
        /* 25 */
        super(parent, style, unit, unitView, context);
        /*    */
        /* 27 */
        this.callgraphBuilder = new DalvikCallgraphBuilder(unit);
        /*    */
    }

    /*    */
    /*    */
    public boolean preFirstBuild()
    /*    */ {
        /* 32 */
        DalvikCallgraphBuilder b = (DalvikCallgraphBuilder) this.callgraphBuilder;
        /*    */
        /* 34 */
        String initialFilter = b.getFilter();
        /* 35 */
        if (initialFilter == null)
            /*    */ {
            /* 37 */
            initialFilter = "# include all packages by default\n*\n# blacklisted packages and sub-packages\n-android*\n-com.android*\n-com.google*\n";
            /*    */
        }
        /*    */
        /*    */
        /*    */
        /*    */
        /*    */
        /*    */
        /*    */
        /*    */
        /*    */
        /* 48 */
        CallgraphPackageFilterDialog dlg = new CallgraphPackageFilterDialog(getShell());
        /* 49 */
        dlg.setInitialPackageList(initialFilter);
        /* 50 */
        String filter = dlg.open();
        /* 51 */
        if (filter == null) {
            /* 52 */
            return false;
            /*    */
        }
        /*    */
        /* 55 */
        b.setFilter(filter);
        /* 56 */
        return true;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\DalvikCallgraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */