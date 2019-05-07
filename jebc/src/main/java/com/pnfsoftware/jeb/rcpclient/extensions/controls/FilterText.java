
package com.pnfsoftware.jeb.rcpclient.extensions.controls;


import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
import com.pnfsoftware.jeb.rcpclient.iviewers.StyleManager;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;


public class FilterText
        extends Composite
        implements IFilterText {
    private Boolean status;
    private Text filterText;
    private Color fgColor;
    private Color bgColor;


    public FilterText(Composite parent) {

        super(parent, 0);

        setLayout(new FillLayout());


        this.filterText = new Text(this, 2948);

        this.filterText.setMessage(S.s(344));


        this.filterText.addFocusListener(new FocusAdapter() {

            public void focusGained(FocusEvent e) {

                FilterText.this.setStatus(FilterText.this.status);

            }

        });

    }


    public Control getTextWidget() {

        return this.filterText;

    }


    public void setText(String s) {

        this.filterText.setText(s);

    }


    public void submitText(String s) {

        setText(s);

        Event e = new Event();

        e.character = '\r';

        this.filterText.notifyListeners(1, e);

    }


    public String getText() {

        return this.filterText.getText();

    }


    public void selectAll() {

        this.filterText.selectAll();

    }


    public void addFocusListener(FocusListener listener) {

        this.filterText.addFocusListener(listener);

    }


    public void removeFocusListener(FocusListener listener) {

        this.filterText.removeFocusListener(listener);

    }


    public void addKeyListener(KeyListener listener) {

        this.filterText.addKeyListener(listener);

    }


    public void removeKeyListener(KeyListener listener) {

        this.filterText.removeKeyListener(listener);

    }


    public void setStatus(Boolean status) {

        this.status = status;

        if (status == null) {

            this.filterText.setBackground(this.bgColor);

            this.filterText.setForeground(this.fgColor);

        } else {

            StyleManager styleman = RcpClientContext.getInstance().getStyleManager();

            ItemClassIdentifiers styleid = status.booleanValue() ? ItemClassIdentifiers.RESULT_SUCCESS : ItemClassIdentifiers.RESULT_ERROR;

            Style style = styleman.getNormalStyle(styleid);

            this.filterText.setBackground(style.getBackgroungColor() == null ? this.bgColor : style.getBackgroungColor());

            this.filterText.setForeground(style.getColor() == null ? this.fgColor : style.getColor());

        }

    }


    public void setBackground(Color color) {

        this.filterText.setBackground(color);

        this.bgColor = color;

    }


    public void setForeground(Color color) {

        this.filterText.setForeground(color);

        this.fgColor = color;

    }


    public Color getBackground() {

        return this.filterText.getBackground();

    }


    public boolean setFocus() {

        return this.filterText.setFocus();

    }


    public static boolean isSelected() {

        Control c = Display.getCurrent().getFocusControl();

        if ((c != null) && ((c.getParent() instanceof FilterText))) {

            return true;

        }

        return false;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\controls\FilterText.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */