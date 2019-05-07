package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinition;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ReparseOptionsDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(ReparseOptionsDialog.class);

    private IUnit unit;
    private List<Text> widgetDatas = new ArrayList();
    private Map<String, Object> options;

    public ReparseOptionsDialog(Shell parent, IUnit unit) {
        super(parent, "Processing options", true, true);
        this.unit = unit;
    }

    public Map<String, Object> open() {
        super.open();
        return this.options;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);


        Group g = new Group(parent, 0);
        g.setText("Options");
        g.setLayoutData(UIUtil.createGridDataSpanHorizontally(2));
        g.setLayout(new GridLayout(2, false));

        IPropertyDefinitionManager pdm = this.unit.getPropertyDefinitionManager();
        IPropertyManager pm = this.unit.getPropertyManager();
        for (IPropertyDefinition def : pdm.getDefinitions()) {
            String name = def.getName();
            Object value = pm.getValue(name);

            new Label(g, 0).setText(name);

            Text widgetData = new Text(g, 2052);
            widgetData.setLayoutData(UIUtil.createGridDataFillHorizontally());
            widgetData.setData("optionName", name);
            this.widgetDatas.add(widgetData);

            widgetData.setText(Strings.safe(value));
        }

        Composite buttons = new Composite(parent, 0);
        buttons.setLayoutData(UIUtil.createGridDataSpanHorizontally(2));
        buttons.setLayout(new RowLayout(256));

        Button btn_ok = UIUtil.createPushbox(buttons, S.s(605), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ReparseOptionsDialog.this.options = new HashMap();
                for (Text widgetData : ReparseOptionsDialog.this.widgetDatas) {
                    ReparseOptionsDialog.this.options.put((String) widgetData.getData("optionName"), widgetData.getText());
                }
                ReparseOptionsDialog.this.shell.close();
            }
        });
        UIUtil.createPushbox(buttons, S.s(105), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                ReparseOptionsDialog.this.options = null;
                ReparseOptionsDialog.this.shell.close();
            }
        });
        this.shell.setDefaultButton(btn_ok);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\ReparseOptionsDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */