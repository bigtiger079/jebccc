package com.pnfsoftware.jeb.rcpclient.parts.units;

import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.units.IBinaryUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.IImageDocument;
import com.pnfsoftware.jeb.rcpclient.extensions.ImageViewer;
import com.pnfsoftware.jeb.util.events.EventSource;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class UnitImageView extends AbstractUnitFragment<IBinaryUnit> {
    private static final ILogger logger = GlobalLog.getLogger(UnitImageView.class);
    private ImageViewer viewer;
    private IImageDocument doc;

    public UnitImageView(Composite parent, int flags, RcpClientContext context, IBinaryUnit unit) {
        super(parent, flags, unit, null, context);
        setLayout(new FillLayout());
        this.doc = new StaticImageDocument(getDisplay(), unit);
        this.viewer = new ImageViewer(this);
        this.viewer.setInput(this.doc);
        setPrimaryWidget(this.viewer.getControl());
    }

    public ImageViewer getViewer() {
        return this.viewer;
    }

    static class StaticImageDocument extends EventSource implements IImageDocument {
        private Image image;

        public StaticImageDocument(Display display, IBinaryUnit unit) {
            try {
                InputStream stream = unit.getInput().getStream();
                Throwable localThrowable3 = null;
                try {
                    this.image = new Image(display, stream);
                } catch (Throwable localThrowable1) {
                    localThrowable3 = localThrowable1;
                    throw localThrowable1;
                } finally {
                    if (stream != null) if (localThrowable3 != null) try {
                        stream.close();
                    } catch (Throwable localThrowable2) {
                        localThrowable3.addSuppressed(localThrowable2);
                    }
                    else stream.close();
                }
            } catch (Exception e) {
                UnitImageView.logger.debug("Image rendering error: %s", e);
                UnitImageView.logger.error("Cannot render image for unit %s, using a dummy 1x1 pixel image instead", unit);
                this.image = new Image(display, new Rectangle(0, 0, 1, 1));
            }
        }

        public Image getImage() {
            return this.image;
        }
    }

    public byte[] export() {
        IInput input = getUnit().getInput();
        byte[] data = null;
        try {
            InputStream in = input.getStream();
            Throwable localThrowable3 = null;
            try {
                data = IO.readInputStream(in);
                return data;
            } catch (Throwable localThrowable4) {
                localThrowable3 = localThrowable4;
                throw localThrowable4;
            } finally {
                if (in != null) if (localThrowable3 != null) try {
                    in.close();
                } catch (Throwable localThrowable2) {
                    localThrowable3.addSuppressed(localThrowable2);
                }
                else {
                    in.close();
                }
            }
        } catch (IOException e) {
            logger.catching(e);
        }
        return null;
    }

    public AbstractUnitFragment.FragmentType getFragmentType() {
        return AbstractUnitFragment.FragmentType.IMAGE;
    }
}


