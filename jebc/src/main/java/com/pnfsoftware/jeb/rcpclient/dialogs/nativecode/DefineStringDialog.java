package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.StringType;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DefineStringDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(DefineStringDialog.class);
    private long address;
    private INativeCodeUnit<?> unit;
    private boolean confirmed;
    private StringSetupInformation info = new StringSetupInformation();
    private Label widgetInfo;
    private Text widgetAddress;
    private Text widgetMinCount;
    private Text widgetMaxCount;
    private Combo widgetStringTypes;
    private Button widgetOk;

    public DefineStringDialog(Shell parent, long address, INativeCodeUnit<?> unit) {
        super(parent, "Define String", true, true);
        this.scrolledContainer = true;
        this.address = address;
        this.unit = unit;
    }

    public StringSetupInformation open() {
        super.open();
        if (!this.confirmed) {
            return null;
        }
        return this.info;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 4);
        this.widgetInfo = new Label(parent, 0);
        this.widgetInfo.setText("N/A");
        this.widgetInfo.setLayoutData(UIUtil.createGridDataSpanHorizontally(4, true, false));
        Group grp = new Group(parent, 0);
        grp.setText("Properties");
        UIUtil.setStandardLayout(grp, 4);
        grp.setLayoutData(UIUtil.createGridDataSpanHorizontally(4, true, false));
        new Label(grp, 0).setText(S.s(52) + ": ");
        this.widgetAddress = new Text(grp, 2052);
        this.widgetAddress.setLayoutData(UIUtil.createGridDataForText(this.widgetAddress, 16));
        this.widgetAddress.setText(String.format("%Xh", new Object[]{Long.valueOf(this.address)}));
        this.widgetAddress.selectAll();
        this.widgetAddress.setFocus();
        new Label(grp, 0).setText("String type: ");
        this.widgetStringTypes = new Combo(grp, 12);
        GridData layoutData = UIUtil.createGridDataFillHorizontally();
        this.widgetStringTypes.setLayoutData(layoutData);
        this.widgetStringTypes.add("(Automatic)");
        for (StringType st : StringType.values()) {
            this.widgetStringTypes.add(st.toString());
        }
        this.widgetStringTypes.select(0);
        new Label(grp, 0).setText("Min chars: ");
        this.widgetMinCount = new Text(grp, 2052);
        this.widgetMinCount.setLayoutData(UIUtil.createGridDataForText(this.widgetAddress, 8));
        this.widgetMinCount.setText("");
        this.widgetMinCount.selectAll();
        new Label(grp, 0).setText("Max chars: ");
        this.widgetMaxCount = new Text(grp, 2052);
        this.widgetMaxCount.setLayoutData(UIUtil.createGridDataForText(this.widgetAddress, 8));
        this.widgetMaxCount.setText(String.format("%X", new Object[]{Long.valueOf(this.address)}));
        this.widgetMaxCount.setText("");
        this.widgetMaxCount.selectAll();
        createOkayCancelButtons(parent);
        this.widgetOk = getButtonByStyle(32);
        this.widgetAddress.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                DefineStringDialog.this.update();
            }
        });
        this.widgetAddress.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                DefineStringDialog.this.update();
            }
        });
        update();
    }

    protected void onConfirm() {
        this.info = new StringSetupInformation();
        this.info.address = getSelectedAddress();
        this.info.addressMax = -1L;
        this.info.minChars = Conversion.stringToInt(this.widgetMinCount.getText(), -1);
        this.info.maxChars = Conversion.stringToInt(this.widgetMaxCount.getText(), -1);
        this.info.stringType = getSelectedStringType();
        this.confirmed = true;
        super.onConfirm();
    }

    private void update() {
        long a = getSelectedAddress();
        this.widgetOk.setEnabled(a >= 0L);
        String info;
        if (a < 0L) {
            info = "Invalid address";
        } else {
            if (this.unit == null) {
                info = String.format("No native unit was provided, cannot retrieve information about location %Xh.", new Object[]{Long.valueOf(a)});
            } else {
                INativeItem item = this.unit.getNativeItemOver(a);
                if (item != null) {
                    info = String.format("Beware, an item already occupies address %Xh.", new Object[]{Long.valueOf(a)});
                } else {
                    info = String.format("Will attempt to define string at address %Xh", new Object[]{Long.valueOf(a)});
                }
            }
        }
        this.widgetInfo.setText(info);
    }

    private long getSelectedAddress() {
        return Conversion.stringToLong(this.widgetAddress.getText(), -1L);
    }

    private StringType getSelectedStringType() {
        int index = this.widgetStringTypes.getSelectionIndex();
        if ((index <= 0) || (index > StringType.values().length)) {
            return null;
        }
        return StringType.values()[(index - 1)];
    }
}


