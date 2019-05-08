
package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
import com.pnfsoftware.jeb.core.output.text.impl.StaticTextDocument;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.core.units.code.asm.cfg.BasicBlock;
import com.pnfsoftware.jeb.util.encoding.Conversion;

public class StaticCodeTextDocument
        extends StaticTextDocument {
    private BasicBlock<IInstruction> b;
    private String methodName;

    public StaticCodeTextDocument(IUnit unit, BasicBlock<IInstruction> b, String text) {
        super(text);
        this.b = b;
        if ((unit instanceof ISourceUnit)) {
            this.methodName = (((ISourceUnit) unit).getFullyQualifiedName() + ":");
        } else {
            this.methodName = "unk:";
        }
    }

    public String coordinatesToAddress(ICoordinates coordinates, AddressConversionPrecision precision) {
        return this.methodName + Long.toHexString(this.b.getAddressOfInstruction((int) coordinates.getAnchorId())) + "h";
    }

    public ICoordinates addressToCoordinates(String address) {
        if (address.length() < this.methodName.length()) {
            return super.addressToCoordinates(address);
        }
        address = address.substring(this.methodName.length());
        long addr = Conversion.toLong(address, -1L);
        if (addr < 0L) {
            return super.addressToCoordinates(address);
        }
        return new Coordinates(this.b.getIndexOfInstruction(addr));
    }
}


