/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeContinuousItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeInstructionItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.processor.ProcessorException;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.AdaptivePopupDialog;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.CodeSetupInformation;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.DefineCodeDialog;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.SortedMap;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.widgets.Shell;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class ActionEditCodeHandler
        /*     */ extends NativeCodeBaseHandler
        /*     */ {
    /*  33 */   private static final ILogger logger = GlobalLog.getLogger(ActionEditCodeHandler.class);

    /*     */
    /*     */
    public ActionEditCodeHandler()
    /*     */ {
        /*  37 */
        super("editCode", "Disassemble Instructions...", SWT.MOD1 | SWT.MOD3 | 0x43);
        /*     */
    }

    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  42 */
        return canExecuteAndNativeCheck(this.part, true);
        /*     */
    }

    /*     */
    /*     */
    public void execute()
    /*     */ {
        /*  47 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part);
        /*  48 */
        long a = getActiveMemoryAddress(this.part, pbcu);
        /*     */
        /*  50 */
        DefineCodeDialog dlg = new DefineCodeDialog(this.shell, a, pbcu);
        /*  51 */
        CodeSetupInformation info = dlg.open();
        /*  52 */
        if (info == null) {
            /*  53 */
            return;
            /*     */
        }
        /*     */
        /*  56 */
        if (!disassemble(this.shell, pbcu, info)) {
            /*  57 */
            logger.error("Failed to define code at address %Xh", new Object[]{Long.valueOf(a)});
            /*     */
        }
        /*     */
        /*  60 */
        postExecute(this.shell);
        /*     */
    }

    /*     */
    /*     */
    static boolean disassemble(Shell shell, INativeCodeUnit<? extends IInstruction> pbcu, CodeSetupInformation info) {
        /*  64 */
        long address = info.getAddress();
        /*  65 */
        int cnt = 0;
        /*  66 */
        while ((info.getMaxInstructionCount() == -1) || (cnt < info.getMaxInstructionCount())) {
            /*  67 */
            boolean r = pbcu.setCodeAt(address, info.getProcessorMode(), false);
            /*  68 */
            if ((!r) &&
                    /*  69 */         (cnt == 0))
                /*     */ {
                /*  71 */
                boolean override = pbcu.getNativeItemAt(address) != null;
                /*  72 */
                if (!override)
                    /*     */ {
                    /*  74 */
                    IInstruction insn = null;
                    /*     */
                    try {
                        /*  76 */
                        insn = pbcu.getProcessor().parseAt(pbcu.getMemory(), address);
                        /*     */
                    }
                    /*     */ catch (ProcessorException e)
                        /*     */ {
                        /*     */
                        break;
                        /*     */
                    }
                    /*  82 */
                    if (insn != null) {
                        /*  83 */
                        int insnSize = insn.getSize();
                        /*  84 */
                        SortedMap<Long, INativeContinuousItem> natives = pbcu.getNativeItemsOver(address, insnSize);
                        /*  85 */
                        if ((natives != null) && (!natives.isEmpty())) {
                            /*  86 */
                            override = true;
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*  90 */
                if (override)
                    /*     */ {
                    /*     */
                    /*  93 */
                    AdaptivePopupDialog dlg = new AdaptivePopupDialog(shell, 2, S.s(207), "Undefine existing items to create an instruction item?", null);
                    /*  94 */
                    if (dlg.open().intValue() == 1) {
                        /*  95 */
                        r = pbcu.setCodeAt(address, info.getProcessorMode(), true);
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*     */
            /* 100 */
            if (!r) {
                /*     */
                break;
                /*     */
            }
            /*     */
            /* 104 */
            INativeContinuousItem item = pbcu.getNativeItemAt(address);
            /* 105 */
            if ((item == null) || (!(item instanceof INativeInstructionItem))) {
                /* 106 */
                logger.error("Expected an instruction item at address %Xh, instead got: %s", new Object[]{Long.valueOf(address), item});
                /* 107 */
                break;
                /*     */
            }
            /*     */
            /* 110 */
            address += item.getMemorySize();
            /* 111 */
            cnt++;
            /*     */
        }
        /*     */
        /* 114 */
        return cnt >= 1;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionEditCodeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */