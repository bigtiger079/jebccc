
package com.pnfsoftware.jeb.rcpclient;


import java.util.Arrays;
import java.util.List;


public class StatusIndicatorData {
    private List<String> elts;


    public StatusIndicatorData(String... elements) {

        this.elts = Arrays.asList(elements);

    }


    public List<String> getElements() {

        return this.elts;

    }

}


