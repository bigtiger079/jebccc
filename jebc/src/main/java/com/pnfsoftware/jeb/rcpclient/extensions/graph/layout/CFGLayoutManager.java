
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\layout\CFGLayoutManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */