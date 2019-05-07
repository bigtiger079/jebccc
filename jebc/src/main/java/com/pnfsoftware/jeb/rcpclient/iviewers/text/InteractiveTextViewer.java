/*      */
package com.pnfsoftware.jeb.rcpclient.iviewers.text;
/*      */
/*      */

import com.pnfsoftware.jeb.client.Licensing;
/*      */ import com.pnfsoftware.jeb.client.S;
/*      */ import com.pnfsoftware.jeb.client.api.IOperable;
/*      */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*      */ import com.pnfsoftware.jeb.core.events.J;
/*      */ import com.pnfsoftware.jeb.core.output.text.IAnchor;
/*      */ import com.pnfsoftware.jeb.core.output.text.ICoordinates;
/*      */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*      */ import com.pnfsoftware.jeb.core.output.text.ITextDocument;
/*      */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*      */ import com.pnfsoftware.jeb.core.output.text.ITextItem;
/*      */ import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
/*      */ import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
/*      */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*      */ import com.pnfsoftware.jeb.core.units.IMetadataManager;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.AbstractRefresher;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.UI;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
/*      */ import com.pnfsoftware.jeb.rcpclient.extensions.ui.UITaskManager;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.hover.IHoverableProvider;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.hover.InteractiveTextHover;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend.IHoverableWidget;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationFactory;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationModelEx;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationRulerColumnEx;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationService;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.StandardAnnotationAccess;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.TextAnnotation;
/*      */ import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedText;
/*      */ import com.pnfsoftware.jeb.util.collect.ItemHistory;
/*      */ import com.pnfsoftware.jeb.util.events.IEvent;
/*      */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*      */ import com.pnfsoftware.jeb.util.format.Strings;
/*      */ import com.pnfsoftware.jeb.util.io.IO;
/*      */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*      */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*      */ import java.io.File;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.eclipse.jface.dialogs.MessageDialog;
/*      */ import org.eclipse.jface.text.Document;
/*      */ import org.eclipse.jface.text.IDocument;
/*      */ import org.eclipse.jface.text.ITextDoubleClickStrategy;
/*      */ import org.eclipse.jface.text.ITextInputListener;
/*      */ import org.eclipse.jface.text.ITextViewerExtension8.EnrichMode;
/*      */ import org.eclipse.jface.text.Position;
/*      */ import org.eclipse.jface.text.Region;
/*      */ import org.eclipse.jface.text.TextPresentation;
/*      */ import org.eclipse.jface.text.source.AnnotationPainter;
/*      */ import org.eclipse.jface.text.source.AnnotationPainter.BoxStrategy;
/*      */ import org.eclipse.jface.text.source.AnnotationPainter.HighlightingStrategy;
/*      */ import org.eclipse.jface.text.source.CompositeRuler;
/*      */ import org.eclipse.jface.text.source.ISourceViewer;
/*      */ import org.eclipse.jface.text.source.SourceViewer;
/*      */ import org.eclipse.jface.text.source.SourceViewerConfiguration;
/*      */ import org.eclipse.swt.custom.SashForm;
/*      */ import org.eclipse.swt.custom.StyleRange;
/*      */ import org.eclipse.swt.custom.StyledText;
/*      */ import org.eclipse.swt.custom.VerifyKeyListener;
/*      */ import org.eclipse.swt.graphics.Color;
/*      */ import org.eclipse.swt.graphics.Cursor;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.layout.FillLayout;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.FileDialog;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Shell;

/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */ public class InteractiveTextViewer
        /*      */ implements IOperable, INavigableViewer, ITextDocumentViewer
        /*      */ {
    /*   94 */   private static final ILogger logger = GlobalLog.getLogger(InteractiveTextViewer.class, Integer.MAX_VALUE);
    /*      */
    /*      */   public static final int FLAG_DISABLE_LINE_WRAPPING = 1;
    /*      */
    /*      */   public static final int FLAG_DISABLE_PART_VERIFICATION = 2;
    /*      */   private Display display;
    /*      */   private Listener mouseWheelFilter;
    /*      */   private IEventListener idocListener;
    /*      */   private Composite container;
    /*      */   private SourceViewer viewer;
    /*      */   private StyledText text;
    /*      */   private WrappedText wrappedText;
    /*      */   private CompositeRuler leftRuler;
    /*      */   private OverviewBar overviewBar;
    /*      */   private AnnotationModelEx annoModel;
    /*  109 */   private List<TextAnnotation> annotations = new ArrayList();
    /*      */   private AnnotationPainter annoPainter;
    /*      */   private boolean documentBeingChanged;
    /*      */   private FindTextOptions findOptions;
    /*  113 */   private ItemHistory<VisualPosition> positionHistory = new ItemHistory();
    /*      */
    /*      */   private int maxCharsPerLine;
    /*      */
    /*      */   private int charsEndLine;
    /*      */
    /*      */   private int charactersWrap;
    /*      */
    /*      */   private DocumentManager docManager;
    /*      */
    /*      */   private NavigationEventManager navigationEventManager;
    /*      */
    /*      */ ScrollBufferManager bufferManager;
    /*      */
    /*      */ IStyleProvider styleAdapter;
    /*      */
    /*      */   private boolean disablePartVerification;

    /*      */
    /*      */
    public InteractiveTextViewer(Composite parent, int flags, ITextDocument idoc, IPropertyManager propertyManager, IMetadataManager mm)
    /*      */ {
        /*  133 */
        this.display = parent.getDisplay();
        /*      */
        /*  135 */
        boolean showHorizontalScrollbar = propertyManager.getBoolean(".ui.text.ShowHorizontalScrollbar");
        /*  136 */
        boolean showVerticalScrollbar = propertyManager.getBoolean(".ui.text.ShowVerticalScrollbar");
        /*      */
        /*  138 */
        this.container = new Composite(parent, showVerticalScrollbar ? 512 : 0);
        /*  139 */
        this.container.setLayout(new FillLayout());
        /*      */
        /*  141 */
        this.maxCharsPerLine = propertyManager.getInteger(".ui.text.CharactersPerLineMax");
        /*  142 */
        this.charsEndLine = propertyManager.getInteger(".ui.text.CharactersPerLineAtEnd");
        /*  143 */
        this.charactersWrap = propertyManager.getInteger(".ui.text.CharactersWrap");
        /*  144 */
        if ((flags & 0x1) != 0) {
            /*  145 */
            this.charactersWrap = -1;
            /*      */
        }
        /*      */
        /*  148 */
        if ((flags & 0x2) != 0) {
            /*  149 */
            this.disablePartVerification = true;
            /*      */
        }
        /*      */
        /*  152 */
        boolean displayEolAtEod = propertyManager.getBoolean(".ui.text.DisplayEolAtEod");
        /*      */
        /*      */
        /*      */
        /*      */
        /*  157 */
        this.docManager = new DocumentManager(idoc, displayEolAtEod);
        /*  158 */
        this.docManager.setCharactersWrap(this.charactersWrap);
        /*      */
        /*      */
        /*      */
        /*  162 */
        OverviewBarProperties overviewBarProperties = OverviewBarProperties.buildOverviewBarProperties(propertyManager, idoc, mm);
        /*      */
        /*  164 */
        int orientation = (overviewBarProperties != null) && ((overviewBarProperties.position == 128) || (overviewBarProperties.position == 1024)) ? 512 : 256;
        /*      */
        /*      */
        /*  167 */
        SashForm container2 = new SashForm(this.container, orientation);
        /*      */
        /*      */
        /*  170 */
        if ((overviewBarProperties != null) && ((overviewBarProperties.position == 128) || (overviewBarProperties.position == 16384)))
            /*      */ {
            /*  172 */
            int overviewBarStyles = 0x800 | (overviewBarProperties.position == 128 ? 'Ā' : 'Ȁ');
            /*      */
            /*  174 */
            this.overviewBar = new OverviewBar(container2, overviewBarStyles, idoc, overviewBarProperties.mm);
            /*      */
        }
        /*      */
        /*      */
        /*  178 */
        this.annoModel = new AnnotationModelEx();
        /*  179 */
        this.leftRuler = new CompositeRuler();
        /*  180 */
        this.leftRuler.setModel(this.annoModel);
        /*  181 */
        AnnotationRulerColumnEx annoRulerColumn = new AnnotationRulerColumnEx(this.annoModel, 16, new StandardAnnotationAccess());
        /*      */
        /*  183 */
        this.leftRuler.addDecorator(0, annoRulerColumn);
        /*      */
        /*  185 */
        annoRulerColumn.addAnnotationType("com.pnfsoftware.jeb.rcpclient.textAnno");
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*  197 */
        this.viewer = new SourceViewer(container2, this.leftRuler, showHorizontalScrollbar ? 256 : 0);
        /*  198 */
        this.viewer.setEditable(false);
        /*  199 */
        annoRulerColumn.setViewer(this.viewer);
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*  207 */
        this.text = this.viewer.getTextWidget();
        /*  208 */
        this.text.setCursor(new Cursor(this.display, 0));
        /*      */
        /*      */
        /*  211 */
        this.text.setAlwaysShowScrollBars(true);
        /*      */
        /*  213 */
        this.wrappedText = new WrappedText(this.docManager, this.text);
        /*      */
        /*      */
        /*  216 */
        this.bufferManager = new ScrollBufferManager(this.wrappedText, this);
        /*      */
        /*  218 */
        this.navigationEventManager = new NavigationEventManager(this.wrappedText, this.bufferManager, this);
        /*  219 */
        this.navigationEventManager.setScrollLineSize(propertyManager.getInteger(".ui.text.ScrollLineSize"));
        /*  220 */
        this.navigationEventManager.setPageLineSize(propertyManager.getInteger(".ui.text.PageLineSize"));
        /*  221 */
        this.navigationEventManager.setPageMultiplier(propertyManager.getInteger(".ui.text.PageMultiplier"));
        /*  222 */
        this.navigationEventManager.setCaretBehaviorViewportStatic(propertyManager.getBoolean(".ui.text.CaretBehaviorViewportStatic"));
        /*      */
        /*  224 */
        this.navigationEventManager.setCancelSimpleMouseWheels(showHorizontalScrollbar);
        /*      */
        /*      */
        int overviewBarStyles;
        /*  227 */
        if ((overviewBarProperties != null) && ((overviewBarProperties.position == 1024) || (overviewBarProperties.position == 131072)))
            /*      */ {
            /*  229 */
            overviewBarStyles = 0x800 | (overviewBarProperties.position == 1024 ? 'Ā' : 'Ȁ');
            /*      */
            /*  231 */
            this.overviewBar = new OverviewBar(container2, overviewBarStyles, idoc, overviewBarProperties.mm);
            /*      */
        }
        /*      */
        /*      */
        /*  235 */
        if (overviewBarProperties != null) {
            /*  236 */
            if (overviewBarProperties.position == 128) {
                /*  237 */
                container2.setWeights(new int[]{5, 95});
                /*      */
            }
            /*  239 */
            else if (overviewBarProperties.position == 1024) {
                /*  240 */
                container2.setWeights(new int[]{95, 5});
                /*      */
            }
            /*  242 */
            else if (overviewBarProperties.position == 16384) {
                /*  243 */
                container2.setWeights(new int[]{2, 98});
                /*      */
            }
            /*  245 */
            else if (overviewBarProperties.position == 131072) {
                /*  246 */
                container2.setWeights(new int[]{98, 2});
                /*      */
            }
            /*      */
        }
        /*      */
        /*      */
        /*  251 */
        this.annoPainter = new AnnotationPainter(this.viewer, new StandardAnnotationAccess());
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*  259 */
        this.annoPainter.addTextStyleStrategy("strategyBox", new AnnotationPainter.BoxStrategy(1));
        /*  260 */
        this.annoPainter.addTextStyleStrategy("strategyHighlighting", new AnnotationPainter.HighlightingStrategy());
        /*      */
        /*  262 */
        for (AnnotationFactory f : AnnotationService.getInstance().getFactories()) {
            /*  263 */
            if (f.getHighlightingColor() != null) {
                /*  264 */
                this.annoPainter.addAnnotationType(f.getType(), "strategyHighlighting");
                /*  265 */
                this.annoPainter.setAnnotationTypeColor(f.getType(), f.getHighlightingColor());
                /*      */
            }
            /*      */
        }
        /*  268 */
        this.viewer.addTextPresentationListener(this.annoPainter);
        /*  269 */
        this.viewer.addPainter(this.annoPainter);
        /*      */
        /*      */
        /*  272 */
        this.viewer.configure(new SourceViewerConfiguration()
                /*      */ {
            /*      */
            public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
                /*  275 */
                return null;
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
            }
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*  290 */
        });
        /*  291 */
        this.navigationEventManager.addNavigationHandlers();
        /*      */
        /*      */
        /*  294 */
        this.viewer.addTextInputListener(new ITextInputListener()
                /*      */ {
            /*      */
            public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
                /*  297 */
                if (newInput == null) {
                    /*  298 */
                    InteractiveTextViewer.logger.debug("inputDocumentChanged: newinput is null", new Object[0]);
                    /*  299 */
                    return;
                    /*      */
                }
                /*      */
                /*  302 */
                InteractiveTextViewer.logger.debug("inputDocumentChanged: linecount=%s", new Object[]{Integer.valueOf(newInput.getNumberOfLines())});
                /*  303 */
                InteractiveTextViewer.this.documentBeingChanged = false;
                /*      */
            }

            /*      */
            /*      */
            /*      */
            public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput)
            /*      */ {
                /*  309 */
                InteractiveTextViewer.this.documentBeingChanged = true;
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
                /*      */
            }
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*  330 */
        });
        /*  331 */
        final AbstractRefresher refresher = new AbstractRefresher(this.display, "IntText")
                /*      */ {
            /*      */
            protected void performRefresh() {
                /*  334 */
                InteractiveTextViewer.this.onUnitChange();
                /*      */
            }
            /*  336 */
        };
        /*  337 */
        idoc.addListener(this. = new IEventListener()
                /*      */ {
            /*      */
            public void onEvent(IEvent e) {
                /*  340 */
                if (e.getType() == J.UnitChange) {
                    /*  341 */
                    refresher.request();
                    /*      */
                }
                /*      */
            }
            /*      */
        });
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void setHoverText(IHoverableProvider provider, IHoverableWidget iHoverableWidget)
    /*      */ {
        /*  361 */
        InteractiveTextHover iHover = new InteractiveTextHover(provider, iHoverableWidget);
        /*  362 */
        this.viewer.setTextHover(iHover, "__dftl_partition_content_type");
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*      */
        /*  369 */
        this.viewer.setHoverEnrichMode(ITextViewerExtension8.EnrichMode.IMMEDIATELY);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void setHoverText(IHoverableProvider provider)
    /*      */ {
        /*  377 */
        setHoverText(provider, null);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    public void initialize(boolean resetCaret)
    /*      */ {
        /*  384 */
        long initAnchor = this.docManager.getDocument().getInitialAnchor();
        /*  385 */
        this.bufferManager.viewAtAnchor(initAnchor, !resetCaret);
        /*      */
        /*      */
        /*      */
        /*      */
        /*  390 */
        if (this.overviewBar != null) {
            /*  391 */
            this.overviewBar.connectToViewer(this);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    public void dispose()
    /*      */ {
        /*  397 */
        this.viewer.removeTextHovers("__dftl_partition_content_type");
        /*      */
        /*  399 */
        if (this.mouseWheelFilter != null) {
            /*  400 */
            this.display.removeFilter(37, this.mouseWheelFilter);
            /*  401 */
            this.mouseWheelFilter = null;
            /*      */
        }
        /*      */
        /*  404 */
        if (this.idocListener != null) {
            /*  405 */
            this.docManager.getDocument().removeListener(this.idocListener);
            /*  406 */
            this.idocListener = null;
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    public ITextDocument getDocument()
    /*      */ {
        /*  412 */
        return this.docManager.getDocument();
        /*      */
    }

    /*      */
    /*      */   WrappedText getWrappedText() {
        /*  416 */
        return this.wrappedText;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public StyledText getTextWidget()
    /*      */ {
        /*  427 */
        return this.text;
        /*      */
    }

    /*      */
    /*      */
    public Composite getWidget()
    /*      */ {
        /*  432 */
        return this.container;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    public Point computeIdealSize()
    /*      */ {
        /*  438 */
        Point p = this.text.computeSize(-1, -1, true);
        /*      */
        /*  440 */
        int w = p.x;
        /*  441 */
        if (this.leftRuler != null) {
            /*  442 */
            w += this.leftRuler.getControl().computeSize(-1, -1).x;
            /*      */
        }
        /*  444 */
        if (this.overviewBar != null) {
            /*  445 */
            w += this.overviewBar.computeSize(-1, -1).x;
            /*      */
        }
        /*      */
        /*  448 */
        int h = p.y;
        /*      */
        /*  450 */
        return new Point(w, h);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public ITextDocument getTextDocument()
    /*      */ {
        /*  459 */
        return this.docManager.getDocument();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public boolean isDisposed()
    /*      */ {
        /*  468 */
        return this.text.isDisposed();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void setFont(Font font)
    /*      */ {
        /*  478 */
        this.text.setFont(font);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public Font getFont()
    /*      */ {
        /*  488 */
        return this.text.getFont();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public ICoordinates getCaretCoordinates()
    /*      */ {
        /*  499 */
        return this.wrappedText.getCaretCoordinates();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public boolean setCaretCoordinates(ICoordinates coord, BufferPoint viewportPoint, boolean record)
    /*      */ {
        /*  514 */
        return setVisualPosition(new VisualPosition(coord, viewportPoint), record);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public VisualPosition getVisualPosition()
    /*      */ {
        /*  523 */
        return new VisualPosition(this.wrappedText.getCaretCoordinates(), this.wrappedText.getCaretViewportPoint());
        /*      */
    }

    /*      */
    /*      */
    public BufferPoint getCaretViewportPoint()
    /*      */ {
        /*  528 */
        return this.wrappedText.getCaretViewportPoint();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public boolean setVisualPosition(VisualPosition pos, boolean record)
    /*      */ {
        /*  541 */
        ICoordinates coord = pos.docCoord;
        /*  542 */
        if (coord == null) {
            /*  543 */
            return false;
            /*      */
        }
        /*      */
        /*  546 */
        VisualPosition pos0 = getVisualPosition();
        /*      */
        /*  548 */
        boolean success = this.bufferManager.setVisualPosition(pos);
        /*  549 */
        if ((success) && (record)) {
            /*  550 */
            this.positionHistory.add(pos0);
            /*      */
        }
        /*      */
        /*  553 */
        return success;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public ITextItem getItemAt(int opaqueOffset)
    /*      */ {
        /*  566 */
        BufferPoint p = this.wrappedText.unwrap(opaqueOffset);
        /*  567 */
        if (p == null) {
            /*  568 */
            return null;
            /*      */
        }
        /*      */
        /*  571 */
        int lineIndex = p.lineIndex;
        /*  572 */
        int columnOffset = p.columnOffset;
        /*      */
        /*  574 */
        List<? extends ILine> lines = this.docManager.getCurrentPart().getLines();
        /*  575 */
        if (lineIndex >= lines.size()) {
            /*  576 */
            return null;
            /*      */
        }
        /*      */
        /*  579 */
        return TextPartUtil.getItemAt((ILine) lines.get(lineIndex), columnOffset);
        /*      */
    }

    /*      */
    /*      */
    public List<ITextItem> getCurrentItems()
    /*      */ {
        /*  584 */
        if (this.docManager.getCurrentPart() == null) {
            /*  585 */
            return Collections.emptyList();
            /*      */
        }
        /*  587 */
        return TextPartUtil.getItems(this.docManager.getCurrentPart());
        /*      */
    }

    /*      */
    /*      */
    private ILine getLineAt(BufferPoint p) {
        /*  591 */
        if (p == null) {
            /*  592 */
            return null;
            /*      */
        }
        /*      */
        /*  595 */
        int lineIndex = p.lineIndex;
        /*      */
        /*  597 */
        List<? extends ILine> lines = this.docManager.getCurrentPart().getLines();
        /*  598 */
        if (lineIndex >= lines.size()) {
            /*  599 */
            return null;
            /*      */
        }
        /*      */
        /*  602 */
        return (ILine) lines.get(lineIndex);
        /*      */
    }

    /*      */
    /*      */
    private void onUnitChange() {
        /*  606 */
        if (this.text.isDisposed()) {
            /*  607 */
            return;
            /*      */
        }
        /*      */
        /*      */
        /*  611 */
        ICoordinates caretCoords = getCaretCoordinates();
        /*  612 */
        int bufOff = 0;
        /*  613 */
        if (caretCoords != null) {
            /*  614 */
            bufOff = this.bufferManager.getBufferOffset(caretCoords, false);
            /*      */
        }
        /*  616 */
        UnwrappedBufferPoint unwrappedPoint = this.wrappedText.unwrap(bufOff);
        /*  617 */
        ILine line = getLineAt(unwrappedPoint);
        /*  618 */
        List<? extends ITextItem> items = null;
        /*  619 */
        if (line != null) {
            /*  620 */
            items = line.getItems();
            /*      */
        }
        /*  622 */
        else if (Licensing.isDebugBuild())
            /*      */ {
            /*  624 */
            throw new RuntimeException("DEBUG: unexpected null line");
            /*      */
        }
        /*      */
        /*      */
        /*  628 */
        refresh();
        /*      */
        /*      */
        /*  631 */
        if (line == null) {
            /*  632 */
            return;
            /*      */
        }
        /*      */
        /*      */
        /*      */
        /*  637 */
        if (caretCoords == null)
            /*      */ {
            /*  639 */
            IAnchor lastAnchor = this.docManager.getAnchorById(this.docManager.getAnchorEnd());
            /*  640 */
            if (lastAnchor == null) {
                /*  641 */
                return;
                /*      */
            }
            /*  643 */
            int lineDelta = Math.max(0, this.wrappedText.getLineCount() - lastAnchor.getLineIndex() - 1);
            /*  644 */
            caretCoords = new Coordinates(this.docManager.getAnchorEnd(), lineDelta);
            /*      */
        }
        /*  646 */
        ICoordinates newCoords = getCaretCoordinates();
        /*      */
        /*      */
        ILine newLine;
        /*  649 */
        if (newCoords != null) {
            /*  650 */
            int newBufOff = this.bufferManager.getBufferOffset(newCoords, false);
            /*  651 */
            ILine newLine = getLineAt(this.wrappedText.unwrap(newBufOff));
            /*  652 */
            if (!line.getText().toString().equals(newLine.getText().toString())) {
            }
            /*      */
            /*      */
        }
        /*      */
        else
            /*      */ {
            /*      */
            /*  658 */
            newCoords = new Coordinates(caretCoords.getAnchorId(), caretCoords.getLineDelta(), 0);
            /*  659 */
            int newBufOff = this.bufferManager.getBufferOffset(newCoords, false);
            /*  660 */
            if ((newBufOff >= this.wrappedText.getCharCount()) || (newBufOff < 0))
                /*      */ {
                /*  662 */
                IAnchor currentAnchor = this.docManager.getAnchorById(caretCoords.getAnchorId());
                /*  663 */
                if (currentAnchor == null)
                    /*      */ {
                    /*  665 */
                    if ((caretCoords.getAnchorId() < TextPartUtil.getLastAnchorId(this.docManager.getCurrentPart())) &&
                            /*  666 */             (caretCoords.getAnchorId() > TextPartUtil.getFirstAnchorId(this.docManager.getCurrentPart())))
                        /*      */ {
                        /*  668 */
                        IAnchor a = TextPartUtil.getApproximateAnchorById(this.docManager.getCurrentPart(), caretCoords
/*  669 */.getAnchorId(), 1);
                        /*  670 */
                        String lineContent = this.wrappedText.getLine(a.getLineIndex());
                        /*      */
                        /*  672 */
                        int columnOffset = caretCoords.getColumnOffset() > lineContent.length() ? lineContent.length() : caretCoords.getColumnOffset();
                        /*  673 */
                        newCoords = new Coordinates(a.getIdentifier(), 0, columnOffset);
                        /*  674 */
                        setCaretCoordinates(newCoords, null, false);
                        /*      */
                    }
                    /*      */
                    else
                        /*      */ {
                        /*  678 */
                        this.wrappedText.setCaretOffset(this.wrappedText.getOffsetAtTopLine());
                        /*      */
                    }
                    /*  680 */
                    return;
                    /*      */
                }
                /*  682 */
                int anchorStartLine = currentAnchor.getLineIndex();
                /*  683 */
                newCoords = new Coordinates(caretCoords.getAnchorId(), this.wrappedText.getLineCount() - (anchorStartLine + 1), 0);
                /*  684 */
                setCaretCoordinates(newCoords, null, false);
                /*  685 */
                return;
                /*      */
            }
            /*  687 */
            newLine = getLineAt(this.wrappedText.unwrap(newBufOff));
            /*  688 */
            if (line.getText().toString().equals(newLine.getText().toString()))
                /*      */ {
                /*  690 */
                setCaretCoordinates(caretCoords, null, false);
                /*  691 */
                return;
                /*      */
            }
            /*      */
        }
        /*      */
        /*  695 */
        restoreCaretAtPreviousItem(caretCoords, items, newLine);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    private void restoreCaretAtPreviousItem(ICoordinates caretCoords, List<? extends ITextItem> items, ILine newLine)
    /*      */ {
        /*  706 */
        List<? extends ITextItem> newItems = newLine.getItems();
        /*      */
        /*      */
        /*      */
        /*  710 */
        int caretInItem = -1;
        /*  711 */
        int caretBeforeItem = -1;
        /*  712 */
        int caretPosition = caretCoords.getColumnOffset();
        /*  713 */
        for (int i = 0; i < items.size(); i++) {
            /*  714 */
            ITextItem item = (ITextItem) items.get(i);
            /*  715 */
            if (caretPosition < item.getOffset())
                /*      */ {
                /*  717 */
                caretBeforeItem = i;
                /*  718 */
                break;
                /*      */
            }
            /*  720 */
            if (caretPosition < item.getOffset() + item.getLength()) {
                /*  721 */
                caretInItem = i;
                /*  722 */
                break;
                /*      */
            }
            /*      */
        }
        /*  725 */
        if (caretInItem != -1)
            /*      */ {
            /*  727 */
            if (caretInItem >= newItems.size())
                /*      */ {
                /*  729 */
                caretPosition = ((ITextItem) items.get(caretInItem)).getOffset();
                /*      */
            }
            /*      */
            else
                /*      */ {
                /*  733 */
                ITextItem item = (ITextItem) newItems.get(caretInItem);
                /*  734 */
                caretPosition += item.getOffset() - ((ITextItem) items.get(caretInItem)).getOffset();
                /*  735 */
                if (caretPosition >= item.getOffset() + item.getLength())
                    /*      */ {
                    /*  737 */
                    caretPosition = item.getOffset() + item.getLength() - 1;
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /*  741 */
        else if (items.size() == newItems.size())
            /*      */ {
            /*      */
            /*      */
            /*      */
            /*  746 */
            if (caretBeforeItem == -1)
                /*      */ {
                /*  748 */
                if (items.size() != 0)
                    /*      */ {
                    /*      */
                    /*      */
                    /*      */
                    /*  753 */
                    ITextItem item = (ITextItem) newItems.get(newItems.size() - 1);
                    /*  754 */
                    ITextItem oldItem = (ITextItem) items.get(items.size() - 1);
                    /*      */
                    /*  756 */
                    caretPosition = caretPosition + (item.getOffset() + item.getLength() - (oldItem.getOffset() + oldItem.getLength()));
                    /*      */
                }
                /*      */
            }
            /*  759 */
            else if (caretBeforeItem != 0)
                /*      */ {
                /*      */
                /*      */
                /*  763 */
                ITextItem item = (ITextItem) newItems.get(caretBeforeItem - 1);
                /*  764 */
                ITextItem oldItem = (ITextItem) items.get(caretBeforeItem - 1);
                /*  765 */
                caretPosition += item.getOffset() + item.getLength() - (oldItem.getOffset() + oldItem.getLength());
                /*      */
            }
            /*      */
        }
        /*  768 */
        if (caretPosition > newLine.getText().length())
            /*      */ {
            /*  770 */
            caretPosition = newLine.getText().length();
            /*      */
        }
        /*  772 */
        Coordinates newCoords = new Coordinates(caretCoords.getAnchorId(), caretCoords.getLineDelta(), caretPosition);
        /*  773 */
        setCaretCoordinates(newCoords, null, false);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void registerAnnotation(TextAnnotation annotation)
    /*      */ {
        /*  782 */
        this.annotations.add(annotation);
        /*  783 */
        applyAnnotation(annotation);
        /*      */
    }

    /*      */
    /*      */
    public void unregisterAnnotation(TextAnnotation annotation) {
        /*  787 */
        unapplyAnnotation(annotation);
        /*  788 */
        this.annotations.remove(annotation);
        /*      */
    }

    /*      */
    /*      */
    public void unregisterAnnotations()
    /*      */ {
        /*  793 */
        unapplyAnnotations();
        /*  794 */
        this.annotations.clear();
        /*      */
    }

    /*      */
    /*      */
    private void applyAnnotation(TextAnnotation annotation) {
        /*  798 */
        ICoordinates coord = annotation.getCoordinates();
        /*  799 */
        int lineIndex = TextPartUtil.coordinatesToLineIndex(this.docManager.getCurrentPart(), coord);
        /*  800 */
        if (lineIndex >= 0) {
            /*  801 */
            int wrappedLineIndex = this.docManager.wrapLine(lineIndex);
            /*  802 */
            if (wrappedLineIndex >= 0) {
                /*  803 */
                int offset = this.wrappedText.getOffsetAtLine(wrappedLineIndex);
                /*  804 */
                int length = this.wrappedText.getLine(wrappedLineIndex).length();
                /*  805 */
                Position pos = new Position(offset, length);
                /*  806 */
                this.annoModel.addAnnotation(annotation, pos);
                /*      */
            }
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    private void unapplyAnnotation(TextAnnotation annotation) {
        /*  812 */
        this.annoModel.removeAnnotation(annotation);
        /*      */
    }

    /*      */
    /*      */
    private void reapplyAnnotations() {
        /*  816 */
        unapplyAnnotations();
        /*  817 */
        applyAnnotations();
        /*      */
    }

    /*      */
    /*      */
    private void applyAnnotations() {
        /*  821 */
        for (TextAnnotation annotation : this.annotations) {
            /*  822 */
            applyAnnotation(annotation);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    private void unapplyAnnotations() {
        /*  827 */
        for (TextAnnotation annotation : this.annotations) {
            /*  828 */
            unapplyAnnotation(annotation);
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public boolean updateDocument(long anchorId, int linesAfter, int linesBefore)
    /*      */ {
        /*  846 */
        logger.i("updateDocument(): anchorId=%d/%Xh, after=%d, before=%d", new Object[]{Long.valueOf(anchorId), Long.valueOf(anchorId), Integer.valueOf(linesAfter), Integer.valueOf(linesBefore)});
        /*  847 */
        if (linesAfter < 0) {
            /*  848 */
            linesAfter = 0;
            /*      */
        }
        /*      */
        /*  851 */
        if ((linesBefore < 0) || (anchorId == this.docManager.getAnchorFirst())) {
            /*  852 */
            linesBefore = 0;
            /*      */
        }
        /*      */
        /*  855 */
        if ((anchorId < this.docManager.getAnchorFirst()) || (anchorId >= this.docManager.getAnchorEnd())) {
            /*  856 */
            return false;
            /*      */
        }
        /*      */
        /*  859 */
        long t0 = System.currentTimeMillis();
        /*  860 */
        ITextDocumentPart newPart = this.docManager.getPart(anchorId, linesAfter, linesBefore);
        /*  861 */
        long t1 = System.currentTimeMillis();
        /*  862 */
        logger.debug("Part at anchor %d retrieved in %dms", new Object[]{Long.valueOf(anchorId), Long.valueOf(t1 - t0)});
        /*  863 */
        if ((newPart == null) || (newPart.getAnchors() == null) || (newPart.getAnchors().isEmpty())) {
            /*  864 */
            logger.debug("Invalid part", new Object[0]);
            /*  865 */
            if (this.docManager.getCurrentPart() == null) {
                /*  866 */
                throw new RuntimeException("No text part can be fetched, bad document output generation.");
                /*      */
            }
            /*  868 */
            return false;
            /*      */
        }
        /*      */
        /*      */
        /*  872 */
        if ((this.charactersWrap < 0) &&
                /*  873 */       (this.maxCharsPerLine >= 10) && (this.maxCharsPerLine - this.charsEndLine > 5)) {
            /*  874 */
            for (int i = 0; i < newPart.getLines().size(); i++) {
                /*  875 */
                ILine line = (ILine) newPart.getLines().get(i);
                /*  876 */
                if (line.getText().length() > this.maxCharsPerLine)
                    /*      */ {
                    /*  878 */
                    newPart = new TextDocumentPartDelegate(newPart, this.maxCharsPerLine, this.charsEndLine);
                    /*  879 */
                    break;
                    /*      */
                }
                /*      */
            }
            /*      */
        }
        /*      */
        /*      */
        /*  885 */
        if (!this.disablePartVerification) {
            /*  886 */
            TextPartUtil.verifyPart(newPart);
            /*      */
        }
        /*  888 */
        this.docManager.setCurrentPart(newPart);
        /*      */
        /*  890 */
        String docContent = this.docManager.getText();
        /*  891 */
        this.bufferManager.updateDocPart();
        /*  892 */
        IDocument doc = new Document(docContent);
        /*  893 */
        TextPresentation pres = buildStyles(doc);
        /*      */
        /*  895 */
        this.viewer.setDocument(doc, this.annoModel);
        /*  896 */
        this.viewer.changeTextPresentation(pres, true);
        /*  897 */
        reapplyAnnotations();
        /*  898 */
        long t2 = System.currentTimeMillis();
        /*  899 */
        logger.debug("updateDocument took %dms", new Object[]{Long.valueOf(t2 - t0)});
        /*      */
        /*  901 */
        return true;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    public void updateAnnotationBar()
    /*      */ {
        /*  908 */
        this.leftRuler.update();
        /*      */
    }

    /*      */
    /*      */
    private TextPresentation buildStyles(IDocument doc) {
        /*  912 */
        if ((this.styleAdapter == null) || (doc == null)) {
            /*  913 */
            return null;
            /*      */
        }
        /*      */
        /*  916 */
        TextPresentation pres = new TextPresentation(new Region(0, doc.getLength()), 1);
        /*  917 */
        for (DocumentManager.RenderedItem renderedItem : this.docManager.getRenderedItems()) {
            /*  918 */
            ITextItem item = renderedItem.item;
            /*  919 */
            Style style = this.styleAdapter.getStyle(item);
            /*  920 */
            if (style != null)
                /*  921 */ if (item.getLength() <= 0) {
                /*  922 */
                logger.error("Trying to apply a void style at offset %d", new Object[]{Integer.valueOf(renderedItem.offset)});
                /*      */
                /*      */
                /*      */
            }
            /*  926 */
            else if (item.getOffset() < 0) {
                /*  927 */
                logger.error("Trying to apply a style at negative offset %d", new Object[]{Integer.valueOf(renderedItem.offset)});
                /*      */
            }
            /*      */
            else
                /*      */ {
                /*  931 */
                int length = item.getLength();
                /*  932 */
                if (renderedItem.offset + length > doc.getLength()) {
                    /*  933 */
                    if (renderedItem.offset > doc.getLength())
                        /*      */ {
                        /*  935 */
                        logger.error("Trying to apply a style at item not in document at offset %d", new Object[]{
/*  936 */                 Integer.valueOf(renderedItem.offset)});
                        /*      */
                        /*      */
                    }
                    /*      */
                    else
                        /*      */ {
                        /*  941 */
                        length = doc.getLength() - renderedItem.offset;
                        /*      */
                    }
                    /*      */
                } else {
                    /*  944 */
                    StyleRange sr = createStyleRange(renderedItem.offset, length, style);
                    /*  945 */
                    pres.addStyleRange(sr);
                    /*      */
                }
                /*      */
            }
        }
        /*  948 */
        return pres;
        /*      */
    }

    /*      */
    /*      */
    private StyleRange createStyleRange(int start, int length, Style style) {
        /*  952 */
        int flags = 0;
        /*  953 */
        if (style.isBold()) {
            /*  954 */
            flags |= 0x1;
            /*      */
        }
        /*  956 */
        if (style.isItalic()) {
            /*  957 */
            flags |= 0x2;
            /*      */
        }
        /*      */
        /*  960 */
        return new StyleRange(start, length, style.getColor(), style.getBackgroungColor(), flags);
        /*      */
    }

    /*      */
    /*      */
    public void setStyleAdapter(IStyleProvider styleAdapter)
    /*      */ {
        /*  965 */
        if (this.documentBeingChanged) {
            /*  966 */
            return;
            /*      */
        }
        /*      */
        /*  969 */
        this.styleAdapter = styleAdapter;
        /*      */
    }

    /*      */
    /*      */
    public IStyleProvider getStyleAdapter()
    /*      */ {
        /*  974 */
        return this.styleAdapter;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void refreshStyles()
    /*      */ {
        /*  997 */
        if (this.documentBeingChanged) {
            /*  998 */
            return;
            /*      */
        }
        /*      */
        /* 1001 */
        TextPresentation pres = buildStyles(this.viewer.getDocument());
        /* 1002 */
        this.viewer.changeTextPresentation(pres, true);
        /*      */
    }

    /*      */
    /* 1005 */   private int activeLineIndex = -1;
    /*      */   private long findAnchorId;

    /*      */
    /*      */
    public void activateCurrentLine(boolean active) {
        /* 1009 */
        if (this.styleAdapter == null) {
            /* 1010 */
            return;
            /*      */
        }
        /* 1012 */
        Color bgcol = this.styleAdapter.getOnCaretBackgroundColor();
        /*      */
        /* 1014 */
        int lineindex = this.wrappedText.getCaretLine();
        /*      */
        /*      */
        /* 1017 */
        if ((this.activeLineIndex >= 0) && (this.activeLineIndex < this.wrappedText.getLineCount())) {
            /* 1018 */
            this.text.setLineBackground(this.activeLineIndex, 1, null);
            /*      */
        }
        /*      */
        /* 1021 */
        if ((active) && (lineindex >= 0) && (lineindex < this.wrappedText.getLineCount())) {
            /* 1022 */
            this.text.setLineBackground(lineindex, 1, bgcol);
            /* 1023 */
            this.activeLineIndex = lineindex;
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void refresh()
    /*      */ {
        /* 1033 */
        if (this.documentBeingChanged) {
            /* 1034 */
            return;
            /*      */
        }
        /*      */
        /* 1037 */
        this.bufferManager.forceRefresh();
        /*      */
        /* 1039 */
        if (this.overviewBar != null) {
            /* 1040 */
            this.overviewBar.refresh();
            /*      */
        }
        /*      */
    }

    /*      */
    /*      */
    public void resetSelection()
    /*      */ {
        /* 1046 */
        this.wrappedText.resetSelection();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public void addItemListener(IItemListener listener)
    /*      */ {
        /* 1055 */
        this.navigationEventManager.addItemListener(listener);
        /*      */
    }

    /*      */
    /*      */
    public void removeItemListener(IItemListener listener)
    /*      */ {
        /* 1060 */
        this.navigationEventManager.removeItemListener(listener);
        /*      */
    }

    /*      */
    /*      */
    public void addPositionListener(IPositionListener listener)
    /*      */ {
        /* 1065 */
        this.navigationEventManager.addPositionListener(listener);
        /*      */
    }

    /*      */
    /*      */
    public void removePositionListener(IPositionListener listener)
    /*      */ {
        /* 1070 */
        this.navigationEventManager.removePositionListener(listener);
        /*      */
    }

    /*      */
    /*      */
    public void addUnhandkedVerifyKeyListener(VerifyKeyListener listener)
    /*      */ {
        /* 1075 */
        this.navigationEventManager.addUnhandledVerifyKeyListener(listener);
        /*      */
    }

    /*      */
    /*      */
    public void removeUnhandledVerifyKeyListener(VerifyKeyListener listener)
    /*      */ {
        /* 1080 */
        this.navigationEventManager.removeUnhandledVerifyKeyListener(listener);
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public ITextDocumentPart getCurrentDocumentPart()
    /*      */ {
        /* 1090 */
        return this.docManager.getCurrentPart();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */   private int findLineDelta;
    /*      */
    /*      */
    /*      */   private int findColumnOffset;

    /*      */
    /*      */
    /*      */
    public boolean supportReverseSearch()
    /*      */ {
        /* 1103 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    public void resetFindTextOptions()
    /*      */ {
        /* 1108 */
        this.findOptions = null;
        /* 1109 */
        this.findAnchorId = this.docManager.getAnchorFirst();
        /* 1110 */
        this.findLineDelta = 0;
        /* 1111 */
        this.findColumnOffset = 0;
        /*      */
    }

    /*      */
    /*      */
    public void setFindTextOptions(FindTextOptions options)
    /*      */ {
        /* 1116 */
        this.findOptions = options;
        /*      */
    }

    /*      */
    /*      */
    public FindTextOptions getFindTextOptions(boolean update)
    /*      */ {
        /* 1121 */
        if (this.findOptions == null) {
            /* 1122 */
            this.findOptions = new FindTextOptions("");
            /*      */
        }
        /* 1124 */
        if (update) {
            /* 1125 */
            ICoordinates c = getCaretCoordinates();
            /* 1126 */
            if (c != null) {
                /* 1127 */
                this.findAnchorId = c.getAnchorId();
                /* 1128 */
                this.findLineDelta = c.getLineDelta();
                /* 1129 */
                this.findColumnOffset = c.getColumnOffset();
                /*      */
            }
            /*      */
        }
        /* 1132 */
        return this.findOptions;
        /*      */
    }

    /*      */
    /*      */
    public InteractiveTextFindResult findText(FindTextOptions optionsOverride)
    /*      */ {
        /* 1137 */
        FindTextOptions options = optionsOverride != null ? optionsOverride : this.findOptions;
        /* 1138 */
        if ((options == null) || (options.getSearchString() == null) || (options.getSearchString().isEmpty())) {
            /* 1139 */
            return null;
            /*      */
        }
        /* 1141 */
        boolean wrappedAround = false;
        /*      */
        /* 1143 */
        logger.debug(S.s(715), new Object[]{options.getSearchString()});
        /*      */
        /* 1145 */
        int searchStringLength = options.getSearchString().length();
        /*      */
        /* 1147 */
        Pattern p = null;
        /* 1148 */
        if (options.isRegularExpression()) {
            /*      */
            try {
                /* 1150 */
                p = Pattern.compile(options.getSearchString(), options.isCaseSensitive() ? 0 : 2);
                /*      */
            }
            /*      */ catch (IllegalArgumentException e) {
                /* 1153 */
                logger.error("Invalid regex, will consider it a standard string: %s", new Object[]{e});
                /*      */
            }
            /*      */
        }
        /*      */
        /*      */
        /*      */
        /*      */
        for (; ; )
            /*      */ {
            /* 1161 */
            long anchorId = this.findAnchorId;
            /* 1162 */
            int lineDelta = this.findLineDelta;
            /* 1163 */
            int columnOffset = this.findColumnOffset;
            /* 1164 */
            Coordinates startCoords = null;
            /* 1165 */
            Coordinates endCoords = null;
            /*      */
            /* 1167 */
            ITextDocumentPart part = this.docManager.getCurrentPart();
            /* 1168 */
            int pos = -1;
            /*      */
            /*      */
            /* 1171 */
            while (pos < 0)
                /*      */ {
                /* 1173 */
                if (Thread.interrupted()) {
                    /* 1174 */
                    this.findAnchorId = anchorId;
                    /* 1175 */
                    this.findLineDelta = 0;
                    /* 1176 */
                    this.findColumnOffset = 0;
                    /* 1177 */
                    return null;
                    /*      */
                }
                /*      */
                /* 1180 */
                if (part != null) {
                    /* 1181 */
                    long firstAnchorId = TextPartUtil.getFirstAnchorId(part);
                    /* 1182 */
                    long nextAnchorId = TextPartUtil.getNextAnchorId(part);
                    /* 1183 */
                    if ((anchorId < firstAnchorId) || (anchorId >= nextAnchorId)) {
                        /* 1184 */
                        logger.i("Requested anchor out of current part: %Xh not in [%Xh, %Xh)", new Object[]{Long.valueOf(anchorId), Long.valueOf(firstAnchorId),
/* 1185 */               Long.valueOf(nextAnchorId)});
                        /* 1186 */
                        part = null;
                        /*      */
                    }
                    /*      */
                }
                /*      */
                /* 1190 */
                if (part == null)
                    /*      */ {
                    /* 1192 */
                    part = this.docManager.getPart(anchorId, 500);
                    /*      */
                }
                /*      */
                /* 1195 */
                IAnchor anchor = TextPartUtil.getAnchorById(part, anchorId);
                /* 1196 */
                if (anchor == null) {
                    /* 1197 */
                    anchor = TextPartUtil.getApproximateAnchorById(part, anchorId, 1);
                    /* 1198 */
                    if (anchor == null) {
                        /* 1199 */
                        logger.i("Error on part request @ %Xh (end: %Xh)", new Object[]{Long.valueOf(anchorId), Long.valueOf(this.docManager.getAnchorEnd())});
                        /* 1200 */
                        break;
                        /*      */
                    }
                    /*      */
                }
                /* 1203 */
                logger.i("Searching part @ %Xh (got %Xh)", new Object[]{Long.valueOf(anchorId), Long.valueOf(anchor.getIdentifier())});
                /*      */
                /* 1205 */
                int index = anchor.getLineIndex() + lineDelta;
                /* 1206 */
                List<? extends ILine> lines = part.getLines();
                /* 1207 */
                for (; index < lines.size(); index++) {
                    /* 1208 */
                    String lineText = ((ILine) lines.get(index)).getText().toString();
                    /* 1209 */
                    if (!lineText.isEmpty())
                        /*      */ {
                        /*      */
                        /*      */
                        /* 1213 */
                        if (p != null) {
                            /* 1214 */
                            Matcher m = p.matcher(lineText);
                            /* 1215 */
                            if (m.find(columnOffset)) {
                                /* 1216 */
                                pos = m.start();
                                /* 1217 */
                                searchStringLength = m.end() - pos;
                                /* 1218 */
                                break;
                                /*      */
                            }
                            /*      */
                        }
                        /*      */
                        else
                            /*      */ {
                            /* 1223 */
                            pos = Strings.search(lineText, columnOffset, options.getSearchString(), false, options.isCaseSensitive(), options.isReverseSearch());
                            /* 1224 */
                            if (pos >= 0) {
                                /*      */
                                break;
                                /*      */
                            }
                            /*      */
                        }
                        /*      */
                        /* 1229 */
                        columnOffset = 0;
                        /*      */
                    }
                    /*      */
                }
                /* 1232 */
                if (pos >= 0) {
                    /* 1233 */
                    anchor = TextPartUtil.getAnchorAtLine(part, index);
                    /* 1234 */
                    anchorId = anchor.getIdentifier();
                    /* 1235 */
                    lineDelta = index - anchor.getLineIndex();
                    /* 1236 */
                    startCoords = new Coordinates(anchorId, lineDelta, pos);
                    /* 1237 */
                    endCoords = new Coordinates(anchorId, lineDelta, pos + searchStringLength);
                    /*      */
                }
                /*      */
                else
                    /*      */ {
                    /* 1241 */
                    lineDelta = 0;
                    /* 1242 */
                    anchorId = TextPartUtil.getNextAnchorId(part);
                    /*      */
                    /* 1244 */
                    if ((anchorId < this.docManager.getAnchorFirst()) || (anchorId >= this.docManager.getAnchorEnd() - 1L)) {
                        /*      */
                        break;
                        /*      */
                    }
                    /*      */
                }
                /*      */
            }
            /*      */
            /*      */
            /* 1251 */
            if (pos < 0) {
                /* 1252 */
                if ((!options.isWrapAround()) ||
                        /* 1253 */           (wrappedAround)) {
                    /*      */
                    break;
                    /*      */
                }
                /*      */
                /*      */
                /*      */
                /* 1259 */
                wrappedAround = true;
                /*      */
                /* 1261 */
                this.findAnchorId = this.docManager.getAnchorFirst();
                /* 1262 */
                this.findLineDelta = 0;
                /* 1263 */
                this.findColumnOffset = 0;
                /*      */
                /*      */
                /*      */
                /*      */
            }
            /*      */
            else
                /*      */ {
                /*      */
                /*      */
                /* 1272 */
                this.findAnchorId = anchorId;
                /* 1273 */
                this.findLineDelta = lineDelta;
                /* 1274 */
                this.findColumnOffset = (pos + 1);
                /*      */
                /*      */
                /* 1277 */
                return new InteractiveTextFindResult(startCoords, endCoords, wrappedAround);
                /*      */
            }
            /*      */
        }
        /* 1280 */
        return InteractiveTextFindResult.EOS;
        /*      */
    }

    /*      */
    /*      */
    public void processFindResult(InteractiveTextFindResult r)
    /*      */ {
        /* 1285 */
        if (r == null) {
            /* 1286 */
            return;
            /*      */
        }
        /* 1288 */
        if (r.isEndOfSearch()) {
            /* 1289 */
            Display.getCurrent().beep();
            /* 1290 */
            logger.warn("End of search", new Object[0]);
            /* 1291 */
            return;
            /*      */
        }
        /* 1293 */
        if (r.isWrappedAround()) {
            /* 1294 */
            Display.getCurrent().beep();
            /* 1295 */
            logger.warn("Search wrapped around", new Object[0]);
            /*      */
        }
        /* 1297 */
        setCaretCoordinates(r.getBegin(), null, false);
        /* 1298 */
        int pos = this.wrappedText.getCaretOffset();
        /* 1299 */
        int posEnd = Math.max(pos, this.bufferManager.getBufferOffset(r.getEnd(), false));
        /* 1300 */
        this.wrappedText.setSelection(pos, posEnd);
        /*      */
    }

    /*      */
    /*      */
    public void clearFindResult() {
        /* 1304 */
        resetSelection();
        /*      */
    }

    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    /*      */
    public boolean verifyOperation(OperationRequest req)
    /*      */ {
        /* 1313 */
        switch (req.getOperation()) {
            /*      */
            case COPY:
                /* 1315 */
                return this.viewer.canDoOperation(4);
            /*      */
            case SELECT_ALL:
                /* 1317 */
                return true;
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            /*      */
            case REFRESH:
                /* 1327 */
                return true;
            /*      */
        }
        /* 1329 */
        return false;
        /*      */
    }

    /*      */
    /*      */
    /*      */
    public boolean doOperation(OperationRequest req)
    /*      */ {
        /* 1335 */
        switch (req.getOperation()) {
            /*      */
            case COPY:
                /* 1337 */
                if (Licensing.isFullBuild()) {
                    /* 1338 */
                    this.viewer.doOperation(4);
                    /*      */
                }
                /* 1340 */
                return true;
            /*      */
            /*      */
            case SELECT_ALL:
                /* 1343 */
                if (this.docManager.isSingleAnchorDocument()) {
                    /* 1344 */
                    this.wrappedText.selectAll();
                    /* 1345 */
                    return true;
                    /*      */
                }
                /*      */
                /*      */
                /*      */
                /* 1350 */
                Shell shell = this.display.getActiveShell();
                /* 1351 */
                MessageDialog.openWarning(shell, "Warning", "Selecting all text in this view might generate a very large amount of data.\n\nPress OK to generate and export the entire text to a file.");
                /*      */
                /*      */
                /*      */
                /* 1355 */
                FileDialog dlg = new FileDialog(shell, 8192);
                /* 1356 */
                dlg.setText("Save text to file");
                /* 1357 */
                final String path = dlg.open();
                /* 1358 */
                if (path == null) {
                    /* 1359 */
                    return false;
                    /*      */
                }
                /*      */
                try
                    /*      */ {
                    /* 1363 */
                    UI.getTaskManager().create(shell, "Generating document...", new Runnable()
                            /*      */ {
                        /*      */
                        public void run() {
                            /* 1366 */
                            ITextDocumentPart part = InteractiveTextViewer.this.docManager.getPart(InteractiveTextViewer.this.docManager.getAnchorFirst(), Integer.MAX_VALUE);
                            /*      */
                            try {
                                /* 1368 */
                                String text = TextPartUtil.buildRawTextFromPartInterruptibly(part);
                                /* 1369 */
                                IO.writeFile(new File(path), text);
                                /*      */
                            }
                            /*      */ catch (Exception e) {
                                /* 1372 */
                                throw new RuntimeException(e);
                            }
                        }
                    }, 1000L);
                    /*      */
                    /*      */
                }
                /*      */ catch (InvocationTargetException e1)
                    /*      */ {
                    /*      */
                    /* 1378 */
                    if (e1.getCause() != null) {
                        /* 1379 */
                        logger.catching(e1.getCause());
                        /*      */
                    }
                    /* 1381 */
                    return false;
                    /*      */
                }
                /*      */ catch (InterruptedException e1) {
                    /* 1384 */
                    return false;
                    /*      */
                }
                /* 1386 */
                return true;
            /*      */
            /*      */
            case REFRESH:
                /* 1389 */
                refresh();
                /* 1390 */
                return true;
            /*      */
        }
        /*      */
        /* 1393 */
        return false;
        /*      */
    }
    /*      */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\InteractiveTextViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */