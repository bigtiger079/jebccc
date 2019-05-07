/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.pnfsoftware.jeb.core.output.IActionableItem;
/*     */ import com.pnfsoftware.jeb.core.units.IAddressableUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
/*     */ import com.pnfsoftware.jeb.core.util.DecompilerHelper;
/*     */ import com.pnfsoftware.jeb.rcpclient.IViewManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.HandlerUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Shell;

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
/*     */ public class DecompiledViewNavigator
        /*     */ implements IViewNavigator
        /*     */ {
    /*  38 */   private static final ILogger logger = GlobalLog.getLogger(DecompiledViewNavigator.class);
    /*     */ IAddressableUnit unit;
    /*     */ RcpClientContext context;
    /*     */ IMPart currentPart;

    /*     */
    /*     */
    public DecompiledViewNavigator(IAddressableUnit unit, RcpClientContext context, IMPart currentPart)
    /*     */ {
        /*  45 */
        this.unit = unit;
        /*  46 */
        this.context = context;
        /*  47 */
        this.currentPart = currentPart;
        /*     */
    }

    /*     */
    /*     */
    public boolean canHandleAddress(String address)
    /*     */ {
        /*  52 */
        return this.unit.isValidAddress(address);
        /*     */
    }

    /*     */
    /*     */
    public boolean navigateTo(IActionableItem item, IViewManager viewManager, boolean record)
    /*     */ {
        /*  57 */
        long itemId = item.getItemId();
        /*  58 */
        if (itemId == 0L) {
            /*  59 */
            return false;
            /*     */
        }
        /*     */
        /*  62 */
        String address = this.unit.getAddressOfItem(itemId);
        /*  63 */
        if (address == null) {
            /*  64 */
            return false;
            /*     */
        }
        /*     */
        /*  67 */
        return navigateTo(address, viewManager, record);
        /*     */
    }

    /*     */
    /*     */
    public boolean navigateTo(String address, IViewManager viewManager, boolean record)
    /*     */ {
        /*  72 */
        if ((address == null) || (viewManager == null)) {
            /*  73 */
            return false;
            /*     */
        }
        /*     */
        /*     */
        /*  77 */
        PartManager pman = (PartManager) viewManager;
        /*     */
        /*  79 */
        IDecompilerUnit decompiler = DecompilerHelper.getRelatedDecompiler(this.unit);
        /*  80 */
        if (decompiler == null) {
            /*  81 */
            return false;
            /*     */
        }
        /*     */
        /*  84 */
        ICodeUnit code = decompiler.getCodeUnit();
        /*  85 */
        if (code == null) {
            /*  86 */
            return false;
            /*     */
        }
        /*  88 */
        if (!code.isValidAddress(address)) {
            /*  89 */
            return false;
            /*     */
        }
        /*     */
        /*  92 */
        IUnit c = decompiler.getDecompiledUnit(address);
        /*  93 */
        if (c == null) {
            /*  94 */
            Shell shell = UI.getShellTracker().get();
            /*     */
            /*     */
            /*     */
            /*  98 */
            c = HandlerUtil.decompileAsync(shell, this.context, decompiler, address);
            /*     */
            /* 100 */
            if (c == null)
                /*     */ {
                /* 102 */
                c = code;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 107 */
        IMPart targetPart = null;
        /* 108 */
        List<IMPart> potentialOriginParts = pman.getPartsForUnit(c);
        /* 109 */
        if (potentialOriginParts.isEmpty()) {
            /* 110 */
            targetPart = (IMPart) pman.create(c, true).get(0);
            /* 111 */
            pman.setOriginator(targetPart, this.currentPart);
            /*     */
        }
        /*     */
        else {
            /* 114 */
            targetPart = pman.selectWithOriginatorDeep(potentialOriginParts, this.currentPart);
            /* 115 */
            if (targetPart != null) {
                /* 116 */
                pman.focus(targetPart);
                /*     */
            }
            /*     */
            else {
                /* 119 */
                targetPart = findFirstPartWithTextFragment(pman, potentialOriginParts);
                /* 120 */
                if (targetPart != null) {
                    /* 121 */
                    pman.setOriginator(targetPart, this.currentPart);
                    /* 122 */
                    pman.focus(targetPart);
                    /*     */
                }
                /*     */
                else
                    /*     */ {
                    /* 126 */
                    targetPart = (IMPart) pman.create(c, false).get(0);
                    /* 127 */
                    pman.setOriginator(targetPart, this.currentPart);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /* 133 */
        if (targetPart == null) {
            /* 134 */
            return false;
            /*     */
        }
        /*     */
        /* 137 */
        UnitPartManager p = pman.getUnitPartManager(targetPart);
        /* 138 */
        if ((p != null) && (p.getUnit() != this.unit)) {
            /* 139 */
            return p.setActiveAddress(address, null, record);
            /*     */
        }
        /* 141 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    private IMPart findFirstPartWithTextFragment(PartManager pman, List<IMPart> parts) {
        /* 145 */
        for (IMPart part : parts) {
            /* 146 */
            UnitPartManager p = pman.getUnitPartManager(part);
            /* 147 */
            if (!Lists.newArrayList(Iterables.filter(p.getFragments(), InteractiveTextView.class)).isEmpty()) {
                /* 148 */
                return part;
                /*     */
            }
            /*     */
        }
        /* 151 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\DecompiledViewNavigator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */