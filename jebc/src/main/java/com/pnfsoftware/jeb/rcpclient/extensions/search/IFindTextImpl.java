package com.pnfsoftware.jeb.rcpclient.extensions.search;

public interface IFindTextImpl<FindTextResult extends IFindTextResult> {
    public abstract boolean supportReverseSearch();

    public abstract void resetFindTextOptions();

    public abstract void setFindTextOptions(FindTextOptions paramFindTextOptions);

    public abstract FindTextOptions getFindTextOptions(boolean paramBoolean);

    public abstract FindTextResult findText(FindTextOptions paramFindTextOptions);

    public abstract void processFindResult(FindTextResult paramFindTextResult);

    public abstract void clearFindResult();
}


