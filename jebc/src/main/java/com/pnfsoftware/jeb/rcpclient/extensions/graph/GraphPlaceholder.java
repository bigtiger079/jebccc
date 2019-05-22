package com.pnfsoftware.jeb.rcpclient.extensions.graph;

import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public abstract class GraphPlaceholder<G extends AbstractGraph> extends Composite {
    G graph;
    GraphPreview<G> preview;
    ToolBar toolbar;

    public GraphPlaceholder(Composite parent, int style, boolean createPreview, boolean createToolbar) {
        super(parent, style);
        FormLayout layout = new FormLayout();
        setLayout(layout);
        this.graph = createGraph(this, null);
        attachGraph();
        if (createPreview) {
            this.preview = new GraphPreview(this, 2048, true);
            this.preview.moveAbove(this.graph);
            this.preview.setGraph(this.graph);
            FormData formData = new FormData();
            formData.top = new FormAttachment(0, 0);
            formData.left = new FormAttachment(0, 0);
            formData.bottom = new FormAttachment(10, 0);
            formData.right = new FormAttachment(10, 0);
            this.preview.setLayoutData(formData);
        }
        if (createToolbar) {
            this.toolbar = new ToolBar(this, 320);
            this.toolbar.moveAbove(this.graph);
            FormData formData = new FormData();
            formData.top = new FormAttachment(0, 0);
            formData.left = (this.preview == null ? new FormAttachment(0, 0) : new FormAttachment(this.preview));
            formData.right = new FormAttachment(100, 0);
            this.toolbar.setLayoutData(formData);
        }
    }

    protected abstract G createGraph(GraphPlaceholder<G> paramGraphPlaceholder, G paramG);

    private void attachGraph() {
        FormData formData = new FormData();
        formData.top = new FormAttachment(0, 0);
        formData.left = new FormAttachment(0, 0);
        formData.bottom = new FormAttachment(100, 0);
        formData.right = new FormAttachment(100, 0);
        this.graph.setLayoutData(formData);
    }

    public G getGraph() {
        return this.graph;
    }

    public GraphPreview<G> getGraphPreview() {
        return this.preview;
    }

    public ToolBar getToolbar() {
        return this.toolbar;
    }

    public boolean setFocus() {
        return this.graph.setFocus();
    }

    public G reset() {
        if (this.graph != null) {
            this.graph.dispose();
            this.graph = null;
        }
        this.graph = createGraph(this, this.graph);
        attachGraph();
        if (this.preview != null) {
            this.preview.setGraph(this.graph);
        }
        return this.graph;
    }

    private void verifyPreview() {
        if (this.preview == null) {
            throw new IllegalStateException("There is no preview widget");
        }
    }

    private void verifyToolbar() {
        if (this.toolbar == null) {
            throw new IllegalStateException("There is no toolbar widget");
        }
    }

    public static final String standardHepMessage = "Standard graph controls:\n\n" + UI.MOD1 + "+Wheel ... Zoom in or out\n] ... Zoom in\n[ ... Zoom out\n" + UI.MOD1 + "+\\ (slash) ... Reset zoom\n\\ (slash) ... Center the graph\nClick on Edge ... Go to the destination node\n" + UI.MOD1 + "+Click on Edge ... Go to the source node\n";

    public ToolItem addHelpButtonToToolbar(String msg) {
        verifyToolbar();
        final String msg0 = Strings.safe(msg, standardHepMessage);
        ToolItem item = new ToolItem(this.toolbar, 8);
        item.setText("Help");
        item.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                UI.info(null, "Help", msg0);
            }
        });
        return item;
    }

    public Combo addComboBox(List<String> entries, int initialSelection) {
        verifyToolbar();
        ToolItem item = new ToolItem(this.toolbar, 2);
        item = new ToolItem(this.toolbar, 2);
        Combo w = new Combo(this.toolbar, 8);
        for (String entry : entries) {
            w.add(entry);
        }
        w.select(initialSelection);
        w.pack();
        item.setWidth(w.getSize().x);
        item.setControl(w);
        return w;
    }

    public Button addPushbox(String name) {
        verifyToolbar();
        ToolItem item = new ToolItem(this.toolbar, 2);
        item = new ToolItem(this.toolbar, 2);
        Button w = new Button(this.toolbar, 8);
        w.setText(name);
        w.pack();
        item.setWidth(w.getSize().x);
        item.setControl(w);
        return w;
    }

    public Button addCheckbox(String name, boolean initialState) {
        verifyToolbar();
        ToolItem item = new ToolItem(this.toolbar, 2);
        item = new ToolItem(this.toolbar, 2);
        Button w = new Button(this.toolbar, 32);
        w.setText(name);
        w.setSelection(initialState);
        w.pack();
        item.setWidth(w.getSize().x);
        item.setControl(w);
        return w;
    }

    public void addModesBoxToToolbar() {
        List<String> modenames = new ArrayList<>();
        for (GraphMode mode : this.graph.getSupportedModes()) {
            modenames.add(mode.getName());
        }
        addComboBox(modenames, 0).addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (GraphPlaceholder.this.graph == null) {
                    return;
                }
                int index = ((Combo) e.widget).getSelectionIndex();
                if ((index < 0) || (index >= GraphPlaceholder.this.graph.getSupportedModes().size())) {
                    return;
                }
                GraphPlaceholder.this.graph.setMode(GraphPlaceholder.this.graph.getSupportedModes().get(index));
            }
        });
    }
}


