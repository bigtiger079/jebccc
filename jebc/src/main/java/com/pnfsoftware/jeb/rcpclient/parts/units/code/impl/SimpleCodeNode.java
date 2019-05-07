/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code.impl;
/*    */
/*    */

import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
/*    */ import com.pnfsoftware.jeb.core.output.tree.ICodeNode;
/*    */ import com.pnfsoftware.jeb.core.units.code.ICodeItem;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class SimpleCodeNode
        /*    */ implements ICodeNode
        /*    */ {
    /*    */   private ICodeItem item;
    /* 24 */   private ICodeNode parent = null;
    /*    */
    /* 26 */   private List<? extends ICodeNode> children = new ArrayList();
    /*    */   private boolean expanded;

    /*    */
    /*    */
    public SimpleCodeNode(ICodeItem item, ICodeNode parent, List<? extends ICodeNode> children, boolean expanded) {
        /* 30 */
        this.item = item;
        /* 31 */
        this.parent = parent;
        /* 32 */
        this.children = children;
        /* 33 */
        this.expanded = expanded;
        /*    */
    }

    /*    */
    /*    */
    public int getInitialExpansion()
    /*    */ {
        /* 38 */
        return this.expanded ? 1 : 0;
        /*    */
    }

    /*    */
    /*    */
    public String getLabel()
    /*    */ {
        /* 43 */
        return this.item.getName(true);
        /*    */
    }

    /*    */
    /*    */
    public String[] getAdditionalLabels()
    /*    */ {
        /* 48 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public ItemClassIdentifiers getClassId()
    /*    */ {
        /* 53 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public long getItemId()
    /*    */ {
        /* 58 */
        return this.item.getItemId();
        /*    */
    }

    /*    */
    /*    */
    public int getItemFlags()
    /*    */ {
        /* 63 */
        return 0;
        /*    */
    }

    /*    */
    /*    */
    public List<? extends ICodeNode> getChildren()
    /*    */ {
        /* 68 */
        return this.children;
        /*    */
    }

    /*    */
    /*    */
    public ICodeNode getParent()
    /*    */ {
        /* 73 */
        return this.parent;
        /*    */
    }

    /*    */
    /*    */
    public ICodeItem getObject()
    /*    */ {
        /* 78 */
        return this.item;
        /*    */
    }

    /*    */
    /*    */
    public ICodeNode findNodeByObject(ICodeItem target)
    /*    */ {
        /* 83 */
        for (ICodeNode child : this.children) {
            /* 84 */
            ICodeNode r = child.findNodeByObject(target);
            /* 85 */
            if (r != null) {
                /* 86 */
                return r;
                /*    */
            }
            /*    */
        }
        /* 89 */
        return null;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\impl\SimpleCodeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */