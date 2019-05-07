package com.pnfsoftware.jeb.rcpclient.handlers._test;

import com.pnfsoftware.jeb.rcpclient.util.SwtUtil;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TestInputDialog
        extends Dialog {
    Double value;


    public TestInputDialog(Shell parent) {

        super(parent);

    }


    public TestInputDialog(Shell parent, int style) {

        super(parent, style);

    }


    public Double open() {

        Shell parent = getParent();

        final Shell shell = new Shell(parent, 67616);

        shell.setText("NumberInputDialog");


        shell.setLayout(new GridLayout(2, true));


        Label label = new Label(shell, 0);

        label.setText("Please enter a valid number:");


        final Text text = new Text(shell, 2052);


        final Button buttonOK = new Button(shell, 8);

        buttonOK.setText("Ok");

        buttonOK.setLayoutData(new GridData(128));

        Button buttonCancel = new Button(shell, 8);

        buttonCancel.setText("Cancel");


        text.addListener(24, new Listener() {

            public void handleEvent(Event event) {

                try {

                    TestInputDialog.this.value = Double.valueOf(text.getText());

                    buttonOK.setEnabled(true);

                } catch (Exception e) {

                    buttonOK.setEnabled(false);

                }


            }

        });

        buttonOK.addListener(13, new Listener() {

            public void handleEvent(Event event) {

                shell.dispose();

            }


        });

        buttonCancel.addListener(13, new Listener() {

            public void handleEvent(Event event) {

                TestInputDialog.this.value = null;

                shell.dispose();

            }


        });

        shell.addListener(31, new Listener() {

            public void handleEvent(Event event) {

                if (event.detail == 2) {

                    event.doit = false;

                }

            }

        });

        text.setText("");

        shell.pack();

        shell.open();


        Display display = parent.getDisplay();

        while (!shell.isDisposed()) {

            if (!display.readAndDispatch()) {

                SwtUtil.sleep(display);

            }

        }

        return this.value;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\_test\TestInputDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */