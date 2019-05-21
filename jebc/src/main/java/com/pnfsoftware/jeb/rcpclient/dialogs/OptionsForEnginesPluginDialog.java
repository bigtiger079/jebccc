package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IEnginesPlugin;
import com.pnfsoftware.jeb.core.IOptionDefinition;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class OptionsForEnginesPluginDialog extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(OptionsForEnginesPluginDialog.class);
    private IEnginesPlugin plugin;
    private List<Text> widgetDatas = new ArrayList<>();
    private Map<String, String> options;

    public OptionsForEnginesPluginDialog(Shell parent, IEnginesPlugin plugin) {
        super(parent, S.s(317), true, true);
        this.scrolledContainer = true;
        this.plugin = plugin;
    }

    public Map<String, String> open() {
        super.open();
        return this.options;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);
        new Label(parent, 0).setText(S.s(651));
        Group g = new Group(parent, 0);
        g.setText("Options");
        g.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
        g.setLayout(new GridLayout(2, false));
        List<? extends IOptionDefinition> deflist = this.plugin.getExecutionOptionDefinitions();
        if (deflist != null) {
            for (IOptionDefinition opt : deflist) {
                String name = opt.getName();
                String desc = Strings.safe(opt.getDescription());
                String defaultValue = opt.getDefaultValue();
                if (name == null) {
                    Label label = new Label(g, 0);
                    label.setText(desc);
                    label.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
                } else if (name.isEmpty()) {
                    logger.warn("Invalid property definition (empty)");
                } else {
                    new Label(g, 0).setText(desc);
                    Text widgetData = new Text(g, 2052);
                    widgetData.setLayoutData(UIUtil.createGridDataForText(widgetData, 30));
                    widgetData.setData("optionName", name);
                    widgetData.setText(Strings.safe(defaultValue));
                    this.widgetDatas.add(widgetData);
                }
            }
        }
        createOkayCancelButtons(parent);
    }

    protected void onConfirm() {
        this.options = new HashMap<>();
        for (Text widgetData : this.widgetDatas) {
            this.options.put((String) widgetData.getData("optionName"), widgetData.getText());
        }
        super.onConfirm();
    }
}


