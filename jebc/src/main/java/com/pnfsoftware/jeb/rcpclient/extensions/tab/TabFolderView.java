package com.pnfsoftware.jeb.rcpclient.extensions.tab;

import com.pnfsoftware.jeb.rcpclient.parts.ILazyView;
import com.pnfsoftware.jeb.rcpclient.util.CTabFolderUtils;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

public class TabFolderView extends Composite implements CTabFolder2Listener {
    private static final ILogger logger = GlobalLog.getLogger(TabFolderView.class);
    private static final String LAZY_INITIALIZED = "initialized";
    private CTabFolder folder;
    private SelectionListener selectionListener;

    public static class Entry {
        String name;
        Control control;
        CTabItem tab;

        Entry(String name, Control control, CTabItem tab) {
            this.name = name;
            this.control = control;
            this.tab = tab;
        }

        public String getName() {
            return this.name;
        }

        public Control getControl() {
            return this.control;
        }

        public boolean hasTab() {
            return this.tab != null;
        }
    }

    private List<ITabFolderListener> listeners = new ArrayList<>();
    private List<Entry> entries = new ArrayList<>();
    private boolean addSpaces;
    private boolean lazyInit;
    private List<Entry> entriesByFocusOrder = new ArrayList<>();
    private boolean clearing;

