package com.pnfsoftware.jeb.rcpclient.iviewers.text;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.output.text.ITextDocument;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.rcpclient.extensions.search.IFindTextImpl;
import com.pnfsoftware.jeb.rcpclient.iviewers.IStyleProvider;
import com.pnfsoftware.jeb.rcpclient.iviewers.hover.IHoverableProvider;
import com.pnfsoftware.jeb.rcpclient.iviewers.text.anno.TextAnnotation;

import java.util.List;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public interface ITextDocumentViewer extends IOperable, IFindTextImpl<InteractiveTextFindResult>, INavigableViewer {
    ITextDocument getDocument();

    void refresh();

    Composite getWidget();

    Point computeIdealSize();

    StyledText getTextWidget();

    ICoordinates getCaretCoordinates();

    boolean setCaretCoordinates(ICoordinates paramICoordinates, BufferPoint paramBufferPoint, boolean paramBoolean);

    BufferPoint getCaretViewportPoint();

    ITextItem getItemAt(int paramInt);

    List<ITextItem> getCurrentItems();

    ITextDocumentPart getCurrentDocumentPart();

    Font getFont();

    void setFont(Font paramFont);

    void refreshStyles();

    void activateCurrentLine(boolean paramBoolean);

    void setStyleAdapter(IStyleProvider paramIStyleProvider);

    IStyleProvider getStyleAdapter();

    void registerAnnotation(TextAnnotation paramTextAnnotation);

    void unregisterAnnotations();

    void initialize(boolean paramBoolean);

    void addItemListener(IItemListener paramIItemListener);

    void removeItemListener(IItemListener paramIItemListener);

    void addPositionListener(IPositionListener paramIPositionListener);

    void removePositionListener(IPositionListener paramIPositionListener);

    void addUnhandkedVerifyKeyListener(VerifyKeyListener paramVerifyKeyListener);

    void removeUnhandledVerifyKeyListener(VerifyKeyListener paramVerifyKeyListener);

    void setHoverText(IHoverableProvider paramIHoverableProvider);

    void dispose();

    boolean isDisposed();

    void resetSelection();
}