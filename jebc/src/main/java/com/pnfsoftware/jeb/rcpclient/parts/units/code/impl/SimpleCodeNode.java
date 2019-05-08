
package com.pnfsoftware.jeb.rcpclient.parts.units.code.impl;

import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.core.output.tree.ICodeNode;
import com.pnfsoftware.jeb.core.units.code.ICodeItem;

import java.util.ArrayList;
import java.util.List;

public class SimpleCodeNode
        implements ICodeNode {
    private ICodeItem item;
    private ICodeNode parent = null;
    private List<? extends ICodeNode> children = new ArrayList();
    private boolean expanded;

    public SimpleCodeNode(ICodeItem item, ICodeNode parent, List<? extends ICodeNode> children, boolean expanded) {
        this.item = item;
        this.parent = parent;
        this.children = children;
        this.expanded = expanded;
    }

    public int getInitialExpansion() {
        return this.expanded ? 1 : 0;
    }

    public String getLabel() {
        return this.item.getName(true);
    }

    public String[] getAdditionalLabels() {
        return null;
    }

    public ItemClassIdentifiers getClassId() {
        return null;
    }

    public long getItemId() {
        return this.item.getItemId();
    }

    public int getItemFlags() {
        return 0;
    }

    public List<? extends ICodeNode> getChildren() {
        return this.children;
    }

    public ICodeNode getParent() {
        return this.parent;
    }

    public ICodeItem getObject() {
        return this.item;
    }

    public ICodeNode findNodeByObject(ICodeItem target) {
        for (ICodeNode child : this.children) {
            ICodeNode r = child.findNodeByObject(target);
            if (r != null) {
                return r;
            }
        }
        return null;
    }
}


