
package com.pnfsoftware.jeb.rcpclient.parts.units.codeobject;

import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.core.output.table.IVisualCell;
import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
import com.pnfsoftware.jeb.core.units.codeobject.ILoaderInformation;

public class CodeLoaderCellItem
        implements IVisualCell {
    private ICodeObjectUnit unit;
    private String name;
    private long offset;
    private boolean relative;

    public CodeLoaderCellItem(ICodeObjectUnit unit, String name, String offset) {
        this(unit, name, offset, true);
    }

    public CodeLoaderCellItem(ICodeObjectUnit unit, String name, String offset, boolean relative) {
        this.unit = unit;
        this.name = name;
        if (offset.endsWith("h")) {
            offset = offset.substring(0, offset.length() - 1);
        }
        try {
            this.offset = Long.parseLong(offset, 16);
        } catch (NumberFormatException e) {
            this.offset = -1L;
        }
        this.relative = relative;
    }

    public String getLabel() {
        return this.name;
    }

    public ItemClassIdentifiers getClassId() {
        return ItemClassIdentifiers.DEFAULT;
    }

    public String getAddress() {
        if (this.offset < 0L) {
            return null;
        }
        long address = this.offset;
        if ((this.relative) && (this.unit != null)) {
            address += this.unit.getLoaderInformation().getImageBase();
        }
        return String.format("%Xh", new Object[]{Long.valueOf(address)});
    }
}


