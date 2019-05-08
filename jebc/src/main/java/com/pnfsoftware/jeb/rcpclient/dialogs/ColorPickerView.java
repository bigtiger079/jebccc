package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TypedListener;

class ColorPickerView
        extends Composite {
    private Label c;

    public ColorPickerView(Composite parent, String text, Color initial) {
        super(parent, 0);
        setLayout(new FillLayout());
        Label l = new Label(parent, 0);
        l.setText(text + ": ");
        this.c = new Label(parent, 2048);
        this.c.setText("            ");
        this.c.setBackground(initial);
        this.c.addMouseListener(new MouseListener() {
            public void mouseUp(MouseEvent e) {
                ColorDialog cdlg = new ColorDialog(ColorPickerView.this.getShell());
                cdlg.setRGB(ColorPickerView.this.c.getBackground().getRGB());
                RGB rgb = cdlg.open();
                if (rgb != null) {
                    ColorPickerView.this.c.setBackground(UIAssetManager.getInstance().getColor(rgb));
                    ColorPickerView.this.notifyListeners(13, new Event());
                }
            }

            public void mouseDown(MouseEvent e) {
            }

            public void mouseDoubleClick(MouseEvent e) {
            }
        });
    }

    public ColorPickerView(Composite parent, String text) {
        this(parent, text, null);
    }

    void addSelectionListener(SelectionListener listener) {
        addListener(13, new TypedListener(listener));
    }

    void removeSelectionListener(SelectionListener listener) {
        removeListener(13, listener);
    }

    public void setColor(Color color) {
        this.c.setBackground(color);
    }

    public Color getColor() {
        return this.c.getBackground();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\ColorPickerView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */