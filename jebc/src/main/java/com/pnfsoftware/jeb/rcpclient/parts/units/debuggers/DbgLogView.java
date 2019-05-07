/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.IUnit;
/*    */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*    */ import com.pnfsoftware.jeb.rcpclient.DebuggerListener;
/*    */ import com.pnfsoftware.jeb.rcpclient.FontManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*    */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*    */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*    */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*    */ import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractTextView;
/*    */ import org.eclipse.jface.action.IMenuManager;
/*    */ import org.eclipse.jface.text.Document;
/*    */ import org.eclipse.jface.text.ITextListener;
/*    */ import org.eclipse.jface.text.TextEvent;
/*    */ import org.eclipse.jface.text.TextViewer;
/*    */ import org.eclipse.swt.custom.StyledText;
/*    */ import org.eclipse.swt.layout.FillLayout;
/*    */ import org.eclipse.swt.widgets.Composite;
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
/*    */ public class DbgLogView
        /*    */ extends AbstractTextView<IUnit>
        /*    */ {
    /*    */
    public DbgLogView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit)
    /*    */ {
        /* 38 */
        super(parent, flags, context, unit);
        /* 39 */
        setLayout(new FillLayout());
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
        /* 72 */
        DebuggerListener listener = DebuggerListener.listenTo(unit, context);
        /* 73 */
        Document doc = listener.getLog();
        /*    */
        /* 75 */
        final TextViewer viewer = buildSimple(this, 768);
        /* 76 */
        viewer.setDocument(doc);
        /*    */
        /*    */
        /* 79 */
        Control ctl = viewer.getControl();
        /* 80 */
        ctl.setBackground(UIAssetManager.getInstance().getColor(15790320));
        /* 81 */
        ctl.setFont(context.getFontManager().getCodeFont());
        /*    */
        /*    */
        /* 84 */
        viewer.addTextListener(new ITextListener()
                /*    */ {
            /*    */
            public void textChanged(TextEvent event) {
                /* 87 */
                viewer.setTopIndex(viewer.getTextWidget().getLineCount());
                /*    */
            }
            /*    */
            /*    */
            /* 91 */
        });
        /* 92 */
        new ContextMenu(viewer.getControl()).addContextMenu(new IContextMenu()
                /*    */ {
            /*    */
            public void fillContextMenu(IMenuManager menuMgr) {
                /* 95 */
                DbgLogView.this.addOperationsToContextMenu(menuMgr);
                /*    */
            }
            /*    */
        });
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgLogView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */