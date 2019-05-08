package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.IWidgetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper.BoundsRestorationType;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.FilterText;
import com.pnfsoftware.jeb.rcpclient.util.SwtUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public abstract class JebDialog
        extends Dialog {
    private static IWidgetManager standardWidgetManager;
    private IWidgetManager widgetManager;
    private String widgetName;
    private int widthMinRatio;
    private int widthMaxRatio;
    private int heightMinRatio;
    private int heightMaxRatio;
    protected Shell shell;
    protected boolean doNotOpenShell;
    protected boolean doNotDispatchEvents;

    public static void setStandardWidgetManager(IWidgetManager widgetManager) {
        standardWidgetManager = widgetManager;
    }

    public static IWidgetManager getStandardWidgetManager() {
        return standardWidgetManager;
    }

    protected ShellWrapper.BoundsRestorationType boundsRestorationType = ShellWrapper.BoundsRestorationType.POSITION;
    protected boolean scrolledContainer;

    public JebDialog(Shell parent, String caption, boolean resizable, boolean modal) {
        this(parent, caption, resizable, modal, null);
    }

    public JebDialog(Shell parent, String caption, boolean resizable, boolean modal, String widgetName) {
        this(parent == null ? Display.getCurrent().getActiveShell() : parent, 0x860 | (resizable ? 16 : 0) | (modal ? 65536 : 0), caption, widgetName);
    }

    public JebDialog(Shell parent, int style, String caption, String widgetName) {
        super(parent, style);
        if (caption != null) {
            setText(caption);
        }
        this.widgetManager = (this.widgetManager != null ? this.widgetManager : standardWidgetManager);
        this.widgetName = ((widgetName != null) && (!widgetName.isEmpty()) ? widgetName : getClass().getName());
        setVisualBounds(20, 80, 20, 80);
    }

    public void setText(String text) {
        super.setText(text);
        if (this.shell != null) {
            this.shell.setText(super.getText());
        }
    }

    public void setVisualBounds(int widthMinRatio, int widthMaxRatio, int heightMinRatio, int heightMaxRatio) {
        if ((widthMinRatio >= 0) && (widthMinRatio <= 100)) {
            this.widthMinRatio = widthMinRatio;
            if (this.widthMaxRatio < this.widthMinRatio) {
                this.widthMaxRatio = this.widthMinRatio;
            }
        }
        if ((widthMaxRatio >= 0) && (widthMaxRatio <= 100)) {
            this.widthMaxRatio = widthMaxRatio;
            if (this.widthMaxRatio < this.widthMinRatio) {
                this.widthMinRatio = this.widthMaxRatio;
            }
        }
        if ((heightMinRatio >= 0) && (heightMinRatio <= 100)) {
            this.heightMinRatio = heightMinRatio;
            if (this.heightMaxRatio < this.heightMinRatio) {
                this.heightMaxRatio = this.heightMinRatio;
            }
        }
        if ((heightMaxRatio >= 0) && (heightMaxRatio <= 100)) {
            this.heightMaxRatio = heightMaxRatio;
            if (this.heightMaxRatio < this.heightMinRatio) {
                this.heightMinRatio = this.heightMaxRatio;
            }
        }
    }

    public String getWidgetName() {
        return this.widgetName;
    }

    public IWidgetManager getWidgetManager() {
        return this.widgetManager;
    }

    public Object open() {
        Shell parent = getParent();
        Rectangle parentBounds = parent.getBounds();
        int wmax = this.widthMaxRatio * parentBounds.width / 100;
        int wmin = this.widthMinRatio * parentBounds.width / 100;
        int hmax = this.heightMaxRatio * parentBounds.height / 100;
        int hmin = this.heightMinRatio * parentBounds.height / 100;
        this.shell = new Shell(parent, getStyle());
        this.shell.setText(getText());
        UIUtil.setWidgetName(this.shell, this.widgetName);
        ShellWrapper shellWrapper = ShellWrapper.wrap(this.shell, this.widgetManager);
        boolean hasRecordedBounds = (shellWrapper != null) && (shellWrapper.hasRecordedBounds());
        if ((this.boundsRestorationType != ShellWrapper.BoundsRestorationType.NONE) && (!hasRecordedBounds)) {
            this.shell.setSize(wmax, hmax);
        }
        ScrolledComposite sctl = null;
        Composite ctl = null;
        this.shell.setLayout(new FillLayout());
        if (this.scrolledContainer) {
            sctl = new ScrolledComposite(this.shell, 768);
            sctl.setExpandHorizontal(true);
            sctl.setExpandVertical(true);
            ctl = new Composite(sctl, 0);
            sctl.setContent(ctl);
        } else {
            ctl = new Composite(this.shell, 0);
        }
        createContents(ctl);
        if (this.scrolledContainer) {
            sctl.setMinSize(ctl.computeSize(-1, -1));
        }
        this.shell.pack();
        if (this.boundsRestorationType != ShellWrapper.BoundsRestorationType.NONE) {
            if (hasRecordedBounds) {
                switch (this.boundsRestorationType) {
                    case POSITION:
                        this.shell.setLocation(shellWrapper.getRecordedPosition());
                        break;
                    case SIZE:
                        this.shell.setSize(shellWrapper.getRecordedSize());
                        break;
                    case SIZE_AND_POSITION:
                        this.shell.setBounds(shellWrapper.getRecordedBounds());
                        break;
                }
            } else {
                boolean smallerWidth = false;
                boolean smallerHeight = false;
                Point dlgsize = this.shell.getSize();
                if (dlgsize.x > wmax) {
                    dlgsize.x = wmax;
                    smallerWidth = true;
                } else if (dlgsize.x < wmin) {
                    dlgsize.x = wmin;
                }
                if (dlgsize.y > hmax) {
                    dlgsize.y = hmax;
                    smallerHeight = true;
                } else if (dlgsize.y < hmin) {
                    dlgsize.y = hmin;
                }
                if ((smallerWidth) && (!smallerHeight)) {
                    int y = this.shell.computeSize(dlgsize.x, -1).y;
                    if ((y >= hmin) && (y <= hmax)) {
                        dlgsize.y = y;
                    }
                }
                if ((!smallerWidth) && (smallerHeight)) {
                    int x = this.shell.computeSize(-1, dlgsize.y).x;
                    if ((x >= wmin) && (x <= wmax)) {
                        dlgsize.x = x;
                    }
                }
                this.shell.setSize(dlgsize);
                dlgsize = this.shell.getSize();
                this.shell.setLocation(parentBounds.x + (parentBounds.width - dlgsize.x) / 2, parentBounds.y + (parentBounds.height - dlgsize.y) / 2);
            }
        }
        this.shell.addListener(31, new Listener() {
            public void handleEvent(Event e) {
                if (e.detail == 4) {
                    e.doit = JebDialog.this.allowKeyboardEnterExecution(e);
                }
            }
        });
        if (!this.doNotOpenShell) {
            this.shell.open();
        }
        if (!this.doNotDispatchEvents) {
            Display display = parent.getDisplay();
            while (!this.shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    SwtUtil.sleep(display);
                }
            }
        }
        return null;
    }

    public Shell getShell() {
        return this.shell;
    }

    public void setFocus() {
        this.shell.setFocus();
    }

    public void setVisible(boolean visible) {
        this.shell.setVisible(visible);
    }

    private static final int[][] defaultAvailableButtons = {{32, 605}, {256, 105}, {64, 828}, {128, 594}};

    protected abstract void createContents(Composite paramComposite);

    private Map<Integer, Button> buttons = new HashMap();

    protected Button getButtonByStyle(int style) {
        return (Button) this.buttons.get(Integer.valueOf(style));
    }

    protected Composite createOkayButton(Composite parent) {
        return createButtons(parent, 32, 32);
    }

    protected Composite createOkayCancelButtons(Composite parent) {
        return createButtons(parent, 288, 32);
    }

    protected Composite createButtons(Composite parent, int buttonStyles, int defaultButton) {
        List<int[]> avails = new ArrayList();
        for (int[] button : defaultAvailableButtons) {
            int buttonMask = button[0];
            if ((buttonStyles & buttonMask) != 0) {
                avails.add(button);
            }
        }
        return createButtons(parent, 0, (int[][]) avails.toArray(new int[avails.size()][]), defaultButton);
    }

    protected Composite createButtons(Composite parent, int style, int[][] availableButtons, int defaultButtonId) {
        Composite buttonsPanel = new Composite(parent, style);
        int cols;
        if ((parent.getLayout() instanceof GridLayout)) {
            cols = availableButtons.length;
            buttonsPanel.setLayoutData(UIUtil.createGridDataSpanHorizontally(cols));
        }
        buttonsPanel.setLayout(new RowLayout(256));
        for (int[] button : availableButtons) {
            final int buttonId = button[0];
            Button btn = UIUtil.createPushbox(buttonsPanel, S.s(button[1]), new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    JebDialog.this.onButtonClick(buttonId);
                }
            });
            this.buttons.put(Integer.valueOf(button[0]), btn);
            if (buttonId == defaultButtonId) {
                this.shell.setDefaultButton(btn);
            }
        }
        return buttonsPanel;
    }

    protected void onButtonClick(int style) {
        if (style == 32) {
            onConfirm();
        } else if (style == 256) {
            onCancel();
        } else if (style == 64) {
            onButtonYes();
        } else if (style == 128) {
            onButtonNo();
        } else {
            this.shell.close();
        }
    }

    protected void onConfirm() {
        this.shell.close();
    }

    protected void onCancel() {
        this.shell.close();
    }

    protected void onButtonYes() {
        this.shell.close();
    }

    protected void onButtonNo() {
        this.shell.close();
    }

    protected boolean allowKeyboardEnterExecution(Event e) {
        return !FilterText.isSelected();
    }
}


