package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataFrame {
    private List<String> labels;
    private List<Row> rows;
    private int numbersRenderingBase;

    public static class Row {
        public int index;
        public List<Object> elements;

        Row(int index, List<Object> elements) {
            this.index = index;
            this.elements = elements;
        }

        public Object get(int index) {
            if ((index < 0) || (index >= this.elements.size())) {
                return null;
            }
            return this.elements.get(index);
        }

        public String toString() {
            return Strings.join(",", this.elements);
        }

        public int size() {
            return this.elements.size();
        }
    }

    private DataFrame(List<String> labels) {
        this.labels = labels;
        this.rows = new ArrayList<>();
    }

    public DataFrame(String... labels) {
        this(Arrays.asList(labels));
    }

    public void setRenderedBaseForNumberObjects(int base) {
        this.numbersRenderingBase = base;
    }

    public int getRenderedBaseForNumberObjects() {
        return this.numbersRenderingBase;
    }

    public void clear() {
        this.rows.clear();
    }

    public void addRow(List<Object> elements) {
        this.rows.add(new Row(this.rows.size(), elements));
    }

    public void addRow(Object... elements) {
        addRow(Arrays.asList(elements));
    }

    public List<String> getColumnLabels() {
        return this.labels;
    }

    public Row getRow(int index) {
        return (Row) this.rows.get(index);
    }

    public List<Row> getRows() {
        return this.rows;
    }

    public String getLabelFor(Row row, int index) {
        Object o = row.get(index);
        if (((o instanceof Long)) || ((o instanceof Integer)) || ((o instanceof Short)) || ((o instanceof Byte))) {
            long v = 0L;
            if ((o instanceof Long)) {
                v = ((Long) o).longValue();
            } else if ((o instanceof Integer)) {
                v = ((Integer) o).longValue() & 0xFFFFFFFF;
            } else if ((o instanceof Short)) {
                v = ((Short) o).longValue() & 0xFFFF;
            } else if ((o instanceof Byte)) {
                v = ((Byte) o).longValue() & 0xFF;
            }
            switch (this.numbersRenderingBase) {
                case 16:
                    return String.format("%X", new Object[]{Long.valueOf(v)});
                case 8:
                    return String.format("%o", new Object[]{Long.valueOf(v)});
            }
            return String.format("%d", new Object[]{Long.valueOf(v)});
        }
        return null;
    }
}


