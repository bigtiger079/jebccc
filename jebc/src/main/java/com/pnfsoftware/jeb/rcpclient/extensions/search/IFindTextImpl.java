package com.pnfsoftware.jeb.rcpclient.extensions.search;

public interface IFindTextImpl<FindTextResult extends IFindTextResult> {
    boolean supportReverseSearch();

    void resetFindTextOptions();

    void setFindTextOptions(FindTextOptions paramFindTextOptions);

    FindTextOptions getFindTextOptions(boolean paramBoolean);

    FindTextResult findText(FindTextOptions paramFindTextOptions);

    void processFindResult(FindTextResult paramFindTextResult);

    void clearFindResult();
}


