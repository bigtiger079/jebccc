/*     */
package com.pnfsoftware.jeb.rcpclient;
/*     */
/*     */

import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionCommentHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionConvertHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionCreatePackageHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionDecompileHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionDeleteHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionExtractToHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionFollowHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionGenerateGraphHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionJumpToHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionMoveToPackageHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionNavigateBackwardHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionNavigateForwardHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionOverridesHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionParseAtHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionRenameHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionReplaceHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionTypeHierarchyHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionViewCommentsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionXrefHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerAttachHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerDetachHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerPauseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerRestartHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerResumeThreadHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerRunHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerRunToLineHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerStepIntoHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerStepOutHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerStepOverHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerSuspendThreadHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerTerminateHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.debugger.DebuggerToggleBreakpointHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditClearHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditCopyHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditCutHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditFindnextHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditOptionsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditPasteHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditSelectAllHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditStyleHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditSwitchThemeHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileAddHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileAdvancedUnitOptionsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileCloseHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileDeleteHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesContributionsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesInterpretersHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesParsersHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesPluginsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesSiglibsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesTypelibsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExitHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportActiveViewHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportAllBinaryUnitsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileExportDecompiledCodeHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileNotificationsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileOpenHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FilePropertiesHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileSaveAsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileSaveHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileScriptsExecuteHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileScriptsExecutelastHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.file.FileShareHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpAboutHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpApidocHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpChangelistHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpCheckupdateHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpDevPortalHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpFAQHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpGroupHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.help.HelpUserManualHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.internal.InternalLoadModelHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.internal.InternalPrintModelHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.internal.InternalSaveModelHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionAutoSigningModeHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionCreateProcedureSignatureHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionCreateSignaturePackageHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionDefineCodeHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionDefineDataHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionDefineProcedureHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionDefineStringHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditArrayHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditCodeHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditInstructionHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditProcedureHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditStackframeHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditStringHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionEditTypeHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionOpenTypeEditorHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionSelectSignaturePackageHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionSelectTypeHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionStartAnalysisHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionStopAnalysisHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.ActionUndefineHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationCanvasCenterHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationCanvasZoomInHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationCanvasZoomOutHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationCanvasZoomResetHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationDoNotReplaceViewsHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationItemNextHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationItemPreviousHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationLocateInCodeHierarchyHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationLocateInGlobalGraphHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.navigation.NavigationLocateUnitHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowClosepartHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusHierarchyHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusLoggerHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusProjectExplorerHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusTerminalHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowRefreshHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowResetUIStateHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowToggleStatusHandler;
/*     */ import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowToggleToolbarHandler;
/*     */ import com.pnfsoftware.jeb.util.base.OSType;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.io.IO;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.jface.action.IContributionManager;
/*     */ import org.eclipse.jface.action.Separator;

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
/*     */ public class AllHandlers
        /*     */ {
    /* 147 */   private static final ILogger logger = GlobalLog.getLogger(AllHandlers.class);
    /*     */
    /* 149 */   private static final AllHandlers instance = new AllHandlers();

    /*     */
    /*     */
    public static AllHandlers getInstance() {
        /* 152 */
        return instance;
        /*     */
    }

    /*     */
    /* 155 */   private Map<Class<? extends JebBaseHandler>, JebBaseHandler> all = new LinkedHashMap();
    /*     */   public static final int GRP_EDITION = 1;

    /*     */
    /* 158 */
    private AllHandlers() {
        add(new FileExportDecompiledCodeHandler());
        /* 159 */
        add(new FileExportActiveViewHandler());
        /* 160 */
        add(new FileExportAllBinaryUnitsHandler());
        /*     */
        /* 162 */
        add(new FileScriptsExecuteHandler());
        /* 163 */
        add(new FileScriptsExecutelastHandler());
        /*     */
        /* 165 */
        add(new FileEnginesPluginsHandler());
        /* 166 */
        add(new FileEnginesParsersHandler());
        /* 167 */
        add(new FileEnginesContributionsHandler());
        /* 168 */
        add(new FileEnginesInterpretersHandler());
        /*     */
        /* 170 */
        add(new FileEnginesTypelibsHandler());
        /* 171 */
        add(new FileEnginesSiglibsHandler());
        /*     */
        /* 173 */
        add(new FileOpenHandler());
        /* 174 */
        add(new FileAddHandler());
        /* 175 */
        add(new FileCloseHandler());
        /* 176 */
        add(new FileSaveHandler());
        /* 177 */
        add(new FileSaveAsHandler());
        /* 178 */
        add(new FilePropertiesHandler());
        /* 179 */
        add(new FileShareHandler());
        /* 180 */
        add(new FileDeleteHandler());
        /* 181 */
        add(new FileNotificationsHandler());
        /* 182 */
        add(new FileAdvancedUnitOptionsHandler());
        /* 183 */
        add(new FileExitHandler());
        /*     */
        /* 185 */
        add(new EditCutHandler());
        /* 186 */
        add(new EditCopyHandler());
        /* 187 */
        add(new EditPasteHandler());
        /* 188 */
        add(new EditSelectAllHandler());
        /* 189 */
        add(new EditClearHandler());
        /* 190 */
        add(new EditFindHandler());
        /* 191 */
        add(new EditFindnextHandler());
        /* 192 */
        add(new EditSwitchThemeHandler());
        /* 193 */
        add(new EditStyleHandler());
        /* 194 */
        add(new EditOptionsHandler());
        /*     */
        /* 196 */
        add(new NavigationDoNotReplaceViewsHandler());
        /* 197 */
        add(new NavigationLocateUnitHandler());
        /* 198 */
        add(new ActionNavigateForwardHandler());
        /* 199 */
        add(new ActionNavigateBackwardHandler());
        /* 200 */
        add(new ActionJumpToHandler());
        /* 201 */
        add(new ActionFollowHandler());
        /* 202 */
        add(new NavigationItemPreviousHandler());
        /* 203 */
        add(new NavigationItemNextHandler());
        /* 204 */
        add(new NavigationLocateInCodeHierarchyHandler());
        /* 205 */
        add(new NavigationLocateInGlobalGraphHandler());
        /* 206 */
        add(new NavigationCanvasCenterHandler());
        /* 207 */
        add(new NavigationCanvasZoomInHandler());
        /* 208 */
        add(new NavigationCanvasZoomOutHandler());
        /* 209 */
        add(new NavigationCanvasZoomResetHandler());
        /*     */
        /* 211 */
        add(new ActionDecompileHandler());
        /* 212 */
        add(new ActionGenerateGraphHandler());
        /* 213 */
        add(new ActionCommentHandler());
        /* 214 */
        add(new ActionViewCommentsHandler());
        /* 215 */
        add(new ActionRenameHandler());
        /* 216 */
        add(new ActionXrefHandler());
        /* 217 */
        add(new ActionConvertHandler());
        /* 218 */
        add(new ActionReplaceHandler());
        /* 219 */
        add(new ActionDeleteHandler());
        /* 220 */
        add(new ActionCreatePackageHandler());
        /* 221 */
        add(new ActionMoveToPackageHandler());
        /* 222 */
        add(new ActionTypeHierarchyHandler());
        /* 223 */
        add(new ActionOverridesHandler());
        /*     */
        /* 225 */
        add(new ActionStartAnalysisHandler());
        /* 226 */
        add(new ActionStopAnalysisHandler());
        /* 227 */
        add(new ActionDefineDataHandler());
        /* 228 */
        add(new ActionEditTypeHandler());
        /* 229 */
        add(new ActionSelectTypeHandler());
        /* 230 */
        add(new ActionEditArrayHandler());
        /* 231 */
        add(new ActionDefineStringHandler());
        /* 232 */
        add(new ActionEditStringHandler());
        /* 233 */
        add(new ActionOpenTypeEditorHandler());
        /* 234 */
        add(new ActionDefineCodeHandler());
        /* 235 */
        add(new ActionEditInstructionHandler());
        /* 236 */
        add(new ActionEditCodeHandler());
        /* 237 */
        add(new ActionDefineProcedureHandler());
        /* 238 */
        add(new ActionEditProcedureHandler());
        /* 239 */
        add(new ActionEditStackframeHandler());
        /* 240 */
        add(new ActionCreateProcedureSignatureHandler());
        /* 241 */
        add(new ActionSelectSignaturePackageHandler());
        /* 242 */
        add(new ActionCreateSignaturePackageHandler());
        /* 243 */
        add(new ActionAutoSigningModeHandler());
        /* 244 */
        add(new ActionUndefineHandler());
        /*     */
        /* 246 */
        add(new DebuggerAttachHandler());
        /* 247 */
        add(new DebuggerRestartHandler());
        /* 248 */
        add(new DebuggerDetachHandler());
        /* 249 */
        add(new DebuggerRunHandler());
        /* 250 */
        add(new DebuggerPauseHandler());
        /* 251 */
        add(new DebuggerTerminateHandler());
        /* 252 */
        add(new DebuggerResumeThreadHandler());
        /* 253 */
        add(new DebuggerSuspendThreadHandler());
        /* 254 */
        add(new DebuggerStepIntoHandler());
        /* 255 */
        add(new DebuggerStepOverHandler());
        /* 256 */
        add(new DebuggerStepOutHandler());
        /* 257 */
        add(new DebuggerRunToLineHandler());
        /* 258 */
        add(new DebuggerToggleBreakpointHandler());
        /*     */
        /* 260 */
        add(new WindowToggleToolbarHandler());
        /* 261 */
        add(new WindowToggleStatusHandler());
        /* 262 */
        add(new WindowRefreshHandler());
        /* 263 */
        add(new WindowResetUIStateHandler());
        /* 264 */
        add(new WindowClosepartHandler());
        /* 265 */
        add(new WindowFocusProjectExplorerHandler());
        /* 266 */
        add(new WindowFocusLoggerHandler());
        /* 267 */
        add(new WindowFocusTerminalHandler());
        /* 268 */
        add(new WindowFocusHierarchyHandler());
        /*     */
        /* 270 */
        add(new HelpUserManualHandler());
        /* 271 */
        add(new HelpFAQHandler());
        /* 272 */
        add(new HelpGroupHandler());
        /* 273 */
        add(new HelpDevPortalHandler());
        /* 274 */
        add(new HelpApidocHandler());
        /* 275 */
        add(new HelpChangelistHandler());
        /* 276 */
        add(new HelpCheckupdateHandler());
        /* 277 */
        add(new HelpAboutHandler());
        /*     */
        /*     */
        /* 280 */
        add(new ActionExtractToHandler());
        /* 281 */
        add(new ActionParseAtHandler());
        /*     */
        /*     */
        /* 284 */
        add(new InternalPrintModelHandler());
        /* 285 */
        add(new InternalLoadModelHandler());
        /* 286 */
        add(new InternalSaveModelHandler());
        /*     */
    }

    /*     */
    /*     */
    private void add(JebBaseHandler handler) {
        /* 290 */
        if (this.all.put(handler.getClass(), handler) != null) {
            /* 291 */
            throw new RuntimeException("Duplicate handler: " + handler.getClass().getSimpleName());
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public JebBaseHandler get(Class<? extends JebBaseHandler> c) {
        /* 296 */
        JebBaseHandler h = (JebBaseHandler) this.all.get(c);
        /* 297 */
        if (h == null) {
            /* 298 */
            throw new RuntimeException("Unknown handler: " + c.getSimpleName());
            /*     */
        }
        /* 300 */
        return h;
        /*     */
    }

    /*     */
    /*     */
    public Collection<JebBaseHandler> getAll() {
        /* 304 */
        return this.all.values();
        /*     */
    }

    /*     */
    /*     */
    public <T extends JebBaseHandler> T create(Class<T> cl) {
        /*     */
        try {
            /* 309 */
            return (JebBaseHandler) cl.getConstructor(new Class[0]).newInstance(new Object[0]);
            /*     */
        }
        /*     */ catch (Exception e) {
        }
        /*     */
        /* 313 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    /*     */   public static final int GRP_FIND = 2;
    /*     */
    /*     */   public static final int GRP_NAVIGATION = 3;
    /*     */   public static final int GRP_NAVCANVAS = 4;
    /*     */   public static final int GRP_BASIC_ACTIONS = 5;
    /*     */   public static final int GRP_ACTIONS = 6;
    /*     */   public static final int GRP_NATIVE_ACTIONS = 7;
    /*     */   public static final int GRP_DEBUGGING = 8;

    /*     */
    public void fillManager(IContributionManager mgr, int group)
    /*     */ {
        /* 327 */
        if (mgr.getItems().length > 0) {
            /* 328 */
            mgr.add(new Separator());
            /*     */
        }
        /*     */
        /* 331 */
        if (group == 1) {
            /* 332 */
            mgr.add(new EditCutHandler());
            /* 333 */
            mgr.add(new EditCopyHandler());
            /* 334 */
            mgr.add(new EditPasteHandler());
            /* 335 */
            mgr.add(new EditSelectAllHandler());
            /*     */
        }
        /* 337 */
        else if (group == 2) {
            /* 338 */
            mgr.add(new EditFindHandler());
            /* 339 */
            mgr.add(new EditFindnextHandler());
            /*     */
        }
        /* 341 */
        else if (group == 3) {
            /* 342 */
            mgr.add(new ActionJumpToHandler());
            /* 343 */
            mgr.add(new ActionFollowHandler());
            /* 344 */
            mgr.add(new ActionNavigateForwardHandler());
            /* 345 */
            mgr.add(new ActionNavigateBackwardHandler());
            /*     */
        }
        /* 347 */
        else if (group == 4) {
            /* 348 */
            mgr.add(new NavigationCanvasCenterHandler());
            /* 349 */
            mgr.add(new NavigationCanvasZoomInHandler());
            /* 350 */
            mgr.add(new NavigationCanvasZoomOutHandler());
            /* 351 */
            mgr.add(new NavigationCanvasZoomResetHandler());
            /*     */
        }
        /* 353 */
        else if (group == 5) {
            /* 354 */
            mgr.add(new ActionDecompileHandler());
            /* 355 */
            mgr.add(new ActionCommentHandler());
            /* 356 */
            mgr.add(new ActionRenameHandler());
            /* 357 */
            mgr.add(new ActionXrefHandler());
            /* 358 */
            mgr.add(new ActionCreatePackageHandler());
            /* 359 */
            mgr.add(new ActionMoveToPackageHandler());
            /* 360 */
            mgr.add(new ActionTypeHierarchyHandler());
            /* 361 */
            mgr.add(new ActionOverridesHandler());
            /*     */
        }
        /* 363 */
        else if (group == 6) {
            /* 364 */
            mgr.add(new ActionDecompileHandler());
            /* 365 */
            mgr.add(new ActionGenerateGraphHandler());
            /* 366 */
            mgr.add(new ActionCommentHandler());
            /* 367 */
            mgr.add(new ActionViewCommentsHandler());
            /* 368 */
            mgr.add(new ActionRenameHandler());
            /* 369 */
            mgr.add(new ActionXrefHandler());
            /* 370 */
            mgr.add(new ActionConvertHandler());
            /* 371 */
            mgr.add(new ActionReplaceHandler());
            /* 372 */
            mgr.add(new ActionDeleteHandler());
            /* 373 */
            mgr.add(new ActionCreatePackageHandler());
            /* 374 */
            mgr.add(new ActionMoveToPackageHandler());
            /* 375 */
            mgr.add(new ActionTypeHierarchyHandler());
            /* 376 */
            mgr.add(new ActionOverridesHandler());
            /*     */
        }
        /* 378 */
        else if (group == 7) {
            /* 379 */
            mgr.add(new ActionDefineDataHandler());
            /* 380 */
            mgr.add(new ActionDefineCodeHandler());
            /* 381 */
            mgr.add(new ActionEditTypeHandler());
            /* 382 */
            mgr.add(new ActionSelectTypeHandler());
            /* 383 */
            mgr.add(new ActionEditArrayHandler());
            /* 384 */
            mgr.add(new ActionDefineStringHandler());
            /* 385 */
            mgr.add(new ActionEditStringHandler());
            /* 386 */
            mgr.add(new ActionOpenTypeEditorHandler());
            /* 387 */
            mgr.add(new ActionEditInstructionHandler());
            /* 388 */
            mgr.add(new ActionEditCodeHandler());
            /* 389 */
            mgr.add(new ActionDefineProcedureHandler());
            /* 390 */
            mgr.add(new ActionEditProcedureHandler());
            /* 391 */
            mgr.add(new ActionEditStackframeHandler());
            /* 392 */
            mgr.add(new ActionCreateProcedureSignatureHandler());
            /* 393 */
            mgr.add(new ActionSelectSignaturePackageHandler());
            /* 394 */
            mgr.add(new ActionCreateSignaturePackageHandler());
            /* 395 */
            mgr.add(new ActionAutoSigningModeHandler());
            /* 396 */
            mgr.add(new ActionUndefineHandler());
            /*     */
        }
        /* 398 */
        else if (group == 8) {
            /* 399 */
            mgr.add(new DebuggerRunHandler());
            /* 400 */
            mgr.add(new DebuggerPauseHandler());
            /* 401 */
            mgr.add(new DebuggerTerminateHandler());
            /* 402 */
            mgr.add(new DebuggerResumeThreadHandler());
            /* 403 */
            mgr.add(new DebuggerSuspendThreadHandler());
            /* 404 */
            mgr.add(new DebuggerStepIntoHandler());
            /* 405 */
            mgr.add(new DebuggerStepOverHandler());
            /* 406 */
            mgr.add(new DebuggerStepOutHandler());
            /* 407 */
            mgr.add(new DebuggerRunToLineHandler());
            /* 408 */
            mgr.add(new DebuggerToggleBreakpointHandler());
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public boolean attemptExecution(Class<? extends JebBaseHandler> handlerType) {
        /* 413 */
        JebBaseHandler h = get(handlerType);
        /* 414 */
        if (!h.canExecute()) {
            /* 415 */
            return false;
            /*     */
        }
        /*     */
        /* 418 */
        h.execute();
        /* 419 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static void dumpTemplateShortcutsFile(String basedir)
    /*     */     throws IOException
    /*     */ {
        /* 430 */
        StringBuilder sb = new StringBuilder("#------------------------------------------------------------------------------\n# JEB Custom Keyboard Shortcuts\n#------------------------------------------------------------------------------\n\n# 1) Rename or copy this file to jeb-shortcuts.cfg\n# 2) Uncomment and add your own keyboard shortcuts for the actions for which you'd like to override the default shortcuts\n\n# Example: by default, Jump is mapped to the 'G' key; the following line (minus the # character) can be used to remap the action to CTRL+J:\n#jump=Ctrl+J\n\n# *** CUSTOMIZABLE HANDLERS ***\n\n");
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
        /*     */
        /* 449 */
        for (JebBaseHandler h : getInstance().getAll()) {
            /* 450 */
            String id = h.getId();
            /* 451 */
            if (!Strings.isBlank(id)) {
                /* 452 */
                sb.append(String.format("#%s=\n", new Object[]{id}));
                /*     */
            }
            /*     */
        }
        /*     */
        /* 456 */
        String s = sb.toString();
        /* 457 */
        if (OSType.determine().isWindows()) {
            /* 458 */
            s = s.replace("\n", "\r\n");
            /*     */
        }
        /*     */
        /* 461 */
        File file = new File(basedir, "build/extra/bin/jeb-shortcuts.cfg.TEMPLATE");
        /* 462 */
        IO.writeFile(file, s);
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\AllHandlers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */