package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditOptionsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpAboutHandler;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class CocoaUIEnhancer {
    private static final long kAboutMenuItem = 0L;
    private static final long kPreferencesMenuItem = 2L;
    private static final long kQuitMenuItem = 10L;
    static long sel_toolbarButtonClicked_;
    static long sel_preferencesMenuItemSelected_;
    static long sel_aboutMenuItemSelected_;
    static Callback proc3Args;
    private final String appName;

    private static class MenuHookObject {
        final RcpClientContext context;

        public MenuHookObject(RcpClientContext context) {
            this.context = context;
        }

        public int actionProc(int id, int sel, int arg0) {
            return (int) actionProc(id, sel, arg0);
        }

        public long actionProc(long id, long sel, long arg0) {
            if (sel == CocoaUIEnhancer.sel_aboutMenuItemSelected_) {
                AllHandlers.getInstance().attemptExecution(HelpAboutHandler.class);
            } else if (sel == CocoaUIEnhancer.sel_preferencesMenuItemSelected_) {
                AllHandlers.getInstance().attemptExecution(EditOptionsHandler.class);
            }
            return 99L;
        }
    }

    public CocoaUIEnhancer(String appName) {
        this.appName = appName;
    }

    public void hookApplicationMenu(final RcpClientContext context) {
        Display display = context.getDisplay();
        MenuHookObject target = new MenuHookObject(context);
        try {
            initialize(target);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        if (!display.isDisposed()) {
            display.addListener(21, new Listener() {
                public void handleEvent(Event event) {
                    event.doit = context.attemptCloseOpenedProject(context.getMainShell());
                }
            });
        }
        display.disposeExec(new Runnable() {
            public void run() {
                CocoaUIEnhancer.this.invoke(CocoaUIEnhancer.proc3Args, "dispose");
            }
        });
    }

    private void initialize(Object callbackObject) throws Exception {
        Class<?> osCls = classForName("org.eclipse.swt.internal.cocoa.OS");
        if (sel_toolbarButtonClicked_ == 0L) {
            sel_preferencesMenuItemSelected_ = registerName(osCls, "preferencesMenuItemSelected:");
            sel_aboutMenuItemSelected_ = registerName(osCls, "aboutMenuItemSelected:");
        }
        proc3Args = new Callback(callbackObject, "actionProc", 3);
        Method getAddress = Callback.class.getMethod("getAddress", new Class[0]);
        Object object = getAddress.invoke(proc3Args, (Object[]) null);
        long proc3 = convertToLong(object);
        if (proc3 == 0L) {
            SWT.error(3);
        }
        Class<?> nsmenuCls = classForName("org.eclipse.swt.internal.cocoa.NSMenu");
        Class<?> nsmenuitemCls = classForName("org.eclipse.swt.internal.cocoa.NSMenuItem");
        Class<?> nsstringCls = classForName("org.eclipse.swt.internal.cocoa.NSString");
        Class<?> nsapplicationCls = classForName("org.eclipse.swt.internal.cocoa.NSApplication");
        object = invoke(osCls, "objc_lookUpClass", new Object[]{"SWTApplicationDelegate"});
        long cls = convertToLong(object);
        invoke(osCls, "class_addMethod", new Object[]{wrapPointer(cls), wrapPointer(sel_preferencesMenuItemSelected_), wrapPointer(proc3), "@:@"});
        invoke(osCls, "class_addMethod", new Object[]{wrapPointer(cls), wrapPointer(sel_aboutMenuItemSelected_), wrapPointer(proc3), "@:@"});
        Object sharedApplication = invoke(nsapplicationCls, "sharedApplication");
        Object mainMenu = invoke(sharedApplication, "mainMenu");
        Object mainMenuItem = invoke(nsmenuCls, mainMenu, "itemAtIndex", new Object[]{wrapPointer(0L)});
        Object appMenu = invoke(mainMenuItem, "submenu");
        Object aboutMenuItem = invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[]{wrapPointer(0L)});
        if (this.appName != null) {
            Object nsStr = invoke(nsstringCls, "stringWith", new Object[]{S.s(1) + " " + this.appName});
            invoke(nsmenuitemCls, aboutMenuItem, "setTitle", new Object[]{nsStr});
        }
        if (this.appName != null) {
            Object quitMenuItem = invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[]{wrapPointer(10L)});
            Object nsStr = invoke(nsstringCls, "stringWith", new Object[]{S.s(675) + " " + this.appName});
            invoke(nsmenuitemCls, quitMenuItem, "setTitle", new Object[]{nsStr});
        }
        Object prefMenuItem = invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[]{wrapPointer(2L)});
        invoke(nsmenuitemCls, prefMenuItem, "setEnabled", new Object[]{Boolean.valueOf(true)});
        invoke(nsmenuitemCls, prefMenuItem, "setAction", new Object[]{wrapPointer(sel_preferencesMenuItemSelected_)});
        invoke(nsmenuitemCls, aboutMenuItem, "setAction", new Object[]{wrapPointer(sel_aboutMenuItemSelected_)});
    }

    private long registerName(Class<?> osCls, String name) throws Exception {
        Object object = invoke(osCls, "sel_registerName", new Object[]{name});
        return convertToLong(object);
    }

    private long convertToLong(Object object) {
        if ((object instanceof Integer)) {
            Integer i = (Integer) object;
            return i.longValue();
        }
        if ((object instanceof Long)) {
            Long l = (Long) object;
            return l.longValue();
        }
        return 0L;
    }

    private static Object wrapPointer(long value) {
        Class<?> PTR_CLASS = C.PTR_SIZEOF == 8 ? Long.TYPE : Integer.TYPE;
        if (PTR_CLASS == Long.TYPE) {
            return new Long(value);
        }
        return new Integer((int) value);
    }

    private static Object invoke(Class<?> clazz, String methodName, Object[] args) {
        return invoke(clazz, null, methodName, args);
    }

    private static Object invoke(Class<?> clazz, Object target, String methodName, Object[] args) {
        try {
            Class<?>[] signature = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                Class<?> thisClass = args[i].getClass();
                if (thisClass == Integer.class) {
                    signature[i] = Integer.TYPE;
                } else if (thisClass == Long.class) {
                    signature[i] = Long.TYPE;
                } else if (thisClass == Byte.class) {
                    signature[i] = Byte.TYPE;
                } else if (thisClass == Boolean.class) {
                    signature[i] = Boolean.TYPE;
                } else signature[i] = thisClass;
            }
            Method method = clazz.getMethod(methodName, signature);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Class<?> classForName(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private Object invoke(Class<?> cls, String methodName) {
        return invoke(cls, methodName, (Class[]) null, (Object[]) null);
    }

    private Object invoke(Class<?> cls, String methodName, Class<?>[] paramTypes, Object... arguments) {
        try {
            Method m = cls.getDeclaredMethod(methodName, paramTypes);
            return m.invoke(null, arguments);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Object invoke(Object obj, String methodName) {
        return invoke(obj, methodName, (Class[]) null, (Object[]) null);
    }

    private Object invoke(Object obj, String methodName, Class<?>[] paramTypes, Object... arguments) {
        try {
            Method m = obj.getClass().getDeclaredMethod(methodName, paramTypes);
            return m.invoke(obj, arguments);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}


