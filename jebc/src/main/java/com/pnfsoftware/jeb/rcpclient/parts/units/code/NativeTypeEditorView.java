package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
import com.pnfsoftware.jeb.core.units.code.asm.type.PrettyTypeFormatter;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.WidgetActionWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.InfiniTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractInfiniTableSectionProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.InfiniTableViewer;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class NativeTypeEditorView extends Composite {
    private static final ILogger logger = GlobalLog.getLogger(NativeTypeEditorView.class);
    private static final int previewDepthLevel = 10;
    private Font codefont;
    private INativeCodeUnit<?> unit;
    private IStructureType type;
    private InfiniTableView view;
    private InfiniTableViewer viewer;
    private StructFieldsProvider provider;
    private Combo comboSelectedType;
    private Text widgetPreview;
    private Button btnPreviewDeep;
    private Button btnPreviewOffsets;

    public NativeTypeEditorView(final Composite parent, int style, final INativeCodeUnit<?> unit) {
        super(parent, style);
        setLayout(new GridLayout(1, false));
        if (unit == null) {
            throw new NullPointerException();
        }
        this.unit = unit;
        ToolBar bar = new ToolBar(this, 320);
        bar.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, false));
        ToolItem item = new ToolItem(bar, 2);
        Label label = new Label(bar, 0);
        label.setText("Select a type: ");
        label.pack();
        item.setWidth(label.getSize().x);
        item.setControl(label);
        item = new ToolItem(bar, 2);
        this.comboSelectedType = new Combo(bar, 8);
        refreshTypesList();
        this.comboSelectedType.pack();
        item.setWidth(this.comboSelectedType.getSize().x);
        item.setControl(this.comboSelectedType);
        item = new ToolItem(bar, 2);
        this.btnPreviewDeep = new Button(bar, 32);
        this.btnPreviewDeep.setText("Deep Preview   ");
        this.btnPreviewDeep.setSelection(true);
        this.btnPreviewDeep.setToolTipText(String.format("Include nested structures (up to %d) in preview", 10));
        this.btnPreviewDeep.pack();
        item.setWidth(this.btnPreviewDeep.getSize().x);
        item.setControl(this.btnPreviewDeep);
        this.btnPreviewDeep.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                NativeTypeEditorView.this.generatePreview();
            }
        });
        item = new ToolItem(bar, 2);
        this.btnPreviewOffsets = new Button(bar, 32);
        this.btnPreviewOffsets.setText("Include Offsets   ");
        this.btnPreviewOffsets.setToolTipText("Display field offsets as comments in preview");
        this.btnPreviewOffsets.pack();
        item.setWidth(this.btnPreviewOffsets.getSize().x);
        item.setControl(this.btnPreviewOffsets);
        this.btnPreviewOffsets.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                NativeTypeEditorView.this.generatePreview();
            }
        });
        item = new ToolItem(bar, 2);
        Button btnCreate = UIUtil.createPushbox(bar, "Create", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                TextDialog dlg = new TextDialog(parent.getShell(), "Structure Name (global namespace)", "", null);
                String structName = dlg.open();
                if (structName != null) {
                    IStructureType t = unit.getTypeManager().createStructure(structName);
                    NativeTypeEditorView.this.refreshTypesList();
                    NativeTypeEditorView.this.setInput(t);
                    NativeTypeEditorView.this.view.setFocus();
                }
            }
        });
        btnCreate.pack();
        item.setWidth(btnCreate.getSize().x);
        item.setControl(btnCreate);
        item = new ToolItem(bar, 2);
        Button btnHelp = UIUtil.createPushbox(bar, S.s(365), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String msg = "This widget is the native structure editor. The left-hand side represents the selected data structure layout.\n\nRight click on a row, at the given offset, to perform any of the following actions:\n- Define a field\n- Undefine a field\n- Rename a field\n- Create an array field\n- Set or select the type of a field\n\nYou may scroll down to access higher offsets.\n\nThe right-hand side represents a standard C representation of the structure.";
                MessageDialog.openInformation(parent.getShell(), "Controls", msg);
            }
        });
        btnHelp.pack();
        item.setWidth(btnHelp.getSize().x);
        item.setControl(btnHelp);
        bar.pack();
        this.comboSelectedType.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = NativeTypeEditorView.this.comboSelectedType.getSelectionIndex();
                if (index >= 0) {
                    String signature = NativeTypeEditorView.this.comboSelectedType.getItem(index);
                    NativeTypeEditorView.this.onTypeSelectionChange(signature);
                }
            }
        });
        SashForm sashContainer = new SashForm(this, 256);
        sashContainer.setLayoutData(UIUtil.createGridDataFill(true, true));
        this.view = new InfiniTableView(sashContainer, 4, new String[]{"Offset", "Size", "Name", "Type", "Comment"});
        this.viewer = new InfiniTableViewer(this.view);
        this.provider = new StructFieldsProvider();
        this.viewer.setContentProvider(this.provider);
        this.viewer.setLabelProvider(new StructFieldLabelProvider(this.provider));
        this.viewer.setTopId(0L, false);
        this.viewer.setMinimumAllowedId(0L);
        this.viewer.setInput(null);
        this.widgetPreview = new Text(sashContainer, 2818);
        this.widgetPreview.setEditable(false);
        WidgetActionWrapper actionWrapper = new WidgetActionWrapper(this.view.getTable());
        actionWrapper.registerAction(new StructEditorActionDefine(this));
        actionWrapper.registerAction(new StructEditorActionUndefine(this));
        actionWrapper.registerAction(new StructEditorActionRename(this));
        actionWrapper.registerAction(new StructEditorActionDefineArray(this));
        actionWrapper.registerAction(new StructEditorActionSetType(this));
        actionWrapper.registerAction(new StructEditorActionSelectType(this));
    }

    public INativeCodeUnit<?> getInputUnit() {
        return this.unit;
    }

    public IStructureType getInputType() {
        return this.type;
    }

    public void setCodefont(Font codefont) {
        this.codefont = codefont;
        if (codefont != null) {
            this.widgetPreview.setFont(codefont);
        }
    }

    private void refreshTypesList() {
        this.comboSelectedType.removeAll();
        for (INativeType t : this.unit.getTypeManager().getTypes()) {
            if ((t instanceof IStructureType)) {
                this.comboSelectedType.add(t.getSignature(true));
            }
        }
    }

    private void onTypeSelectionChange(String signature) {
        INativeType type = this.unit.getTypeManager().getType(signature);
        if (!(type instanceof IStructureType)) {
            return;
        }
        setInput((IStructureType) type);
    }

    void refresh() {
        int index0 = this.view.getTable().getSelectionIndex();
        this.viewer.refresh();
        generatePreview();
        IStructuredSelection sel1 = (IStructuredSelection) this.viewer.getSelection();
        if (sel1.isEmpty()) {
            this.view.getTable().setSelection(index0);
        }
    }

    public void setInput(IStructureType type) {
        this.viewer.setInput(type);
        if (type != null) {
            int i = 0;
            for (String itemName : this.comboSelectedType.getItems()) {
                if (itemName.equals(type.getSignature(true))) {
                    this.comboSelectedType.select(i);
                }
                i++;
            }
        }
        generatePreview();
    }

    public ItemEntry getSelectedEntry() {
        IStructuredSelection sel = (IStructuredSelection) this.viewer.getSelection();
        return sel.isEmpty() ? null : (ItemEntry) sel.getFirstElement();
    }

    void generatePreview() {
        IStructureType type = (IStructureType) this.viewer.getInput();
        if (type == null) {
            this.widgetPreview.setText("<No Preview>");
            return;
        }
        boolean previewDeep = this.btnPreviewDeep.getSelection();
        boolean previewOffsets = this.btnPreviewOffsets.getSelection();
        PrettyTypeFormatter f = new PrettyTypeFormatter(type);
        String str = f.format(previewDeep ? 10 : 1, previewOffsets);
        if ((type.getPadding() != 1) || (type.getAlignment() != 0)) {
            str = String.format("// Size: %d, Padding: %d, Alignment: %d\n%s", type.getSize(), type.getPadding(), type.getAlignment(), str);
        }
        this.widgetPreview.setText(str);
    }

    class StructFieldLabelProvider extends DefaultCellLabelProvider {
        public StructFieldLabelProvider(NativeTypeEditorView.StructFieldsProvider provider) {
            super(provider);
        }

        public void update(ViewerCell cell) {
            super.update(cell);
            if ((NativeTypeEditorView.this.codefont != null) && ((cell.getColumnIndex() == 0) || (cell.getColumnIndex() == 1))) {
                cell.setFont(NativeTypeEditorView.this.codefont);
            }
            ItemEntry entry = (ItemEntry) cell.getElement();
            if (!entry.slack) {
                int rgb = entry.type != null ? 16777136 : 16777184;
                cell.setBackground(UIAssetManager.getInstance().getColor(rgb));
            }
        }

        public String getStringAt(Object element, int key) {
            ItemEntry e = (ItemEntry) element;
            switch (key) {
                case 0:
                    return String.format("+%08X", e.offset);
                case 1:
                    return String.format("%04X", e.size);
                case 3:
                    if (e.type != null) {
                        return e.type.getSignature(true);
                    }
                    break;
            }
            return super.getStringAt(element, key);
        }
    }

    class StructFieldsProvider extends AbstractInfiniTableSectionProvider {
        StructFieldsProvider() {
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            NativeTypeEditorView.this.type = ((IStructureType) newInput);
        }

        public Object[] getRowElements(Object row) {
            ItemEntry e = (ItemEntry) row;
            return new Object[]{e.offset, e.size, e.name, e.type, e.comment};
        }

        public Object[] get(Object inputElement, long id, int cnt) {
            Assert.a(NativeTypeEditorView.this.type == inputElement);
            return readFields(id, cnt);
        }

        private Object[] readFields(long id, int cnt) {
            Assert.a((id >= 0L) && (cnt >= 0));
            long id1 = id + cnt;
            List<ItemEntry> r = new ArrayList<>();
            List<? extends IStructureTypeField> fields = NativeTypeEditorView.this.type.getFieldsWithGaps();
            int i = 0;
            long currentId = 0L;
            while ((i < fields.size()) && (id < id1)) {
                IStructureTypeField elt = fields.get(i);
                long nextId = elt.isSynthetic() ? currentId + elt.getSize() : currentId + 1L;
                if ((id >= currentId) && (id < nextId)) {
                    if (!elt.isSynthetic()) {
                        Assert.a(id == currentId);
                        r.add(new ItemEntry(elt.getOffset(), elt.getSize(), elt.getName(), elt.getType(), elt.isBitfield() ? String.format("bitfield [%d:%d[", elt.getBitstart(), elt.getBitend()) : null, false));
                        id += 1L;
                    } else {
                        int off = elt.getOffset() + (int) (id - currentId);
                        while ((id < nextId) && (id < id1)) {
                            r.add(new ItemEntry(off, 1, null, null, null, false));
                            off++;
                            id += 1L;
                        }
                    }
                    currentId = id;
                } else {
                    currentId = nextId;
                }
                i++;
            }
            if (id < id1) {
                long typeLastId = 0L;
                i = 0;
                while (i < fields.size()) {
                    IStructureTypeField elt = fields.get(i);
                    typeLastId += (elt.isSynthetic() ? elt.getSize() : 1L);
                    i++;
                }
                int delta = (int) (NativeTypeEditorView.this.type.getSize() - typeLastId);
                while (id < id1) {
                    r.add(new ItemEntry((int) (id + delta), 1, null, null, null, true));
                    id += 1L;
                }
            }
            return r.toArray();
        }
    }
}


