package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignatureDBManager;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageEntry;
import com.pnfsoftware.jeb.core.units.code.asm.sig.NativeSignaturePackageMetadata;
import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
import com.pnfsoftware.jeb.rcpclient.IGraphicalTaskExecutor;
import com.pnfsoftware.jeb.rcpclient.IWidgetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilteredTableView;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.ITableEventListener;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCellLabelProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.DefaultCheckStateProvider;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.FilteredTableViewer;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTableContentProvider;
import com.pnfsoftware.jeb.util.collect.ArrayUtil;

import java.io.File;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ListSiglibsDialog
        extends JebDialog {
    private IGraphicalTaskExecutor executor;
    private NativeSignatureDBManager dbman;
    private FilteredTableViewer ftv;

    public ListSiglibsDialog(Shell parent, IGraphicalTaskExecutor executor) {
        super(parent, "Signature libraries", true, true, "siglibsDialog");
        setVisualBounds(-1, 90, -1, -1);
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
        this.executor = executor;
    }

    public void setInput(NativeSignatureDBManager dbman) {
        this.dbman = dbman;
    }

    public Object open() {
        return super.open();
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        FilteredTableView ft = new FilteredTableView(parent, 32, new String[]{"Loaded", "Processor", S.s(591), S.s(268), S.s(86), S.s(341)});
        GridData data = UIUtil.createGridDataFill(true, true);
        data.minimumHeight = 200;
        ft.setLayoutData(data);
        this.ftv = new FilteredTableViewer(ft);
        ContentProviderListener p = new ContentProviderListener();
        this.ftv.setContentProvider(p);
        this.ftv.setCheckStateProvider(new DefaultCheckStateProvider(p));
        this.ftv.setLabelProvider(new DefaultCellLabelProvider(p));
        ft.addTableEventListener(p);
        this.ftv.setInput(this.dbman);
        createOkayButton(parent);
        if (getStandardWidgetManager() != null)
            getStandardWidgetManager().wrapWidget(ft, "listSiglibs");
    }

    class ContentProviderListener implements ITableEventListener, IFilteredTableContentProvider {
        private NativeSignatureDBManager dbman0;

        ContentProviderListener() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.dbman0 = ((NativeSignatureDBManager) newInput);
        }

        public void dispose() {
        }

        public Object[] getElements(Object inputElement) {
            return this.dbman0.getAvailablePackages().toArray();
        }

        public Object[] getRowElements(Object row) {
            if ((row instanceof NativeSignaturePackageEntry)) {
                NativeSignaturePackageEntry e = (NativeSignaturePackageEntry) row;
                String loaded = e.isLoadedInMemory() ? S.s(828) : S.s(594);
                NativeSignaturePackageMetadata hdr = e.getMetadata();
                String processorType = hdr.getTargetProcessorType().toString();
                String name = hdr.getName();
                String description = hdr.getDescription();
                String author = hdr.getAuthor();
                String filepath = e.getFile().getPath();
                return new Object[]{loaded, processorType, name, description, author, filepath};
            }
            return ArrayUtil.NO_OBJECT;
        }

        public boolean isChecked(Object row) {
            if ((row instanceof NativeSignaturePackageEntry)) {
                NativeSignaturePackageEntry e = (NativeSignaturePackageEntry) row;
                return e.isLoadedInMemory();
            }
            return false;
        }

        public void onTableEvent(Object row, boolean isSelected, boolean isChecked) {
            if ((row instanceof NativeSignaturePackageEntry)) {
                final NativeSignaturePackageEntry e = (NativeSignaturePackageEntry) row;
                boolean isLoaded = e.isLoadedInMemory();
                if ((isChecked) && (!isLoaded)) {
                    if (ListSiglibsDialog.this.executor == null) {
                        this.dbman0.loadPackage(e, true);
                    } else {
                        ListSiglibsDialog.this.executor.executeTask("Loading signature library...", new Runnable() {
                            public void run() {
                                if (!ListSiglibsDialog.ContentProviderListener.this.dbman0.loadPackage(e, true)) {
                                    UI.error("Package could not be loaded, no suitable analyzed files were found.");
                                }
                            }
                        });
                    }
                } else if ((!isChecked) && (isLoaded)) {
                    UI.error("Signature libraries cannot be unloaded.");
                }
                ListSiglibsDialog.this.ftv.refresh();
            }
        }
    }
}


