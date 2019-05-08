package com.pnfsoftware.jeb.rcpclient.extensions.graph.layout;

import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.CFG;

public abstract interface ICFGLayout<T extends IInstruction> {
    public abstract Spreadsheet<BasicBlock<T>> build(CFG<T> paramCFG);
}


