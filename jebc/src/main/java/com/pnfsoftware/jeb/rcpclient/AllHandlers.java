package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.*;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerAttachHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerDetachHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerPauseHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerRestartHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerResumeThreadHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerRunHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerRunToLineHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerStepIntoHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerStepOutHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerStepOverHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerSuspendThreadHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerTerminateHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerToggleBreakpointHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditClearHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditCopyHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditCutHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindnextHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditOptionsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditPasteHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditSelectAllHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditStyleHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditSwitchThemeHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileAddHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileAdvancedUnitOptionsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileCloseHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileDeleteHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesContributionsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesInterpretersHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesParsersHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesPluginsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesSiglibsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesTypelibsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExitHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportActiveViewHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportAllBinaryUnitsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportDecompiledCodeHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileNotificationsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileOpenHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FilePropertiesHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileSaveAsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileSaveHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileScriptsExecuteHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileScriptsExecutelastHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileShareHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpAboutHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpApidocHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpChangelistHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpCheckupdateHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpDevPortalHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpFAQHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpGroupHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpUserManualHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.internal.InternalLoadModelHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.internal.InternalPrintModelHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.internal.InternalSaveModelHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionAutoSigningModeHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionCreateProcedureSignatureHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionCreateSignaturePackageHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionDefineCodeHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionDefineDataHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionDefineProcedureHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionDefineStringHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditArrayHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditCodeHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditInstructionHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditProcedureHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditStackframeHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditStringHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditTypeHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionOpenTypeEditorHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionSelectSignaturePackageHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionSelectTypeHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionStartAnalysisHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionStopAnalysisHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionUndefineHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationCanvasCenterHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationCanvasZoomInHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationCanvasZoomOutHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationCanvasZoomResetHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationDoNotReplaceViewsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationItemNextHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationItemPreviousHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationLocateInCodeHierarchyHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationLocateInGlobalGraphHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationLocateUnitHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowClosepartHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusHierarchyHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusLoggerHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusProjectExplorerHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusTerminalHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowRefreshHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowResetUIStateHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowToggleStatusHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowToggleToolbarHandler;
import com.pnfsoftware.jeb.util.base.OSType;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.Separator;

public class AllHandlers {
    private static final ILogger logger = GlobalLog.getLogger(AllHandlers.class);
    private static final AllHandlers instance = new AllHandlers();

    public static AllHandlers getInstance() {
        return instance;
    }

    private Map<Class<? extends JebBaseHandler>, JebBaseHandler> all = new LinkedHashMap();
    public static final int GRP_EDITION = 1;

    private AllHandlers() {
        add(new FileExportDecompiledCodeHandler());
        add(new FileExportActiveViewHandler());
        add(new FileExportAllBinaryUnitsHandler());
        add(new FileScriptsExecuteHandler());
        add(new FileScriptsExecutelastHandler());
        add(new FileEnginesPluginsHandler());
        add(new FileEnginesParsersHandler());
        add(new FileEnginesContributionsHandler());
        add(new FileEnginesInterpretersHandler());
        add(new FileEnginesTypelibsHandler());
        add(new FileEnginesSiglibsHandler());
        add(new FileOpenHandler());
        add(new FileAddHandler());
        add(new FileCloseHandler());
        add(new FileSaveHandler());
        add(new FileSaveAsHandler());
        add(new FilePropertiesHandler());
        add(new FileShareHandler());
        add(new FileDeleteHandler());
        add(new FileNotificationsHandler());
        add(new FileAdvancedUnitOptionsHandler());
        add(new FileExitHandler());
        add(new EditCutHandler());
        add(new EditCopyHandler());
        add(new EditPasteHandler());
        add(new EditSelectAllHandler());
        add(new EditClearHandler());
        add(new EditFindHandler());
        add(new EditFindnextHandler());
        add(new EditSwitchThemeHandler());
        add(new EditStyleHandler());
        add(new EditOptionsHandler());
        add(new NavigationDoNotReplaceViewsHandler());
        add(new NavigationLocateUnitHandler());
        add(new ActionNavigateForwardHandler());
        add(new ActionNavigateBackwardHandler());
        add(new ActionJumpToHandler());
        add(new ActionFollowHandler());
        add(new NavigationItemPreviousHandler());
        add(new NavigationItemNextHandler());
        add(new NavigationLocateInCodeHierarchyHandler());
        add(new NavigationLocateInGlobalGraphHandler());
        add(new NavigationCanvasCenterHandler());
        add(new NavigationCanvasZoomInHandler());
        add(new NavigationCanvasZoomOutHandler());
        add(new NavigationCanvasZoomResetHandler());
        add(new ActionDecompileHandler());
        add(new ActionGenerateGraphHandler());
        add(new ActionCommentHandler());
        add(new ActionViewCommentsHandler());
        add(new ActionRenameHandler());
        add(new ActionXrefHandler());
        add(new ActionConvertHandler());
        add(new ActionReplaceHandler());
        add(new ActionDeleteHandler());
        add(new ActionCreatePackageHandler());
        add(new ActionMoveToPackageHandler());
        add(new ActionTypeHierarchyHandler());
        add(new ActionOverridesHandler());
        add(new ActionStartAnalysisHandler());
        add(new ActionStopAnalysisHandler());
        add(new ActionDefineDataHandler());
        add(new ActionEditTypeHandler());
        add(new ActionSelectTypeHandler());
        add(new ActionEditArrayHandler());
        add(new ActionDefineStringHandler());
        add(new ActionEditStringHandler());
        add(new ActionOpenTypeEditorHandler());
        add(new ActionDefineCodeHandler());
        add(new ActionEditInstructionHandler());
        add(new ActionEditCodeHandler());
        add(new ActionDefineProcedureHandler());
        add(new ActionEditProcedureHandler());
        add(new ActionEditStackframeHandler());
        add(new ActionCreateProcedureSignatureHandler());
        add(new ActionSelectSignaturePackageHandler());
        add(new ActionCreateSignaturePackageHandler());
        add(new ActionAutoSigningModeHandler());
        add(new ActionUndefineHandler());
        add(new DebuggerAttachHandler());
        add(new DebuggerRestartHandler());
        add(new DebuggerDetachHandler());
        add(new DebuggerRunHandler());
        add(new DebuggerPauseHandler());
        add(new DebuggerTerminateHandler());
        add(new DebuggerResumeThreadHandler());
        add(new DebuggerSuspendThreadHandler());
        add(new DebuggerStepIntoHandler());
        add(new DebuggerStepOverHandler());
        add(new DebuggerStepOutHandler());
        add(new DebuggerRunToLineHandler());
        add(new DebuggerToggleBreakpointHandler());
        add(new WindowToggleToolbarHandler());
        add(new WindowToggleStatusHandler());
        add(new WindowRefreshHandler());
        add(new WindowResetUIStateHandler());
        add(new WindowClosepartHandler());
        add(new WindowFocusProjectExplorerHandler());
        add(new WindowFocusLoggerHandler());
        add(new WindowFocusTerminalHandler());
        add(new WindowFocusHierarchyHandler());
        add(new HelpUserManualHandler());
        add(new HelpFAQHandler());
        add(new HelpGroupHandler());
        add(new HelpDevPortalHandler());
        add(new HelpApidocHandler());
        add(new HelpChangelistHandler());
        add(new HelpCheckupdateHandler());
        add(new HelpAboutHandler());
        add(new ActionExtractToHandler());
        add(new ActionParseAtHandler());
        add(new InternalPrintModelHandler());
        add(new InternalLoadModelHandler());
        add(new InternalSaveModelHandler());

        add(new ActionGenerateHookHandler());
    }

    private void add(JebBaseHandler handler) {
        if (this.all.put(handler.getClass(), handler) != null) {
            throw new RuntimeException("Duplicate handler: " + handler.getClass().getSimpleName());
        }
    }

    public JebBaseHandler get(Class<? extends JebBaseHandler> c) {
        JebBaseHandler h = (JebBaseHandler) this.all.get(c);
        if (h == null) {
            throw new RuntimeException("Unknown handler: " + c.getSimpleName());
        }
        return h;
    }

    public Collection<JebBaseHandler> getAll() {
        return this.all.values();
    }

    public <T extends JebBaseHandler> T create(Class<T> cl) {
        try {
            return cl.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }

    public static final int GRP_FIND = 2;
    public static final int GRP_NAVIGATION = 3;
    public static final int GRP_NAVCANVAS = 4;
    public static final int GRP_BASIC_ACTIONS = 5;
    public static final int GRP_ACTIONS = 6;
    public static final int GRP_NATIVE_ACTIONS = 7;
    public static final int GRP_DEBUGGING = 8;

    public void fillManager(IContributionManager mgr, int group) {
        if (mgr.getItems().length > 0) {
            mgr.add(new Separator());
        }
        if (group == 1) {
            mgr.add(new EditCutHandler());
            mgr.add(new EditCopyHandler());
            mgr.add(new EditPasteHandler());
            mgr.add(new EditSelectAllHandler());
        } else if (group == GRP_FIND) {
            mgr.add(new EditFindHandler());
            mgr.add(new EditFindnextHandler());
        } else if (group == GRP_NAVIGATION) {
            mgr.add(new ActionJumpToHandler());
            mgr.add(new ActionFollowHandler());
            mgr.add(new ActionNavigateForwardHandler());
            mgr.add(new ActionNavigateBackwardHandler());
        } else if (group == GRP_NAVCANVAS) {
            mgr.add(new NavigationCanvasCenterHandler());
            mgr.add(new NavigationCanvasZoomInHandler());
            mgr.add(new NavigationCanvasZoomOutHandler());
            mgr.add(new NavigationCanvasZoomResetHandler());
        } else if (group == GRP_BASIC_ACTIONS) {
            mgr.add(new ActionDecompileHandler());
            mgr.add(new ActionCommentHandler());
            mgr.add(new ActionRenameHandler());
            mgr.add(new ActionXrefHandler());
            mgr.add(new ActionCreatePackageHandler());
            mgr.add(new ActionMoveToPackageHandler());
            mgr.add(new ActionTypeHierarchyHandler());
            mgr.add(new ActionOverridesHandler());
        } else if (group == GRP_ACTIONS) {
            mgr.add(new ActionDecompileHandler());
            mgr.add(new ActionGenerateGraphHandler());
            mgr.add(new ActionCommentHandler());
            mgr.add(new ActionViewCommentsHandler());
            mgr.add(new ActionRenameHandler());
            mgr.add(new ActionXrefHandler());
            mgr.add(new ActionConvertHandler());
            mgr.add(new ActionReplaceHandler());
            mgr.add(new ActionDeleteHandler());
            mgr.add(new ActionCreatePackageHandler());
            mgr.add(new ActionMoveToPackageHandler());
            mgr.add(new ActionTypeHierarchyHandler());
            mgr.add(new ActionOverridesHandler());
            mgr.add(new ActionGenerateHookHandler());
        } else if (group == GRP_NATIVE_ACTIONS) {
            mgr.add(new ActionDefineDataHandler());
            mgr.add(new ActionDefineCodeHandler());
            mgr.add(new ActionEditTypeHandler());
            mgr.add(new ActionSelectTypeHandler());
            mgr.add(new ActionEditArrayHandler());
            mgr.add(new ActionDefineStringHandler());
            mgr.add(new ActionEditStringHandler());
            mgr.add(new ActionOpenTypeEditorHandler());
            mgr.add(new ActionEditInstructionHandler());
            mgr.add(new ActionEditCodeHandler());
            mgr.add(new ActionDefineProcedureHandler());
            mgr.add(new ActionEditProcedureHandler());
            mgr.add(new ActionEditStackframeHandler());
            mgr.add(new ActionCreateProcedureSignatureHandler());
            mgr.add(new ActionSelectSignaturePackageHandler());
            mgr.add(new ActionCreateSignaturePackageHandler());
            mgr.add(new ActionAutoSigningModeHandler());
            mgr.add(new ActionUndefineHandler());
        } else if (group == GRP_DEBUGGING) {
            mgr.add(new DebuggerRunHandler());
            mgr.add(new DebuggerPauseHandler());
            mgr.add(new DebuggerTerminateHandler());
            mgr.add(new DebuggerResumeThreadHandler());
            mgr.add(new DebuggerSuspendThreadHandler());
            mgr.add(new DebuggerStepIntoHandler());
            mgr.add(new DebuggerStepOverHandler());
            mgr.add(new DebuggerStepOutHandler());
            mgr.add(new DebuggerRunToLineHandler());
            mgr.add(new DebuggerToggleBreakpointHandler());
        }
    }

    public boolean attemptExecution(Class<? extends JebBaseHandler> handlerType) {
        JebBaseHandler h = get(handlerType);
        if (!h.canExecute()) {
            return false;
        }
        h.execute();
        return true;
    }

    public static void dumpTemplateShortcutsFile(String basedir) throws IOException {
        StringBuilder sb = new StringBuilder("#------------------------------------------------------------------------------\n# JEB Custom Keyboard Shortcuts\n#------------------------------------------------------------------------------\n\n# 1) Rename or copy this file to jeb-shortcuts.cfg\n# 2) Uncomment and add your own keyboard shortcuts for the actions for which you'd like to override the default shortcuts\n\n# Example: by default, Jump is mapped to the 'G' key; the following line (minus the # character) can be used to remap the action to CTRL+J:\n#jump=Ctrl+J\n\n# *** CUSTOMIZABLE HANDLERS ***\n\n");
        for (JebBaseHandler h : getInstance().getAll()) {
            String id = h.getId();
            if (!Strings.isBlank(id)) {
                sb.append(String.format("#%s=\n", new Object[]{id}));
            }
        }
        String s = sb.toString();
        if (OSType.determine().isWindows()) {
            s = s.replace("\n", "\r\n");
        }
        File file = new File(basedir, "build/extra/bin/jeb-shortcuts.cfg.TEMPLATE");
        IO.writeFile(file, s);
    }
}


