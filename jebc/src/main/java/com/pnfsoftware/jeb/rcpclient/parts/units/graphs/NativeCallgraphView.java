/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
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
/*    */ public class NativeCallgraphView
        /*    */ extends AbstractGlobalGraphView<INativeCodeUnit<IInstruction>>
        /*    */ {
    /* 20 */   private static final ILogger logger = GlobalLog.getLogger(NativeCallgraphView.class);

    /*    */
    /*    */
    /*    */
    /*    */
    public NativeCallgraphView(Composite parent, int style, RcpClientContext context, INativeCodeUnit<IInstruction> unit, IRcpUnitView unitView)
    /*    */ {
        /* 26 */
        super(parent, style, unit, unitView, context);
        /*    */
        /* 28 */
        this.callgraphBuilder = new NativeCallgraphBuilder(unit);
        /*    */
    }

    /*    */
    /*    */
    public boolean preFirstBuild()
    /*    */ {
        /* 33 */
        return true;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\NativeCallgraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */