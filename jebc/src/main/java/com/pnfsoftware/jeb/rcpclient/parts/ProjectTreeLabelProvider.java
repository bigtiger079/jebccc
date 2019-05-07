/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.google.common.net.MediaType;
/*     */ import com.pnfsoftware.jeb.core.IArtifact;
/*     */ import com.pnfsoftware.jeb.core.ILiveArtifact;
/*     */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*     */ import com.pnfsoftware.jeb.core.IUnitCreator;
/*     */ import com.pnfsoftware.jeb.core.units.IBinaryUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.jface.resource.ImageRegistry;
/*     */ import org.eclipse.jface.resource.JFaceResources;
/*     */ import org.eclipse.jface.viewers.StyledCellLabelProvider;
/*     */ import org.eclipse.jface.viewers.ViewerCell;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Image;

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
/*     */
/*     */
/*     */
/*     */
/*     */ public class ProjectTreeLabelProvider
        /*     */ extends StyledCellLabelProvider
        /*     */ implements IValueProvider
        /*     */ {
    /*     */   public static final int FGCOLOR_UNIT_REPARSED = 160;
    /*     */   public static final int BGCOLOR_DEBUGGER_ATTACHED = 10223003;
    /*     */   public static final int BGCOLOR_UNIT_UNPROCESSED = 14079682;

    /*     */
    /*     */
    public void update(ViewerCell cell)
    /*     */ {
        /*  50 */
        Color fgcolor = null;
        /*  51 */
        Color bgcolor = null;
        /*  52 */
        String text = "?";
        /*  53 */
        Image img = null;
        /*     */
        /*  55 */
        Object element = cell.getElement();
        /*     */
        /*  57 */
        if ((element instanceof IRuntimeProject)) {
            /*  58 */
            IRuntimeProject project = (IRuntimeProject) element;
            /*  59 */
            text = project.getName();
            /*  60 */
            img = UIAssetManager.getInstance().getImage("eclipse/prj_obj.png");
            /*     */
        }
        /*  62 */
        else if ((element instanceof ILiveArtifact)) {
            /*  63 */
            ILiveArtifact artifact = (ILiveArtifact) element;
            /*  64 */
            text = artifact.getArtifact().getName();
            /*  65 */
            img = UIAssetManager.getInstance().getImage("eclipse/generic_element.png");
            /*     */
        }
        /*  67 */
        else if ((element instanceof IUnit)) {
            /*  68 */
            IUnit unit = (IUnit) element;
            /*     */
            /*  70 */
            fgcolor = determineLabelFgcolor(unit);
            /*  71 */
            bgcolor = determineLabelBgcolor(unit);
            /*  72 */
            text = unit.getName();
            /*     */
            /*  74 */
            String type = unit.getFormatType();
            /*     */
            /*     */
            /*     */
            /*     */
            /*  79 */
            String unitIconKey = "unit:/" + type;
            /*  80 */
            img = JFaceResources.getImage(unitIconKey);
            /*  81 */
            if (img == null) {
                /*  82 */
                byte[] iconData = unit.getIconData();
                /*  83 */
                if (iconData != null) {
                    /*  84 */
                    img = new Image(UIAssetManager.getInstance().getDisplay(), new ByteArrayInputStream(iconData));
                    /*  85 */
                    JFaceResources.getImageRegistry().put(unitIconKey, img);
                    /*     */
                }
                /*  87 */
                if (img == null) {
                    /*  88 */
                    String iconRelPath = getUnitIconRelativePath(unit);
                    /*  89 */
                    if (iconRelPath != null) {
                        /*  90 */
                        img = UIAssetManager.getInstance().getImage(iconRelPath);
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /*  96 */
        cell.setForeground(fgcolor);
        /*  97 */
        cell.setBackground(bgcolor);
        /*  98 */
        cell.setText(text);
        /*     */
        /* 100 */
        if (img != null) {
            /* 101 */
            cell.setImage(img);
            /*     */
        }
        /*     */
        /* 104 */
        super.update(cell);
        /*     */
    }

    /*     */
    /*     */
    private static boolean isTransientUnit(IUnit unit) {
        /* 108 */
        IUnitCreator parent = unit.getParent();
        /* 109 */
        if ((parent instanceof IUnit)) {
            /* 110 */
            return ((IUnit) parent).isTransientChild(unit);
            /*     */
        }
        /* 112 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private static Color determineLabelFgcolor(IUnit unit) {
        /* 116 */
        if (isTransientUnit(unit)) {
            /* 117 */
            return UIAssetManager.getInstance().getColor(160);
            /*     */
        }
        /*     */
        /* 120 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    private static Color determineLabelBgcolor(IUnit unit) {
        /* 124 */
        if (!unit.isProcessed()) {
            /* 125 */
            return UIAssetManager.getInstance().getColor(14079682);
            /*     */
        }
        /*     */
        /* 128 */
        if (((unit instanceof IDebuggerUnit)) &&
                /* 129 */       (((IDebuggerUnit) unit).isAttached())) {
            /* 130 */
            return UIAssetManager.getInstance().getColor(10223003);
            /*     */
        }
        /*     */
        /*     */
        /* 134 */
        return null;
        /*     */
    }

    /*     */
    /* 137 */   private static Map<String, String> typeToPath = new HashMap();

    /*     */
    /* 139 */   static {
        typeToPath.put("composite", "eclipse/fldr_obj.png");
        /* 140 */
        typeToPath.put("odex", "images/odex_icon.png");
        /* 141 */
        typeToPath.put("dex", "android.png");
        /* 142 */
        typeToPath.put("apk", "eclipse/importzip_wiz.png");
        /* 143 */
        typeToPath.put("xapk", "eclipse/importzip_wiz.png");
        /* 144 */
        typeToPath.put("crx", "images/chrome_icon.png");
        /* 145 */
        typeToPath.put("zip", "eclipse/importzip_wiz.png");
        /* 146 */
        typeToPath.put("sevenzip", "eclipse/importzip_wiz.png");
        /* 147 */
        typeToPath.put("jar", "eclipse/jar_obj.png");
        /* 148 */
        typeToPath.put("ar", "eclipse/importzip_wiz.png");
        /* 149 */
        typeToPath.put("tar", "eclipse/importzip_wiz.png");
        /* 150 */
        typeToPath.put("html", "eclipse/html_tag_obj.png");
        /* 151 */
        typeToPath.put("xml", "eclipse/generic_xml_obj.png");
        /* 152 */
        typeToPath.put("json", "images/json_icon.png");
        /* 153 */
        typeToPath.put("cert", "eclipse/owned_monitor_obj.png");
        /* 154 */
        typeToPath.put("java", "eclipse/jcu_obj.png");
        /* 155 */
        typeToPath.put("javaclass", "images/java_icon.png");
        /* 156 */
        typeToPath.put("javascript", "images/js_icon.png");
        /* 157 */
        typeToPath.put("text", "eclipse/file_obj.png");
        /* 158 */
        typeToPath.put("pdf", "images/pdf_icon.png");
        /* 159 */
        typeToPath.put("text", "eclipse/file_obj.png");
        /* 160 */
        typeToPath.put("msdoc", "images/word_icon.png");
        /* 161 */
        typeToPath.put("msxls", "images/excel_icon.png");
        /* 162 */
        typeToPath.put("msppt", "images/powerpoint_icon.png");
        /* 163 */
        typeToPath.put("msoutlook", "images/outlook_icon.png");
        /* 164 */
        typeToPath.put("msthumbs", "images/thumbs_icon.png");
        /* 165 */
        typeToPath.put("elf", "images/elf_icon.png");
        /* 166 */
        typeToPath.put("winpe", "images/pe_icon.png");
        /* 167 */
        typeToPath.put("macho", "eclipse/builder.png");
        /* 168 */
        typeToPath.put("wincoff", "eclipse/builder.png");
        /*     */
        /* 170 */
        typeToPath.put("c", "eclipse/file_obj.png");
        /* 171 */
        typeToPath.put("cpp", "eclipse/file_obj.png");
        /* 172 */
        typeToPath.put("wasm", "images/wasm_icon.png");
        /*     */
    }

    /*     */
    /*     */
    public static String getUnitIconRelativePath(IUnit unit) {
        /* 176 */
        String type = unit.getFormatType();
        /* 177 */
        if (type != null) {
            /* 178 */
            for (String t : typeToPath.keySet()) {
                /* 179 */
                if (t.equalsIgnoreCase(type)) {
                    /* 180 */
                    return (String) typeToPath.get(t);
                    /*     */
                }
                /*     */
            }
            /* 183 */
            if (type.startsWith("dcmp_")) {
                /* 184 */
                return "eclipse/debugt_obj.png";
                /*     */
            }
            /* 186 */
            if (type.startsWith("dbug_")) {
                /* 187 */
                return "eclipse/debug_view.png";
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /* 196 */
            String lt = type.toLowerCase();
            /* 197 */
            if (lt.contains("stream")) {
                /* 198 */
                return "eclipse/builder.png";
                /*     */
            }
            /* 200 */
            if (lt.contains("image")) {
                /* 201 */
                return "eclipse/image_application.png";
                /*     */
            }
            /* 203 */
            if ((unit instanceof ICodeUnit)) {
                /* 204 */
                return "images/processor-icon.png";
                /*     */
            }
            /*     */
        }
        /*     */
        /* 208 */
        if ((unit instanceof IBinaryUnit)) {
            /* 209 */
            String mimeType = ((IBinaryUnit) unit).getMimeType();
            /* 210 */
            if (mimeType != null) {
                /* 211 */
                String[] parts = mimeType.split("/");
                /* 212 */
                if (parts.length == 2) {
                    /*     */
                    try {
                        /* 214 */
                        MediaType m = MediaType.create(parts[0], parts[1]);
                        /* 215 */
                        if (m.is(MediaType.ANY_IMAGE_TYPE)) {
                            /* 216 */
                            return "eclipse/image_application.png";
                            /*     */
                        }
                        /* 218 */
                        if (m.is(MediaType.ANY_AUDIO_TYPE)) {
                            /* 219 */
                            return "eclipse/image_application.png";
                            /*     */
                        }
                        /* 221 */
                        if (m.is(MediaType.ANY_VIDEO_TYPE)) {
                            /* 222 */
                            return "eclipse/image_application.png";
                            /*     */
                        }
                        /*     */
                    }
                    /*     */ catch (Exception localException) {
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 232 */
        return "eclipse/unknown_obj.png";
        /*     */
    }

    /*     */
    /*     */
    public String getString(Object element)
    /*     */ {
        /* 237 */
        if ((element instanceof IRuntimeProject)) {
            /* 238 */
            return ((IRuntimeProject) element).getName();
            /*     */
        }
        /* 240 */
        if ((element instanceof ILiveArtifact)) {
            /* 241 */
            return ((ILiveArtifact) element).getArtifact().getName();
            /*     */
        }
        /* 243 */
        if ((element instanceof IUnit)) {
            /* 244 */
            return ((IUnit) element).getName();
            /*     */
        }
        /* 246 */
        if (element != null) {
            /* 247 */
            return element.toString();
            /*     */
        }
        /* 249 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public String getStringAt(Object element, int filterLabelIndex)
    /*     */ {
        /* 254 */
        return getString(element);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\ProjectTreeLabelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */