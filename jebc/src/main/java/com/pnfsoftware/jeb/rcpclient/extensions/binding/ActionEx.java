package com.pnfsoftware.jeb.rcpclient.extensions.binding;

import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

public abstract class ActionEx extends Action implements Runnable {
    private static KeyShortcutsManager ksm;
    boolean hasCustomShortcut;

    public static void setKeyboardShortcuts(KeyShortcutsManager manager) {
        ksm = manager;
    }

    public static KeyShortcutsManager getKeyShortcutManager() {
        return ksm;
    }

    List<Integer> acclist = new ArrayList<>();
    String text;
    String text0;

    public ActionEx(String id, String text) {
        this(id, text, 0);
    }

    public ActionEx(String id, String text, int style) {
        this(id, text, style, new int[0]);
    }

    public ActionEx(String id, String text, int style, int... accelerators) {
        super(text, style);
        this.text0 = text;
        setText(text);
        if (!Strings.isBlank(id)) {
            setId(id);
        }
        int keycode;
        if (ksm != null) {
            keycode = ksm.getShortcutKeycode(getId());
            if ((keycode != 0) && (addExtraAccelerator(keycode))) {
                this.hasCustomShortcut = true;
            }
        }
        for (int accelerator : accelerators) {
            setAccelerator(accelerator);
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setAccelerator(int keycode) {
        if (!this.hasCustomShortcut) {
            addExtraAccelerator(keycode);
        }
    }

    public int getAccelerator() {
        return 0;
    }

    public boolean addExtraAccelerator(int keycode) {
        if ((ksm != null) && (ksm.isReserved(keycode)) && (!Strings.equals(getId(), ksm.getActionIdForKeycode(keycode)))) {
            return false;
        }
        int mod = keycode & SWT.MODIFIER_MASK;
        int key = keycode & (~SWT.MODIFIER_MASK);
        keycode = mod | Character.toLowerCase(key);
        if (this.acclist.contains(keycode)) {
            return false;
        }
        if (this.acclist.isEmpty()) {
            String s = KeyStroke.getInstance(mod, Character.toUpperCase(key)).toString();
            setText(this.text0 + "\t" + refineAcceleratorString(s));
        }
        this.acclist.add(keycode);
        return true;
    }

    private String refineAcceleratorString(String s) {
        s = s.replace("CR", "ENTER");
        StringBuilder sb = new StringBuilder();
        boolean upper = true;
        for (int i = 0; i < s.length(); i++) {
            Character c = s.charAt(i);
            if (c == '_') {
                c = ' ';
                upper = true;
            } else if (c == '+') {
                c = null;
                upper = true;
            } else if (upper) {
                c = Character.toUpperCase(c);
                upper = false;
            } else {
                c = Character.toLowerCase(c);
            }
            if (c != null) {
                sb.append(c);
            }
        }
        s = sb.toString().replace("Enter", "↩").replace("Tab", "↹").replace("Space", "␣").replace("Del", "⌫").replace("Command", "⌘").replace("Ctrl", "⌃").replace("Control", "⌃").replace("Alt", "⌥").replace("Shift", "⇧").replace("Arrow Left", "←").replace("Arrow Up", "↑").replace("Arrow Right", "→").replace("Arrow Down", "↓");
        return s;
    }

    public List<Integer> getExtraAccelerators() {
        return this.acclist;
    }

    public boolean canExecute() {
        return isEnabled();
    }

    public void execute() {
        run();
    }

    public boolean checkExecutionContext(Control ctl) {
        return (isAllowedForControl(ctl)) && (!isForbiddenForControl(ctl));
    }

    protected boolean isAllowedForControl(Control ctl) {
        return true;
    }

    protected boolean isForbiddenForControl(Control ctl) {
        return false;
    }
}


