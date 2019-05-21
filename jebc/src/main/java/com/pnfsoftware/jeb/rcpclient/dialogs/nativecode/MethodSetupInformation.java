package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

public class MethodSetupInformation {
    Boolean routineNonReturning;
    Integer routineDataSPDeltaOnReturn;

    public Boolean getRoutineNonReturning() {
        return this.routineNonReturning;
    }

    public Integer getRoutineDataSPDeltaOnReturn() {
        return this.routineDataSPDeltaOnReturn;
    }

    public String toString() {
        return String.format("routineNonReturning=%s,routineDataSPDeltaOnReturn=%s", this.routineNonReturning, this.routineDataSPDeltaOnReturn);
    }
}
