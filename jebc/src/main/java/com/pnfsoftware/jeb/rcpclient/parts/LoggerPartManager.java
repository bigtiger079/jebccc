
package com.pnfsoftware.jeb.rcpclient.parts;


import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.FindTextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
import com.pnfsoftware.jeb.rcpclient.extensions.search.SimpleTextFindResults;
import com.pnfsoftware.jeb.rcpclient.extensions.search.StyledTextFindImpl;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditClearHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditCopyHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindnextHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditSelectAllHandler;
import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class LoggerPartManager
        extends AbstractPartManager
        implements IOperable, IContextMenu {
    private static final ILogger logger = GlobalLog.getLogger(LoggerPartManager.class);

    private SourceViewer viewer;
    private StyledTextFindImpl findimpl;
    private GraphicalTextFinder<SimpleTextFindResults> finder;


    public LoggerPartManager(RcpClientContext context) {

        super(context);

    }


    public void createView(Composite parent, IMPart tab) {

        parent.setLayout(new FillLayout());


        this.viewer = new SourceViewer(parent, null, 768);

        this.viewer.setDocument(this.context.getLogDocument());

        this.viewer.setEditable(false);


        this.findimpl = new StyledTextFindImpl(this.viewer.getTextWidget());

        this.finder = new GraphicalTextFinder(this.findimpl, this.context);


        Control ctl = this.viewer.getControl();

        ctl.setBackground(UIAssetManager.getInstance().getColor(15790320));


        this.context.getFontManager().registerWidget(ctl);


        this.viewer.addTextListener(new ITextListener() {

            public void textChanged(TextEvent e) {

                synchronized (LoggerPartManager.this) {

                    LoggerPartManager.this.viewer.setTopIndex(LoggerPartManager.this.viewer.getTextWidget().getLineCount());

                }


            }


        });

        new ContextMenu(this.viewer.getControl()).addContextMenu(this);

    }


    public void fillContextMenu(IMenuManager menuMgr) {

        menuMgr.add(new EditCopyHandler());

        menuMgr.add(new EditSelectAllHandler());

        menuMgr.add(new Separator());

        menuMgr.add(new EditFindHandler());

        menuMgr.add(new EditFindnextHandler());

        menuMgr.add(new Separator());

        menuMgr.add(new EditClearHandler());

    }


    public void setFocus() {

        this.viewer.getControl().setFocus();

    }


    public boolean verifyOperation(OperationRequest req) {

        switch (req.getOperation()) {

            case COPY:

                return this.viewer.canDoOperation(4);

            case SELECT_ALL:

                return this.viewer.canDoOperation(7);

            case CLEAR:

                return true;

            case FIND:

                return true;

            case FIND_NEXT:

                return this.finder.isReady();

        }

        return false;

    }


    public boolean doOperation(OperationRequest req) {

        switch (req.getOperation()) {

            case COPY:

                this.viewer.doOperation(4);

                return true;

            case SELECT_ALL:

                this.viewer.doOperation(7);

                return true;

            case CLEAR:

                synchronized (this) {

                    this.viewer.getDocument().set("");

                }

                return true;

            case FIND:

                TextHistory history = RcpClientContext.getStandardFindTextHistory(this.context);

                String selectedText = this.viewer.getTextWidget().getSelectionText();

                if (selectedText.length() > 0) {

                    this.findimpl.getFindTextOptions(false).setSearchString(selectedText);

                }

                FindTextDialog dlg = new FindTextDialog(this.viewer.getControl().getShell(), this.finder, history);

                dlg.open();

                return true;

            case FIND_NEXT:

                this.finder.search(null);

                return true;

        }

        return false;

    }

}


