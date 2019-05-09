package com.pnfsoftware.jeb.rcpclient.iviewers.text.anno;

import com.pnfsoftware.jeb.rcpclient.UIAssetManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class AnnotationService {
    private static AnnotationService instance;
    public static final String BASETYPE = "com.pnfsoftware.jeb.rcpclient.textAnno";
    public static final String TYPE_PC = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgPC";
    public static final String TYPE_BP = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgBP";
    public static final String TYPE_BP_DISABLED = "com.pnfsoftware.jeb.rcpclient.textAnno.dbgBPDisabled";

    public static synchronized AnnotationService getInstance() {
        if (instance == null) {
            instance = new AnnotationService();
            instance.create("com.pnfsoftware.jeb.rcpclient.textAnno.dbgPC", i("eclipse/inst_ptr_top.png"), c(8563403));
            instance.create("com.pnfsoftware.jeb.rcpclient.textAnno.dbgBP", i("eclipse/breakpoint.png"), null);
            instance.create("com.pnfsoftware.jeb.rcpclient.textAnno.dbgBPDisabled", i("eclipse/breakpoint_crossed.png"), null);
        }
        return instance;
    }

    static Image i(String path) {
        return UIAssetManager.getInstance().getImage(path);
    }

    static Color c(int rgb) {
        return UIAssetManager.getInstance().getColor(rgb);
    }

    public Map<String, AnnotationFactory> map = new HashMap();

    public AnnotationFactory create(String type, Image image, Color hlColor) {
        AnnotationFactory f = (AnnotationFactory) this.map.get(type);
        if (f == null) {
            f = new AnnotationFactory(type, image, hlColor);
            this.map.put(type, f);
        }
        return f;
    }

    public AnnotationFactory getFactory(String type) {
        return (AnnotationFactory) this.map.get(type);
    }

    public List<AnnotationFactory> getFactories() {
        return new ArrayList<>(this.map.values());
    }
}


