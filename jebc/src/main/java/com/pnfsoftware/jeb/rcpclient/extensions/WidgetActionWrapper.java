package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.rcpclient.operations.ContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.IContextMenu;
import com.pnfsoftware.jeb.rcpclient.operations.JebAction;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;

public class WidgetActionWrapper implements IContextMenu {
    private Control ctl;

    private static class ViewerKeyAdapter extends KeyAdapter {
        private int fKeyCode;
        private Action fAction;
        private int fStateMask;

        public ViewerKeyAdapter(int keyCode, int stateMask, Action action) {
            this.fKeyCode = keyCode;
            this.fStateMask = stateMask;
            this.fAction = action;
        }

        public void keyPressed(KeyEvent e) {
            if ((((e.stateMask & this.fStateMask) != 0) || (this.fStateMask == 0)) && (e.keyCode == this.fKeyCode) && (this.fAction.isEnabled())) {
                this.fAction.run();
                e.doit = false;
            }
        }
    }

    private List<Action> menuActions = new ArrayList<>();

    public WidgetActionWrapper(Control ctl) {
        this.ctl = ctl;
        new ContextMenu(ctl).addContextMenu(this);
    }

    public void registerAction(JebAction action) {
        if (action.isContextual()) {
            this.menuActions.add(action);
        }
        if (action.getKeyCode() != 0) {
            this.ctl.addKeyListener(new ViewerKeyAdapter(action.getKeyCode(), action.getKeyModifier(), action));
        }
    }

    public void fillContextMenu(IMenuManager menuMgr) {
        for (Action action : this.menuActions) {
            action.setEnabled(action.isEnabled());
            menuMgr.add(action);
        }
    }
}


