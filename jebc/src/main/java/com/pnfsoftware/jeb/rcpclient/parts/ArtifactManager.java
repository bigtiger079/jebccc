package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.UnitUtil;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.android.IDexUnit;
import com.pnfsoftware.jeb.core.units.codeobject.ICodeObjectUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

public class ArtifactManager implements IArtifactManager {
    private static final ILogger logger = GlobalLog.getLogger(ArtifactManager.class);
    private static final List<IUnit> EMPTY = new ArrayList<>();
    private static ArtifactManager instance;

    public static ArtifactManager getInstance() {
        if (instance == null) {
            instance = new ArtifactManager();
        }
        return instance;
    }

    private String getType(ILiveArtifact artifact) {
        List<IUnit> artifactUnits = artifact.getUnits();
        if ((artifactUnits != null) && (artifactUnits.size() == 1)) {
            return artifactUnits.get(0).getFormatType();
        }
        return null;
    }

    public List<IUnit> getExpandedUnits(ILiveArtifact artifact) {
        String type = getType(artifact);
        if (type == null) {
            return EMPTY;
        }
        IUnit unit0 = artifact.getUnits().get(0);
        if ((unit0 instanceof ICodeObjectUnit)) {
            ArrayList<IUnit> arrayList = new ArrayList<>();
            arrayList.add(unit0);
            return arrayList;
        }
        switch (type) {
            case "apk":
            case "ar":
            case "zip":
                List<IUnit> expanded = new ArrayList<>();
                expanded.add(unit0);
                return expanded;
        }
        return EMPTY;
    }

    public void processLiveArtifact(RcpClientContext context, PartManager pman, ILiveArtifact artifact, TreeViewer projectTreeViewer) {
        String type = getType(artifact);
        if (type == null) {
            return;
        }
        IUnit unit0 = artifact.getUnits().get(0);
        if ((unit0 instanceof ICodeObjectUnit)) {
            ICodeUnit child = UnitUtil.findChild(unit0, null, ICodeUnit.class, false, 0);
            if (child != null) {
                openUnit(context, pman, child, projectTreeViewer);
                return;
            }
        }
        switch (type) {
            case "apk":
                IUnit dexUnit = UnitUtil.findChild(unit0, null, IDexUnit.class, false, 0);
                if (dexUnit != null) {
                    openUnit(context, pman, dexUnit, projectTreeViewer);
                }
                break;
            case "machofat":
                IUnit nativeUnit = UnitUtil.findChild(unit0, null, ICodeUnit.class, false, 0);
                if (nativeUnit != null) {
                    openUnit(context, pman, nativeUnit, projectTreeViewer);
                }
                break;
            default:
                if (isDirectOpeningFile(type)) {
                    openUnit(context, pman, unit0, projectTreeViewer);
                }
                break;
        }
    }

    protected void openUnit(RcpClientContext context, PartManager pman, IUnit unit, TreeViewer projectTreeViewer) {
        projectTreeViewer.setSelection(new StructuredSelection(unit), true);
        if (!HandlerUtil.processUnit(projectTreeViewer.getTree().getShell(), context, unit, true)) {
            return;
        }
        pman.create(unit, true);
    }

    private List<String> directOpening = Arrays.asList("dex", "xml");

    protected boolean isDirectOpeningFile(String type) {
        return this.directOpening.contains(type);
    }
}


