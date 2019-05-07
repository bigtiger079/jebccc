/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.tree.ICodeNode;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeClass;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeField;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeMethod;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodePackage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
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
/*     */ public class CodeNodeWrapper
        /*     */ implements Comparable<CodeNodeWrapper>
        /*     */ {
    /*     */   private ICodeNode node;
    /*     */   private ICodeItem item;
    /*     */   private String name;

    /*     */
    /*     */
    static List<CodeNodeWrapper> wrapNodes(Collection<? extends ICodeNode> nodes)
    /*     */ {
        /*  37 */
        List<CodeNodeWrapper> r = new ArrayList(nodes.size());
        /*  38 */
        for (ICodeNode node : nodes) {
            /*  39 */
            r.add(new CodeNodeWrapper(node));
            /*     */
        }
        /*  41 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    static List<ICodeNode> unwrapNodes(Collection<CodeNodeWrapper> wnodes) {
        /*  45 */
        List<ICodeNode> r = new ArrayList(wnodes.size());
        /*  46 */
        for (CodeNodeWrapper wnode : wnodes) {
            /*  47 */
            r.add(wnode.node);
            /*     */
        }
        /*  49 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public CodeNodeWrapper(ICodeNode node)
    /*     */ {
        /*  57 */
        this.node = node;
        /*  58 */
        this.item = node.getObject();
        /*  59 */
        this.name = (this.item == null ? null : this.item.getName(true));
        /*     */
    }

    /*     */
    /*     */
    public int compareTo(CodeNodeWrapper o)
    /*     */ {
        /*  64 */
        return compare(this, o);
        /*     */
    }

    /*     */
    /*     */
    private static int compare(CodeNodeWrapper a, CodeNodeWrapper b) {
        /*  68 */
        ICodeItem item1 = a.item;
        /*  69 */
        ICodeItem item2 = b.item;
        /*     */
        /*  71 */
        int score1 = getBaseScore(item1);
        /*  72 */
        int score2 = getBaseScore(item2);
        /*  73 */
        if (score1 > score2) {
            /*  74 */
            return -1;
            /*     */
        }
        /*  76 */
        if (score1 < score2) {
            /*  77 */
            return 1;
            /*     */
        }
        /*     */
        /*  80 */
        Integer r = compareNullFirst(item1, item2);
        /*  81 */
        if (r != null) {
            /*  82 */
            return r.intValue();
            /*     */
        }
        /*     */
        /*  85 */
        String name1 = a.name;
        /*  86 */
        String name2 = b.name;
        /*     */
        /*  88 */
        r = compareNullFirst(name1, name2);
        /*  89 */
        if (r != null) {
            /*  90 */
            return r.intValue();
            /*     */
        }
        /*     */
        /*  93 */
        return name1.compareTo(name2);
        /*     */
    }

    /*     */
    /*     */
    private static int getBaseScore(ICodeItem item) {
        /*  97 */
        if ((item instanceof ICodePackage)) {
            /*  98 */
            return 100;
            /*     */
        }
        /* 100 */
        if ((item instanceof ICodeClass)) {
            /* 101 */
            return 90;
            /*     */
        }
        /* 103 */
        if ((item instanceof ICodeField)) {
            /* 104 */
            return 80;
            /*     */
        }
        /* 106 */
        if ((item instanceof ICodeMethod)) {
            /* 107 */
            return 70;
            /*     */
        }
        /* 109 */
        return 0;
        /*     */
    }

    /*     */
    /*     */
    private static Integer compareNullFirst(Object item1, Object item2) {
        /* 113 */
        if (item1 == null) {
            /* 114 */
            if (item2 == null) {
                /* 115 */
                return Integer.valueOf(0);
                /*     */
            }
            /*     */
            /* 118 */
            return Integer.valueOf(-1);
            /*     */
        }
        /* 120 */
        if (item2 == null) {
            /* 121 */
            return Integer.valueOf(1);
            /*     */
        }
        /* 123 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\CodeNodeWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */