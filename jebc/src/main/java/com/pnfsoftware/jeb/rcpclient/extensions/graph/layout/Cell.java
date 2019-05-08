
package com.pnfsoftware.jeb.rcpclient.extensions.graph.layout;

import com.pnfsoftware.jeb.util.base.Assert;

public class Cell<T> {
    T obj;
    RowCol coords;
    int horiMergerDisp;
    int vertMergerDisp;

    Cell(int row, int col) {
        this.coords = new RowCol(row, col);
    }

    public RowCol getCoordinates() {
        return this.coords;
    }

    public int getRow() {
        return this.coords.getRow();
    }

    public int getColumn() {
        return this.coords.getColumn();
    }

    public int getNextRow() {
        return this.coords.getRow() + getVerticalSpan();
    }

    public int getNextColumn() {
        return this.coords.getColumn() + getHorizontalSpan();
    }

    public int getHorizontalSpan() {
        if (this.horiMergerDisp < 0) {
            throw new RuntimeException();
        }
        return 1 + this.horiMergerDisp;
    }

    public int getVerticalSpan() {
        if (this.vertMergerDisp < 0) {
            throw new RuntimeException();
        }
        return 1 + this.vertMergerDisp;
    }

    Cell<T> getPrimary(Spreadsheet<T> grid) {
        if ((this.horiMergerDisp >= 0) && (this.vertMergerDisp >= 0)) {
            return this;
        }
        Cell<T> primary = grid.getCellInternal(this.coords.getRow() + this.vertMergerDisp, this.coords.getColumn() + this.horiMergerDisp);
        Assert.a(primary != null, "Expected a primary cell, got null");
        return primary;
    }

    boolean isPrimary() {
        return (this.horiMergerDisp >= 0) && (this.vertMergerDisp >= 0);
    }

    public boolean isPartOfMergedCell() {
        return (this.horiMergerDisp != 0) || (this.vertMergerDisp != 0);
    }

    public void setObject(T obj) {
        this.obj = obj;
    }

    public T getObject() {
        return (T) this.obj;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[" + this.coords.getRow());
        if (this.horiMergerDisp != 0) {
            sb.append(String.format("%+d", new Object[]{Integer.valueOf(this.horiMergerDisp)}));
        }
        sb.append("," + this.coords.getColumn());
        if (this.vertMergerDisp != 0) {
            sb.append(String.format("%+d", new Object[]{Integer.valueOf(this.vertMergerDisp)}));
        }
        sb.append("]");
        if (this.obj != null) {
            sb.append(":" + this.obj);
        }
        return sb.toString();
    }
}


