package com.pnfsoftware.jeb.rcpclient.iviewers;

import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import org.eclipse.swt.graphics.Color;

public interface IStyleProvider {
    Style getStyle(ItemClassIdentifiers paramItemClassIdentifiers, boolean paramBoolean);

    Style getStyle(IItem paramIItem);

    Color getOnCaretBackgroundColor();
}


