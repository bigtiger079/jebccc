package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.core.input.IInputLocation;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.impl.HexDumpDocument;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.util.format.Strings;

public class TextDocumentLocationGenerator {
    private IUnit unit;
    private ITextDocumentViewer viewer;
    private ITextDocumentPart lastKnownPart;
    private ICoordinates lastKnownCoordinates;
    private String lastKnownAddress;

    public TextDocumentLocationGenerator(IUnit unit, ITextDocumentViewer viewer) {
        this.viewer = viewer;
    }

    public String generateStatus(ICoordinates coord) {
        String address = null;
        IInputLocation location = null;
        if (coord != null) {
            address = getAddress(coord);
            if ((address != null) && ((this.unit instanceof IInteractiveUnit))) {
                if (!(this.viewer.getDocument() instanceof HexDumpDocument)) {
                    IInteractiveUnit iunit = (IInteractiveUnit) this.unit;
                    location = iunit.addressToLocation(address);
                }
            }
        }
        String address2 = "";
        if ((address != null) && ((this.unit instanceof INativeCodeUnit))) {
            INativeCodeUnit<?> pbcu = (INativeCodeUnit) this.unit;
            long a = pbcu.getCanonicalMemoryAddress(address);
            if (a != -1L) {
                String ss = pbcu.getSymbolicStringAddress(a, 2);
                if (ss != null) {
                    address2 = String.format(" (%s)", new Object[]{ss});
                }
            }
        }
        String statusText = String.format("coord: %s | addr: %s%s | loc: %s", new Object[]{Strings.safe(coord, "?"), Strings.safe(address, "?"), address2, Strings.safe(location, "?")});
        return statusText;
    }

    public String getAddress(ICoordinates coord) {
        if (coord == null) {
            return null;
        }
        ITextDocumentPart part = this.viewer.getCurrentDocumentPart();
        if ((this.lastKnownPart != part) || (!coord.equals(this.lastKnownCoordinates))) {
            this.lastKnownPart = part;
            this.lastKnownCoordinates = coord;
            this.lastKnownAddress = this.viewer.getDocument().coordinatesToAddress(coord, AddressConversionPrecision.FINE);
        }
        return this.lastKnownAddress;
    }
}


