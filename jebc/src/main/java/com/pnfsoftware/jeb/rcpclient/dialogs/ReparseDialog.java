package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IPluginInformation;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.units.IBinaryUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitIdentifier;
import com.pnfsoftware.jeb.core.units.IUnitProcessor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ReparseDialog extends JebDialog {
    public static class Information {
        private IUnit unit;
        private String subUnitName;
        private String wantedType;
        private long offset;
        private long size;

        public Information(IUnit unit, String subUnitName, String wantedType, long offset, long size) {
            this.unit = unit;
            this.subUnitName = subUnitName;
            this.wantedType = wantedType;
            this.offset = offset;
            this.size = size;
        }

        public IUnit getUnit() {
            return this.unit;
        }

        public String getSubUnitName() {
            return this.subUnitName;
        }

        public String getWantedType() {
            return this.wantedType;
        }

        public long getOffset() {
            return this.offset;
        }

        public long getSize() {
            return this.size;
        }
    }

    private static final ILogger logger = GlobalLog.getLogger(ReparseDialog.class);
    private IUnit unit;
    private List<IUnitIdentifier> unitIdentifiers;
    private long maxsize;
    private Information reparseInfo;
    private Text widgetSubUnitName;
    private Combo widgetParsers;
    private Text widgetOffset;
    private Text widgetSize;
    private Text widgetEnd;
    private Button widgetNullInput;

    public ReparseDialog(Shell parent, IUnit unit) {
        super(parent, S.s(626), true, true);
        this.scrolledContainer = true;
        if (unit == null) {
            throw new NullPointerException();
        }
        this.unit = unit;
        this.unitIdentifiers = new ArrayList<>(RuntimeProjectUtil.findProject(unit).getProcessor().getUnitIdentifiers());
        Collections.sort(this.unitIdentifiers, new Comparator<IUnitIdentifier>() {
            public int compare(IUnitIdentifier o1, IUnitIdentifier o2) {
                return o1.getFormatType().compareTo(o2.getFormatType());
            }
        });
        this.maxsize = 0L;
        if ((unit instanceof IBinaryUnit)) {
            IInput input = ((IBinaryUnit) unit).getInput();
            this.maxsize = input.getCurrentSize();
        }
    }

    public Information open() {
        super.open();
        return this.reparseInfo;
    }

    public String getName() {
        return this.widgetSubUnitName.getText();
    }

    public String getWantedType() {
        int index = this.widgetParsers.getSelectionIndex();
        if ((index < 0) || (index >= this.unitIdentifiers.size())) {
            return null;
        }
        return this.unitIdentifiers.get(index).getFormatType();
    }

    private boolean isNullInput() {
        return this.widgetNullInput.getSelection();
    }

    private void setOffset(long offset) {
        this.widgetOffset.setText(String.format("%d", offset));
    }

    private long getOffset() {
        if ((this.maxsize < 0L) || (isNullInput())) {
            return -1L;
        }
        return Conversion.stringToLong(this.widgetOffset.getText(), 0L);
    }

    private void setSize(long size) {
        this.widgetSize.setText(String.format("%d", size));
    }

    public long getSize() {
        if ((this.maxsize < 0L) || (isNullInput())) {
            return -1L;
        }
        return Conversion.stringToLong(this.widgetSize.getText(), 0L);
    }

    private void updateEnd() {
        long end = getOffset() + getSize();
        this.widgetEnd.setText(String.format("%d", end));
    }

    protected void createContents(Composite parent) {
        GridLayout layout = new GridLayout(2, false);
        layout.marginLeft = (layout.marginRight = layout.marginTop = layout.marginBottom = 6);
        parent.setLayout(layout);
        new Label(parent, 0).setText(S.s(591) + ": ");
        this.widgetSubUnitName = new Text(parent, 2052);
        this.widgetSubUnitName.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetSubUnitName.setText("sub-unit");
        this.widgetSubUnitName.selectAll();
        this.widgetSubUnitName.setFocus();
        new Label(parent, 0).setText(S.s(627) + ": ");
        this.widgetParsers = new Combo(parent, 12);
        GridData layoutData = UIUtil.createGridDataFillHorizontally();
        layoutData.widthHint = UIUtil.determineTextWidth(this.widgetParsers, 40);
        this.widgetParsers.setLayoutData(layoutData);
        for (IUnitIdentifier id : this.unitIdentifiers) {
            this.widgetParsers.add(id.getFormatType());
        }
        new Label(parent, 0).setText(S.s(270) + ": ");
        final Text widgetPinfo = new Text(parent, 2060);
        widgetPinfo.setLayoutData(UIUtil.createGridDataFillHorizontally());
        widgetPinfo.setText("N/A");
        new Label(parent, 0).setText(S.s(388) + ": ");
        this.widgetNullInput = new Button(parent, 32);
        this.widgetNullInput.setText("null (parent unit only)");
        this.widgetNullInput.setEnabled(this.maxsize >= 0L);
        new Label(parent, 0).setText(S.s(604) + ": ");
        this.widgetOffset = new Text(parent, 2052);
        this.widgetOffset.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetOffset.setText("0");
        new Label(parent, 0).setText(S.s(741) + ": ");
        this.widgetSize = new Text(parent, 2052);
        this.widgetSize.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetSize.setText(String.format("%d", this.maxsize));
        new Label(parent, 0).setText(String.format("(%s: )", S.s(289)));
        this.widgetEnd = new Text(parent, 2052);
        this.widgetEnd.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetEnd.setText(String.format("%d", this.maxsize));
        createOkayCancelButtons(parent);
        final Button widgetOk = getButtonByStyle(32);
        widgetOk.setEnabled(false);
        this.widgetOffset.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                long offset = ReparseDialog.this.getOffset();
                if (offset < 0L) {
                    offset = 0L;
                    ReparseDialog.this.setOffset(0L);
                }
                if (offset > ReparseDialog.this.maxsize) {
                    ReparseDialog.this.setOffset(ReparseDialog.this.maxsize);
                    ReparseDialog.this.setSize(0L);
                } else {
                    long size = ReparseDialog.this.getSize();
                    if (offset + size >= ReparseDialog.this.maxsize) {
                        size = ReparseDialog.this.maxsize - offset;
                        ReparseDialog.this.setSize(size);
                    }
                }
                ReparseDialog.this.updateEnd();
            }
        });
        this.widgetSize.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                long size = ReparseDialog.this.getSize();
                long offset = ReparseDialog.this.getOffset();
                if (offset + size > ReparseDialog.this.maxsize) {
                    ReparseDialog.this.setOffset(ReparseDialog.this.maxsize);
                    ReparseDialog.this.setSize(0L);
                }
                ReparseDialog.this.updateEnd();
            }
        });
        this.widgetParsers.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = ReparseDialog.this.widgetParsers.getSelectionIndex();
                if ((index < 0) || (index >= ReparseDialog.this.unitIdentifiers.size())) {
                    return;
                }
                IPluginInformation pi = ((IUnitIdentifier) ReparseDialog.this.unitIdentifiers.get(index)).getPluginInformation();
                if (pi == null) {
                    return;
                }
                if (!widgetOk.isEnabled()) {
                    widgetOk.setEnabled(true);
                    ReparseDialog.this.shell.setDefaultButton(widgetOk);
                }
                String s = String.format("%s (v%s)", Strings.safe2(pi.getDescription(), String.format("(%s)", S.s(595))), Strings.safe2(pi.getVersion(), "?"));
                widgetPinfo.setText(s);
            }
        });
        this.widgetNullInput.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean enabled = !ReparseDialog.this.isNullInput();
                ReparseDialog.this.widgetOffset.setEnabled(enabled);
                ReparseDialog.this.widgetSize.setEnabled(enabled);
                ReparseDialog.this.widgetEnd.setEnabled(enabled);
            }
        });
    }

    protected void onConfirm() {
        this.reparseInfo = new Information(this.unit, getName(), getWantedType(), getOffset(), getSize());
        super.onConfirm();
    }
}


