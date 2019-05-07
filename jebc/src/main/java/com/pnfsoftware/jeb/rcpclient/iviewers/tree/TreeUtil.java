/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.tree;
/*     */
/*     */

import org.apache.commons.lang3.StringUtils;
/*     */ import org.apache.commons.text.StringEscapeUtils;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeColumn;
/*     */ import org.eclipse.swt.widgets.TreeItem;

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
/*     */
/*     */
/*     */ public class TreeUtil
        /*     */ {
    /*     */   public static class SwtTreeFormatter
            /*     */ implements TreeUtil.TreeFormatter<TreeItem>
            /*     */ {
        /*     */ Tree tree;
        /*     */ TreeColumn[] tcs;

        /*     */
        /*     */
        public SwtTreeFormatter(Tree tree)
        /*     */ {
            /*  36 */
            this.tree = tree;
            /*  37 */
            this.tcs = tree.getColumns();
            /*     */
        }

        /*     */
        /*     */
        public TreeItem[] getChildren(TreeItem t)
        /*     */ {
            /*  42 */
            return t.getItems();
            /*     */
        }

        /*     */
        /*     */
        public int getPropertySize()
        /*     */ {
            /*  47 */
            return this.tcs.length;
            /*     */
        }

        /*     */
        /*     */
        public String getPropertyName(TreeItem item, int i)
        /*     */ {
            /*  52 */
            return this.tcs[i].getText();
            /*     */
        }

        /*     */
        /*     */
        public String getPropertyValue(TreeItem item, int i)
        /*     */ {
            /*  57 */
            if (this.tcs.length == 0) {
                /*  58 */
                return item.getText();
                /*     */
            }
            /*  60 */
            return item.getText(i);
            /*     */
        }
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
    /*     */
    /*     */
    public static String buildXml(Tree tree, int indent)
    /*     */ {
        /*  96 */
        SwtTreeFormatter formatter = new SwtTreeFormatter(tree);
        /*  97 */
        return buildXml(formatter, tree.getItems(), indent);
        /*     */
    }

    /*     */
    /*     */
    public static <T> String buildXml(TreeFormatter<T> formatter, T[] items, int indent) {
        /* 101 */
        StringBuilder stb = new StringBuilder();
        /* 102 */
        populateXmlExport(stb, 0, indent, formatter, items);
        /* 103 */
        return stb.toString();
        /*     */
    }

    /*     */
    /*     */
    private static <T> void populateXmlExport(StringBuilder stb, int i, int indent, TreeFormatter<T> formatter, T[] items)
    /*     */ {
        /* 108 */
        for (T item : items) {
            /* 109 */
            stb.append(StringUtils.leftPad(" ", i * indent)).append("<item>\n");
            /* 110 */
            populateProperties(stb, i + 1, indent, formatter, item);
            /* 111 */
            T[] children = formatter.getChildren(item);
            /* 112 */
            if ((children != null) && (children.length > 0)) {
                /* 113 */
                populateXmlExport(stb, i + 1, indent, formatter, children);
                /*     */
            }
            /* 115 */
            stb.append(StringUtils.leftPad(" ", i * indent)).append("</item>\n");
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    private static <T> void populateProperties(StringBuilder stb, int i, int indent, TreeFormatter<T> formatter, T item)
    /*     */ {
        /* 122 */
        if (formatter.getPropertySize() == 0)
            /*     */ {
            /* 124 */
            String value = StringEscapeUtils.escapeXml11(formatter.getPropertyValue(item, 0));
            /* 125 */
            stb.append(StringUtils.leftPad(" ", i * indent)).append("<value>").append(value).append("</value>\n");
            /*     */
        }
        /*     */
        else {
            /* 128 */
            for (int j = 0; j < formatter.getPropertySize(); j++) {
                /* 129 */
                String name = StringEscapeUtils.escapeXml11(formatter.getPropertyName(item, j));
                /* 130 */
                String value = StringEscapeUtils.escapeXml11(formatter.getPropertyValue(item, j));
                /* 131 */
                stb.append(StringUtils.leftPad(" ", i * indent)).append("<property name=\"").append(name)
/* 132 */.append("\" value=\"").append(value).append("\"/>\n");
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   public static abstract interface TreeFormatter<T>
            /*     */ {
        /*     */
        public abstract T[] getChildren(T paramT);

        /*     */
        /*     */
        public abstract int getPropertySize();

        /*     */
        /*     */
        public abstract String getPropertyName(T paramT, int paramInt);

        /*     */
        /*     */
        public abstract String getPropertyValue(T paramT, int paramInt);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\tree\TreeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */