package com.pnfsoftware.jeb.rcpclient.extensions.search;

public class SimpleTextFindResults implements IFindTextResult {
    public static SimpleTextFindResults EOS = new SimpleTextFindResults(-1);
    private int indexBegin;
    private int indexEnd;
    private int flag;

    public SimpleTextFindResults(int flag) {
        this.flag = flag;
    }

    public SimpleTextFindResults(int indexBegin, int indexEnd, boolean wrappedAround) {
        this.indexBegin = indexBegin;
        this.indexEnd = indexEnd;
        this.flag = (wrappedAround ? 1 : 0);
    }

    public boolean isEndOfSearch() {
        return this.flag == -1;
    }

    public boolean isWrappedAround() {
        return this.flag == 1;
    }

    public int getIndexBegin() {
        return this.indexBegin;
    }

    public int getIndexEnd() {
        return this.indexEnd;
    }

    public String toString() {
        return String.format("%d-%d", new Object[]{Integer.valueOf(this.indexBegin), Integer.valueOf(this.indexEnd)});
    }
}


