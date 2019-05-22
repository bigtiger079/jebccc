package com.pnfsoftware.jeb.rcpclient.extensions.graph.layout;

import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Deprecated
class CFGLayoutExp1<T extends IInstruction> implements ICFGLayout<T> {
    private static final ILogger logger = GlobalLog.getLogger(CFGLayoutExp1.class);
    CFG<T> cfg;
    Spreadsheet<BasicBlock<T>> grid;
    LinkedHashMap<Long, BasicBlock<T>> blockmap;

    public Spreadsheet<BasicBlock<T>> build(CFG<T> cfg) {
        if (this.grid != null) {
            return this.grid;
        }
        if ((cfg == null) || (cfg.size() == 0)) {
            throw new IllegalArgumentException("Illegal CFG");
        }
        this.cfg = cfg;
        this.grid = new Spreadsheet();
        this.blockmap = new LinkedHashMap();
        for (BasicBlock<T> b : cfg) {
            this.blockmap.put(b.getFirstAddress(), b);
        }
        long addr = (Long) this.blockmap.keySet().iterator().next();
        BasicBlock<T> b = this.blockmap.remove(addr);
        Cell<BasicBlock<T>> cell0 = this.grid.writeCell(0, 0, b);
        List<Cell<BasicBlock<T>>> parents = new ArrayList<>();
        parents.add(cell0);
        addChildren(parents);
        improveLayoutMultiPass();
        return this.grid;
    }

    private int improveLayoutMultiPass() {
        for (Cell<BasicBlock<T>> cell : this.grid.getRealCells()) {
            logger.i("- Cell %s", cell);
        }
        int ipass = 0;
        int totalChanges = 0;
        for (; ; ) {
            ipass++;
            int changes = 0;
            for (int row = 0; row < this.grid.getRowCount(); row++) {
                List<Cell<BasicBlock<T>>> rowcells = this.grid.getRealCellsOnRow(row);
                int cnt = rowcells.size();
                for (int j = cnt - 1; j >= 0; j--) {
                    Cell<BasicBlock<T>> cell = rowcells.get(j);
                    RowCol srcCoord = cell.getCoordinates();
                    BasicBlock<T> bb = cell.getObject();
                    List<Cell<BasicBlock<T>>> dstCells = getDestinationCells(bb);
                    int[] limits = findLimits(dstCells, row + 1);
                    int hspan = limits[1] - limits[0];
                    if (hspan != 0) {
                        if (srcCoord.getColumn() <= limits[0]) {
                            if (srcCoord.getColumn() < limits[0]) {
                                if (this.grid.isFree(srcCoord.getRow(), limits[0])) {
                                    cell = this.grid.moveCell(cell, srcCoord.getRow(), limits[0], false);
                                }
                            } else if (hspan != cell.getHorizontalSpan()) {
                                if (this.grid.isRangeFree(cell.getRow(), cell.getNextColumn(), limits[1])) {
                                    logger.i("Expanding cell %s, new hspan=%d", cell, hspan);
                                    cell = this.grid.mergeCells(cell.getRow(), cell.getColumn(), hspan, 1);
                                    changes++;
                                }
                            }
                        }
                    }
                }
            }
            if (changes == 0) {
                break;
            }
            totalChanges += changes;
            logger.i("Layouting Pass %d", ipass);
        }
        return totalChanges;
    }

    private int[] findLimits(List<Cell<BasicBlock<T>>> dstCells, int wantedRow) {
        int min = 0;
        int max = 0;
        for (Cell<BasicBlock<T>> cell : dstCells) {
            if (cell.getRow() == wantedRow) {
                int col = cell.getColumn();
                if (col < min) {
                    min = col;
                }
                col = cell.getNextColumn();
                if (col > max) {
                    max = col;
                }
            }
        }
        return new int[]{min, max};
    }

    private void addChildren(List<Cell<BasicBlock<T>>> parents) {
        while (!parents.isEmpty()) {
            List<Cell<BasicBlock<T>>> nextParents = new ArrayList<>();
            for (Cell<BasicBlock<T>> parent : parents) {
                int srcRow = parent.getCoordinates().getRow();
                int srcCol = parent.getCoordinates().getColumn();
                List<BasicBlock<T>> dstlist = ((BasicBlock) parent.getObject()).getOutputBlocks();
                for (BasicBlock<T> dst : dstlist) {
                    long addr = dst.getFirstAddress();
                    if (this.blockmap.containsKey(addr)) {
                        this.blockmap.remove(addr);
                        Cell<BasicBlock<T>> cell = this.grid.createFirstAvailableOnRow(srcRow + 1, srcCol);
                        cell.setObject(dst);
                        nextParents.add(cell);
                    }
                }
            }
            int srcRow;
            int srcCol;
            parents = nextParents;
        }
    }

    private List<Cell<BasicBlock<T>>> getDestinationCells(BasicBlock<T> src) {
        List<Cell<BasicBlock<T>>> r = new ArrayList<>();
        for (BasicBlock<T> dst : src.getOutputBlocks()) {
            Cell<BasicBlock<T>> cell = this.grid.getCellByObject(dst);
            Assert.a(cell != null, "Cannot find cell for block: " + dst);
            r.add(cell);
        }
        return r;
    }
}


