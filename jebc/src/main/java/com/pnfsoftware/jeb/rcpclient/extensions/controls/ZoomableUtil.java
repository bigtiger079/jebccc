
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

public abstract class ZoomableUtil {
    public static int updateZoom(int previous, int update) {
        if (update > 0) {
            return previous + 1;
        }
        if (update < 0) {
            return previous - 1;
        }
        return 0;
    }

    public static int sanitizeZoom(int zoom) {
        if (zoom < 0) {
            return -1;
        }
        if (zoom > 0) {
            return 1;
        }
        return 0;
    }
}


