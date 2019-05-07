/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.pnfsoftware.jeb.rcpclient.IStatusIndicator;
/*     */ import com.pnfsoftware.jeb.rcpclient.StatusIndicatorData;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.action.IContributionItem;
/*     */ import org.eclipse.jface.action.StatusLineContributionItem;
/*     */ import org.eclipse.jface.action.StatusLineManager;
/*     */ import org.eclipse.swt.widgets.Control;
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
/*     */
/*     */
/*     */
/*     */
/*     */ public class JebStatusIndicator
        /*     */ implements IStatusIndicator
        /*     */ {
    /*  37 */   private static final ILogger logger = GlobalLog.getLogger(JebStatusIndicator.class);
    /*     */
    /*     */   private StatusLineManager statusManager;
    /*     */
    /*     */   private Display display;
    /*     */
    /*     */   private String text0;
    /*  44 */   private List<StatusLineContributionItem> contribs = new ArrayList();

    /*     */
    /*     */
    public JebStatusIndicator(StatusLineManager statusManager) {
        /*  47 */
        this.statusManager = statusManager;
        /*  48 */
        this.display = statusManager.getControl().getDisplay();
        /*     */
        /*     */
        /*     */
        /*  52 */
        StatusLineContributionItem contrib = new StatusLineContributionItem("area1", 80);
        /*  53 */
        contrib.setText(" ");
        /*  54 */
        statusManager.add(contrib);
        /*  55 */
        this.contribs.add(contrib);
        /*     */
        /*  57 */
        contrib = new StatusLineContributionItem("area2", 20);
        /*  58 */
        contrib.setText(" ");
        /*  59 */
        statusManager.add(contrib);
        /*  60 */
        this.contribs.add(contrib);
        /*     */
        /*  62 */
        contrib = new StatusLineContributionItem("area3", 10);
        /*  63 */
        contrib.setText(" ");
        /*  64 */
        statusManager.add(contrib);
        /*  65 */
        this.contribs.add(contrib);
        /*     */
        /*  67 */
        statusManager.setCancelEnabled(true);
        /*  68 */
        statusManager.update(true);
        /*     */
    }

    /*     */
    /*     */
    public StatusLineContributionItem getContribution(String id)
    /*     */ {
        /*  73 */
        for (IContributionItem item : this.statusManager.getItems()) {
            /*  74 */
            if (Strings.equals(id, item.getId())) {
                /*  75 */
                return (StatusLineContributionItem) item;
                /*     */
            }
            /*     */
        }
        /*  78 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public boolean hasContribution(String id)
    /*     */ {
        /*  83 */
        return getContribution(id) != null;
        /*     */
    }

    /*     */
    /*     */
    public void addContribution(StatusLineContributionItem item)
    /*     */ {
        /*  88 */
        this.statusManager.add(item);
        /*  89 */
        this.contribs.add(item);
        /*  90 */
        this.statusManager.update(true);
        /*     */
    }

    /*     */
    /*     */
    public boolean removeContribution(StatusLineContributionItem item)
    /*     */ {
        /*  95 */
        if (this.statusManager.remove(item) == null) {
            /*  96 */
            return false;
            /*     */
        }
        /*  98 */
        this.contribs.remove(item);
        /*  99 */
        this.statusManager.update(true);
        /* 100 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public boolean removeContribution(String id)
    /*     */ {
        /* 105 */
        StatusLineContributionItem contrib = getContribution(id);
        /* 106 */
        if (contrib == null) {
            /* 107 */
            return false;
            /*     */
        }
        /* 109 */
        return removeContribution(contrib);
        /*     */
    }

    /*     */
    /*     */
    public void clearAdditionalContributions()
    /*     */ {
        /* 114 */
        int cnt = 0;
        /* 115 */
        while (this.contribs.size() > 3) {
            /* 116 */
            this.statusManager.remove((IContributionItem) this.contribs.remove(3));
            /* 117 */
            cnt++;
            /*     */
        }
        /* 119 */
        if (cnt > 0) {
            /* 120 */
            this.statusManager.update(true);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public StatusIndicatorData save()
    /*     */ {
        /* 126 */
        return new StatusIndicatorData(new String[]{getText()});
        /*     */
    }

    /*     */
    /*     */
    public boolean restore(StatusIndicatorData data)
    /*     */ {
        /* 131 */
        if (data.getElements().isEmpty()) {
            /* 132 */
            return false;
            /*     */
        }
        /* 134 */
        setText((String) data.getElements().get(0));
        /* 135 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    public void clear()
    /*     */ {
        /* 140 */
        setText(null);
        /*     */
    }

    /*     */
    /*     */
    public void setText(String s)
    /*     */ {
        /* 145 */
        setText(0, s);
        /*     */
    }

    /*     */
    /*     */
    public String getText()
    /*     */ {
        /* 150 */
        return getText(0);
        /*     */
    }

    /*     */
    /*     */
    public void setText(final int index, final String s)
    /*     */ {
        /* 155 */
        if ((index < 0) || (index > this.contribs.size())) {
            /* 156 */
            return;
            /*     */
        }
        /* 158 */
        UIExecutor.asyncIfNotOnUIThread(this.display, new UIRunnable()
                /*     */ {
            /*     */
            public void runi() {
                /* 161 */
                String s2 = Strings.safe(s, "");
                /* 162 */
                if (index == 0) {
                    /* 163 */
                    JebStatusIndicator.this.text0 = s2;
                    /* 164 */
                    JebStatusIndicator.this.statusManager.setMessage(s2);
                    /*     */
                }
                /*     */
                else {
                    /* 167 */
                    ((StatusLineContributionItem) JebStatusIndicator.this.contribs.get(index - 1)).setText(s2);
                    /*     */
                }
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public String getText(int index)
    /*     */ {
        /* 175 */
        if ((index < 0) || (index > this.contribs.size())) {
            /* 176 */
            return null;
            /*     */
        }
        /* 178 */
        if (index == 0) {
            /* 179 */
            return this.text0;
            /*     */
        }
        /* 181 */
        return ((StatusLineContributionItem) this.contribs.get(index - 1)).getText();
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\JebStatusIndicator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */