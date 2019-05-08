package com.pnfsoftware.jeb.rcpclient.extensions.graph.layout;

import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CFGLayout<T extends IInstruction> implements ICFGLayout<T> {
    private static final ILogger logger = GlobalLog.getLogger(CFGLayout.class);
    private boolean disregardIrregularFlows;
    private CFG<T> cfg;
    private Spreadsheet<BasicBlock<T>> grid;
    private Set<Long> processed;
    private boolean specialHandlingForDiamonds = true;
    private Set<Long> skipDstProc = new HashSet();
    private Set<Long> diamondExits = new HashSet();

    public CFGLayout(boolean disregardIrregularFlows) {
        this.disregardIrregularFlows = disregardIrregularFlows;
    }

    public void setDisregardIrregularFlows(boolean disregardIrregularFlows) {
        this.disregardIrregularFlows = disregardIrregularFlows;
    }

    public boolean isDisregardIrregularFlows() {
        return this.disregardIrregularFlows;
    }

    public Spreadsheet<BasicBlock<T>> build(CFG<T> cfg) {
        if (this.grid != null) {
            throw new IllegalStateException();
        }
        if ((cfg == null) || (cfg.size() == 0)) {
            throw new IllegalArgumentException("Illegal CFG");
        }
        this.cfg = cfg;
        this.grid = new Spreadsheet();
        this.processed = new HashSet();
        process(cfg.getEntryBlock(), 0, 0);
        logger.i("Layout:\n%s", new Object[]{this.grid});
        return this.grid;
    }

    private boolean process(BasicBlock<T> b, int row, int col) {
        long base = b.getFirstAddress();
        if (this.processed.contains(Long.valueOf(base))) {
            return false;
        }
        this.processed.add(Long.valueOf(base));
        Cell<BasicBlock<T>> cell0 = this.grid.writeCell(row, col, b);
        Assert.a(cell0.isPrimary());
        if (this.skipDstProc.contains(Long.valueOf(base))) {
            return true;
        }
        if (this.diamondExits.contains(Long.valueOf(base))) {
            this.grid.mergeCells(row, col, 2, 1);
        }
        if (cell0.isPartOfMergedCell()) {
            col += cell0.getHorizontalSpan() - 1;
        }
        Cell<BasicBlock<T>> c1 = this.grid.writeCell(row + 1, col, null);
        Assert.a(c1.isPrimary());
        List<BasicBlock<T>> dstlist = getDests(b);
        BasicBlock<T> bB;
        if ((this.specialHandlingForDiamonds) && (dstlist.size() == 2)) {
            BasicBlock<T> bA = (BasicBlock) dstlist.get(0);
            bB = (BasicBlock) dstlist.get(1);
            if ((!this.processed.contains(Long.valueOf(bA.getFirstAddress()))) && (!this.processed.contains(Long.valueOf(bB.getFirstAddress())))) {
                List<BasicBlock<T>> d_bA = getDests(bA);
                if ((d_bA.size() == 1) && (d_bA.equals(getDests(bB))) && (!this.processed.contains(Long.valueOf(((BasicBlock) d_bA.get(0)).getFirstAddress())))) {
                    this.skipDstProc.add(Long.valueOf(bB.getFirstAddress()));
                    this.diamondExits.add(Long.valueOf(((BasicBlock) d_bA.get(0)).getFirstAddress()));
                }
            }
        }
        int splitcount = 0;
        for (BasicBlock<T> b2 : dstlist) {
            if (!this.processed.contains(Long.valueOf(b2.getFirstAddress()))) {
                splitcount++;
            }
        }
        while (splitcount-- >= 2) {
            c1 = this.grid.splitCell(c1, true);
        }
        this.grid.clearNullCells(true);
        int col2 = col;
        int i = 0;
        for (BasicBlock<T> b2 : dstlist) {
            boolean wroteCell = process(b2, row + 1, col2);
            i++;
            if (i >= dstlist.size()) {
                break;
            }
            if (wroteCell) {
                col2 = this.grid.getCell(row + 1, col2, true).getNextColumn();
            }
        }
        return true;
    }

    private List<BasicBlock<T>> getDests(BasicBlock<T> b) {
        return this.disregardIrregularFlows ? b.getOutputBlocks() : b.getAllOutputBlocks();
    }
}


