package com.pnfsoftware.jeb.rcpclient.iviewers;

import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import org.eclipse.swt.graphics.Color;

public interface IStyleProvider {
    public abstract Style getStyle(ItemClassIdentifiers paramItemClassIdentifiers, boolean paramBoolean);

    public abstract Style getStyle(IItem paramIItem);

    public abstract Color getOnCaretBackgroundColor();
}


