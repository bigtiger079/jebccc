package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.UnitAddress;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.android.IApkUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
import com.pnfsoftware.jeb.util.base.Couple;

import java.util.*;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;

public class AddressNavigator {
    private RcpClientContext context;
    private StructuredViewer viewer;
    private IRcpUnitFragment nav;
    private IUnit managedUnit;

    public AddressNavigator(RcpClientContext context, StructuredViewer viewer, IRcpUnitFragment nav, IUnit managedUnit) {
        this.context = context;
        this.viewer = viewer;
        this.nav = nav;
        this.managedUnit = managedUnit;
        setup();
    }

    private void setup() {
        if (this.viewer != null) {
            this.viewer.addDoubleClickListener(new IDoubleClickListener() {
                public void doubleClick(DoubleClickEvent event) {
                    AddressNavigator.this.navigate(null);
                }
            });
            this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
                public void selectionChanged(SelectionChangedEvent event) {
                    AddressNavigator.this.context.refreshHandlersStates();
                }
            });
        }
    }

    public void forceNavigation(String address) {
        navigate(address);
    }

    private void navigate(String address) {
        if (address == null) {
            address = this.nav.getActiveAddress();
            if (address == null) {
                return;
            }
        }
        PartManager pman = this.context.getPartManager();
        TreeMap<Integer, List<Couple<IMPart, IRcpUnitFragment>>> toFocus = new TreeMap();
        List<IMPart> parts = pman.getPartsForUnit(this.managedUnit, 1);
        for (IMPart part : parts) {
            UnitPartManager object = pman.getUnitPartManager(part);
            if (object != null) {
                IRcpUnitFragment fragment = object.getActiveFragment();
                if ((fragment != null) && (fragment != this.nav) && (fragment.getFocusPriority() != 0)) {
                    if (isValidAddress(fragment, address)) {
                        add(toFocus, new Couple(part, fragment));
                    }
                }
            }
        }
        if (!toFocus.isEmpty()) {
            if (focus(pman, toFocus, address, false)) {
                return;
            }
            toFocus.clear();
        }
        parts = pman.getPartsForUnitFamily(this.managedUnit, 1);
        for (IMPart part : parts) {
            UnitPartManager object = pman.getUnitPartManager(part);
            if (object != null) {
                IRcpUnitFragment fragment = object.getActiveFragment();
                if ((fragment != null) && (fragment != this.nav) && (object.getUnit() != this.managedUnit) && (fragment.getFocusPriority() != 0)) {
                    if (isValidAddress(fragment, address)) {
                        add(toFocus, new Couple(part, fragment));
                    }
                }
            }
        }
        if (!toFocus.isEmpty()) {
            if (focus(pman, toFocus, address, false)) {
                return;
            }
            toFocus.clear();
        }
        IMPart part;
        IRcpUnitFragment activeFragment;
        Iterator localIterator2;
        IRcpUnitFragment fragment;
        IRcpUnitFragment selectedFragment;
        Iterator<IMPart> iterator = parts.iterator();
        while (iterator.hasNext()) {
            part = iterator.next();
            UnitPartManager object = pman.getUnitPartManager(part);
            if (object != null) {
                activeFragment = object.getActiveFragment();
                List<IRcpUnitFragment> fragments = getFragmentsDefaultFirst(object);
                for (localIterator2 = fragments.iterator(); localIterator2.hasNext(); ) {
                    fragment = (IRcpUnitFragment) localIterator2.next();
                    if ((fragment != null) && (fragment != this.nav) && (fragment != activeFragment) && (fragment.getFocusPriority() != 0)) {
                        if (isValidAddress(fragment, address)) add(toFocus, new Couple(part, fragment));
                    }
                }
            }
        }
        if (!toFocus.isEmpty()) {
            if (focus(pman, toFocus, address, true)) {
                return;
            }
            toFocus.clear();
        }
        parts = pman.getPartsForUnitFamily(this.managedUnit, 2);
        parts.addAll(0, pman.restoreMissingParts(this.managedUnit));
        IUnit mainUnit = getMainUnitForAddress(address);
        if (mainUnit != null) {
            parts.addAll(0, pman.restoreMissingParts(mainUnit));
        }
        iterator = parts.iterator();
        while (iterator.hasNext()) {
            part = iterator.next();
            UnitPartManager object = pman.getUnitPartManager(part);
            if (object != null) {
                selectedFragment = object.getActiveFragment();
                if ((selectedFragment != null) && (selectedFragment != this.nav) && (isValidAddress(selectedFragment, address))) {
                    add(toFocus, new Couple(part, selectedFragment));
                }
                List<IRcpUnitFragment> fragments = getFragmentsDefaultFirst(object);
                for (IRcpUnitFragment fra : fragments)
                    if ((fra != null) && (fra != this.nav) && (fra != selectedFragment) && (fra.getFocusPriority() != 0)) {
                        if (isValidAddress(fra, address)) add(toFocus, new Couple(part, fra));
                    }
            }
        }
        if (!toFocus.isEmpty()) {
            focus(pman, toFocus, address, true);
        }
    }

    private void add(TreeMap<Integer, List<Couple<IMPart, IRcpUnitFragment>>> toFocus, Couple<IMPart, IRcpUnitFragment> couple) {
        int priority = ((IRcpUnitFragment) couple.getSecond()).getFocusPriority();
        List<Couple<IMPart, IRcpUnitFragment>> list = (List) toFocus.get(Integer.valueOf(priority));
        if (list == null) {
            list = new ArrayList<>();
            toFocus.put(Integer.valueOf(priority), list);
        }
        list.add(couple);
    }

    private boolean isValidAddress(IRcpUnitFragment fragment, String address) {
        return fragment.isValidActiveAddress(address, null);
    }

    private boolean focus(PartManager pman, TreeMap<Integer, List<Couple<IMPart, IRcpUnitFragment>>> toFocus, String address, boolean setActiveFragment) {
        IMPart activePart0 = pman.getActivePart();
        for (Map.Entry<Integer, List<Couple<IMPart, IRcpUnitFragment>>> e : toFocus.descendingMap().entrySet()) {
            for (Couple<IMPart, IRcpUnitFragment> e2 : e.getValue()) {
                IMPart part = e2.getFirst();
                IRcpUnitFragment fragment = e2.getSecond();
                if (fragment.setActiveAddress(address, null, false)) {
                    if (pman.getActivePart() == activePart0) {
                        pman.focus(part);
                    }
                    if (setActiveFragment) {
                        pman.getUnitPartManager(part).setActiveFragment(fragment);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private List<IRcpUnitFragment> getFragmentsDefaultFirst(UnitPartManager object) {
        List<IRcpUnitFragment> fragments = object.getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            if (((IRcpUnitFragment) fragments.get(i)).isDefaultFragment()) {
                fragments.add(0, fragments.remove(i));
                break;
            }
        }
        return fragments;
    }

    private IUnit getMainUnitForAddress(String address) {
        if ((this.managedUnit instanceof IApkUnit)) {
            IApkUnit apkUnit = (IApkUnit) this.managedUnit;
            UnitAddress<ICodeUnit> newAddress;
            for (IUnit child : apkUnit.getChildren()) {
                if ((child instanceof IDebuggerUnit)) {
                    newAddress = ((IDebuggerUnit) child).convertToUnitAddress(address);
                    if (newAddress != null) {
                        return newAddress.getUnit();
                    }
                    for (IUnit childDbg : child.getChildren()) {
                        if ((childDbg instanceof IDebuggerUnit)) {
                            newAddress = ((IDebuggerUnit) childDbg).convertToUnitAddress(address);
                            if (newAddress != null) {
                                return newAddress.getUnit();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}


