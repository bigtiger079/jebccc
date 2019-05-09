package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.rcpclient.extensions.WidgetBoundsManager;
import com.pnfsoftware.jeb.rcpclient.util.StateDataProvider;
import com.pnfsoftware.jeb.rcpclient.util.TextHistoryCollection;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

public class RcpClientProperties {
    private IPropertyManager pm;

    public RcpClientProperties(IPropertyManager pm) {
        this.pm = pm;
    }

    public boolean allowInternalCommands() {
        return (Licensing.isDebugBuild()) || (BooleanUtils.toBoolean(Boolean.valueOf(this.pm.getBoolean(".ui.AllowInternalCommands"))));
    }

    public int getLoggerMaxLength() {
        return this.pm.getInteger(".ui.LoggerMaxLength");
    }

    TextHistoryCollection parseTextHistoryData(String s) {
        TextHistoryCollection textHistoryData = TextHistoryCollection.decode(s);
        if (textHistoryData == null) {
            textHistoryData = new TextHistoryCollection();
        }
        return textHistoryData;
    }

    TextHistoryCollection getTextHistoryData() {
        String s = this.pm.getString(".ui.TextHistoryData");
        return parseTextHistoryData(s);
    }

    String buildTextHistoryData(TextHistoryCollection data) {
        String s = "";
        if (data != null) {
            s = data.encode();
        }
        return s;
    }

    void setTextHistoryData(TextHistoryCollection data) {
        String s = buildTextHistoryData(data);
        this.pm.setString(".ui.TextHistoryData", s);
    }

    List<String> parseRecentlyOpenedFiles(String s) {
        if (s == null) {
            return new ArrayList<>();
        }
        return Strings.decodeList(s);
    }

    List<String> getRecentlyOpenedFiles() {
        String s = this.pm.getString(".ui.RecentlyOpenedFiles");
        return parseRecentlyOpenedFiles(s);
    }

    String buildRecentlyOpenedFiles(List<String> files) {
        return files == null ? "" : Strings.encodeList(files);
    }

    void setRecentlyOpenedFiles(List<String> files) {
        String s = buildRecentlyOpenedFiles(files);
        this.pm.setString(".ui.RecentlyOpenedFiles", s);
    }

    List<String> parseRecentlyExecutedScripts(String s) {
        if (s == null) {
            return new ArrayList<>();
        }
        return Strings.decodeList(s);
    }

    List<String> getRecentlyExecutedScripts() {
        String s = this.pm.getString(".ui.RecentlyExecutedScripts");
        return parseRecentlyExecutedScripts(s);
    }

    String buildRecentlyExecutedScripts(List<String> files) {
        return files == null ? "" : Strings.encodeList(files);
    }

    void setRecentlyExecutedScripts(List<String> files) {
        String s = buildRecentlyExecutedScripts(files);
        this.pm.setString(".ui.RecentlyExecutedScripts", s);
    }

    WidgetBoundsManager getWidgetBoundsManager() {
        String s = this.pm.getString(".ui.WidgetBoundsHistoryData");
        return new WidgetBoundsManager(s);
    }

    void setWidgetBoundsManager(WidgetBoundsManager manager) {
        String s = manager == null ? "" : manager.encode();
        this.pm.setString(".ui.WidgetBoundsHistoryData", s);
    }

    StateDataProvider getWidgetPersistenceProvider() {
        String s = this.pm.getString(".ui.WidgetPersistenceData");
        return new StateDataProvider(s);
    }

    void setWidgetPersistenceProvider(StateDataProvider provider) {
        String s = provider == null ? "" : provider.encode();
        this.pm.setString(".ui.WidgetPersistenceData", s);
    }

    StateDataProvider getDialogPersistenceDataProvider() {
        String s = this.pm.getString(".ui.DialogPersistenceData");
        return new StateDataProvider(s);
    }

    void setDialogPersistenceDataProvider(StateDataProvider provider) {
        String s = provider == null ? "" : provider.encode();
        this.pm.setString(".ui.DialogPersistenceData", s);
    }

    public boolean getProjectUnitSync() {
        return this.pm.getBoolean(".ui.ProjectUnitSync");
    }

    public void setProjectUnitSync(boolean enabled) {
        this.pm.setBoolean(".ui.ProjectUnitSync", Boolean.valueOf(enabled));
    }

    public boolean getConsolePartSync() {
        return this.pm.getBoolean(".ui.ConsolePartSync");
    }

    public void setConsolePartSync(boolean enabled) {
        this.pm.setBoolean(".ui.ConsolePartSync", Boolean.valueOf(enabled));
    }

    public boolean getDoNotReplaceViews() {
        return this.pm.getBoolean(".ui.DoNotReplaceViews");
    }

    public void setDoNotReplaceViews(boolean enabled) {
        this.pm.setBoolean(".ui.DoNotReplaceViews", Boolean.valueOf(enabled));
    }

    public boolean getOptionsDialogAdvancedMode() {
        return this.pm.getBoolean(".ui.OptionsDialogAdvancedMode");
    }

    public void setOptionsDialogAdvancedMode(boolean enabled) {
        this.pm.setBoolean(".ui.OptionsDialogAdvancedMode", Boolean.valueOf(enabled));
    }

    public boolean shouldAutoOpenDefaultUnit() {
        return this.pm.getBoolean(".ui.AutoOpenDefaultUnit");
    }
}


