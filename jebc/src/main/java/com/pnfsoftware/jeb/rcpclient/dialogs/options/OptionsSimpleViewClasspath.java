package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DirectorySelectionListener;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.EditableList;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FileSelectionListener;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class OptionsSimpleViewClasspath extends OptionsSimpleViewList {
    public OptionsSimpleViewClasspath(OptionsChanges.Changes changes, OptionsSimpleListener listener, String propertyKey, String separator) {
        super(changes, listener, propertyKey, separator);
    }

    protected EditableList build(Composite parent, String label, String value) {
        final EditableList list = super.build(parent, label, value);
        list.addButton(S.s(50) + "...", new DirectorySelectionListener(parent.getShell(), null) {
            public String getDefaultText() {
                return "";
            }

            public void setText(String dirname) {
                OptionsSimpleViewClasspath.this.addToClasspath(dirname);
            }
        }, false);
        list.addButton(S.s(51) + "...", new FileSelectionListener(parent.getShell(), null, new String[]{"*.jar"}) {
            public String getDefaultText() {
                return "";
            }

            public void setText(String file) {
                OptionsSimpleViewClasspath.this.addToClasspath(file);
            }
        }, false);
        list.addButton(S.s(284) + "...", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                OptionsSimpleViewClasspath.this.onEdit(list);
            }
        }, true);
        addRemoveButton(list);
        list.getTable().addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                OptionsSimpleViewClasspath.this.onEdit(list);
            }
        });
        return list;
    }

    private void onEdit(EditableList list) {
        TableItem[] indices = list.getSelection();
        if ((indices == null) || indices.length != 1) {
            return;
        }
        String intialValue = indices[0].getText();
        InputDialog id = new InputDialog(list.getShell(), "Classpath", "Edit the classpath:", intialValue, null);
        int result = id.open();
        if (result == 0) {
            updateClasspath(list.getSelectionIndices()[0], id.getValue());
        }
    }

    private void addToClasspath(String dirname) {
        StringBuilder newProperty = new StringBuilder();
        Object previousValue = getProperty();
        if ((previousValue != null) && (!((String) previousValue).isEmpty())) {
            newProperty.append(previousValue.toString());
            newProperty.append(this.separator.replace("\\", ""));
        }
        newProperty.append(dirname);
        String newPropertyStr = newProperty.toString();
        this.changes.addChange(this.propertyKey, newPropertyStr);
    }

    private void updateClasspath(int position, String value) {
        String[] values = getItems((String) getProperty());
        values[position] = value;
        this.changes.addChange(this.propertyKey, StringUtils.join(values, this.separator.replace("\\", "")));
    }
}


