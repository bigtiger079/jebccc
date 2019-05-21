package com.pnfsoftware.jeb.rcpclient.dialogs.options;

import com.pnfsoftware.jeb.util.format.Strings;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Control;

public class AbstractOptionsSimpleWidget {
    private static final String DEFAULT_TOKENIZER = "\\|";
    private static final String DEFAULT_RETOKENIZER = "|";
    protected OptionsChanges.Changes changes;
    protected OptionsSimpleListener listener;
    protected String propertyKey;

    public AbstractOptionsSimpleWidget(OptionsChanges.Changes changes, OptionsSimpleListener listener, String propertyKey) {
        this.changes = changes;
        this.listener = listener;
        this.propertyKey = propertyKey;
    }

    public String getToken(int tokenPosition) {
        return getToken(getValue(), tokenPosition);
    }

    public static String getToken(String property, int tokenPosition) {
        if (property == null) {
            return "";
        }
        String[] tokens = property.split(DEFAULT_TOKENIZER, -1);
        if (tokenPosition >= tokens.length) {
            return "";
        }
        return tokens[tokenPosition];
    }

    protected String getNewValue(String newText, String previousValue, int tokenPosition) {
        String currentValue = null;
        if (this.changes.getChange(this.propertyKey) != null) {
            currentValue = this.changes.getChange(this.propertyKey).toString();
        } else {
            currentValue = previousValue;
        }
        if (currentValue == null) {
            currentValue = "";
        }
        String[] tokens = currentValue.split(DEFAULT_TOKENIZER, -1);
        String[] newTokens;
        if (tokenPosition >= tokens.length) {
            newTokens = new String[tokenPosition + 1];
        } else {
            newTokens = new String[tokens.length];
        }
        System.arraycopy(tokens, 0, newTokens, 0, tokens.length);
        newTokens[tokenPosition] = newText;
        return StringUtils.join(newTokens, DEFAULT_RETOKENIZER);
    }

    protected void addSimpleViewElements(Control c) {
        addSimpleViewElements(this.listener, this.propertyKey, c);
    }

    public static void addSimpleViewElements(OptionsSimpleListener listener, String propertyKey, Control c) {
        listener.addElement(propertyKey, c);
    }

    protected Object getProperty() {
        return getProperty(this.changes, this.propertyKey, false);
    }

    public static Object getProperty(OptionsChanges.Changes c, String propertyKey, boolean retrieveOldDataIfEmpty) {
        Object previousData = c.getChange(propertyKey);
        if ((previousData == null) || ((retrieveOldDataIfEmpty) && (Strings.isBlank(previousData.toString())))) {
            previousData = c.getValue(propertyKey);
        }
        return previousData;
    }

    protected String getValue() {
        if ((this.changes == null) || (this.propertyKey == null)) {
            return "";
        }
        return this.changes.getString(this.propertyKey);
    }
}


