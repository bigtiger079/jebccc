
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.client.S;

import java.io.File;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FileSelectionListener
        extends SelectionAdapter {
    Shell parent;
    Text text;
    private String[] extensions;

    public FileSelectionListener(Shell parent, Text text, String[] extensions) {
        this.parent = parent;
        this.text = text;
        this.extensions = extensions;
    }

    public void widgetSelected(SelectionEvent e) {
        FileDialog dirdlg = new FileDialog(this.parent, 4096);
        dirdlg.setText(S.s(341));
        dirdlg.setFilterExtensions(this.extensions);
        String dirname = getDefaultText();
        if (dirname != null) {
            File dir = new File(dirname);
            if (dir.isAbsolute()) {
            }
        }
        if (dirname != null) {
            dirdlg.setFilterPath(dirname);
        }
        dirname = dirdlg.open();
        if (dirname != null) {
            setText(dirname);
        }
    }

    public String getDefaultText() {
        return this.text.getText();
    }

    public void setText(String dirname) {
        this.text.setText(dirname);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\controls\FileSelectionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */