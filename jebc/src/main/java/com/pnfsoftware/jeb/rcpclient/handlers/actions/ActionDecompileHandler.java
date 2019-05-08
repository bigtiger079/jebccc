
package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.util.DecompilerHelper;
import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;

public class ActionDecompileHandler
        extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionDecompileHandler.class);

    public ActionDecompileHandler() {
        super("decompile", S.s(475), null, "eclipse/debugt_obj.png");
        setAccelerator(9);
    }

    public boolean canExecute() {
        if (this.part == null) {
            return false;
        }
        Object object = this.part.getManager();
        if (isDisableHandlers(this.part)) {
            return false;
        }
        if ((object instanceof UnitPartManager)) {
            String address = ((UnitPartManager) object).getActiveAddress(AddressConversionPrecision.COARSE);
            if (address != null) {
                return true;
            }
        }
        return false;
    }

    public void execute() {
        this.context.getTelemetry().record("actionDecompile");
        if (this.part == null) {
            return;
        }
        Object object = this.part.getManager();
        if (!(object instanceof UnitPartManager)) {
            return;
        }
        UnitPartManager unitPart = (UnitPartManager) object;
        IUnit unit = unitPart.getUnit();
        IRcpUnitFragment activeFragment = unitPart.getActiveFragment();
        if (activeFragment == null) {
            logger.i("No active view", new Object[0]);
            return;
        }
        String address = activeFragment.getActiveAddress();
        if (address == null) {
            logger.info("Cannot determine where to decompile", new Object[0]);
            return;
        }
        IDecompilerUnit decompiler = DecompilerHelper.getDecompiler(unit);
        if ((decompiler == null) && (
                (!(unit.getParent() instanceof IUnit)) || (!((IUnit) unit.getParent()).getName().equals("decompiler")))) {
            StringBuilder msg = new StringBuilder("Your build does not provide decompilation support for this type of code.\n\nThe decompilers available with your license type are:\n");
            msg.append(Strings.join(", ", DecompilerHelper.getAvailableDecompilerNames(this.context.getEnginesContext())));
            if (!UI.popupOptional(this.shell, 0, "Decompiler not available", msg.toString(), "dlgDecompilerNotAvailable")) {
                logger.warn("No decompiler available", new Object[0]);
            }
            return;
        }
        logger.i("decompiler= %s", new Object[]{decompiler});
        PartManager pman = this.context.getPartManager();
        IMPart targetPart = null;
        GlobalPosition pos0 = this.context.getViewManager().getCurrentGlobalPosition();
        if (decompiler != null) {
            IUnit c;
            try {
                c = decompiler.getDecompiledUnit(address);
            } catch (ClassCastException e) {
                MessageDialog.openError(this.shell, S.s(304), "It seems that your JDB2 database contains inconsistencies, which makes it incompatible with this version of JEB.\n\nWe apologize for this inconvenience.");
                if (this.context.isDevelopmentMode()) {
                    logger.catching(e);
                }
                return;
            }
            if (c == null) {
                logger.info("Decompiling at %s", new Object[]{address});
                c = HandlerUtil.decompileAsync(this.shell, this.context, decompiler, address);
                if (c == null) {
                    return;
                }
            }
            List<UnitPartManager> targetParts = pman.getPartManagersForUnit(c);
            if (targetParts.isEmpty()) {
                targetPart = (IMPart) pman.create(c, true).get(0);
                pman.setOriginator(targetPart, this.part);
            } else {
                targetPart = pman.getFirstPartForUnit(c);
                pman.setOriginator(targetPart, this.part);
                pman.focus(targetPart);
            }
        } else {
            IUnitCreator parent = unit.getParent();
            if (!(parent instanceof IUnit)) {
                return;
            }
            parent = ((IUnit) parent).getParent();
            if (!(parent instanceof IUnit)) {
                return;
            }
            IUnit disassembler = (IUnit) parent;
            List<IMPart> potentialOriginParts = pman.getPartsForUnit(disassembler);
            if (potentialOriginParts.isEmpty()) {
                targetPart = (IMPart) pman.create(disassembler, true).get(0);
            } else {
                targetPart = findFirstPartWithTextFragment(pman, potentialOriginParts);
                if (targetPart != null) {
                    pman.setOriginator(targetPart, this.part);
                    pman.focus(targetPart);
                } else {
                    targetPart = (IMPart) pman.create(disassembler, false).get(0);
                }
            }
        }
        if (targetPart != null) {
            UnitPartManager p = pman.getUnitPartManager(targetPart);
            if (p != null) {
                p.setActiveAddress(address, null, false);
            }
        }
        if (pos0 != null) {
            this.context.getViewManager().recordGlobalPosition(pos0);
        }
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


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\actions\ActionDecompileHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */