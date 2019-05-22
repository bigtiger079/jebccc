package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

public abstract class UIRunnable implements Runnable {
    private static final ILogger logger = GlobalLog.getLogger(UIRunnable.class);
    public static final long UITHREAD_BLOCK_WARNING_TIME = 100L;
    public static final long UITHREAD_BLOCK_REPORTING_TIME = 1000L;
    private String name;
    private long createdTs;
    private long execStartTs;
    private long execEndTs;

    public UIRunnable(String name) {
        this.name = name;
        this.createdTs = System.currentTimeMillis();
    }

    public UIRunnable() {
        this(null);
        if (Licensing.isDebugBuild()) {
            StackTraceElement[] elts = Thread.currentThread().getStackTrace();
            if (elts.length >= 2) {
                StackTraceElement e = elts[1];
                if (Licensing.isDebugBuild()) {
                    this.name = e.toString();
                } else {
                    this.name = String.format("%s.%s", e.getClassName(), e.getMethodName());
                }
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public abstract void runi();

    public final void run() {
        // Byte code:
        //   0: aload_0
        //   1: invokestatic 39	java/lang/System:currentTimeMillis	()J
        //   4: putfield 25	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execStartTs	J
        //   7: aload_0
        //   8: invokevirtual 31	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:runi	()V
        //   11: aload_0
        //   12: invokestatic 39	java/lang/System:currentTimeMillis	()J
        //   15: putfield 24	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execEndTs	J
        //   18: aload_0
        //   19: getfield 24	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execEndTs	J
        //   22: aload_0
        //   23: getfield 25	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execStartTs	J
        //   26: lsub
        //   27: lstore_1
        //   28: lload_1
        //   29: ldc2_w 19
        //   32: lcmp
        //   33: ifle +40 -> 73
        //   36: getstatic 26	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:logger	Lcom/pnfsoftware/jeb/util/logging/ILogger;
        //   39: ldc 3
        //   41: iconst_3
        //   42: anewarray 10	java/lang/Object
        //   45: dup
        //   46: iconst_0
        //   47: aload_0
        //   48: getfield 27	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:name	Ljava/lang/String;
        //   51: aastore
        //   52: dup
        //   53: iconst_1
        //   54: lload_1
        //   55: invokestatic 33	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   58: aastore
        //   59: dup
        //   60: iconst_2
        //   61: ldc2_w 19
        //   64: invokestatic 33	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   67: aastore
        //   68: invokeinterface 44 3 0
        //   73: goto +194 -> 267
        //   76: astore_1
        //   77: aload_1
        //   78: getfield 28	org/eclipse/swt/SWTException:code	I
        //   81: bipush 24
        //   83: if_icmpeq +21 -> 104
        //   86: aload_1
        //   87: getfield 28	org/eclipse/swt/SWTException:code	I
        //   90: bipush 44
        //   92: if_icmpeq +12 -> 104
        //   95: aload_1
        //   96: getfield 28	org/eclipse/swt/SWTException:code	I
        //   99: bipush 45
        //   101: if_icmpne +33 -> 134
        //   104: getstatic 26	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:logger	Lcom/pnfsoftware/jeb/util/logging/ILogger;
        //   107: ldc 2
        //   109: iconst_1
        //   110: anewarray 10	java/lang/Object
        //   113: dup
        //   114: iconst_0
        //   115: aload_1
        //   116: invokevirtual 42	org/eclipse/swt/SWTException:toString	()Ljava/lang/String;
        //   119: aastore
        //   120: invokeinterface 44 3 0
        //   125: getstatic 26	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:logger	Lcom/pnfsoftware/jeb/util/logging/ILogger;
        //   128: aload_1
        //   129: invokeinterface 43 2 0
        //   134: aload_0
        //   135: invokestatic 39	java/lang/System:currentTimeMillis	()J
        //   138: putfield 24	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execEndTs	J
        //   141: aload_0
        //   142: getfield 24	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execEndTs	J
        //   145: aload_0
        //   146: getfield 25	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execStartTs	J
        //   149: lsub
        //   150: lstore_1
        //   151: lload_1
        //   152: ldc2_w 19
        //   155: lcmp
        //   156: ifle +40 -> 196
        //   159: getstatic 26	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:logger	Lcom/pnfsoftware/jeb/util/logging/ILogger;
        //   162: ldc 3
        //   164: iconst_3
        //   165: anewarray 10	java/lang/Object
        //   168: dup
        //   169: iconst_0
        //   170: aload_0
        //   171: getfield 27	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:name	Ljava/lang/String;
        //   174: aastore
        //   175: dup
        //   176: iconst_1
        //   177: lload_1
        //   178: invokestatic 33	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   181: aastore
        //   182: dup
        //   183: iconst_2
        //   184: ldc2_w 19
        //   187: invokestatic 33	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   190: aastore
        //   191: invokeinterface 44 3 0
        //   196: goto +71 -> 267
        //   199: astore_3
        //   200: aload_0
        //   201: invokestatic 39	java/lang/System:currentTimeMillis	()J
        //   204: putfield 24	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execEndTs	J
        //   207: aload_0
        //   208: getfield 24	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execEndTs	J
        //   211: aload_0
        //   212: getfield 25	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:execStartTs	J
        //   215: lsub
        //   216: lstore 4
        //   218: lload 4
        //   220: ldc2_w 19
        //   223: lcmp
        //   224: ifle +41 -> 265
        //   227: getstatic 26	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:logger	Lcom/pnfsoftware/jeb/util/logging/ILogger;
        //   230: ldc 3
        //   232: iconst_3
        //   233: anewarray 10	java/lang/Object
        //   236: dup
        //   237: iconst_0
        //   238: aload_0
        //   239: getfield 27	com/pnfsoftware/jeb/rcpclient/extensions/UIRunnable:name	Ljava/lang/String;
        //   242: aastore
        //   243: dup
        //   244: iconst_1
        //   245: lload 4
        //   247: invokestatic 33	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   250: aastore
        //   251: dup
        //   252: iconst_2
        //   253: ldc2_w 19
        //   256: invokestatic 33	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   259: aastore
        //   260: invokeinterface 44 3 0
        //   265: aload_3
        //   266: athrow
        //   267: return
        // Line number table:
        //   Java source line #75	-> byte code offset #0
        //   Java source line #76	-> byte code offset #7
        //   Java source line #86	-> byte code offset #11
        //   Java source line #87	-> byte code offset #18
        //   Java source line #88	-> byte code offset #28
        //   Java source line #89	-> byte code offset #36
        //   Java source line #90	-> byte code offset #55
        //   Java source line #89	-> byte code offset #68
        //   Java source line #101	-> byte code offset #73
        //   Java source line #78	-> byte code offset #76
        //   Java source line #79	-> byte code offset #77
        //   Java source line #81	-> byte code offset #104
        //   Java source line #82	-> byte code offset #125
        //   Java source line #86	-> byte code offset #134
        //   Java source line #87	-> byte code offset #141
        //   Java source line #88	-> byte code offset #151
        //   Java source line #89	-> byte code offset #159
        //   Java source line #90	-> byte code offset #178
        //   Java source line #89	-> byte code offset #191
        //   Java source line #101	-> byte code offset #196
        //   Java source line #86	-> byte code offset #199
        //   Java source line #87	-> byte code offset #207
        //   Java source line #88	-> byte code offset #218
        //   Java source line #89	-> byte code offset #227
        //   Java source line #90	-> byte code offset #247
        //   Java source line #89	-> byte code offset #260
        //   Java source line #101	-> byte code offset #265
        //   Java source line #102	-> byte code offset #267
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	268	0	this	UIRunnable
        //   27	28	1	exectime	long
        //   76	53	1	e	org.eclipse.swt.SWTException
        //   150	28	1	exectime	long
        //   199	67	3	localObject	Object
        //   216	30	4	exectime	long
        // Exception table:
        //   from	to	target	type
        //   0	11	76	org/eclipse/swt/SWTException
        //   0	11	199	finally
        //   76	134	199	finally
    }

    public boolean isStarted() {
        return this.execStartTs != 0L;
    }

    public boolean isDone() {
        return this.execEndTs != 0L;
    }

    public long getCreatedTs() {
        return this.createdTs;
    }

    public long getExecStartTs() {
        return this.execStartTs;
    }

    public long getExecEndTs() {
        return this.execEndTs;
    }
}


