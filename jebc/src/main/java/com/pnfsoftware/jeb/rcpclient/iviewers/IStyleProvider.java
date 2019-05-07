package com.pnfsoftware.jeb.rcpclient.iviewers;

import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import org.eclipse.swt.graphics.Color;

public abstract interface IStyleProvider {
    public abstract Style getStyle(ItemClassIdentifiers paramItemClassIdentifiers, boolean paramBoolean);

    public abstract Style getStyle(IItem paramIItem);

    public abstract Color getOnCaretBackgroundColor();
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\IStyleProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */