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

public abstract interface ITextDocumentViewer
        extends IOperable, IFindTextImpl<InteractiveTextFindResult>, INavigableViewer {
    public abstract ITextDocument getDocument();

    public abstract void refresh();

    public abstract Composite getWidget();

    public abstract Point computeIdealSize();

    public abstract StyledText getTextWidget();

    public abstract ICoordinates getCaretCoordinates();

    public abstract boolean setCaretCoordinates(ICoordinates paramICoordinates, BufferPoint paramBufferPoint, boolean paramBoolean);

    public abstract BufferPoint getCaretViewportPoint();

    public abstract ITextItem getItemAt(int paramInt);

    public abstract List<ITextItem> getCurrentItems();

    public abstract ITextDocumentPart getCurrentDocumentPart();

    public abstract Font getFont();

    public abstract void setFont(Font paramFont);

    public abstract void refreshStyles();

    public abstract void activateCurrentLine(boolean paramBoolean);

    public abstract void setStyleAdapter(IStyleProvider paramIStyleProvider);

    public abstract IStyleProvider getStyleAdapter();

    public abstract void registerAnnotation(TextAnnotation paramTextAnnotation);

    public abstract void unregisterAnnotations();

    public abstract void initialize(boolean paramBoolean);

    public abstract void addItemListener(IItemListener paramIItemListener);

    public abstract void removeItemListener(IItemListener paramIItemListener);

    public abstract void addPositionListener(IPositionListener paramIPositionListener);

    public abstract void removePositionListener(IPositionListener paramIPositionListener);

    public abstract void addUnhandkedVerifyKeyListener(VerifyKeyListener paramVerifyKeyListener);

    public abstract void removeUnhandledVerifyKeyListener(VerifyKeyListener paramVerifyKeyListener);

    public abstract void setHoverText(IHoverableProvider paramIHoverableProvider);

    public abstract void dispose();

    public abstract boolean isDisposed();

    public abstract void resetSelection();
}


