package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;

public abstract interface IArtifactManager {
    public abstract List<IUnit> getExpandedUnits(ILiveArtifact paramILiveArtifact);

    public abstract void processLiveArtifact(RcpClientContext paramRcpClientContext, PartManager paramPartManager, ILiveArtifact paramILiveArtifact, TreeViewer paramTreeViewer);
}


