package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import java.io.InputStream;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class AnimatedGif extends Canvas {
    private final ImageLoader loader = new ImageLoader();
    private Display display;
    private int img;
    private volatile boolean animating;
    private Thread thread;

    public AnimatedGif(Composite parent, int style) {
        super(parent, style);
        this.display = parent.getDisplay();
    }

    public void load(InputStream resource) {
        this.loader.load(resource);
    }

    public void animate() {
        if (this.thread == null) {
            this.thread = createThread();
            this.thread.setDaemon(true);
        }
        if (this.thread.isAlive()) {
            return;
        }
        this.thread.start();
    }

    public void stop() {
        this.animating = false;
        if (this.thread != null) {
            try {
                this.thread.join();
                this.thread = null;
            } catch (InterruptedException localInterruptedException) {
            }
        }
    }

    private Thread createThread() {
        return new Thread() {
            long currentTime = System.currentTimeMillis();

            public void run() {
                AnimatedGif.this.animating = true;
                while (AnimatedGif.this.animating) {
                    AnimatedGif.this.img = (AnimatedGif.this.img == AnimatedGif.this.loader.data.length - 1 ? 0 : AnimatedGif.this.img + 1);
                    int delayTime = Math.max(50, 10 * AnimatedGif.this.loader.data[AnimatedGif.this.img].delayTime);
                    long now = System.currentTimeMillis();
                    long ms = Math.max(this.currentTime + delayTime - now, 5L);
                    this.currentTime += delayTime;
                    try {
                        Thread.sleep(ms);
                    } catch (Exception e) {
                        return;
                    }
                    if (!AnimatedGif.this.display.isDisposed()) {
                        AnimatedGif.this.display.syncExec(new Runnable() {
                            public void run() {
                                GC gc = null;
                                Image frameImage = null;
                                try {
                                    gc = new GC(AnimatedGif.this);
                                    ImageData nextFrameData = AnimatedGif.this.loader.data[AnimatedGif.this.img];
                                    frameImage = new Image(AnimatedGif.this.display, nextFrameData);
                                    gc.drawImage(frameImage, nextFrameData.x, nextFrameData.y);
                                } catch (SWTException localSWTException) {
                                } finally {
                                    if ((frameImage != null) && (!frameImage.isDisposed())) {
                                        frameImage.dispose();
                                    }
                                    if ((gc != null) && (!gc.isDisposed())) {
                                        gc.dispose();
                                    }
                                }
                            }
                        });
                    }
                }
                AnimatedGif.this.display.syncExec(new Runnable() {
                    public void run() {
                        // Byte code:
                        //   0: aconst_null
                        //   1: astore_1
                        //   2: new 7	org/eclipse/swt/graphics/GC
                        //   5: dup
                        //   6: aload_0
                        //   7: getfield 10	com/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif$1$2:this$1	Lcom/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif$1;
                        //   10: getfield 9	com/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif$1:this$0	Lcom/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif;
                        //   13: invokespecial 15	org/eclipse/swt/graphics/GC:<init>	(Lorg/eclipse/swt/graphics/Drawable;)V
                        //   16: astore_1
                        //   17: aload_1
                        //   18: iconst_0
                        //   19: iconst_0
                        //   20: aload_0
                        //   21: getfield 10	com/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif$1$2:this$1	Lcom/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif$1;
                        //   24: getfield 9	com/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif$1:this$0	Lcom/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif;
                        //   27: invokevirtual 13	com/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif:getBounds	()Lorg/eclipse/swt/graphics/Rectangle;
                        //   30: getfield 12	org/eclipse/swt/graphics/Rectangle:width	I
                        //   33: aload_0
                        //   34: getfield 10	com/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif$1$2:this$1	Lcom/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif$1;
                        //   37: getfield 9	com/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif$1:this$0	Lcom/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif;
                        //   40: invokevirtual 13	com/pnfsoftware/jeb/rcpclient/extensions/controls/AnimatedGif:getBounds	()Lorg/eclipse/swt/graphics/Rectangle;
                        //   43: getfield 11	org/eclipse/swt/graphics/Rectangle:height	I
                        //   46: invokevirtual 17	org/eclipse/swt/graphics/GC:fillRectangle	(IIII)V
                        //   49: aload_1
                        //   50: ifnull +35 -> 85
                        //   53: aload_1
                        //   54: invokevirtual 18	org/eclipse/swt/graphics/GC:isDisposed	()Z
                        //   57: ifne +28 -> 85
                        //   60: aload_1
                        //   61: invokevirtual 16	org/eclipse/swt/graphics/GC:dispose	()V
                        //   64: goto +21 -> 85
                        //   67: astore_2
                        //   68: aload_1
                        //   69: ifnull +14 -> 83
                        //   72: aload_1
                        //   73: invokevirtual 18	org/eclipse/swt/graphics/GC:isDisposed	()Z
                        //   76: ifne +7 -> 83
                        //   79: aload_1
                        //   80: invokevirtual 16	org/eclipse/swt/graphics/GC:dispose	()V
                        //   83: aload_2
                        //   84: athrow
                        //   85: return
                        // Line number table:
                        //   Java source line #129	-> byte code offset #0
                        //   Java source line #131	-> byte code offset #2
                        //   Java source line #132	-> byte code offset #17
                        //   Java source line #135	-> byte code offset #49
                        //   Java source line #136	-> byte code offset #60
                        //   Java source line #135	-> byte code offset #67
                        //   Java source line #136	-> byte code offset #79
                        //   Java source line #138	-> byte code offset #83
                        //   Java source line #139	-> byte code offset #85
                        // Local variable table:
                        //   start	length	slot	name	signature
                        //   0	86	0	this	2
                        //   1	79	1	gc	GC
                        //   67	17	2	localObject	Object
                        // Exception table:
                        //   from	to	target	type
                        //   2	49	67	finally
                    }
                });
            }
        };
    }
}
