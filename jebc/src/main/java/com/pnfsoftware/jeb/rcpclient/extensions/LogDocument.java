
package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public class LogDocument
        extends Document {
    private static final ILogger logger = GlobalLog.getLogger(LogDocument.class);
    private int limit;
    private int limitHalf;

    public LogDocument(int limit) {
        this.limit = Math.max(0, limit);
        this.limitHalf = (limit / 2);
    }

    public int getLimit() {
        return this.limit;
    }

    public synchronized void append(String additionString) {
        try {
            int addLen = additionString.length();
            int curLen = getLength();
            int hypLen = curLen + addLen;
            if ((this.limit > 0) && (hypLen > this.limit)) {
                if (addLen > this.limit) {
                    String text = additionString.substring(addLen - this.limit, addLen);
                    set(text);
                } else if (addLen > this.limitHalf) {
                    set(additionString);
                } else {
                    int r = this.limitHalf - addLen;
                    String text = get(curLen - r, r) + additionString;
                    set(text);
                }
            } else {
                replace(curLen, 0, additionString);
            }
        } catch (BadLocationException e) {
            logger.catching(e);
        }
    }
}


