
package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.FindTextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
import com.pnfsoftware.jeb.rcpclient.extensions.search.SimpleTextFindResults;
import com.pnfsoftware.jeb.rcpclient.extensions.search.StyledTextFindImpl;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractTextView<T extends IUnit>
        extends AbstractUnitFragment<T> {
    private TextViewer viewer;
    private StyledTextFindImpl findimpl;
    private GraphicalTextFinder<SimpleTextFindResults> finder;

    public AbstractTextView(Composite parent, int style, RcpClientContext context, T unit) {
        super(parent, style, unit, null, context);
    }

    protected TextViewer buildSimple(Composite parent, int style) {
        this.viewer = new TextViewer(parent, style);
        this.viewer.setEditable(false);
        this.findimpl = new StyledTextFindImpl(this.viewer.getTextWidget());
        this.finder = new GraphicalTextFinder(this.findimpl, this.context);
        return this.viewer;
    }

    public boolean verifyOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case COPY:
                return this.viewer.canDoOperation(4);
            case SELECT_ALL:
                return this.viewer.canDoOperation(7);
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
            case FIND:
                FindTextDialog dlg = FindTextDialog.getInstance(this);
                if (dlg != null) {
                    dlg.setFocus();
                    return true;
                }
                TextHistory history = RcpClientContext.getStandardFindTextHistory(this.context);
                String selectedText = this.viewer.getTextWidget().getSelectionText();
                if (selectedText.length() > 0) {
                    this.findimpl.getFindTextOptions(false).setSearchString(selectedText);
                }
                dlg = new FindTextDialog(this.viewer.getControl().getShell(), this.finder, history, false, this, getUnit() == null ? null : getUnit().getName());
                dlg.open();
                return true;
            case FIND_NEXT:
                this.finder.search(null);
                return true;
        }
        return false;
    }

    public byte[] export() {
        return Strings.encodeUTF8(this.viewer.getTextWidget().getText());
    }

    public AbstractUnitFragment.FragmentType getFragmentType() {
        return AbstractUnitFragment.FragmentType.TEXT;
    }
}


