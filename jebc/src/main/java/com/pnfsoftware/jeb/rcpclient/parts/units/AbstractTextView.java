/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.FindTextDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.SimpleTextFindResults;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.search.StyledTextFindImpl;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import org.eclipse.jface.text.TextViewer;
/*     */ import org.eclipse.swt.custom.StyledText;
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
/*     */ public abstract class AbstractTextView<T extends IUnit>
        /*     */ extends AbstractUnitFragment<T>
        /*     */ {
    /*     */   private TextViewer viewer;
    /*     */   private StyledTextFindImpl findimpl;
    /*     */   private GraphicalTextFinder<SimpleTextFindResults> finder;

    /*     */
    /*     */
    public AbstractTextView(Composite parent, int style, RcpClientContext context, T unit)
    /*     */ {
        /*  40 */
        super(parent, style, unit, null, context);
        /*     */
    }

    /*     */
    /*     */
    protected TextViewer buildSimple(Composite parent, int style) {
        /*  44 */
        this.viewer = new TextViewer(parent, style);
        /*  45 */
        this.viewer.setEditable(false);
        /*     */
        /*  47 */
        this.findimpl = new StyledTextFindImpl(this.viewer.getTextWidget());
        /*  48 */
        this.finder = new GraphicalTextFinder(this.findimpl, this.context);
        /*     */
        /*  50 */
        return this.viewer;
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /*  55 */
        switch (req.getOperation()) {
            /*     */
            case COPY:
                /*  57 */
                return this.viewer.canDoOperation(4);
            /*     */
            case SELECT_ALL:
                /*  59 */
                return this.viewer.canDoOperation(7);
            /*     */
            case FIND:
                /*  61 */
                return true;
            /*     */
            case FIND_NEXT:
                /*  63 */
                return this.finder.isReady();
            /*     */
        }
        /*  65 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /*  71 */
        switch (req.getOperation()) {
            /*     */
            case COPY:
                /*  73 */
                this.viewer.doOperation(4);
                /*  74 */
                return true;
            /*     */
            case SELECT_ALL:
                /*  76 */
                this.viewer.doOperation(7);
                /*  77 */
                return true;
            /*     */
            case FIND:
                /*  79 */
                FindTextDialog dlg = FindTextDialog.getInstance(this);
                /*  80 */
                if (dlg != null) {
                    /*  81 */
                    dlg.setFocus();
                    /*  82 */
                    return true;
                    /*     */
                }
                /*  84 */
                TextHistory history = RcpClientContext.getStandardFindTextHistory(this.context);
                /*  85 */
                String selectedText = this.viewer.getTextWidget().getSelectionText();
                /*  86 */
                if (selectedText.length() > 0) {
                    /*  87 */
                    this.findimpl.getFindTextOptions(false).setSearchString(selectedText);
                    /*     */
                }
                /*  89 */
                dlg = new FindTextDialog(this.viewer.getControl().getShell(), this.finder, history, false, this, getUnit() == null ? null : getUnit().getName());
                /*  90 */
                dlg.open();
                /*  91 */
                return true;
            /*     */
            case FIND_NEXT:
                /*  93 */
                this.finder.search(null);
                /*  94 */
                return true;
            /*     */
        }
        /*  96 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 102 */
        return Strings.encodeUTF8(this.viewer.getTextWidget().getText());
        /*     */
    }

    /*     */
    /*     */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*     */ {
        /* 107 */
        return AbstractUnitFragment.FragmentType.TEXT;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\AbstractTextView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */