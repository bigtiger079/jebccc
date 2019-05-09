package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.google.common.collect.Lists;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IUnitCreator;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.util.DecompilerHelper;
import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
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

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;

public class ActionDecompileHandler extends JebBaseHandler {
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
            return address != null;
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
            logger.i("No active view");
            return;
        }
        String address = activeFragment.getActiveAddress();
        if (address == null) {
            logger.info("Cannot determine where to decompile");
            return;
        }
        IDecompilerUnit decompiler = DecompilerHelper.getDecompiler(unit);
        if ((decompiler == null) && ((!(unit.getParent() instanceof IUnit)) || (!unit.getParent().getName().equals("decompiler")))) {
            StringBuilder msg = new StringBuilder("Your build does not provide decompilation support for this type of code.\n\nThe decompilers available with your license type are:\n");
            msg.append(Strings.join(", ", DecompilerHelper.getAvailableDecompilerNames(this.context.getEnginesContext())));
            if (!UI.popupOptional(this.shell, 0, "Decompiler not available", msg.toString(), "dlgDecompilerNotAvailable")) {
                logger.warn("No decompiler available");
            }
            return;
        }
        logger.info("decompiler= %s", decompiler);
        PartManager partManager = this.context.getPartManager();
        IMPart targetPart;
        GlobalPosition globalPosition = this.context.getViewManager().getCurrentGlobalPosition();
        if (decompiler != null) {
            IUnit iUnit;
            try {
                iUnit = decompiler.getDecompiledUnit(address);
            } catch (ClassCastException e) {
                MessageDialog.openError(this.shell, S.s(304), "It seems that your JDB2 database contains inconsistencies, which makes it incompatible with this version of JEB.\n\nWe apologize for this inconvenience.");
                if (this.context.isDevelopmentMode()) {
                    logger.catching(e);
                }
                return;
            }
            if (iUnit == null) {
                logger.info("Decompiling at %s", address);
                iUnit = HandlerUtil.decompileAsync(this.shell, this.context, decompiler, address);
                if (iUnit == null) {
                    return;
                }
            }
            List<UnitPartManager> targetParts = partManager.getPartManagersForUnit(iUnit);
            if (targetParts.isEmpty()) {
                targetPart = partManager.create(iUnit, true).get(0);
                partManager.setOriginator(targetPart, this.part);
            } else {
                targetPart = partManager.getFirstPartForUnit(iUnit);
                partManager.setOriginator(targetPart, this.part);
                partManager.focus(targetPart);
            }
        } else {
            IUnitCreator parent = unit.getParent();
            if (!(parent instanceof IUnit)) {
                return;
            }
            parent = parent.getParent();
            if (!(parent instanceof IUnit)) {
                return;
            }
            IUnit disassembler = (IUnit) parent;
            List<IMPart> potentialOriginParts = partManager.getPartsForUnit(disassembler);
            if (potentialOriginParts.isEmpty()) {
                targetPart = partManager.create(disassembler, true).get(0);
            } else {
                targetPart = findFirstPartWithTextFragment(partManager, potentialOriginParts);
                if (targetPart != null) {
                    partManager.setOriginator(targetPart, this.part);
                    partManager.focus(targetPart);
                } else {
                    targetPart = partManager.create(disassembler, false).get(0);
                }
            }
        }
        if (targetPart != null) {
            UnitPartManager p = partManager.getUnitPartManager(targetPart);
            if (p != null) {
                p.setActiveAddress(address, null, false);
            }
        }
        if (globalPosition != null) {
            this.context.getViewManager().recordGlobalPosition(globalPosition);
        }
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


