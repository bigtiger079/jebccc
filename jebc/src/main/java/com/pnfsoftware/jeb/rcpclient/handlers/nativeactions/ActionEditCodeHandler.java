package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
import com.pnfsoftware.jeb.core.units.code.asm.items.INativeInstructionItem;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
import com.pnfsoftware.jeb.core.units.code.asm.processor.ProcessorException;
import com.pnfsoftware.jeb.rcpclient.dialogs.AdaptivePopupDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.CodeSetupInformation;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.DefineCodeDialog;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.SortedMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ActionEditCodeHandler extends NativeCodeBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionEditCodeHandler.class);

    public ActionEditCodeHandler() {
        super("editCode", "Disassemble Instructions...", SWT.MOD1 | SWT.MOD3 | 0x43);
    }

    public boolean canExecute() {
        return canExecuteAndNativeCheck(this.part, true);
    }

    public void execute() {
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        long a = getActiveMemoryAddress(this.part, pbcu);
        DefineCodeDialog dlg = new DefineCodeDialog(this.shell, a, pbcu);
        CodeSetupInformation info = dlg.open();
        if (info == null) {
            return;
        }
        if (!disassemble(this.shell, pbcu, info)) {
            logger.error("Failed to define code at address %Xh", new Object[]{Long.valueOf(a)});
        }
        postExecute(this.shell);
    }

    static boolean disassemble(Shell shell, INativeCodeUnit<? extends IInstruction> pbcu, CodeSetupInformation info) {
        long address = info.getAddress();
        int cnt = 0;
        while ((info.getMaxInstructionCount() == -1) || (cnt < info.getMaxInstructionCount())) {
            boolean r = pbcu.setCodeAt(address, info.getProcessorMode(), false);
            if ((!r) && (cnt == 0)) {
                boolean override = pbcu.getNativeItemAt(address) != null;
                if (!override) {
                    IInstruction insn = null;
                    try {
                        insn = pbcu.getProcessor().parseAt(pbcu.getMemory(), address);
                    } catch (ProcessorException e) {
                        break;
                    }
                    if (insn != null) {
                        int insnSize = insn.getSize();
                        SortedMap<Long, INativeContinuousItem> natives = pbcu.getNativeItemsOver(address, insnSize);
                        if ((natives != null) && (!natives.isEmpty())) {
                            override = true;
                        }
                    }
                }
                if (override) {
                    AdaptivePopupDialog dlg = new AdaptivePopupDialog(shell, 2, S.s(207), "Undefine existing items to create an instruction item?", null);
                    if (dlg.open().intValue() == 1) {
                        r = pbcu.setCodeAt(address, info.getProcessorMode(), true);
                    }
                }
            }
            if (!r) {
                break;
            }
            INativeContinuousItem item = pbcu.getNativeItemAt(address);
            if ((item == null) || (!(item instanceof INativeInstructionItem))) {
                logger.error("Expected an instruction item at address %Xh, instead got: %s", new Object[]{Long.valueOf(address), item});
                break;
            }
            address += item.getMemorySize();
            cnt++;
        }
        return cnt >= 1;
    }
}


