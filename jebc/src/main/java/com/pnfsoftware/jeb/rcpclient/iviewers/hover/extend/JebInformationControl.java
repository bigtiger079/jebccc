package com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.rcpclient.UIAssetManager;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.iviewers.hover.IHoverableProvider;
import com.pnfsoftware.jeb.rcpclient.util.GenericHistory;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;

public class JebInformationControl extends AbstractInformationControl implements IInformationControlExtension2, DisposeListener {
    private static final ILogger logger = GlobalLog.getLogger(JebInformationControl.class);
    private IHoverableWidget hoverable;
    private Composite contentComposite;
    private Browser fText;
    private ToolBarManager tbm;
    private Action actionBackward;
    private Action actionForward;
    private String information;
    private IHoverableProvider iHoverableProvider;
    private GenericHistory<Object> history = new GenericHistory();
    private JebLocationListener jebLocationListener;
    private List<File> temporaryFiles = new ArrayList();

    public JebInformationControl(Shell parent, boolean isResizeable, IHoverableWidget hoverable) {
        super(parent, isResizeable);
        this.hoverable = hoverable;
        create();
    }

    public JebInformationControl(Shell parent, ToolBarManager toolBarManager, IHoverableWidget hoverable) {
        super(parent, toolBarManager);
        this.hoverable = hoverable;
        create();
    }

    public void setInput(Object input) {
        input = preprocessInput(input);
        this.history.record(input);
        setInputInner(input);
        refreshButtons();
    }

    private void setInputInner(Object input) {
        if ((input instanceof String)) {
            setInformation((String) input);
        } else if ((input instanceof HoverableData)) {
            if (this.hoverable != null) {
                this.hoverable.setInput(((HoverableData) input).getData());
            }
        } else if ((input instanceof InnerUrl)) {
            this.jebLocationListener.ignoreNextUrl = true;
            this.fText.setUrl(((InnerUrl) input).url);
        } else {
            setInformation(Objects.toString(input));
        }
    }

    public void setInformation(String information) {
        super.setInformation(information);
        this.fText.setText(information);
        this.information = information;
    }

    protected void createContent(Composite parent) {
        this.contentComposite = parent;
        if (this.hoverable != null) {
            this.hoverable.buildWidget(this.contentComposite);
        } else {
            Composite cop = new Composite(parent, 0);
            GridLayout gl = new GridLayout(1, false);
            gl.horizontalSpacing = 0;
            gl.marginWidth = 0;
            gl.marginHeight = 0;
            cop.setLayout(gl);
            this.fText = new Browser(cop, 0);
            GridData gd = new GridData(4, 4, true, true);
            this.fText.setLayoutData(gd);
            this.fText.getWebBrowser();
            buildToolBarManager(cop);
        }
    }

    public IInformationControlCreator getInformationPresenterControlCreator() {
        return new IInformationControlCreator() {
            public IInformationControl createInformationControl(Shell parent) {
                JebInformationControl jic = new JebInformationControl(parent, null, JebInformationControl.this.hoverable);
                jic.addLocationListener(JebInformationControl.this.iHoverableProvider);
                return jic;
            }
        };
    }

    public boolean hasToolBar() {
        return this.tbm != null;
    }

    private ToolBarManager buildToolBarManager(Composite parent) {
        this.tbm = new ToolBarManager(256);
        this.actionBackward = new Action(null, ImageDescriptor.createFromImage(UIAssetManager.getInstance().getImage("eclipse/backward_nav.png"))) {
            public void run() {
                JebInformationControl.this.setInputInner(JebInformationControl.this.history.backward());
                JebInformationControl.this.refreshButtons();
            }
        };
        this.tbm.add(this.actionBackward);
        this.actionForward = new Action(null, ImageDescriptor.createFromImage(UIAssetManager.getInstance().getImage("eclipse/forward_nav.png"))) {
            public void run() {
                JebInformationControl.this.setInputInner(JebInformationControl.this.history.forward());
                JebInformationControl.this.refreshButtons();
            }
        };
        this.tbm.add(this.actionForward);
        if (Licensing.isDebugBuild()) {
            Action copySourceCode = new Action(null, ImageDescriptor.createFromImage(UIAssetManager.getInstance().getImage("jeb1/icon-export.png"))) {
                public void run() {
                    UIUtil.copyTextToClipboard(JebInformationControl.this.fText.getText());
                }
            };
            this.tbm.add(copySourceCode);
        }
        this.tbm.createControl(parent);
        refreshButtons();
        return this.tbm;
    }

    private void refreshButtons() {
        if (!hasToolBar()) {
            return;
        }
        this.actionBackward.setEnabled(this.history.hasBackward());
        this.actionForward.setEnabled(this.history.hasForward());
        this.tbm.getControl().update();
    }

    public boolean hasContents() {
        if (this.hoverable != null) {
            return this.hoverable.hasContents();
        }
        return this.information != null;
    }

    public void dispose() {
        super.dispose();
        for (File f : this.temporaryFiles) {
            f.delete();
        }
    }

    public void widgetDisposed(DisposeEvent e) {
    }

    public void addLocationListener(IHoverableProvider iHoverableProvider) {
        this.iHoverableProvider = iHoverableProvider;
        if ((this.fText != null) && (iHoverableProvider != null)) {
            this.jebLocationListener = new JebLocationListener();
            this.fText.addLocationListener(this.jebLocationListener);
            this.fText.addMenuDetectListener(new MenuDetectListener() {
                public void menuDetected(MenuDetectEvent e) {
                    e.doit = false;
                }
            });
        }
    }

    private class JebLocationListener implements LocationListener {
        boolean ignoreNextUrl;
        boolean redirecting;

        private JebLocationListener() {
        }

        public void changing(LocationEvent event) {
            if ((this.redirecting) || (this.ignoreNextUrl)) {
                return;
            }
            Object obj = JebInformationControl.this.preprocessInput(JebInformationControl.this.iHoverableProvider.getHoverInfoOnLocationRequest(event.location, event.top));
            if (obj != null) {
                if (((obj instanceof URI)) && (!this.redirecting)) {
                    event.doit = false;
                    this.redirecting = true;
                    JebInformationControl.this.fText.setUrl(obj.toString());
                    this.redirecting = false;
                    return;
                }
                JebInformationControl.this.setInput(obj);
                event.doit = false;
            }
        }

        public void changed(LocationEvent event) {
            if ((!event.location.equals("about:blank")) && (event.top)) {
                if (this.ignoreNextUrl) {
                    this.ignoreNextUrl = false;
                    return;
                }
                JebInformationControl.this.history.record(new JebInformationControl.InnerUrl(JebInformationControl.this.fText.getUrl()));
                JebInformationControl.this.refreshButtons();
            }
        }
    }

    private static class InnerUrl {
        String url;

        public InnerUrl(String url) {
            this.url = url;
        }
    }

    private Object preprocessInput(Object o) {
        if ((o instanceof HoverableHtml)) {
            HoverableHtml html = (HoverableHtml) o;
            if (html.getAnchor() != null) {
                try {
                    File tmp = IO.createTempFile();
                    try {
                        IO.writeFile(tmp, html.getHtml());
                        this.temporaryFiles.add(tmp);
                        return new URI(tmp + "#" + html.getAnchor());
                    } catch (URISyntaxException | IOException e) {
                        logger.catching(e);
                        tmp.delete();
                    }
                    return o;
                } catch (IOException e1) {
                    logger.catching(e1);
                }
            }
        }
        return o;
    }
}


