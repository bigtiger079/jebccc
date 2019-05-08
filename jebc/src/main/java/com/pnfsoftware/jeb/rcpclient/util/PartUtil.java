package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.rcpclient.extensions.app.Folder;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Part;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;

import java.util.ArrayDeque;
import java.util.Deque;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Control;

public class PartUtil {
    public static IMPart getPart(Control ctl) {
        Deque<Control> stk = new ArrayDeque(16);
        while (!(ctl instanceof Folder)) {
            if (ctl == null) {
                return null;
            }
            stk.push(ctl);
            ctl = ctl.getParent();
        }
        if (stk.size() < 2) {
            return null;
        }
        Folder folder = (Folder) ctl;
        Control e1 = (Control) stk.pop();
        if (!(e1 instanceof CTabFolder)) {
            throw new RuntimeException();
        }
        Control e2 = (Control) stk.pop();
        Part part = folder.getPartByControl(e2);
        if (part == null) {
            return null;
        }
        return part;
    }
}


