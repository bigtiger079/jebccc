/*    */
package com.pnfsoftware.jeb.rcpclient.util;
/*    */
/*    */

import com.pnfsoftware.jeb.rcpclient.extensions.app.Folder;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.Part;
/*    */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*    */ import java.util.ArrayDeque;
/*    */ import java.util.Deque;
/*    */ import org.eclipse.swt.custom.CTabFolder;
/*    */ import org.eclipse.swt.widgets.Control;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class PartUtil
        /*    */ {
    /*    */
    public static IMPart getPart(Control ctl)
    /*    */ {
        /* 33 */
        Deque<Control> stk = new ArrayDeque(16);
        /* 34 */
        while (!(ctl instanceof Folder)) {
            /* 35 */
            if (ctl == null) {
                /* 36 */
                return null;
                /*    */
            }
            /* 38 */
            stk.push(ctl);
            /* 39 */
            ctl = ctl.getParent();
            /*    */
        }
        /* 41 */
        if (stk.size() < 2) {
            /* 42 */
            return null;
            /*    */
        }
        /*    */
        /* 45 */
        Folder folder = (Folder) ctl;
        /* 46 */
        Control e1 = (Control) stk.pop();
        /* 47 */
        if (!(e1 instanceof CTabFolder)) {
            /* 48 */
            throw new RuntimeException();
            /*    */
        }
        /*    */
        /* 51 */
        Control e2 = (Control) stk.pop();
        /* 52 */
        Part part = folder.getPartByControl(e2);
        /* 53 */
        if (part == null)
            /*    */ {
            /* 55 */
            return null;
            /*    */
        }
        /*    */
        /* 58 */
        return part;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\PartUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */