/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.client.Licensing;
/*     */ import com.pnfsoftware.jeb.core.properties.IPropertyManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.WidgetBoundsManager;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.StateDataProvider;
/*     */ import com.pnfsoftware.jeb.rcpclient.util.TextHistoryCollection;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.lang3.BooleanUtils;

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
/*     */ public class RcpClientProperties
        /*     */ {
    /*     */   private IPropertyManager pm;

    /*     */
    /*     */
    public RcpClientProperties(IPropertyManager pm)
    /*     */ {
        /*  31 */
        this.pm = pm;
        /*     */
    }

    /*     */
    /*     */
    public boolean allowInternalCommands() {
        /*  35 */
        return (Licensing.isDebugBuild()) ||
                /*  36 */       (BooleanUtils.toBoolean(Boolean.valueOf(this.pm.getBoolean(".ui.AllowInternalCommands"))));
        /*     */
    }

    /*     */
    /*     */
    public int getLoggerMaxLength() {
        /*  40 */
        return this.pm.getInteger(".ui.LoggerMaxLength");
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   TextHistoryCollection parseTextHistoryData(String s)
    /*     */ {
        /*  48 */
        TextHistoryCollection textHistoryData = TextHistoryCollection.decode(s);
        /*  49 */
        if (textHistoryData == null) {
            /*  50 */
            textHistoryData = new TextHistoryCollection();
            /*     */
        }
        /*  52 */
        return textHistoryData;
        /*     */
    }

    /*     */
    /*     */   TextHistoryCollection getTextHistoryData() {
        /*  56 */
        String s = this.pm.getString(".ui.TextHistoryData");
        /*  57 */
        return parseTextHistoryData(s);
        /*     */
    }

    /*     */
    /*     */   String buildTextHistoryData(TextHistoryCollection data) {
        /*  61 */
        String s = "";
        /*  62 */
        if (data != null) {
            /*  63 */
            s = data.encode();
            /*     */
        }
        /*  65 */
        return s;
        /*     */
    }

    /*     */
    /*     */   void setTextHistoryData(TextHistoryCollection data) {
        /*  69 */
        String s = buildTextHistoryData(data);
        /*  70 */
        this.pm.setString(".ui.TextHistoryData", s);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   List<String> parseRecentlyOpenedFiles(String s)
    /*     */ {
        /*  78 */
        if (s == null) {
            /*  79 */
            return new ArrayList();
            /*     */
        }
        /*  81 */
        return Strings.decodeList(s);
        /*     */
    }

    /*     */
    /*     */   List<String> getRecentlyOpenedFiles() {
        /*  85 */
        String s = this.pm.getString(".ui.RecentlyOpenedFiles");
        /*  86 */
        return parseRecentlyOpenedFiles(s);
        /*     */
    }

    /*     */
    /*     */   String buildRecentlyOpenedFiles(List<String> files) {
        /*  90 */
        return files == null ? "" : Strings.encodeList(files);
        /*     */
    }

    /*     */
    /*     */   void setRecentlyOpenedFiles(List<String> files) {
        /*  94 */
        String s = buildRecentlyOpenedFiles(files);
        /*  95 */
        this.pm.setString(".ui.RecentlyOpenedFiles", s);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   List<String> parseRecentlyExecutedScripts(String s)
    /*     */ {
        /* 103 */
        if (s == null) {
            /* 104 */
            return new ArrayList();
            /*     */
        }
        /* 106 */
        return Strings.decodeList(s);
        /*     */
    }

    /*     */
    /*     */   List<String> getRecentlyExecutedScripts() {
        /* 110 */
        String s = this.pm.getString(".ui.RecentlyExecutedScripts");
        /* 111 */
        return parseRecentlyExecutedScripts(s);
        /*     */
    }

    /*     */
    /*     */   String buildRecentlyExecutedScripts(List<String> files) {
        /* 115 */
        return files == null ? "" : Strings.encodeList(files);
        /*     */
    }

    /*     */
    /*     */   void setRecentlyExecutedScripts(List<String> files) {
        /* 119 */
        String s = buildRecentlyExecutedScripts(files);
        /* 120 */
        this.pm.setString(".ui.RecentlyExecutedScripts", s);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   WidgetBoundsManager getWidgetBoundsManager()
    /*     */ {
        /* 128 */
        String s = this.pm.getString(".ui.WidgetBoundsHistoryData");
        /* 129 */
        return new WidgetBoundsManager(s);
        /*     */
    }

    /*     */
    /*     */   void setWidgetBoundsManager(WidgetBoundsManager manager) {
        /* 133 */
        String s = manager == null ? "" : manager.encode();
        /* 134 */
        this.pm.setString(".ui.WidgetBoundsHistoryData", s);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   StateDataProvider getWidgetPersistenceProvider()
    /*     */ {
        /* 142 */
        String s = this.pm.getString(".ui.WidgetPersistenceData");
        /* 143 */
        return new StateDataProvider(s);
        /*     */
    }

    /*     */
    /*     */   void setWidgetPersistenceProvider(StateDataProvider provider) {
        /* 147 */
        String s = provider == null ? "" : provider.encode();
        /* 148 */
        this.pm.setString(".ui.WidgetPersistenceData", s);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */   StateDataProvider getDialogPersistenceDataProvider()
    /*     */ {
        /* 156 */
        String s = this.pm.getString(".ui.DialogPersistenceData");
        /* 157 */
        return new StateDataProvider(s);
        /*     */
    }

    /*     */
    /*     */   void setDialogPersistenceDataProvider(StateDataProvider provider) {
        /* 161 */
        String s = provider == null ? "" : provider.encode();
        /* 162 */
        this.pm.setString(".ui.DialogPersistenceData", s);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public boolean getProjectUnitSync()
    /*     */ {
        /* 170 */
        return this.pm.getBoolean(".ui.ProjectUnitSync");
        /*     */
    }

    /*     */
    /*     */
    public void setProjectUnitSync(boolean enabled) {
        /* 174 */
        this.pm.setBoolean(".ui.ProjectUnitSync", Boolean.valueOf(enabled));
        /*     */
    }

    /*     */
    /*     */
    public boolean getConsolePartSync() {
        /* 178 */
        return this.pm.getBoolean(".ui.ConsolePartSync");
        /*     */
    }

    /*     */
    /*     */
    public void setConsolePartSync(boolean enabled) {
        /* 182 */
        this.pm.setBoolean(".ui.ConsolePartSync", Boolean.valueOf(enabled));
        /*     */
    }

    /*     */
    /*     */
    public boolean getDoNotReplaceViews() {
        /* 186 */
        return this.pm.getBoolean(".ui.DoNotReplaceViews");
        /*     */
    }

    /*     */
    /*     */
    public void setDoNotReplaceViews(boolean enabled) {
        /* 190 */
        this.pm.setBoolean(".ui.DoNotReplaceViews", Boolean.valueOf(enabled));
        /*     */
    }

    /*     */
    /*     */
    public boolean getOptionsDialogAdvancedMode() {
        /* 194 */
        return this.pm.getBoolean(".ui.OptionsDialogAdvancedMode");
        /*     */
    }

    /*     */
    /*     */
    public void setOptionsDialogAdvancedMode(boolean enabled) {
        /* 198 */
        this.pm.setBoolean(".ui.OptionsDialogAdvancedMode", Boolean.valueOf(enabled));
        /*     */
    }

    /*     */
    /*     */
    public boolean shouldAutoOpenDefaultUnit() {
        /* 202 */
        return this.pm.getBoolean(".ui.AutoOpenDefaultUnit");
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\RcpClientProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */