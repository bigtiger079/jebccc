
package com.pnfsoftware.jeb.rcpclient.extensions.app;


import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMElement;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMFolder;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPartManager;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


public class Part
        implements IMPart {
    private static final ILogger logger = GlobalLog.getLogger(Part.class);

    private static int internalPartCreationCount = 0;

    int internalPartId;

    String elementId;

    Folder defaultOwner;

    Folder owner;
    CTabItem tab;
    private IMPartManager manager;
    int state;
    private Map<String, Object> data;
    private boolean closeOnHide;
    private Composite cControl;
    private String cLabel;
    private String cTooltip;
    private Image cIcon;
    private Font cFont;
    private boolean cCloseable;


    public Part(Folder owner) {

        this.internalPartId = (internalPartCreationCount++);

        this.owner = owner;

        this.defaultOwner = owner;

        createContainerWidget();

    }


    public Part(CTabItem tab) {

        this.internalPartId = (internalPartCreationCount++);

        setTab(tab);

        this.defaultOwner = this.owner;

        createContainerWidget();

    }


    private void setTab(CTabItem tab) {

        this.owner = Folder.tabToFolder(tab);

        this.tab = tab;

    }


    private void createContainerWidget() {

        Composite container = new Composite(this.owner.getFolderWidget(), 2048);

        container.setLayout(new FillLayout());

        this.cControl = container;

    }


    public Composite getContainerWidget() {

        return this.tab == null ? this.cControl : (Composite) this.tab.getControl();

    }


    public boolean isHidden() {

        return this.tab == null;

    }


    void hide() {

        if (isHidden()) {

            throw new IllegalStateException();

        }

        this.cControl = ((Composite) this.tab.getControl());

        this.cLabel = this.tab.getText();

        this.cTooltip = this.tab.getToolTipText();

        this.cIcon = this.tab.getImage();

        this.cFont = this.tab.getFont();

        this.cCloseable = this.tab.getShowClose();

        this.tab.dispose();

        this.tab = null;

    }


    void restoreInto(CTabItem tab) {

        if (!isHidden()) {

            throw new IllegalStateException();

        }

        if (this.cControl != null) {

            CTabFolder folder = tab.getParent();

            this.cControl.setParent(folder);

            tab.setControl(this.cControl);

        }

        if (this.cLabel != null) {

            tab.setText(this.cLabel);

        }

        if (this.cTooltip != null) {

            tab.setToolTipText(this.cTooltip);

        }

        if (this.cIcon != null) {

            tab.setImage(this.cIcon);

        }

        if (this.cFont != null) {

            tab.setFont(this.cFont);

        }

        tab.setShowClose(this.cCloseable);

        setTab(tab);

    }


    public Control getControl() {

        return this.tab == null ? this.cControl : this.tab.getControl();

    }


    public boolean isCloseOnHide() {

        return this.closeOnHide;

    }


    public void setCloseOnHide(boolean closeOnHide) {

        this.closeOnHide = closeOnHide;

    }


    public String getLabel() {

        return this.tab == null ? this.cLabel : this.tab.getText();

    }


    public void setLabel(String label) {

        if (this.tab == null) {

            this.cLabel = label;

        } else {

            this.tab.setText(label);

        }

    }


    public String getTooltip() {

        return this.tab == null ? this.cTooltip : this.tab.getToolTipText();

    }


    public void setTooltip(String tooltip) {

        if (this.tab == null) {

            this.cTooltip = tooltip;

        } else {

            this.tab.setToolTipText(tooltip);

        }

    }


    public boolean isHideable() {

        return this.tab == null ? this.cCloseable : this.tab.getShowClose();

    }


    public void setHideable(boolean closeable) {

        if (this.tab == null) {

            this.cCloseable = closeable;

        } else {

            this.tab.setShowClose(closeable);

        }

    }


    public Image getIcon() {

        return this.tab == null ? this.cIcon : this.tab.getImage();

    }


    public void setIcon(Image icon) {

        if (this.tab == null) {

            this.cIcon = icon;

        } else {

            this.tab.setImage(icon);

        }

    }


    public Font getFont() {

        return this.tab == null ? this.cFont : this.tab.getFont();

    }


    public void setFont(Font font) {

        if (this.tab == null) {

            this.cFont = font;

        } else {

            this.tab.setFont(font);

        }

    }


    public void setElementId(String elementId) {

        this.elementId = elementId;

    }


    public String getElementId() {

        return this.elementId;

    }


    public List<? extends IMElement> getChildrenElements() {

        return Collections.emptyList();

    }


    public IMFolder getParentElement() {

        return this.owner;

    }


    public void setManager(IMPartManager manager) {

        this.manager = manager;

    }


    public IMPartManager getManager() {

        return this.manager;

    }


    public Map<String, Object> getData() {

        if (this.data == null) {

            this.data = new HashMap();

        }

        return this.data;

    }


    public String toString() {

        return String.format("Part@%d[%s]", new Object[]{Integer.valueOf(this.internalPartId), getLabel()});

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\app\Part.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */