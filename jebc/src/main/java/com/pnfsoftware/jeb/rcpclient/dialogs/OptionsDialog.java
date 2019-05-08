package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientProperties;
import com.pnfsoftware.jeb.rcpclient.dialogs.options.OptionsChanges;
import com.pnfsoftware.jeb.rcpclient.dialogs.options.OptionsChanges.Changes;
import com.pnfsoftware.jeb.rcpclient.dialogs.options.OptionsSimpleViewDevelopment;
import com.pnfsoftware.jeb.rcpclient.dialogs.options.OptionsSimpleViewGeneral;
import com.pnfsoftware.jeb.rcpclient.dialogs.options.OptionsTreeView;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabFolderView;
import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabFolderView.Entry;
import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class OptionsDialog
        extends JebDialog {
    private static final String GENERAL = S.s(361);
    private static final String DEV = S.s(271);
    public static final String CLIENT = "Client";
    public static final String ENGINES = "Engines";
    public static final String PROJECT_SPECIFIC = "Project-specific";
    private static final String[] advancedTabs = {"Client", "Engines", "Project-specific"};
    private static final String[] simpleTabs = {GENERAL, DEV};
    private RcpClientProperties clientProperties;
    private IPropertyManager clientPM;
    private IPropertyManager corePM;
    private IPropertyManager prjPM;
    private boolean blockModeChange;
    private boolean lazyInit;
    private boolean expandOnFiltering;
    private OptionsChanges changes = new OptionsChanges();
    private boolean advancedOptions = false;
    private boolean saveChanges = false;
    private TabFolderView tabman;

    public OptionsDialog(Shell parent, RcpClientProperties clientProperties, IPropertyManager clientPM, IPropertyManager corePM, IPropertyManager prjPM) {
        super(parent, S.s(616), true, true);
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
        this.clientProperties = clientProperties;
        if ((clientPM == null) && (corePM == null) && (prjPM == null)) {
            throw new IllegalArgumentException("Please provide at least one property manager.");
        }
        this.clientPM = clientPM;
        this.corePM = corePM;
        this.prjPM = prjPM;
        boolean advanced = false;
        if (clientProperties != null) {
            advanced = clientProperties.getOptionsDialogAdvancedMode();
        }
        if ((clientPM == null) || (corePM == null)) {
            advanced = true;
            this.blockModeChange = true;
        }
        setMode(advanced);
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }

    public void setExpandOnFiltering(boolean expandOnFiltering) {
        this.expandOnFiltering = expandOnFiltering;
    }

    public boolean isExpandOnFiltering() {
        return this.expandOnFiltering;
    }

    public Boolean open() {
        super.open();
        if (!this.saveChanges) {
            return Boolean.valueOf(false);
        }
        OptionsChanges.Changes engChanges = this.changes.get("Engines");
        if (engChanges == null) {
            return Boolean.valueOf(false);
        }
        return Boolean.valueOf(engChanges.hasChanges());
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 4);
        this.shell.setMinimumSize(600, 500);
        this.tabman = new TabFolderView(parent, 2048, true, this.lazyInit);
        this.tabman.setLayoutData(UIUtil.createGridDataSpanHorizontally(4, true, true));
        CTabFolder folder = this.tabman.getContainer();
        if (this.clientPM != null) {
            this.changes.addPropertyManager("Client", this.clientPM);
            this.tabman.addEntry("Client", OptionsTreeView.build(folder, this.clientPM, this.changes.get("Client"), this.expandOnFiltering), this.advancedOptions);
        }
        if (this.corePM != null) {
            this.changes.addPropertyManager("Engines", this.corePM);
            this.tabman.addEntry("Engines", OptionsTreeView.build(folder, this.corePM, this.changes.get("Engines"), this.expandOnFiltering), this.advancedOptions);
        }
        if (this.prjPM != null) {
            this.changes.addPropertyManager("Project-specific", this.prjPM);
            this.tabman.addEntry("Project-specific", OptionsTreeView.build(folder, this.prjPM, this.changes.get("Project-specific"), this.expandOnFiltering), this.advancedOptions);
        }
        if ((this.clientPM != null) && (this.corePM != null)) {
            this.tabman.addEntry(GENERAL, new OptionsSimpleViewGeneral(folder, this.changes), !this.advancedOptions);
            this.tabman.addEntry(DEV, new OptionsSimpleViewDevelopment(folder, this.changes), !this.advancedOptions);
        }
        createButtons(parent, 288, 32);
        createButtons(parent, 0, new int[][]{{-5, 559}}, 0);
        if (this.blockModeChange) {
            getButtonByStyle(-5).setEnabled(false);
        }
        updateButtonText(this.advancedOptions);
        Button btnHelp = UIUtil.createPushbox(parent, S.s(365), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/manual");
            }
        });
        GridData data = new GridData();
        data.horizontalAlignment = 3;
        btnHelp.setLayoutData(data);
        for (TabFolderView.Entry e : this.tabman.getEntries()) {
            if (e.hasTab()) {
                this.tabman.showEntry(e, true);
                break;
            }
        }
    }

    protected void onButtonClick(int style) {
        if (style == -5) {
            setMode(!this.advancedOptions);
        } else {
            super.onButtonClick(style);
        }
    }

    protected void onConfirm() {
        this.changes.saveAllChanges(null);
        this.saveChanges = true;
        super.onConfirm();
    }

    private void setMode(boolean advancedOptions) {
        if (advancedOptions == this.advancedOptions) {
            return;
        }
        if (this.tabman != null) {
            if (advancedOptions) {
                for (String e : advancedTabs) {
                    this.tabman.showEntry(e, e.equals("Client"));
                }
                for (String e : simpleTabs) {
                    this.tabman.hideEntry(e);
                }
                this.tabman.hideEntry(GENERAL);
            } else {
                for (String e : advancedTabs) {
                    this.tabman.hideEntry(e);
                }
                for (String e : simpleTabs) {
                    this.tabman.showEntry(e, e.equals(GENERAL));
                }
            }
        }
        updateButtonText(advancedOptions);
        this.advancedOptions = advancedOptions;
        if (this.clientProperties != null) {
            this.clientProperties.setOptionsDialogAdvancedMode(advancedOptions);
        }
    }

    private void updateButtonText(boolean advancedOptions) {
        Button b = getButtonByStyle(-5);
        if (b != null) {
            b.setText(S.s(613) + " >>");
            b.getParent().pack();
        }
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\OptionsDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */