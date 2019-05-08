
package com.pnfsoftware.jeb.rcpclient.parts.units.code;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.ICommentManager;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IMemoryModel;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.WidgetActionWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.InfiniTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.AbstractInfiniTableSectionProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.InfiniTableViewer;
import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.collect.ISegmentFactory;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class StackEditorView
        extends Composite {
    private static final ILogger logger = GlobalLog.getLogger(StackEditorView.class);
    private Font codefont;
    private INativeCodeUnit<?> unit;
    private INativeMethodItem routine;
    private InfiniTableView view;
    private InfiniTableViewer viewer;
    private StackItemsProvider provider;

    public StackEditorView(final Composite parent, int style, INativeCodeUnit<?> unit) {
        super(parent, style);
        setLayout(new GridLayout(1, false));
        if (unit == null) {
            throw new NullPointerException();
        }
        this.unit = unit;
        ToolBar bar = new ToolBar(this, 320);
        bar.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, false));
        ToolItem item = new ToolItem(bar, 2);
        Button btnHelp = UIUtil.createPushbox(bar, S.s(365), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String msg = "This widget is the native routine stack editor. The table represents a stack memory layout.\n\nRight click on a row, at the given offset, to perform any of the following actions:\n- Define a field\n- Undefine a field\n- Rename a field\n- Create an array field\n- Set or select the type of a field\n\nYou may scroll up/down to access lower/higher offsets.\n";
                MessageDialog.openInformation(parent.getShell(), "Controls", msg);
            }
        });
        btnHelp.pack();
        item.setWidth(btnHelp.getSize().x);
        item.setControl(btnHelp);
        bar.pack();
        this.view = new InfiniTableView(this, 4, new String[]{"Offset", "Size", "Name", "Type", "Comment"});
        this.view.setLayoutData(UIUtil.createGridDataFill(true, true));
        this.viewer = new InfiniTableViewer(this.view);
        this.provider = new StackItemsProvider();
        this.viewer.setContentProvider(this.provider);
        this.viewer.setLabelProvider(new StackItemLabelProvider(this.provider));
        this.viewer.setTopId(0L, false);
        this.viewer.setInput(null);
        WidgetActionWrapper actionWrapper = new WidgetActionWrapper(this.view.getTable());
        actionWrapper.registerAction(new StackEditorActionDefine(this));
        actionWrapper.registerAction(new StackEditorActionUndefine(this));
        actionWrapper.registerAction(new StackEditorActionRename(this));
        actionWrapper.registerAction(new StackEditorActionDefineArray(this));
        actionWrapper.registerAction(new StackEditorActionSetType(this));
        actionWrapper.registerAction(new StackEditorActionSelectType(this));
    }

    public INativeCodeUnit<?> getInputUnit() {
        return this.unit;
    }

    public INativeMethodItem getInputRoutine() {
        return this.routine;
    }

    public void setInputRoutine(INativeMethodItem routine) {
        this.routine = routine;
        this.viewer.setInput(routine);
    }

    public void setCodefont(Font codefont) {
        this.codefont = codefont;
    }

    void refresh() {
        int index0 = this.view.getTable().getSelectionIndex();
        this.viewer.refresh();
        IStructuredSelection sel1 = (IStructuredSelection) this.viewer.getSelection();
        if (sel1.isEmpty()) {
            this.view.getTable().setSelection(index0);
        }
    }

    public ItemEntry getSelectedEntry() {
        IStructuredSelection sel = (IStructuredSelection) this.viewer.getSelection();
        return sel.isEmpty() ? null : (ItemEntry) sel.getFirstElement();
    }

    class StackItemLabelProvider extends DefaultCellLabelProvider {
        public StackItemLabelProvider(StackEditorView.StackItemsProvider provider) {
            super(provider);
        }

        public void update(ViewerCell cell) {
            super.update(cell);
            if ((StackEditorView.this.codefont != null) && ((cell.getColumnIndex() == 0) || (cell.getColumnIndex() == 1))) {
                cell.setFont(StackEditorView.this.codefont);
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
                    if (e.offset >= 0) {
                        return String.format("+%08X", new Object[]{Integer.valueOf(e.offset)});
                    }
                    return String.format("-%08X", new Object[]{Integer.valueOf(-e.offset)});
                case 1:
                    return String.format("%04X", new Object[]{Integer.valueOf(e.size)});
                case 3:
                    if (e.type != null) {
                        return e.type.getSignature(true);
                    }
                    break;
            }
            return super.getStringAt(element, key);
        }
    }

    class StackItemsProvider
            extends AbstractInfiniTableSectionProvider {
        StackItemsProvider() {
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            StackEditorView.this.routine = ((INativeMethodItem) newInput);
        }

        public Object[] getRowElements(Object row) {
            ItemEntry e = (ItemEntry) row;
            return new Object[]{Integer.valueOf(e.offset), Integer.valueOf(e.size), e.name, e.type, e.comment};
        }

        public Object[] get(Object inputElement, long id, int cnt) {
            Assert.a(StackEditorView.this.routine == (INativeMethodItem) inputElement);
            return readSlots(StackEditorView.this.unit, StackEditorView.this.routine, id, cnt);
        }

        private INativeContinuousItem createGapItem(IMemoryModel model, long address, int size) {
            INativeContinuousItem item = (INativeContinuousItem) model.getGapFactory().create(Long.valueOf(address), Long.valueOf(address + size));
            item.setName("undefined");
            return item;
        }

        private Object[] readSlots(INativeCodeUnit<?> unit, INativeMethodItem routine, long id, int cnt) {
            INativeMethodDataItem routineData = routine.getData();
            IMemoryModel stk = routineData.getStackframeModel();
            int indexZero = -1;
            List<INativeContinuousItem> list = new ArrayList();
            Set<Long> slackset = new HashSet();
            long a;
            if (!stk.isEmpty()) {
                Long lastAddress = null;
                SortedMap<Long, INativeContinuousItem> view_ = stk.getView(null, null);
                TreeMap<Long, INativeContinuousItem> view = new TreeMap(view_);
                for (Long address : view.descendingKeySet()) {
                    INativeContinuousItem item = (INativeContinuousItem) view.get(address);
                    if ((lastAddress != null) && (lastAddress != item.getEnd())) {
                        for (a = lastAddress.longValue() - 1L; a >= ((Long) item.getEnd()).longValue(); a -= 1L) {
                            list.add(createGapItem(stk, a, 1));
                        }
                    }
                    list.add(item);
                    lastAddress = (Long) item.getBegin();
                }
                INativeContinuousItem slot = (INativeContinuousItem) list.get(0);
                long first = Math.max(0L, ((Long) slot.getEnd()).longValue() + cnt * 1);
                List<INativeContinuousItem> tmplist = new ArrayList();
                for (a = first - 1L; a >= ((Long) slot.getEnd()).longValue(); a -= 1L) {
                    tmplist.add(createGapItem(stk, a, 1));
                    slackset.add(Long.valueOf(a));
                }
                list.addAll(0, tmplist);
                slot = (INativeContinuousItem) list.get(list.size() - 1);
                long last = Math.min(0L, ((Long) slot.getBegin()).longValue() - cnt * 1);
                for (a = ((Long) slot.getBegin()).longValue() - 1L; a >= last; a -= 1L) {
                    list.add(createGapItem(stk, a, 1));
                    slackset.add(Long.valueOf(a));
                }
                indexZero = -1;
                for (INativeContinuousItem item : list) {
                    if ((((Long) item.getBegin()).longValue() <= 0L) && (((Long) item.getEnd()).longValue() > 0L)) {
                        break;
                    }
                    indexZero++;
                }
            }
            if ((indexZero < 0) || (id + indexZero < 0L) || (id + indexZero + cnt > list.size())) {
                list.clear();
                for (a = id; a < id + cnt; a += 1L) {
                    INativeContinuousItem item = createGapItem(stk, -a, 1);
                    list.add(item);
                    slackset.add(Long.valueOf(-a));
                }
                indexZero = (int) -id;
            }
            List<ItemEntry> r = new ArrayList();
            int i0 = (int) (id + indexZero);
            for (int i = i0; i < i0 + cnt; i++) {
                INativeContinuousItem item = (INativeContinuousItem) list.get(i);
                ItemEntry e = new ItemEntry();
                e.offset = ((int) item.getMemoryAddress());
                e.name = item.getName(true);
                e.size = ((int) item.getMemorySize());
                e.type = ((item instanceof INativeDataItem) ? ((INativeDataItem) item).getType() : null);
                e.comment = stk.getCommentManager().getComment(e.offset);
                e.slack = slackset.contains(Long.valueOf(e.offset));
                r.add(e);
            }
            return r.toArray();
        }
    }
}


