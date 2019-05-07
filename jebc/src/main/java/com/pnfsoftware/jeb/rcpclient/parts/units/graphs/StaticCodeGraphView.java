/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*    */ import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
/*    */ import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.graph.GraphPlaceholder;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.units.StaticCodeTextDocument;
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
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class StaticCodeGraphView
        /*    */ extends AbstractControlFlowGraphView<IUnit>
        /*    */ {
    /* 30 */   private static final ILogger logger = GlobalLog.getLogger(StaticCodeGraphView.class);
    /*    */
    /*    */   private CFG<?> cfg;

    /*    */
    /*    */
    public StaticCodeGraphView(Composite parent, int style, RcpClientContext context, IUnit unit, IRcpUnitView unitView, CFG<?> initialCfg)
    /*    */ {
        /* 36 */
        super(parent, style, unit, unitView, context);
        /*    */
        /*    */
        /*    */
        /* 40 */
        this.cfg = initialCfg;
        /*    */
    }

    /*    */
    /*    */
    public boolean setFocus()
    /*    */ {
        /* 45 */
        setCfg(this.cfg);
        /* 46 */
        return this.gp.setFocus();
        /*    */
    }

    /*    */
    /*    */
    public boolean setActiveAddress(String address, Object extraAddressDetails, boolean recordPosition)
    /*    */ {
        /* 51 */
        logger.warn("This graph does not support fine-grained addressing and positioning", new Object[0]);
        /* 52 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    protected boolean doItemFollow()
    /*    */ {
        /* 57 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    protected ITextDocument getTextForBlock(BasicBlock<IInstruction> b)
    /*    */ {
        /* 63 */
        StringBuilder sb = new StringBuilder();
        /* 64 */
        int i = 0;
        /* 65 */
        for (IInstruction insn : b) {
            /* 66 */
            if (i >= 1) {
                /* 67 */
                sb.append("\n");
                /*    */
            }
            /* 69 */
            sb.append(String.format("%s", new Object[]{insn.format(null)}));
            /* 70 */
            i++;
            /*    */
        }
        /* 72 */
        String text = sb.toString();
        /* 73 */
        return new StaticCodeTextDocument(getUnit(), b, text);
        /*    */
    }

    /*    */
    /*    */
    public void setCfg(CFG<?> cfg)
    /*    */ {
        /* 78 */
        reset(true);
        /*    */
        /* 80 */
        this.cfg = cfg;
        /*    */
        /* 82 */
        if (cfg != null) {
            /* 83 */
            generateGraphForCFG(cfg);
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    public CFG<?> getCfg() {
        /* 88 */
        return this.cfg;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\graphs\StaticCodeGraphView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */