/*     */
package com.pnfsoftware.jeb.rcpclient.util;
/*     */
/*     */

import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class DataFrame
        /*     */ {
    /*     */   private List<String> labels;
    /*     */   private List<Row> rows;
    /*     */   private int numbersRenderingBase;

    /*     */
    /*     */   public static class Row
            /*     */ {
        /*     */     public int index;
        /*     */     public List<Object> elements;

        /*     */
        /*     */     Row(int index, List<Object> elements)
        /*     */ {
            /*  36 */
            this.index = index;
            /*  37 */
            this.elements = elements;
            /*     */
        }

        /*     */
        /*     */
        public Object get(int index) {
            /*  41 */
            if ((index < 0) || (index >= this.elements.size())) {
                /*  42 */
                return null;
                /*     */
            }
            /*  44 */
            return this.elements.get(index);
            /*     */
        }

        /*     */
        /*     */
        public String toString()
        /*     */ {
            /*  49 */
            return Strings.join(",", this.elements);
            /*     */
        }

        /*     */
        /*     */
        public int size() {
            /*  53 */
            return this.elements.size();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private DataFrame(List<String> labels)
    /*     */ {
        /*  62 */
        this.labels = labels;
        /*  63 */
        this.rows = new ArrayList();
        /*     */
    }

    /*     */
    /*     */
    public DataFrame(String... labels) {
        /*  67 */
        this(Arrays.asList(labels));
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void setRenderedBaseForNumberObjects(int base)
    /*     */ {
        /*  76 */
        this.numbersRenderingBase = base;
        /*     */
    }

    /*     */
    /*     */
    public int getRenderedBaseForNumberObjects() {
        /*  80 */
        return this.numbersRenderingBase;
        /*     */
    }

    /*     */
    /*     */
    public void clear() {
        /*  84 */
        this.rows.clear();
        /*     */
    }

    /*     */
    /*     */
    public void addRow(List<Object> elements) {
        /*  88 */
        this.rows.add(new Row(this.rows.size(), elements));
        /*     */
    }

    /*     */
    /*     */
    public void addRow(Object... elements) {
        /*  92 */
        addRow(Arrays.asList(elements));
        /*     */
    }

    /*     */
    /*     */
    public List<String> getColumnLabels() {
        /*  96 */
        return this.labels;
        /*     */
    }

    /*     */
    /*     */
    public Row getRow(int index) {
        /* 100 */
        return (Row) this.rows.get(index);
        /*     */
    }

    /*     */
    /*     */
    public List<Row> getRows() {
        /* 104 */
        return this.rows;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public String getLabelFor(Row row, int index)
    /*     */ {
        /* 121 */
        Object o = row.get(index);
        /* 122 */
        if (((o instanceof Long)) || ((o instanceof Integer)) || ((o instanceof Short)) || ((o instanceof Byte))) {
            /* 123 */
            long v = 0L;
            /* 124 */
            if ((o instanceof Long)) {
                /* 125 */
                v = ((Long) o).longValue();
                /*     */
            }
            /* 127 */
            else if ((o instanceof Integer)) {
                /* 128 */
                v = ((Integer) o).longValue() & 0xFFFFFFFF;
                /*     */
            }
            /* 130 */
            else if ((o instanceof Short)) {
                /* 131 */
                v = ((Short) o).longValue() & 0xFFFF;
                /*     */
            }
            /* 133 */
            else if ((o instanceof Byte)) {
                /* 134 */
                v = ((Byte) o).longValue() & 0xFF;
                /*     */
            }
            /* 136 */
            switch (this.numbersRenderingBase) {
                /*     */
                case 16:
                    /* 138 */
                    return String.format("%X", new Object[]{Long.valueOf(v)});
                /*     */
                case 8:
                    /* 140 */
                    return String.format("%o", new Object[]{Long.valueOf(v)});
                /*     */
            }
            /* 142 */
            return String.format("%d", new Object[]{Long.valueOf(v)});
            /*     */
        }
        /*     */
        /* 145 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\DataFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */