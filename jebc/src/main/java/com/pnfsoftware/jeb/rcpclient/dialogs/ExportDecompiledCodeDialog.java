package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.DirectorySelectorView;
import com.pnfsoftware.jeb.util.format.Strings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ExportDecompiledCodeDialog
        extends JebDialog {
    private Text text;
    private ExportStatus exportStatus;

    public static enum State {
        ALL,
        FILTER,
        DECOMPILED,
        CURRENT;

        private State() {
        }
    }

    private State exportState = State.ALL;
    private String initialFilter;
    private String initialDirectory;
    private DirectorySelectorView folderText;
    private String outputDirectory;
    private Button mergeAll;
    private boolean merge;
    private Text fileText;
    private String outputFile;

    public static class ExportStatus {
        private ExportDecompiledCodeDialog.State state;
        private String filter;

        public ExportStatus(ExportDecompiledCodeDialog.State state, String filter) {
            this.state = state;
            this.filter = filter;
        }

        public ExportDecompiledCodeDialog.State getState() {
            return this.state;
        }

        public String getFilter() {
            return this.filter;
        }
    }

    public ExportDecompiledCodeDialog(Shell parent) {
        super(parent, S.s(327), true, true);
        this.scrolledContainer = true;
    }

    public void setInitialState(State state) {
        this.exportState = state;
    }

    public void setInitialState(ExportStatus state) {
        setInitialState(state.state);
        setInitialFilter(state.filter);
    }

    public State getState() {
        return this.exportState;
    }

    public void setInitialFilter(String initialFilter) {
        this.initialFilter = initialFilter;
    }

    public void setInitialDirectory(String initialDirectory) {
        this.initialDirectory = initialDirectory;
    }

    public String getOutputDirectory() {
        return this.outputDirectory;
    }

    public boolean isMergeFiles() {
        return this.merge;
    }

    public String getOutputFile() {
        return this.outputFile;
    }

    public ExportStatus open() {
        super.open();
        return this.exportStatus;
    }

    private class ExportSelectionListener implements SelectionListener {
        private ExportDecompiledCodeDialog.State state;

        public ExportSelectionListener(ExportDecompiledCodeDialog.State state) {
            this.state = state;
        }

        public void widgetSelected(SelectionEvent e) {
            //TODO state.ordinal ???
            switch (this.state.ordinal()) {
                case 1:
                case 2:
                case 3:
                    ExportDecompiledCodeDialog.this.text.setEnabled(false);
                    break;
                case 4:
                    ExportDecompiledCodeDialog.this.text.setEnabled(true);
                    break;
            }
            ExportDecompiledCodeDialog.this.exportState = this.state;
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }

    public void createContents(Composite parent) {
        UIUtil.setStandardLayout(parent, 1);
        Group c0 = new Group(parent, 0);
        c0.setText(S.s(324));
        GridData exportGridData = UIUtil.createGridDataFillHorizontally();
        exportGridData.horizontalSpan = 2;
        c0.setLayoutData(exportGridData);
        c0.setLayout(new GridLayout(1, false));
        Button all = new Button(c0, 16);
        all.setText(S.s(54));
        all.addSelectionListener(new ExportSelectionListener(State.ALL));
        Button filter = new Button(c0, 16);
        filter.setText(S.s(343));
        filter.addSelectionListener(new ExportSelectionListener(State.FILTER));
        this.text = new Text(c0, 2048);
        this.text.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.text.setMessage(S.s(343));
        Button decomp = new Button(c0, 16);
        decomp.setText(S.s(607));
        decomp.addSelectionListener(new ExportSelectionListener(State.DECOMPILED));
        Button current = new Button(c0, 16);
        current.setText(S.s(228));
        current.addSelectionListener(new ExportSelectionListener(State.CURRENT));
        Text currentUnitText = new Text(c0, 2048);
        currentUnitText.setLayoutData(UIUtil.createGridDataFillHorizontally());
        currentUnitText.setEnabled(false);
        switch (this.exportState) {
            case ALL:
                all.setSelection(true);
                all.setFocus();
                this.text.setEnabled(false);
                current.setEnabled(false);
                break;
            case FILTER:
                filter.setSelection(true);
                filter.setFocus();
                current.setEnabled(false);
                break;
            case DECOMPILED:
                decomp.setSelection(true);
                decomp.setFocus();
                this.text.setEnabled(false);
                current.setEnabled(false);
                break;
            case CURRENT:
                current.setSelection(true);
                current.setFocus();
                this.text.setEnabled(false);
                if (this.initialFilter != null) {
                    currentUnitText.setText(this.initialFilter);
                }
                break;
        }
        Group c1 = new Group(parent, 0);
        c1.setText("Destination");
        c1.setLayoutData(UIUtil.createGridDataSpanHorizontally(2, true, false));
        c1.setLayout(new GridLayout(1, false));
        this.folderText = new DirectorySelectorView(c1, S.s(269) + ": ", this.initialDirectory);
        this.folderText.setLayoutData(UIUtil.createGridDataFillHorizontally());
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        this.folderText.setLayoutData(gd);
        this.mergeAll = new Button(this.folderText, 32);
        this.mergeAll.setText("Merge to one file: ");
        this.mergeAll.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (ExportDecompiledCodeDialog.this.mergeAll.getSelection()) {
                    ExportDecompiledCodeDialog.this.fileText.setEnabled(true);
                } else {
                    ExportDecompiledCodeDialog.this.fileText.setEnabled(false);
                }
            }
        });
        this.fileText = new Text(this.folderText, 2048);
        this.fileText.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.fileText.setMessage("File name");
        this.fileText.setEnabled(false);
        if (this.initialFilter != null) {
            this.text.setText(this.initialFilter);
        }
        Composite buttons = createButtons(parent, 288, 32);
        GridData gdButtons = new GridData();
        gdButtons.verticalIndent = 20;
        buttons.setLayoutData(gdButtons);
    }

    protected void onConfirm() {
        if (Strings.isBlank(this.folderText.getText())) {
            MessageDialog.openError(this.shell, "Empty directory", "Destination directory can not be empty. Please select one.");
        } else if ((this.mergeAll.getSelection()) && (Strings.isBlank(this.fileText.getText()))) {
            MessageDialog.openError(this.shell, "Empty file name", "File name can not be empty. Please enter one.");
        } else {
            this.exportStatus = new ExportStatus(this.exportState, this.text.getText());
            this.outputDirectory = this.folderText.getText();
            this.merge = this.mergeAll.getSelection();
            this.outputFile = this.fileText.getText();
            super.onConfirm();
        }
    }

    protected void onCancel() {
        this.exportStatus = null;
        super.onCancel();
    }
}


