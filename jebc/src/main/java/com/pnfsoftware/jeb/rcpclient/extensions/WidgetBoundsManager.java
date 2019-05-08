package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Rectangle;

public class WidgetBoundsManager {
    private static final ILogger logger = GlobalLog.getLogger(WidgetBoundsManager.class);
    private Map<Integer, Rectangle> map = new HashMap();

    public WidgetBoundsManager() {
    }

    public WidgetBoundsManager(String encodedData) {
        loadFromString(encodedData);
    }

    public boolean loadFromString(String s) {
        String[] elts = s.split("\\|");
        int errcnt = 0;
        for (String elt : elts) {
            String[] kv = elt.split("=");
            try {
                int id = Integer.parseInt(kv[0]);
                String[] v = kv[1].split(",");
                int x = Integer.parseInt(v[0]);
                int y = Integer.parseInt(v[1]);
                int w = Integer.parseInt(v[2]);
                int h = Integer.parseInt(v[3]);
                Rectangle r = new Rectangle(x, y, w, h);
                this.map.put(Integer.valueOf(id), r);
            } catch (Exception e) {
                logger.debug("Invalid shell bounds entry: \"%s\"", new Object[]{elt});
                errcnt++;
            }
        }
        return errcnt == 0;
    }

    public String encode() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Iterator localIterator = this.map.keySet().iterator(); localIterator.hasNext(); ) {
            int widgetId = ((Integer) localIterator.next()).intValue();
            if (i >= 1) {
                sb.append("|");
            }
            Rectangle r = (Rectangle) this.map.get(Integer.valueOf(widgetId));
            sb.append(String.format("%d=%d,%d,%d,%d", new Object[]{Integer.valueOf(widgetId), Integer.valueOf(r.x), Integer.valueOf(r.y), Integer.valueOf(r.width), Integer.valueOf(r.height)}));
            i++;
        }
        return sb.toString();
    }

    public Rectangle getRecordedBounds(int widgetId) {
        return (Rectangle) this.map.get(Integer.valueOf(widgetId));
    }

    public void setRecordedBounds(int widgetId, Rectangle bounds) {
        if (bounds == null) {
            this.map.remove(Integer.valueOf(widgetId));
        } else {
            this.map.put(Integer.valueOf(widgetId), bounds);
        }
    }

    public void clearBounds() {
        this.map.clear();
    }
}


