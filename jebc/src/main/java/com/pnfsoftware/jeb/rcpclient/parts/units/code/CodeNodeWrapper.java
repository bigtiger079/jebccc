package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.core.output.tree.ICodeNode;
import com.pnfsoftware.jeb.core.units.code.ICodeClass;
import com.pnfsoftware.jeb.core.units.code.ICodeField;
import com.pnfsoftware.jeb.core.units.code.ICodeItem;
import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
import com.pnfsoftware.jeb.core.units.code.ICodePackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CodeNodeWrapper implements Comparable<CodeNodeWrapper> {
    private ICodeNode node;
    private ICodeItem item;
    private String name;

    static List<CodeNodeWrapper> wrapNodes(Collection<? extends ICodeNode> nodes) {
        List<CodeNodeWrapper> r = new ArrayList<>(nodes.size());
        for (ICodeNode node : nodes) {
            r.add(new CodeNodeWrapper(node));
        }
        return r;
    }

    static List<ICodeNode> unwrapNodes(Collection<CodeNodeWrapper> wnodes) {
        List<ICodeNode> r = new ArrayList<>(wnodes.size());
        for (CodeNodeWrapper wnode : wnodes) {
            r.add(wnode.node);
        }
        return r;
    }

    public CodeNodeWrapper(ICodeNode node) {
        this.node = node;
        this.item = node.getObject();
        this.name = (this.item == null ? null : this.item.getName(true));
    }

    public int compareTo(CodeNodeWrapper o) {
        return compare(this, o);
    }

    private static int compare(CodeNodeWrapper a, CodeNodeWrapper b) {
        ICodeItem item1 = a.item;
        ICodeItem item2 = b.item;
        int score1 = getBaseScore(item1);
        int score2 = getBaseScore(item2);
        if (score1 > score2) {
            return -1;
        }
        if (score1 < score2) {
            return 1;
        }
        Integer r = compareNullFirst(item1, item2);
        if (r != null) {
            return r;
        }
        String name1 = a.name;
        String name2 = b.name;
        r = compareNullFirst(name1, name2);
        if (r != null) {
            return r;
        }
        return name1.compareTo(name2);
    }

    private static int getBaseScore(ICodeItem item) {
        if ((item instanceof ICodePackage)) {
            return 100;
        }
        if ((item instanceof ICodeClass)) {
            return 90;
        }
        if ((item instanceof ICodeField)) {
            return 80;
        }
        if ((item instanceof ICodeMethod)) {
            return 70;
        }
        return 0;
    }

    private static Integer compareNullFirst(Object item1, Object item2) {
        if (item1 == null) {
            if (item2 == null) {
                return 0;
            }
            return -1;
        }
        if (item2 == null) {
            return 1;
        }
        return null;
    }
}


