
package com.pnfsoftware.jeb.rcpclient.parts;


import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.StatusIndicatorData;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


public class JebStatusIndicator
        implements IStatusIndicator {
    private static final ILogger logger = GlobalLog.getLogger(JebStatusIndicator.class);

    private StatusLineManager statusManager;

    private Display display;

    private String text0;
    private List<StatusLineContributionItem> contribs = new ArrayList();


    public JebStatusIndicator(StatusLineManager statusManager) {

        this.statusManager = statusManager;

        this.display = statusManager.getControl().getDisplay();


        StatusLineContributionItem contrib = new StatusLineContributionItem("area1", 80);

        contrib.setText(" ");

        statusManager.add(contrib);

        this.contribs.add(contrib);


        contrib = new StatusLineContributionItem("area2", 20);

        contrib.setText(" ");

        statusManager.add(contrib);

        this.contribs.add(contrib);


        contrib = new StatusLineContributionItem("area3", 10);

        contrib.setText(" ");

        statusManager.add(contrib);

        this.contribs.add(contrib);


        statusManager.setCancelEnabled(true);

        statusManager.update(true);

    }


    public StatusLineContributionItem getContribution(String id) {

        for (IContributionItem item : this.statusManager.getItems()) {

            if (Strings.equals(id, item.getId())) {

                return (StatusLineContributionItem) item;

            }

        }

        return null;

    }


    public boolean hasContribution(String id) {

        return getContribution(id) != null;

    }


    public void addContribution(StatusLineContributionItem item) {

        this.statusManager.add(item);

        this.contribs.add(item);

        this.statusManager.update(true);

    }


    public boolean removeContribution(StatusLineContributionItem item) {

        if (this.statusManager.remove(item) == null) {

            return false;

        }

        this.contribs.remove(item);

        this.statusManager.update(true);

        return true;

    }


    public boolean removeContribution(String id) {

        StatusLineContributionItem contrib = getContribution(id);

        if (contrib == null) {

            return false;

        }

        return removeContribution(contrib);

    }


    public void clearAdditionalContributions() {

        int cnt = 0;

        while (this.contribs.size() > 3) {

            this.statusManager.remove((IContributionItem) this.contribs.remove(3));

            cnt++;

        }

        if (cnt > 0) {

            this.statusManager.update(true);

        }

    }


    public StatusIndicatorData save() {

        return new StatusIndicatorData(new String[]{getText()});

    }


    public boolean restore(StatusIndicatorData data) {

        if (data.getElements().isEmpty()) {

            return false;

        }

        setText((String) data.getElements().get(0));

        return true;

    }


    public void clear() {

        setText(null);

    }


    public void setText(String s) {

        setText(0, s);

    }


    public String getText() {

        return getText(0);

    }


    public void setText(final int index, final String s) {

        if ((index < 0) || (index > this.contribs.size())) {

            return;

        }

        UIExecutor.asyncIfNotOnUIThread(this.display, new UIRunnable() {

            public void runi() {

                String s2 = Strings.safe(s, "");

                if (index == 0) {

                    JebStatusIndicator.this.text0 = s2;

                    JebStatusIndicator.this.statusManager.setMessage(s2);

                } else {

                    ((StatusLineContributionItem) JebStatusIndicator.this.contribs.get(index - 1)).setText(s2);

                }

            }

        });

    }


    public String getText(int index) {

        if ((index < 0) || (index > this.contribs.size())) {

            return null;

        }

        if (index == 0) {

            return this.text0;

        }

        return ((StatusLineContributionItem) this.contribs.get(index - 1)).getText();

    }

}


