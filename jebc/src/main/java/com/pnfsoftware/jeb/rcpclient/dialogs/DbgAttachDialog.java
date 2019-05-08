package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerMachineInformation;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerProcessInformation;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerTargetEnumerator;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnitIdentifier;
import com.pnfsoftware.jeb.core.units.code.debug.impl.DebuggerSetupInformation;
import com.pnfsoftware.jeb.rcpclient.IGraphicalTaskExecutor;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DataFrameView;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame.Row;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class DbgAttachDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(DbgAttachDialog.class);
    private IGraphicalTaskExecutor taskExecutor;
    private IDebuggerUnit dbg;
    private IUnit target;

    public static class DbgAttachInfo {
        public DebuggerSetupInformation info;

        DbgAttachInfo(DebuggerSetupInformation info, IDebuggerUnitIdentifier ident) {
            this.info = info;
            this.ident = ident;
        }

        public String toString() {
            return String.format("info=%s,ident=%s", new Object[]{this.info, this.ident});
        }

        public IDebuggerUnitIdentifier ident;
    }

    private List<IDebuggerUnitIdentifier> idents = new ArrayList();
    private List<IDebuggerUnitIdentifier> identifiers = new ArrayList();
    private List<IDebuggerMachineInformation> machines = new ArrayList();
    private DataFrame dfMachines;
    private DataFrameView dfvMachines;
    private List<? extends IDebuggerProcessInformation> processes = new ArrayList();
    private DataFrame dfProcesses;
    private DataFrameView dfvProcesses;
    private Button btnRemoteTarget;
    private Text widgetHostname;
    private Text widgetPort;
    private Button btnSuspendThreads;
    private Button btnUseChildren;
    private Button btnRefresh;
    private DbgAttachInfo result;

    public DbgAttachDialog(Shell parent, RcpClientContext context, IDebuggerUnit optionalDebuggerUnit, IUnit optionalTargetUnit) {
        super(parent, S.s(234), true, true);
        this.scrolledContainer = true;
        setVisualBounds(30, -1, -1, 70);
        this.taskExecutor = context;
        this.dbg = optionalDebuggerUnit;
        if ((this.dbg == null) && (context != null)) {
            this.idents = context.getEnginesContext().getDebuggerUnitIdentifiers();
        }
        this.target = optionalTargetUnit;
    }

    void updateMachinesList() {
        Runnable r = new Runnable() {
            public void run() {
                DbgAttachDialog.this.identifiers.clear();
                DbgAttachDialog.this.machines.clear();
                IDebuggerTargetEnumerator ta;
                IDebuggerUnitIdentifier ident;
                if (DbgAttachDialog.this.dbg != null) {
                    ta = DbgAttachDialog.this.dbg.getTargetEnumerator();
                    if (ta != null) {
                        for (IDebuggerMachineInformation machine : ta.listMachines()) {
                            DbgAttachDialog.this.identifiers.add(null);
                            DbgAttachDialog.this.machines.add(machine);
                        }
                    }
                } else {
                    Iterator<IDebuggerUnitIdentifier> iterator = DbgAttachDialog.this.idents.iterator();
                    while (iterator.hasNext()) {
                        ident = iterator.next();
                        ta = ident.getTargetEnumerator();
                        if (ta != null) {
                            for (IDebuggerMachineInformation machine : ta.listMachines()) {
                                DbgAttachDialog.this.identifiers.add(ident);
                                DbgAttachDialog.this.machines.add(machine);
                            }
                        }
                    }
//                    for (ta = DbgAttachDialog.this.idents.iterator(); ta.hasNext(); ) {
//                        ident = (IDebuggerUnitIdentifier) ta.next();
//                        ta = ident.getTargetEnumerator();
//                        if (ta != null)
//                            for (IDebuggerMachineInformation machine : ta.listMachines()) {
//                                DbgAttachDialog.this.identifiers.add(ident);
//                                DbgAttachDialog.this.machines.add(machine);
//                            }
//                    }
                }
            }
        };
        if (this.taskExecutor != null) {
            this.taskExecutor.executeTaskWithPopupDelay(500, "Please wait while debugger information is being gathered...", false, r);
        } else {
            BusyIndicator.showWhile(this.shell.getDisplay(), r);
        }
    }

    void updateProcessesList(final IDebuggerMachineInformation machine) {
        Runnable r = new Runnable() {
            public void run() {
                DbgAttachDialog.this.processes = machine.getProcesses();
            }
        };
        if (this.taskExecutor != null) {
            this.taskExecutor.executeTaskWithPopupDelay(500, "Please wait while processes information is being gathered...", false, r);
        } else {
            BusyIndicator.showWhile(this.shell.getDisplay(), r);
        }
    }

    void prepareMachineList() {
        if (this.dfMachines == null) {
            this.dfMachines = new DataFrame(new String[]{S.s(591), S.s(447), S.s(351), S.s(387)});
        } else {
            this.dfMachines.clear();
        }
        updateMachinesList();
        for (IDebuggerMachineInformation machine : this.machines) {
            int flags = machine.getFlags();
            String ff = "";
            if ((flags & 0x1) != 0) {
                ff = ff + "Online";
            } else {
                ff = ff + "Offline";
            }
            this.dfMachines.addRow(new Object[]{machine.getName(), machine.getLocation(), ff, machine.getInformation()});
        }
    }

    String prepareProcessList(IDebuggerMachineInformation machine) {
        if (this.dfProcesses == null) {
            this.dfProcesses = new DataFrame(new String[]{S.s(376), S.s(591), S.s(351)});
        } else {
            this.dfProcesses.clear();
        }
        StringBuilder suggestedFilter = new StringBuilder();
        if (machine != null) {
            updateProcessesList(machine);
            for (IDebuggerProcessInformation process : this.processes) {
                int flags = process.getFlags();
                String ff = "";
                if ((flags & 0x1) != 0) {
                    ff = ff + "D";
                }
                this.dfProcesses.addRow(new Object[]{Long.valueOf(process.getId()), process.getName(), ff});
            }
        }
        IUnit unit = this.target;
        while (unit != null) {
            if (!Strings.isBlank(unit.getName())) {
                if (suggestedFilter.length() > 0) {
                    suggestedFilter.append("|");
                }
                suggestedFilter.append(unit.getName());
            }
            if (!(unit.getParent() instanceof IUnit)) {
                break;
            }
            unit = (IUnit) unit.getParent();
        }
        return suggestedFilter.toString();
    }

    void refreshMachineList() {
        int index = this.dfvMachines.getSelectedRow();
        prepareMachineList();
        this.dfvMachines.refresh();
        if ((index < this.dfvMachines.getItemCount()) && (index >= 0)) {
            this.dfvMachines.setSelection(index);
        } else if (this.dfvMachines.getItemCount() > 0) {
            this.dfvMachines.setSelection(0);
        }
    }

    void refreshProcessList() {
        int index = this.dfvMachines.getSelectedRow();
        String suggFilter = prepareProcessList((index < 0) || (index >= this.machines.size()) ? null : (IDebuggerMachineInformation) this.machines.get(index));
        this.dfvProcesses.refresh();
        tryAutoSelectProcess(suggFilter);
    }

    public DbgAttachInfo open() {
        super.open();
        return this.result;
    }

    protected void createContents(Composite parent) {
        this.shell.setMinimumSize(600, 450);
        int autoInitDone = 0;
        UIUtil.setStandardLayout(parent, 3);
        Composite c = new Composite(parent, 0);
        c.setLayoutData(UIUtil.createGridDataSpanHorizontally(3, true, true));
        c.setLayout(new GridLayout(1, false));
        prepareMachineList();
        IDebuggerMachineInformation machine = this.machines.isEmpty() ? null : (IDebuggerMachineInformation) this.machines.get(0);
        String suggestedFilter = prepareProcessList(machine);
        Group g0 = new Group(c, 0);
        g0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        g0.setLayout(new GridLayout(1, false));
        g0.setText(String.format("%s / %s", new Object[]{S.s(449), S.s(273)}));
        this.dfvMachines = new DataFrameView(g0, this.dfMachines, true);
        this.dfvMachines.addExtraEntriesToContextMenu();
        this.dfvMachines.setLayoutData(UIUtil.createGridDataFillHorizontally());
        if (machine != null) {
            this.dfvMachines.setSelection(0);
            autoInitDone++;
        }
        Group g1 = new Group(c, 0);
        g1.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, true));
        g1.setLayout(new GridLayout(1, false));
        g1.setText(S.s(663));
        this.dfvProcesses = new DataFrameView(g1, this.dfProcesses, true);
        this.dfvProcesses.addExtraEntriesToContextMenu();
        if (tryAutoSelectProcess(suggestedFilter)) {
            autoInitDone++;
        }
        this.dfvProcesses.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, true));
        this.dfvProcesses.refresh();
        this.dfvMachines.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = DbgAttachDialog.this.dfvMachines.getSelectedRow();
                DbgAttachDialog.logger.i("Selected: %d", new Object[]{Integer.valueOf(index)});
                DbgAttachDialog.this.refreshProcessList();
            }
        });
        this.dfvProcesses.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                if (DbgAttachDialog.this.tryAttach()) {
                    DbgAttachDialog.this.shell.close();
                }
            }
        });
        Group g2 = new Group(parent, 0);
        g2.setLayoutData(UIUtil.createGridDataSpanHorizontally(3, true, false));
        g2.setLayout(new GridLayout(2, false));
        g2.setText("Remote Debugging");
        this.btnRemoteTarget = UIUtil.createCheckbox(g2, "Debug a remote target", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean isRemote = DbgAttachDialog.this.btnRemoteTarget.getSelection();
                DbgAttachDialog.this.dfvMachines.setEnabled(!isRemote);
                DbgAttachDialog.this.dfvProcesses.setEnabled(!isRemote);
                DbgAttachDialog.this.widgetHostname.setEnabled(isRemote);
                DbgAttachDialog.this.widgetPort.setEnabled(isRemote);
                DbgAttachDialog.this.btnRefresh.setEnabled(!isRemote);
            }
        });
        this.btnRemoteTarget.setLayoutData(UIUtil.createGridDataSpanHorizontally(2));
        new Label(g2, 0).setText(S.s(670) + ": ");
        this.widgetHostname = new Text(g2, 2052);
        this.widgetHostname.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetHostname.setEnabled(false);
        new Label(g2, 0).setText(S.s(671) + ": ");
        this.widgetPort = new Text(g2, 2052);
        this.widgetPort.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.widgetPort.setEnabled(false);
        Group g3 = new Group(parent, 0);
        g3.setLayoutData(UIUtil.createGridDataSpanHorizontally(3, true, false));
        g3.setLayout(new GridLayout(1, false));
        g3.setText("Options");
        this.btnSuspendThreads = UIUtil.createCheckbox(g3, S.s(760), null);
        this.btnSuspendThreads.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, false));
        this.btnUseChildren = UIUtil.createCheckbox(g3, "Allow children debuggers (for Android apps, provide Native debugging)", null);
        this.btnUseChildren.setLayoutData(UIUtil.createGridDataSpanHorizontally(1, true, false));
        this.btnUseChildren.setToolTipText("Example: when debugging an Android app with native code, the native code debugger will be a child of the bytecode debugger");
        Button btnAttach = UIUtil.createPushbox(parent, S.s(83), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (DbgAttachDialog.this.tryAttach()) {
                    DbgAttachDialog.this.shell.close();
                }
            }
        });
        UIUtil.createPushbox(parent, S.s(201), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                DbgAttachDialog.this.shell.close();
            }
        });
        this.btnRefresh = UIUtil.createPushbox(parent, "Refresh Machines List", new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                DbgAttachDialog.this.refreshMachineList();
                DbgAttachDialog.this.refreshProcessList();
            }
        });
        if (autoInitDone == 2) {
            btnAttach.setFocus();
        }
        this.shell.setDefaultButton(btnAttach);
    }

    private boolean tryAutoSelectProcess(String suggestedFilter) {
        if (!Strings.isBlank(suggestedFilter)) {
            this.dfvProcesses.forceFilter(suggestedFilter);
            int candidate = 0;
            TableItem[] items = this.dfvProcesses.getTable().getItems();
            for (int i = 0; i < items.length; i++) {
                DataFrame.Row df = (DataFrame.Row) items[i].getData();
                boolean debuggable = ((String) df.elements.get(2)).contains("D");
                if (debuggable) {
                    if (candidate == 0) {
                        this.dfvProcesses.setSelection(i);
                    }
                    candidate++;
                }
            }
            if (candidate == 1) {
                return true;
            }
        }
        return false;
    }

    private boolean tryAttach() {
        this.result = null;
        boolean isRemote = this.btnRemoteTarget.getSelection();
        if (!isRemote) {
            int index = this.dfvMachines.getSelectedRow();
            if ((index >= 0) && (index < this.machines.size())) {
                IDebuggerMachineInformation machine = (IDebuggerMachineInformation) this.machines.get(index);
                IDebuggerUnitIdentifier ident = (IDebuggerUnitIdentifier) this.identifiers.get(index);
                index = this.dfvProcesses.getSelectedRow();
                if ((index >= 0) && (index < this.processes.size())) {
                    IDebuggerProcessInformation process = (IDebuggerProcessInformation) this.processes.get(index);
                    this.result = new DbgAttachInfo(DebuggerSetupInformation.create(machine, process), ident);
                }
            }
        } else {
            String hostname = this.widgetHostname.getText();
            if (hostname.isEmpty()) {
                hostname = "localhost";
                this.widgetHostname.setText(hostname);
            }
            int port = Conversion.stringToInt(this.widgetPort.getText());
            if (port != 0) {
                this.result = new DbgAttachInfo(DebuggerSetupInformation.create(hostname, port), null);
            }
        }
        if (this.result == null) {
            MessageDialog.openWarning(this.shell, S.s(405), S.s(406));
            return false;
        }
        this.result.info.setSuspendThreads(this.btnSuspendThreads.getSelection());
        this.result.info.setUseChildrenDebuggers(this.btnUseChildren.getSelection());
        return true;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\DbgAttachDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */