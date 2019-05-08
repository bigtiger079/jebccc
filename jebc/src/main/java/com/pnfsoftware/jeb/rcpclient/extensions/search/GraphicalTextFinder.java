
package com.pnfsoftware.jeb.rcpclient.extensions.search;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.rcpclient.IGraphicalTaskExecutor;
import com.pnfsoftware.jeb.util.concurrent.MonitorInfoAdapter;
import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;

public class GraphicalTextFinder<FindTextResult extends IFindTextResult> {
    private IFindTextImpl<FindTextResult> target;
    private IGraphicalTaskExecutor taskExecutor;
    private FindTextResult lastResult;
    private boolean searchInterrupted;

    public GraphicalTextFinder(IFindTextImpl<FindTextResult> target, IGraphicalTaskExecutor taskExecutor) {
        if (target == null) {
            throw new NullPointerException();
        }
        this.target = target;
        this.taskExecutor = taskExecutor;
    }

    public IFindTextImpl<?> getFindTextImpl() {
        return this.target;
    }

    public boolean isReady() {
        return this.target.getFindTextOptions(false).getSearchString().length() > 0;
    }

    public synchronized void search(final FindTextOptions opt) {
        this.target.getFindTextOptions(true);
        this.lastResult = null;
        this.searchInterrupted = false;
        if (this.taskExecutor == null) {
            this.lastResult = this.target.findText(opt);
        } else {
            MonitorInfoAdapter monitor = new MonitorInfoAdapter(3000L, 300L) {
                public void onInterrupt() {
                    GraphicalTextFinder.this.searchInterrupted = true;
                }
            };
            ThreadUtil.monitor(ThreadUtil.start(new Runnable() {
                public void run() {
                    GraphicalTextFinder.this.lastResult = GraphicalTextFinder.this.target.findText(opt);
                }
            }), monitor);
            if (this.lastResult == null) {
                if (!this.searchInterrupted) {
                    return;
                }
                this.taskExecutor.executeTask(S.s(714) + "...", new Runnable() {
                    public void run() {
                        GraphicalTextFinder.this.lastResult = GraphicalTextFinder.this.target.findText(opt);
                    }
                });
            }
        }
        if (this.lastResult != null) {
            this.target.processFindResult(this.lastResult);
        }
    }
}


