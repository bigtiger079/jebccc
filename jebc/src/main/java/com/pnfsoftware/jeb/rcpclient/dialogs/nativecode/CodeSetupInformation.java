package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;


public class CodeSetupInformation {
    long address;


    int procMode;


    int maxInsnCount;


    public CodeSetupInformation(long address) {
        this(address, 0, 100);
    }

    public CodeSetupInformation(long address, int procMode, int maxInsnCount) {
        this.address = address;
        this.procMode = procMode;
        this.maxInsnCount = maxInsnCount;
    }

    public long getAddress() {
        return this.address;
    }

    public int getProcessorMode() {
        return this.procMode;
    }

    public int getMaxInstructionCount() {
        return this.maxInsnCount;
    }
}