    public TabFolderView(Composite parent, int flags, boolean addSpaces, boolean lazyInit) {
        super(parent, 0);
        setLayout(new FillLayout());
        this.addSpaces = addSpaces;
        this.folder = new CTabFolder(this, flags);
        CTabFolderUtils.setCTabFolderHeight(this.folder, -1);
        this.folder.addCTabFolder2Listener(this);
        this.folder.addSelectionListener(this.selectionListener = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (TabFolderView.this.clearing) {
                    return;
                }
                CTabItem selectedTab = TabFolderView.this.folder.getSelection();
                for (TabFolderView.Entry e : TabFolderView.this.entries) {
                    if (e.tab == selectedTab) {
                        Control control = selectedTab.getControl();
                        if (TabFolderView.this.isLazyInitializationToPerform(control)) {
                            ((ILazyView) control).lazyInitialization();
                            control.setData("initialized", Boolean.TRUE);
                        }
                        e.control.setFocus();
                        if (TabFolderView.this.entriesByFocusOrder.contains(e)) {
                            TabFolderView.this.entriesByFocusOrder.remove(e);
                        }
                        TabFolderView.this.entriesByFocusOrder.add(0, e);
                        break;
                    }
                }
            }
        });
        this.folder.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
            }

            public void focusGained(FocusEvent e) {
            }
        });
        this.lazyInit = lazyInit;
    }

    public void cleanUp() {
        this.folder.removeSelectionListener(this.selectionListener);
        this.entries.clear();
        this.listeners.clear();
    }

    public void addListener(ITabFolderListener l) {
        this.listeners.add(l);
    }

    public void removeListener(ITabFolderListener l) {
        this.listeners.remove(l);
    }

    public CTabFolder getContainer() {
        return this.folder;
    }

    public List<Control> getControls() {
        List<Control> r = new ArrayList<>();
        for (Entry entry : this.entries) {
            r.add(entry.control);
        }
        return r;
    }

    public int getEntryCount() {
        return this.entries.size();
    }

    public int getEntryVisibleCount() {
        int i = 0;
        for (Entry entry : this.entries) {
            if (entry.tab != null) {
                i++;
            }
        }
        return i;
    }

    private boolean isImmediateLazyInitialization(Control control) {
        return ((control instanceof ILazyView)) && (!this.lazyInit);
    }

    private boolean isLazyInitializationToPerform(Control control) {
        return ((control instanceof ILazyView)) && (this.lazyInit) && (control.getData("initialized") == null);
    }

    public boolean addEntry(String name, Control control) {
        return addEntry(name, control, null, true);
    }

    public boolean addEntry(String name, Control control, Integer index) {
        return addEntry(name, control, index, true);
    }

    public boolean addEntry(String name, Control control, boolean showTab) {
        return addEntry(name, control, null, showTab);
    }

    public boolean addEntry(String name, Control control, Integer index, boolean showTab) {
        if (control == null) {
            return false;
        }
        if (isImmediateLazyInitialization(control)) {
            ((ILazyView) control).lazyInitialization();
            control.setData("initialized", Boolean.TRUE);
        }
        CTabItem item = null;
        if (showTab) {
            item = createCTabItem(name, control, index);
        }
        Entry entry = new Entry(name, control, item);
        this.entries.add(entry);
        for (ITabFolderListener l : this.listeners) {
            l.tabAdded(new TabFolderEvent(this, name));
        }
        return true;
    }

    public boolean hideEntry(String name) {
        Entry entry = getEntry(name);
        if (entry == null) {
            return false;
        }
        if (entry.tab != null) {
            entry.tab.dispose();
            entry.tab = null;
        }
        return true;
    }

    public boolean removeEntry(String name) {
        Entry entry = getEntry(name);
        if (entry == null) {
            return false;
        }
        return removeEntry(entry);
    }

    public boolean removeEntry(Control ctl) {
        Entry entry = getEntry(ctl);
        if (entry == null) {
            return false;
        }
        return removeEntry(entry);
    }

    private boolean removeEntry(Entry entry) {
        if (entry.tab != null) {
            entry.tab.dispose();
            entry.tab = null;
            entry.control.dispose();
            entry.control = null;
        }
        this.entries.remove(entry);
        this.entriesByFocusOrder.remove(entry);
        for (ITabFolderListener l : this.listeners) {
            l.tabRemoved(new TabFolderEvent(this, entry.name));
        }
        return true;
    }

    public void removeAllEntries() {
        // Byte code:
        //   0: aload_0
        //   1: iconst_1
        //   2: putfield 32	com/pnfsoftware/jeb/rcpclient/extensions/tab/TabFolderView:clearing	Z
        //   5: aload_0
        //   6: getfield 33	com/pnfsoftware/jeb/rcpclient/extensions/tab/TabFolderView:entries	Ljava/util/List;
        //   9: invokeinterface 102 1 0
        //   14: ifne +27 -> 41
        //   17: aload_0
        //   18: aload_0
        //   19: getfield 33	com/pnfsoftware/jeb/rcpclient/extensions/tab/TabFolderView:entries	Ljava/util/List;
        //   22: iconst_0
        //   23: invokeinterface 101 2 0
        //   28: checkcast 9	com/pnfsoftware/jeb/rcpclient/extensions/tab/TabFolderView$Entry
        //   31: getfield 41	com/pnfsoftware/jeb/rcpclient/extensions/tab/TabFolderView$Entry:name	Ljava/lang/String;
        //   34: invokevirtual 53	com/pnfsoftware/jeb/rcpclient/extensions/tab/TabFolderView:removeEntry	(Ljava/lang/String;)Z
        //   37: pop
        //   38: goto -33 -> 5
        //   41: aload_0
        //   42: iconst_0
        //   43: putfield 32	com/pnfsoftware/jeb/rcpclient/extensions/tab/TabFolderView:clearing	Z
        //   46: goto +11 -> 57
        //   49: astore_1
        //   50: aload_0
        //   51: iconst_0
        //   52: putfield 32	com/pnfsoftware/jeb/rcpclient/extensions/tab/TabFolderView:clearing	Z
        //   55: aload_1
        //   56: athrow
        //   57: return
        // Line number table:
        //   Java source line #316	-> byte code offset #0
        //   Java source line #318	-> byte code offset #5
        //   Java source line #319	-> byte code offset #17
        //   Java source line #323	-> byte code offset #41
        //   Java source line #324	-> byte code offset #46
        //   Java source line #323	-> byte code offset #49
        //   Java source line #324	-> byte code offset #55
        //   Java source line #325	-> byte code offset #57
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	58	0	this	TabFolderView
        //   49	7	1	localObject	Object
        // Exception table:
        //   from	to	target	type
        //   5	41	49	finally
    }

    public boolean showEntry(int index, boolean setFocus) {
        if ((index < 0) || (index >= this.entries.size())) {
            return false;
        }
        Entry entry = (Entry) this.entries.get(index);
        showEntry(entry, setFocus);
        return true;
    }

    public boolean showEntry(Control control, boolean setFocus) {
        Entry entry = getEntry(control);
        if (entry == null) {
            return false;
        }
        showEntry(entry, setFocus);
        return true;
    }

    public boolean showEntry(String name, boolean setFocus) {
        Entry entry = getEntry(name);
        if (entry == null) {
            return false;
        }
        showEntry(entry, setFocus);
        return true;
    }

    public void showEntry(Entry entry, boolean setFocus) {
        if (entry.tab == null) {
            CTabItem item = createCTabItem(entry.name, entry.control, null);
            entry.tab = item;
        }
        if (setFocus) {
            this.folder.setSelection(entry.tab);
            this.folder.notifyListeners(13, new Event());
            entry.tab.getControl().setFocus();
        }
    }

    private CTabItem createCTabItem(String name, Control control, Integer index) {
        if (control.getParent() != this.folder) {
            control.setParent(this.folder);
        }
        if (index == null) {
            index = Integer.valueOf(this.folder.getItemCount());
        }
        if (index.intValue() < 0) {
            index = Integer.valueOf(index.intValue() + this.folder.getItemCount());
        }
        CTabItem item = new CTabItem(this.folder, 0, index.intValue());
        String displayName = name;
        if (this.addSpaces) {
            displayName = "   " + name + "   ";
        }
        item.setText(displayName);
        item.setControl(control);
        return item;
    }

    public Control getCurrentEntryControl() {
        for (Entry entry : this.entries) {
            if (this.folder.getSelection() == entry.tab) {
                return entry.control;
            }
        }
        return null;
    }

    public String getCurrentEntryName() {
        for (Entry entry : this.entries) {
            if (this.folder.getSelection() == entry.tab) {
                return entry.name;
            }
        }
        return null;
    }

    public Control getEntryControl(int index) {
        return ((Entry) this.entries.get(index)).control;
    }

    public String getEntryName(int index) {
        return ((Entry) this.entries.get(index)).name;
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(this.entries);
    }

    private Entry getEntry(String name) {
        for (Entry entry : this.entries) {
            if (entry.name.equals(name)) {
                return entry;
            }
        }
        return null;
    }

    private Entry getEntry(Control control) {
        for (Entry entry : this.entries) {
            if (entry.control == control) {
                return entry;
            }
        }
        return null;
    }

    public CTabItem getTab(Control control) {
        for (CTabItem item : this.folder.getItems()) {
            if (item.getControl() == control) {
                return item;
            }
        }
        return null;
    }

    public CTabItem getMostRecentlyFocusedTab() {
        return this.entriesByFocusOrder.isEmpty() ? null : ((Entry) this.entriesByFocusOrder.get(0)).tab;
    }

    public List<CTabItem> getPreviouslyFocusedTabs() {
        List<CTabItem> li = new ArrayList<>(this.entriesByFocusOrder.size());
        for (Entry e : this.entriesByFocusOrder) {
            li.add(e.tab);
        }
        return li;
    }

    public void close(CTabFolderEvent e) {
        CTabItem tab = (CTabItem) e.item;
        for (Entry entry : this.entries) {
            if (entry.tab == tab) {
                entry.tab = null;
                break;
            }
        }
    }

    public void maximize(CTabFolderEvent e) {
    }

    public void minimize(CTabFolderEvent e) {
    }

    public void restore(CTabFolderEvent e) {
    }

    public void showList(CTabFolderEvent e) {
    }
}


