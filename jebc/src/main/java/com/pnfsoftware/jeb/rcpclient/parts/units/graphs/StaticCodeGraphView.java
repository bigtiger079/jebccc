package com.pnfsoftware.jeb.rcpclient.parts.units.graphs;

import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitView;
import com.pnfsoftware.jeb.rcpclient.parts.units.StaticCodeTextDocument;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.widgets.Composite;

public class StaticCodeGraphView extends AbstractControlFlowGraphView<IUnit> {
    private static final ILogger logger = GlobalLog.getLogger(StaticCodeGraphView.class);
    private CFG<?> cfg;

    public StaticCodeGraphView(Composite parent, int style, RcpClientContext context, IUnit unit, IRcpUnitView unitView, CFG<?> initialCfg) {
        super(parent, style, unit, unitView, context);
        this.cfg = initialCfg;
    }

    public boolean setFocus() {
        setCfg(this.cfg);
        return this.gp.setFocus();
    }

    public boolean setActiveAddress(String address, Object extraAddressDetails, boolean recordPosition) {
        logger.warn("This graph does not support fine-grained addressing and positioning");
        return false;
    }

    protected boolean doItemFollow() {
        return false;
    }

    protected ITextDocument getTextForBlock(BasicBlock<IInstruction> b) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (IInstruction insn : b) {
            if (i >= 1) {
                sb.append("\n");
            }
            sb.append(String.format("%s", insn.format(null)));
            i++;
        }
        String text = sb.toString();
        return new StaticCodeTextDocument(getUnit(), b, text);
    }

    public void setCfg(CFG<?> cfg) {
        reset(true);
        this.cfg = cfg;
        if (cfg != null) {
            generateGraphForCFG((CFG<IInstruction>) cfg);
        }
    }

    public CFG<?> getCfg() {
        return this.cfg;
    }
}


