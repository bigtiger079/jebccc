
package com.pnfsoftware.jeb.rcpclient.extensions;


import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Display;


public abstract class AbstractRefresher {
    private static final ILogger logger = GlobalLog.getLogger(AbstractRefresher.class);
    private Display display;
    private String name;
    private AtomicInteger state;


    public AbstractRefresher(Display display, String name) {

        this.display = display;

        this.name = name;

        this.state = new AtomicInteger(0);

    }


    public AbstractRefresher(Display display) {

        this(display, null);

    }


    public Display getDisplay() {

        return this.display;

    }


    public String getName() {

        return this.name;

    }


    public final int getState() {

        return this.state.get();

    }


    public final void request() {

        boolean waitRefreshFinished = false;

        synchronized (this.state) {

            int v = getState();

            if (v == 0) {

                this.state.set(1);

            } else if (v == 1) {

                this.state.set(2);

                waitRefreshFinished = true;

            } else {

                return;

            }

        }


        if (waitRefreshFinished) {

            ThreadUtil.start(new Runnable() {

                public void run() {

                    do {

                        try {

                            Thread.sleep(200L);

                        } catch (InterruptedException e) {

                            if (!AbstractRefresher.this.state.compareAndSet(2, 1)) {

                                AbstractRefresher.this.state.set(0);

                            }

                            return;

                        }


                    } while (AbstractRefresher.this.getState() != 3);


                    AbstractRefresher.this.state.set(1);

                    AbstractRefresher.this.launchRefresh();

                }


            });

        } else {

            launchRefresh();

        }

    }


    private void launchRefresh() {

        UIExecutor.async(this.display, new UIRunnable(String.format("Refresher(%s)", new Object[]{this.name})) {

            public void runi() {

                synchronized (AbstractRefresher.this) {

                    if (AbstractRefresher.this.shouldPerformRefresh()) {

                        AbstractRefresher.this.performRefresh();

                        synchronized (AbstractRefresher.this.state) {

                            if (!AbstractRefresher.this.state.compareAndSet(2, 3)) {

                                AbstractRefresher.this.state.set(0);

                            }

                        }

                    }

                }

            }

        });

    }


    protected boolean shouldPerformRefresh() {

        return true;

    }


    protected abstract void performRefresh();

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\AbstractRefresher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */