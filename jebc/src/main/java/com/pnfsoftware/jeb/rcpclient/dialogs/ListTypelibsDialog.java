package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.code.asm.type.TypeLibraryEntry;
import com.pnfsoftware.jeb.core.units.code.asm.type.TypeLibraryMetadata;
import com.pnfsoftware.jeb.core.units.code.asm.type.TypeLibraryService;
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
import com.pnfsoftware.jeb.util.format.Strings;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ListTypelibsDialog extends JebDialog {
    private IGraphicalTaskExecutor executor;
    private TypeLibraryService tls;
    private FilteredTableViewer ftv;

    public ListTypelibsDialog(Shell parent, IGraphicalTaskExecutor executor) {
        super(parent, "Type libraries", true, true, "typelibsDialog");
        setVisualBounds(-1, 90, -1, -1);
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
        this.executor = executor;
    }

    public void setInput(TypeLibraryService tls) {
        this.tls = tls;
    }

    public Object open() {
        return super.open();
    }

    protected void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent);
        FilteredTableView ft = new FilteredTableView(parent, 32, new String[]{"Loaded", "Targets", "Group", S.s(591), S.s(268), S.s(86), S.s(341)});
        GridData data = UIUtil.createGridDataFill(true, true);
        data.minimumHeight = 200;
        ft.setLayoutData(data);
        this.ftv = new FilteredTableViewer(ft);
        ContentProviderListener p = new ContentProviderListener();
        this.ftv.setContentProvider(p);
        this.ftv.setCheckStateProvider(new DefaultCheckStateProvider(p));
        this.ftv.setLabelProvider(new DefaultCellLabelProvider(p));
        ft.addTableEventListener(p);
        this.ftv.setInput(this.tls);
        createOkayButton(parent);
        if (getStandardWidgetManager() != null) getStandardWidgetManager().wrapWidget(ft, "listTypelibs");
    }

    class ContentProviderListener implements ITableEventListener, IFilteredTableContentProvider {
        private TypeLibraryService tls0;

        ContentProviderListener() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.tls0 = ((TypeLibraryService) newInput);
        }

        public void dispose() {
        }

        public Object[] getElements(Object inputElement) {
            List<TypeLibraryEntry> r = this.tls0.getAvailables();
            TypeLibraryEntry[] a = (TypeLibraryEntry[]) r.toArray(new TypeLibraryEntry[0]);
            Arrays.sort(a);
            return a;
        }

        public Object[] getRowElements(Object row) {
            if ((row instanceof TypeLibraryEntry)) {
                TypeLibraryEntry e = (TypeLibraryEntry) row;
                String loaded = e.getTypelib() != null ? S.s(828) : S.s(594);
                TypeLibraryMetadata hdr = e.getMetadataHeader();
                int groupId = hdr.getGroupId();
                String proctypes = Strings.join(", ", hdr.getProcessorTypes());
                String groupName = TypeLibraryService.groupIdToName(groupId);
                String name = hdr.getName();
                String description = hdr.getDescription();
                String author = hdr.getAuthor();
                String filepath = e.getFile().getPath();
                return new Object[]{loaded, proctypes, groupName, name, description, author, filepath};
            }
            return ArrayUtil.NO_OBJECT;
        }

        public boolean isChecked(Object row) {
            if ((row instanceof TypeLibraryEntry)) {
                TypeLibraryEntry e = (TypeLibraryEntry) row;
                return e.getTypelib() != null;
            }
            return false;
        }

        public void onTableEvent(Object row, boolean isSelected, boolean isChecked) {
            if ((row instanceof TypeLibraryEntry)) {
                final TypeLibraryEntry e = (TypeLibraryEntry) row;
                boolean isLoaded = e.getTypelib() != null;
                if ((isChecked) && (!isLoaded)) {
                    if (ListTypelibsDialog.this.executor == null) {
                        this.tls0.load(e);
                    } else {
                        ListTypelibsDialog.this.executor.executeTask("Loading type library...", new Runnable() {
                            public void run() {
                                ListTypelibsDialog.ContentProviderListener.this.tls0.load(e);
                            }
                        });
                    }
                } else if ((!isChecked) && (isLoaded)) {
                    UI.error("Type libraries cannot be unloaded.");
                }
                ListTypelibsDialog.this.ftv.refresh();
            }
        }
    }
}


