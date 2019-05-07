/*     */
package com.pnfsoftware.jeb.rcpclient.handlers.nativeactions;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.ICodeType;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.items.INativeMethodItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.INativeType;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.IPrototypeItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeLibrary;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.ITypeManager;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.TypeLibraryService;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.type.TypeUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
/*     */ import com.pnfsoftware.jeb.util.collect.Lists;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;

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
/*     */ public class ActionEditTypeHandler
        /*     */ extends NativeCodeBaseHandler
        /*     */ {
    /*  30 */   private static final ILogger logger = GlobalLog.getLogger(ActionEditTypeHandler.class);

    /*     */
    /*     */
    public ActionEditTypeHandler() {
        /*  33 */
        super("editType", S.s(495), 89);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean canExecute()
    /*     */ {
        /*  39 */
        return canExecuteAndNativeCheck(this.part, true, true);
        /*     */
    }

    /*     */
    /*     */
    public void execute()
    /*     */ {
        /*  44 */
        INativeCodeUnit<?> pbcu = getNativeCodeUnit(this.part, true);
        /*  45 */
        long a = getActiveMemoryAddress(this.part, pbcu, true);
        /*     */
        /*  47 */
        String currentTypeSig = null;
        /*  48 */
        INativeMethodItem m = pbcu.getInternalMethod(a, true);
        /*  49 */
        if (m != null) {
            /*  50 */
            IPrototypeItem proto = m.getPrototype();
            /*  51 */
            currentTypeSig = proto == null ? null : proto.getSignature(true);
            /*     */
        }
        /*     */
        else {
            /*  54 */
            ICodeType type = pbcu.getDataTypeAt(a);
            /*  55 */
            currentTypeSig = type == null ? null : type.getSignature(true);
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*  62 */
        String caption = String.format("Edit type at %Xh", new Object[]{Long.valueOf(a)});
        /*  63 */
        TextDialog dlg = new TextDialog(this.shell, caption, currentTypeSig, null);
        /*  64 */
        dlg.setLineCount(1);
        /*  65 */
        dlg.setSelected(true);
        /*  66 */
        String typestr = dlg.open();
        /*  67 */
        if (typestr == null) {
            /*  68 */
            return;
            /*     */
        }
        /*     */
        /*  71 */
        if (m != null) {
            /*  72 */
            boolean r = pbcu.setRoutinePrototype(m, typestr);
            /*  73 */
            if (!r) {
                /*  74 */
                logger.error("Failed to set prototype of method %s (%Xh)", new Object[]{m.getName(true), Long.valueOf(a)});
                /*     */
            }
            /*     */
        }
        /*     */
        else
            /*     */ {
            /*  79 */
            ITypeManager typeman = pbcu.getTypeManager();
            /*  80 */
            INativeType t = typeman.getType(typestr);
            /*  81 */
            if (t == null)
                /*     */ {
                /*  83 */
                t = (INativeType) Lists.getFirst(TypeUtil.findType(pbcu.getTypeManager().getTypes(), typestr, true));
                /*  84 */
                if (t == null)
                    /*     */ {
                    /*  86 */
                    for (ITypeLibrary typelib : pbcu.getTypeLibraryService().getLoadedTypeLibraries()) {
                        /*  87 */
                        t = (INativeType) Lists.getFirst(TypeUtil.findType(typelib.getTypes(), typestr, true));
                        /*  88 */
                        if (t != null) {
                            /*     */
                            break;
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /*  94 */
            if (t == null) {
                /*  95 */
                return;
                /*     */
            }
            /*  97 */
            if (!pbcu.setDataTypeAt(a, t)) {
                /*  98 */
                logger.error("Failed to set type at address %Xh", new Object[]{Long.valueOf(a)});
                /*     */
            }
            /*     */
        }
        /*     */
        /* 102 */
        postExecute(this.shell);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\nativeactions\ActionEditTypeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */