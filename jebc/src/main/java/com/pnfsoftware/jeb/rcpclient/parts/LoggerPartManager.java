/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.IOperable;
/*     */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.rcpclient.FontManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.FindTextDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.SimpleTextFindResults;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.StyledTextFindImpl;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditClearHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditCopyHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindnextHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditSelectAllHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import org.eclipse.jface.action.IMenuManager;
/*     */ import org.eclipse.jface.action.Separator;
/*     */ import org.eclipse.jface.text.IDocument;
/*     */ import org.eclipse.jface.text.ITextListener;
/*     */ import org.eclipse.jface.text.TextEvent;
/*     */ import org.eclipse.jface.text.source.SourceViewer;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;

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
/*     */
/*     */
/*     */
/*     */
/*     */ public class LoggerPartManager
        /*     */ extends AbstractPartManager
        /*     */ implements IOperable, IContextMenu
        /*     */ {
    /*  57 */   private static final ILogger logger = GlobalLog.getLogger(LoggerPartManager.class);
    /*     */
    /*     */   private SourceViewer viewer;
    /*     */   private StyledTextFindImpl findimpl;
    /*     */   private GraphicalTextFinder<SimpleTextFindResults> finder;

    /*     */
    /*     */
    public LoggerPartManager(RcpClientContext context)
    /*     */ {
        /*  65 */
        super(context);
        /*     */
    }

    /*     */
    /*     */
    public void createView(Composite parent, IMPart tab)
    /*     */ {
        /*  70 */
        parent.setLayout(new FillLayout());
        /*     */
        /*  72 */
        this.viewer = new SourceViewer(parent, null, 768);
        /*  73 */
        this.viewer.setDocument(this.context.getLogDocument());
        /*  74 */
        this.viewer.setEditable(false);
        /*     */
        /*  76 */
        this.findimpl = new StyledTextFindImpl(this.viewer.getTextWidget());
        /*  77 */
        this.finder = new GraphicalTextFinder(this.findimpl, this.context);
        /*     */
        /*  79 */
        Control ctl = this.viewer.getControl();
        /*  80 */
        ctl.setBackground(UIAssetManager.getInstance().getColor(15790320));
        /*     */
        /*  82 */
        this.context.getFontManager().registerWidget(ctl);
        /*     */
        /*     */
        /*  85 */
        this.viewer.addTextListener(new ITextListener()
                /*     */ {
            /*     */
            public void textChanged(TextEvent e) {
                /*  88 */
                synchronized (LoggerPartManager.this) {
                    /*  89 */
                    LoggerPartManager.this.viewer.setTopIndex(LoggerPartManager.this.viewer.getTextWidget().getLineCount());
                    /*     */
                }
                /*     */
                /*     */
            }
            /*     */
            /*  94 */
        });
        /*  95 */
        new ContextMenu(this.viewer.getControl()).addContextMenu(this);
        /*     */
    }

    /*     */
    /*     */
    public void fillContextMenu(IMenuManager menuMgr)
    /*     */ {
        /* 100 */
        menuMgr.add(new EditCopyHandler());
        /* 101 */
        menuMgr.add(new EditSelectAllHandler());
        /* 102 */
        menuMgr.add(new Separator());
        /* 103 */
        menuMgr.add(new EditFindHandler());
        /* 104 */
        menuMgr.add(new EditFindnextHandler());
        /* 105 */
        menuMgr.add(new Separator());
        /* 106 */
        menuMgr.add(new EditClearHandler());
        /*     */
    }

    /*     */
    /*     */
    public void setFocus()
    /*     */ {
        /* 111 */
        this.viewer.getControl().setFocus();
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /* 116 */
        switch (req.getOperation()) {
            /*     */
            case COPY:
                /* 118 */
                return this.viewer.canDoOperation(4);
            /*     */
            case SELECT_ALL:
                /* 120 */
                return this.viewer.canDoOperation(7);
            /*     */
            case CLEAR:
                /* 122 */
                return true;
            /*     */
            case FIND:
                /* 124 */
                return true;
            /*     */
            case FIND_NEXT:
                /* 126 */
                return this.finder.isReady();
            /*     */
        }
        /* 128 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 134 */
        switch (req.getOperation()) {
            /*     */
            case COPY:
                /* 136 */
                this.viewer.doOperation(4);
                /* 137 */
                return true;
            /*     */
            case SELECT_ALL:
                /* 139 */
                this.viewer.doOperation(7);
                /* 140 */
                return true;
            /*     */
            case CLEAR:
                /* 142 */
                synchronized (this) {
                    /* 143 */
                    this.viewer.getDocument().set("");
                    /*     */
                }
                /* 145 */
                return true;
            /*     */
            case FIND:
                /* 147 */
                TextHistory history = RcpClientContext.getStandardFindTextHistory(this.context);
                /* 148 */
                String selectedText = this.viewer.getTextWidget().getSelectionText();
                /* 149 */
                if (selectedText.length() > 0) {
                    /* 150 */
                    this.findimpl.getFindTextOptions(false).setSearchString(selectedText);
                    /*     */
                }
                /* 152 */
                FindTextDialog dlg = new FindTextDialog(this.viewer.getControl().getShell(), this.finder, history);
                /* 153 */
                dlg.open();
                /* 154 */
                return true;
            /*     */
            case FIND_NEXT:
                /* 156 */
                this.finder.search(null);
                /* 157 */
                return true;
            /*     */
        }
        /* 159 */
        return false;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\LoggerPartManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */