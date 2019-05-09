package com.pnfsoftware.jeb.rcpclient.parts;

import com.google.common.collect.Lists;
import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.units.IAddressableUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.util.DecompilerHelper;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Shell;

public class DecompiledViewNavigator implements IViewNavigator {
    private static final ILogger logger = GlobalLog.getLogger(DecompiledViewNavigator.class);
    IAddressableUnit unit;
    RcpClientContext context;
    IMPart currentPart;

    public DecompiledViewNavigator(IAddressableUnit unit, RcpClientContext context, IMPart currentPart) {
        this.unit = unit;
        this.context = context;
        this.currentPart = currentPart;
    }

    public boolean canHandleAddress(String address) {
        return this.unit.isValidAddress(address);
    }

    public boolean navigateTo(IActionableItem item, IViewManager viewManager, boolean record) {
        long itemId = item.getItemId();
        if (itemId == 0L) {
            return false;
        }
        String address = this.unit.getAddressOfItem(itemId);
        if (address == null) {
            return false;
        }
        return navigateTo(address, viewManager, record);
    }

    public boolean navigateTo(String address, IViewManager viewManager, boolean record) {
        if ((address == null) || (viewManager == null)) {
            return false;
        }
        PartManager partManager = (PartManager) viewManager;
        IDecompilerUnit decompiler = DecompilerHelper.getRelatedDecompiler(this.unit);
        if (decompiler == null) {
            return false;
        }
        ICodeUnit code = decompiler.getCodeUnit();
        if (code == null) {
            return false;
        }
        if (!code.isValidAddress(address)) {
            return false;
        }
        IUnit unit = decompiler.getDecompiledUnit(address);
        if (unit == null) {
            Shell shell = UI.getShellTracker().get();
            unit = HandlerUtil.decompileAsync(shell, this.context, decompiler, address);
            if (unit == null) {
                unit = code;
            }
        }
        IMPart targetPart;
        List<IMPart> potentialOriginParts = partManager.getPartsForUnit(unit);
        if (potentialOriginParts.isEmpty()) {
            targetPart = partManager.create(unit, true).get(0);
            partManager.setOriginator(targetPart, this.currentPart);
        } else {
            targetPart = partManager.selectWithOriginatorDeep(potentialOriginParts, this.currentPart);
            if (targetPart != null) {
                partManager.focus(targetPart);
            } else {
                targetPart = findFirstPartWithTextFragment(partManager, potentialOriginParts);
                if (targetPart != null) {
                    partManager.setOriginator(targetPart, this.currentPart);
                    partManager.focus(targetPart);
                } else {
                    targetPart = partManager.create(unit, false).get(0);
                    partManager.setOriginator(targetPart, this.currentPart);
                }
            }
        }
        if (targetPart == null) {
            return false;
        }
        UnitPartManager unitPartManager = partManager.getUnitPartManager(targetPart);
        if ((unitPartManager != null) && (unitPartManager.getUnit() != this.unit)) {
            return unitPartManager.setActiveAddress(address, null, record);
        }
        return true;
    }

    private IMPart findFirstPartWithTextFragment(PartManager pman, List<IMPart> parts) {
        for (IMPart part : parts) {
            UnitPartManager p = pman.getUnitPartManager(part);
            if (!Lists.newArrayList(p.getFragments().stream().filter((InteractiveTextView.class)::isInstance).collect(Collectors.toList())).isEmpty()) {
                return part;
            }
        }
        return null;
    }
}


