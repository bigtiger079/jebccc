/*     */
package com.pnfsoftware.jeb.rcpclient.handlers._test;
/*     */
/*     */

import com.pnfsoftware.jeb.rcpclient.util.SwtUtil;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Dialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class TestInputDialog
        /*     */ extends Dialog
        /*     */ {
    /*     */ Double value;

    /*     */
    /*     */
    public TestInputDialog(Shell parent)
    /*     */ {
        /*  33 */
        super(parent);
        /*     */
    }

    /*     */
    /*     */
    public TestInputDialog(Shell parent, int style) {
        /*  37 */
        super(parent, style);
        /*     */
    }

    /*     */
    /*     */
    public Double open() {
        /*  41 */
        Shell parent = getParent();
        /*  42 */
        final Shell shell = new Shell(parent, 67616);
        /*  43 */
        shell.setText("NumberInputDialog");
        /*     */
        /*  45 */
        shell.setLayout(new GridLayout(2, true));
        /*     */
        /*  47 */
        Label label = new Label(shell, 0);
        /*  48 */
        label.setText("Please enter a valid number:");
        /*     */
        /*  50 */
        final Text text = new Text(shell, 2052);
        /*     */
        /*  52 */
        final Button buttonOK = new Button(shell, 8);
        /*  53 */
        buttonOK.setText("Ok");
        /*  54 */
        buttonOK.setLayoutData(new GridData(128));
        /*  55 */
        Button buttonCancel = new Button(shell, 8);
        /*  56 */
        buttonCancel.setText("Cancel");
        /*     */
        /*  58 */
        text.addListener(24, new Listener()
                /*     */ {
            /*     */
            public void handleEvent(Event event) {
                /*     */
                try {
                    /*  62 */
                    TestInputDialog.this.value = Double.valueOf(text.getText());
                    /*  63 */
                    buttonOK.setEnabled(true);
                    /*     */
                }
                /*     */ catch (Exception e) {
                    /*  66 */
                    buttonOK.setEnabled(false);
                    /*     */
                }
                /*     */
                /*     */
            }
            /*  70 */
        });
        /*  71 */
        buttonOK.addListener(13, new Listener()
                /*     */ {
            /*     */
            public void handleEvent(Event event) {
                /*  74 */
                shell.dispose();
                /*     */
            }
            /*     */
            /*  77 */
        });
        /*  78 */
        buttonCancel.addListener(13, new Listener()
                /*     */ {
            /*     */
            public void handleEvent(Event event) {
                /*  81 */
                TestInputDialog.this.value = null;
                /*  82 */
                shell.dispose();
                /*     */
            }
            /*     */
            /*  85 */
        });
        /*  86 */
        shell.addListener(31, new Listener()
                /*     */ {
            /*     */
            public void handleEvent(Event event) {
                /*  89 */
                if (event.detail == 2) {
                    /*  90 */
                    event.doit = false;
                    /*     */
                }
                /*     */
            }
            /*  93 */
        });
        /*  94 */
        text.setText("");
        /*  95 */
        shell.pack();
        /*  96 */
        shell.open();
        /*     */
        /*  98 */
        Display display = parent.getDisplay();
        /*  99 */
        while (!shell.isDisposed()) {
            /* 100 */
            if (!display.readAndDispatch()) {
                /* 101 */
                SwtUtil.sleep(display);
                /*     */
            }
            /*     */
        }
        /* 104 */
        return this.value;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\handlers\_test\TestInputDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */