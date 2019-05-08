package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DefineCodeDialog extends JebDialog {
    private long address;
    private INativeCodeUnit<?> unit;
    private boolean confirmed;
    private CodeSetupInformation info = new CodeSetupInformation(-1L);
    private Label widgetInfo;
    private Text widgetAddress;
    private Text widgetProcMode;
    private Text widgetMaxInsnCount;
    private Button widgetOk;

    public DefineCodeDialog(Shell parent, long address, INativeCodeUnit<?> unit) {
        super(parent, "Define code", true, true);
        this.scrolledContainer = true;
        this.address = address;
        this.unit = unit;
    }

    public void setDefaults(CodeSetupInformation info) {
        if (info == null) {
            throw new IllegalArgumentException();
        }
        this.info = info;
        this.address = info.getAddress();
    }

    public CodeSetupInformation open() {
        super.open();
        if (!this.confirmed) {
            return null;
        }
        return this.info;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);
        this.widgetInfo = new Label(parent, 0);
        this.widgetInfo.setText("N/A");
        this.widgetInfo.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
        new Label(parent, 0).setText(S.s(52) + ": ");
        this.widgetAddress = new Text(parent, 2052);
        this.widgetAddress.setLayoutData(UIUtil.createGridDataForText(this.widgetAddress, 16));
        this.widgetAddress.setText(String.format("%Xh", new Object[]{Long.valueOf(this.address)}));
        this.widgetAddress.selectAll();
        this.widgetAddress.setFocus();
        Group g1 = new Group(parent, 0);
        g1.setText("Mode");
        g1.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
        g1.setLayout(new GridLayout(2, false));
        createHelpLabel(g1, "0: \"default\" mode; -1: \"current\" mode; else, use 16/32/64/etc.");
        new Label(g1, 0).setText(S.s(723) + ": ");
        this.widgetProcMode = new Text(g1, 2052);
        GridData textGridData = UIUtil.createGridDataFillHorizontally();
        textGridData.minimumWidth = 30;
        this.widgetProcMode.setLayoutData(textGridData);
        this.widgetProcMode.setText("" + this.info.getProcessorMode());
        this.widgetProcMode.selectAll();
        Group g2 = new Group(parent, 0);
        g2.setText("Count");
        g2.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
        g2.setLayout(new GridLayout(2, false));
        createHelpLabel(g2, "Use -1 to disassemble as many instructions as possible.");
        new Label(g2, 0).setText("Maximum instruction count: ");
        this.widgetMaxInsnCount = new Text(g2, 2052);
        this.widgetMaxInsnCount.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetMaxInsnCount.setText("" + this.info.getMaxInstructionCount());
        this.widgetMaxInsnCount.selectAll();
        createOkayCancelButtons(parent);
        this.widgetOk = getButtonByStyle(32);
        this.widgetAddress.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                DefineCodeDialog.this.update();
            }
        });
        this.widgetAddress.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                DefineCodeDialog.this.update();
            }
        });
        update();
    }

    protected void onConfirm() {
        this.confirmed = true;
        this.info.address = getSelectedAddress();
        this.info.procMode = Conversion.stringToInt(this.widgetProcMode.getText(), -1);
        this.info.maxInsnCount = Conversion.stringToInt(this.widgetMaxInsnCount.getText(), -1);
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
                    info = String.format("Will attempt to disassemble code at address %Xh", new Object[]{Long.valueOf(a)});
                }
            }
        }
        this.widgetInfo.setText(info);
    }

    private long getSelectedAddress() {
        return Conversion.stringToLong(this.widgetAddress.getText(), -1L);
    }

    void createHelpLabel(Composite parent, String text) {
        Label label = new Label(parent, 0);
        label.setText(text);
        label.setLayoutData(UIUtil.createGridDataSpanHorizontally(2));
    }
}


