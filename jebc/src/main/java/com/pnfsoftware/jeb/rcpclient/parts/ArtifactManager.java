/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.pnfsoftware.jeb.core.ILiveArtifact;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.UnitUtil;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.IDexUnit;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.viewers.StructuredSelection;
/*     */ import org.eclipse.jface.viewers.TreeViewer;
/*     */ import org.eclipse.swt.widgets.Tree;

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
/*     */ public class ArtifactManager
        /*     */ implements IArtifactManager
        /*     */ {
    /*  37 */   private static final ILogger logger = GlobalLog.getLogger(ArtifactManager.class);
    /*     */
    /*  39 */   private static final List<IUnit> EMPTY = new ArrayList();
    /*     */   private static ArtifactManager instance;

    /*     */
    /*     */
    public static ArtifactManager getInstance()
    /*     */ {
        /*  44 */
        if (instance == null) {
            /*  45 */
            instance = new ArtifactManager();
            /*     */
        }
        /*  47 */
        return instance;
        /*     */
    }

    /*     */
    /*     */
    private String getType(ILiveArtifact artifact) {
        /*  51 */
        List<IUnit> artifactUnits = artifact.getUnits();
        /*  52 */
        if ((artifactUnits != null) && (artifactUnits.size() == 1)) {
            /*  53 */
            return ((IUnit) artifactUnits.get(0)).getFormatType();
            /*     */
        }
        /*  55 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public List<IUnit> getExpandedUnits(ILiveArtifact artifact)
    /*     */ {
        /*  64 */
        String type = getType(artifact);
        /*  65 */
        if (type == null) {
            /*  66 */
            return EMPTY;
            /*     */
        }
        /*  68 */
        IUnit unit0 = (IUnit) artifact.getUnits().get(0);
        /*     */
        /*  70 */
        if ((unit0 instanceof ICodeObjectUnit)) {
            /*  71 */       ??? =new ArrayList();
            /*  72 */       ???.add(unit0);
            /*  73 */
            return (List<IUnit>) ???;
            /*     */
        }
        /*     */
        /*  76 */
        switch (type) {
            /*     */
            case "apk":
                /*     */
            case "ar":
                /*     */
            case "zip":
                /*  80 */
                List<IUnit> expanded = new ArrayList();
                /*  81 */
                expanded.add(unit0);
                /*  82 */
                return expanded;
            /*     */
        }
        /*  84 */
        return EMPTY;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void processLiveArtifact(RcpClientContext context, PartManager pman, ILiveArtifact artifact, TreeViewer projectTreeViewer)
    /*     */ {
        /*  95 */
        String type = getType(artifact);
        /*  96 */
        if (type == null) {
            /*  97 */
            return;
            /*     */
        }
        /*  99 */
        IUnit unit0 = (IUnit) artifact.getUnits().get(0);
        /*     */
        /*     */
        /* 102 */
        if ((unit0 instanceof ICodeObjectUnit)) {
            /* 103 */       ??? =UnitUtil.findChild(unit0, null, ICodeUnit.class, false, 0);
            /* 104 */
            if (??? !=null){
                /* 105 */
                openUnit(context, pman, ???,projectTreeViewer);
                /* 106 */
                return;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 110 */
        switch (type) {
            /*     */
            case "apk":
                /* 112 */
                IUnit dexUnit = UnitUtil.findChild(unit0, null, IDexUnit.class, false, 0);
                /* 113 */
                if (dexUnit != null) {
                    /* 114 */
                    openUnit(context, pman, dexUnit, projectTreeViewer);
                    /*     */
                }
                /*     */
                /*     */
                break;
            /*     */
            case "machofat":
                /* 119 */
                IUnit nativeUnit = UnitUtil.findChild(unit0, null, ICodeUnit.class, false, 0);
                /* 120 */
                if (nativeUnit != null) {
                    /* 121 */
                    openUnit(context, pman, nativeUnit, projectTreeViewer);
                    /*     */
                }
                /*     */
                /*     */
                break;
            /*     */
            default:
                /* 126 */
                if (isDirectOpeningFile(type)) {
                    /* 127 */
                    openUnit(context, pman, unit0, projectTreeViewer);
                    /*     */
                }
                /*     */
                break;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    protected void openUnit(RcpClientContext context, PartManager pman, IUnit unit, TreeViewer projectTreeViewer) {
        /* 134 */
        projectTreeViewer.setSelection(new StructuredSelection(unit), true);
        /*     */
        /*     */
        /* 137 */
        if (!HandlerUtil.processUnit(projectTreeViewer.getTree().getShell(), context, unit, true)) {
            /* 138 */
            return;
            /*     */
        }
        /*     */
        /* 141 */
        pman.create(unit, true);
        /*     */
    }

    /*     */
    /* 144 */   private List<String> directOpening = Arrays.asList(new String[]{"dex", "xml"});

    /*     */
    /*     */
    protected boolean isDirectOpeningFile(String type) {
        /* 147 */
        return this.directOpening.contains(type);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\ArtifactManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */