package com.pnfsoftware.jeb.rcpclient.extensions.search;

import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

public class StyledTextFindImpl implements IFindTextImpl<SimpleTextFindResults> {
    private static final ILogger logger = GlobalLog.getLogger(StyledTextFindImpl.class);
    StyledText widget;
    FindTextOptions findOptions;
    String text;
    int position;

    public StyledTextFindImpl(StyledText widget) {
        if (widget == null) {
            throw new NullPointerException();
        }
        this.widget = widget;
    }

    public boolean supportReverseSearch() {
        return true;
    }

    public void resetFindTextOptions() {
        this.findOptions = null;
    }

    public void setFindTextOptions(FindTextOptions options) {
        this.findOptions = options;
    }

    public FindTextOptions getFindTextOptions(boolean update) {
        if (this.findOptions == null) {
            this.findOptions = new FindTextOptions(this.widget.getSelectionText());
        }
        if (update) {
            this.text = this.widget.getText();
            this.position = this.widget.getCaretOffset();
        }
        return this.findOptions;
    }

    public SimpleTextFindResults findText(FindTextOptions optionsOverride) {
        if (this.text == null) {
            return null;
        }
        FindTextOptions options = optionsOverride != null ? optionsOverride : this.findOptions;
        if ((options == null) || (options.getSearchString() == null) || (options.getSearchString().isEmpty())) {
            return null;
        }
        boolean wrappedAround = false;
        for (; ; ) {
            int index = Strings.search(this.text, this.position, options.getSearchString(), options.isRegularExpression(), options.isCaseSensitive(), options.isReverseSearch());
            if (index >= 0) {
                return new SimpleTextFindResults(index, index + options.getSearchString().length(), wrappedAround);
            }
            if (!options.isWrapAround()) {
                break;
            }
            if (!options.isReverseSearch()) {
                if (this.position == 0) {
                    break;
                }
                this.position = 0;
            } else {
                if (this.position == this.text.length()) {
                    break;
                }
                this.position = this.text.length();
            }
            wrappedAround = true;
        }
        return SimpleTextFindResults.EOS;
    }

    public void processFindResult(SimpleTextFindResults r) {
        if (r == null) {
            return;
        }
        if (r.isEndOfSearch()) {
            Display.getCurrent().beep();
            logger.warn("End of search", new Object[0]);
            return;
        }
        if (r.isWrappedAround()) {
            Display.getCurrent().beep();
            logger.warn("Search wrapped around", new Object[0]);
        }
        if (!this.findOptions.isReverseSearch()) {
            this.widget.setSelection(r.getIndexBegin(), r.getIndexEnd());
        } else {
            this.widget.setSelection(r.getIndexEnd(), r.getIndexBegin());
        }
    }

    public void clearFindResult() {
        int offset = this.widget.getCaretOffset();
        this.widget.setSelection(offset);
    }
}


