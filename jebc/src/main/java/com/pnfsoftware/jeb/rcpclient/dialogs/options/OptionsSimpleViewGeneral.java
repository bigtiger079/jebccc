package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DirectorySelectorView;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class OptionsSimpleViewGeneral extends Composite {
    protected static final String CLIENT = "Client";
    protected static final String ENGINES = "Engines";
    protected static final String PROJECT_SPECIFIC = "Project-specific";
    private Map<String, OptionsSimpleListener> listeners = new HashMap<>();
    private final OptionsChanges optionsChanges;
    private List<Control> proxySubEntries = new ArrayList<>();

    public OptionsSimpleViewGeneral(Composite parent, OptionsChanges optionsChanges) {
        super(parent, 0);
        setLayout(new FillLayout());
        Composite ph = new Composite(this, 0);
        ph.setLayout(new GridLayout(1, false));
        this.optionsChanges = optionsChanges;
        initSimpleViewElementListener(CLIENT);
        initSimpleViewElementListener(ENGINES);
        initSimpleViewElementListener(PROJECT_SPECIFIC);
        if (optionsChanges.get(ENGINES) != null) {
            Group plugin = createGroup(ph, S.s(639));
            DirectorySelectorView plug = createDirectoryOption(plugin, S.s(647), null, ENGINES, Licensing.canUseCoreAPI() ? "PluginsFolder" : null);
            if (!Licensing.canUseCoreAPI()) {
                plug.setEnabled(false);
            }
        }
        Group update = createGroup(ph, S.s(796));
        createBooleanOption(update, S.s(798), null, CLIENT, "CheckUpdates");
        Group proxy = createGroup(update, S.s(668));
        Button proxyButton = createProxyOption(proxy, S.s(669), null, CLIENT, "NetworkProxy");
        this.proxySubEntries.add(createComboBox(proxy, S.s(672), null, CLIENT, "NetworkProxy", 0, new String[]{"http", "socks"}));
        this.proxySubEntries.add(createTextOption(proxy, S.s(670), null, CLIENT, "NetworkProxy", 1));
        Text portText = createTextOption(proxy, S.s(671), null, CLIENT, "NetworkProxy", 2);
        this.proxySubEntries.add(portText);
        Group proxyAuth = createGroup(proxy, "Authentication");
        proxyAuth.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
        Text userText = createTextOption(proxyAuth, S.s(811), null, CLIENT, "NetworkProxy", 3);
        this.proxySubEntries.add(userText);
        Text passwordText = createTextOption(proxyAuth, S.s(631), null, CLIENT, "NetworkProxy", 4);
        this.proxySubEntries.add(passwordText);
        addVerifyListenerIntOnly(portText);
        ((OptionsSimpleListener) this.listeners.get(CLIENT)).addEnabledOnCheckbox(proxyButton, this.proxySubEntries);
        initProxyOptions(CLIENT, "NetworkProxy");
    }

    private Group createGroup(Composite parent, String label) {
        Group g = UIUtil.createGroup(parent, label);
        g.setLayoutData(UIUtil.createGridDataFillHorizontally());
        return g;
    }

    private void initSimpleViewElementListener(String propertyManagerKey) {
        OptionsSimpleListener listener = new OptionsSimpleListener();
        this.listeners.put(propertyManagerKey, listener);
        OptionsChanges.Changes c = getChanges(propertyManagerKey);
        if (c != null) {
            c.listeners.add(listener);
        }
    }

    private DirectorySelectorView createDirectoryOption(Composite parent, String label, String toolTip, String propertyManagerKey, String propertyKey) {
        return new OptionsSimpleViewText(getChanges(propertyManagerKey), (OptionsSimpleListener) this.listeners.get(propertyManagerKey), propertyKey).createDirectory(parent, label, toolTip);
    }

    private Text createTextOption(Composite parent, String label, String toolTip, String propertyManagerKey, String propertyKey, int tokenPosition) {
        return new OptionsSimpleViewText(getChanges(propertyManagerKey), (OptionsSimpleListener) this.listeners.get(propertyManagerKey), propertyKey).create(parent, label, toolTip, tokenPosition);
    }

    private void initProxyOptions(String propertyManagerKey, String propertyKey) {
        OptionsChanges.Changes c = getChanges(propertyManagerKey);
        boolean initialValue = !Strings.isBlank((String) AbstractOptionsSimpleWidget.getProperty(c, propertyKey, true));
        for (Control child : this.proxySubEntries) {
            child.setEnabled(initialValue);
        }
    }

    private Button createProxyOption(Composite parent, String label, String toolTip, final String propertyManagerKey, final String propertyKey) {
        Button b = UIUtil.createCheckbox(parent, label, new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                OptionsChanges.Changes c = OptionsSimpleViewGeneral.this.getChanges(propertyManagerKey);
                boolean newValue = ((Button) e.getSource()).getSelection();
                Object newValueObj;
                Composite parentButton = ((Button) e.getSource()).getParent();
                Object previousData;
                if (newValue) {
                    newValueObj = parentButton.getData("PREVIOUS_DATA");
                    if (newValueObj == null) {
                        newValueObj = AbstractOptionsSimpleWidget.getProperty(c, propertyKey, true);
                    }
                    if ((newValueObj == null) || (StringUtils.isEmpty(newValueObj.toString()))) {
                        newValueObj = "||";
                    }
                    parentButton.setData("PREVIOUS_DATA", null);
                } else {
                    previousData = c.changeList.get(propertyKey);
                    if (previousData == null) {
                        previousData = c.pm.getValue(propertyKey);
                    }
                    if ((previousData != null) && (StringUtils.isEmpty(previousData.toString()))) {
                        previousData = null;
                    }
                    parentButton.setData("PREVIOUS_DATA", previousData);
                    newValueObj = "";
                }
                for (previousData = OptionsSimpleViewGeneral.this.proxySubEntries.iterator(); ((Iterator) previousData).hasNext(); ) {
                    Control child = (Control) ((Iterator) previousData).next();
                    child.setEnabled(newValue);
                }
                c.addChange(propertyKey, newValueObj);
            }
        });
        OptionsChanges.Changes c = getChanges(propertyManagerKey);
        b.setSelection(StringUtils.isNotEmpty(c.pm.getString(propertyKey)));
        if (toolTip != null) {
            b.setToolTipText(toolTip);
        }
        b.setLayoutData(UIUtil.createGridDataSpanHorizontally(2));
        AbstractOptionsSimpleWidget.addSimpleViewElements((OptionsSimpleListener) this.listeners.get(propertyManagerKey), propertyKey, b);
        return b;
    }

    private OptionsChanges.Changes getChanges(String propertyManagerKey) {
        return this.optionsChanges.get(propertyManagerKey);
    }

    private Control createComboBox(Composite parent, String label, String toolTip, String propertyManagerKey, String propertyKey, int tokenPosition, String[] options) {
        return new OptionsSimpleViewCombo(getChanges(propertyManagerKey), this.listeners.get(propertyManagerKey), propertyKey).createComboBox(parent, label, toolTip, tokenPosition, options);
    }

    private Button createBooleanOption(Composite parent, String label, String toolTip, String propertyManagerKey, String propertyKey) {
        return new OptionsBooleanViewer(getChanges(propertyManagerKey), this.listeners.get(propertyManagerKey), propertyKey).create(parent, label, toolTip);
    }

    private void addVerifyListenerIntOnly(Text text) {
        text.addListener(25, new Listener() {
            public void handleEvent(Event e) {
                String string = e.text;
                char[] chars = new char[string.length()];
                string.getChars(0, chars.length, chars, 0);
                for (int i = 0; i < chars.length; i++) {
                    if (('0' > chars[i]) || (chars[i] > '9')) {
                        e.doit = false;
                        return;
                    }
                }
            }
        });
    }
}


