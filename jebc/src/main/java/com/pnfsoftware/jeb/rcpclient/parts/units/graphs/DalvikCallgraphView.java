
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.core.units.code.android.IDexUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.widgets.Composite;

public class DalvikCallgraphView extends AbstractGlobalGraphView<IDexUnit> {
    private static final ILogger logger = GlobalLog.getLogger(DalvikCallgraphView.class);
    private DalvikCallgraphBuilder callgraphBuilder;

    public DalvikCallgraphView(Composite parent, int style, RcpClientContext context, IDexUnit unit, IRcpUnitView unitView) {
        super(parent, style, unit, unitView, context);
        this.callgraphBuilder = new DalvikCallgraphBuilder(unit);
    }

    public boolean preFirstBuild() {
        DalvikCallgraphBuilder b = (DalvikCallgraphBuilder) this.callgraphBuilder;
        String initialFilter = b.getFilter();
        if (initialFilter == null) {
            initialFilter = "# include all packages by default\n*\n# blacklisted packages and sub-packages\n-android*\n-com.android*\n-com.google*\n";
        }
        CallgraphPackageFilterDialog dlg = new CallgraphPackageFilterDialog(getShell());
        dlg.setInitialPackageList(initialFilter);
        String filter = dlg.open();
        if (filter == null) {
            return false;
        }
        b.setFilter(filter);
        return true;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\DalvikCallgraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */