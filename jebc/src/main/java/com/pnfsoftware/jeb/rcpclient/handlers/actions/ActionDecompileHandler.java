/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.actions;
/*     */
/*     */

import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*     */ import com.pnfsoftware.jeb.core.IUnitCreator;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.util.DecompilerHelper;
/*     */ import com.pnfsoftware.jeb.rcpclient.GlobalPosition;
/*     */ import com.pnfsoftware.jeb.rcpclient.IViewManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.dialogs.MessageDialog;

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
/*     */ public class ActionDecompileHandler
        /*     */ extends JebBaseHandler
        /*     */ {
    /*  42 */   private static final ILogger logger = GlobalLog.getLogger(ActionDecompileHandler.class);

    /*     */
    /*     */
    public ActionDecompileHandler() {
        /*  45 */
        super("decompile", S.s(475), null, "eclipse/debugt_obj.png");
        /*  46 */
        setAccelerator(9);
        /*     */
    }

    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  51 */
        if (this.part == null) {
            /*  52 */
            return false;
            /*     */
        }
        /*  54 */
        Object object = this.part.getManager();
        /*  55 */
        if (isDisableHandlers(this.part)) {
            /*  56 */
            return false;
            /*     */
        }
        /*  58 */
        if ((object instanceof UnitPartManager)) {
            /*  59 */
            String address = ((UnitPartManager) object).getActiveAddress(AddressConversionPrecision.COARSE);
            /*  60 */
            if (address != null) {
                /*  61 */
                return true;
                /*     */
            }
            /*     */
        }
        /*  64 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public void execute()
    /*     */ {
        /*  69 */
        this.context.getTelemetry().record("actionDecompile");
        /*     */
        /*  71 */
        if (this.part == null) {
            /*  72 */
            return;
            /*     */
        }
        /*  74 */
        Object object = this.part.getManager();
        /*  75 */
        if (!(object instanceof UnitPartManager)) {
            /*  76 */
            return;
            /*     */
        }
        /*  78 */
        UnitPartManager unitPart = (UnitPartManager) object;
        /*  79 */
        IUnit unit = unitPart.getUnit();
        /*     */
        /*     */
        /*  82 */
        IRcpUnitFragment activeFragment = unitPart.getActiveFragment();
        /*  83 */
        if (activeFragment == null) {
            /*  84 */
            logger.i("No active view", new Object[0]);
            /*  85 */
            return;
            /*     */
        }
        /*     */
        /*  88 */
        String address = activeFragment.getActiveAddress();
        /*  89 */
        if (address == null) {
            /*  90 */
            logger.info("Cannot determine where to decompile", new Object[0]);
            /*  91 */
            return;
            /*     */
        }
        /*     */
        /*  94 */
        IDecompilerUnit decompiler = DecompilerHelper.getDecompiler(unit);
        /*  95 */
        if ((decompiler == null) && (
                /*  96 */       (!(unit.getParent() instanceof IUnit)) || (!((IUnit) unit.getParent()).getName().equals("decompiler")))) {
            /*  97 */
            StringBuilder msg = new StringBuilder("Your build does not provide decompilation support for this type of code.\n\nThe decompilers available with your license type are:\n");
            /*     */
            /*     */
            /* 100 */
            msg.append(Strings.join(", ", DecompilerHelper.getAvailableDecompilerNames(this.context.getEnginesContext())));
            /* 101 */
            if (!UI.popupOptional(this.shell, 0, "Decompiler not available", msg.toString(), "dlgDecompilerNotAvailable")) {
                /* 102 */
                logger.warn("No decompiler available", new Object[0]);
                /*     */
            }
            /* 104 */
            return;
            /*     */
        }
        /*     */
        /* 107 */
        logger.i("decompiler= %s", new Object[]{decompiler});
        /*     */
        /* 109 */
        PartManager pman = this.context.getPartManager();
        /* 110 */
        IMPart targetPart = null;
        /*     */
        /* 112 */
        GlobalPosition pos0 = this.context.getViewManager().getCurrentGlobalPosition();
        /*     */
        /*     */
        /* 115 */
        if (decompiler != null)
            /*     */ {
            /*     */
            try {
                /* 118 */
                c = decompiler.getDecompiledUnit(address);
                /*     */
            } catch (ClassCastException e) {
                /*     */
                IUnit c;
                /* 121 */
                MessageDialog.openError(this.shell, S.s(304), "It seems that your JDB2 database contains inconsistencies, which makes it incompatible with this version of JEB.\n\nWe apologize for this inconvenience.");
                /*     */
                /*     */
                /* 124 */
                if (this.context.isDevelopmentMode()) {
                    /* 125 */
                    logger.catching(e);
                    /*     */
                }
                /*     */
                return;
                /*     */
            }
            /*     */
            IUnit c;
            /* 130 */
            if (c == null)
                /*     */ {
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
                /* 143 */
                logger.info("Decompiling at %s", new Object[]{address});
                /* 144 */
                c = HandlerUtil.decompileAsync(this.shell, this.context, decompiler, address);
                /* 145 */
                if (c == null) {
                    /* 146 */
                    return;
                    /*     */
                }
                /*     */
            }
            /*     */
            /*     */
            /* 151 */
            List<UnitPartManager> targetParts = pman.getPartManagersForUnit(c);
            /* 152 */
            if (targetParts.isEmpty()) {
                /* 153 */
                targetPart = (IMPart) pman.create(c, true).get(0);
                /* 154 */
                pman.setOriginator(targetPart, this.part);
                /*     */
            }
            /*     */
            else {
                /* 157 */
                targetPart = pman.getFirstPartForUnit(c);
                /* 158 */
                pman.setOriginator(targetPart, this.part);
                /* 159 */
                pman.focus(targetPart);
                /*     */
            }
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 164 */
            IUnitCreator parent = unit.getParent();
            /* 165 */
            if (!(parent instanceof IUnit)) {
                /* 166 */
                return;
                /*     */
            }
            /* 168 */
            parent = ((IUnit) parent).getParent();
            /* 169 */
            if (!(parent instanceof IUnit)) {
                /* 170 */
                return;
                /*     */
            }
            /* 172 */
            IUnit disassembler = (IUnit) parent;
            /*     */
            /* 174 */
            List<IMPart> potentialOriginParts = pman.getPartsForUnit(disassembler);
            /* 175 */
            if (potentialOriginParts.isEmpty()) {
                /* 176 */
                targetPart = (IMPart) pman.create(disassembler, true).get(0);
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
            }
            /*     */
            else
                /*     */ {
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /*     */
                /* 195 */
                targetPart = findFirstPartWithTextFragment(pman, potentialOriginParts);
                /* 196 */
                if (targetPart != null) {
                    /* 197 */
                    pman.setOriginator(targetPart, this.part);
                    /* 198 */
                    pman.focus(targetPart);
                    /*     */
                }
                /*     */
                else
                    /*     */ {
                    /* 202 */
                    targetPart = (IMPart) pman.create(disassembler, false).get(0);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 209 */
        if (targetPart != null) {
            /* 210 */
            UnitPartManager p = pman.getUnitPartManager(targetPart);
            /* 211 */
            if (p != null) {
                /* 212 */
                p.setActiveAddress(address, null, false);
                /*     */
            }
            /*     */
        }
        /*     */
        /* 216 */
        if (pos0 != null) {
            /* 217 */
            this.context.getViewManager().recordGlobalPosition(pos0);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private IMPart findFirstPartWithTextFragment(PartManager pman, List<IMPart> parts) {
        /* 222 */
        for (IMPart part : parts) {
            /* 223 */
            UnitPartManager p = pman.getUnitPartManager(part);
            /* 224 */
            if (!Lists.newArrayList(Iterables.filter(p.getFragments(), InteractiveTextView.class)).isEmpty()) {
                /* 225 */
                return part;
                /*     */
            }
            /*     */
        }
        /* 228 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\actions\ActionDecompileHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */