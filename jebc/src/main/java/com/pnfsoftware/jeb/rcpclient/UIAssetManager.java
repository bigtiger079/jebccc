
package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.rcpclient.extensions.SwtRegistry;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public final class UIAssetManager
        extends SwtRegistry {
    private static final ILogger logger = GlobalLog.getLogger(UIAssetManager.class);
    private static UIAssetManager manager = null;

    public static UIAssetManager getInstance() {
        if (manager == null) {
            manager = new UIAssetManager();
        }
        return manager;
    }

    private Map<String, Image> images = new HashMap();

    private UIAssetManager() {
        super(Display.getCurrent());
    }

    public Image getImage(String filename) {
        Image asset = (Image) this.images.get(filename);
        if (asset == null) {
            try {
                InputStream in = Assets.getAsset(filename);
                Throwable localThrowable3 = null;
                try {
                    if (in == null) {
                        logger.warn("Image not found: %s", new Object[]{filename});
                    } else {
                        asset = new Image(this.display, in);
                    }
                    this.images.put(filename, asset);
                } catch (Throwable localThrowable1) {
                    localThrowable3 = localThrowable1;
                    throw localThrowable1;
                } finally {
                    if (in != null) if (localThrowable3 != null) try {
                        in.close();
                    } catch (Throwable localThrowable2) {
                        localThrowable3.addSuppressed(localThrowable2);
                    }
                    else in.close();
                }
            } catch (SWTException e) {
                logger.catching(e);
            } catch (IOException localIOException) {
            }
        }
        return asset;
    }

    public Image getImage(String filename, AssetManagerOverlay overlay) {
        if (overlay == null) {
            return getImage(filename);
        }
        if (!overlay.hasLayer()) {
            return getImage(filename);
        }
        Image asset = (Image) this.images.get(filename + overlay.getId());
        if (asset == null) {
            asset = overlay.build(getImage(filename));
            this.images.put(filename + overlay.getId(), asset);
        }
        return asset;
    }

    public void dispose() {
        super.dispose();
        for (Image image : this.images.values()) {
            image.dispose();
        }
    }
}


