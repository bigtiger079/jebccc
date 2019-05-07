package com.pnfsoftware.jeb.rcpclient.extensions.search;

public abstract interface IFindTextImpl<FindTextResult extends IFindTextResult> {
    public abstract boolean supportReverseSearch();

    public abstract void resetFindTextOptions();

    public abstract void setFindTextOptions(FindTextOptions paramFindTextOptions);

    public abstract FindTextOptions getFindTextOptions(boolean paramBoolean);

    public abstract FindTextResult findText(FindTextOptions paramFindTextOptions);

    public abstract void processFindResult(FindTextResult paramFindTextResult);

    public abstract void clearFindResult();
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\search\IFindTextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */