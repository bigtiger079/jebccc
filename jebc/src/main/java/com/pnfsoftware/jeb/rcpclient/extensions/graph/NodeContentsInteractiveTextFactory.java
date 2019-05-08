package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.iviewers.StyleManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.ItemStyleProvider2;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;

import org.eclipse.swt.widgets.Display;

public class NodeContentsInteractiveTextFactory implements Iterable<NodeContentsInteractiveTextView> {
    static final ILogger logger = GlobalLog.getLogger(NodeContentsInteractiveTextFactory.class);
    Display display;
    FontManager fontManager;
    ItemStyleProvider2 styleProvider;
    IUnit unit;
    IStatusIndicator statusIndicator;
    IOperable master;
    IGraphController controller;
    RcpClientContext context;
    IdentityHashMap<GraphNode, NodeContentsInteractiveTextView> map = new IdentityHashMap();
    IdentityHashMap<NodeContentsInteractiveTextView, GraphNode> rmap = new IdentityHashMap();

    public NodeContentsInteractiveTextFactory(Display display, FontManager fontManager, StyleManager styleman, IUnit unit, IStatusIndicator statusIndicator, IOperable master, IGraphController controller, RcpClientContext context) {
        this.display = display;
        this.fontManager = fontManager;
        this.styleProvider = new ItemStyleProvider2(styleman);
        this.unit = unit;
        this.statusIndicator = statusIndicator;
        this.master = master;
        this.controller = controller;
        this.context = context;
    }

    public NodeContentsInteractiveTextView create(GraphNode node, ITextDocument doc) {
        NodeContentsInteractiveTextView t = new NodeContentsInteractiveTextView(node, 0, doc, this.fontManager, this.styleProvider, this.unit, this.statusIndicator, this.master, this.controller, this.context);
        this.map.put(node, t);
        this.rmap.put(t, node);
        return t;
    }

    public Iterator<NodeContentsInteractiveTextView> iterator() {
        return this.map.values().iterator();
    }

    public NodeContentsInteractiveTextView getContentsForNode(GraphNode node) {
        return (NodeContentsInteractiveTextView) this.map.get(node);
    }

    public GraphNode getNodeForContents(NodeContentsInteractiveTextView contents) {
        return (GraphNode) this.rmap.get(contents);
    }
}


