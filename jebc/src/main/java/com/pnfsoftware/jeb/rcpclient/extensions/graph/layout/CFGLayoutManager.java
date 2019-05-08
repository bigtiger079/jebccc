package com.pnfsoftware.jeb.rcpclient.extensions.graph.layout;

import com.pnfsoftware.jeb.core.units.code.IInstruction;

public class CFGLayoutManager {
    public static <T extends IInstruction> ICFGLayout<T> createDefault() {
        return createDefault(false);
    }

    public static <T extends IInstruction> ICFGLayout<T> createDefault(boolean disregardIrregularFlows) {
        return new CFGLayout(disregardIrregularFlows);
    }
}


