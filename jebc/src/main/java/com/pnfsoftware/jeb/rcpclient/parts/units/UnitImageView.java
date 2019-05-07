/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.core.input.IInput;
/*     */ import com.pnfsoftware.jeb.core.units.IBinaryUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.IImageDocument;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.ImageViewer;
/*     */ import com.pnfsoftware.jeb.util.events.EventSource;
/*     */ import com.pnfsoftware.jeb.util.io.IO;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
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
/*     */ public class UnitImageView
        /*     */ extends AbstractUnitFragment<IBinaryUnit>
        /*     */ {
    /*  35 */   private static final ILogger logger = GlobalLog.getLogger(UnitImageView.class);
    /*     */   private ImageViewer viewer;
    /*     */   private IImageDocument doc;

    /*     */
    /*     */
    public UnitImageView(Composite parent, int flags, RcpClientContext context, IBinaryUnit unit)
    /*     */ {
        /*  41 */
        super(parent, flags, unit, null, context);
        /*  42 */
        setLayout(new FillLayout());
        /*     */
        /*  44 */
        this.doc = new StaticImageDocument(getDisplay(), unit);
        /*     */
        /*  46 */
        this.viewer = new ImageViewer(this);
        /*  47 */
        this.viewer.setInput(this.doc);
        /*     */
        /*  49 */
        setPrimaryWidget(this.viewer.getControl());
        /*     */
    }

    /*     */
    /*     */
    public ImageViewer getViewer() {
        /*  53 */
        return this.viewer;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */   static class StaticImageDocument
            /*     */ extends EventSource
            /*     */ implements IImageDocument
            /*     */ {
        /*     */     private Image image;

        /*     */
        /*     */
        /*     */
        public StaticImageDocument(Display display, IBinaryUnit unit)
        /*     */ {
            /*     */
            try
                /*     */ {
                /*  69 */
                InputStream stream = unit.getInput().getStream();
                Throwable localThrowable3 = null;
                /*  70 */
                try {
                    this.image = new Image(display, stream);
                    /*     */
                }
                /*     */ catch (Throwable localThrowable1)
                    /*     */ {
                    /*  69 */
                    localThrowable3 = localThrowable1;
                    throw localThrowable1;
                    /*     */
                } finally {
                    /*  71 */
                    if (stream != null) if (localThrowable3 != null) try {
                        stream.close();
                    } catch (Throwable localThrowable2) {
                        localThrowable3.addSuppressed(localThrowable2);
                    }
                    else stream.close();
                    /*     */
                }
                /*  73 */
            } catch (Exception e) {
                UnitImageView.logger.debug("Image rendering error: %s", new Object[]{e});
                /*  74 */
                UnitImageView.logger.error("Cannot render image for unit %s, using a dummy 1x1 pixel image instead", new Object[]{unit});
                /*  75 */
                this.image = new Image(display, new Rectangle(0, 0, 1, 1));
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */
        public Image getImage()
        /*     */ {
            /*  81 */
            return this.image;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /*  87 */
        IInput input = ((IBinaryUnit) getUnit()).getInput();
        /*  88 */
        byte[] data = null;
        /*  89 */
        try {
            InputStream in = input.getStream();
            Throwable localThrowable3 = null;
            /*  90 */
            try {
                data = IO.readInputStream(in);
                /*  91 */
                return data;
                /*     */
            }
            /*     */ catch (Throwable localThrowable4)
                /*     */ {
                /*  89 */
                localThrowable3 = localThrowable4;
                throw localThrowable4;
                /*     */
            }
            /*     */ finally {
                /*  92 */
                if (in != null) if (localThrowable3 != null) try {
                    in.close();
                } catch (Throwable localThrowable2) {
                    localThrowable3.addSuppressed(localThrowable2);
                }
                else {
                    in.close();
                    /*     */
                }
                /*     */
            }
            /*  95 */
            return null;
            /*     */
        }
        /*     */ catch (IOException e)
            /*     */ {
            /*  94 */
            logger.catching(e);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*     */ {
        /* 101 */
        return AbstractUnitFragment.FragmentType.IMAGE;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\UnitImageView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */