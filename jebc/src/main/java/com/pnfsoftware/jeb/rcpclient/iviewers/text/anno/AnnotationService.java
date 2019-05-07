/*    */
package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.eclipse.swt.graphics.Color;
/*    */ import org.eclipse.swt.graphics.Image;

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
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class AnnotationService
        /*    */ {
    /*    */   private static AnnotationService instance;
    /*    */   public static final String BASETYPE = "com.pnfsoftware.jeb.rcpclient.textAnno";
    /*    */   public static final String TYPE_PC = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgPC";
    /*    */   public static final String TYPE_BP = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgBP";
    /*    */   public static final String TYPE_BP_DISABLED = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgBPDisabled";

    /*    */
    /*    */
    public static synchronized AnnotationService getInstance()
    /*    */ {
        /* 41 */
        if (instance == null) {
            /* 42 */
            instance = new AnnotationService();
            /*    */
            /*    */
            /* 45 */
            instance.create("com.pnfsoftware.jeb.rcpclient.textAnno.dbgPC", i("eclipse/inst_ptr_top.png"), c(8563403));
            /*    */
            /*    */
            /* 48 */
            instance.create("com.pnfsoftware.jeb.rcpclient.textAnno.dbgBP", i("eclipse/breakpoint.png"), null);
            /*    */
            /*    */
            /* 51 */
            instance.create("com.pnfsoftware.jeb.rcpclient.textAnno.dbgBPDisabled", i("eclipse/breakpoint_crossed.png"), null);
            /*    */
        }
        /* 53 */
        return instance;
        /*    */
    }

    /*    */
    /*    */
    static Image i(String path) {
        /* 57 */
        return UIAssetManager.getInstance().getImage(path);
        /*    */
    }

    /*    */
    /*    */
    static Color c(int rgb) {
        /* 61 */
        return UIAssetManager.getInstance().getColor(rgb);
        /*    */
    }

    /*    */
    /* 64 */   public Map<String, AnnotationFactory> map = new HashMap();

    /*    */
    /*    */
    /*    */
    /*    */
    public AnnotationFactory create(String type, Image image, Color hlColor)
    /*    */ {
        /* 70 */
        AnnotationFactory f = (AnnotationFactory) this.map.get(type);
        /* 71 */
        if (f == null) {
            /* 72 */
            f = new AnnotationFactory(type, image, hlColor);
            /* 73 */
            this.map.put(type, f);
            /*    */
        }
        /* 75 */
        return f;
        /*    */
    }

    /*    */
    /*    */
    public AnnotationFactory getFactory(String type) {
        /* 79 */
        return (AnnotationFactory) this.map.get(type);
        /*    */
    }

    /*    */
    /*    */
    public List<AnnotationFactory> getFactories() {
        /* 83 */
        return new ArrayList(this.map.values());
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\anno\AnnotationService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */