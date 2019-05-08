package com.pnfsoftware.jeb.rcpclient.extensions.search;

import com.pnfsoftware.jeb.util.format.Strings;

public class FindTextOptions {
    private String searchString;
    private boolean caseSensitive;
    private boolean regex;
    private boolean reverseSearch;
    private boolean wrapAround;

    public FindTextOptions() {
        this.searchString = "";
    }

    public FindTextOptions(String initialSearchString) {
        this.searchString = Strings.safe(initialSearchString);
    }

    public FindTextOptions clone() {
        FindTextOptions dst = new FindTextOptions(this.searchString);
        dst.caseSensitive = this.caseSensitive;
        dst.regex = this.regex;
        dst.reverseSearch = this.reverseSearch;
        dst.wrapAround = this.wrapAround;
        return dst;
    }

    public String getSearchString() {
        return this.searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isRegularExpression() {
        return this.regex;
    }

    public void setRegularExpression(boolean regex) {
        this.regex = regex;
    }

    public boolean isReverseSearch() {
        return this.reverseSearch;
    }

    public void setReverseSearch(boolean reverseSearch) {
        this.reverseSearch = reverseSearch;
    }

    public boolean isWrapAround() {
        return this.wrapAround;
    }

    public void setWrapAround(boolean wrapAround) {
        this.wrapAround = wrapAround;
    }

    public String toString() {
        return String.format("\"%s\"", new Object[]{this.searchString});
    }
}


