
package com.pnfsoftware.jeb.rcpclient.parts;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.units.IAddressableUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.util.DecompilerHelper;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;


public class DecompiledViewNavigator
        implements IViewNavigator {
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


        PartManager pman = (PartManager) viewManager;


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


        IUnit c = decompiler.getDecompiledUnit(address);

        if (c == null) {

            Shell shell = UI.getShellTracker().get();


            c = HandlerUtil.decompileAsync(shell, this.context, decompiler, address);


            if (c == null) {

                c = code;

            }

        }


        IMPart targetPart = null;

        List<IMPart> potentialOriginParts = pman.getPartsForUnit(c);

        if (potentialOriginParts.isEmpty()) {

            targetPart = (IMPart) pman.create(c, true).get(0);

            pman.setOriginator(targetPart, this.currentPart);

        } else {

            targetPart = pman.selectWithOriginatorDeep(potentialOriginParts, this.currentPart);

            if (targetPart != null) {

                pman.focus(targetPart);

            } else {

                targetPart = findFirstPartWithTextFragment(pman, potentialOriginParts);

                if (targetPart != null) {

                    pman.setOriginator(targetPart, this.currentPart);

                    pman.focus(targetPart);

                } else {

                    targetPart = (IMPart) pman.create(c, false).get(0);

                    pman.setOriginator(targetPart, this.currentPart);

                }

            }

        }


        if (targetPart == null) {

            return false;

        }


        UnitPartManager p = pman.getUnitPartManager(targetPart);

        if ((p != null) && (p.getUnit() != this.unit)) {

            return p.setActiveAddress(address, null, record);

        }

        return true;

    }


    private IMPart findFirstPartWithTextFragment(PartManager pman, List<IMPart> parts) {

        for (IMPart part : parts) {

            UnitPartManager p = pman.getUnitPartManager(part);

            if (!Lists.newArrayList(Iterables.filter(p.getFragments(), InteractiveTextView.class)).isEmpty()) {

                return part;

            }

        }

        return null;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\DecompiledViewNavigator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */