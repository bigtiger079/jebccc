/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.UnitAddress;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.android.IApkUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
/*     */ import com.pnfsoftware.jeb.util.base.Couple;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.TreeMap;
/*     */ import org.eclipse.jface.viewers.DoubleClickEvent;
/*     */ import org.eclipse.jface.viewers.IDoubleClickListener;
/*     */ import org.eclipse.jface.viewers.ISelectionChangedListener;
/*     */ import org.eclipse.jface.viewers.SelectionChangedEvent;
/*     */ import org.eclipse.jface.viewers.StructuredViewer;

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
/*     */ public class AddressNavigator
        /*     */ {
    /*     */   private RcpClientContext context;
    /*     */   private StructuredViewer viewer;
    /*     */   private IRcpUnitFragment nav;
    /*     */   private IUnit managedUnit;

    /*     */
    /*     */
    public AddressNavigator(RcpClientContext context, StructuredViewer viewer, IRcpUnitFragment nav, IUnit managedUnit)
    /*     */ {
        /*  47 */
        this.context = context;
        /*  48 */
        this.viewer = viewer;
        /*  49 */
        this.nav = nav;
        /*  50 */
        this.managedUnit = managedUnit;
        /*  51 */
        setup();
        /*     */
    }

    /*     */
    /*     */
    private void setup() {
        /*  55 */
        if (this.viewer != null) {
            /*  56 */
            this.viewer.addDoubleClickListener(new IDoubleClickListener()
                    /*     */ {
                /*     */
                public void doubleClick(DoubleClickEvent event) {
                    /*  59 */
                    AddressNavigator.this.navigate(null);
                    /*     */
                }
                /*  61 */
            });
            /*  62 */
            this.viewer.addSelectionChangedListener(new ISelectionChangedListener()
                    /*     */ {
                /*     */
                public void selectionChanged(SelectionChangedEvent event) {
                    /*  65 */
                    AddressNavigator.this.context.refreshHandlersStates();
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void forceNavigation(String address) {
        /*  72 */
        navigate(address);
        /*     */
    }

    /*     */
    /*     */
    private void navigate(String address) {
        /*  76 */
        if (address == null) {
            /*  77 */
            address = this.nav.getActiveAddress();
            /*  78 */
            if (address == null) {
                /*  79 */
                return;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*  84 */
        PartManager pman = this.context.getPartManager();
        /*     */
        /*     */
        /*     */
        /*  88 */
        TreeMap<Integer, List<Couple<IMPart, IRcpUnitFragment>>> toFocus = new TreeMap();
        /*     */
        /*     */
        /*     */
        /*     */
        /*  93 */
        List<IMPart> parts = pman.getPartsForUnit(this.managedUnit, 1);
        /*  94 */
        for (IMPart part : parts) {
            /*  95 */
            UnitPartManager object = pman.getUnitPartManager(part);
            /*  96 */
            if (object != null) {
                /*  97 */
                IRcpUnitFragment fragment = object.getActiveFragment();
                /*  98 */
                if ((fragment != null) && (fragment != this.nav) && (fragment.getFocusPriority() != 0))
                    /*     */ {
                    /*     */
                    /*     */
                    /* 102 */
                    if (isValidAddress(fragment, address)) {
                        /* 103 */
                        add(toFocus, new Couple(part, fragment));
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 108 */
        if (!toFocus.isEmpty()) {
            /* 109 */
            if (focus(pman, toFocus, address, false)) {
                /* 110 */
                return;
                /*     */
            }
            /* 112 */
            toFocus.clear();
            /*     */
        }
        /*     */
        /*     */
        /* 116 */
        parts = pman.getPartsForUnitFamily(this.managedUnit, 1);
        /* 117 */
        for (IMPart part : parts) {
            /* 118 */
            UnitPartManager object = pman.getUnitPartManager(part);
            /* 119 */
            if (object != null) {
                /* 120 */
                IRcpUnitFragment fragment = object.getActiveFragment();
                /* 121 */
                if ((fragment != null) && (fragment != this.nav) && (object.getUnit() != this.managedUnit) &&
                        /* 122 */           (fragment.getFocusPriority() != 0))
                    /*     */ {
                    /*     */
                    /*     */
                    /* 126 */
                    if (isValidAddress(fragment, address)) {
                        /* 127 */
                        add(toFocus, new Couple(part, fragment));
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 132 */
        if (!toFocus.isEmpty()) {
            /* 133 */
            if (focus(pman, toFocus, address, false)) {
                /* 134 */
                return;
                /*     */
            }
            /* 136 */
            toFocus.clear();
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 141 */
        for (??? =parts.iterator(); ???.hasNext();){
            part = (IMPart) ???.next();
            /* 142 */
            UnitPartManager object = pman.getUnitPartManager(part);
            /* 143 */
            if (object != null) {
                /* 144 */
                activeFragment = object.getActiveFragment();
                /*     */
                /* 146 */
                List<IRcpUnitFragment> fragments = getFragmentsDefaultFirst(object);
                /* 147 */
                for (localIterator2 = fragments.iterator(); localIterator2.hasNext(); ) {
                    fragment = (IRcpUnitFragment) localIterator2.next();
                    /* 148 */
                    if ((fragment != null) && (fragment != this.nav) && (fragment != activeFragment) &&
                            /* 149 */             (fragment.getFocusPriority() != 0))
                        /*     */ {
                        /*     */
                        /*     */
                        /* 153 */
                        if (isValidAddress(fragment, address))
                            /* 154 */ add(toFocus, new Couple(part, fragment));
                    }
                    /*     */
                }
            }
        }
        /*     */
        IRcpUnitFragment activeFragment;
        /*     */
        Iterator localIterator2;
        /*     */
        IRcpUnitFragment fragment;
        /* 159 */
        if (!toFocus.isEmpty()) {
            /* 160 */
            if (focus(pman, toFocus, address, true)) {
                /* 161 */
                return;
                /*     */
            }
            /* 163 */
            toFocus.clear();
            /*     */
        }
        /*     */
        /*     */
        /* 167 */
        parts = pman.getPartsForUnitFamily(this.managedUnit, 2);
        /*     */
        /* 169 */
        parts.addAll(0, pman.restoreMissingParts(this.managedUnit));
        /*     */
        /* 171 */
        IUnit mainUnit = getMainUnitForAddress(address);
        /* 172 */
        if (mainUnit != null) {
            /* 173 */
            parts.addAll(0, pman.restoreMissingParts(mainUnit));
            /*     */
        }
        /*     */
        /* 176 */
        for (IMPart part = parts.iterator(); part.hasNext(); ) {
            part = (IMPart) part.next();
            /* 177 */
            UnitPartManager object = pman.getUnitPartManager(part);
            /* 178 */
            if (object != null)
                /*     */ {
                /* 180 */
                selectedFragment = object.getActiveFragment();
                /* 181 */
                if ((selectedFragment != null) && (selectedFragment != this.nav) &&
                        /* 182 */           (isValidAddress(selectedFragment, address))) {
                    /* 183 */
                    add(toFocus, new Couple(part, selectedFragment));
                    /*     */
                }
                /*     */
                /*     */
                /*     */
                /* 188 */
                Object fragments = getFragmentsDefaultFirst(object);
                /* 189 */
                for (IRcpUnitFragment fragment : (List) fragments)
                    /* 190 */
                    if ((fragment != null) && (fragment != this.nav) && (fragment != selectedFragment) &&
                            /* 191 */             (fragment.getFocusPriority() != 0))
                        /*     */ {
                        /*     */
                        /*     */
                        /* 195 */
                        if (isValidAddress(fragment, address))
                            /* 196 */ add(toFocus, new Couple(part, fragment));
                    }
                /*     */
            }
            /*     */
        }
        /*     */
        IMPart part;
        /*     */
        IRcpUnitFragment selectedFragment;
        /* 201 */
        if (!toFocus.isEmpty()) {
            /* 202 */
            focus(pman, toFocus, address, true);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void add(TreeMap<Integer, List<Couple<IMPart, IRcpUnitFragment>>> toFocus, Couple<IMPart, IRcpUnitFragment> couple)
    /*     */ {
        /* 208 */
        int priority = ((IRcpUnitFragment) couple.getSecond()).getFocusPriority();
        /* 209 */
        List<Couple<IMPart, IRcpUnitFragment>> list = (List) toFocus.get(Integer.valueOf(priority));
        /* 210 */
        if (list == null) {
            /* 211 */
            list = new ArrayList();
            /* 212 */
            toFocus.put(Integer.valueOf(priority), list);
            /*     */
        }
        /* 214 */
        list.add(couple);
        /*     */
    }

    /*     */
    /*     */
    private boolean isValidAddress(IRcpUnitFragment fragment, String address) {
        /* 218 */
        return fragment.isValidActiveAddress(address, null);
        /*     */
    }

    /*     */
    /*     */
    private boolean focus(PartManager pman, TreeMap<Integer, List<Couple<IMPart, IRcpUnitFragment>>> toFocus, String address, boolean setActiveFragment)
    /*     */ {
        /* 223 */
        IMPart activePart0 = pman.getActivePart();
        /* 224 */
        for (Map.Entry<Integer, List<Couple<IMPart, IRcpUnitFragment>>> e : toFocus.descendingMap().entrySet()) {
            /* 225 */
            for (Couple<IMPart, IRcpUnitFragment> e2 : (List) e.getValue()) {
                /* 226 */
                IMPart part = (IMPart) e2.getFirst();
                /* 227 */
                IRcpUnitFragment fragment = (IRcpUnitFragment) e2.getSecond();
                /* 228 */
                if (fragment.setActiveAddress(address, null, false)) {
                    /* 229 */
                    if (pman.getActivePart() == activePart0) {
                        /* 230 */
                        pman.focus(part);
                        /*     */
                    }
                    /* 232 */
                    if (setActiveFragment) {
                        /* 233 */
                        pman.getUnitPartManager(part).setActiveFragment(fragment);
                        /*     */
                    }
                    /* 235 */
                    return true;
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 239 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private List<IRcpUnitFragment> getFragmentsDefaultFirst(UnitPartManager object) {
        /* 243 */
        List<IRcpUnitFragment> fragments = object.getFragments();
        /*     */
        /* 245 */
        for (int i = 0; i < fragments.size(); i++) {
            /* 246 */
            if (((IRcpUnitFragment) fragments.get(i)).isDefaultFragment()) {
                /* 247 */
                fragments.add(0, fragments.remove(i));
                /* 248 */
                break;
                /*     */
            }
            /*     */
        }
        /* 251 */
        return fragments;
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
    /*     */
    /*     */
    private IUnit getMainUnitForAddress(String address)
    /*     */ {
        /* 264 */
        if ((this.managedUnit instanceof IApkUnit)) {
            /* 265 */
            IApkUnit apkUnit = (IApkUnit) this.managedUnit;
            /*     */
            /* 267 */
            for (IUnit child : apkUnit.getChildren()) {
                /* 268 */
                if ((child instanceof IDebuggerUnit))
                    /*     */ {
                    /* 270 */
                    newAddress = ((IDebuggerUnit) child).convertToUnitAddress(address);
                    /* 271 */
                    if (newAddress != null)
                        /*     */ {
                        /* 273 */
                        return newAddress.getUnit();
                        /*     */
                    }
                    /*     */
                    /* 276 */
                    for (IUnit childDbg : child.getChildren()) {
                        /* 277 */
                        if ((childDbg instanceof IDebuggerUnit))
                            /*     */ {
                            /* 279 */
                            newAddress = ((IDebuggerUnit) childDbg).convertToUnitAddress(address);
                            /* 280 */
                            if (newAddress != null)
                                /*     */ {
                                /* 282 */
                                return newAddress.getUnit();
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        UnitAddress<ICodeUnit> newAddress;
        /* 291 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\AddressNavigator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */