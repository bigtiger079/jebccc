package com.pnfsoftware.jeb.rcpclient.dialogs.nativecode;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.BranchTarget;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IBranchResolution;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.IBranchTarget;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeAnalyzer;
import com.pnfsoftware.jeb.core.units.code.asm.analyzer.INativeCodeModel;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeInstructionItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.InstructionHints;
import com.pnfsoftware.jeb.core.units.code.asm.type.IPrototypeItem;
import com.pnfsoftware.jeb.core.units.code.asm.type.TypeStringParseException;
import com.pnfsoftware.jeb.core.units.code.asm.type.TypeStringParser;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.util.format.Formatter;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditInstructionDialog extends JebDialog {
    private long address;
    private INativeCodeUnit<?> unit;
    private boolean confirmed;
    private INativeInstructionItem ii;
    private Text widgetAddress;
    private Text widgetLabel;
    private Text widgetBytes;
    private Text widgetDisas;
    private Text widgetHintSPDelta;
    private Text widgetHintProto;
    private Text widgetDynTarget;
    private static final String KEY_DYNTARGET = "dynTargetItem";

    public EditInstructionDialog(Shell parent, long address, INativeCodeUnit<?> unit) {
        super(parent, "Edit the Instruction", true, true);
        this.scrolledContainer = true;
        this.address = address;
        this.unit = unit;
    }

    public Boolean open() {
        super.open();
        return this.confirmed;
    }

    protected void createContents(final Composite parent) {
        UIUtil.setStandardLayout(parent, 2);
        new Label(parent, 0).setText(S.s(52) + ": ");
        this.widgetAddress = UIUtil.createTextboxInGrid(parent, 2060, 50, 1, true, false);
        new Label(parent, 0).setText(S.s(424) + ": ");
        this.widgetLabel = new Text(parent, 2060);
        this.widgetLabel.setLayoutData(UIUtil.createGridDataFillHorizontally());
        new Label(parent, 0).setText("Bytes: ");
        this.widgetBytes = new Text(parent, 2060);
        this.widgetBytes.setLayoutData(UIUtil.createGridDataFillHorizontally());
        new Label(parent, 0).setText("Disassembly: ");
        this.widgetDisas = new Text(parent, 2060);
        this.widgetDisas.setLayoutData(UIUtil.createGridDataFillHorizontally());
        Group grp = UIUtil.createGroupGrid(parent, "Attributes", 2, 2);
        new Label(grp, 0).setText("SP Delta: ");
        this.widgetHintSPDelta = new Text(grp, 2052);
        this.widgetHintSPDelta.setLayoutData(UIUtil.createGridDataFillHorizontally());
        new Label(grp, 0).setText("Prototype Hint: ");
        this.widgetHintProto = new Text(grp, 2052);
        this.widgetHintProto.setLayoutData(UIUtil.createGridDataFillHorizontally());
        new Label(grp, 0).setText("Dynamic Target: ");
        Composite c = UIUtil.createCompositeGrid(grp, 1, 3);
        UIUtil.createTightPushbox(c, "Select", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                INativeMethodItem routine = new NativeRoutineChooserDialog(parent.getShell(), EditInstructionDialog.this.unit).open();
                if (routine != null) {
                    EditInstructionDialog.this.setDynTargetWidget(routine);
                }
            }
        });
        UIUtil.createTightPushbox(c, "Clear", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                EditInstructionDialog.this.setDynTargetWidget(null);
            }
        });
        this.widgetDynTarget = new Text(c, 2060);
        this.widgetDynTarget.setLayoutData(UIUtil.createGridDataFillHorizontally());
        loadInstruction(this.address);
        createOkayCancelButtons(parent);
    }

    private boolean loadInstruction(long address) {
        this.widgetAddress.setText("");
        this.widgetLabel.setText("");
        this.widgetBytes.setText("");
        this.widgetDisas.setText("");
        this.widgetHintSPDelta.setText("");
        this.widgetHintProto.setText("");
        setDynTargetWidget(null);
        this.ii = null;
        INativeItem item = this.unit.getNativeItemAt(address);
        if (!(item instanceof INativeInstructionItem)) {
            return false;
        }
        this.ii = ((INativeInstructionItem) item);
        this.widgetAddress.setText(Strings.safe(this.ii.getAddress()));
        this.widgetLabel.setText(Strings.safe(this.ii.getLabel()));
        this.widgetBytes.setText(Formatter.formatBinaryLine(this.ii.getInstruction().getCode()));
        this.widgetDisas.setText(this.ii.getInstruction().format(this.ii.getMemoryAddress()));
        InstructionHints hints = this.ii.getHints(false);
        if (hints != null) {
            this.widgetHintSPDelta.setText(Strings.safe(hints.getStackPointerDelta()));
            this.widgetHintProto.setText(Strings.safe(hints.getCallsitePrototype()));
        }
        IBranchTarget target = getCurrentlyStoredResolvedTarget();
        if (target != null) {
            setDynTargetWidget(target.getRoutine());
        }
        return true;
    }

    private IBranchTarget getCurrentlyStoredResolvedTarget() {
        IBranchResolution reso = this.unit.getCodeModel().getDynamicBranchResolution(this.address);
        return reso.isEmpty() ? null : reso.getResolvedTarget();
    }

    private void setDynTargetWidget(INativeMethodItem routine) {
        if (routine == null) {
            this.widgetDynTarget.setText("");
        } else {
            this.widgetDynTarget.setText(routine.getName(true));
        }
        this.widgetDynTarget.setData(KEY_DYNTARGET, routine);
    }

    protected void onConfirm() {
        this.confirmed = true;
        String text = this.widgetHintSPDelta.getText().trim();
        if (text.isEmpty()) {
            if (this.ii.getHints(false) != null) {
                this.ii.getHints(true).setStackPointerDelta(null);
            }
        } else {
            try {
                int spdelta = Integer.parseInt(text);
                this.ii.getHints(true).setStackPointerDelta(spdelta);
            } catch (NumberFormatException e) {
                UI.error("Cannot parse integer value for SP delta");
                return;
            }
        }
        text = this.widgetHintProto.getText().trim();
        if (text.isEmpty()) {
            if (this.ii.getHints(false) != null) {
                this.ii.getHints(true).setCallsitePrototype(null);
            }
        } else {
            try {
                IPrototypeItem proto = new TypeStringParser(this.unit.getTypeManager()).parsePrototype(text);
                this.ii.getHints(true).setCallsitePrototype(proto);
            } catch (TypeStringParseException e) {
                UI.error("Cannot parse prototype. The expected format is:\n\n<callingConvention> returnType(param1Type, param2Type, ...)");
                return;
            }
        }
        INativeMethodItem routine = (INativeMethodItem) this.widgetDynTarget.getData(KEY_DYNTARGET);
        if (routine == null) {
            IBranchTarget current = getCurrentlyStoredResolvedTarget();
            if (current != null) {
                this.unit.getCodeAnalyzer().unrecordDynamicBranchTarget(this.address, true, current);
            }
        } else {
            this.unit.getCodeAnalyzer().recordDynamicBranchTarget(this.address, true, new BranchTarget(routine), false);
        }
        super.onConfirm();
    }
}


