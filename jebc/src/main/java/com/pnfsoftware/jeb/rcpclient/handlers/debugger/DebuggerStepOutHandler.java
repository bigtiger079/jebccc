package com.pnfsoftware.jeb.rcpclient.handlers.debugger;

import com.pnfsoftware.jeb.client.S;

public class DebuggerStepOutHandler extends DebuggerBaseHandler {
    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerStepOutHandler.execute():void, dex: 
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:118)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:248)
        	at jadx.core.ProcessClass.process(ProcessClass.java:29)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.base/java.lang.Iterable.forEach(Unknown Source)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.getJavaMethodByNode(JadxDecompiler.java:322)
        	at jadx.api.JavaClass.convertNode(JavaClass.java:163)
        	at jadx.api.JavaClass.getJavaNodeAtPosition(JavaClass.java:181)
        Caused by: jadx.core.utils.exceptions.DecodeException: Unknown instruction: 'invoke-custom'
        	at jadx.core.dex.instructions.InsnDecoder.decode(InsnDecoder.java:581)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:74)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:104)
        	... 11 more
        */
    public void execute() {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerStepOutHandler.execute():void, dex: 
        */
        throw new UnsupportedOperationException("Method not decompiled: com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerStepOutHandler.execute():void");
    }

    public DebuggerStepOutHandler() {
        super("dbgStepOut", S.s(572), null, "eclipse/stepreturn_co.png", 16777232);
    }

    public boolean canExecute() {
        return canStepOperation(this.part);
    }
}