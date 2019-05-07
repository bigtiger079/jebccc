/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.code;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.ICommentManager;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IMemoryModel;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.WidgetActionWrapper;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.InfiniTableView;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractInfiniTableSectionProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.InfiniTableViewer;
/*     */ import com.pnfsoftware.jeb.util.base.Assert;
/*     */ import com.pnfsoftware.jeb.util.collect.ISegmentFactory;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import org.eclipse.jface.dialogs.MessageDialog;
/*     */ import org.eclipse.jface.viewers.IStructuredSelection;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.jface.viewers.ViewerCell;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Table;
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
/*     */ public class StackEditorView
        /*     */ extends Composite
        /*     */ {
    /*  56 */   private static final ILogger logger = GlobalLog.getLogger(StackEditorView.class);
    /*     */   private Font codefont;
    /*     */   private INativeCodeUnit<?> unit;
    /*     */   private INativeMethodItem routine;
    /*     */   private InfiniTableView view;
    /*     */   private InfiniTableViewer viewer;
    /*     */   private StackItemsProvider provider;

    /*     */
    /*     */
    public StackEditorView(final Composite parent, int style, INativeCodeUnit<?> unit)
    /*     */ {
        /*  66 */
        super(parent, style);
        /*  67 */
        setLayout(new GridLayout(1, false));
        /*     */
        /*  69 */
        if (unit == null) {
            /*  70 */
            throw new NullPointerException();
            /*     */
        }
        /*  72 */
        this.unit = unit;
        /*     */
        /*  74 */
        ToolBar bar = new ToolBar(this, 320);
        /*  75 */
        bar.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, false));
        /*     */
        /*  77 */
        ToolItem item = new ToolItem(bar, 2);
        /*  78 */
        Button btnHelp = UIUtil.createPushbox(bar, S.s(365), new SelectionAdapter()
                /*     */ {
            /*     */
            public void widgetSelected(SelectionEvent e) {
                /*  81 */
                String msg = "This widget is the native routine stack editor. The table represents a stack memory layout.\n\nRight click on a row, at the given offset, to perform any of the following actions:\n- Define a field\n- Undefine a field\n- Rename a field\n- Create an array field\n- Set or select the type of a field\n\nYou may scroll up/down to access lower/higher offsets.\n";
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
                /*  93 */
                MessageDialog.openInformation(parent.getShell(), "Controls", msg);
                /*     */
            }
            /*  95 */
        });
        /*  96 */
        btnHelp.pack();
        /*  97 */
        item.setWidth(btnHelp.getSize().x);
        /*  98 */
        item.setControl(btnHelp);
        /*     */
        /* 100 */
        bar.pack();
        /*     */
        /* 102 */
        this.view = new InfiniTableView(this, 4, new String[]{"Offset", "Size", "Name", "Type", "Comment"});
        /* 103 */
        this.view.setLayoutData(UIUtil.createGridDataFill(true, true));
        /* 104 */
        this.viewer = new InfiniTableViewer(this.view);
        /*     */
        /* 106 */
        this.provider = new StackItemsProvider();
        /* 107 */
        this.viewer.setContentProvider(this.provider);
        /* 108 */
        this.viewer.setLabelProvider(new StackItemLabelProvider(this.provider));
        /* 109 */
        this.viewer.setTopId(0L, false);
        /* 110 */
        this.viewer.setInput(null);
        /*     */
        /*     */
        /* 113 */
        WidgetActionWrapper actionWrapper = new WidgetActionWrapper(this.view.getTable());
        /* 114 */
        actionWrapper.registerAction(new StackEditorActionDefine(this));
        /* 115 */
        actionWrapper.registerAction(new StackEditorActionUndefine(this));
        /* 116 */
        actionWrapper.registerAction(new StackEditorActionRename(this));
        /* 117 */
        actionWrapper.registerAction(new StackEditorActionDefineArray(this));
        /* 118 */
        actionWrapper.registerAction(new StackEditorActionSetType(this));
        /* 119 */
        actionWrapper.registerAction(new StackEditorActionSelectType(this));
        /*     */
    }

    /*     */
    /*     */
    public INativeCodeUnit<?> getInputUnit() {
        /* 123 */
        return this.unit;
        /*     */
    }

    /*     */
    /*     */
    public INativeMethodItem getInputRoutine() {
        /* 127 */
        return this.routine;
        /*     */
    }

    /*     */
    /*     */
    public void setInputRoutine(INativeMethodItem routine) {
        /* 131 */
        this.routine = routine;
        /* 132 */
        this.viewer.setInput(routine);
        /*     */
    }

    /*     */
    /*     */
    public void setCodefont(Font codefont) {
        /* 136 */
        this.codefont = codefont;
        /*     */
    }

    /*     */
    /*     */   void refresh() {
        /* 140 */
        int index0 = this.view.getTable().getSelectionIndex();
        /*     */
        /* 142 */
        this.viewer.refresh();
        /*     */
        /*     */
        /* 145 */
        IStructuredSelection sel1 = (IStructuredSelection) this.viewer.getSelection();
        /* 146 */
        if (sel1.isEmpty()) {
            /* 147 */
            this.view.getTable().setSelection(index0);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public ItemEntry getSelectedEntry() {
        /* 152 */
        IStructuredSelection sel = (IStructuredSelection) this.viewer.getSelection();
        /* 153 */
        return sel.isEmpty() ? null : (ItemEntry) sel.getFirstElement();
        /*     */
    }

    /*     */
    /*     */   class StackItemLabelProvider extends DefaultCellLabelProvider {
        /*     */
        public StackItemLabelProvider(StackEditorView.StackItemsProvider provider) {
            /* 158 */
            super();
            /*     */
        }

        /*     */
        /*     */
        public void update(ViewerCell cell)
        /*     */ {
            /* 163 */
            super.update(cell);
            /* 164 */
            if ((StackEditorView.this.codefont != null) && ((cell.getColumnIndex() == 0) || (cell.getColumnIndex() == 1))) {
                /* 165 */
                cell.setFont(StackEditorView.this.codefont);
                /*     */
            }
            /*     */
            /* 168 */
            ItemEntry entry = (ItemEntry) cell.getElement();
            /* 169 */
            if (!entry.slack) {
                /* 170 */
                int rgb = entry.type != null ? 16777136 : 16777184;
                /* 171 */
                cell.setBackground(UIAssetManager.getInstance().getColor(rgb));
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */
        public String getStringAt(Object element, int key)
        /*     */ {
            /* 177 */
            ItemEntry e = (ItemEntry) element;
            /* 178 */
            switch (key) {
                /*     */
                case 0:
                    /* 180 */
                    if (e.offset >= 0) {
                        /* 181 */
                        return String.format("+%08X", new Object[]{Integer.valueOf(e.offset)});
                        /*     */
                    }
                    /*     */
                    /* 184 */
                    return String.format("-%08X", new Object[]{Integer.valueOf(-e.offset)});
                /*     */
                /*     */
                case 1:
                    /* 187 */
                    return String.format("%04X", new Object[]{Integer.valueOf(e.size)});
                /*     */
                case 3:
                    /* 189 */
                    if (e.type != null) {
                        /* 190 */
                        return e.type.getSignature(true);
                        /*     */
                    }
                    /*     */
                    break;
                /*     */
            }
            /* 194 */
            return super.getStringAt(element, key);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   class StackItemsProvider
            /*     */ extends AbstractInfiniTableSectionProvider
            /*     */ {
        /*     */     StackItemsProvider() {
        }

        /*     */
        /*     */
        public void dispose() {
        }

        /*     */
        /*     */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        /*     */ {
            /* 207 */
            StackEditorView.this.routine = ((INativeMethodItem) newInput);
            /*     */
        }

        /*     */
        /*     */
        public Object[] getRowElements(Object row)
        /*     */ {
            /* 212 */
            ItemEntry e = (ItemEntry) row;
            /* 213 */
            return new Object[]{Integer.valueOf(e.offset), Integer.valueOf(e.size), e.name, e.type, e.comment};
            /*     */
        }

        /*     */
        /*     */
        public Object[] get(Object inputElement, long id, int cnt)
        /*     */ {
            /* 218 */
            Assert.a(StackEditorView.this.routine == (INativeMethodItem) inputElement);
            /* 219 */
            return readSlots(StackEditorView.this.unit, StackEditorView.this.routine, id, cnt);
            /*     */
        }

        /*     */
        /*     */
        private INativeContinuousItem createGapItem(IMemoryModel model, long address, int size) {
            /* 223 */
            INativeContinuousItem item = (INativeContinuousItem) model.getGapFactory().create(Long.valueOf(address), Long.valueOf(address + size));
            /* 224 */
            item.setName("undefined");
            /* 225 */
            return item;
            /*     */
        }

        /*     */
        /*     */
        private Object[] readSlots(INativeCodeUnit<?> unit, INativeMethodItem routine, long id, int cnt) {
            /* 229 */
            INativeMethodDataItem routineData = routine.getData();
            /* 230 */
            IMemoryModel stk = routineData.getStackframeModel();
            /*     */
            /* 232 */
            int indexZero = -1;
            /* 233 */
            List<INativeContinuousItem> list = new ArrayList();
            /* 234 */
            Set<Long> slackset = new HashSet();
            /*     */
            long a;
            /* 236 */
            if (!stk.isEmpty()) {
                /* 237 */
                Long lastAddress = null;
                /* 238 */
                SortedMap<Long, INativeContinuousItem> view_ = stk.getView(null, null);
                /* 239 */
                TreeMap<Long, INativeContinuousItem> view = new TreeMap(view_);
                /* 240 */
                for (Long address : view.descendingKeySet()) {
                    /* 241 */
                    INativeContinuousItem item = (INativeContinuousItem) view.get(address);
                    /*     */
                    /* 243 */
                    if ((lastAddress != null) && (lastAddress != item.getEnd())) {
                        /* 244 */
                        for (long a = lastAddress.longValue() - 1L; a >= ((Long) item.getEnd()).longValue(); a -= 1L) {
                            /* 245 */
                            list.add(createGapItem(stk, a, 1));
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                    /* 249 */
                    list.add(item);
                    /* 250 */
                    lastAddress = (Long) item.getBegin();
                    /*     */
                }
                /*     */
                /*     */
                /* 254 */
                INativeContinuousItem slot = (INativeContinuousItem) list.get(0);
                /* 255 */
                long first = Math.max(0L, ((Long) slot.getEnd()).longValue() + cnt * 1);
                /* 256 */
                List<INativeContinuousItem> tmplist = new ArrayList();
                /* 257 */
                for (long a = first - 1L; a >= ((Long) slot.getEnd()).longValue(); a -= 1L) {
                    /* 258 */
                    tmplist.add(createGapItem(stk, a, 1));
                    /* 259 */
                    slackset.add(Long.valueOf(a));
                    /*     */
                }
                /* 261 */
                list.addAll(0, tmplist);
                /*     */
                /*     */
                /* 264 */
                slot = (INativeContinuousItem) list.get(list.size() - 1);
                /* 265 */
                long last = Math.min(0L, ((Long) slot.getBegin()).longValue() - cnt * 1);
                /* 266 */
                for (a = ((Long) slot.getBegin()).longValue() - 1L; a >= last; a -= 1L) {
                    /* 267 */
                    list.add(createGapItem(stk, a, 1));
                    /* 268 */
                    slackset.add(Long.valueOf(a));
                    /*     */
                }
                /*     */
                /*     */
                /* 272 */
                indexZero = -1;
                /* 273 */
                for (INativeContinuousItem item : list) {
                    /* 274 */
                    if ((((Long) item.getBegin()).longValue() <= 0L) && (((Long) item.getEnd()).longValue() > 0L)) {
                        /*     */
                        break;
                        /*     */
                    }
                    /* 277 */
                    indexZero++;
                    /*     */
                }
                /*     */
            }
            /*     */
            /*     */
            /* 282 */
            if ((indexZero < 0) || (id + indexZero < 0L) || (id + indexZero + cnt > list.size())) {
                /* 283 */
                list.clear();
                /* 284 */
                for (long a = id; a < id + cnt; a += 1L) {
                    /* 285 */
                    INativeContinuousItem item = createGapItem(stk, -a, 1);
                    /* 286 */
                    list.add(item);
                    /* 287 */
                    slackset.add(Long.valueOf(-a));
                    /*     */
                }
                /* 289 */
                indexZero = (int) -id;
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /*     */
            /* 295 */
            List<ItemEntry> r = new ArrayList();
            /* 296 */
            int i0 = (int) (id + indexZero);
            /* 297 */
            for (int i = i0; i < i0 + cnt; i++) {
                /* 298 */
                INativeContinuousItem item = (INativeContinuousItem) list.get(i);
                /* 299 */
                ItemEntry e = new ItemEntry();
                /* 300 */
                e.offset = ((int) item.getMemoryAddress());
                /* 301 */
                e.name = item.getName(true);
                /* 302 */
                e.size = ((int) item.getMemorySize());
                /* 303 */
                e.type = ((item instanceof INativeDataItem) ? ((INativeDataItem) item).getType() : null);
                /* 304 */
                e.comment = stk.getCommentManager().getComment(e.offset);
                /* 305 */
                e.slack = slackset.contains(Long.valueOf(e.offset));
                /* 306 */
                r.add(e);
                /*     */
            }
            /* 308 */
            return r.toArray();
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\StackEditorView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */