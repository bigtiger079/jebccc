package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.rcpclient.extensions.controls.EditableList;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class OptionsSimpleListener
        implements Listener {
    private Map<String, List<Control>> elements = new HashMap();
    private Map<Control, List<Control>> enabledOnCheckBoxItems = new HashMap();

    public void handleEvent(Event event) {
        Object[] data = (Object[]) event.data;
        for (Map.Entry<String, List<Control>> ent : this.elements.entrySet()) {
            if (data[0].equals(ent.getKey())) {
                for (Control c : ent.getValue())
                    if (data[1] != null) {
                        if ((c instanceof Text)) {
                            OptionsSimpleViewText.refresh((Text) c, data);
                        } else if ((c instanceof Button)) {
                            Boolean selected = null;
                            if ((data[1] instanceof Boolean)) {
                                selected = Boolean.valueOf(BooleanUtils.toBoolean((Boolean) data[1]));
                            } else if (data[1] != null) {
                                selected = Boolean.valueOf(!Strings.isBlank(data[1].toString()));
                            }
                            List<Control> subEntries = (List) this.enabledOnCheckBoxItems.get(c);
                            if ((subEntries != null) && (!subEntries.isEmpty())) {
                                for (Control child : subEntries) {
                                    child.setEnabled(BooleanUtils.toBoolean(selected));
                                }
                            }
                            if (selected != null) {
                                ((Button) c).setSelection(selected.booleanValue());
                            }
                        } else if ((c instanceof Combo)) {
                            OptionsSimpleViewCombo.refresh((Combo) c, data);
                        } else if ((c instanceof EditableList)) {
                            OptionsSimpleViewList.refresh((EditableList) c, data);
                        }
                        c.getParent().update();
                    }
            }
        }
    }

    public void addEnabledOnCheckbox(Control c, List<Control> children) {
        this.enabledOnCheckBoxItems.put(c, children);
    }

    protected void addElement(String propertyKey, Control c) {
        List<Control> controls = (List) this.elements.get(propertyKey);
        if (controls == null) {
            controls = new ArrayList();
            this.elements.put(propertyKey, controls);
        }
        controls.add(c);
    }
}

