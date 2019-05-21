package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.output.tree.ICodeNode;
import com.pnfsoftware.jeb.core.units.code.ICodeItem;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.parts.units.code.CodeHierarchyView;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class CodeHierarchyDialog extends JebDialog {
    private RcpClientContext context;
    private ICodeUnit unit;
    private ICodeNode baseNode;
    private ICodeNode baseNodeUp;
    private CodeHierarchyView v;
    private CodeHierarchyView v2;
    private String selectedAddress;

    public CodeHierarchyDialog(Shell parent, ICodeUnit unit, ICodeNode baseNode, ICodeNode baseNodeUp, RcpClientContext context) {
        super(parent, S.s(579), true, false);
        this.unit = unit;
        this.baseNode = baseNode;
        this.baseNodeUp = baseNodeUp;
        this.context = context;
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
    }

    public String open() {
        super.open();
        return this.selectedAddress;
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        boolean displayLabels = (this.baseNode != null) && (this.baseNodeUp != null);
        this.v = setup(parent, true, this.baseNode, displayLabels ? "Descending Hierarchy" : null);
        if (this.baseNodeUp != null) {
            this.v2 = setup(parent, false, this.baseNodeUp, displayLabels ? "Ascending Hierarchy" : null);
        }
        createOkayCancelButtons(parent);
    }

    private CodeHierarchyView setup(Composite parent, boolean down, ICodeNode node, String label) {
        if (label != null) {
            UIUtil.createWrappedLabelInGridLayout(parent, 0, label, 1);
        }
        CodeHierarchyView v = new CodeHierarchyView(parent, 0, this.context, this.unit, node, 0, 0, false);
        GridData data = UIUtil.createGridDataSpanHorizontally(1, true, true);
        data.minimumHeight = 200;
        v.setLayoutData(data);
        v.getViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                CodeHierarchyDialog.this.onConfirm();
            }
        });
        RcpClientContext.wrapWidget(this.context, v, "dlgCodehier" + (down ? "Down" : "Up"));
        return v;
    }

    protected void onConfirm() {
        setSelectedAddress();
        super.onConfirm();
    }

    private void setSelectedAddress() {
        CodeHierarchyView widget = this.v;
        if ((this.v2 != null) && (this.v2.isFocusControl())) {
            widget = this.v2;
        }
        ICodeNode node = widget.getSelectedNode();
        if ((node != null) && ((node.getObject() != null))) {
            this.selectedAddress = node.getObject().getAddress();
        }
    }
}


