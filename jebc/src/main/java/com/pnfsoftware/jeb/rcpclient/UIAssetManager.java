/*    */
package com.pnfsoftware.jeb.rcpclient;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.extensions.SwtRegistry;
/*    */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*    */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.eclipse.swt.SWTException;
/*    */ import org.eclipse.swt.graphics.Image;
/*    */ import org.eclipse.swt.widgets.Display;

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
/*    */
/*    */ public final class UIAssetManager
        /*    */ extends SwtRegistry
        /*    */ {
    /* 30 */   private static final ILogger logger = GlobalLog.getLogger(UIAssetManager.class);
    /*    */
    /* 32 */   private static UIAssetManager manager = null;

    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    /*    */
    public static UIAssetManager getInstance()
    /*    */ {
        /* 42 */
        if (manager == null) {
            /* 43 */
            manager = new UIAssetManager();
            /*    */
        }
        /* 45 */
        return manager;
        /*    */
    }

    /*    */
    /* 48 */   private Map<String, Image> images = new HashMap();

    /*    */
    /*    */
    private UIAssetManager() {
        /* 51 */
        super(Display.getCurrent());
        /*    */
    }

    /*    */
    /*    */
    public Image getImage(String filename) {
        /* 55 */
        Image asset = (Image) this.images.get(filename);
        /* 56 */
        if (asset == null) {
            /* 57 */
            try {
                InputStream in = Assets.getAsset(filename);
                Throwable localThrowable3 = null;
                /* 58 */
                try {
                    if (in == null) {
                        /* 59 */
                        logger.warn("Image not found: %s", new Object[]{filename});
                        /*    */
                    }
                    /*    */
                    else {
                        /* 62 */
                        asset = new Image(this.display, in);
                        /*    */
                    }
                    /* 64 */
                    this.images.put(filename, asset);
                    /*    */
                }
                /*    */ catch (Throwable localThrowable1)
                    /*    */ {
                    /* 57 */
                    localThrowable3 = localThrowable1;
                    throw localThrowable1;
                    /*    */
                    /*    */
                    /*    */
                }
                /*    */ finally
                    /*    */ {
                    /*    */
                    /*    */
                    /* 65 */
                    if (in != null) if (localThrowable3 != null) try {
                        in.close();
                    } catch (Throwable localThrowable2) {
                        localThrowable3.addSuppressed(localThrowable2);
                    }
                    else in.close();
                    /*    */
                }
                /* 67 */
            } catch (SWTException e) {
                logger.catching(e);
                /*    */
            }
            /*    */ catch (IOException localIOException) {
            }
            /*    */
        }
        /*    */
        /* 72 */
        return asset;
        /*    */
    }

    /*    */
    /*    */
    public Image getImage(String filename, AssetManagerOverlay overlay) {
        /* 76 */
        if (overlay == null) {
            /* 77 */
            return getImage(filename);
            /*    */
        }
        /* 79 */
        if (!overlay.hasLayer()) {
            /* 80 */
            return getImage(filename);
            /*    */
        }
        /* 82 */
        Image asset = (Image) this.images.get(filename + overlay.getId());
        /* 83 */
        if (asset == null) {
            /* 84 */
            asset = overlay.build(getImage(filename));
            /* 85 */
            this.images.put(filename + overlay.getId(), asset);
            /*    */
        }
        /* 87 */
        return asset;
        /*    */
    }

    /*    */
    /*    */
    public void dispose()
    /*    */ {
        /* 92 */
        super.dispose();
        /* 93 */
        for (Image image : this.images.values()) {
            /* 94 */
            image.dispose();
            /*    */
        }
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\UIAssetManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */