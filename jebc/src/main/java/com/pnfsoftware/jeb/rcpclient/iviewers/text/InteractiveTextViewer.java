
package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.core.output.text.IAnchor;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.core.output.text.TextPartUtil;
import com.pnfsoftware.jeb.core.output.text.impl.Coordinates;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.units.IMetadataManager;
import com.pnfsoftware.jeb.rcpclient.extensions.AbstractRefresher;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
import com.pnfsoftware.jeb.rcpclient.extensions.ui.UITaskManager;
import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
import com.pnfsoftware.jeb.rcpclient.iviewers.Style;
import com.pnfsoftware.jeb.rcpclient.iviewers.hover.IHoverableProvider;
import com.pnfsoftware.jeb.rcpclient.iviewers.hover.InteractiveTextHover;
import com.pnfsoftware.jeb.rcpclient.iviewers.hover.extend.IHoverableWidget;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationFactory;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationModelEx;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationRulerColumnEx;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.AnnotationService;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.StandardAnnotationAccess;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.TextAnnotation;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.wrapped.WrappedText;
import com.pnfsoftware.jeb.util.collect.ItemHistory;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextViewerExtension8;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.AnnotationPainter.BoxStrategy;
import org.eclipse.jface.text.source.AnnotationPainter.HighlightingStrategy;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class InteractiveTextViewer
        implements IOperable, INavigableViewer, ITextDocumentViewer {
    private static final ILogger logger = GlobalLog.getLogger(InteractiveTextViewer.class, Integer.MAX_VALUE);
    public static final int FLAG_DISABLE_LINE_WRAPPING = 1;
    public static final int FLAG_DISABLE_PART_VERIFICATION = 2;
    private Display display;
    private Listener mouseWheelFilter;
    private IEventListener idocListener;
    private Composite container;
    private SourceViewer viewer;
    private StyledText text;
    private WrappedText wrappedText;
    private CompositeRuler leftRuler;
    private OverviewBar overviewBar;
    private AnnotationModelEx annoModel;
    private List<TextAnnotation> annotations = new ArrayList();
    private AnnotationPainter annoPainter;
    private boolean documentBeingChanged;
    private FindTextOptions findOptions;
    private ItemHistory<VisualPosition> positionHistory = new ItemHistory();
    private int maxCharsPerLine;
    private int charsEndLine;
    private int charactersWrap;
    private DocumentManager docManager;
    private NavigationEventManager navigationEventManager;
    ScrollBufferManager bufferManager;
    IStyleProvider styleAdapter;
    private boolean disablePartVerification;

    public InteractiveTextViewer(Composite parent, int flags, ITextDocument idoc, IPropertyManager propertyManager, IMetadataManager mm) {
        this.display = parent.getDisplay();
        boolean showHorizontalScrollbar = propertyManager.getBoolean(".ui.text.ShowHorizontalScrollbar");
        boolean showVerticalScrollbar = propertyManager.getBoolean(".ui.text.ShowVerticalScrollbar");
        this.container = new Composite(parent, showVerticalScrollbar ? 512 : 0);
        this.container.setLayout(new FillLayout());
        this.maxCharsPerLine = propertyManager.getInteger(".ui.text.CharactersPerLineMax");
        this.charsEndLine = propertyManager.getInteger(".ui.text.CharactersPerLineAtEnd");
        this.charactersWrap = propertyManager.getInteger(".ui.text.CharactersWrap");
        if ((flags & 0x1) != 0) {
            this.charactersWrap = -1;
        }
        if ((flags & 0x2) != 0) {
            this.disablePartVerification = true;
        }
        boolean displayEolAtEod = propertyManager.getBoolean(".ui.text.DisplayEolAtEod");
        this.docManager = new DocumentManager(idoc, displayEolAtEod);
        this.docManager.setCharactersWrap(this.charactersWrap);
        OverviewBarProperties overviewBarProperties = OverviewBarProperties.buildOverviewBarProperties(propertyManager, idoc, mm);
        int orientation = (overviewBarProperties != null) && ((overviewBarProperties.position == 128) || (overviewBarProperties.position == 1024)) ? 512 : 256;
        SashForm container2 = new SashForm(this.container, orientation);
        if ((overviewBarProperties != null) && ((overviewBarProperties.position == 128) || (overviewBarProperties.position == 16384))) {
            int overviewBarStyles = 0x800 | (overviewBarProperties.position == 128 ? 'Ā' : 'Ȁ');
            this.overviewBar = new OverviewBar(container2, overviewBarStyles, idoc, overviewBarProperties.mm);
        }
        this.annoModel = new AnnotationModelEx();
        this.leftRuler = new CompositeRuler();
        this.leftRuler.setModel(this.annoModel);
        AnnotationRulerColumnEx annoRulerColumn = new AnnotationRulerColumnEx(this.annoModel, 16, new StandardAnnotationAccess());
        this.leftRuler.addDecorator(0, annoRulerColumn);
        annoRulerColumn.addAnnotationType("com.pnfsoftware.jeb.rcpclient.textAnno");
        this.viewer = new SourceViewer(container2, this.leftRuler, showHorizontalScrollbar ? 256 : 0);
        this.viewer.setEditable(false);
        annoRulerColumn.setViewer(this.viewer);
        this.text = this.viewer.getTextWidget();
        this.text.setCursor(new Cursor(this.display, 0));
        this.text.setAlwaysShowScrollBars(true);
        this.wrappedText = new WrappedText(this.docManager, this.text);
        this.bufferManager = new ScrollBufferManager(this.wrappedText, this);
        this.navigationEventManager = new NavigationEventManager(this.wrappedText, this.bufferManager, this);
        this.navigationEventManager.setScrollLineSize(propertyManager.getInteger(".ui.text.ScrollLineSize"));
        this.navigationEventManager.setPageLineSize(propertyManager.getInteger(".ui.text.PageLineSize"));
        this.navigationEventManager.setPageMultiplier(propertyManager.getInteger(".ui.text.PageMultiplier"));
        this.navigationEventManager.setCaretBehaviorViewportStatic(propertyManager.getBoolean(".ui.text.CaretBehaviorViewportStatic"));
        this.navigationEventManager.setCancelSimpleMouseWheels(showHorizontalScrollbar);
        int overviewBarStyles;
        if ((overviewBarProperties != null) && ((overviewBarProperties.position == 1024) || (overviewBarProperties.position == 131072))) {
            overviewBarStyles = 0x800 | (overviewBarProperties.position == 1024 ? 'Ā' : 'Ȁ');
            this.overviewBar = new OverviewBar(container2, overviewBarStyles, idoc, overviewBarProperties.mm);
        }
        if (overviewBarProperties != null) {
            if (overviewBarProperties.position == 128) {
                container2.setWeights(new int[]{5, 95});
            } else if (overviewBarProperties.position == 1024) {
                container2.setWeights(new int[]{95, 5});
            } else if (overviewBarProperties.position == 16384) {
                container2.setWeights(new int[]{2, 98});
            } else if (overviewBarProperties.position == 131072) {
                container2.setWeights(new int[]{98, 2});
            }
        }
        this.annoPainter = new AnnotationPainter(this.viewer, new StandardAnnotationAccess());
        this.annoPainter.addTextStyleStrategy("strategyBox", new AnnotationPainter.BoxStrategy(1));
        this.annoPainter.addTextStyleStrategy("strategyHighlighting", new AnnotationPainter.HighlightingStrategy());
        for (AnnotationFactory f : AnnotationService.getInstance().getFactories()) {
            if (f.getHighlightingColor() != null) {
                this.annoPainter.addAnnotationType(f.getType(), "strategyHighlighting");
                this.annoPainter.setAnnotationTypeColor(f.getType(), f.getHighlightingColor());
            }
        }
        this.viewer.addTextPresentationListener(this.annoPainter);
        this.viewer.addPainter(this.annoPainter);
        this.viewer.configure(new SourceViewerConfiguration() {
            public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
                return null;
            }
        });
        this.navigationEventManager.addNavigationHandlers();
        this.viewer.addTextInputListener(new ITextInputListener() {
            public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
                if (newInput == null) {
                    InteractiveTextViewer.logger.debug("inputDocumentChanged: newinput is null", new Object[0]);
                    return;
                }
                InteractiveTextViewer.logger.debug("inputDocumentChanged: linecount=%s", new Object[]{Integer.valueOf(newInput.getNumberOfLines())});
                InteractiveTextViewer.this.documentBeingChanged = false;
            }

            public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
                InteractiveTextViewer.this.documentBeingChanged = true;
            }
        });
        final AbstractRefresher refresher = new AbstractRefresher(this.display, "IntText") {
            protected void performRefresh() {
                InteractiveTextViewer.this.onUnitChange();
            }
        };
        idoc.addListener(this.idocListener = new IEventListener() {
            public void onEvent(IEvent e) {
                if (e.getType() == J.UnitChange) {
                    refresher.request();
                }
            }
        });
    }

    public void setHoverText(IHoverableProvider provider, IHoverableWidget iHoverableWidget) {
        InteractiveTextHover iHover = new InteractiveTextHover(provider, iHoverableWidget);
        this.viewer.setTextHover(iHover, "__dftl_partition_content_type");
        this.viewer.setHoverEnrichMode(ITextViewerExtension8.EnrichMode.IMMEDIATELY);
    }

    public void setHoverText(IHoverableProvider provider) {
        setHoverText(provider, null);
    }

    public void initialize(boolean resetCaret) {
        long initAnchor = this.docManager.getDocument().getInitialAnchor();
        this.bufferManager.viewAtAnchor(initAnchor, !resetCaret);
        if (this.overviewBar != null) {
            this.overviewBar.connectToViewer(this);
        }
    }

    public void dispose() {
        this.viewer.removeTextHovers("__dftl_partition_content_type");
        if (this.mouseWheelFilter != null) {
            this.display.removeFilter(37, this.mouseWheelFilter);
            this.mouseWheelFilter = null;
        }
        if (this.idocListener != null) {
            this.docManager.getDocument().removeListener(this.idocListener);
            this.idocListener = null;
        }
    }

    public ITextDocument getDocument() {
        return this.docManager.getDocument();
    }

    WrappedText getWrappedText() {
        return this.wrappedText;
    }

    public StyledText getTextWidget() {
        return this.text;
    }

    public Composite getWidget() {
        return this.container;
    }

    public Point computeIdealSize() {
        Point p = this.text.computeSize(-1, -1, true);
        int w = p.x;
        if (this.leftRuler != null) {
            w += this.leftRuler.getControl().computeSize(-1, -1).x;
        }
        if (this.overviewBar != null) {
            w += this.overviewBar.computeSize(-1, -1).x;
        }
        int h = p.y;
        return new Point(w, h);
    }

    public ITextDocument getTextDocument() {
        return this.docManager.getDocument();
    }

    public boolean isDisposed() {
        return this.text.isDisposed();
    }

    public void setFont(Font font) {
        this.text.setFont(font);
    }

    public Font getFont() {
        return this.text.getFont();
    }

    public ICoordinates getCaretCoordinates() {
        return this.wrappedText.getCaretCoordinates();
    }

    public boolean setCaretCoordinates(ICoordinates coord, BufferPoint viewportPoint, boolean record) {
        return setVisualPosition(new VisualPosition(coord, viewportPoint), record);
    }

    public VisualPosition getVisualPosition() {
        return new VisualPosition(this.wrappedText.getCaretCoordinates(), this.wrappedText.getCaretViewportPoint());
    }

    public BufferPoint getCaretViewportPoint() {
        return this.wrappedText.getCaretViewportPoint();
    }

    public boolean setVisualPosition(VisualPosition pos, boolean record) {
        ICoordinates coord = pos.docCoord;
        if (coord == null) {
            return false;
        }
        VisualPosition pos0 = getVisualPosition();
        boolean success = this.bufferManager.setVisualPosition(pos);
        if ((success) && (record)) {
            this.positionHistory.add(pos0);
        }
        return success;
    }

    public ITextItem getItemAt(int opaqueOffset) {
        BufferPoint p = this.wrappedText.unwrap(opaqueOffset);
        if (p == null) {
            return null;
        }
        int lineIndex = p.lineIndex;
        int columnOffset = p.columnOffset;
        List<? extends ILine> lines = this.docManager.getCurrentPart().getLines();
        if (lineIndex >= lines.size()) {
            return null;
        }
        return TextPartUtil.getItemAt((ILine) lines.get(lineIndex), columnOffset);
    }

    public List<ITextItem> getCurrentItems() {
        if (this.docManager.getCurrentPart() == null) {
            return Collections.emptyList();
        }
        return TextPartUtil.getItems(this.docManager.getCurrentPart());
    }

    private ILine getLineAt(BufferPoint p) {
        if (p == null) {
            return null;
        }
        int lineIndex = p.lineIndex;
        List<? extends ILine> lines = this.docManager.getCurrentPart().getLines();
        if (lineIndex >= lines.size()) {
            return null;
        }
        return (ILine) lines.get(lineIndex);
    }

    private void onUnitChange() {
        if (this.text.isDisposed()) {
            return;
        }
        ICoordinates caretCoords = getCaretCoordinates();
        int bufOff = 0;
        if (caretCoords != null) {
            bufOff = this.bufferManager.getBufferOffset(caretCoords, false);
        }
        UnwrappedBufferPoint unwrappedPoint = this.wrappedText.unwrap(bufOff);
        ILine line = getLineAt(unwrappedPoint);
        List<? extends ITextItem> items = null;
        if (line != null) {
            items = line.getItems();
        } else if (Licensing.isDebugBuild()) {
            throw new RuntimeException("DEBUG: unexpected null line");
        }
        refresh();
        if (line == null) {
            return;
        }
        if (caretCoords == null) {
            IAnchor lastAnchor = this.docManager.getAnchorById(this.docManager.getAnchorEnd());
            if (lastAnchor == null) {
                return;
            }
            int lineDelta = Math.max(0, this.wrappedText.getLineCount() - lastAnchor.getLineIndex() - 1);
            caretCoords = new Coordinates(this.docManager.getAnchorEnd(), lineDelta);
        }
        ICoordinates newCoords = getCaretCoordinates();
        ILine newLine;
        if (newCoords != null) {
            int newBufOff = this.bufferManager.getBufferOffset(newCoords, false);
            newLine = getLineAt(this.wrappedText.unwrap(newBufOff));
            if (!line.getText().toString().equals(newLine.getText().toString())) {
            }
        } else {
            newCoords = new Coordinates(caretCoords.getAnchorId(), caretCoords.getLineDelta(), 0);
            int newBufOff = this.bufferManager.getBufferOffset(newCoords, false);
            if ((newBufOff >= this.wrappedText.getCharCount()) || (newBufOff < 0)) {
                IAnchor currentAnchor = this.docManager.getAnchorById(caretCoords.getAnchorId());
                if (currentAnchor == null) {
                    if ((caretCoords.getAnchorId() < TextPartUtil.getLastAnchorId(this.docManager.getCurrentPart())) &&
                            (caretCoords.getAnchorId() > TextPartUtil.getFirstAnchorId(this.docManager.getCurrentPart()))) {
                        IAnchor a = TextPartUtil.getApproximateAnchorById(this.docManager.getCurrentPart(), caretCoords
                                .getAnchorId(), 1);
                        String lineContent = this.wrappedText.getLine(a.getLineIndex());
                        int columnOffset = caretCoords.getColumnOffset() > lineContent.length() ? lineContent.length() : caretCoords.getColumnOffset();
                        newCoords = new Coordinates(a.getIdentifier(), 0, columnOffset);
                        setCaretCoordinates(newCoords, null, false);
                    } else {
                        this.wrappedText.setCaretOffset(this.wrappedText.getOffsetAtTopLine());
                    }
                    return;
                }
                int anchorStartLine = currentAnchor.getLineIndex();
                newCoords = new Coordinates(caretCoords.getAnchorId(), this.wrappedText.getLineCount() - (anchorStartLine + 1), 0);
                setCaretCoordinates(newCoords, null, false);
                return;
            }
            newLine = getLineAt(this.wrappedText.unwrap(newBufOff));
            if (line.getText().toString().equals(newLine.getText().toString())) {
                setCaretCoordinates(caretCoords, null, false);
                return;
            }
        }
        restoreCaretAtPreviousItem(caretCoords, items, newLine);
    }

    private void restoreCaretAtPreviousItem(ICoordinates caretCoords, List<? extends ITextItem> items, ILine newLine) {
        List<? extends ITextItem> newItems = newLine.getItems();
        int caretInItem = -1;
        int caretBeforeItem = -1;
        int caretPosition = caretCoords.getColumnOffset();
        for (int i = 0; i < items.size(); i++) {
            ITextItem item = (ITextItem) items.get(i);
            if (caretPosition < item.getOffset()) {
                caretBeforeItem = i;
                break;
            }
            if (caretPosition < item.getOffset() + item.getLength()) {
                caretInItem = i;
                break;
            }
        }
        if (caretInItem != -1) {
            if (caretInItem >= newItems.size()) {
                caretPosition = ((ITextItem) items.get(caretInItem)).getOffset();
            } else {
                ITextItem item = (ITextItem) newItems.get(caretInItem);
                caretPosition += item.getOffset() - ((ITextItem) items.get(caretInItem)).getOffset();
                if (caretPosition >= item.getOffset() + item.getLength()) {
                    caretPosition = item.getOffset() + item.getLength() - 1;
                }
            }
        } else if (items.size() == newItems.size()) {
            if (caretBeforeItem == -1) {
                if (items.size() != 0) {
                    ITextItem item = (ITextItem) newItems.get(newItems.size() - 1);
                    ITextItem oldItem = (ITextItem) items.get(items.size() - 1);
                    caretPosition = caretPosition + (item.getOffset() + item.getLength() - (oldItem.getOffset() + oldItem.getLength()));
                }
            } else if (caretBeforeItem != 0) {
                ITextItem item = (ITextItem) newItems.get(caretBeforeItem - 1);
                ITextItem oldItem = (ITextItem) items.get(caretBeforeItem - 1);
                caretPosition += item.getOffset() + item.getLength() - (oldItem.getOffset() + oldItem.getLength());
            }
        }
        if (caretPosition > newLine.getText().length()) {
            caretPosition = newLine.getText().length();
        }
        Coordinates newCoords = new Coordinates(caretCoords.getAnchorId(), caretCoords.getLineDelta(), caretPosition);
        setCaretCoordinates(newCoords, null, false);
    }

    public void registerAnnotation(TextAnnotation annotation) {
        this.annotations.add(annotation);
        applyAnnotation(annotation);
    }

    public void unregisterAnnotation(TextAnnotation annotation) {
        unapplyAnnotation(annotation);
        this.annotations.remove(annotation);
    }

    public void unregisterAnnotations() {
        unapplyAnnotations();
        this.annotations.clear();
    }

    private void applyAnnotation(TextAnnotation annotation) {
        ICoordinates coord = annotation.getCoordinates();
        int lineIndex = TextPartUtil.coordinatesToLineIndex(this.docManager.getCurrentPart(), coord);
        if (lineIndex >= 0) {
            int wrappedLineIndex = this.docManager.wrapLine(lineIndex);
            if (wrappedLineIndex >= 0) {
                int offset = this.wrappedText.getOffsetAtLine(wrappedLineIndex);
                int length = this.wrappedText.getLine(wrappedLineIndex).length();
                Position pos = new Position(offset, length);
                this.annoModel.addAnnotation(annotation, pos);
            }
        }
    }

    private void unapplyAnnotation(TextAnnotation annotation) {
        this.annoModel.removeAnnotation(annotation);
    }

    private void reapplyAnnotations() {
        unapplyAnnotations();
        applyAnnotations();
    }

    private void applyAnnotations() {
        for (TextAnnotation annotation : this.annotations) {
            applyAnnotation(annotation);
        }
    }

    private void unapplyAnnotations() {
        for (TextAnnotation annotation : this.annotations) {
            unapplyAnnotation(annotation);
        }
    }

    public boolean updateDocument(long anchorId, int linesAfter, int linesBefore) {
        logger.i("updateDocument(): anchorId=%d/%Xh, after=%d, before=%d", new Object[]{Long.valueOf(anchorId), Long.valueOf(anchorId), Integer.valueOf(linesAfter), Integer.valueOf(linesBefore)});
        if (linesAfter < 0) {
            linesAfter = 0;
        }
        if ((linesBefore < 0) || (anchorId == this.docManager.getAnchorFirst())) {
            linesBefore = 0;
        }
        if ((anchorId < this.docManager.getAnchorFirst()) || (anchorId >= this.docManager.getAnchorEnd())) {
            return false;
        }
        long t0 = System.currentTimeMillis();
        ITextDocumentPart newPart = this.docManager.getPart(anchorId, linesAfter, linesBefore);
        long t1 = System.currentTimeMillis();
        logger.debug("Part at anchor %d retrieved in %dms", new Object[]{Long.valueOf(anchorId), Long.valueOf(t1 - t0)});
        if ((newPart == null) || (newPart.getAnchors() == null) || (newPart.getAnchors().isEmpty())) {
            logger.debug("Invalid part", new Object[0]);
            if (this.docManager.getCurrentPart() == null) {
                throw new RuntimeException("No text part can be fetched, bad document output generation.");
            }
            return false;
        }
        if ((this.charactersWrap < 0) &&
                (this.maxCharsPerLine >= 10) && (this.maxCharsPerLine - this.charsEndLine > 5)) {
            for (int i = 0; i < newPart.getLines().size(); i++) {
                ILine line = (ILine) newPart.getLines().get(i);
                if (line.getText().length() > this.maxCharsPerLine) {
                    newPart = new TextDocumentPartDelegate(newPart, this.maxCharsPerLine, this.charsEndLine);
                    break;
                }
            }
        }
        if (!this.disablePartVerification) {
            TextPartUtil.verifyPart(newPart);
        }
        this.docManager.setCurrentPart(newPart);
        String docContent = this.docManager.getText();
        this.bufferManager.updateDocPart();
        IDocument doc = new Document(docContent);
        TextPresentation pres = buildStyles(doc);
        this.viewer.setDocument(doc, this.annoModel);
        this.viewer.changeTextPresentation(pres, true);
        reapplyAnnotations();
        long t2 = System.currentTimeMillis();
        logger.debug("updateDocument took %dms", new Object[]{Long.valueOf(t2 - t0)});
        return true;
    }

    public void updateAnnotationBar() {
        this.leftRuler.update();
    }

    private TextPresentation buildStyles(IDocument doc) {
        if ((this.styleAdapter == null) || (doc == null)) {
            return null;
        }
        TextPresentation pres = new TextPresentation(new Region(0, doc.getLength()), 1);
        for (DocumentManager.RenderedItem renderedItem : this.docManager.getRenderedItems()) {
            ITextItem item = renderedItem.item;
            Style style = this.styleAdapter.getStyle(item);
            if (style != null)
                if (item.getLength() <= 0) {
                    logger.error("Trying to apply a void style at offset %d", new Object[]{Integer.valueOf(renderedItem.offset)});
                } else if (item.getOffset() < 0) {
                    logger.error("Trying to apply a style at negative offset %d", new Object[]{Integer.valueOf(renderedItem.offset)});
                } else {
                    int length = item.getLength();
                    if (renderedItem.offset + length > doc.getLength()) {
                        if (renderedItem.offset > doc.getLength()) {
                            logger.error("Trying to apply a style at item not in document at offset %d", new Object[]{
                                    Integer.valueOf(renderedItem.offset)});
                        } else {
                            length = doc.getLength() - renderedItem.offset;
                        }
                    } else {
                        StyleRange sr = createStyleRange(renderedItem.offset, length, style);
                        pres.addStyleRange(sr);
                    }
                }
        }
        return pres;
    }

    private StyleRange createStyleRange(int start, int length, Style style) {
        int flags = 0;
        if (style.isBold()) {
            flags |= 0x1;
        }
        if (style.isItalic()) {
            flags |= 0x2;
        }
        return new StyleRange(start, length, style.getColor(), style.getBackgroungColor(), flags);
    }

    public void setStyleAdapter(IStyleProvider styleAdapter) {
        if (this.documentBeingChanged) {
            return;
        }
        this.styleAdapter = styleAdapter;
    }

    public IStyleProvider getStyleAdapter() {
        return this.styleAdapter;
    }

    public void refreshStyles() {
        if (this.documentBeingChanged) {
            return;
        }
        TextPresentation pres = buildStyles(this.viewer.getDocument());
        this.viewer.changeTextPresentation(pres, true);
    }

    private int activeLineIndex = -1;
    private long findAnchorId;

    public void activateCurrentLine(boolean active) {
        if (this.styleAdapter == null) {
            return;
        }
        Color bgcol = this.styleAdapter.getOnCaretBackgroundColor();
        int lineindex = this.wrappedText.getCaretLine();
        if ((this.activeLineIndex >= 0) && (this.activeLineIndex < this.wrappedText.getLineCount())) {
            this.text.setLineBackground(this.activeLineIndex, 1, null);
        }
        if ((active) && (lineindex >= 0) && (lineindex < this.wrappedText.getLineCount())) {
            this.text.setLineBackground(lineindex, 1, bgcol);
            this.activeLineIndex = lineindex;
        }
    }

    public void refresh() {
        if (this.documentBeingChanged) {
            return;
        }
        this.bufferManager.forceRefresh();
        if (this.overviewBar != null) {
            this.overviewBar.refresh();
        }
    }

    public void resetSelection() {
        this.wrappedText.resetSelection();
    }

    public void addItemListener(IItemListener listener) {
        this.navigationEventManager.addItemListener(listener);
    }

    public void removeItemListener(IItemListener listener) {
        this.navigationEventManager.removeItemListener(listener);
    }

    public void addPositionListener(IPositionListener listener) {
        this.navigationEventManager.addPositionListener(listener);
    }

    public void removePositionListener(IPositionListener listener) {
        this.navigationEventManager.removePositionListener(listener);
    }

    public void addUnhandkedVerifyKeyListener(VerifyKeyListener listener) {
        this.navigationEventManager.addUnhandledVerifyKeyListener(listener);
    }

    public void removeUnhandledVerifyKeyListener(VerifyKeyListener listener) {
        this.navigationEventManager.removeUnhandledVerifyKeyListener(listener);
    }

    public ITextDocumentPart getCurrentDocumentPart() {
        return this.docManager.getCurrentPart();
    }

    private int findLineDelta;
    private int findColumnOffset;

    public boolean supportReverseSearch() {
        return false;
    }

    public void resetFindTextOptions() {
        this.findOptions = null;
        this.findAnchorId = this.docManager.getAnchorFirst();
        this.findLineDelta = 0;
        this.findColumnOffset = 0;
    }

    public void setFindTextOptions(FindTextOptions options) {
        this.findOptions = options;
    }

    public FindTextOptions getFindTextOptions(boolean update) {
        if (this.findOptions == null) {
            this.findOptions = new FindTextOptions("");
        }
        if (update) {
            ICoordinates c = getCaretCoordinates();
            if (c != null) {
                this.findAnchorId = c.getAnchorId();
                this.findLineDelta = c.getLineDelta();
                this.findColumnOffset = c.getColumnOffset();
            }
        }
        return this.findOptions;
    }

    public InteractiveTextFindResult findText(FindTextOptions optionsOverride) {
        FindTextOptions options = optionsOverride != null ? optionsOverride : this.findOptions;
        if ((options == null) || (options.getSearchString() == null) || (options.getSearchString().isEmpty())) {
            return null;
        }
        boolean wrappedAround = false;
        logger.debug(S.s(715), new Object[]{options.getSearchString()});
        int searchStringLength = options.getSearchString().length();
        Pattern p = null;
        if (options.isRegularExpression()) {
            try {
                p = Pattern.compile(options.getSearchString(), options.isCaseSensitive() ? 0 : 2);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid regex, will consider it a standard string: %s", new Object[]{e});
            }
        }
        for (; ; ) {
            long anchorId = this.findAnchorId;
            int lineDelta = this.findLineDelta;
            int columnOffset = this.findColumnOffset;
            Coordinates startCoords = null;
            Coordinates endCoords = null;
            ITextDocumentPart part = this.docManager.getCurrentPart();
            int pos = -1;
            while (pos < 0) {
                if (Thread.interrupted()) {
                    this.findAnchorId = anchorId;
                    this.findLineDelta = 0;
                    this.findColumnOffset = 0;
                    return null;
                }
                if (part != null) {
                    long firstAnchorId = TextPartUtil.getFirstAnchorId(part);
                    long nextAnchorId = TextPartUtil.getNextAnchorId(part);
                    if ((anchorId < firstAnchorId) || (anchorId >= nextAnchorId)) {
                        logger.i("Requested anchor out of current part: %Xh not in [%Xh, %Xh)", new Object[]{Long.valueOf(anchorId), Long.valueOf(firstAnchorId),
                                Long.valueOf(nextAnchorId)});
                        part = null;
                    }
                }
                if (part == null) {
                    part = this.docManager.getPart(anchorId, 500);
                }
                IAnchor anchor = TextPartUtil.getAnchorById(part, anchorId);
                if (anchor == null) {
                    anchor = TextPartUtil.getApproximateAnchorById(part, anchorId, 1);
                    if (anchor == null) {
                        logger.i("Error on part request @ %Xh (end: %Xh)", new Object[]{Long.valueOf(anchorId), Long.valueOf(this.docManager.getAnchorEnd())});
                        break;
                    }
                }
                logger.i("Searching part @ %Xh (got %Xh)", new Object[]{Long.valueOf(anchorId), Long.valueOf(anchor.getIdentifier())});
                int index = anchor.getLineIndex() + lineDelta;
                List<? extends ILine> lines = part.getLines();
                for (; index < lines.size(); index++) {
                    String lineText = ((ILine) lines.get(index)).getText().toString();
                    if (!lineText.isEmpty()) {
                        if (p != null) {
                            Matcher m = p.matcher(lineText);
                            if (m.find(columnOffset)) {
                                pos = m.start();
                                searchStringLength = m.end() - pos;
                                break;
                            }
                        } else {
                            pos = Strings.search(lineText, columnOffset, options.getSearchString(), false, options.isCaseSensitive(), options.isReverseSearch());
                            if (pos >= 0) {
                                break;
                            }
                        }
                        columnOffset = 0;
                    }
                }
                if (pos >= 0) {
                    anchor = TextPartUtil.getAnchorAtLine(part, index);
                    anchorId = anchor.getIdentifier();
                    lineDelta = index - anchor.getLineIndex();
                    startCoords = new Coordinates(anchorId, lineDelta, pos);
                    endCoords = new Coordinates(anchorId, lineDelta, pos + searchStringLength);
                } else {
                    lineDelta = 0;
                    anchorId = TextPartUtil.getNextAnchorId(part);
                    if ((anchorId < this.docManager.getAnchorFirst()) || (anchorId >= this.docManager.getAnchorEnd() - 1L)) {
                        break;
                    }
                }
            }
            if (pos < 0) {
                if ((!options.isWrapAround()) ||
                        (wrappedAround)) {
                    break;
                }
                wrappedAround = true;
                this.findAnchorId = this.docManager.getAnchorFirst();
                this.findLineDelta = 0;
                this.findColumnOffset = 0;
            } else {
                this.findAnchorId = anchorId;
                this.findLineDelta = lineDelta;
                this.findColumnOffset = (pos + 1);
                return new InteractiveTextFindResult(startCoords, endCoords, wrappedAround);
            }
        }
        return InteractiveTextFindResult.EOS;
    }

    public void processFindResult(InteractiveTextFindResult r) {
        if (r == null) {
            return;
        }
        if (r.isEndOfSearch()) {
            Display.getCurrent().beep();
            logger.warn("End of search", new Object[0]);
            return;
        }
        if (r.isWrappedAround()) {
            Display.getCurrent().beep();
            logger.warn("Search wrapped around", new Object[0]);
        }
        setCaretCoordinates(r.getBegin(), null, false);
        int pos = this.wrappedText.getCaretOffset();
        int posEnd = Math.max(pos, this.bufferManager.getBufferOffset(r.getEnd(), false));
        this.wrappedText.setSelection(pos, posEnd);
    }

    public void clearFindResult() {
        resetSelection();
    }

    public boolean verifyOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case COPY:
                return this.viewer.canDoOperation(4);
            case SELECT_ALL:
                return true;
            case REFRESH:
                return true;
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case COPY:
                if (Licensing.isFullBuild()) {
                    this.viewer.doOperation(4);
                }
                return true;
            case SELECT_ALL:
                if (this.docManager.isSingleAnchorDocument()) {
                    this.wrappedText.selectAll();
                    return true;
                }
                Shell shell = this.display.getActiveShell();
                MessageDialog.openWarning(shell, "Warning", "Selecting all text in this view might generate a very large amount of data.\n\nPress OK to generate and export the entire text to a file.");
                FileDialog dlg = new FileDialog(shell, 8192);
                dlg.setText("Save text to file");
                final String path = dlg.open();
                if (path == null) {
                    return false;
                }
                try {
                    UI.getTaskManager().create(shell, "Generating document...", new Runnable() {
                        public void run() {
                            ITextDocumentPart part = InteractiveTextViewer.this.docManager.getPart(InteractiveTextViewer.this.docManager.getAnchorFirst(), Integer.MAX_VALUE);
                            try {
                                String text = TextPartUtil.buildRawTextFromPartInterruptibly(part);
                                IO.writeFile(new File(path), text);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, 1000L);
                } catch (InvocationTargetException e1) {
                    if (e1.getCause() != null) {
                        logger.catching(e1.getCause());
                    }
                    return false;
                } catch (InterruptedException e1) {
                    return false;
                }
                return true;
            case REFRESH:
                refresh();
                return true;
        }
        return false;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\iviewers\text\InteractiveTextViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */