
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;


import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.DebuggerListener;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractTextView;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class DbgLogView
        extends AbstractTextView<IUnit> {

    public DbgLogView(Composite parent, int flags, RcpClientContext context, IDebuggerUnit unit) {

        super(parent, flags, context, unit);

        setLayout(new FillLayout());


        DebuggerListener listener = DebuggerListener.listenTo(unit, context);

        Document doc = listener.getLog();


        final TextViewer viewer = buildSimple(this, 768);

        viewer.setDocument(doc);


        Control ctl = viewer.getControl();

        ctl.setBackground(UIAssetManager.getInstance().getColor(15790320));

        ctl.setFont(context.getFontManager().getCodeFont());


        viewer.addTextListener(new ITextListener() {

            public void textChanged(TextEvent event) {

                viewer.setTopIndex(viewer.getTextWidget().getLineCount());

            }


        });

        new ContextMenu(viewer.getControl()).addContextMenu(new IContextMenu() {

            public void fillContextMenu(IMenuManager menuMgr) {

                DbgLogView.this.addOperationsToContextMenu(menuMgr);

            }

        });

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgLogView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */