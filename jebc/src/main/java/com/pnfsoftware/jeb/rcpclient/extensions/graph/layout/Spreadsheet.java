package com.pnfsoftware.jeb.rcpclient.extensions.graph.layout;

import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

public class Spreadsheet<T> {
    private static final ILogger logger = GlobalLog.getLogger(Spreadsheet.class);
    List<List<Cell<T>>> grid = new ArrayList<>();
    int lastRowIndex;
    int lastColumnIndex;

    public Spreadsheet() {
        this(0, 0);
    }

    public Spreadsheet(int idealRowCount, int idealColumnCount) {
    }

    public int getLastRowIndex() {
        return this.lastRowIndex;
    }

    public int getLastColumnIndex() {
        return this.lastColumnIndex;
    }

    public int getRowCount() {
        return this.lastRowIndex + 1;
    }

    public int getColumnCount() {
        return this.lastColumnIndex + 1;
    }

    public void verify() {
        if (this.grid.size() != getRowCount()) {
            throw new RuntimeException(String.format("Expected %d rows, got %d", getRowCount(), this.grid.size()));
        }
        for (int row = 0; row < getRowCount(); row++) {
            List<Cell<T>> cellrow = this.grid.get(row);
            if ((cellrow != null) && (cellrow.size() != getColumnCount())) {
                throw new RuntimeException(String.format("Row %d: Expected %d columns, got %d", row, getColumnCount(), cellrow.size()));
            }
        }
        for (int row = 0; row < getRowCount(); row++) {
            List<Cell<T>> cellrow = this.grid.get(row);
            if (cellrow != null) {
                for (int col = 0; col < getColumnCount(); col++) {
                    Cell<T> cell = cellrow.get(col);
                    if ((cell != null) && (cell.isPartOfMergedCell())) {
                        if ((cell.getRow() != row) || (cell.getColumn() != col)) {
                            throw new RuntimeException(String.format("Inconsistent cell coordinates: expected (%d,%d), got (%d,%d)", row, col, cell.getRow(), cell.getColumn()));
                        }
                        if ((cell.horiMergerDisp > 0) && (cell.horiMergerDisp < 0)) {
                            throw new RuntimeException();
                        }
                        if ((cell.vertMergerDisp > 0) && (cell.vertMergerDisp < 0)) {
                            throw new RuntimeException();
                        }
                        if (cell.isPrimary()) {
                            int i = 0;
                            for (int row2 = row; row2 <= row + cell.vertMergerDisp; row2++) {
                                for (int col2 = col; col2 <= col + cell.horiMergerDisp; col2++) {
                                    if (i++ != 0) {
                                        Cell<T> slave = getCellInternal(row2, col2);
                                        if (slave == null) {
                                            throw new RuntimeException(String.format("%d,%d: Expected slave cell, got null", row2, col2));
                                        }
                                        if (row2 + slave.vertMergerDisp != row) {
                                            throw new RuntimeException(String.format("%d,%d: Bad slave vertical displacement", row2, col2));
                                        }
                                        if (col2 + slave.horiMergerDisp != col) {
                                            throw new RuntimeException(String.format("%d,%d: Bad slave horizontal displacement", row2, col2));
                                        }
                                    }
                                }
                            }
                        } else {
                            int row2 = row + cell.vertMergerDisp;
                            int col2 = col + cell.horiMergerDisp;
                            Cell<T> master = getCellInternal(row2, col2);
                            if ((master == null) || (!master.isPrimary()) || (!master.isPartOfMergedCell())) {
                                throw new RuntimeException(String.format("%d,%d: slave does not reference a master cell", row, col));
                            }
                        }
                    }
                }
            }
        }
    }

    public int clearNullCells(boolean includeNullMergers) {
        int cnt = 0;
        for (int row = 0; row < getRowCount(); row++) {
            List<Cell<T>> cellrow = this.grid.get(row);
            if (cellrow != null) {
                for (int col = 0; col < getColumnCount(); col++) {
                    Cell<T> cell = cellrow.get(col);
                    if ((cell != null) && (cell.getObject() == null)) {
                        if ((includeNullMergers) || (!cell.isPartOfMergedCell())) {
                            for (int row2 = row; row2 <= row + cell.vertMergerDisp; row2++)
                                for (int col2 = col; col2 <= col + cell.horiMergerDisp; col2++) {
                                    ((List) this.grid.get(row2)).set(col2, null);
                                    cnt++;
                                }
                        }
                    }
                }
            }
        }
        return cnt;
    }

    public int findNullRow(int rowStart, boolean includeNullMergers) {
        label112:
        for (int row = rowStart; row < getRowCount(); row++) {
            List<Cell<T>> cellrow = this.grid.get(row);
            if (cellrow == null) {
                return row;
            }
            for (int col = 0; col < getColumnCount(); col++) {
                Cell<T> cell = cellrow.get(col);
                if (cell != null) {
                    if (cell.getObject() != null) {
                        break label112;
                    }
                    if ((cell.isPartOfMergedCell()) && ((!includeNullMergers) || (cell.getPrimary(this).getObject() != null))) {
                        break label112;
                    }
                }
            }
            return row;
        }
        return -1;
    }

    public int findNullColumn(int columnStart, boolean includeNullMergers) {
        label85:
        for (int col = columnStart; col < getColumnCount(); col++) {
            for (int row = 0; row < getRowCount(); row++) {
                Cell<T> cell = getCellInternal(row, col);
                if (cell != null) {
                    if (cell.getObject() != null) {
                        break label85;
                    }
                    if ((cell.isPartOfMergedCell()) && ((!includeNullMergers) || (cell.getPrimary(this).getObject() != null))) {
                        break label85;
                    }
                }
            }
            return col;
        }
        return -1;
    }

    public void removeRow(int row) {
        List<Cell<T>> cells = this.grid.get(row);
        if (cells == null) {
            this.grid.remove(row);
            return;
        }
        for (int col = 0; col < getColumnCount(); col++) {
            Cell<T> cell = cells.get(col);
            if (cell == null) {
                cells.remove(col);
            } else {
                if (cell.isPartOfMergedCell()) {
                    throw new RuntimeException();
                }
                cells.remove(col);
            }
        }
    }

    public void removeColumn(int col) {
        for (int row = 0; row < getRowCount(); row++) {
            List<Cell<T>> cells = this.grid.get(row);
            if (cells != null) {
                Cell<T> cell = cells.get(col);
                if (cell == null) {
                    cells.remove(col);
                } else {
                    if (cell.isPartOfMergedCell()) {
                        throw new RuntimeException();
                    }
                    cells.remove(col);
                }
            }
        }
    }

    private Cell<T> nullifyCell(int row, int col) {
        return writeCell(row, col, null);
    }

    public Cell<T> writeCell(int row, int col, T object) {
        return writeCell(row, col, 0, 0, object, false);
    }

    public Cell<T> writeCell(int row, int col, int hspan, int vspan, T object) {
        return writeCell(row, col, hspan, vspan, object, false);
    }

    public Cell<T> writeCell(int row, int col, int hspan, int vspan, T object, boolean failOnOverwrite) {
        if ((row < 0) || (col < 0)) {
            throw new IllegalArgumentException("Illegal cell coordinates");
        }
        if ((hspan < 0) || (vspan < 0)) {
            throw new IllegalArgumentException("Illegal spanning");
        }
        if (((hspan == 0) && (vspan != 0)) || ((hspan != 0) && (vspan == 0))) {
            throw new IllegalArgumentException("Illegal hspan/vspan combo");
        }
        while (row >= this.grid.size()) {
            this.grid.add(null);
        }
        List<Cell<T>> cells = this.grid.get(row);
        if (cells == null) {
            cells = new ArrayList<>();
            this.grid.set(row, cells);
        }
        if (row > this.lastRowIndex) {
            this.lastRowIndex = row;
        }
        while (col >= cells.size()) {
            cells.add(null);
        }
        Cell<T> cell = cells.get(col);
        if (cell == null) {
            cell = new Cell(row, col);
            cells.set(col, cell);
        }
        if (col > this.lastColumnIndex) {
            this.lastColumnIndex = col;
        } else if (col < this.lastColumnIndex) {
            while (cells.size() < this.lastColumnIndex + 1) {
                cells.add(null);
            }
        }
        if ((hspan == 0) && (vspan == 0)) {
            cell = cell.getPrimary(this);
        } else {
            cell = mergeCells(row, col, hspan, vspan);
        }
        if ((failOnOverwrite) && (cell.obj != null)) {
            throw new RuntimeException(String.format("Cell: Cannot overwrite '%s' by '%s'", cell.obj, object));
        }
        cell.obj = object;
        return cell;
    }

    Cell<T> getCellInternal(int row, int col) {
        if ((row < 0) || (col < 0)) {
            throw new IllegalArgumentException("Illegal cell coordinnates");
        }
        if (row >= this.grid.size()) {
            return null;
        }
        List<Cell<T>> cells = this.grid.get(row);
        if (cells == null) {
            return null;
        }
        if (col >= cells.size()) {
            return null;
        }
        return cells.get(col);
    }

    public Cell<T> getCellSmart(int row, int col, boolean adjustCoordinatesForMergedCells) {
        Cell<T> cell = getCellInternal(row, col);
        if ((cell != null) && (adjustCoordinatesForMergedCells)) {
            cell = cell.getPrimary(this);
        }
        return cell;
    }

    public Cell<T> getCell(int row, int col) {
        return getCell(row, col, false);
    }

    public Cell<T> getCell(int row, int col, boolean createIfNull) {
        Cell<T> cell = getCellInternal(row, col);
        if (cell == null) {
            if (createIfNull) {
                cell = writeCell(row, col, null);
            }
        } else if (!cell.isPrimary()) {
            throw new IllegalArgumentException(String.format("The cell at (%d,%d) is not a primary cell", row, col));
        }
        return cell;
    }

    public boolean isFree(int row, int col) {
        return isFree(row, col, 1, 1);
    }

    public boolean isRangeFree(int row, int colBegin, int colEnd) {
        return isFree(row, colBegin, colEnd - colBegin, 1);
    }

    public boolean isFree(int row, int col, int hspan, int vspan) {
        for (int i = row; i < row + vspan; i++) {
            for (int j = col; j < col + hspan; j++) {
                if (getCellInternal(i, j) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public Cell<T> mergeCells(int row, int col, int horizontalSpan, int verticalSpan) {
        if ((horizontalSpan <= 0) || (verticalSpan <= 0)) {
            throw new IllegalArgumentException("Spanning requirements cannot be negative or zero");
        }
        Cell<T> cell = getCellInternal(row, col);
        if (cell == null) {
            cell = writeCell(row, col, null);
        } else {
            if (!cell.isPrimary()) {
                throw new IllegalArgumentException("Main cell is not a primary");
            }
            if ((cell.getHorizontalSpan() == horizontalSpan) && (cell.getVerticalSpan() == verticalSpan)) {
                return cell;
            }
        }
        for (int i = row; i < row + verticalSpan; i++) {
            for (int j = col; j < col + horizontalSpan; j++) {
                Cell<T> c = getCellInternal(i, j);
                if (c != null) {
                    Cell<T> master = c.getPrimary(this);
                    if ((master.getRow() < row) || (master.getNextRow() > row + verticalSpan) || (master.getColumn() < col) || (master.getColumn() > col + horizontalSpan)) {
                        throw new RuntimeException("Merging partially overlaps other merged cells");
                    }
                }
            }
        }
        int vdisp = 0;
        for (int i = row; i < row + verticalSpan; i++) {
            int hdisp = 0;
            for (int j = col; j < col + horizontalSpan; j++) {
                Cell<T> c = getCellInternal(i, j);
                if (c == null) {
                    c = nullifyCell(i, j);
                } else if (c != cell) {
                    c.obj = null;
                }
                if ((i == row) && (j == col)) {
                    c.horiMergerDisp = (horizontalSpan - 1);
                    c.vertMergerDisp = (verticalSpan - 1);
                } else {
                    c.horiMergerDisp = hdisp;
                    c.vertMergerDisp = vdisp;
                }
                hdisp--;
            }
            vdisp--;
        }
        return cell;
    }

    public Cell<T> splitCell(Cell<T> cell, boolean horizontal) {
        if (!cell.isPrimary()) {
            throw new IllegalArgumentException("The cell must be a primary cell");
        }
        Cell<T> splitcell = null;
        if (horizontal) {
            int targetrow = cell.getRow();
            int targetcol = cell.getColumn();
            List<Cell<T>> cells;
            if (cell.horiMergerDisp > 0) {
                int i = 0;
                for (int row = targetrow; row <= targetrow + cell.vertMergerDisp; row++) {
                    cells = this.grid.get(row);
                    for (int col = targetcol + 1; col <= targetcol + cell.horiMergerDisp; col++) {
                        Cell<T> cell2 = cells.get(col);
                        if (i++ == 0) {
                            cell.horiMergerDisp -= 1;
                            cell2.vertMergerDisp = cell.vertMergerDisp;
                            splitcell = cell2;
                        } else {
                            cell2.horiMergerDisp += 1;
                        }
                    }
                }
                for (int row = targetrow; row <= targetrow + cell.vertMergerDisp; row++) {
                    getCellInternal(row, targetcol).horiMergerDisp = 0;
                }
            } else {
                int inscol = targetcol + 1;
                int row = 0;
                for (List<Cell<T>> cellrow : this.grid) {
                    if (cellrow != null) {
                        int base = 0;
                        if (row != targetrow) {
                            for (int col = targetcol; col >= 0; col--) {
                                Cell<T> c = cellrow.get(col);
                                if (c == null) {
                                    c = new Cell(row, col);
                                    cellrow.set(col, c);
                                }
                                if (c.horiMergerDisp >= 0) {
                                    c.horiMergerDisp += 1;
                                    break;
                                }
                                base--;
                            }
                        }
                        Cell<T> ci = new Cell(row, inscol);
                        cellrow.add(inscol, ci);
                        if (row != targetrow) {
                            ci.horiMergerDisp = (base - 1);
                        } else {
                            splitcell = ci;
                        }
                        boolean mergerUpdate = true;
                        for (int col = inscol + 1; col < cellrow.size(); col++) {
                            Cell<T> c = cellrow.get(col);
                            if (c == null) {
                                c = new Cell(row, col);
                                cellrow.set(col, c);
                            }
                            c.coords = new RowCol(row, col);
                            if (mergerUpdate) {
                                if (c.horiMergerDisp >= 0) {
                                    mergerUpdate = false;
                                } else {
                                    c.horiMergerDisp -= 1;
                                }
                            }
                        }
                    }
                    row++;
                }
                this.lastColumnIndex += 1;
            }
        } else {
            throw new RuntimeException("Vertical splits for cells are not supported yet");
        }
        return splitcell;
    }

    public Cell<T> moveCell(Cell<T> cell, int dstRow, int dstCol, boolean keepSpanning) {
        int dstHspan = 1;
        int dstVspan = 1;
        if (keepSpanning) {
            dstHspan = cell.getHorizontalSpan();
            dstVspan = cell.getVerticalSpan();
        }
        if (!isFree(dstRow, dstCol, dstHspan, dstVspan)) {
            return null;
        }
        Cell<T> dst = writeCell(dstRow, dstCol, dstHspan, dstVspan, cell.getObject());
        Assert.a(dst != null, "Unexpected cell creation failure");
        return dst;
    }

    public boolean deleteCell(Cell<T> cell, boolean keepMerger) {
        throw new RuntimeException("TBI");
    }

    public List<Cell<T>> getRealCells() {
        List<Cell<T>> r = new ArrayList<>();
        for (int row = 0; row < this.grid.size(); row++) {
            getRealCellsOnRow(row, r);
        }
        return r;
    }

    public List<Cell<T>> getRealCellsOnRow(int row) {
        List<Cell<T>> r = new ArrayList<>();
        getRealCellsOnRow(row, r);
        return r;
    }

    private void getRealCellsOnRow(int row, List<Cell<T>> sink) {
        List<Cell<T>> cells = this.grid.get(row);
        if (cells != null) {
            for (Cell<T> cell1 : cells) {
                Cell<T> cell = (Cell) cell1;
                if ((cell != null) && (cell.obj != null) && (cell.horiMergerDisp >= 0) && (cell.vertMergerDisp >= 0)) {
                    sink.add(cell);
                }
            }
        }
    }

    public Cell<T> createFirstAvailableOnRow(int row, int col) {
        Cell<T> cell = getCellInternal(row, col);
        if (cell == null) {
            cell = nullifyCell(row, col);
            Assert.a((cell.getRow() == row) && (cell.getColumn() == col));
            return cell;
        }
        cell = cell.getPrimary(this);
        do {
            Assert.a(cell.isPrimary());
            if (cell.obj == null) {
                return cell;
            }
            col = cell.getColumn() + cell.getHorizontalSpan();
            cell = getCellSmart(row, col, false);
        } while (cell != null);
        return nullifyCell(row, col);
    }

    public Cell<T> getCellByObject(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
        for (Cell<T> cell : getRealCells()) {
            if (obj.equals(cell.obj)) {
                return cell;
            }
        }
        return null;
    }

    public String formatCells() {
        return getRealCells().toString();
    }

    public String toString() {
        return format();
    }

    public String format() {
        if (this.grid.isEmpty()) {
            return "(empty)";
        }
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row <= this.lastRowIndex; row++) {
            List<Cell<T>> rowcells = this.grid.get(row);
            int col = 0;
            if (rowcells != null) {
                for (; col < rowcells.size(); col++) {
                    Cell<T> cell = rowcells.get(col);
                    if (cell != null) {
                        if (cell.horiMergerDisp > 0) {
                            sb.append("x");
                        } else if (cell.horiMergerDisp < 0) {
                            sb.append("=x");
                            if ((col + 1 > this.lastColumnIndex) || (rowcells.get(col + 1) == null) || (rowcells.get(col + 1).horiMergerDisp >= 0)) {
                                sb.append(" ");
                            }
                        } else {
                            sb.append("x ");
                        }
                    } else {
                        sb.append("- ");
                    }
                }
            }
            for (; col <= this.lastColumnIndex; col++) {
                sb.append("- ");
            }
            if (row < this.lastRowIndex) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}


