/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*    */
/*    */

import com.pnfsoftware.jeb.core.input.IInputLocation;
/*    */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*    */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*    */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*    */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*    */ import com.pnfsoftware.jeb.core.output.text.impl.HexDumpDocument;
/*    */ import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
/*    */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*    */ import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;

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
/*    */ public class TextDocumentLocationGenerator
        /*    */ {
    /*    */   private IUnit unit;
    /*    */   private ITextDocumentViewer viewer;
    /*    */   private ITextDocumentPart lastKnownPart;
    /*    */   private ICoordinates lastKnownCoordinates;
    /*    */   private String lastKnownAddress;

    /*    */
    /*    */
    public TextDocumentLocationGenerator(IUnit unit, ITextDocumentViewer viewer)
    /*    */ {
        /* 35 */
        this.viewer = viewer;
        /*    */
    }

    /*    */
    /*    */
    public String generateStatus(ICoordinates coord)
    /*    */ {
        /* 40 */
        String address = null;
        /* 41 */
        IInputLocation location = null;
        /* 42 */
        if (coord != null) {
            /* 43 */
            address = getAddress(coord);
            /* 44 */
            if ((address != null) && ((this.unit instanceof IInteractiveUnit)))
                /*    */ {
                /*    */
                /* 47 */
                if (!(this.viewer.getDocument() instanceof HexDumpDocument)) {
                    /* 48 */
                    IInteractiveUnit iunit = (IInteractiveUnit) this.unit;
                    /* 49 */
                    location = iunit.addressToLocation(address);
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /*    */
        /* 54 */
        String address2 = "";
        /* 55 */
        if ((address != null) && ((this.unit instanceof INativeCodeUnit))) {
            /* 56 */
            INativeCodeUnit<?> pbcu = (INativeCodeUnit) this.unit;
            /* 57 */
            long a = pbcu.getCanonicalMemoryAddress(address);
            /* 58 */
            if (a != -1L) {
                /* 59 */
                String ss = pbcu.getSymbolicStringAddress(a, 2);
                /* 60 */
                if (ss != null) {
                    /* 61 */
                    address2 = String.format(" (%s)", new Object[]{ss});
                    /*    */
                }
                /*    */
            }
            /*    */
        }
        /*    */
        /* 66 */
        String statusText = String.format("coord: %s | addr: %s%s | loc: %s", new Object[]{Strings.safe(coord, "?"),
/* 67 */       Strings.safe(address, "?"), address2, Strings.safe(location, "?")});
        /*    */
        /*    */
        /*    */
        /* 71 */
        return statusText;
        /*    */
    }

    /*    */
    /*    */
    public String getAddress(ICoordinates coord) {
        /* 75 */
        if (coord == null) {
            /* 76 */
            return null;
            /*    */
        }
        /* 78 */
        ITextDocumentPart part = this.viewer.getCurrentDocumentPart();
        /* 79 */
        if ((this.lastKnownPart != part) || (!coord.equals(this.lastKnownCoordinates)))
            /*    */ {
            /* 81 */
            this.lastKnownPart = part;
            /* 82 */
            this.lastKnownCoordinates = coord;
            /* 83 */
            this.lastKnownAddress = this.viewer.getDocument().coordinatesToAddress(coord, AddressConversionPrecision.FINE);
            /*    */
        }
        /* 85 */
        return this.lastKnownAddress;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\TextDocumentLocationGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */