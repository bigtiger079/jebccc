
package com.pnfsoftware.jeb.rcpclient.handlers.windows;


import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;

import java.util.List;

import org.eclipse.swt.SWT;


public class WindowFocusHierarchyHandler
        extends JebBaseHandler {

    public WindowFocusHierarchyHandler() {

        super("showCurrentCodeHierarchy", "Show Current Code Hierarchy View", null, null);

        setAccelerator(SWT.MOD1 | 0x34);

    }


    public boolean canExecute() {

        return true;

    }


    public void execute() {

        PartManager pman = this.context.getPartManager();


        IUnit unit = pman.getUnitForPart(this.part);

        if (attemptFocus(pman, unit)) {

            return;

        }


        List<IMPart> parts = pman.getUnitParts();


        for (IMPart p : parts) {

            unit = pman.getUnitForPart(p);

            if (attemptFocus(pman, unit)) {

                return;

            }

        }

    }


    private boolean attemptFocus(PartManager pman, IUnit unit) {

        if (unit != null) {

            for (IMPart part : pman.getPartsForUnit(unit)) {

                UnitPartManager manager = (UnitPartManager) part.getManager();

                CodeHierarchyView fragment = (CodeHierarchyView) manager.getFragmentByType(CodeHierarchyView.class);

                if (fragment != null) {

                    pman.activatePart(part, true);

                    manager.setActiveFragment(fragment);

                    return true;

                }

            }

        }

        return false;

    }

}


