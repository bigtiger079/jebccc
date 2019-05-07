/*    */
package com.pnfsoftware.jeb.rcpclient.operations;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.Assets;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.binding.ActionEx;
/*    */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*    */ import com.pnfsoftware.jeb.util.format.Strings;
/*    */ import java.io.InputStream;
/*    */ import org.eclipse.jface.resource.ImageDescriptor;
/*    */ import org.eclipse.swt.graphics.ImageData;

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
/*    */
/*    */ public abstract class JebAction
        /*    */ extends ActionEx
        /*    */ {
    /* 26 */   protected boolean isContextual = true;
    /*    */   protected int keyCode;
    /*    */   protected int keyModifier;
    /*    */   protected JebBaseHandler handler;

    /*    */
    /*    */
    public JebAction(String id, String name) {
        /* 32 */
        super(id, name);
        /*    */
    }

    /*    */
    /*    */
    public JebAction(String id, String name, int style, String tooltip, final String icon, int accelerator) {
        /* 36 */
        super(id, name, style);
        /*    */
        /* 38 */
        if (!Strings.isBlank(tooltip)) {
            /* 39 */
            setToolTipText(tooltip);
            /*    */
        }
        /*    */
        /* 42 */
        if (icon != null) {
            /* 43 */
            setImageDescriptor(new ImageDescriptor()
                    /*    */ {
                /*    */
                public ImageData getImageData()
                /*    */ {
                    /* 47 */
                    InputStream is = Assets.getAsset(icon);
                    /* 48 */
                    if (is != null) {
                        /* 49 */
                        return new ImageData(is);
                        /*    */
                    }
                    /* 51 */
                    return null;
                    /*    */
                }
                /*    */
            });
            /*    */
        }
        /*    */
        /* 56 */
        if (accelerator != 0) {
            /* 57 */
            setAccelerator(accelerator);
            /*    */
        }
        /*    */
    }

    /*    */
    /*    */
    public boolean isContextual() {
        /* 62 */
        return this.isContextual;
        /*    */
    }

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public int getKeyCode()
    /*    */ {
        /* 71 */
        return this.keyCode;
        /*    */
    }

    /*    */
    /*    */
    public int getKeyModifier() {
        /* 75 */
        return this.keyModifier;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\JebAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */