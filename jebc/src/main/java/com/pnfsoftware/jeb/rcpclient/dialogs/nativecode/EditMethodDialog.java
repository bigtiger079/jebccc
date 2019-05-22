package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodDataItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.InputField;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.TriStateField;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditMethodDialog extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(EditMethodDialog.class);
    private INativeCodeUnit<?> unit;
    private INativeMethodItem method;
    private boolean confirmed;
    private MethodSetupInformation info = new MethodSetupInformation();
    private Text widgetMethodName;
    private Text widgetSig;
    private Text widgetAddress;
    private Text widgetAddressEnd;
    private InputField wRoutineDataSPDeltaOnReturn;
    private TriStateField wRoutineNonReturning;

    public EditMethodDialog(Shell parent, INativeCodeUnit<?> unit, INativeMethodItem method) {
        super(parent, "Edit Method", true, true);
        this.scrolledContainer = true;
        this.unit = unit;
        this.method = method;
    }

    public MethodSetupInformation open() {
        super.open();
        if (!this.confirmed) {
            return null;
        }
        return this.info;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 2);
        Group grp0 = UIUtil.createGroupGrid(parent, "Declaration", 2, 2);
        new Label(grp0, 0).setText("Name: ");
        this.widgetMethodName = new Text(grp0, 2052);
        this.widgetMethodName.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetMethodName.setText(this.method.getName(true));
        this.widgetMethodName.setEditable(false);
        new Label(grp0, 0).setText("Signature: ");
        this.widgetSig = new Text(grp0, 2052);
        this.widgetSig.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetSig.setText(this.method.getSignature(true));
        this.widgetSig.setEditable(false);
        buildItemAttributesWidget(grp0, this.method);
        INativeMethodDataItem methodData = this.method.getData();
        if (methodData != null) {
            Group grp1 = UIUtil.createGroupGrid(parent, "Internal Data", 2, 2);
            new Label(grp1, 0).setText("Entry-point: ");
            this.widgetAddress = new Text(grp1, 2052);
            this.widgetAddress.setLayoutData(UIUtil.createGridDataFillHorizontally());
            this.widgetAddress.setText(String.format("%Xh", methodData.getMemoryAddress()));
            this.widgetAddress.setEditable(false);
            new Label(grp1, 0).setText("Begin: ");
            this.widgetAddressEnd = new Text(grp1, 2052);
            this.widgetAddressEnd.setLayoutData(UIUtil.createGridDataFillHorizontally());
            this.widgetAddressEnd.setText(String.format("%Xh", methodData.getCFG().getFirstAddress()));
            this.widgetAddressEnd.setEditable(false);
            new Label(grp1, 0).setText("End: ");
            this.widgetAddressEnd = new Text(grp1, 2052);
            this.widgetAddressEnd.setLayoutData(UIUtil.createGridDataFillHorizontally());
            this.widgetAddressEnd.setText(String.format("%Xh", methodData.getCFG().getEndAddress()));
            this.widgetAddressEnd.setEditable(false);
            buildItemAttributesWidget(grp1, methodData);
        }
        createOkayCancelButtons(parent);
        getButtonByStyle(32);
        update();
    }

    protected void onConfirm() {
        this.info = new MethodSetupInformation();
        String s = this.wRoutineDataSPDeltaOnReturn.getText();
        this.info.routineDataSPDeltaOnReturn = (s.isEmpty() ? null : Conversion.stringToInt(s));
        this.info.routineNonReturning = this.wRoutineNonReturning.getState();
        this.confirmed = true;
        super.onConfirm();
    }

    private void update() {
    }

    public void buildItemAttributesWidget(Composite parent, final INativeItem item) {
        new Label(parent, 0).setText("Attributes: ");
        if (item.getAttributes().isEmpty()) {
            new Label(parent, 0).setText("-");
        } else {
            final List widgetAttr = new List(parent, 2052);
            GridData layoutData = UIUtil.createGridDataFillHorizontally();
            layoutData.widthHint = UIUtil.determineTextWidth(widgetAttr, 30);
            widgetAttr.setLayoutData(layoutData);
            for (String key : item.getAttributes().keySet()) {
                widgetAttr.add(key);
            }
            new Label(parent, 0).setText("(Attr. value) ");
            final Label widgetAttrInfo = new Label(parent, 2048);
            widgetAttrInfo.setText("N/A");
            widgetAttrInfo.setLayoutData(UIUtil.createGridDataFillHorizontally());
            widgetAttr.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    String[] sel = widgetAttr.getSelection();
                    if (sel.length == 0) {
                        widgetAttrInfo.setText("");
                    } else {
                        Object o = item.getAttribute(sel[0], Object.class);
                        String val;
                        if ((o instanceof INativeItem)) {
                            val = ((INativeItem) o).getName(true);
                        } else {
                            val = Strings.safe(o);
                        }
                        widgetAttrInfo.setText(val);
                    }
                }
            });
        }
        Group grp = new Group(parent, 0);
        grp.setText("Customizable attributes:");
        grp.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, true));
        UIUtil.setStandardLayout(grp, 1);
        if ((item instanceof INativeMethodDataItem)) {
            INativeMethodDataItem routineData = (INativeMethodDataItem) item;
            Integer spdelta = routineData.getSPDeltaOnReturn();
            this.wRoutineDataSPDeltaOnReturn = new InputField(grp, "Stack pointer delta on return", spdelta == null ? "" : spdelta.toString(), 4);
        }
        if ((item instanceof INativeMethodItem)) {
            INativeMethodItem routine = (INativeMethodItem) item;
            Boolean nonReturning = routine.getNonReturning();
            this.wRoutineNonReturning = new TriStateField(grp, "Method does not return", nonReturning, null);
        }
    }
}
