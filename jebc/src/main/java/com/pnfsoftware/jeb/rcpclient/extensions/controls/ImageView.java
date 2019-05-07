
package com.pnfsoftware.jeb.rcpclient.extensions.controls;


import java.io.ByteArrayInputStream;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;


public class ImageView
        extends Composite {
    private Canvas canvas;
    private Point origin;


    public ImageView(Composite parent, byte[] data) {

        this(parent, new Image(parent.getDisplay(), new ByteArrayInputStream(data)));

    }


    public ImageView(Composite parent, final Image image) {

        super(parent, 0);

        setLayout(new FillLayout());


        this.canvas = new Canvas(this, 1049344);

        this.origin = new Point(0, 0);


        final ScrollBar hBar = this.canvas.getHorizontalBar();

        hBar.addListener(13, new Listener() {

            public void handleEvent(Event e) {

                int hSelection = hBar.getSelection();

                int destX = -hSelection - ImageView.this.origin.x;

                Rectangle rect = image.getBounds();

                ImageView.this.canvas.scroll(destX, 0, 0, 0, rect.width, rect.height, false);

                ImageView.this.origin.x = (-hSelection);

            }


        });

        final ScrollBar vBar = this.canvas.getVerticalBar();

        vBar.addListener(13, new Listener() {

            public void handleEvent(Event e) {

                int vSelection = vBar.getSelection();

                int destY = -vSelection - ImageView.this.origin.y;

                Rectangle rect = image.getBounds();

                ImageView.this.canvas.scroll(0, destY, 0, 0, rect.width, rect.height, false);

                ImageView.this.origin.y = (-vSelection);

            }


        });

        this.canvas.addListener(11, new Listener() {

            public void handleEvent(Event e) {

                Rectangle rect = image.getBounds();

                Rectangle client = ImageView.this.canvas.getClientArea();

                hBar.setMaximum(rect.width);

                vBar.setMaximum(rect.height);

                hBar.setThumb(Math.min(rect.width, client.width));

                vBar.setThumb(Math.min(rect.height, client.height));

                int hPage = rect.width - client.width;

                int vPage = rect.height - client.height;

                int hSelection = hBar.getSelection();

                int vSelection = vBar.getSelection();

                if (hSelection >= hPage) {

                    if (hPage <= 0) {

                        hSelection = 0;

                    }

                    ImageView.this.origin.x = (-hSelection);

                }

                if (vSelection >= vPage) {

                    if (vPage <= 0) {

                        vSelection = 0;

                    }

                    ImageView.this.origin.y = (-vSelection);

                }

                ImageView.this.canvas.redraw();

            }


        });

        this.canvas.addListener(9, new Listener() {

            public void handleEvent(Event e) {

                GC gc = e.gc;

                gc.drawImage(image, ImageView.this.origin.x, ImageView.this.origin.y);

                Rectangle rect = image.getBounds();

                Rectangle client = ImageView.this.canvas.getClientArea();

                int marginWidth = client.width - rect.width;

                if (marginWidth > 0) {

                    gc.fillRectangle(rect.width, 0, marginWidth, client.height);

                }

                int marginHeight = client.height - rect.height;

                if (marginHeight > 0) {

                    gc.fillRectangle(0, rect.height, client.width, marginHeight);

                }

            }

        });

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\controls\ImageView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */