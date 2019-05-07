/*     */
package com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend;
/*     */
/*     */

import com.pnfsoftware.jeb.client.Licensing;
/*     */ import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.hover.IHoverableProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.GenericHistory;
/*     */ import com.pnfsoftware.jeb.util.io.IO;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import org.eclipse.jface.action.Action;
/*     */ import org.eclipse.jface.action.ToolBarManager;
/*     */ import org.eclipse.jface.resource.ImageDescriptor;
/*     */ import org.eclipse.jface.text.AbstractInformationControl;
/*     */ import org.eclipse.jface.text.IInformationControl;
/*     */ import org.eclipse.jface.text.IInformationControlCreator;
/*     */ import org.eclipse.jface.text.IInformationControlExtension2;
/*     */ import org.eclipse.swt.browser.Browser;
/*     */ import org.eclipse.swt.browser.LocationEvent;
/*     */ import org.eclipse.swt.browser.LocationListener;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MenuDetectEvent;
/*     */ import org.eclipse.swt.events.MenuDetectListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.ToolBar;

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
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class JebInformationControl
        /*     */ extends AbstractInformationControl
        /*     */ implements IInformationControlExtension2, DisposeListener
        /*     */ {
    /*  61 */   private static final ILogger logger = GlobalLog.getLogger(JebInformationControl.class);
    /*     */
    /*     */   private IHoverableWidget hoverable;
    /*     */
    /*     */   private Composite contentComposite;
    /*     */
    /*     */   private Browser fText;
    /*     */
    /*     */   private ToolBarManager tbm;
    /*     */
    /*     */   private Action actionBackward;
    /*     */
    /*     */   private Action actionForward;
    /*     */
    /*     */   private String information;
    /*     */
    /*     */   private IHoverableProvider iHoverableProvider;
    /*     */
    /*  79 */   private GenericHistory<Object> history = new GenericHistory();
    /*     */
    /*     */   private JebLocationListener jebLocationListener;
    /*     */
    /*  83 */   private List<File> temporaryFiles = new ArrayList();

    /*     */
    /*     */
    public JebInformationControl(Shell parent, boolean isResizeable, IHoverableWidget hoverable) {
        /*  86 */
        super(parent, isResizeable);
        /*  87 */
        this.hoverable = hoverable;
        /*  88 */
        create();
        /*     */
    }

    /*     */
    /*     */
    public JebInformationControl(Shell parent, ToolBarManager toolBarManager, IHoverableWidget hoverable)
    /*     */ {
        /*  93 */
        super(parent, toolBarManager);
        /*  94 */
        this.hoverable = hoverable;
        /*  95 */
        create();
        /*     */
    }

    /*     */
    /*     */
    public void setInput(Object input)
    /*     */ {
        /* 100 */
        input = preprocessInput(input);
        /* 101 */
        this.history.record(input);
        /* 102 */
        setInputInner(input);
        /* 103 */
        refreshButtons();
        /*     */
    }

    /*     */
    /*     */
    private void setInputInner(Object input) {
        /* 107 */
        if ((input instanceof String)) {
            /* 108 */
            setInformation((String) input);
            /*     */
        }
        /* 110 */
        else if ((input instanceof HoverableData)) {
            /* 111 */
            if (this.hoverable != null) {
                /* 112 */
                this.hoverable.setInput(((HoverableData) input).getData());
                /*     */
            }
            /*     */
        }
        /* 115 */
        else if ((input instanceof InnerUrl))
            /*     */ {
            /* 117 */
            this.jebLocationListener.ignoreNextUrl = true;
            /* 118 */
            this.fText.setUrl(((InnerUrl) input).url);
            /*     */
        }
        /*     */
        else {
            /* 121 */
            setInformation(Objects.toString(input));
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public void setInformation(String information)
    /*     */ {
        /* 127 */
        super.setInformation(information);
        /* 128 */
        this.fText.setText(information);
        /* 129 */
        this.information = information;
        /*     */
    }

    /*     */
    /*     */
    protected void createContent(Composite parent)
    /*     */ {
        /* 134 */
        this.contentComposite = parent;
        /* 135 */
        if (this.hoverable != null) {
            /* 136 */
            this.hoverable.buildWidget(this.contentComposite);
            /*     */
        }
        /*     */
        else {
            /* 139 */
            Composite cop = new Composite(parent, 0);
            /* 140 */
            GridLayout gl = new GridLayout(1, false);
            /* 141 */
            gl.horizontalSpacing = 0;
            /* 142 */
            gl.marginWidth = 0;
            /* 143 */
            gl.marginHeight = 0;
            /* 144 */
            cop.setLayout(gl);
            /* 145 */
            this.fText = new Browser(cop, 0);
            /* 146 */
            GridData gd = new GridData(4, 4, true, true);
            /* 147 */
            this.fText.setLayoutData(gd);
            /* 148 */
            this.fText.getWebBrowser();
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /* 155 */
            buildToolBarManager(cop);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public IInformationControlCreator getInformationPresenterControlCreator()
    /*     */ {
        /* 161 */
        new IInformationControlCreator()
                /*     */ {
            /*     */
            public IInformationControl createInformationControl(Shell parent) {
                /* 164 */
                JebInformationControl jic = new JebInformationControl(parent, null, JebInformationControl.this.hoverable);
                /* 165 */
                jic.addLocationListener(JebInformationControl.this.iHoverableProvider);
                /* 166 */
                return jic;
                /*     */
            }
            /*     */
        };
        /*     */
    }

    /*     */
    /*     */
    public boolean hasToolBar()
    /*     */ {
        /* 173 */
        return this.tbm != null;
        /*     */
    }

    /*     */
    /*     */
    private ToolBarManager buildToolBarManager(Composite parent) {
        /* 177 */
        this.tbm = new ToolBarManager(256);
        /*     */
        /* 179 */
        this.actionBackward = new Action(null, ImageDescriptor.createFromImage(UIAssetManager.getInstance().getImage("eclipse/backward_nav.png")))
                /*     */ {
            /*     */
            public void run()
            /*     */ {
                /* 182 */
                JebInformationControl.this.setInputInner(JebInformationControl.this.history.backward());
                /* 183 */
                JebInformationControl.this.refreshButtons();
                /*     */
            }
            /* 185 */
        };
        /* 186 */
        this.tbm.add(this.actionBackward);
        /*     */
        /* 188 */
        this.actionForward = new Action(null, ImageDescriptor.createFromImage(UIAssetManager.getInstance().getImage("eclipse/forward_nav.png")))
                /*     */ {
            /*     */
            public void run()
            /*     */ {
                /* 191 */
                JebInformationControl.this.setInputInner(JebInformationControl.this.history.forward());
                /* 192 */
                JebInformationControl.this.refreshButtons();
                /*     */
            }
            /* 194 */
        };
        /* 195 */
        this.tbm.add(this.actionForward);
        /* 196 */
        if (Licensing.isDebugBuild())
            /*     */ {
            /* 198 */
            Action copySourceCode = new Action(null, ImageDescriptor.createFromImage(UIAssetManager.getInstance().getImage("jeb1/icon-export.png")))
                    /*     */ {
                /*     */
                public void run() {
                    /* 201 */
                    UIUtil.copyTextToClipboard(JebInformationControl.this.fText.getText());
                    /*     */
                }
                /* 203 */
            };
            /* 204 */
            this.tbm.add(copySourceCode);
            /*     */
        }
        /* 206 */
        this.tbm.createControl(parent);
        /* 207 */
        refreshButtons();
        /* 208 */
        return this.tbm;
        /*     */
    }

    /*     */
    /*     */
    private void refreshButtons() {
        /* 212 */
        if (!hasToolBar()) {
            /* 213 */
            return;
            /*     */
        }
        /* 215 */
        this.actionBackward.setEnabled(this.history.hasBackward());
        /* 216 */
        this.actionForward.setEnabled(this.history.hasForward());
        /* 217 */
        this.tbm.getControl().update();
        /*     */
    }

    /*     */
    /*     */
    public boolean hasContents()
    /*     */ {
        /* 222 */
        if (this.hoverable != null) {
            /* 223 */
            return this.hoverable.hasContents();
            /*     */
        }
        /*     */
        /*     */
        /* 227 */
        return this.information != null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void dispose()
    /*     */ {
        /* 233 */
        super.dispose();
        /* 234 */
        for (File f : this.temporaryFiles) {
            /* 235 */
            f.delete();
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void widgetDisposed(DisposeEvent e) {
    }

    /*     */
    /*     */
    public void addLocationListener(IHoverableProvider iHoverableProvider)
    /*     */ {
        /* 244 */
        this.iHoverableProvider = iHoverableProvider;
        /* 245 */
        if ((this.fText != null) && (iHoverableProvider != null)) {
            /* 246 */
            this.jebLocationListener = new JebLocationListener(null);
            /* 247 */
            this.fText.addLocationListener(this.jebLocationListener);
            /* 248 */
            this.fText.addMenuDetectListener(new MenuDetectListener()
                    /*     */ {
                /*     */
                /*     */
                /*     */
                public void menuDetected(MenuDetectEvent e) {
                    /* 253 */
                    e.doit = false;
                }
                /*     */
            });
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   private class JebLocationListener implements LocationListener {
        /*     */ boolean ignoreNextUrl;
        /*     */ boolean redirecting;

        /*     */
        /*     */
        private JebLocationListener() {
        }

        /*     */
        /*     */
        public void changing(LocationEvent event) {
            /* 265 */
            if ((this.redirecting) || (this.ignoreNextUrl)) {
                /* 266 */
                return;
                /*     */
            }
            /* 268 */
            Object obj = JebInformationControl.this.preprocessInput(JebInformationControl.this.iHoverableProvider.getHoverInfoOnLocationRequest(event.location, event.top));
            /* 269 */
            if (obj != null) {
                /* 270 */
                if (((obj instanceof URI)) && (!this.redirecting))
                    /*     */ {
                    /* 272 */
                    event.doit = false;
                    /*     */
                    /*     */
                    /* 275 */
                    this.redirecting = true;
                    /* 276 */
                    JebInformationControl.this.fText.setUrl(obj.toString());
                    /* 277 */
                    this.redirecting = false;
                    /* 278 */
                    return;
                    /*     */
                }
                /* 280 */
                JebInformationControl.this.setInput(obj);
                /* 281 */
                event.doit = false;
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */
        /*     */
        /*     */
        public void changed(LocationEvent event)
        /*     */ {
            /* 289 */
            if ((!event.location.equals("about:blank")) &&
                    /* 290 */         (event.top)) {
                /* 291 */
                if (this.ignoreNextUrl) {
                    /* 292 */
                    this.ignoreNextUrl = false;
                    /* 293 */
                    return;
                    /*     */
                }
                /*     */
                /* 296 */
                JebInformationControl.this.history.record(new JebInformationControl.InnerUrl(JebInformationControl.this.fText.getUrl()));
                /* 297 */
                JebInformationControl.this.refreshButtons();
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   private static class InnerUrl
            /*     */ {
        /*     */ String url;

        /*     */
        /*     */
        public InnerUrl(String url) {
            /* 307 */
            this.url = url;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private Object preprocessInput(Object o) {
        /* 312 */
        if ((o instanceof HoverableHtml)) {
            /* 313 */
            HoverableHtml html = (HoverableHtml) o;
            /* 314 */
            if (html.getAnchor() != null)
                /*     */ {
                /*     */
                try
                    /*     */ {
                    /* 318 */
                    File tmp = IO.createTempFile();
                    /*     */
                    try {
                        /* 320 */
                        IO.writeFile(tmp, html.getHtml());
                        /* 321 */
                        this.temporaryFiles.add(tmp);
                        /* 322 */
                        return new URI(tmp + "#" + html.getAnchor());
                        /*     */
                    }
                    /*     */ catch (URISyntaxException | IOException e) {
                        /* 325 */
                        logger.catching(e);
                        /* 326 */
                        tmp.delete();
                        /*     */
                    }
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /*     */
                    /* 334 */
                    return o;
                    /*     */
                }
                /*     */ catch (IOException e1)
                    /*     */ {
                    /* 330 */
                    logger.catching(e1);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\hover\extend\JebInformationControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */