package com.pnfsoftware.jeb.rcpclient.iviewers.text;

public class BufferPoint {
    public int lineIndex;
    public int columnOffset;

    public BufferPoint(int columnOffset, int lineIndex) {
        this.lineIndex = lineIndex;
        this.columnOffset = columnOffset;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.columnOffset;
        result = 31 * result + this.lineIndex;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BufferPoint other = (BufferPoint) obj;
        if (this.columnOffset != other.columnOffset) return false;
        return this.lineIndex == other.lineIndex;
    }

    public String toString() {
        return String.format("(%d,%d)", this.lineIndex, this.columnOffset);
    }
}


