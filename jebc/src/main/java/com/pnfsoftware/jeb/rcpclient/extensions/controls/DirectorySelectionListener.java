
package com.pnfsoftware.jeb.rcpclient.extensions.controls;


import com.pnfsoftware.jeb.client.S;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class DirectorySelectionListener
        extends SelectionAdapter {
    Shell parent;
    Text text;


    public DirectorySelectionListener(Shell parent, Text text) {

        this.parent = parent;

        this.text = text;

    }


    public void widgetSelected(SelectionEvent e) {

        DirectoryDialog dirdlg = new DirectoryDialog(this.parent, 4096);

        dirdlg.setMessage(S.s(277));


        String dirname = getDefaultText();

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

