/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.IOperable;
/*     */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.core.util.DebuggerHelper;
/*     */ import com.pnfsoftware.jeb.rcpclient.AllHandlers;
/*     */ import com.pnfsoftware.jeb.rcpclient.FontManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ConsoleViewer;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditClearHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindnextHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.action.Separator;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Listener;

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
/*     */ public class TerminalPartManager
        /*     */ extends AbstractPartManager
        /*     */ implements IOperable, IContextMenu
        /*     */ {
    /*  45 */   private static final ILogger logger = GlobalLog.getLogger(TerminalPartManager.class);
    /*     */   private ConsoleViewer cv;
    /*     */   private Listener focusInFilter;

    /*     */
    /*     */
    public TerminalPartManager(RcpClientContext context)
    /*     */ {
        /*  51 */
        super(context);
        /*     */
    }

    /*     */
    /*     */
    public void createView(Composite parent, IMPart tab)
    /*     */ {
        /*  56 */
        parent.setLayout(new FillLayout());
        /*     */
        /*     */
        /*  59 */
        this.cv = new ConsoleViewer(parent, 0);
        /*     */
        /*  61 */
        final MultiInterpreter mi = this.context.getMasterInterpreter();
        /*  62 */
        this.cv.setInterpreter(mi);
        /*     */
        /*     */
        /*  65 */
        mi.addListener(new IEventListener()
                /*     */ {
            /*     */
            public void onEvent(IEvent e) {
                /*  68 */
                if (((com.pnfsoftware.jeb.util.events.Event) e).getType().intValue() == 0) {
                    /*  69 */
                    TerminalPartManager.logger.i("EVENT_INTERPRETER_CHANGE", new Object[0]);
                    /*  70 */
                    TerminalPartManager.this.cv.updatePromptAfterCommand(mi.getName() + "> ");
                    /*     */
                }
                /*  72 */
                else if (((com.pnfsoftware.jeb.util.events.Event) e).getType().intValue() == 1) {
                    /*  73 */
                    TerminalPartManager.logger.i("EVENT_INTERPRETER_CHANGE_IMMEDIATE", new Object[0]);
                    /*  74 */
                    TerminalPartManager.this.cv.updatePrompt(mi.getName() + "> ");
                    /*     */
                }
                /*     */
                /*     */
            }
            /*  78 */
        });
        /*  79 */
        Control ctl = this.cv.getControl();
        /*  80 */
        this.context.getFontManager().registerWidget(ctl);
        /*     */
        /*     */
        /*  83 */
        this.focusInFilter = new Listener()
                /*     */ {
            /*     */
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                /*  86 */
                PartManager pman = TerminalPartManager.this.context.getPartManager();
                /*  87 */
                IMPart part = pman.getActivePart();
                /*  88 */
                if (part != null) {
                    /*  89 */
                    IUnit unit = pman.getUnitForPart(part);
                    /*     */
                    /*  91 */
                    if (TerminalPartManager.this.focusChanged(unit)) {
                        /*  92 */
                        mi.notifyListeners(new com.pnfsoftware.jeb.util.events.Event(1));
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*  96 */
        };
        /*  97 */
        this.context.getDisplay().addFilter(15, this.focusInFilter);
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
        /* 111 */
        new ContextMenu(this.cv.getControl()).addContextMenu(this);
        /*     */
    }

    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /* 116 */
        AllHandlers.getInstance().fillManager(menuMgr, 1);
        /* 117 */
        menuMgr.add(new Separator());
        /* 118 */
        menuMgr.add(new EditFindHandler());
        /* 119 */
        menuMgr.add(new EditFindnextHandler());
        /* 120 */
        menuMgr.add(new Separator());
        /* 121 */
        menuMgr.add(new EditClearHandler());
        /*     */
    }

    /*     */
    /*     */
    private boolean focusChanged(IUnit unit) {
        /* 125 */
        if (unit == null) {
            /* 126 */
            return false;
            /*     */
        }
        /*     */
        /* 129 */
        if ((unit instanceof ICodeUnit))
            /*     */ {
            /* 131 */
            IDebuggerUnit dbgUnit = DebuggerHelper.getDebuggerForUnit(this.context.getOpenedProject(), (ICodeUnit) unit);
            /* 132 */
            if (dbgUnit != null) {
                /* 133 */
                unit = dbgUnit;
                /*     */
            }
            /*     */
        }
        /* 136 */
        if ((this.cv.getInterpreter() instanceof MultiInterpreter)) {
            /* 137 */
            return ((MultiInterpreter) this.cv.getInterpreter()).onFocusChanged(unit);
            /*     */
        }
        /* 139 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    public void deleteView()
    /*     */ {
        /* 144 */
        if (this.focusInFilter != null)
            /*     */ {
            /* 146 */
            this.context.getDisplay().removeFilter(15, this.focusInFilter);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public ConsoleViewer getConsoleViewer()
    /*     */ {
        /* 152 */
        return this.cv;
        /*     */
    }

    /*     */
    /*     */
    public void setFocus()
    /*     */ {
        /* 157 */
        this.cv.getControl().setFocus();
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 162 */
        return this.cv.verifyOperation(req);
        /*     */
    }

    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 167 */
        return this.cv.doOperation(req);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\TerminalPartManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */