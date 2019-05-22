package com.pnfsoftware.jeb.rcpclient.iviewers.text;

public class UnwrappedBufferPoint extends BufferPoint {
    public boolean eol;

    public UnwrappedBufferPoint(int columnOffset, int lineIndex, boolean eol) {
        super(columnOffset, lineIndex);
        this.eol = eol;
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.eol ? 1231 : 1237);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        UnwrappedBufferPoint other = (UnwrappedBufferPoint) obj;
        return this.eol == other.eol;
    }

    public String toString() {
        return String.format("unwrapped_%s[eol=%b]", super.toString(), this.eol);
    }
}


