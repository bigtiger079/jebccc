package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.core.properties.impl.CoreProperties;
import com.pnfsoftware.jeb.core.properties.impl.DevPluginClassname;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.EditableList;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.EditableList.ICheckable;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


public class OptionsSimpleViewClassname
        extends OptionsSimpleViewList {
    private static final ILogger logger = GlobalLog.getLogger(OptionsSimpleViewClassname.class);

    private static final String MESSAGE_TITLE = "Classname";

    private static final String MESSAGE_VERIFY_TITLE = "Verify classname";

    private String classPathProperty;
    private String classPathSeparator;

    public OptionsSimpleViewClassname(OptionsChanges.Changes changes, OptionsSimpleListener listener, String propertyKey, String separator, String classPathProperty, String classPathSeparator) {
        super(changes, listener, propertyKey, separator);
        this.classPathProperty = classPathProperty;
        this.classPathSeparator = classPathSeparator;
    }

    protected EditableList build(Composite parent, String label, String value) {
        final EditableList list = super.build(parent, label, value);
        boolean readOnly = this.changes == null;
        final CoreProperties cp = new CoreProperties(readOnly ? null : this.changes.getPropertyManager());
        if (!readOnly) {
            CheckableClassnameProvider provider = new CheckableClassnameProvider(cp);
            list.setData("CHECKABLE", provider);
            list.resetItems(provider.getCheckableList(list, value));
            list.getTable().addListener(13, new Listener() {
                public void handleEvent(Event event) {
                    boolean checked = event.detail == 32;
                    if (!checked) {
                        return;
                    }

                    Table table = (Table) event.widget;
                    int selected;
                    for (selected = 0; selected < table.getItemCount(); selected++) {
                        if (table.getItem(selected) == event.item) {
                            break;
                        }
                    }

                    List<DevPluginClassname> classnames = cp.parseDevPluginClassnames((String) OptionsSimpleViewClassname.this.getProperty());

                    if ((selected < 0) || (selected >= table.getItemCount())) {
                        OptionsSimpleViewClassname.logger.error("The change was not recorded!", new Object[0]);
                        return;
                    }

                    DevPluginClassname classname = (DevPluginClassname) classnames.get(selected);

                    DevPluginClassname newClassname = new DevPluginClassname(classname.getClassname(), !classname.isEnabled());
                    classnames.remove(selected);
                    classnames.add(selected, newClassname);
                    OptionsSimpleViewClassname.this.changes.addChange(OptionsSimpleViewClassname.this.propertyKey, cp.buildDevPluginClassnames(classnames));
                }
            });
        }
        list.addButton("Add...", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                InputDialog id = new InputDialog(list.getShell(), "Classname", "Enter the classname:", "", null);
                int result = id.open();
                if ((result == 0) && (!Strings.isBlank(id.getValue()))) {
                    OptionsSimpleViewClassname.this.addClassname(cp, id.getValue());
                    OptionsSimpleViewClassname.this.displayVerifyClassname(id.getValue(), list.getShell());
                }
            }
        }, false);


        list.addButton("Edit...", new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                OptionsSimpleViewClassname.this.onEdit(list, cp);
            }
        }, true);


        addRemoveButton(list);
        list.addButton("Verify", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent se) {
                TableItem[] indices = list.getSelection();
                if ((indices == null) || (indices.length == 0) || (indices.length > 1)) {
                    return;
                }
                String cname = indices[0].getText();
                OptionsSimpleViewClassname.this.displayVerifyClassname(cname, list.getShell());
            }
        }, true);


        list.getTable().addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent e) {
                OptionsSimpleViewClassname.this.onEdit(list, cp);
            }

        });
        return list;
    }

    private void onEdit(EditableList list, CoreProperties cp) {
        TableItem[] indices = list.getSelection();
        if ((indices == null) || (indices.length == 0) || (indices.length > 1)) {
            return;
        }
        String intialValue = indices[0].getText();
        InputDialog id = new InputDialog(list.getShell(), "Classname", "Edit the classname:", intialValue, null);
        int result = id.open();
        if ((result == 0) && (!Strings.isBlank(id.getValue()))) {
            updateClassname(cp, list.getSelectionIndices()[0], id.getValue());
            displayVerifyClassname(id.getValue(), list.getShell());
        }
    }

    private void addClassname(CoreProperties cp, String classname) {
        List<DevPluginClassname> classnames = cp.parseDevPluginClassnames((String) getProperty());
        DevPluginClassname classnameObj = new DevPluginClassname(classname, true);
        classnames.add(classnameObj);
        this.changes.addChange(this.propertyKey, cp.buildDevPluginClassnames(classnames));
    }

    private void updateClassname(CoreProperties cp, int position, String value) {
        List<DevPluginClassname> classnames = cp.parseDevPluginClassnames((String) getProperty());
        DevPluginClassname old = (DevPluginClassname) classnames.remove(position);
        classnames.add(position, new DevPluginClassname(value, old.isEnabled()));
        this.changes.addChange(this.propertyKey, cp.buildDevPluginClassnames(classnames));
    }

    private void displayVerifyClassname(String classname, Shell shell) {
        if (verifyClassname(classname)) {
            MessageDialog.openInformation(shell, "Verify classname", "Classname was found in specified classpath");
        } else {
            MessageDialog.openError(shell, "Verify classname", "Classname can not be found in specified classpath");
        }
    }

    private boolean verifyClassname(String classname) {
        String[] values = getItems(this.classPathSeparator, (String) getProperty(this.changes, this.classPathProperty, false));
        try {
            buildClassLoader(values).loadClass(classname);
            return true;
        } catch (ClassNotFoundException e) {
        }
        return false;
    }

    private URLClassLoader buildClassLoader(String[] paths) {
        List<URL> urls = new ArrayList();
        for (String pathelt : paths) {
            try {
                urls.add(new File(pathelt.trim()).toURI().toURL());
            } catch (MalformedURLException e) {
            }
        }


        return new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
    }

    protected int getTableStyle() {
        return 32;
    }

    private static class CheckableClassnameProvider implements OptionsSimpleViewList.ICheckableProvider {
        CoreProperties cp;

        public CheckableClassnameProvider(CoreProperties cp) {
            this.cp = cp;
        }

        public List<EditableList.ICheckable> getCheckableList(EditableList table, String value) {
            List<EditableList.ICheckable> result = new ArrayList();
            List<DevPluginClassname> classnames = this.cp.parseDevPluginClassnames(value);
            for (DevPluginClassname cl : classnames) {
                result.add(new OptionsSimpleViewClassname.CheckableDevPluginClassname(cl));
            }
            return result;
        }
    }

    private static class CheckableDevPluginClassname implements EditableList.ICheckable {
        DevPluginClassname classname;

        public CheckableDevPluginClassname(DevPluginClassname classname) {
            this.classname = classname;
        }

        public String getText() {
            return this.classname.getClassname();
        }

        public boolean isChecked() {
            return this.classname.isEnabled();
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\options\OptionsSimpleViewClassname.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */