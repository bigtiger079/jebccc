package com.pnfsoftware.jeb.rcpclient.extensions.graph.layout;

public class RowCol {
    private int row;
    private int col;

    public RowCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.col;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.col;
        result = 31 * result + this.row;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RowCol other = (RowCol) obj;
        if (this.col != other.col) return false;
        return this.row == other.row;
    }

    public String toString() {
        return String.format("[%d,%d]", this.row, this.col);
    }
}


