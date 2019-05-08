package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.util.events.IEventSource;
import org.eclipse.swt.graphics.Image;

public abstract interface IImageDocument extends IEventSource {
    public abstract Image getImage();
}


