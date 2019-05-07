
package com.pnfsoftware.jeb.rcpclient.parts.units;


import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.IPlugin;
import com.pnfsoftware.jeb.core.IPluginInformation;
import com.pnfsoftware.jeb.core.IUnitContribution;
import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
import com.pnfsoftware.jeb.core.output.IActionableItem;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.core.units.IAddressableUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.iviewers.hover.IHoverableProvider;
import com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend.HoverableHtml;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.ITextDocumentViewer;
import com.pnfsoftware.jeb.util.base.HtmlTypedContentProperties;
import com.pnfsoftware.jeb.util.base.TypedContent;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;


public class TextHoverableProvider
        implements IHoverableProvider {
    private static final ILogger logger = GlobalLog.getLogger(TextHoverableProvider.class);
    private Display display;
    private RcpClientContext context;
    private IUnit unit;
    private ITextDocumentViewer iviewer;


    public TextHoverableProvider(RcpClientContext context, IUnit unit, ITextDocumentViewer iviewer) {

        this.display = context.getDisplay();

        this.context = context;

        this.unit = unit;

        this.iviewer = iviewer;

    }


    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {

        logger.debug("Hovering on: %s", new Object[]{hoverRegion});


        TextItemRetriever retriever = new TextItemRetriever(this.iviewer, hoverRegion);

        UIExecutor.sync(this.display, retriever);

        ITextItem item = retriever.getItem();

        if (item != null) {

            String itemText = item.getText();


            long itemId = 0L;

            if ((item instanceof IActionableItem)) {

                itemId = ((IActionableItem) item).getItemId();

            }


            if (((itemId != 0L) || (itemText != null)) && ((this.unit instanceof IAddressableUnit))) {

                List<IUnitContribution> contributions = RuntimeProjectUtil.findUnitContributions(this.context.getOpenedProject(), this.unit);

                if ((contributions != null) && (!contributions.isEmpty())) {

                    StringBuilder sb = new StringBuilder();

                    String anchor = null;

                    for (IUnitContribution contrib : contributions) {

                        TypedContent content = null;

                        try {

                            content = contrib.getItemInformation((IAddressableUnit) this.unit, itemId, itemText);

                        } catch (Exception e) {

                            logger.catchingSilent(e);

                        }

                        if (content != null) {

                            appendContribution(sb, content);

                            if (anchor == null) {

                                anchor = retrieveAnchor(content);

                            }

                            this.context.getTelemetry().record("contributionItemHover", "name", getPluginNameName(contrib));

                        }

                    }

                    return buildResult(sb, anchor);

                }

            }

        }

        return null;

    }


    private Object buildResult(StringBuilder sb, String anchor) {

        if (sb.length() == 0) {

            return null;

        }

        if (anchor != null) {

            return new HoverableHtml(sb.toString(), anchor);

        }

        return sb.toString();

    }


    private void appendContribution(StringBuilder sb, TypedContent content) {

        if ((content != null) && (!content.getText().isEmpty())) {

            if (Strings.isContainedIn(content.getMimeType(), new String[]{"text/html", "text/plain"})) {

                sb.append(content.getText());

                sb.append("\n");

            }

        }

    }


    static class TextItemRetriever extends UIRunnable {
        ITextDocumentViewer interactiveTextViewer;
        IRegion region;
        ITextItem item;


        public TextItemRetriever(ITextDocumentViewer iviewer, IRegion region) {

            this.interactiveTextViewer = iviewer;

            this.region = region;

            this.item = null;

        }


        public void runi() {

            if (this.interactiveTextViewer.getTextWidget().isDisposed()) {

                this.item = null;

                return;

            }

            this.item = this.interactiveTextViewer.getItemAt(this.region.getOffset());

            String r = Strings.toString(this.item);

            TextHoverableProvider.logger.i(r, new Object[0]);

        }


        public ITextItem getItem() {

            return this.item;

        }

    }


    public Object getHoverInfoOnLocationRequest(String location, boolean top) {

        if ((this.unit instanceof IAddressableUnit)) {

            List<IUnitContribution> contributions = RuntimeProjectUtil.findUnitContributions(this.context.getOpenedProject(), this.unit);


            if ((contributions != null) && (!contributions.isEmpty())) {

                StringBuilder sb = new StringBuilder();

                String anchor = null;

                for (IUnitContribution contrib : contributions) {

                    TypedContent content = null;

                    try {

                        content = contrib.getLocationInformation((IAddressableUnit) this.unit, location);

                    } catch (Exception e) {

                        logger.catchingSilent(e);

                    }

                    if (content != null) {

                        appendContribution(sb, content);

                        if (anchor == null) {

                            anchor = retrieveAnchor(content);

                        }

                        this.context.getTelemetry().record("contributionItemHoverNav", "name", getPluginNameName(contrib));

                    }

                }

                return buildResult(sb, anchor);

            }

        }

        return null;

    }


    private String retrieveAnchor(TypedContent content) {

        if ((content.getMimeType().equals("text/html")) && ((content.getProperties() instanceof HtmlTypedContentProperties))) {

            HtmlTypedContentProperties props = (HtmlTypedContentProperties) content.getProperties();

            if (props != null) {

                return props.getAnchor();

            }

        }

        return null;

    }


    private static String getPluginNameName(IPlugin plugin) {

        if ((plugin.getPluginInformation() != null) && (!Strings.isBlank(plugin.getPluginInformation().getName()))) {

            return plugin.getPluginInformation().getName();

        }

        return plugin.getClass().getName();

    }

}


