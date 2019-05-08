package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.ViewerRefresher;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class DescriptionView extends AbstractTextView<IUnit> implements IContextMenu {
    private ViewerRefresher refresher;
    private IEventListener unitListener;

    public DescriptionView(Composite parent, int flags, RcpClientContext context, final IUnit unit) {
        super(parent, flags, context, unit);
        setLayout(new FillLayout());
        String description = unit.getDescription();
        final Document doc = new Document(description);
        TextViewer viewer = buildSimple(this, 768);
        this.refresher = new ViewerRefresher(parent.getDisplay(), viewer) {
            protected void performRefresh() {
                doc.set(unit.getDescription());
            }
        };
        unit.addListener(this.unitListener = new IEventListener() {
            public void onEvent(IEvent e) {
                if (J.isUnitEvent(e)) {
                    DescriptionView.this.refresher.request();
                }
            }
        });
        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                unit.removeListener(DescriptionView.this.unitListener);
            }
        });
        viewer.setDocument(doc);
        Control ctl = viewer.getControl();
        context.getFontManager().registerWidget(ctl);
        new ContextMenu(viewer.getControl()).addContextMenu(this);
    }

    public void fillContextMenu(IMenuManager menuMgr) {
        addOperationsToContextMenu(menuMgr);
    }

    public boolean verifyOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case REFRESH:
                return true;
        }
        return super.verifyOperation(req);
    }

    public boolean doOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case REFRESH:
                this.refresher.request();
                return true;
        }
        return super.doOperation(req);
    }
}


