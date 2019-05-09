package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.EditableList;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.EditableList.ICheckable;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

public class OptionsSimpleViewList extends AbstractOptionsSimpleWidget {
    protected String separator;

    public OptionsSimpleViewList(OptionsChanges.Changes changes, OptionsSimpleListener listener, String propertyKey, String separator) {
        super(changes, listener, propertyKey);
        this.separator = separator;
    }

    public EditableList create(Composite parent, String label) {
        EditableList widget = build(parent, label, getValue());
        addSimpleViewElements(widget);
        return widget;
    }

    protected EditableList build(Composite parent, String label, String value) {
        EditableList list = new EditableList(parent, getTableStyle(), label, getItems(this.separator, value));
        list.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
        list.setData("SEPARATOR", this.separator);
        return list;
    }

    protected int getTableStyle() {
        return 0;
    }

    protected void addRemoveButton(final EditableList list) {
        list.addButton(S.s(677), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int[] toRemove = list.getSelectionIndices();
                if ((toRemove == null) || (toRemove.length == 0)) {
                    return;
                }
                Object previousValue = OptionsSimpleViewList.this.getProperty();
                if (previousValue == null) {
                    return;
                }
                String[] items = OptionsSimpleViewList.getItems(OptionsSimpleViewList.this.separator, previousValue.toString());
                String[] newItems = OptionsSimpleViewList.this.removeElements(items, toRemove);
                OptionsSimpleViewList.this.changes.addChange(OptionsSimpleViewList.this.propertyKey, StringUtils.join(newItems, OptionsSimpleViewList.this.separator.replace("\\", "")));
            }
        }, true);
    }

    protected String[] getItems(String value) {
        return getItems(this.separator, value);
    }

    protected static String[] getItems(EditableList table, String value) {
        return getItems((String) table.getData("SEPARATOR"), value);
    }

    protected static String[] getItems(String separator, String value) {
        if ((value == null) || (value.isEmpty())) {
            return new String[0];
        }
        return value.split(separator);
    }

    protected String[] removeElements(String[] items, int[] toRemoveArray) {
        if ((toRemoveArray == null) || (toRemoveArray.length == 0)) {
            return items;
        }
        List<String> result = new ArrayList<>();
        List<Integer> toRemove = asList(toRemoveArray);
        for (int sourceIndex = 0; sourceIndex < items.length; sourceIndex++) {
            if (!toRemove.contains(Integer.valueOf(sourceIndex))) {
                result.add(items[sourceIndex]);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    private List<Integer> asList(int[] array) {
        List<Integer> result = new ArrayList<>();
        for (int v : array) {
            result.add(Integer.valueOf(v));
        }
        return result;
    }

    public static void refresh(EditableList table, Object[] data) {
        Object checkable = table.getData("CHECKABLE");
        if (checkable != null) {
            table.resetItems(((ICheckableProvider) checkable).getCheckableList(table, (String) data[1]));
        } else {
            table.resetItems(getItems(table, (String) data[1]));
        }
    }

    public static abstract interface ICheckableProvider {
        public abstract List<EditableList.ICheckable> getCheckableList(EditableList paramEditableList, String paramString);
    }
}


