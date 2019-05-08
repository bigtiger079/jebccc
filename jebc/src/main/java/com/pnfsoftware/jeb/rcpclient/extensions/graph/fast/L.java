
package com.pnfsoftware.jeb.rcpclient.extensions.graph.fast;

public class L {
    int src;
    int dst;

    public L(int src, int dst) {
        this.src = src;
        this.dst = dst;
    }

    public int getSrcId() {
        return this.src;
    }

    public int getDstId() {
        return this.dst;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.dst;
        result = 31 * result + this.src;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        L other = (L) obj;
        if (this.dst != other.dst)
            return false;
        if (this.src != other.src)
            return false;
        return true;
    }

    public L clone() {
        return new L(this.src, this.dst);
    }

    public String toString() {
        return String.format("%d>%d", new Object[]{Integer.valueOf(this.src), Integer.valueOf(this.dst)});
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\fast\L.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */