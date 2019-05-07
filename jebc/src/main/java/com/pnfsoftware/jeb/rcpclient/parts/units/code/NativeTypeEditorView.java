/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureType;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.IStructureTypeField;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.PrettyTypeFormatter;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.WidgetActionWrapper;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.InfiniTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractInfiniTableSectionProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.InfiniTableViewer;
/*     */ import com.pnfsoftware.jeb.util.base.Assert;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.dialogs.MessageDialog;
/*     */ import org.eclipse.jface.viewers.IStructuredSelection;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.jface.viewers.ViewerCell;
/*     */ import org.eclipse.swt.custom.SashForm;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.eclipse.swt.widgets.ToolBar;
/*     */ import org.eclipse.swt.widgets.ToolItem;

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
/*     */ public class NativeTypeEditorView
        /*     */ extends Composite
        /*     */ {
    /*  56 */   private static final ILogger logger = GlobalLog.getLogger(NativeTypeEditorView.class);
    /*     */
    /*     */   private static final int previewDepthLevel = 10;
    /*     */
    /*     */   private Font codefont;
    /*     */
    /*     */   private INativeCodeUnit<?> unit;
    /*     */
    /*     */   private IStructureType type;
    /*     */
    /*     */   private InfiniTableView view;
    /*     */   private InfiniTableViewer viewer;
    /*     */   private StructFieldsProvider provider;
    /*     */   private Combo comboSelectedType;
    /*     */   private Text widgetPreview;
    /*     */   private Button btnPreviewDeep;
    /*     */   private Button btnPreviewOffsets;

    /*     */
    /*     */
    public NativeTypeEditorView(final Composite parent, int style, final INativeCodeUnit<?> unit)
    /*     */ {
        /*  76 */
        super(parent, style);
        /*  77 */
        setLayout(new GridLayout(1, false));
        /*     */
        /*  79 */
        if (unit == null) {
            /*  80 */
            throw new NullPointerException();
            /*     */
        }
        /*  82 */
        this.unit = unit;
        /*     */
        /*  84 */
        ToolBar bar = new ToolBar(this, 320);
        /*  85 */
        bar.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, false));
        /*     */
        /*  87 */
        ToolItem item = new ToolItem(bar, 2);
        /*  88 */
        Label label = new Label(bar, 0);
        /*  89 */
        label.setText("Select a type: ");
        /*  90 */
        label.pack();
        /*  91 */
        item.setWidth(label.getSize().x);
        /*  92 */
        item.setControl(label);
        /*     */
        /*  94 */
        item = new ToolItem(bar, 2);
        /*  95 */
        this.comboSelectedType = new Combo(bar, 8);
        /*  96 */
        refreshTypesList();
        /*  97 */
        this.comboSelectedType.pack();
        /*  98 */
        item.setWidth(this.comboSelectedType.getSize().x);
        /*  99 */
        item.setControl(this.comboSelectedType);
        /*     */
        /* 101 */
        item = new ToolItem(bar, 2);
        /* 102 */
        this.btnPreviewDeep = new Button(bar, 32);
        /* 103 */
        this.btnPreviewDeep.setText("Deep Preview   ");
        /* 104 */
        this.btnPreviewDeep.setSelection(true);
        /* 105 */
        this.btnPreviewDeep
/* 106 */.setToolTipText(String.format("Include nested structures (up to %d) in preview", new Object[]{Integer.valueOf(10)}));
        /* 107 */
        this.btnPreviewDeep.pack();
        /* 108 */
        item.setWidth(this.btnPreviewDeep.getSize().x);
        /* 109 */
        item.setControl(this.btnPreviewDeep);
        /* 110 */
        this.btnPreviewDeep.addSelectionListener(new SelectionAdapter()
                /*     */ {
            /*     */
            public void widgetSelected(SelectionEvent e) {
                /* 113 */
                NativeTypeEditorView.this.generatePreview();
                /*     */
            }
            /*     */
            /* 116 */
        });
        /* 117 */
        item = new ToolItem(bar, 2);
        /* 118 */
        this.btnPreviewOffsets = new Button(bar, 32);
        /* 119 */
        this.btnPreviewOffsets.setText("Include Offsets   ");
        /* 120 */
        this.btnPreviewOffsets.setToolTipText("Display field offsets as comments in preview");
        /* 121 */
        this.btnPreviewOffsets.pack();
        /* 122 */
        item.setWidth(this.btnPreviewOffsets.getSize().x);
        /* 123 */
        item.setControl(this.btnPreviewOffsets);
        /* 124 */
        this.btnPreviewOffsets.addSelectionListener(new SelectionAdapter()
                /*     */ {
            /*     */
            public void widgetSelected(SelectionEvent e) {
                /* 127 */
                NativeTypeEditorView.this.generatePreview();
                /*     */
            }
            /*     */
            /* 130 */
        });
        /* 131 */
        item = new ToolItem(bar, 2);
        /* 132 */
        Button btnCreate = UIUtil.createPushbox(bar, "Create", new SelectionAdapter()
                /*     */ {
            /*     */
            public void widgetSelected(SelectionEvent e) {
                /* 135 */
                TextDialog dlg = new TextDialog(parent.getShell(), "Structure Name (global namespace)", "", null);
                /* 136 */
                String structName = dlg.open();
                /* 137 */
                if (structName != null) {
                    /* 138 */
                    IStructureType t = unit.getTypeManager().createStructure(structName);
                    /* 139 */
                    NativeTypeEditorView.this.refreshTypesList();
                    /* 140 */
                    NativeTypeEditorView.this.setInput(t);
                    /* 141 */
                    NativeTypeEditorView.this.view.setFocus();
                    /*     */
                }
                /*     */
            }
            /* 144 */
        });
        /* 145 */
        btnCreate.pack();
        /* 146 */
        item.setWidth(btnCreate.getSize().x);
        /* 147 */
        item.setControl(btnCreate);
        /*     */
        /* 149 */
        item = new ToolItem(bar, 2);
        /* 150 */
        Button btnHelp = UIUtil.createPushbox(bar, S.s(365), new SelectionAdapter()
                /*     */ {
            /*     */
            public void widgetSelected(SelectionEvent e)
            /*     */ {
                /* 154 */
                String msg = "This widget is the native structure editor. The left-hand side represents the selected data structure layout.\n\nRight click on a row, at the given offset, to perform any of the following actions:\n- Define a field\n- Undefine a field\n- Rename a field\n- Create an array field\n- Set or select the type of a field\n\nYou may scroll down to access higher offsets.\n\nThe right-hand side represents a standard C representation of the structure.";
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
                /* 169 */
                MessageDialog.openInformation(parent.getShell(), "Controls", msg);
                /*     */
            }
            /* 171 */
        });
        /* 172 */
        btnHelp.pack();
        /* 173 */
        item.setWidth(btnHelp.getSize().x);
        /* 174 */
        item.setControl(btnHelp);
        /*     */
        /* 176 */
        bar.pack();
        /*     */
        /* 178 */
        this.comboSelectedType.addSelectionListener(new SelectionAdapter()
                /*     */ {
            /*     */
            public void widgetSelected(SelectionEvent e) {
                /* 181 */
                int index = NativeTypeEditorView.this.comboSelectedType.getSelectionIndex();
                /* 182 */
                if (index >= 0) {
                    /* 183 */
                    String signature = NativeTypeEditorView.this.comboSelectedType.getItem(index);
                    /* 184 */
                    NativeTypeEditorView.this.onTypeSelectionChange(signature);
                    /*     */
                }
                /*     */
                /*     */
            }
            /* 188 */
        });
        /* 189 */
        SashForm sashContainer = new SashForm(this, 256);
        /* 190 */
        sashContainer.setLayoutData(UIUtil.createGridDataFill(true, true));
        /*     */
        /* 192 */
        this.view = new InfiniTableView(sashContainer, 4, new String[]{"Offset", "Size", "Name", "Type", "Comment"});
        /*     */
        /*     */
        /* 195 */
        this.viewer = new InfiniTableViewer(this.view);
        /*     */
        /* 197 */
        this.provider = new StructFieldsProvider();
        /* 198 */
        this.viewer.setContentProvider(this.provider);
        /* 199 */
        this.viewer.setLabelProvider(new StructFieldLabelProvider(this.provider));
        /* 200 */
        this.viewer.setTopId(0L, false);
        /* 201 */
        this.viewer.setMinimumAllowedId(0L);
        /* 202 */
        this.viewer.setInput(null);
        /*     */
        /* 204 */
        this.widgetPreview = new Text(sashContainer, 2818);
        /* 205 */
        this.widgetPreview.setEditable(false);
        /*     */
        /*     */
        /*     */
        /* 209 */
        WidgetActionWrapper actionWrapper = new WidgetActionWrapper(this.view.getTable());
        /* 210 */
        actionWrapper.registerAction(new StructEditorActionDefine(this));
        /* 211 */
        actionWrapper.registerAction(new StructEditorActionUndefine(this));
        /* 212 */
        actionWrapper.registerAction(new StructEditorActionRename(this));
        /* 213 */
        actionWrapper.registerAction(new StructEditorActionDefineArray(this));
        /* 214 */
        actionWrapper.registerAction(new StructEditorActionSetType(this));
        /* 215 */
        actionWrapper.registerAction(new StructEditorActionSelectType(this));
        /*     */
    }

    /*     */
    /*     */
    public INativeCodeUnit<?> getInputUnit() {
        /* 219 */
        return this.unit;
        /*     */
    }

    /*     */
    /*     */
    public IStructureType getInputType() {
        /* 223 */
        return this.type;
        /*     */
    }

    /*     */
    /*     */
    public void setCodefont(Font codefont) {
        /* 227 */
        this.codefont = codefont;
        /* 228 */
        if (codefont != null) {
            /* 229 */
            this.widgetPreview.setFont(codefont);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void refreshTypesList() {
        /* 234 */
        this.comboSelectedType.removeAll();
        /* 235 */
        for (INativeType t : this.unit.getTypeManager().getTypes()) {
            /* 236 */
            if ((t instanceof IStructureType)) {
                /* 237 */
                this.comboSelectedType.add(t.getSignature(true));
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private void onTypeSelectionChange(String signature) {
        /* 243 */
        INativeType type = this.unit.getTypeManager().getType(signature);
        /* 244 */
        if (!(type instanceof IStructureType)) {
            /* 245 */
            return;
            /*     */
        }
        /*     */
        /* 248 */
        setInput((IStructureType) type);
        /*     */
    }

    /*     */
    /*     */   void refresh()
    /*     */ {
        /* 253 */
        int index0 = this.view.getTable().getSelectionIndex();
        /*     */
        /* 255 */
        this.viewer.refresh();
        /* 256 */
        generatePreview();
        /*     */
        /*     */
        /* 259 */
        IStructuredSelection sel1 = (IStructuredSelection) this.viewer.getSelection();
        /* 260 */
        if (sel1.isEmpty()) {
            /* 261 */
            this.view.getTable().setSelection(index0);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void setInput(IStructureType type) {
        /* 266 */
        this.viewer.setInput(type);
        /*     */
        /*     */
        /* 269 */
        if (type != null) {
            /* 270 */
            int i = 0;
            /* 271 */
            for (String itemName : this.comboSelectedType.getItems()) {
                /* 272 */
                if (itemName.equals(type.getSignature(true))) {
                    /* 273 */
                    this.comboSelectedType.select(i);
                    /*     */
                }
                /* 275 */
                i++;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 279 */
        generatePreview();
        /*     */
    }

    /*     */
    /*     */
    public ItemEntry getSelectedEntry() {
        /* 283 */
        IStructuredSelection sel = (IStructuredSelection) this.viewer.getSelection();
        /* 284 */
        return sel.isEmpty() ? null : (ItemEntry) sel.getFirstElement();
        /*     */
    }

    /*     */
    /*     */   void generatePreview() {
        /* 288 */
        IStructureType type = (IStructureType) this.viewer.getInput();
        /* 289 */
        if (type == null) {
            /* 290 */
            this.widgetPreview.setText("<No Preview>");
            /* 291 */
            return;
            /*     */
        }
        /*     */
        /* 294 */
        boolean previewDeep = this.btnPreviewDeep.getSelection();
        /* 295 */
        boolean previewOffsets = this.btnPreviewOffsets.getSelection();
        /*     */
        /* 297 */
        PrettyTypeFormatter f = new PrettyTypeFormatter(type);
        /* 298 */
        String str = f.format(previewDeep ? 10 : 1, previewOffsets);
        /*     */
        /* 300 */
        if ((type.getPadding() != 1) || (type.getAlignment() != 0)) {
            /* 301 */
            str = String.format("// Size: %d, Padding: %d, Alignment: %d\n%s", new Object[]{Integer.valueOf(type.getSize()), Integer.valueOf(type.getPadding()),
/* 302 */         Integer.valueOf(type.getAlignment()), str});
            /*     */
        }
        /* 304 */
        this.widgetPreview.setText(str);
        /*     */
    }

    /*     */
    /*     */   class StructFieldLabelProvider extends DefaultCellLabelProvider {
        /*     */
        public StructFieldLabelProvider(NativeTypeEditorView.StructFieldsProvider provider) {
            /* 309 */
            super();
            /*     */
        }

        /*     */
        /*     */
        public void update(ViewerCell cell)
        /*     */ {
            /* 314 */
            super.update(cell);
            /* 315 */
            if ((NativeTypeEditorView.this.codefont != null) && ((cell.getColumnIndex() == 0) || (cell.getColumnIndex() == 1))) {
                /* 316 */
                cell.setFont(NativeTypeEditorView.this.codefont);
                /*     */
            }
            /*     */
            /* 319 */
            ItemEntry entry = (ItemEntry) cell.getElement();
            /* 320 */
            if (!entry.slack) {
                /* 321 */
                int rgb = entry.type != null ? 16777136 : 16777184;
                /* 322 */
                cell.setBackground(UIAssetManager.getInstance().getColor(rgb));
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */
        public String getStringAt(Object element, int key)
        /*     */ {
            /* 328 */
            ItemEntry e = (ItemEntry) element;
            /* 329 */
            switch (key) {
                /*     */
                case 0:
                    /* 331 */
                    return String.format("+%08X", new Object[]{Integer.valueOf(e.offset)});
                /*     */
                case 1:
                    /* 333 */
                    return String.format("%04X", new Object[]{Integer.valueOf(e.size)});
                /*     */
                case 3:
                    /* 335 */
                    if (e.type != null) {
                        /* 336 */
                        return e.type.getSignature(true);
                        /*     */
                    }
                    /*     */
                    break;
                /*     */
            }
            /* 340 */
            return super.getStringAt(element, key);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   class StructFieldsProvider
            /*     */ extends AbstractInfiniTableSectionProvider
            /*     */ {
        /*     */     StructFieldsProvider() {
        }

        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        /*     */ {
            /* 353 */
            NativeTypeEditorView.this.type = ((IStructureType) newInput);
            /*     */
        }

        /*     */
        /*     */
        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 360 */
            ItemEntry e = (ItemEntry) row;
            /* 361 */
            return new Object[]{Integer.valueOf(e.offset), Integer.valueOf(e.size), e.name, e.type, e.comment};
            /*     */
        }

        /*     */
        /*     */
        public Object[] get(Object inputElement, long id, int cnt)
        /*     */ {
            /* 366 */
            Assert.a(NativeTypeEditorView.this.type == (IStructureType) inputElement);
            /* 367 */
            return readFields(id, cnt);
            /*     */
        }

        /*     */
        /*     */
        private Object[] readFields(long id, int cnt) {
            /* 371 */
            Assert.a((id >= 0L) && (cnt >= 0));
            /* 372 */
            long id1 = id + cnt;
            /*     */
            /* 374 */
            List<ItemEntry> r = new ArrayList();
            /* 375 */
            List<? extends IStructureTypeField> fields = NativeTypeEditorView.this.type.getFieldsWithGaps();
            /*     */
            /* 377 */
            int i = 0;
            /* 378 */
            long currentId = 0L;
            /* 379 */
            while ((i < fields.size()) && (id < id1)) {
                /* 380 */
                IStructureTypeField elt = (IStructureTypeField) fields.get(i);
                /* 381 */
                long nextId = elt.isSynthetic() ? currentId + elt.getSize() : currentId + 1L;
                /*     */
                /* 383 */
                if ((id >= currentId) && (id < nextId)) {
                    /* 384 */
                    if (!elt.isSynthetic()) {
                        /* 385 */
                        Assert.a(id == currentId);
                        /* 386 */
                        r.add(new ItemEntry(elt.getOffset(), elt.getSize(), elt.getName(), elt.getType(), elt
/* 387 */.isBitfield() ? String.format("bitfield [%d:%d[", new Object[]{Integer.valueOf(elt.getBitstart()), Integer.valueOf(elt.getBitend())}) : null, false));
                        /*     */
                        /*     */
                        /* 390 */
                        id += 1L;
                        /*     */
                    }
                    /*     */
                    else {
                        /* 393 */
                        int off = elt.getOffset() + (int) (id - currentId);
                        /* 394 */
                        while ((id < nextId) && (id < id1)) {
                            /* 395 */
                            r.add(new ItemEntry(off, 1, null, null, null, false));
                            /* 396 */
                            off++;
                            /* 397 */
                            id += 1L;
                            /*     */
                        }
                        /*     */
                    }
                    /* 400 */
                    currentId = id;
                    /*     */
                }
                /*     */
                else {
                    /* 403 */
                    currentId = nextId;
                    /*     */
                }
                /*     */
                /* 406 */
                i++;
                /*     */
            }
            /*     */
            /* 409 */
            if (id < id1)
                /*     */ {
                /* 411 */
                long typeLastId = 0L;
                /* 412 */
                i = 0;
                /* 413 */
                while (i < fields.size()) {
                    /* 414 */
                    IStructureTypeField elt = (IStructureTypeField) fields.get(i);
                    /* 415 */
                    typeLastId += (elt.isSynthetic() ? elt.getSize() : 1L);
                    /* 416 */
                    i++;
                    /*     */
                }
                /* 418 */
                int delta = (int) (NativeTypeEditorView.this.type.getSize() - typeLastId);
                /*     */
                /*     */
                /* 421 */
                while (id < id1) {
                    /* 422 */
                    r.add(new ItemEntry((int) (id + delta), 1, null, null, null, true));
                    /* 423 */
                    id += 1L;
                    /*     */
                }
                /*     */
            }
            /*     */
            /* 427 */
            return r.toArray();
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\NativeTypeEditorView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */