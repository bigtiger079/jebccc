/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.client.S;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditOptionsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpAboutHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.internal.C;
/*     */ import org.eclipse.swt.internal.Callback;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;

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
/*     */
/*     */
/*     */
/*     */
/*     */ public class CocoaUIEnhancer
        /*     */ {
    /*     */   private static final long kAboutMenuItem = 0L;
    /*     */   private static final long kPreferencesMenuItem = 2L;
    /*     */   private static final long kQuitMenuItem = 10L;
    /*     */   static long sel_toolbarButtonClicked_;
    /*     */   static long sel_preferencesMenuItemSelected_;
    /*     */   static long sel_aboutMenuItemSelected_;
    /*     */   static Callback proc3Args;
    /*     */   private final String appName;

    /*     */
    /*     */   private static class MenuHookObject
            /*     */ {
        /*     */     final RcpClientContext context;

        /*     */
        /*     */
        public MenuHookObject(RcpClientContext context)
        /*     */ {
            /*  70 */
            this.context = context;
            /*     */
        }

        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        public int actionProc(int id, int sel, int arg0)
        /*     */ {
            /*  78 */
            return (int) actionProc(id, sel, arg0);
            /*     */
        }

        /*     */
        /*     */
        /*     */
        /*     */
        public long actionProc(long id, long sel, long arg0)
        /*     */ {
            /*  85 */
            if (sel == CocoaUIEnhancer.sel_aboutMenuItemSelected_) {
                /*  86 */
                AllHandlers.getInstance().attemptExecution(HelpAboutHandler.class);
                /*     */
            }
            /*  88 */
            else if (sel == CocoaUIEnhancer.sel_preferencesMenuItemSelected_) {
                /*  89 */
                AllHandlers.getInstance().attemptExecution(EditOptionsHandler.class);
                /*     */
            }
            /*     */
            /*     */
            /*     */
            /*     */
            /*  95 */
            return 99L;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public CocoaUIEnhancer(String appName)
    /*     */ {
        /* 107 */
        this.appName = appName;
        /*     */
    }

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
    public void hookApplicationMenu(final RcpClientContext context)
    /*     */ {
        /* 120 */
        Display display = context.getDisplay();
        /*     */
        /*     */
        /*     */
        /* 124 */
        MenuHookObject target = new MenuHookObject(context);
        /*     */
        /*     */
        try
            /*     */ {
            /* 128 */
            initialize(target);
            /*     */
        }
        /*     */ catch (Exception e) {
            /* 131 */
            throw new IllegalStateException(e);
            /*     */
        }
        /*     */
        /*     */
        /* 135 */
        if (!display.isDisposed()) {
            /* 136 */
            display.addListener(21, new Listener()
                    /*     */ {
                /*     */
                public void handleEvent(Event event) {
                    /* 139 */
                    event.doit = context.attemptCloseOpenedProject(context.getMainShell());
                    /*     */
                }
                /*     */
            });
            /*     */
        }
        /*     */
        /*     */
        /* 145 */
        display.disposeExec(new Runnable() {
            /*     */
            public void run() {
                /* 147 */
                CocoaUIEnhancer.this.invoke(CocoaUIEnhancer.proc3Args, "dispose");
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    private void initialize(Object callbackObject) throws Exception
    /*     */ {
        /* 154 */
        Class<?> osCls = classForName("org.eclipse.swt.internal.cocoa.OS");
        /*     */
        /*     */
        /* 157 */
        if (sel_toolbarButtonClicked_ == 0L)
            /*     */ {
            /* 159 */
            sel_preferencesMenuItemSelected_ = registerName(osCls, "preferencesMenuItemSelected:");
            /* 160 */
            sel_aboutMenuItemSelected_ = registerName(osCls, "aboutMenuItemSelected:");
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /* 165 */
        proc3Args = new Callback(callbackObject, "actionProc", 3);
        /* 166 */
        Method getAddress = Callback.class.getMethod("getAddress", new Class[0]);
        /* 167 */
        Object object = getAddress.invoke(proc3Args, (Object[]) null);
        /* 168 */
        long proc3 = convertToLong(object);
        /* 169 */
        if (proc3 == 0L) {
            /* 170 */
            SWT.error(3);
            /*     */
        }
        /*     */
        /* 173 */
        Class<?> nsmenuCls = classForName("org.eclipse.swt.internal.cocoa.NSMenu");
        /* 174 */
        Class<?> nsmenuitemCls = classForName("org.eclipse.swt.internal.cocoa.NSMenuItem");
        /* 175 */
        Class<?> nsstringCls = classForName("org.eclipse.swt.internal.cocoa.NSString");
        /* 176 */
        Class<?> nsapplicationCls = classForName("org.eclipse.swt.internal.cocoa.NSApplication");
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 183 */
        object = invoke(osCls, "objc_lookUpClass", new Object[]{"SWTApplicationDelegate"});
        /* 184 */
        long cls = convertToLong(object);
        /*     */
        /*     */
        /* 187 */
        invoke(osCls, "class_addMethod", new Object[]{wrapPointer(cls), wrapPointer(sel_preferencesMenuItemSelected_),
/* 188 */       wrapPointer(proc3), "@:@"});
        /* 189 */
        invoke(osCls, "class_addMethod", new Object[]{
/* 190 */       wrapPointer(cls), wrapPointer(sel_aboutMenuItemSelected_), wrapPointer(proc3), "@:@"});
        /*     */
        /*     */
        /* 193 */
        Object sharedApplication = invoke(nsapplicationCls, "sharedApplication");
        /* 194 */
        Object mainMenu = invoke(sharedApplication, "mainMenu");
        /* 195 */
        Object mainMenuItem = invoke(nsmenuCls, mainMenu, "itemAtIndex", new Object[]{wrapPointer(0L)});
        /* 196 */
        Object appMenu = invoke(mainMenuItem, "submenu");
        /*     */
        /*     */
        /* 199 */
        Object aboutMenuItem = invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[]{wrapPointer(0L)});
        /* 200 */
        if (this.appName != null) {
            /* 201 */
            Object nsStr = invoke(nsstringCls, "stringWith", new Object[]{S.s(1) + " " + this.appName});
            /* 202 */
            invoke(nsmenuitemCls, aboutMenuItem, "setTitle", new Object[]{nsStr});
            /*     */
        }
        /*     */
        /* 205 */
        if (this.appName != null) {
            /* 206 */
            Object quitMenuItem = invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[]{wrapPointer(10L)});
            /* 207 */
            Object nsStr = invoke(nsstringCls, "stringWith", new Object[]{S.s(675) + " " + this.appName});
            /* 208 */
            invoke(nsmenuitemCls, quitMenuItem, "setTitle", new Object[]{nsStr});
            /*     */
        }
        /*     */
        /*     */
        /* 212 */
        Object prefMenuItem = invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[]{
/* 213 */       wrapPointer(2L)});
        /* 214 */
        invoke(nsmenuitemCls, prefMenuItem, "setEnabled", new Object[]{Boolean.valueOf(true)});
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 221 */
        invoke(nsmenuitemCls, prefMenuItem, "setAction", new Object[]{wrapPointer(sel_preferencesMenuItemSelected_)});
        /* 222 */
        invoke(nsmenuitemCls, aboutMenuItem, "setAction", new Object[]{wrapPointer(sel_aboutMenuItemSelected_)});
        /*     */
    }

    /*     */
    /*     */
    private long registerName(Class<?> osCls, String name) throws Exception {
        /* 226 */
        Object object = invoke(osCls, "sel_registerName", new Object[]{name});
        /* 227 */
        return convertToLong(object);
        /*     */
    }

    /*     */
    /*     */
    private long convertToLong(Object object) {
        /* 231 */
        if ((object instanceof Integer)) {
            /* 232 */
            Integer i = (Integer) object;
            /* 233 */
            return i.longValue();
            /*     */
        }
        /* 235 */
        if ((object instanceof Long)) {
            /* 236 */
            Long l = (Long) object;
            /* 237 */
            return l.longValue();
            /*     */
        }
        /* 239 */
        return 0L;
        /*     */
    }

    /*     */
    /*     */
    private static Object wrapPointer(long value) {
        /* 243 */
        Class<?> PTR_CLASS = C.PTR_SIZEOF == 8 ? Long.TYPE : Integer.TYPE;
        /* 244 */
        if (PTR_CLASS == Long.TYPE) {
            /* 245 */
            return new Long(value);
            /*     */
        }
        /*     */
        /* 248 */
        return new Integer((int) value);
        /*     */
    }

    /*     */
    /*     */
    private static Object invoke(Class<?> clazz, String methodName, Object[] args)
    /*     */ {
        /* 253 */
        return invoke(clazz, null, methodName, args);
        /*     */
    }

    /*     */
    /*     */
    private static Object invoke(Class<?> clazz, Object target, String methodName, Object[] args) {
        /*     */
        try {
            /* 258 */
            Class<?>[] signature = new Class[args.length];
            /* 259 */
            for (int i = 0; i < args.length; i++) {
                /* 260 */
                Class<?> thisClass = args[i].getClass();
                /* 261 */
                if (thisClass == Integer.class) {
                    /* 262 */
                    signature[i] = Integer.TYPE;
                    /* 263 */
                } else if (thisClass == Long.class) {
                    /* 264 */
                    signature[i] = Long.TYPE;
                    /* 265 */
                } else if (thisClass == Byte.class) {
                    /* 266 */
                    signature[i] = Byte.TYPE;
                    /* 267 */
                } else if (thisClass == Boolean.class) {
                    /* 268 */
                    signature[i] = Boolean.TYPE;
                    /*     */
                } else
                    /* 270 */           signature[i] = thisClass;
                /*     */
            }
            /* 272 */
            Method method = clazz.getMethod(methodName, signature);
            /* 273 */
            return method.invoke(target, args);
            /*     */
        }
        /*     */ catch (Exception e) {
            /* 276 */
            throw new IllegalStateException(e);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private Class<?> classForName(String classname) {
        /*     */
        try {
            /* 282 */
            return Class.forName(classname);
            /*     */
        }
        /*     */ catch (ClassNotFoundException e)
            /*     */ {
            /* 286 */
            throw new IllegalStateException(e);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private Object invoke(Class<?> cls, String methodName) {
        /* 291 */
        return invoke(cls, methodName, (Class[]) null, (Object[]) null);
        /*     */
    }

    /*     */
    /*     */
    private Object invoke(Class<?> cls, String methodName, Class<?>[] paramTypes, Object... arguments) {
        /*     */
        try {
            /* 296 */
            Method m = cls.getDeclaredMethod(methodName, paramTypes);
            /* 297 */
            return m.invoke(null, arguments);
            /*     */
        }
        /*     */ catch (Exception e) {
            /* 300 */
            throw new IllegalStateException(e);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private Object invoke(Object obj, String methodName) {
        /* 305 */
        return invoke(obj, methodName, (Class[]) null, (Object[]) null);
        /*     */
    }

    /*     */
    /*     */
    private Object invoke(Object obj, String methodName, Class<?>[] paramTypes, Object... arguments) {
        /*     */
        try {
            /* 310 */
            Method m = obj.getClass().getDeclaredMethod(methodName, paramTypes);
            /* 311 */
            return m.invoke(obj, arguments);
            /*     */
        }
        /*     */ catch (Exception e) {
            /* 314 */
            throw new IllegalStateException(e);
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\CocoaUIEnhancer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */