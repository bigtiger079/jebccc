package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.EditableList;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class OptionsSimpleViewDevelopment extends Composite {
    protected static final String CLIENT = "Client";
    protected static final String ENGINES = "Engines";
    protected static final String PROJECT_SPECIFIC = "Project-specific";
    private static final String PROPERTY_CLASSPATH = "DevPluginClasspath";
    private static final String PROPERTY_CLASSNAMES = "DevPluginClassnames";
    private Map<String, OptionsSimpleListener> listeners = new HashMap();
    private final OptionsChanges optionsChanges;

    public OptionsSimpleViewDevelopment(Composite parent, OptionsChanges optionsChanges) {
        super(parent, 0);
        setLayout(new FillLayout());
        Composite ph = new Composite(this, 0);
        ph.setLayout(new GridLayout(1, false));
        this.optionsChanges = optionsChanges;
        initSimpleViewElementListener("Client");
        initSimpleViewElementListener("Engines");
        Group dev = createGroup(ph, S.s(271));
        createBooleanOption(dev, S.s(272), S.s(816), "Client", "DevelopmentMode");
        EditableList cp = createClasspathOption(dev, S.s(646), "Engines", Licensing.canUseCoreAPI() ? "DevPluginClasspath" : null, "\\" + File.pathSeparator);
        EditableList cn = createClassnameOption(dev, S.s(645), "Engines", Licensing.canUseCoreAPI() ? "DevPluginClassnames" : null, ",", Licensing.canUseCoreAPI() ? "DevPluginClasspath" : null, "\\" + File.pathSeparator);
        if (!Licensing.canUseCoreAPI()) {
            cp.setEnabled(false);
            cn.setEnabled(false);
        }
    }

    private void initSimpleViewElementListener(String propertyManagerKey) {
        OptionsSimpleListener listener = new OptionsSimpleListener();
        this.listeners.put(propertyManagerKey, listener);
        OptionsChanges.Changes c = this.optionsChanges.get(propertyManagerKey);
        if (c != null) {
            c.listeners.add(listener);
        }
    }

    private Group createGroup(Composite parent, String label) {
        Group g = UIUtil.createGroup(parent, label);
        g.setLayoutData(UIUtil.createGridDataFillHorizontally());
        return g;
    }

    private EditableList createClasspathOption(Composite parent, String label, String propertyManagerKey, String propertyKey, String separator) {
        return new OptionsSimpleViewClasspath(this.optionsChanges.get(propertyManagerKey), (OptionsSimpleListener) this.listeners.get(propertyManagerKey), propertyKey, separator).create(parent, label);
    }

    private EditableList createClassnameOption(Composite parent, String label, String propertyManagerKey, String propertyKey, String separator, String classPathProperty, String classPathSeparator) {
        return new OptionsSimpleViewClassname(this.optionsChanges.get(propertyManagerKey), (OptionsSimpleListener) this.listeners.get(propertyManagerKey), propertyKey, separator, classPathProperty, classPathSeparator).create(parent, label);
    }

    private Button createBooleanOption(Composite parent, String label, String toolTip, String propertyManagerKey, String propertyKey) {
        return new OptionsBooleanViewer(this.optionsChanges.get(propertyManagerKey), (OptionsSimpleListener) this.listeners.get(propertyManagerKey), propertyKey).create(parent, label, toolTip);
    }
}


