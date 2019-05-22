package com.pnfsoftware.jeb.rcpclient.parts;

import com.google.common.net.MediaType;
import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.units.IBinaryUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.util.regex.IValueProvider;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public class ProjectTreeLabelProvider extends StyledCellLabelProvider implements IValueProvider {
    public static final int FGCOLOR_UNIT_REPARSED = 160;
    public static final int BGCOLOR_DEBUGGER_ATTACHED = 10223003;
    public static final int BGCOLOR_UNIT_UNPROCESSED = 14079682;

    public void update(ViewerCell cell) {
        Color fgcolor = null;
        Color bgcolor = null;
        String text = "?";
        Image img = null;
        Object element = cell.getElement();
        if ((element instanceof IRuntimeProject)) {
            IRuntimeProject project = (IRuntimeProject) element;
            text = project.getName();
            img = UIAssetManager.getInstance().getImage("eclipse/prj_obj.png");
        } else if ((element instanceof ILiveArtifact)) {
            ILiveArtifact artifact = (ILiveArtifact) element;
            text = artifact.getArtifact().getName();
            img = UIAssetManager.getInstance().getImage("eclipse/generic_element.png");
        } else if ((element instanceof IUnit)) {
            IUnit unit = (IUnit) element;
            fgcolor = determineLabelFgcolor(unit);
            bgcolor = determineLabelBgcolor(unit);
            text = unit.getName();
            String type = unit.getFormatType();
            String unitIconKey = "unit:/" + type;
            img = JFaceResources.getImage(unitIconKey);
            if (img == null) {
                byte[] iconData = unit.getIconData();
                if (iconData != null) {
                    img = new Image(UIAssetManager.getInstance().getDisplay(), new ByteArrayInputStream(iconData));
                    JFaceResources.getImageRegistry().put(unitIconKey, img);
                }
                if (img == null) {
                    String iconRelPath = getUnitIconRelativePath(unit);
                    if (iconRelPath != null) {
                        img = UIAssetManager.getInstance().getImage(iconRelPath);
                    }
                }
            }
        }
        cell.setForeground(fgcolor);
        cell.setBackground(bgcolor);
        cell.setText(text);
        if (img != null) {
            cell.setImage(img);
        }
        super.update(cell);
    }

    private static boolean isTransientUnit(IUnit unit) {
        IUnitCreator parent = unit.getParent();
        if ((parent instanceof IUnit)) {
            return ((IUnit) parent).isTransientChild(unit);
        }
        return false;
    }

    private static Color determineLabelFgcolor(IUnit unit) {
        if (isTransientUnit(unit)) {
            return UIAssetManager.getInstance().getColor(160);
        }
        return null;
    }

    private static Color determineLabelBgcolor(IUnit unit) {
        if (!unit.isProcessed()) {
            return UIAssetManager.getInstance().getColor(14079682);
        }
        if (((unit instanceof IDebuggerUnit)) && (((IDebuggerUnit) unit).isAttached())) {
            return UIAssetManager.getInstance().getColor(10223003);
        }
        return null;
    }

    private static Map<String, String> typeToPath = new HashMap();

    static {
        typeToPath.put("composite", "eclipse/fldr_obj.png");
        typeToPath.put("odex", "images/odex_icon.png");
        typeToPath.put("dex", "android.png");
        typeToPath.put("apk", "eclipse/importzip_wiz.png");
        typeToPath.put("xapk", "eclipse/importzip_wiz.png");
        typeToPath.put("crx", "images/chrome_icon.png");
        typeToPath.put("zip", "eclipse/importzip_wiz.png");
        typeToPath.put("sevenzip", "eclipse/importzip_wiz.png");
        typeToPath.put("jar", "eclipse/jar_obj.png");
        typeToPath.put("ar", "eclipse/importzip_wiz.png");
        typeToPath.put("tar", "eclipse/importzip_wiz.png");
        typeToPath.put("html", "eclipse/html_tag_obj.png");
        typeToPath.put("xml", "eclipse/generic_xml_obj.png");
        typeToPath.put("json", "images/json_icon.png");
        typeToPath.put("cert", "eclipse/owned_monitor_obj.png");
        typeToPath.put("java", "eclipse/jcu_obj.png");
        typeToPath.put("javaclass", "images/java_icon.png");
        typeToPath.put("javascript", "images/js_icon.png");
        typeToPath.put("text", "eclipse/file_obj.png");
        typeToPath.put("pdf", "images/pdf_icon.png");
        typeToPath.put("text", "eclipse/file_obj.png");
        typeToPath.put("msdoc", "images/word_icon.png");
        typeToPath.put("msxls", "images/excel_icon.png");
        typeToPath.put("msppt", "images/powerpoint_icon.png");
        typeToPath.put("msoutlook", "images/outlook_icon.png");
        typeToPath.put("msthumbs", "images/thumbs_icon.png");
        typeToPath.put("elf", "images/elf_icon.png");
        typeToPath.put("winpe", "images/pe_icon.png");
        typeToPath.put("macho", "eclipse/builder.png");
        typeToPath.put("wincoff", "eclipse/builder.png");
        typeToPath.put("c", "eclipse/file_obj.png");
        typeToPath.put("cpp", "eclipse/file_obj.png");
        typeToPath.put("wasm", "images/wasm_icon.png");
    }

    public static String getUnitIconRelativePath(IUnit unit) {
        String type = unit.getFormatType();
        if (type != null) {
            for (String t : typeToPath.keySet()) {
                if (t.equalsIgnoreCase(type)) {
                    return typeToPath.get(t);
                }
            }
            if (type.startsWith("dcmp_")) {
                return "eclipse/debugt_obj.png";
            }
            if (type.startsWith("dbug_")) {
                return "eclipse/debug_view.png";
            }
            String lt = type.toLowerCase();
            if (lt.contains("stream")) {
                return "eclipse/builder.png";
            }
            if (lt.contains("image")) {
                return "eclipse/image_application.png";
            }
            if ((unit instanceof ICodeUnit)) {
                return "images/processor-icon.png";
            }
        }
        if ((unit instanceof IBinaryUnit)) {
            String mimeType = ((IBinaryUnit) unit).getMimeType();
            if (mimeType != null) {
                String[] parts = mimeType.split("/");
                if (parts.length == 2) {
                    try {
                        MediaType m = MediaType.create(parts[0], parts[1]);
                        if (m.is(MediaType.ANY_IMAGE_TYPE)) {
                            return "eclipse/image_application.png";
                        }
                        if (m.is(MediaType.ANY_AUDIO_TYPE)) {
                            return "eclipse/image_application.png";
                        }
                        if (m.is(MediaType.ANY_VIDEO_TYPE)) {
                            return "eclipse/image_application.png";
                        }
                    } catch (Exception localException) {
                    }
                }
            }
        }
        return "eclipse/unknown_obj.png";
    }

    public String getString(Object element) {
        if ((element instanceof IRuntimeProject)) {
            return ((IRuntimeProject) element).getName();
        }
        if ((element instanceof ILiveArtifact)) {
            return ((ILiveArtifact) element).getArtifact().getName();
        }
        if ((element instanceof IUnit)) {
            return ((IUnit) element).getName();
        }
        if (element != null) {
            return element.toString();
        }
        return null;
    }

    public String getStringAt(Object element, int filterLabelIndex) {
        return getString(element);
    }
}


