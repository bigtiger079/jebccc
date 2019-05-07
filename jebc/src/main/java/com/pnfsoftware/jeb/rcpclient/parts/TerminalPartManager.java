
package com.pnfsoftware.jeb.rcpclient.parts;


import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.core.util.DebuggerHelper;
import com.pnfsoftware.jeb.rcpclient.AllHandlers;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.ConsoleViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditClearHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindnextHandler;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;


public class TerminalPartManager
        extends AbstractPartManager
        implements IOperable, IContextMenu {
    private static final ILogger logger = GlobalLog.getLogger(TerminalPartManager.class);
    private ConsoleViewer cv;
    private Listener focusInFilter;


    public TerminalPartManager(RcpClientContext context) {

        super(context);

    }


    public void createView(Composite parent, IMPart tab) {

        parent.setLayout(new FillLayout());


        this.cv = new ConsoleViewer(parent, 0);


        final MultiInterpreter mi = this.context.getMasterInterpreter();

        this.cv.setInterpreter(mi);


        mi.addListener(new IEventListener() {

            public void onEvent(IEvent e) {

                if (((com.pnfsoftware.jeb.util.events.Event) e).getType().intValue() == 0) {

                    TerminalPartManager.logger.i("EVENT_INTERPRETER_CHANGE", new Object[0]);

                    TerminalPartManager.this.cv.updatePromptAfterCommand(mi.getName() + "> ");

                } else if (((com.pnfsoftware.jeb.util.events.Event) e).getType().intValue() == 1) {

                    TerminalPartManager.logger.i("EVENT_INTERPRETER_CHANGE_IMMEDIATE", new Object[0]);

                    TerminalPartManager.this.cv.updatePrompt(mi.getName() + "> ");

                }


            }

        });

        Control ctl = this.cv.getControl();

        this.context.getFontManager().registerWidget(ctl);


        this.focusInFilter = new Listener() {

            public void handleEvent(org.eclipse.swt.widgets.Event event) {

                PartManager pman = TerminalPartManager.this.context.getPartManager();

                IMPart part = pman.getActivePart();

                if (part != null) {

                    IUnit unit = pman.getUnitForPart(part);


                    if (TerminalPartManager.this.focusChanged(unit)) {

                        mi.notifyListeners(new com.pnfsoftware.jeb.util.events.Event(1));

                    }

                }

            }

        };

        this.context.getDisplay().addFilter(15, this.focusInFilter);


        new ContextMenu(this.cv.getControl()).addContextMenu(this);

    }


    public void fillContextMenu(IMenuManager menuMgr) {

        AllHandlers.getInstance().fillManager(menuMgr, 1);

        menuMgr.add(new Separator());

        menuMgr.add(new EditFindHandler());

        menuMgr.add(new EditFindnextHandler());

        menuMgr.add(new Separator());

        menuMgr.add(new EditClearHandler());

    }


    private boolean focusChanged(IUnit unit) {

        if (unit == null) {

            return false;

        }


        if ((unit instanceof ICodeUnit)) {

            IDebuggerUnit dbgUnit = DebuggerHelper.getDebuggerForUnit(this.context.getOpenedProject(), (ICodeUnit) unit);

            if (dbgUnit != null) {

                unit = dbgUnit;

            }

        }

        if ((this.cv.getInterpreter() instanceof MultiInterpreter)) {

            return ((MultiInterpreter) this.cv.getInterpreter()).onFocusChanged(unit);

        }

        return false;

    }


    public void deleteView() {

        if (this.focusInFilter != null) {

            this.context.getDisplay().removeFilter(15, this.focusInFilter);

        }

    }


    public ConsoleViewer getConsoleViewer() {

        return this.cv;

    }


    public void setFocus() {

        this.cv.getControl().setFocus();

    }


    public boolean verifyOperation(OperationRequest req) {

        return this.cv.verifyOperation(req);

    }


    public boolean doOperation(OperationRequest req) {

        return this.cv.doOperation(req);

    }

}


