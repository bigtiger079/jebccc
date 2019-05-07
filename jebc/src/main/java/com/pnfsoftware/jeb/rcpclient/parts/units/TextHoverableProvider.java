/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
/*     */ import com.pnfsoftware.jeb.core.IPlugin;
/*     */ import com.pnfsoftware.jeb.core.IPluginInformation;
/*     */ import com.pnfsoftware.jeb.core.IUnitContribution;
/*     */ import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
/*     */ import com.pnfsoftware.jeb.core.output.IActionableItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextItem;
/*     */ import com.pnfsoftware.jeb.core.units.IAddressableUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.hover.IHoverableProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend.HoverableHtml;
/*     */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
/*     */ import com.pnfsoftware.jeb.util.base.HtmlTypedContentProperties;
/*     */ import com.pnfsoftware.jeb.util.base.TypedContent;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.text.IRegion;
/*     */ import org.eclipse.jface.text.ITextViewer;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.widgets.Display;

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
/*     */ public class TextHoverableProvider
        /*     */ implements IHoverableProvider
        /*     */ {
    /*  44 */   private static final ILogger logger = GlobalLog.getLogger(TextHoverableProvider.class);
    /*     */   private Display display;
    /*     */   private RcpClientContext context;
    /*     */   private IUnit unit;
    /*     */   private ITextDocumentViewer iviewer;

    /*     */
    /*     */
    public TextHoverableProvider(RcpClientContext context, IUnit unit, ITextDocumentViewer iviewer)
    /*     */ {
        /*  52 */
        this.display = context.getDisplay();
        /*  53 */
        this.context = context;
        /*  54 */
        this.unit = unit;
        /*  55 */
        this.iviewer = iviewer;
        /*     */
    }

    /*     */
    /*     */
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
    /*     */ {
        /*  60 */
        logger.debug("Hovering on: %s", new Object[]{hoverRegion});
        /*     */
        /*     */
        /*     */
        /*  64 */
        TextItemRetriever retriever = new TextItemRetriever(this.iviewer, hoverRegion);
        /*  65 */
        UIExecutor.sync(this.display, retriever);
        /*  66 */
        ITextItem item = retriever.getItem();
        /*  67 */
        if (item != null) {
            /*  68 */
            String itemText = item.getText();
            /*     */
            /*  70 */
            long itemId = 0L;
            /*  71 */
            if ((item instanceof IActionableItem)) {
                /*  72 */
                itemId = ((IActionableItem) item).getItemId();
                /*     */
            }
            /*     */
            /*  75 */
            if (((itemId != 0L) || (itemText != null)) && ((this.unit instanceof IAddressableUnit)))
                /*     */ {
                /*  77 */
                List<IUnitContribution> contributions = RuntimeProjectUtil.findUnitContributions(this.context.getOpenedProject(), this.unit);
                /*  78 */
                if ((contributions != null) && (!contributions.isEmpty())) {
                    /*  79 */
                    StringBuilder sb = new StringBuilder();
                    /*  80 */
                    String anchor = null;
                    /*  81 */
                    for (IUnitContribution contrib : contributions) {
                        /*  82 */
                        TypedContent content = null;
                        /*     */
                        try {
                            /*  84 */
                            content = contrib.getItemInformation((IAddressableUnit) this.unit, itemId, itemText);
                            /*     */
                        }
                        /*     */ catch (Exception e) {
                            /*  87 */
                            logger.catchingSilent(e);
                            /*     */
                        }
                        /*  89 */
                        if (content != null) {
                            /*  90 */
                            appendContribution(sb, content);
                            /*  91 */
                            if (anchor == null) {
                                /*  92 */
                                anchor = retrieveAnchor(content);
                                /*     */
                            }
                            /*  94 */
                            this.context.getTelemetry().record("contributionItemHover", "name", getPluginNameName(contrib));
                            /*     */
                        }
                        /*     */
                    }
                    /*  97 */
                    return buildResult(sb, anchor);
                    /*     */
                }
                /*     */
            }
            /*     */
        }
        /* 101 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    private Object buildResult(StringBuilder sb, String anchor) {
        /* 105 */
        if (sb.length() == 0) {
            /* 106 */
            return null;
            /*     */
        }
        /* 108 */
        if (anchor != null) {
            /* 109 */
            return new HoverableHtml(sb.toString(), anchor);
            /*     */
        }
        /* 111 */
        return sb.toString();
        /*     */
    }

    /*     */
    /*     */
    private void appendContribution(StringBuilder sb, TypedContent content)
    /*     */ {
        /* 116 */
        if ((content != null) && (!content.getText().isEmpty())) {
            /* 117 */
            if (Strings.isContainedIn(content.getMimeType(), new String[]{"text/html", "text/plain"})) {
                /* 118 */
                sb.append(content.getText());
                /* 119 */
                sb.append("\n");
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */   static class TextItemRetriever extends UIRunnable {
        /*     */ ITextDocumentViewer interactiveTextViewer;
        /*     */ IRegion region;
        /*     */ ITextItem item;

        /*     */
        /*     */
        public TextItemRetriever(ITextDocumentViewer iviewer, IRegion region) {
            /* 130 */
            this.interactiveTextViewer = iviewer;
            /* 131 */
            this.region = region;
            /* 132 */
            this.item = null;
            /*     */
        }

        /*     */
        /*     */
        public void runi()
        /*     */ {
            /* 137 */
            if (this.interactiveTextViewer.getTextWidget().isDisposed()) {
                /* 138 */
                this.item = null;
                /* 139 */
                return;
                /*     */
            }
            /* 141 */
            this.item = this.interactiveTextViewer.getItemAt(this.region.getOffset());
            /* 142 */
            String r = Strings.toString(this.item);
            /* 143 */
            TextHoverableProvider.logger.i(r, new Object[0]);
            /*     */
        }

        /*     */
        /*     */
        public ITextItem getItem() {
            /* 147 */
            return this.item;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public Object getHoverInfoOnLocationRequest(String location, boolean top)
    /*     */ {
        /* 153 */
        if ((this.unit instanceof IAddressableUnit)) {
            /* 154 */
            List<IUnitContribution> contributions = RuntimeProjectUtil.findUnitContributions(this.context.getOpenedProject(), this.unit);
            /*     */
            /* 156 */
            if ((contributions != null) && (!contributions.isEmpty())) {
                /* 157 */
                StringBuilder sb = new StringBuilder();
                /* 158 */
                String anchor = null;
                /* 159 */
                for (IUnitContribution contrib : contributions) {
                    /* 160 */
                    TypedContent content = null;
                    /*     */
                    try {
                        /* 162 */
                        content = contrib.getLocationInformation((IAddressableUnit) this.unit, location);
                        /*     */
                    }
                    /*     */ catch (Exception e) {
                        /* 165 */
                        logger.catchingSilent(e);
                        /*     */
                    }
                    /* 167 */
                    if (content != null) {
                        /* 168 */
                        appendContribution(sb, content);
                        /* 169 */
                        if (anchor == null) {
                            /* 170 */
                            anchor = retrieveAnchor(content);
                            /*     */
                        }
                        /* 172 */
                        this.context.getTelemetry().record("contributionItemHoverNav", "name", getPluginNameName(contrib));
                        /*     */
                    }
                    /*     */
                }
                /* 175 */
                return buildResult(sb, anchor);
                /*     */
            }
            /*     */
        }
        /* 178 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    private String retrieveAnchor(TypedContent content) {
        /* 182 */
        if ((content.getMimeType().equals("text/html")) && ((content.getProperties() instanceof HtmlTypedContentProperties))) {
            /* 183 */
            HtmlTypedContentProperties props = (HtmlTypedContentProperties) content.getProperties();
            /* 184 */
            if (props != null) {
                /* 185 */
                return props.getAnchor();
                /*     */
            }
            /*     */
        }
        /* 188 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    private static String getPluginNameName(IPlugin plugin)
    /*     */ {
        /* 193 */
        if ((plugin.getPluginInformation() != null) && (!Strings.isBlank(plugin.getPluginInformation().getName()))) {
            /* 194 */
            return plugin.getPluginInformation().getName();
            /*     */
        }
        /* 196 */
        return plugin.getClass().getName();
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\TextHoverableProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */