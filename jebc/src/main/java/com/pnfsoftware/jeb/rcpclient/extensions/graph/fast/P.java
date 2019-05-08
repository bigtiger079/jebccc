
package com.pnfsoftware.jeb.rcpclient.extensions.graph.fast;

import com.pnfsoftware.jeb.rcpclient.extensions.graph.IGraphNode;

public class P
        implements IGraphNode {
    Integer id;
    double x;
    double y;

    public P() {
    }

    public P(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public P(Integer id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return this.id == null ? -1 : this.id.intValue();
    }

    public void reset() {
        this.x = 0.0D;
        this.y = 0.0D;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public P clone() {
        return new P(this.id, this.x, this.y);
    }

    public double dist() {
        if ((this.x == 0.0D) && (this.y == 0.0D)) {
            return 0.0D;
        }
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public double dist(P other) {
        if ((this.x == other.x) && (this.y == other.y)) {
            return 0.0D;
        }
        return Math.sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y));
    }

    public double distsquare(P other) {
        if ((this.x == other.x) && (this.y == other.y)) {
            return 0.0D;
        }
        return (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y);
    }

    public void add(P other) {
        this.x += other.x;
        this.y += other.y;
    }

    public void sub(P other) {
        this.x -= other.x;
        this.y -= other.y;
    }

    public void scale(double r) {
        this.x *= r;
        this.y *= r;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        P other = (P) obj;
        if ((this.id == null) || (other.id == null))
            return false;
        return this.id.equals(other.id);
    }

    public String toString() {
        if (this.id == null) {
            return String.format("(%f,%f)", new Object[]{Double.valueOf(this.x), Double.valueOf(this.y)});
        }
        return String.format("%d:(%f,%f)", new Object[]{this.id, Double.valueOf(this.x), Double.valueOf(this.y)});
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\graph\fast\P.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */