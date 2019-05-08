package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.rcpclient.FontManager;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.themes.ThemeManager;
import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
import com.pnfsoftware.jeb.rcpclient.iviewers.StyleManager;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class StyleOptionsDialog
        extends JebDialog {
    private static final ILogger logger = GlobalLog.getLogger(StyleOptionsDialog.class);
    private Boolean modified = Boolean.FALSE;
    private ThemeManager themeManager;
    private FontManager fontman;
    private FontManager fontman0;
    private StyleManager styleman;
    private StyleManager styleman0;
    private Label labelFontName;
    private Button btnDarkTheme;
    private CCombo colors;

    public StyleOptionsDialog(Shell parent, ThemeManager themeManager, StyleManager styleManager, FontManager fontManager) {
        super(parent, S.s(755), true, true);
        this.scrolledContainer = true;
        this.boundsRestorationType = ShellWrapper.BoundsRestorationType.SIZE_AND_POSITION;
        this.themeManager = themeManager;
        if (styleManager == null) {
            throw new NullPointerException();
        }
        this.styleman = styleManager;
        this.fontman = fontManager;
    }

    public Boolean open() {
        super.open();
        return this.modified;
    }

    protected void createContents(final Composite parent) {
        UIUtil.setStandardLayout(parent);
        if (this.fontman != null) {
            this.fontman0 = this.fontman.clone();
            Group grpFont = new Group(parent, 0);
            grpFont.setLayout(new GridLayout(2, false));
            grpFont.setText(S.s(202));
            grpFont.setLayoutData(UIUtil.createGridDataFillHorizontally());
            UIUtil.createPushbox(grpFont, S.s(722) + "...", new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    FontDialog dlgFontSelect = new FontDialog(StyleOptionsDialog.this.shell);
                    Font codefont = StyleOptionsDialog.this.fontman.getCodeFont();
                    dlgFontSelect.setFontList(codefont.getFontData());
                    FontData fd = dlgFontSelect.open();
                    if (fd != null) {
                        codefont = new Font(parent.getDisplay(), dlgFontSelect.getFontList());
                        StyleOptionsDialog.this.fontman.setCodeFont(codefont);
                        StyleOptionsDialog.this.refreshWidgets();
                    }
                }
            });
            this.labelFontName = new Label(grpFont, 2048);
            this.labelFontName.setLayoutData(UIUtil.createGridDataFillHorizontally());
        }
        this.styleman0 = this.styleman.clone();
        Group grpTheme = new Group(parent, 0);
        grpTheme.setLayout(new GridLayout(2, false));
        grpTheme.setText(S.s(771));
        grpTheme.setLayoutData(UIUtil.createGridDataFillHorizontally());
        this.btnDarkTheme = UIUtil.createCheckbox(grpTheme, S.s(810), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean selected = StyleOptionsDialog.this.btnDarkTheme.getSelection();
                boolean restart = StyleOptionsDialog.this.themeManager.setActiveTheme(selected ? "theme.dark" : "theme.standard");
                if (restart) {
                }
            }
        });
        createColorPickerForClassIds(parent);
        Composite buttons = new Composite(parent, 0);
        buttons.setLayout(new GridLayout(3, false));
        buttons.setLayoutData(UIUtil.createGridDataSpanHorizontally(2));
        UIUtil.createPushbox(buttons, S.s(605), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                StyleOptionsDialog.this.modified = Boolean.TRUE;
                StyleOptionsDialog.this.shell.close();
            }
        });
        UIUtil.createPushbox(buttons, S.s(105), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (StyleOptionsDialog.this.fontman != null) {
                    StyleOptionsDialog.this.fontman.restore(StyleOptionsDialog.this.fontman0, true);
                }
                StyleOptionsDialog.this.styleman.restore(StyleOptionsDialog.this.styleman0, true);
                StyleOptionsDialog.this.shell.close();
            }
        });
        UIUtil.createPushbox(buttons, S.s(681), new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (StyleOptionsDialog.this.fontman != null) {
                    StyleOptionsDialog.this.fontman.resetDefaults(true, true);
                }
                StyleOptionsDialog.this.styleman.resetDefaults(true);
                StyleOptionsDialog.this.refreshWidgets();
            }
        });
        refreshWidgets();
    }

    private void refreshWidgets() {
        if (this.fontman != null) {
            Font codefont = this.fontman.getCodeFont();
            if ((codefont != null) && (codefont.getFontData().length >= 1)) {
                FontData fd = codefont.getFontData()[0];
                String text = String.format("%s %dpt", new Object[]{fd.getName(), Integer.valueOf(fd.getHeight())});
                if ((fd.getStyle() & 0x1) != 0) {
                    text = text + " " + S.s(99);
                }
                if ((fd.getStyle() & 0x2) != 0) {
                    text = text + " " + S.s(407);
                }
                this.labelFontName.setText(text);
            }
        }
        if (this.themeManager == null) {
            this.btnDarkTheme.setEnabled(false);
        } else {
            this.btnDarkTheme.setSelection(this.themeManager.isDarkTheme());
        }
        this.colors.select(0);
        this.colors.notifyListeners(13, new Event());
    }

    private void createColorPickerForClassIds(Composite parent) {
        Group c0 = new Group(parent, 0);
        c0.setLayoutData(UIUtil.createGridDataFillHorizontally());
        c0.setLayout(new RowLayout(512));
        c0.setText(S.s(196));
        Composite c1 = new Composite(c0, 0);
        c1.setLayout(new RowLayout(256));
        Label lbl_sc = new Label(c1, 0);
        lbl_sc.setText(S.s(779) + ":  ");
        this.colors = new CCombo(c1, 8390664);
        final ItemClassIdentifiers[] style_types = ItemClassIdentifiers.values();
        for (ItemClassIdentifiers classId : style_types) {
            this.colors.add(classId.toString());
        }
        Group c2 = new Group(c0, 0);
        c2.setLayout(new RowLayout(256));
        c2.setText(S.s(597));
        final ColorPickerView normal_fgcolor = new ColorPickerView(c2, S.s(355));
        final ColorPickerView normal_bgcolor = new ColorPickerView(c2, S.s(96));
        final Button normal_bold = UIUtil.createCheckbox(c2, S.s(99), null);
        final Button normal_italic = UIUtil.createCheckbox(c2, S.s(407), null);
        Group c3 = new Group(c0, 0);
        c3.setLayout(new RowLayout(256));
        c3.setText(S.s(49));
        final ColorPickerView active_fgcolor = new ColorPickerView(c3, S.s(355));
        final ColorPickerView active_bgcolor = new ColorPickerView(c3, S.s(96));
        final Button active_bold = UIUtil.createCheckbox(c3, S.s(99), null);
        final Button active_italic = UIUtil.createCheckbox(c3, S.s(407), null);
        this.colors.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = StyleOptionsDialog.this.colors.getSelectionIndex();
                Style style = StyleOptionsDialog.this.styleman.getNormalStyle(style_types[index]);
                normal_fgcolor.setColor(style.getColor());
                normal_bgcolor.setColor(style.getBackgroungColor());
                normal_bold.setSelection(style.isBold());
                normal_italic.setSelection(style.isItalic());
                style = StyleOptionsDialog.this.styleman.getActiveStyle(style_types[index]);
                active_fgcolor.setColor(style.getColor());
                active_bgcolor.setColor(style.getBackgroungColor());
                active_bold.setSelection(style.isBold());
                active_italic.setSelection(style.isItalic());
            }
        });
        SelectionAdapter listener_normal_fgcolor = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = StyleOptionsDialog.this.colors.getSelectionIndex();
                StyleOptionsDialog.this.styleman.getNormalStyle(style_types[index]).setColor(((ColorPickerView) e.widget).getColor());
            }
        };
        normal_fgcolor.addSelectionListener(listener_normal_fgcolor);
        SelectionAdapter listener_normal_bgcolor = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = StyleOptionsDialog.this.colors.getSelectionIndex();
                StyleOptionsDialog.this.styleman.getNormalStyle(style_types[index]).setBackgroundColor(((ColorPickerView) e.widget).getColor());
            }
        };
        normal_bgcolor.addSelectionListener(listener_normal_bgcolor);
        normal_bold.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = StyleOptionsDialog.this.colors.getSelectionIndex();
                StyleOptionsDialog.this.styleman.getNormalStyle(style_types[index]).setBold(((Button) e.widget).getSelection());
            }
        });
        normal_italic.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = StyleOptionsDialog.this.colors.getSelectionIndex();
                StyleOptionsDialog.this.styleman.getNormalStyle(style_types[index]).setItalic(((Button) e.widget).getSelection());
            }
        });
        SelectionAdapter listener_active_fgcolor = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = StyleOptionsDialog.this.colors.getSelectionIndex();
                StyleOptionsDialog.this.styleman.getActiveStyle(style_types[index]).setColor(((ColorPickerView) e.widget).getColor());
            }
        };
        active_fgcolor.addSelectionListener(listener_active_fgcolor);
        SelectionAdapter listener_active_bgcolor = new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = StyleOptionsDialog.this.colors.getSelectionIndex();
                StyleOptionsDialog.this.styleman.getActiveStyle(style_types[index]).setBackgroundColor(((ColorPickerView) e.widget).getColor());
            }
        };
        active_bgcolor.addSelectionListener(listener_active_bgcolor);
        active_bold.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = StyleOptionsDialog.this.colors.getSelectionIndex();
                StyleOptionsDialog.this.styleman.getActiveStyle(style_types[index]).setBold(((Button) e.widget).getSelection());
            }
        });
        active_italic.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int index = StyleOptionsDialog.this.colors.getSelectionIndex();
                StyleOptionsDialog.this.styleman.getActiveStyle(style_types[index]).setItalic(((Button) e.widget).getSelection());
            }
        });
    }
}


