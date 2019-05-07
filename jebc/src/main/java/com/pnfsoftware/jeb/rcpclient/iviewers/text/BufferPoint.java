
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

        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        BufferPoint other = (BufferPoint) obj;

        if (this.columnOffset != other.columnOffset)
            return false;

        if (this.lineIndex != other.lineIndex)
            return false;

        return true;

    }


    public String toString() {

        return String.format("(%d,%d)", new Object[]{Integer.valueOf(this.lineIndex), Integer.valueOf(this.columnOffset)});

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\BufferPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */