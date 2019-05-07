
package com.pnfsoftware.jeb.rcpclient.iviewers.text;


import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.rcpclient.extensions.search.IFindTextResult;


public class InteractiveTextFindResult
        implements IFindTextResult {
    public static InteractiveTextFindResult EOS = new InteractiveTextFindResult(-1);
    private ICoordinates begin;
    private ICoordinates end;
    private int flag;


    public InteractiveTextFindResult(int flag) {

        this.flag = flag;

    }


    public InteractiveTextFindResult(ICoordinates begin, ICoordinates end, boolean wrappedAround) {

        this.begin = begin;

        this.end = end;

        this.flag = (wrappedAround ? 1 : 0);

    }


    public boolean isEndOfSearch() {

        return this.flag == -1;

    }


    public boolean isWrappedAround() {

        return this.flag == 1;

    }


    public ICoordinates getBegin() {

        return this.begin;

    }


    public ICoordinates getEnd() {

        return this.end;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\InteractiveTextFindResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */