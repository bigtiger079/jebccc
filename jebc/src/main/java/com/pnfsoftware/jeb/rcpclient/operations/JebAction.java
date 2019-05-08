
package com.pnfsoftware.jeb.rcpclient.operations;

import com.pnfsoftware.jeb.rcpclient.Assets;
import com.pnfsoftware.jeb.rcpclient.extensions.binding.ActionEx;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.util.format.Strings;

import java.io.InputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;

public abstract class JebAction
        extends ActionEx {
    protected boolean isContextual = true;
    protected int keyCode;
    protected int keyModifier;
    protected JebBaseHandler handler;

    public JebAction(String id, String name) {
        super(id, name);
    }

    public JebAction(String id, String name, int style, String tooltip, final String icon, int accelerator) {
        super(id, name, style);
        if (!Strings.isBlank(tooltip)) {
            setToolTipText(tooltip);
        }
        if (icon != null) {
            setImageDescriptor(new ImageDescriptor() {
                public ImageData getImageData() {
                    InputStream is = Assets.getAsset(icon);
                    if (is != null) {
                        return new ImageData(is);
                    }
                    return null;
                }
            });
        }
        if (accelerator != 0) {
            setAccelerator(accelerator);
        }
    }

    public boolean isContextual() {
        return this.isContextual;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public int getKeyModifier() {
        return this.keyModifier;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\JebAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */