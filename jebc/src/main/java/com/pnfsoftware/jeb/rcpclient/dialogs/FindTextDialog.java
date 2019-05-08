package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.HistoryAssistedTextField;
import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
import com.pnfsoftware.jeb.rcpclient.extensions.search.IFindTextImpl;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.util.collect.WeakIdentityHashMap;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FindTextDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(FindTextDialog.class);
    private static WeakIdentityHashMap<Object, FindTextDialog> usermap = new WeakIdentityHashMap();
    private GraphicalTextFinder<?> finder;

    public static FindTextDialog getInstance(Object owner) {
        return (FindTextDialog) usermap.get(owner);
    }

    private IFindTextImpl<?> findimpl;
    private TextHistory textHistory;
    private HistoryAssistedTextField searchField;
    private Button btn_case_sensitive;
    private Button btn_regex;
    private Button btn_reverse;
    private Button btn_wraparound;
    private boolean validRegex;
    private boolean wantsRegex;

    public FindTextDialog(Shell parent, GraphicalTextFinder<?> finder, TextHistory textHistory) {
        this(parent, finder, textHistory, true, null, null);
    }

    public FindTextDialog(Shell parent, GraphicalTextFinder<?> finder, TextHistory textHistory, boolean modal, Control owner, String ownerName) {
        super(parent, generateTitle(modal, ownerName), true, modal);
        this.scrolledContainer = true;
        if (finder == null) {
            throw new NullPointerException();
        }
        this.finder = finder;
        this.findimpl = finder.getFindTextImpl();
        this.textHistory = textHistory;
        if ((!modal) && (owner != null)) {
            owner.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent e) {
                    if (!FindTextDialog.this.shell.isDisposed()) {
                        FindTextDialog.this.shell.dispose();
                    }
                }
            });
        }
    }

    private static String generateTitle(boolean modal, String ownerName) {
        String title = S.s(345);
        if ((!modal) && (ownerName != null)) {
            title = title + " (" + Strings.truncateWithSuffix(ownerName, 24, "...") + ")";
        }
        return title;
    }

    private void setOwnerObject(Object owner) {
        usermap.put(owner, this);
    }

    private void unsetOwnerObject(Object owner) {
        usermap.remove(owner);
    }

    private Object getOwnerObject() {
        for (Object o : usermap.keySet()) {
            if (usermap.get(o) == this) {
                return o;
            }
        }
        return null;
    }

    public Object open(Object owner) {
        try {
            setOwnerObject(owner);
            return open();
        } finally {
            unsetOwnerObject(owner);
        }
    }

    public Object open() {
        super.open();
        return null;
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        Group areaSearch = new Group(parent, 0);
        areaSearch.setText("Search string");
        areaSearch.setLayoutData(new GridData(4, 128, true, false));
        areaSearch.setLayout(new GridLayout(1, false));
        Group areaOptions = new Group(parent, 0);
        areaOptions.setText(S.s(616));
        areaOptions.setLayoutData(new GridData(4, 128, true, false));
        areaOptions.setLayout(new GridLayout(2, false));
        Composite areaAction = new Composite(parent, 0);
        areaAction.setLayout(new RowLayout(256));
        FindTextOptions opt = this.findimpl.getFindTextOptions(true);
        this.searchField = new HistoryAssistedTextField(areaSearch, S.s(345) + ":", this.textHistory, true);
        GridData data = new GridData();
        data.horizontalAlignment = 4;
        data.grabExcessHorizontalSpace = true;
        this.searchField.setLayoutData(data);
        this.searchField.setText(opt.getSearchString());
        this.searchField.selectAll();
        this.btn_case_sensitive = new Button(areaOptions, 32);
        this.btn_case_sensitive.setText(S.s(109));
        this.btn_case_sensitive.setSelection(opt.isCaseSensitive());
        this.btn_regex = new Button(areaOptions, 32);
        this.btn_regex.setText(S.s(676));
        this.btn_regex.setSelection(opt.isRegularExpression());
        this.btn_wraparound = new Button(areaOptions, 32);
        this.btn_wraparound.setText(S.s(826));
        this.btn_wraparound.setSelection(opt.isWrapAround());
        this.btn_reverse = new Button(areaOptions, 32);
        this.btn_reverse.setText(S.s(684));
        this.btn_reverse.setSelection(opt.isReverseSearch());
        this.btn_reverse.setEnabled(this.findimpl.supportReverseSearch());
        UIUtil.createPushbox(areaAction, S.s(345), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                FindTextDialog.this.search();
            }
        });
        UIUtil.createPushbox(areaAction, S.s(201), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                FindTextDialog.this.shell.close();
            }
        });
        this.btn_regex.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FindTextDialog.this.wantsRegex = FindTextDialog.this.btn_regex.getSelection();
            }
        });
        this.searchField.getWidget().addKeyListener(this.kl);
        this.btn_case_sensitive.addKeyListener(this.kl);
        this.btn_regex.addKeyListener(this.kl);
        this.btn_reverse.addKeyListener(this.kl);
        this.btn_wraparound.addKeyListener(this.kl);
        this.searchField.getWidget().addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                FindTextDialog.this.validateRegex();
            }
        });
        validateRegex();
    }

    private KeyAdapter kl = new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
            if (e.character == '\r') {
                FindTextDialog.this.search();
            } else if ((e.stateMask == SWT.MOD1) && (e.keyCode == 102)) {
                Object o = FindTextDialog.this.getOwnerObject();
                if ((o instanceof Control)) {
                    ((Control) o).setFocus();
                }
            }
        }
    };

    private void search() {
        String value = this.searchField.getText();
        if (value.length() <= 0) {
            return;
        }
        this.searchField.confirm();
        FindTextOptions opt = this.findimpl.getFindTextOptions(true);
        opt.setSearchString(value);
        opt.setCaseSensitive(this.btn_case_sensitive.getSelection());
        opt.setRegularExpression(this.btn_regex.getSelection());
        opt.setWrapAround(this.btn_wraparound.getSelection());
        opt.setReverseSearch(this.btn_reverse.getSelection());
        this.finder.search(opt);
        getShell().setFocus();
    }

    private void validateRegex() {
        try {
            Pattern.compile(this.searchField.getText());
            this.validRegex = true;
        } catch (IllegalArgumentException e) {
            this.validRegex = false;
        }
        if (!this.validRegex) {
            this.btn_regex.setSelection(false);
            this.btn_regex.setEnabled(false);
        } else {
            this.btn_regex.setEnabled(true);
            this.btn_regex.setSelection(this.wantsRegex);
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\FindTextDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */