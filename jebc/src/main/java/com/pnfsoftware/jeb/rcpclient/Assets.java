/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.util.io.IO;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public final class Assets
        /*     */ {
    /*  26 */   private static final ILogger logger = GlobalLog.getLogger(Assets.class);
    /*     */
    /*     */   public static final String ICON_JEB = "jeb1/icon-jeb.png";
    /*     */
    /*     */   public static final String ICON_JEB_MEDIUM = "jeb1/icon-jeb-32.png";
    /*     */
    /*     */   public static final String ICON_JEB_LARGE = "jeb1/icon-jeb-48.png";
    /*     */
    /*     */   public static final String ICON_JEB_XLARGE1 = "jeb1/icon-jeb-128.png";
    /*     */
    /*     */   public static final String ICON_OPEN = "jeb1/icon-open.png";
    /*     */
    /*     */   public static final String ICON_EXPORT = "jeb1/icon-export.png";
    /*     */
    /*     */   public static final String ICON_SAVE = "jeb1/icon-save.png";
    /*     */
    /*     */   public static final String ICON_PROPERTIES = "jeb1/icon-properties.png";
    /*     */
    /*     */   public static final String ICON_WORLD = "jeb1/icon-world.png";
    /*     */
    /*     */   public static final String ICON_OPTIONS = "jeb1/icon-options.png";
    /*     */
    /*     */   public static final String ICON_CODE = "jeb1/icon-code-context.png";
    /*     */
    /*     */   public static final String ICON_REFRESH = "jeb1/icon-refresh.png";
    /*     */
    /*     */   public static final String ICON_ARROW_DOWN = "jeb1/icon-arrow-down.png";
    /*     */
    /*     */   public static final String ICON_ARROW_LEFT = "jeb1/icon-arrow-left.png";
    /*     */
    /*     */   public static final String ICON_ARROW_RIGHT = "jeb1/icon-arrow-right.png";
    /*     */
    /*     */   public static final String ICON_LETTER_B = "jeb1/icon-letter-b.png";
    /*     */   public static final String ICON_LETTER_C = "jeb1/icon-letter-c.png";
    /*     */   public static final String ICON_LETTER_N = "jeb1/icon-letter-n.png";
    /*     */   public static final String ICON_LETTER_R = "jeb1/icon-letter-r.png";
    /*     */   public static final String ICON_LETTER_X = "jeb1/icon-letter-x.png";
    /*     */   public static final String ICON_LETTER_X_RED = "jeb1/icon-letter-x-red.png";
    /*     */   public static final String ICON_INFO = "jeb1/icon-info.png";
    /*     */   public static final String ICON_HELP = "jeb1/icon-help.png";
    /*     */   public static final String ICON_DOC = "jeb1/icon-doc.png";
    /*     */   public static final String ICON_CHECKUPDATE = "jeb1/icon-checkupdate.png";
    /*     */   public static final String ICON_MEMORY = "jeb1/icon-memory.png";
    /*     */   public static final String ICON_FORUM = "jeb1/icon-forum.png";
    /*     */   public static final String ICON_MANUAL = "jeb1/icon-manual.png";
    /*     */   public static final String ICON_DELETE = "jeb1/icon-delete.png";
    /*     */   public static final String ICON_MEDIA_RECORD = "jeb1/icon-media-record.png";
    /*     */   public static final String ICON_MEDIA_STOP = "jeb1/icon-media-stop.png";
    /*     */   public static final String ICON_MEDIA_PLAY = "jeb1/icon-media-play.png";
    /*     */   public static final String ICON_MEDIA_PAUSE = "jeb1/icon-media-pause.png";
    /*     */   public static final String ICON_SCRIPT = "jeb1/icon-script.png";

    /*     */
    /*     */
    public static InputStream getAsset(String filename)
    /*     */ {
        /*  80 */
        String path = String.format("../../../../../icons/%s", new Object[]{filename});
        /*     */
        /*  82 */
        InputStream in = Assets.class.getResourceAsStream(path);
        /*  83 */
        if (in == null)
            /*     */ {
            /*  85 */
            path = String.format("../../../../icons/%s", new Object[]{filename});
            /*     */
            /*  87 */
            in = Assets.class.getResourceAsStream(path);
            /*  88 */
            if (in == null) {
                /*  89 */
                path = String.format("/icons/%s", new Object[]{filename});
                /*     */
                /*  91 */
                in = Assets.class.getResourceAsStream(path);
                /*  92 */
                if (in == null)
                    /*     */ {
                    /*  94 */
                    File f = new File(path.substring(1));
                    /*  95 */
                    if (f.exists()) {
                        /*     */
                        try {
                            /*  97 */
                            return new FileInputStream(f);
                            /*     */
                        }
                        /*     */ catch (FileNotFoundException localFileNotFoundException) {
                        }
                        /*     */
                    }
                    /*     */
                    /*     */
                    /* 103 */
                    logger.warn("Cannot find asset: ", new Object[]{path});
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /* 108 */
        return in;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static byte[] readAsset(String filename)
    /*     */     throws IOException
    /*     */ {
        /* 119 */
        InputStream in = getAsset(filename);
        /* 120 */
        if (in == null) {
            /* 121 */
            throw new IOException("UI asset not found: " + filename);
            /*     */
        }
        /*     */
        try {
            /* 124 */
            return IO.readInputStream(in);
            /*     */
        }
        /*     */ finally {
            /* 127 */
            in.close();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static int getAssetSize(String filename)
    /*     */ {
        /* 138 */
        int size = -1;
        /* 139 */
        try {
            InputStream in = getAsset(filename);
            Throwable localThrowable4 = null;
            /* 140 */
            try {
                if (in == null) {
                    /* 141 */
                    return -1;
                    /*     */
                }
                /* 143 */
                size = in.available();
                /*     */
            }
            /*     */ catch (Throwable localThrowable6)
                /*     */ {
                /* 139 */
                localThrowable4 = localThrowable6;
                throw localThrowable6;
                /*     */
                /*     */
            }
            /*     */ finally
                /*     */ {
                /* 144 */
                if (in != null) if (localThrowable4 != null) try {
                    in.close();
                } catch (Throwable localThrowable3) {
                    localThrowable4.addSuppressed(localThrowable3);
                }
                else in.close();
                /*     */
            }
            /*     */
        } catch (IOException localIOException) {
        }
        /* 147 */
        return size;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\Assets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */