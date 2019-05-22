package com.pnfsoftware.jeb.rcpclient.iviewers.tree;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class TreeUtil {
    public static class SwtTreeFormatter implements TreeUtil.TreeFormatter<TreeItem> {
        Tree tree;
        TreeColumn[] tcs;

        public SwtTreeFormatter(Tree tree) {
            this.tree = tree;
            this.tcs = tree.getColumns();
        }

        public TreeItem[] getChildren(TreeItem t) {
            return t.getItems();
        }

        public int getPropertySize() {
            return this.tcs.length;
        }

        public String getPropertyName(TreeItem item, int i) {
            return this.tcs[i].getText();
        }

        public String getPropertyValue(TreeItem item, int i) {
            if (this.tcs.length == 0) {
                return item.getText();
            }
            return item.getText(i);
        }
    }

    public static String buildXml(Tree tree, int indent) {
        SwtTreeFormatter formatter = new SwtTreeFormatter(tree);
        return buildXml(formatter, tree.getItems(), indent);
    }

    public static <T> String buildXml(TreeFormatter<T> formatter, T[] items, int indent) {
        StringBuilder stb = new StringBuilder();
        populateXmlExport(stb, 0, indent, formatter, items);
        return stb.toString();
    }

    private static <T> void populateXmlExport(StringBuilder stb, int i, int indent, TreeFormatter<T> formatter, T[] items) {
        for (T item : items) {
            stb.append(StringUtils.leftPad(" ", i * indent)).append("<item>\n");
            populateProperties(stb, i + 1, indent, formatter, item);
            T[] children = formatter.getChildren(item);
            if ((children != null) && (children.length > 0)) {
                populateXmlExport(stb, i + 1, indent, formatter, children);
            }
            stb.append(StringUtils.leftPad(" ", i * indent)).append("</item>\n");
        }
    }

    private static <T> void populateProperties(StringBuilder stb, int i, int indent, TreeFormatter<T> formatter, T item) {
        if (formatter.getPropertySize() == 0) {
            String value = StringEscapeUtils.escapeXml11(formatter.getPropertyValue(item, 0));
            stb.append(StringUtils.leftPad(" ", i * indent)).append("<value>").append(value).append("</value>\n");
        } else {
            for (int j = 0; j < formatter.getPropertySize(); j++) {
                String name = StringEscapeUtils.escapeXml11(formatter.getPropertyName(item, j));
                String value = StringEscapeUtils.escapeXml11(formatter.getPropertyValue(item, j));
                stb.append(StringUtils.leftPad(" ", i * indent)).append("<property name=\"").append(name).append("\" value=\"").append(value).append("\"/>\n");
            }
        }
    }

    public interface TreeFormatter<T> {
        T[] getChildren(T paramT);

        int getPropertySize();

        String getPropertyName(T paramT, int paramInt);

        String getPropertyValue(T paramT, int paramInt);
    }
}


