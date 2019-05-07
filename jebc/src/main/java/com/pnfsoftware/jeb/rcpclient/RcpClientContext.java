package com.pnfsoftware.jeb.rcpclient;

import com.pnfsoftware.jeb.client.AbstractClientContext;
import com.pnfsoftware.jeb.client.AbstractContext;
import com.pnfsoftware.jeb.client.Licensing;
import com.pnfsoftware.jeb.client.PublicAnnouncement;
import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.SoftwareBuildInfo;
import com.pnfsoftware.jeb.client.events.JC;
import com.pnfsoftware.jeb.client.events.JebClientEvent;
import com.pnfsoftware.jeb.client.script.ScriptException;
import com.pnfsoftware.jeb.client.script.ScriptExecutionException;
import com.pnfsoftware.jeb.client.script.ScriptInitializationException;
import com.pnfsoftware.jeb.client.script.ScriptLoader;
import com.pnfsoftware.jeb.client.script.ScriptPreparationException;
import com.pnfsoftware.jeb.client.telemetry.ITelemetryDatabase;
import com.pnfsoftware.jeb.core.Artifact;
import com.pnfsoftware.jeb.core.CoreOptions;
import com.pnfsoftware.jeb.core.IArtifact;
import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
import com.pnfsoftware.jeb.core.Version;
import com.pnfsoftware.jeb.core.events.ClientNotificationLevel;
import com.pnfsoftware.jeb.core.events.ControllerNotification;
import com.pnfsoftware.jeb.core.exceptions.InterruptionException;
import com.pnfsoftware.jeb.core.exceptions.JebException;
import com.pnfsoftware.jeb.core.exceptions.SerializationException;
import com.pnfsoftware.jeb.core.exceptions.UnitLockedException;
import com.pnfsoftware.jeb.core.input.FileInput;
import com.pnfsoftware.jeb.core.input.IInput;
import com.pnfsoftware.jeb.core.properties.IPropertyDefinitionManager;
import com.pnfsoftware.jeb.core.properties.IPropertyManager;
import com.pnfsoftware.jeb.core.properties.impl.CommonsConfigurationWrapper;
import com.pnfsoftware.jeb.core.properties.impl.PropertyDefinitionManager;
import com.pnfsoftware.jeb.core.properties.impl.PropertyTypeBoolean;
import com.pnfsoftware.jeb.core.properties.impl.PropertyTypeInteger;
import com.pnfsoftware.jeb.core.properties.impl.PropertyTypePath;
import com.pnfsoftware.jeb.core.properties.impl.PropertyTypeString;
import com.pnfsoftware.jeb.core.properties.impl.SimplePropertyManager;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitIdentifier;
import com.pnfsoftware.jeb.core.units.IUnitLock;
import com.pnfsoftware.jeb.core.units.NotificationType;
import com.pnfsoftware.jeb.core.units.UnitUtil;
import com.pnfsoftware.jeb.core.units.code.IDecompilerUnit;
import com.pnfsoftware.jeb.core.units.code.android.IApkUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.rcpclient.dialogs.ControllerAddressDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.CustomSurveyDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.JebDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.LicenseKeyAutoDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.SoftwareUpdateDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.AbstractRefresher;
import com.pnfsoftware.jeb.rcpclient.extensions.MenuUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.ShellActivationTracker;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.UIUtil;
import com.pnfsoftware.jeb.rcpclient.extensions.WidgetBoundsManager;
import com.pnfsoftware.jeb.rcpclient.extensions.app.App;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Dock;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Folder;
import com.pnfsoftware.jeb.rcpclient.extensions.app.Panel;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMAppContext;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.extensions.binding.ActionEx;
import com.pnfsoftware.jeb.rcpclient.extensions.binding.KeyAcceleratorManager;
import com.pnfsoftware.jeb.rcpclient.extensions.binding.KeyShortcutsManager;
import com.pnfsoftware.jeb.rcpclient.extensions.state.WidgetWrapper;
import com.pnfsoftware.jeb.rcpclient.extensions.themes.ThemeManager;
import com.pnfsoftware.jeb.rcpclient.extensions.ui.Toast;
import com.pnfsoftware.jeb.rcpclient.extensions.ui.UITaskManager;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionCommentHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionConvertHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionCreatePackageHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionDecompileHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionDeleteHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionFollowHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionGenerateGraphHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionJumpToHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionMoveToPackageHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionNavigateBackwardHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionNavigateForwardHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionOverridesHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionRenameHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionReplaceHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionTypeHierarchyHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionViewCommentsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.actions.ActionXrefHandler;
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
import com.pnfsoftware.jeb.rcpclient.handlers.edition.EditLanguageMenuHandler;
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
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileEnginesExecutepluginMenuHandler;
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
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileOpenrecentMenuHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FilePropertiesHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileSaveAsHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileSaveHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileScriptsExecuteHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileScriptsExecutelastHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.file.FileScriptsExecuterecentMenuHandler;
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
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusPartMenuHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusProjectExplorerHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowFocusTerminalHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowRefreshHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowResetUIStateHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowToggleStatusHandler;
import com.pnfsoftware.jeb.rcpclient.handlers.windows.WindowToggleToolbarHandler;
import com.pnfsoftware.jeb.rcpclient.iviewers.StyleManager;
import com.pnfsoftware.jeb.rcpclient.operations.JebAction;
import com.pnfsoftware.jeb.rcpclient.parts.JebStatusIndicator;
import com.pnfsoftware.jeb.rcpclient.parts.MultiInterpreter;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager.PropertyProvider;
import com.pnfsoftware.jeb.rcpclient.parts.ProjectExplorerPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UIState;
import com.pnfsoftware.jeb.rcpclient.util.BrowserUtil;
import com.pnfsoftware.jeb.rcpclient.util.PartUtil;
import com.pnfsoftware.jeb.rcpclient.util.StateDataProvider;
import com.pnfsoftware.jeb.rcpclient.util.TextHistory;
import com.pnfsoftware.jeb.rcpclient.util.TextHistoryCollection;
import com.pnfsoftware.jeb.util.base.CallableWithProgressCallback;
import com.pnfsoftware.jeb.util.base.IProgressCallback;
import com.pnfsoftware.jeb.util.base.OSType;
import com.pnfsoftware.jeb.util.base.Throwables;
import com.pnfsoftware.jeb.util.concurrent.DaemonExecutors;
import com.pnfsoftware.jeb.util.concurrent.ThreadUtil;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.format.SizeFormatter;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.io.IO;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import com.pnfsoftware.jeb.util.logging.LogStatusSink;
import com.pnfsoftware.jeb.util.net.Net;
import com.pnfsoftware.jeb.util.net.NetProxyInfo;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class RcpClientContext
        extends AbstractClientContext
        implements IGraphicalTaskExecutor, IWidgetManager, IMAppContext {
    public static final ILogger logger = GlobalLog.getLogger(RcpClientContext.class);

    private static String mainShellOriginalTitle;

    public static final String shortcutsFilename = "jeb-shortcuts.cfg";

    private static final int maxRecentFiles = 10;

    private static final int maxRecentScripts = 10;

    private static RcpClientContext singleIntance;

    private Display display;
    private JebApp app;

    private class LogManagerRoutine
            implements Runnable {
        int logLimit;
        int logLimitHalf;
        String additionString;
        String status;

        private LogManagerRoutine() {
        }

        public void run() {
            this.logLimit = RcpClientContext.this.uiProperties.getLoggerMaxLength();
            this.logLimitHalf = (this.logLimit / 2);

            StringBuilder addition = new StringBuilder();
            for (; ; ) {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e1) {
                    break;
                }

                this.status = RcpClientContext.this.logStatusSink.retrieve();


                this.additionString = null;
                synchronized (RcpClientContext.this.logBuffer) {
                    if (!RcpClientContext.this.logBuffer.isEmpty()) {
                        for (CharSequence s : RcpClientContext.this.logBuffer) {
                            addition.append(s);
                        }
                        RcpClientContext.this.logBuffer.clear();
                    }
                }
                if (addition.length() != 0) {
                    this.additionString = addition.toString();
                    addition.setLength(0);
                }


                if ((this.status != null) || (this.additionString != null)) {


                    if (RcpClientContext.this.display.isDisposed()) {
                        break;
                    }

                    UIExecutor.sync(RcpClientContext.this.display, new UIRunnable() {
                        public void runi() {
                            try {
                                if ((RcpClientContext.this.getStatusIndicator() != null) &&
                                        (RcpClientContext.LogManagerRoutine.this.status != null)) {
                                    RcpClientContext.this.getStatusIndicator().setText(1, RcpClientContext.LogManagerRoutine.this.status);
                                }


                                if (RcpClientContext.LogManagerRoutine.this.additionString != null) {
                                    int addLen = RcpClientContext.LogManagerRoutine.this.additionString.length();
                                    int curLen = RcpClientContext.this.logDocument.getLength();
                                    int hypLen = curLen + addLen;
                                    if ((RcpClientContext.LogManagerRoutine.this.logLimit > 0) && (hypLen > RcpClientContext.LogManagerRoutine.this.logLimit)) {
                                        if (addLen > RcpClientContext.LogManagerRoutine.this.logLimit) {
                                            String text = RcpClientContext.LogManagerRoutine.this.additionString.substring(addLen - RcpClientContext.LogManagerRoutine.this.logLimit, addLen);
                                            RcpClientContext.this.logDocument.set(text);
                                        } else if (addLen > RcpClientContext.LogManagerRoutine.this.logLimitHalf) {
                                            RcpClientContext.this.logDocument.set(RcpClientContext.LogManagerRoutine.this.additionString);
                                        } else {
                                            int r = RcpClientContext.LogManagerRoutine.this.logLimitHalf - addLen;
                                            String text = RcpClientContext.this.logDocument.get(curLen - r, r) + RcpClientContext.LogManagerRoutine.this.additionString;
                                            RcpClientContext.this.logDocument.set(text);
                                        }
                                    } else {
                                        RcpClientContext.this.logDocument.replace(curLen, 0, RcpClientContext.LogManagerRoutine.this.additionString);
                                    }
                                }
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

    static {
        GlobalLog.addDestinationStream(System.out);
        if (Licensing.isDebugBuild()) {


            String[] filterPatterns = new String[0];


            for (String pattern : filterPatterns) {
                GlobalLog.addGlobalFilter(pattern, Integer.MAX_VALUE);
            }
        }


        mainShellOriginalTitle = "JEB";

        String cname = getChannelName();
        if (cname != null) {
            mainShellOriginalTitle = mainShellOriginalTitle + app_ver.getMajor() + " " + cname;
        }
    }


    private ExecutorService executorService = DaemonExecutors.newFixedThreadPool(20);
    private PrintStream jebSessionLogStream;
    private List<CharSequence> logBuffer;
    private LogStatusSink logStatusSink;
    private RcpErrorHandler errorHandler;
    private ThreadManager threadManager;
    private Shell mainShell;
    private PartManager pman;
    private FontManager fontManager;
    private StyleManager styleManager;
    private IDocument logDocument;
    private IStatusIndicator statusIndicator;
    private Thread threadMotd;
    private RcpClientProperties uiProperties;
    private TextHistoryCollection textHistoryData;
    private WidgetBoundsManager widgetBoundsManager;
    private StateDataProvider widgetPersistenceProvider;
    private StateDataProvider dialogPersistenceProvider;
    private WidgetWrapper widgetWrapper;
    private boolean clearUIState;
    private boolean restartRequired;
    private List<String> recentFiles;
    private List<String> recentScripts;
    private String lastScript;
    private Map<Object, Map<Object, Object>> assoData = new WeakHashMap();

    private MultiInterpreter masterInterpreter;

    private Thread threadMonitor;

    private Thread threadSoftwareUpdater;
    private boolean queuedSampleSharingNagger;
    private AtomicInteger updateCheckState = new AtomicInteger();

    private int sessionProjectCount;

    private int projectReadyTimestamp;

    private int projectArtifactCount;
    private String lastReloadedProjectPath;
    Boolean preferQuickSave;
    private AbstractRefresher handlersRefresher;

    public RcpClientContext() {
        if (singleIntance != null) {
            throw new IllegalStateException("A single instance of the RCP client context is allowed per VM");
        }
        singleIntance = this;
        this.display = UI.getDisplay();
        JebDialog.setStandardWidgetManager(this);
        getThemeManager();

        this.handlersRefresher = new AbstractRefresher(this.display) {
            protected void performRefresh() {
                RcpClientContext.this.internalSynchronizedRefreshHandlersStates();
            }
        };
        this.display.addFilter(3, new Listener() {
            public void handleEvent(Event event) {
                Control cursorCtrl = RcpClientContext.this.app.getDisplay().getCursorControl();
                Control focusControl = RcpClientContext.this.app.getDisplay().getFocusControl();
                if ((cursorCtrl != null) && (cursorCtrl != focusControl)) {
                    IMPart part = PartUtil.getPart(cursorCtrl);
                    if (part != null) {
                        RcpClientContext.logger.i("Focus forced in %s", new Object[]{part});
                        cursorCtrl.forceFocus();
                    }

                }

            }
        });
        this.display.addFilter(15, new Listener() {

            public void handleEvent(Event event) {
            }

        });
        this.display.addFilter(13, new Panel.SashSelectionFilter(30));
    }

    public static RcpClientContext getInstance() {
        return singleIntance;
    }

    public Display getDisplay() {
        return this.display;
    }

    public Shell getMainShell() {
        return this.mainShell;
    }

    public Shell getActiveShell() {
        return UI.getShellTracker().get();
    }

    public App getApp() {
        return this.app;
    }


    public void refreshHandlersStates() {
        this.handlersRefresher.request();
    }


    public void internalSynchronizedRefreshHandlersStates() {
        this.app.getToolbarManager().update(true);
        for (IContributionItem item : this.app.getToolbarManager().getItems()) {
            item.update();
        }


        this.app.getMenuManager().updateAll(true);
        for (IContributionItem item : this.app.getMenuManager().getItems()) {
            item.update();
        }
        updateContributionsRecursively(this.app.getMenuManager());

        Iterator<JebBaseHandler> iterator = AllHandlers.getInstance().getAll().iterator();
       while (iterator.hasNext()) {
            JebAction action = (JebAction)iterator.next();
            if (action.isEnabled()) {
                action.setEnabled(true);
            }
        }
    }

    private void updateContributionsRecursively(IContributionItem item) {
        item.update();
        IContributionItem innerItem;
        if ((item instanceof SubContributionItem)) {
            innerItem = ((SubContributionItem) item).getInnerItem();
            updateContributionsRecursively(innerItem);
        } else if ((item instanceof IMenuManager)) {
            for (IContributionItem subItem : ((IMenuManager) item).getItems()) {
                updateContributionsRecursively(subItem);
            }
        }
    }

    public void markRestartRequired() {
        this.restartRequired = true;
    }

    public boolean isRestartRequired() {
        return this.restartRequired;
    }

    public void requestResetUIState() {
        this.clearUIState = true;
        markRestartRequired();
    }

    public boolean onApplicationException(Exception e) {
        if (getErrorHandler() != null) {
            getErrorHandler().handle(e);
        } else {
            logger.catchingSilent(e);
            String msg = String.format("The following error was reported on the UI thread:\n\n%s", new Object[]{
                    Throwables.formatStacktraceShort(e)});
            UI.error(msg);
        }
        return true;
    }

    public void onApplicationBuilt(App app) {
        this.app = ((JebApp) app);


        logger.setEnabledLevel(GlobalLog.getEnabledLevel());
        if (Licensing.isDebugBuild()) {
            try {
                this.jebSessionLogStream = new PrintStream(new File(getBaseDirectory(), "jeb-session.log"));
                GlobalLog.addDestinationStream(this.jebSessionLogStream);
            } catch (FileNotFoundException e) {
                logger.catching(e);
            }
        }

        this.logBuffer = Collections.synchronizedList(new ArrayList());
        GlobalLog.addDestinationBuffer(this.logBuffer);
        this.logStatusSink = new LogStatusSink();
        GlobalLog.addStatusSink(this.logStatusSink);


        String[] argv = app.getArguments();
        initialize(argv);
        this.uiProperties = new RcpClientProperties(getPropertyManager());


        this.logDocument = new Document();
        ThreadUtil.start(Licensing.isDebugBuild() ? LogManagerRoutine.class.getSimpleName() : null, new LogManagerRoutine());


        this.errorHandler = new RcpErrorHandler(this);
        this.threadManager = new ThreadManager(this.errorHandler);


        String shortcutsPath = getPropertyManager().getStringUnsafe(".ui.KeyboardShortcutsFile");
        if (!Strings.isBlank(shortcutsPath)) {
            File file = new File(shortcutsPath);
            if (!file.isAbsolute()) {
                file = new File(getProgramDirectory(), shortcutsPath);
            }
            if (file.canRead()) {
                Parameters params = new Parameters();

                FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder(PropertiesConfiguration.class).configure(new BuilderParameters[]{(BuilderParameters) params.properties().setFile(file)});
                builder.setAutoSave(true);
                try {
                    PropertiesConfiguration cfg = (PropertiesConfiguration) builder.getConfiguration();
                    ActionEx.setKeyboardShortcuts(new KeyShortcutsManager(new SimplePropertyManager(new CommonsConfigurationWrapper(cfg))));
                } catch (ConfigurationException localConfigurationException) {
                }
            }
        }


        AllHandlers h = AllHandlers.getInstance();
        KeyAcceleratorManager kam = new KeyAcceleratorManager(this.display);
        kam.registerHandlers(h.getAll());


        this.statusIndicator = new JebStatusIndicator(app.getStatusManager());


        buildShell(app.getPrimaryShell());
        buildDock(app.getDock());
        buildMenu(app.getMenuManager());
        buildToolbar(app.getToolbarManager());


        initTheme();
    }

    private void buildShell(Shell shell) {
        shell.setImage(UIAssetManager.getInstance().getImage("program/jeb_32px.png"));

        boolean maximized = false;
        Rectangle bounds = null;
        String strBounds = getPropertyManager().getString("state.MainShellBounds");
        if (!Strings.isBlank(strBounds)) {
            String[] elts = Strings.trim(strBounds).split(",");
            if ((elts.length == 1) && (elts[0].equals("-1"))) {
                maximized = true;
            } else if (elts.length == 4) {
                int x = Conversion.stringToInt(elts[0]);
                int y = Conversion.stringToInt(elts[1]);
                int w = Conversion.stringToInt(elts[2]);
                int h = Conversion.stringToInt(elts[3]);
                bounds = new Rectangle(x, y, w, h);
            }
        }

        if (maximized) {
            shell.setMaximized(true);
        } else if (bounds != null) {
            shell.setBounds(bounds);
        } else {
            Rectangle ca = shell.getDisplay().getPrimaryMonitor().getClientArea();
            double incX = ca.width / 100.0D;
            double incY = ca.height / 100.0D;
            shell.setBounds(new Rectangle((int) (3.0D * incX), (int) (3.0D * incY), (int) (94.0D * incX), (int) (94.0D * incY)));
        }
    }

    private void buildDock(Dock dock) {
        ProjectExplorerPartManager.setupDragAndDrop(this.app.getDock(), this);
        this.app.folderWorkspace = dock.getInitialFolder();
        this.app.folderWorkspace.setCloseOnEmpty(false);

        int projectFolderRatio = getPropertyManager().getInteger("state.ProjectFolderRatio");
        if ((projectFolderRatio <= 0) || (projectFolderRatio >= 100)) {
            projectFolderRatio = 18;
        }
        this.app.folderProject = dock.splitFolder(this.app.folderWorkspace, -3, projectFolderRatio);
        this.app.folderProject.setCloseOnEmpty(false);

        int consolesFolderRatio = getPropertyManager().getInteger("state.ConsolesFolderRatio");
        if ((consolesFolderRatio <= 0) || (consolesFolderRatio >= 100)) {
            consolesFolderRatio = Licensing.isDebugBuild() ? 35 : 20;
        }
        this.app.folderConsoles = dock.splitFolder(this.app.folderWorkspace, -2, consolesFolderRatio);
        this.app.folderConsoles.setCloseOnEmpty(false);
    }

    private void buildMenu(MenuManager menuManager) {
        boolean isMac = OSType.determine().isMac();

        AllHandlers h = AllHandlers.getInstance();
        if (Licensing.isDebugBuild()) {
            try {
                AllHandlers.dumpTemplateShortcutsFile(getBaseDirectory());
            } catch (IOException e) {
                logger.catching(e);
            }
        }

        MenuManager menuOpenRecent = new MenuManager(S.s(529), null);
        menuOpenRecent.setRemoveAllWhenShown(true);
        menuOpenRecent.addMenuListener(new FileOpenrecentMenuHandler());

        MenuManager menuExport = MenuUtil.createAutoRefreshMenu(S.s(501));
        menuExport.add(h.get(FileExportDecompiledCodeHandler.class));
        menuExport.add(h.get(FileExportActiveViewHandler.class));
        menuExport.add(h.get(FileExportAllBinaryUnitsHandler.class));

        MenuManager menuScriptsRecent = new MenuManager(S.s(545));
        menuScriptsRecent.setRemoveAllWhenShown(true);
        menuScriptsRecent.addMenuListener(new FileScriptsExecuterecentMenuHandler());

        MenuManager menuScripts = MenuUtil.createAutoRefreshMenu(S.s(558));
        menuScripts.add(menuScriptsRecent);
        menuScripts.add(new Separator());
        menuScripts.add(h.get(FileScriptsExecuteHandler.class));
        menuScripts.add(h.get(FileScriptsExecutelastHandler.class));

        MenuManager menuPluginsExecuteEP = new MenuManager(S.s(499));
        menuPluginsExecuteEP.setRemoveAllWhenShown(true);
        menuPluginsExecuteEP.addMenuListener(new FileEnginesExecutepluginMenuHandler());

        MenuManager menuPlugins = MenuUtil.createAutoRefreshMenu(S.s(539));
        menuPlugins.add(menuPluginsExecuteEP);
        menuPlugins.add(h.get(FileEnginesPluginsHandler.class));
        menuPlugins.add(new Separator());
        menuPlugins.add(h.get(FileEnginesParsersHandler.class));
        menuPlugins.add(h.get(FileEnginesContributionsHandler.class));
        menuPlugins.add(h.get(FileEnginesInterpretersHandler.class));

        MenuManager menuEngines = MenuUtil.createAutoRefreshMenu(S.s(496));
        menuEngines.add(h.get(FileEnginesTypelibsHandler.class));
        menuEngines.add(h.get(FileEnginesSiglibsHandler.class));

        MenuManager menuFile = MenuUtil.createAutoRefreshMenu(S.s(504));
        menuFile.add(h.get(FileOpenHandler.class));
        menuFile.add(menuOpenRecent);
        menuFile.add(h.get(FileAddHandler.class));
        menuFile.add(h.get(FileCloseHandler.class));
        menuFile.add(new Separator());
        menuFile.add(menuExport);
        menuFile.add(h.get(FileSaveHandler.class));
        menuFile.add(h.get(FileSaveAsHandler.class));
        menuFile.add(new Separator());
        menuFile.add(menuScripts);
        menuFile.add(menuPlugins);
        menuFile.add(menuEngines);
        menuFile.add(new Separator());
        menuFile.add(h.get(FilePropertiesHandler.class));
        menuFile.add(h.get(FileShareHandler.class));
        menuFile.add(new Separator());
        menuFile.add(h.get(FileDeleteHandler.class));
        menuFile.add(h.get(FileNotificationsHandler.class));
        menuFile.add(h.get(FileAdvancedUnitOptionsHandler.class));
        if (!isMac) {
            menuFile.add(new Separator());
            menuFile.add(h.get(FileExitHandler.class));
        }

        MenuManager menuEditLanguage = new MenuManager(S.s(515));
        menuEditLanguage.setRemoveAllWhenShown(true);
        menuEditLanguage.addMenuListener(new EditLanguageMenuHandler());

        MenuManager menuEdit = MenuUtil.createAutoRefreshMenu(S.s(487));
        menuEdit.add(h.get(EditCutHandler.class));
        menuEdit.add(h.get(EditCopyHandler.class));
        menuEdit.add(h.get(EditPasteHandler.class));
        menuEdit.add(h.get(EditSelectAllHandler.class));
        menuEdit.add(h.get(EditClearHandler.class));

        menuEdit.add(new Separator());
        menuEdit.add(h.get(EditFindHandler.class));
        menuEdit.add(h.get(EditFindnextHandler.class));
        menuEdit.add(new Separator());
        menuEdit.add(menuEditLanguage);
        menuEdit.add(new Separator());
        menuEdit.add(h.get(EditSwitchThemeHandler.class));
        menuEdit.add(h.get(EditStyleHandler.class));
        if (!isMac) {
            menuEdit.add(h.get(EditOptionsHandler.class));
        }

        MenuManager menuNavigation = MenuUtil.createAutoRefreshMenu(S.s(522));
        menuNavigation.add(h.get(NavigationDoNotReplaceViewsHandler.class));
        menuNavigation.add(new Separator());
        menuNavigation.add(h.get(NavigationLocateUnitHandler.class));
        menuNavigation.add(h.get(NavigationLocateInCodeHierarchyHandler.class));
        menuNavigation.add(h.get(NavigationLocateInGlobalGraphHandler.class));
        menuNavigation.add(new Separator());
        menuNavigation.add(h.get(ActionNavigateForwardHandler.class));
        menuNavigation.add(h.get(ActionNavigateBackwardHandler.class));
        menuNavigation.add(h.get(ActionJumpToHandler.class));
        menuNavigation.add(new Separator());
        menuNavigation.add(h.get(ActionFollowHandler.class));
        menuNavigation.add(h.get(NavigationItemPreviousHandler.class));
        menuNavigation.add(h.get(NavigationItemNextHandler.class));
        menuNavigation.add(new Separator());
        menuNavigation.add(h.get(NavigationCanvasCenterHandler.class));
        menuNavigation.add(h.get(NavigationCanvasZoomInHandler.class));
        menuNavigation.add(h.get(NavigationCanvasZoomOutHandler.class));
        menuNavigation.add(h.get(NavigationCanvasZoomResetHandler.class));

        MenuManager menuAction = MenuUtil.createAutoRefreshMenu(S.s(459));
        menuAction.add(h.get(ActionDecompileHandler.class));
        menuAction.add(h.get(ActionGenerateGraphHandler.class));
        menuAction.add(new Separator());
        menuAction.add(h.get(ActionCommentHandler.class));
        menuAction.add(h.get(ActionViewCommentsHandler.class));
        menuAction.add(h.get(ActionRenameHandler.class));
        menuAction.add(h.get(ActionXrefHandler.class));
        menuAction.add(h.get(ActionConvertHandler.class));
        menuAction.add(h.get(ActionReplaceHandler.class));
        menuAction.add(h.get(ActionDeleteHandler.class));
        menuAction.add(new Separator());
        menuAction.add(h.get(ActionCreatePackageHandler.class));
        menuAction.add(h.get(ActionMoveToPackageHandler.class));
        menuAction.add(h.get(ActionTypeHierarchyHandler.class));
        menuAction.add(h.get(ActionOverridesHandler.class));

        MenuManager menuNative = MenuUtil.createAutoRefreshMenu("Nati&ve");
        menuNative.add(h.get(ActionDefineCodeHandler.class));
        menuNative.add(h.get(ActionDefineDataHandler.class));
        menuNative.add(h.get(ActionUndefineHandler.class));
        menuNative.add(new Separator());
        menuNative.add(h.get(ActionEditInstructionHandler.class));
        menuNative.add(h.get(ActionEditCodeHandler.class));
        menuNative.add(new Separator());
        menuNative.add(h.get(ActionDefineProcedureHandler.class));
        menuNative.add(h.get(ActionEditProcedureHandler.class));
        menuNative.add(new Separator());
        menuNative.add(h.get(ActionAutoSigningModeHandler.class));
        menuNative.add(h.get(ActionCreateSignaturePackageHandler.class));
        menuNative.add(h.get(ActionSelectSignaturePackageHandler.class));
        menuNative.add(h.get(ActionCreateProcedureSignatureHandler.class));
        menuNative.add(new Separator());
        menuNative.add(h.get(ActionDefineStringHandler.class));
        menuNative.add(h.get(ActionEditStringHandler.class));
        menuNative.add(new Separator());
        menuNative.add(h.get(ActionEditArrayHandler.class));
        menuNative.add(new Separator());
        menuNative.add(h.get(ActionEditTypeHandler.class));
        menuNative.add(h.get(ActionSelectTypeHandler.class));
        menuNative.add(h.get(ActionOpenTypeEditorHandler.class));
        menuNative.add(new Separator());
        menuNative.add(h.get(ActionEditStackframeHandler.class));
        menuNative.add(new Separator());
        menuNative.add(h.get(ActionStartAnalysisHandler.class));
        menuNative.add(h.get(ActionStopAnalysisHandler.class));

        MenuManager menuDebugger = MenuUtil.createAutoRefreshMenu(S.s(474));
        menuDebugger.add(h.get(DebuggerAttachHandler.class));
        menuDebugger.add(h.get(DebuggerRestartHandler.class));
        menuDebugger.add(h.get(DebuggerDetachHandler.class));
        menuDebugger.add(new Separator());
        menuDebugger.add(h.get(DebuggerRunHandler.class));
        menuDebugger.add(h.get(DebuggerPauseHandler.class));
        menuDebugger.add(h.get(DebuggerTerminateHandler.class));
        menuDebugger.add(new Separator());
        menuDebugger.add(h.get(DebuggerResumeThreadHandler.class));
        menuDebugger.add(h.get(DebuggerSuspendThreadHandler.class));
        menuDebugger.add(new Separator());
        menuDebugger.add(h.get(DebuggerStepIntoHandler.class));
        menuDebugger.add(h.get(DebuggerStepOverHandler.class));
        menuDebugger.add(h.get(DebuggerStepOutHandler.class));
        menuDebugger.add(h.get(DebuggerRunToLineHandler.class));
        menuDebugger.add(new Separator());
        menuDebugger.add(h.get(DebuggerToggleBreakpointHandler.class));

        MenuManager menuFocusPart = new MenuManager(S.s(568), null);
        menuFocusPart.setRemoveAllWhenShown(true);
        menuFocusPart.addMenuListener(new WindowFocusPartMenuHandler());

        MenuManager menuWindow = MenuUtil.createAutoRefreshMenu(S.s(582));
        menuWindow.add(h.get(WindowToggleToolbarHandler.class));
        menuWindow.add(h.get(WindowToggleStatusHandler.class));
        menuWindow.add(h.get(WindowRefreshHandler.class));
        menuWindow.add(h.get(WindowResetUIStateHandler.class));
        menuWindow.add(new Separator());
        menuWindow.add(h.get(WindowClosepartHandler.class));
        menuWindow.add(new Separator());
        menuWindow.add(h.get(WindowFocusProjectExplorerHandler.class));
        menuWindow.add(h.get(WindowFocusLoggerHandler.class));
        menuWindow.add(h.get(WindowFocusTerminalHandler.class));
        menuWindow.add(h.get(WindowFocusHierarchyHandler.class));
        menuWindow.add(menuFocusPart);

        MenuManager menuInternal = MenuUtil.createAutoRefreshMenu("Internal");
        menuInternal.add(h.get(InternalPrintModelHandler.class));
        menuInternal.add(h.get(InternalLoadModelHandler.class));
        menuInternal.add(h.get(InternalSaveModelHandler.class));

        MenuManager menuHelp = MenuUtil.createAutoRefreshMenu(S.s(510));
        if (Licensing.isDebugBuild()) {
            menuHelp.add(menuInternal);
            menuHelp.add(new Separator());
        }
        menuHelp.add(h.get(HelpUserManualHandler.class));
        menuHelp.add(h.get(HelpFAQHandler.class));
        menuHelp.add(h.get(HelpGroupHandler.class));
        menuHelp.add(new Separator());
        menuHelp.add(h.get(HelpDevPortalHandler.class));
        menuHelp.add(h.get(HelpApidocHandler.class));
        menuHelp.add(new Separator());
        menuHelp.add(h.get(HelpChangelistHandler.class));
        menuHelp.add(h.get(HelpCheckupdateHandler.class));
        if (!isMac) {
            menuHelp.add(new Separator());
            menuHelp.add(h.get(HelpAboutHandler.class));
        }

        menuManager.add(menuFile);
        menuManager.add(menuEdit);
        menuManager.add(menuNavigation);
        menuManager.add(menuAction);
        menuManager.add(menuNative);
        menuManager.add(menuDebugger);
        menuManager.add(menuWindow);
        menuManager.add(menuHelp);

        menuManager.updateAll(true);
    }

    private void buildToolbar(ToolBarManager toolbarManager) {
        AllHandlers h = AllHandlers.getInstance();

        toolbarManager.add(h.get(FileOpenHandler.class));
        toolbarManager.add(h.get(FileSaveHandler.class));
        toolbarManager.add(h.get(EditOptionsHandler.class));
        toolbarManager.add(h.get(EditSwitchThemeHandler.class));
        toolbarManager.add(h.get(FileNotificationsHandler.class));

        toolbarManager.add(new Separator());
        toolbarManager.add(h.get(FileShareHandler.class));

        toolbarManager.add(new Separator());
        toolbarManager.add(h.get(ActionJumpToHandler.class));
        toolbarManager.add(h.get(ActionFollowHandler.class));
        toolbarManager.add(h.get(ActionNavigateBackwardHandler.class));
        toolbarManager.add(h.get(ActionNavigateForwardHandler.class));

        toolbarManager.add(new Separator());
        toolbarManager.add(h.get(ActionDecompileHandler.class));
        toolbarManager.add(h.get(ActionCommentHandler.class));
        toolbarManager.add(h.get(ActionRenameHandler.class));
        toolbarManager.add(h.get(ActionXrefHandler.class));

        toolbarManager.add(new Separator());
        toolbarManager.add(h.get(ActionCreatePackageHandler.class));
        toolbarManager.add(h.get(ActionMoveToPackageHandler.class));
        toolbarManager.add(h.get(ActionTypeHierarchyHandler.class));
        toolbarManager.add(h.get(ActionOverridesHandler.class));

        toolbarManager.add(new Separator());
        toolbarManager.add(h.get(DebuggerAttachHandler.class));
        toolbarManager.add(h.get(DebuggerRunHandler.class));
        toolbarManager.add(h.get(DebuggerPauseHandler.class));
        toolbarManager.add(h.get(DebuggerTerminateHandler.class));
        toolbarManager.add(h.get(DebuggerStepIntoHandler.class));
        toolbarManager.add(h.get(DebuggerStepOverHandler.class));
        toolbarManager.add(h.get(DebuggerRunToLineHandler.class));

        toolbarManager.update(true);
    }

    public void onApplicationReady(App app) {
        logMemoryUsage();

        this.mainShell = app.getPrimaryShell();
        if (this.mainShell == null) {
            throw new RuntimeException("Cannot retrieve the main window's shell");
        }
        UI.getShellTracker().setMainShell(this.mainShell);


        this.mainShell.setText(mainShellOriginalTitle);


        final Shell shell = this.mainShell;
        UIUtil.setWidgetName(shell, "mainShell");


        this.threadMonitor = ThreadUtil.start(Licensing.isDebugBuild() ? "Memory Monitor" : null, new Runnable() {
            public void run() {
                int i = 0;
                for (; ; ) {
                    Runtime rt = Runtime.getRuntime();
                    final long used = rt.totalMemory() - rt.freeMemory();

                    UIExecutor.sync(RcpClientContext.this.display, new UIRunnable() {
                        public void runi() {
                            RcpClientContext.this.getStatusIndicator().setText(3, SizeFormatter.formatByteSize(used));
                        }
                    });


                    if (i++ % 10 == 0) {
                        RcpClientContext.logger.status("", new Object[0]);
                    }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });

        try {
            start();
        } catch (JebException e) {
            String f = "The following error occurred while JEB was initializing:\n\n\"%s\"\n\nPlease update your settings or contact support, and restart JEB. The program will now terminate.";

            UI.error(String.format(f, new Object[]{e.getMessage()}));
            terminate();
        }


        String taskname = "Initializing JEB engines...";
        if (this.coreOptions.isDevelopmentMode()) {
            taskname = taskname + " (dev. mode)";
        }
        executeTaskWithPopupDelay(1500, taskname, false, new Callable() {
            public Boolean call() throws Exception {
                try {
                    RcpClientContext.this.coreOptions.setAllowAsynchronousProcessing(true);
                    RcpClientContext.this.initializeEngines();
                    return Boolean.valueOf(true);
                } catch (JebException e) {
                    String f = "The following error occurred while JEB was initializing:\n\n\"%s\"\n\nPlease update your settings or contact support, and restart JEB. The program will now terminate.";

                    UI.error(String.format(f, new Object[]{e.getMessage()}));
                    AbstractContext.terminate();
                }
                return Boolean.valueOf(false);
            }
        });
        logger.debug("The JEB engines are initialized", new Object[0]);

        if ((getEnginesContext() == null) || (getCoreContext() == null)) {
            logger.error("The JEB engines were not initialized!", new Object[0]);
            return;
        }


        EnginesListener.initialize(this, getEnginesContext());


        CoreListener.initialize(this, getCoreContext());


        this.threadSoftwareUpdater = startUpdater();


        if (shouldCheckPublicAnnouncements()) {
            checkPublicAnnouncement();
        }


        showAvailableDecompilers();


        notifyListeners(new JebClientEvent(JC.InitializationComplete));


        getPartManager().initialize();


        if ((!isRestartRequired()) && (getPropertyManager().getInteger(".ui.survey.CustomSurvey1Timestamp") == 0) &&
                (getPropertyManager().getInteger(".RunCount") >= 5)) {
            this.display.timerExec(120000, new Runnable() {
                public void run() {
                    int ts = (int) (System.currentTimeMillis() / 1000L);
                    RcpClientContext.this.getPropertyManager().setInteger(".ui.survey.CustomSurvey1Timestamp", Integer.valueOf(ts));
                    new CustomSurveyDialog(shell, RcpClientContext.this).open();
                }
            });
        }


        if (this.inputpath != null) {


            UIExecutor.async(this.display, new UIRunnable() {
                public void runi() {
                    RcpClientContext.this.loadInputAsProject(shell, RcpClientContext.this.inputpath);
                }
            });
        }
    }

    public RcpErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public ThreadManager getThreadManager() {
        return this.threadManager;
    }

    public void onCoreError() {
        this.basicChecksPassed = false;
    }

    public void initialize(String[] argv) {
        super.initialize(argv);


        IPropertyDefinitionManager pdm = new PropertyDefinitionManager("ui", super.getPropertyDefinitionManager());

        pdm.addDefinition("LoggerMaxLength", PropertyTypeInteger.createPositiveOrZero(Integer.valueOf(262144)));
        pdm.addDefinition("AutoSavePeriod", PropertyTypeInteger.createPositiveOrZero(Integer.valueOf(60)));
        pdm.addDefinition("AutoOpenDefaultUnit", PropertyTypeBoolean.create(Boolean.TRUE));
        pdm.addInternalDefinition("Theme", PropertyTypeString.create());
        pdm.addInternalDefinition("CodeFont", PropertyTypeString.create());
        pdm.addInternalDefinition("ClassIdStyles", PropertyTypeString.create());
        pdm.addInternalDefinition("ItemStyles", PropertyTypeString.create());
        pdm.addInternalDefinition("TextHistoryData", PropertyTypeString.create());
        pdm.addInternalDefinition("WidgetBoundsHistoryData", PropertyTypeString.create());
        pdm.addInternalDefinition("WidgetPersistenceData", PropertyTypeString.create());
        pdm.addInternalDefinition("DialogPersistenceData", PropertyTypeString.create());
        pdm.addInternalDefinition("RecentlyOpenedFiles", PropertyTypeString.create());
        pdm.addInternalDefinition("RecentlyExecutedScripts", PropertyTypeString.create());
        pdm.addInternalDefinition("AllowInternalCommands", PropertyTypeBoolean.create(Boolean.FALSE));
        pdm.addInternalDefinition("OptionsDialogAdvancedMode", PropertyTypeBoolean.create(Boolean.FALSE));
        pdm.addDefinition("AlwaysLoadFragments", PropertyTypeBoolean.create(Boolean.FALSE));
        pdm.addDefinition("ExpandTreeNodesOnFiltering", PropertyTypeBoolean.create(Boolean.TRUE));
        pdm.addDefinition("NavigationBarPosition", PropertyTypeInteger.create(0, 4, Integer.valueOf(2)));
        pdm.addInternalDefinition("NavigationBarThickness", PropertyTypeInteger.createPositiveOrZero(Integer.valueOf(0)));
        pdm.addDefinition("ProjectUnitSync", PropertyTypeBoolean.create(Boolean.valueOf(false)));
        pdm.addDefinition("ConsolePartSync", PropertyTypeBoolean.create(Boolean.valueOf(true)));
        pdm.addDefinition("DoNotReplaceViews", PropertyTypeBoolean.create(Boolean.valueOf(true)));
        pdm.addDefinition("ShowWarningNotificationsInStatus", PropertyTypeBoolean.create(Boolean.valueOf(true)));
        pdm.addDefinition("KeyboardShortcutsFile", PropertyTypePath.create("jeb-shortcuts.cfg"));

        IPropertyDefinitionManager pdmState = new PropertyDefinitionManager("state", pdm);
        pdmState.addDefinition("MainShellBounds", PropertyTypeString.create(), "blank= default, -1=maximized, \"x,y,w,h\"=set");


        IPropertyDefinitionManager pdmText = new PropertyDefinitionManager("text", pdm);
        pdmText.addDefinition("ShowHorizontalScrollbar", PropertyTypeBoolean.create(Boolean.valueOf(true)));
        pdmText.addDefinition("ShowVerticalScrollbar", PropertyTypeBoolean.create(Boolean.valueOf(false)));
        pdmText.addDefinition("DisplayEolAtEod", PropertyTypeBoolean.create(Boolean.valueOf(true)));
        pdmText.addDefinition("CharactersPerLineMax", PropertyTypeInteger.createPositiveOrZero(Integer.valueOf(4000)));
        pdmText.addDefinition("CharactersPerLineAtEnd", PropertyTypeInteger.createPositiveOrZero(Integer.valueOf(100)));
        pdmText.addDefinition("AllowLineWrapping", PropertyTypeBoolean.create(Boolean.valueOf(false)));
        pdmText.addDefinition("CharactersWrap", PropertyTypeInteger.create(Integer.valueOf(-1)));
        pdmText.addDefinition("ScrollLineSize", PropertyTypeInteger.create(Integer.valueOf(0)));
        pdmText.addDefinition("PageLineSize", PropertyTypeInteger.create(Integer.valueOf(0)));
        pdmText.addDefinition("PageMultiplier", PropertyTypeInteger.create(Integer.valueOf(0)));
        pdmText.addDefinition("CaretBehaviorViewportStatic", PropertyTypeBoolean.create(Boolean.valueOf(false)));

        IPropertyDefinitionManager pdmCfg = new PropertyDefinitionManager("cfg", pdmText);
        pdmCfg.addDefinition("ShowAddresses", PropertyTypeBoolean.create(Boolean.valueOf(false)));
        pdmCfg.addDefinition("ShowBytesCount", PropertyTypeInteger.create(Integer.valueOf(0)));

        IPropertyDefinitionManager pdmGraphs = new PropertyDefinitionManager("graphs", pdmText);
        pdmGraphs.addDefinition("LockView", PropertyTypeBoolean.create(Boolean.valueOf(false)));
        pdmGraphs.addDefinition("KeepInMainDock", PropertyTypeBoolean.create(Boolean.valueOf(false)));
        pdmGraphs.addDefinition("AutoGenerate", PropertyTypeBoolean.create(Boolean.valueOf(false)));

        IPropertyDefinitionManager pdmTree = new PropertyDefinitionManager("tree", pdm);
        pdmTree.addDefinition("BucketFlatThreshold", PropertyTypeInteger.createPositiveOrZero(Integer.valueOf(2000)));
        pdmTree.addDefinition("BucketTreeThreshold", PropertyTypeInteger.createPositiveOrZero(Integer.valueOf(200)));
        pdmTree.addDefinition("BucketFlatMaxElements", PropertyTypeInteger.createPositiveOrZero(Integer.valueOf(500)));
        pdmTree.addDefinition("BucketTreeMaxElements", PropertyTypeInteger.createPositiveOrZero(Integer.valueOf(200)));
        pdmTree.addDefinition("UseExplicitDefaultPackage", PropertyTypeBoolean.create(Boolean.valueOf(true)));
    }

    public void start() throws JebException {
        super.start();
    }

    private void showAvailableDecompilers() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (IUnitIdentifier ident : getEnginesContext().getUnitIdentifiers()) {
            String type = ident.getFormatType();
            if ((type != null) && (type.startsWith("dcmp_"))) {
                if (i >= 1) {
                    sb.append(", ");
                }
                sb.append(type.substring("dcmp_".length()));
                i++;
            }
        }
        if (i >= 1) {
            sb.insert(0, String.format("Available decompiler%s: ", new Object[]{i >= 2 ? "s" : ""}));

            logger.status(sb.toString(), new Object[0]);
        }
    }

    public boolean isHeadless() {
        return false;
    }


    public boolean displayEula(String eula) {
        Shell shell = getActiveShell();
        if ((shell == null) || (shell.isDisposed())) {
            logger.warn("The EULA cannot be displayed.", new Object[0]);
            return false;
        }

        TextDialog eulabox = new TextDialog(shell, S.s(312), eula, "eulaDialog");
        eulabox.setLineCount(20);
        eulabox.setColumnCount(80);
        eulabox.setEditable(false);
        eulabox.setSelected(false);
        eulabox.setOkLabelId(Integer.valueOf(3));
        eulabox.setCancelLabelId(Integer.valueOf(235));
        if (eulabox.open() == null) {
            UI.error("The license agreement must be accepted in order to proceed.\n\nJEB will terminate.");
            return false;
        }


        return true;
    }


    public void displayDemoInformation(String demoInfo) {
        Shell shell = getActiveShell();
        if ((shell == null) || (shell.isDisposed())) {
            logger.warn("The demo information cannot be displayed.", new Object[0]);
            return;
        }

        TextDialog v = new TextDialog(shell, S.s(249), demoInfo, "demoInfoDialog");
        v.setLineCount(10);
        v.setColumnCount(80);
        v.setEditable(false);
        v.setSelected(false);
        v.setOkLabelId(Integer.valueOf(201));
        v.setCancelLabelId(null);
        v.open();
    }

    public void openChangelistDialog(Shell shell, String optionalChangelist) {
        String changelist = optionalChangelist;
        if (optionalChangelist == null) {
            changelist = Licensing.getChangeList();
        }

        TextDialog v = new TextDialog(shell, S.s(190), changelist, "changelistDialog");
        v.setLineCount(15);
        v.setColumnCount(90);
        v.setEditable(false);
        v.setSelected(false);
        v.setOkLabelId(Integer.valueOf(201));
        v.setCancelLabelId(null);
        v.open();
    }

    public void onUpdatedSoftware(String changelist, Version oldVersion) {
        Shell shell = getActiveShell();
        if ((shell == null) || (shell.isDisposed())) {
            logger.warn("The changelist cannot be displayed.", new Object[0]);
            return;
        }

        if (changelist != null) {
            if (!isPreRelease()) {
                openChangelistDialog(shell, changelist);
            } else if (app_ver.getMajor() == 3) {
                String welcomeFmt = "Thank you for using this %s version of JEB 3.\n\nThis new version of JEB features:\n- A lighter, faster UI client; it also comes with a Dark theme and customizable keyboard shortcuts\n- Global interactive graphs views, such as the callgraph view\n- Major improvements to our native decompilation pipeline\n- The first release of our Intel x86 and x86-64 interactive decompilers\n- Type libraries for the Android NDK\n- Type libraries for Windows user-mode and kernel-mode binaries, for x86 and ARM (32 and 64 bit)\n- Signature libraries for common Android NDK libraries\n- Signature libraries for Visual Studio compiled programs\n- Many more additions and improvements that we will detail when the full release is available\n\nThank you for reporting bugs to our Support team.";


                String welcomeMsg = String.format(welcomeFmt, new Object[]{getChannelName().toLowerCase()});
                UI.popupOptional(shell, 0, "Welcome", welcomeMsg, "dlgWelcomePreRelease");
            }
        } else {
            String message = String.format(S.s(347), new Object[]{app_ver.toString()});
            message = message + "\n\n" + S.s(348);
            if (UI.question(shell, S.s(823), message)) {
                BrowserUtil.openInBrowser("https://www.pnfsoftware.com/jeb/changelist");
            }
        }

        if (oldVersion == null) {
            return;
        }


        oldVersion = Version.create(oldVersion.getMajor(), oldVersion.getMinor(), oldVersion.getBuildid());


        if (Licensing.isReleaseBuild()) {
        }
    }


    public String retrieveLicenseKey(String licdata) {
        Shell shell = getActiveShell();
        if ((shell == null) || (shell.isDisposed())) {
            logger.warn("The licensing dialog cannot be displayed.", new Object[0]);
            return null;
        }

        LicenseKeyAutoDialog dlg = new LicenseKeyAutoDialog(shell, licdata, getNetworkUtility(), this);
        String r = dlg.open();
        if (r == null) {
            return null;
        }


        NetProxyInfo proxyinfo = dlg.getNet().getProxyInformation();
        if (proxyinfo != null) {
            setProxyString(proxyinfo.toString());
        }


        return r;
    }

    public void notifySupportExpired() {
        Shell shell = getActiveShell();
        if ((shell == null) || (shell.isDisposed())) {
            logger.warn("The support expiration dialog cannot be displayed.", new Object[0]);
            return;
        }

        if (Licensing.isDemoBuild()) {
            String msg = S.s(255);
            UI.warn(shell, S.s(821), msg);

            Toast.urgent(shell, "*** Demo has expired ***")
                    .setFont(UIAssetManager.getInstance().getFont(shell.getFont(), Integer.valueOf(15), Integer.valueOf(1)))
                    .setDuration(Long.MAX_VALUE).show();
            return;
        }

        String msg = S.s(759) + "\n\nPress OK to check for or install an update. Press Cancel to continue.";

        if (UI.confirm(shell, S.s(821), msg)) {
            new SoftwareUpdateDialog(shell, this, true).open();
        }
    }

    Thread startUpdater() {
        return ThreadUtil.start(Licensing.isDebugBuild() ? "Software Updater" : null, new Runnable() {
            public void run() {
                int periodInHours = 6;
                for (; ; ) {
                    if (!RcpClientContext.this.isRestartRequired()) {

                        if (RcpClientContext.this.shouldCheckUpdates()) {
                            RcpClientContext.this.checkUpdate();
                        }
                        try {
                            Thread.sleep(21600000L);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        });
    }


    public boolean checkUpdate() {
        return checkUpdate(false, false, null);
    }


    public boolean checkUpdate(boolean calledByUser, boolean expiredLicense, IProgressCallback progressCallback) {
        if (!this.updateCheckState.compareAndSet(0, 1)) {
            return false;
        }
        try {
            final SoftwareBuildInfo sbi = new SoftwareBuildInfo();

            int ping = ping(true, Integer.MAX_VALUE, sbi, progressCallback);
            String message;
            if ((ping == -2) && (calledByUser)) {
                message = String.format(S.s(94), new Object[]{"software@pnfsoftware.com"});
                UI.warn(message);
            } else if (ping == 2) {
                this.updateCheckState.set(2);


                UIExecutor.async(this.display, new UIRunnable() {
                    public void runi() {
                        Shell shell = RcpClientContext.this.getActiveShell();
                        if ((shell != null) && (!shell.isDisposed())) {
                            RcpClientContext.this.installUpdate(shell, sbi);
                        }
                    }
                });
            } else if ((ping == 0) && (calledByUser)) {
                message = expiredLicense ? "It seems there is no available update for your license at this time." : S.s(808);

                if ((sbi.getFlags() & 0x1) != 0) {
                    message = String.format("Good news, JEB version %d is available!\n\nThis major update needs to be installed separately. Please check your registered email for download details.", new Object[]{


                            Integer.valueOf(app_ver.getMajor() + 1)});
                }
                UI.info(message);
            }
            return true;
        } finally {
            this.updateCheckState.compareAndSet(1, 0);
        }
    }


    public boolean installUpdate(Shell shell, SoftwareBuildInfo sbi) {
        if ((this.updateCheckState.get() != 2) && (!this.updateCheckState.compareAndSet(0, 2))) {
            return false;
        }

        int nextState = 0;
        try {
            String channelStr = "";
            if (sbi != null) {
                int channel = sbi.getChannel();

                if (channel == 1) {
                    channelStr = " (beta)";
                } else if (channel == 2) {
                    channelStr = " (alpha)";

                } else if (channel >= 3) {
                    channelStr = String.format(" (channel-%d)", new Object[]{Integer.valueOf(channel)});
                }
            }


            String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            ArrayList<String> command = new ArrayList();
            command.add(javaBin);
            command.add("-jar");
            command.add(getAppDirectory() + File.separator + "jebi.jar");
            command.add("--up");
            command.add("--di");
            command.add("--start-client");
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File(getBaseDirectory()));
            try {
                logger.debug("Executing installer: %s", new Object[]{builder.command()});
                logger.debug("Using base directory: %s", new Object[]{builder.directory()});
                builder.start();
            } catch (IOException e) {
                logger.catchingSilent(e);
            }

            String title = String.format("Software update%s", new Object[]{channelStr});
            String message = "A software update is ready to be installed.\n\nClose JEB instance(s) at your best convenience. The update will be installed next time you start the program.";

            UI.info(shell, title, message);
            nextState = 3;
            return true;
        } finally {
            this.updateCheckState.set(nextState);
        }
    }

    public synchronized void checkPublicAnnouncement() {
        if (this.threadMotd != null) {
            try {
                this.threadMotd.join(500L);
                if (this.threadMotd.isAlive()) {
                    return;
                }
            } catch (InterruptedException localInterruptedException) {
            }
        }


        this.threadMotd = ThreadUtil.start(Licensing.isDebugBuild() ? "Public Announcement" : null, new Runnable() {
            public void run() {
                PublicAnnouncement r = RcpClientContext.this.retrieveLatestPublicAnnouncement();
                if ((r != null) && (r.getId() > RcpClientContext.this.getLastPublicAnnouncementId())) {
                    String message = r.getText();
                    if (!Strings.isBlank(message)) {
                        UI.info(message);
                    }
                    RcpClientContext.this.setLastPublicAnnouncementId(r.getId());
                }
            }
        });
    }


    public PartManager getPartManager() {
        if (this.pman == null) {
            if (this.app == null) {
                throw new IllegalStateException("The reference to the app is not set yet");
            }
            this.pman = new PartManager(this, this.app, new PartManager.PropertyProvider(this));
        }
        return this.pman;
    }

    public IViewManager getViewManager() {
        return getPartManager();
    }

    public MultiInterpreter getMasterInterpreter() {
        if (this.masterInterpreter == null) {
            this.masterInterpreter = new MultiInterpreter();
        }
        return this.masterInterpreter;
    }

    public void onApplicationClose(App app) {
        try {
            int duration = (int) (System.currentTimeMillis() / 1000L) - getStartTimestamp();
            getTelemetry().record("sessionClose", "duration", duration + "", "projectCount", this.sessionProjectCount + "");

            if (Licensing.isDebugBuild()) {
                logger.debug("UI-Executor statistics:\n%s", new Object[]{UIExecutor.format()});
            }


            if (this.clearUIState) {
                clearWidgetBounds();
                clearWidgetPersistenceProvider();
                clearDialogPersistenceDataProvider();
            }
            saveWidgetBoundsManager();
            saveWidgetPersistenceProvider();
            saveDialogPersistenceDataProvider();

            saveTextHistoryData();
            saveRecentlyOpenedFiles();
            saveRecentlyExecutedScripts();

            if (this.styleManager != null) {
                saveStyleManager(this.styleManager);
            }
            saveTheme();


            if (this.jebSessionLogStream != null) {
                this.jebSessionLogStream.close();
                this.jebSessionLogStream = null;
            }

            super.stop();
        } catch (Exception e) {
            logger.catching(e);
        }
    }

    public String getLastReloadedProjectPath() {
        return this.lastReloadedProjectPath;
    }


    public boolean onApplicationCloseAttempt(App app) {
        return attemptCloseOpenedProject(null);
    }

    public boolean attemptCloseOpenedProject(Shell shell) {
        if (shell == null) {
            shell = getActiveShell();
        }

        if (!dlgCheckLockedUnits(shell)) {
            return false;
        }

        boolean save = false;
        if ((Licensing.isFullBuild()) &&
                (hasOpenedProject())) {
            MessageBox mb = new MessageBox(shell, 456);
            mb.setText(S.s(207));
            mb.setMessage(S.s(659) + ".\n\n" + S.s(660));
            int r = mb.open();
            if (r == 64) {
                save = true;
            } else if (r == 256) {
                return false;
            }
        }


        getPartManager().closeUnitParts();


        if (this.clearUIState) {
            removeAppLayoutInformation();
        } else {
            saveAppLayoutInformation();
        }


        if (save) {
            saveOpenedProject(shell, false, null);
        }

        this.lastReloadedProjectPath = null;

        this.preferQuickSave = null;


        closeOpenedProject();


        if ((mainShellOriginalTitle != null) && (shell != null)) {
            shell.setText(mainShellOriginalTitle);
        }

        getStatusIndicator().clearAdditionalContributions();


        if (this.projectReadyTimestamp != 0) {
            int projectDuration = (int) (System.currentTimeMillis() / 1000L) - this.projectReadyTimestamp;
            getTelemetry().record("projectClose", "duration", projectDuration + "", "artifactCount", this.projectArtifactCount + "");
        }


        return true;
    }

    private IRuntimeProject loadProject(Shell shell, final String path) {
        IRuntimeProject r = (IRuntimeProject) executeTask(S.s(445) + "...", new CallableWithProgressCallback() {
            public IRuntimeProject call() {
                try {
                    IRuntimeProject prj = RcpClientContext.this.getEnginesContext().loadProject(path, this.callback);
                    RcpClientContext.this.projectArtifactCount = prj.getLiveArtifacts().size();
                    return prj;
                } catch (SerializationException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return r;
    }

    private boolean dlgCheckLockedUnits(Shell shell) {
        IRuntimeProject rp = getOpenedProject();
        if (rp != null) {
            for (IUnit unit : RuntimeProjectUtil.getAllUnits(rp)) {
                if (unit.getLock().isLocked()) {
                    String msg = String.format("The unit \"%s\" (and possibly others) is locked, most likely because a background task is executing.\n\nWould you like to proceed?", new Object[]{

                            UnitUtil.buildFullyQualifiedUnitPath(unit)});
                    return UI.confirm(shell, "Unit locked", msg);
                }
            }
        }
        return true;
    }

    public boolean saveOpenedProject(Shell shell, boolean checkForLockedUnits, final String persistenceKey) {
        final IRuntimeProject rp = getOpenedProject();
        if (rp == null) {
            return false;
        }

        if ((checkForLockedUnits) && (!dlgCheckLockedUnits(shell))) {
            return false;
        }


        int strategy = rp.getPersistenceStrategy();

        if ((strategy == 0) && (this.preferQuickSave == null)) {
            if (shouldOfferQuickSave(rp)) {
                String msg = String.format("This project appears to be very large. Performing a regular \"full save\" may take time and consume a large amount of memory.\n\nWould you like to perform a \"quick save\" instead?", new Object[0]);


                this.preferQuickSave = Boolean.valueOf(UI.question(shell, "Persisting a Large Project", msg));
                rp.setPersistenceStrategy(this.preferQuickSave.booleanValue() ? 2 : 1);
            } else {
                this.preferQuickSave = Boolean.valueOf(false);
            }
        }


        long t0 = System.currentTimeMillis();
        boolean r = executeTask(S.s(661) + "...", new Runnable() {
            public void run() {
                try {
                    String projectKey = rp.getKey();

                    RcpClientContext.this.getEnginesContext().saveProject(projectKey, persistenceKey, null);


                } catch (SerializationException | IOException e) {


                    throw new RuntimeException(e);
                }

            }
        });
        int duration = (int) ((System.currentTimeMillis() - t0) / 1000L);
        getTelemetry().record("projectSave", "duration", "" + duration);
        return r;
    }

    private boolean shouldOfferQuickSave(IRuntimeProject prj) {
        try {
            boolean isApk = false;
            boolean isLarge = false;
            if (prj.getLiveArtifacts().size() >= 1) {
                ILiveArtifact a = prj.getLiveArtifact(0);
                for (IUnit topunit : a.getUnits()) {
                    if ((topunit instanceof IApkUnit)) {
                        isApk = true;
                        break;
                    }
                }
                IInput input = a.getArtifact().getInput();
                if ((input != null) && (input.getCurrentSize() >= 1048576L)) {
                    isLarge = true;
                }
            }
            return (isApk) && (isLarge);
        } catch (Exception e) {
            getErrorHandler().processThrowableSilent(e);
        }
        return false;
    }


    public boolean loadInputAsProject(Shell shell, String path) {
        try {
            path = new File(path).getCanonicalPath();
        } catch (IOException e) {
            path = new File(path).getAbsolutePath();
        }

        logger.i("Processing file: %s...", new Object[]{path});
        if ((path == null) || (!IO.isFile(path)) || (!this.basicChecksPassed)) {
            logger.warn("Invalid input or input is not a file: \"%s\"", new Object[]{path});
            return false;
        }
        if (!new File(path).canRead()) {
            logger.warn("Input file cannot be read: \"%s\"", new Object[]{path});
            return false;
        }

        if (!attemptCloseOpenedProject(shell)) {
            logger.warn("The existing project could not be closed", new Object[0]);
            return false;
        }

        IRuntimeProject project = null;
        boolean newProject = false;


        if (IO.getFirstIntLE(path) == 843203658) {
            logger.info("Opening an existing project (%s)", new Object[]{path});
            this.lastReloadedProjectPath = path;

            long t0 = System.currentTimeMillis();
            project = loadProject(shell, path);
            if (project == null) {
                logger.warn("Invalid project file", new Object[0]);
                return false;
            }
            int duration = (int) ((System.currentTimeMillis() - t0) / 1000L);
            getTelemetry().record("projectLoad", "duration", "" + duration, "filesize", "" + new File(path).length());

            newProject = false;
        } else {
            logger.info("Creating a new project (primary file: %s)", new Object[]{path});
            String projectPath = buildProjectPath(path);

            int lastIndex = findLatestProjectDatabase(projectPath);
            if (lastIndex >= 1) {
                projectPath = buildProjectPath(path, lastIndex);
            }

            if (IO.isFile(projectPath)) {
                if (IO.getFirstIntLE(projectPath) == 843203658) {
                    MessageBox mb = new MessageBox(shell, 194);
                    mb.setText(S.s(207));
                    mb.setMessage(String.format("Project database \"%s\" matches the file about to be opened.\n\nWould you like to open the existing project?", new Object[]{projectPath}));

                    int r = mb.open();
                    if (r == 64) {
                        return loadInputAsProject(shell, projectPath);
                    }
                }

                for (; ; ) {
                    lastIndex++;
                    projectPath = buildProjectPath(path, lastIndex);
                    if (!new File(projectPath).exists()) {
                        break;
                    }
                }
            }

            long t0 = System.currentTimeMillis();
            project = loadProject(shell, projectPath);
            if (project == null) {
                logger.warn("Invalid project file", new Object[0]);
                return false;
            }
            int duration = (int) ((System.currentTimeMillis() - t0) / 1000L);
            getTelemetry().record("projectCreate", "duration", "" + duration);

            processFileArtifact(shell, project, path);
            newProject = true;
        }

        addRecentlyOpenedFile(path);


        String title = mainShellOriginalTitle;
        String key = project.getKey();
        if ((newProject) || (Strings.equals(path, key))) {
            title = String.format("%s - %s", new Object[]{mainShellOriginalTitle, path});
        } else {
            title = String.format("%s - %s (key: %s)", new Object[]{mainShellOriginalTitle, path, key});
        }
        shell.setText(title);


        getStatusIndicator().removeContribution("contribUnitNotificationWarning");
        if ((getPropertyManager().getBoolean(".ui.ShowWarningNotificationsInStatus")) &&
                (RuntimeProjectUtil.hasNotification(project, NotificationType.WARNING.getLevel()))) {
            StatusLineContributionItem contrib = new NotificationWarningContribution(this);
            getStatusIndicator().addContribution(contrib);
        }


        this.sessionProjectCount += 1;
        this.projectReadyTimestamp = ((int) (System.currentTimeMillis() / 1000L));
        return true;
    }

    private String buildProjectPath(String path, int index) {
        if (index <= 0) {
            return path + ".jdb2";
        }

        return path + ".jdb2." + index;
    }

    private String buildProjectPath(String path) {
        return buildProjectPath(path, 0);
    }

    private int findLatestProjectDatabase(String projectPath) {
        File projectFile = new File(projectPath);
        File dir = IO.getParentFile2(projectFile);
        FileFilter fileFilter = new WildcardFileFilter(projectFile.getName() + ".*", IOCase.INSENSITIVE);

        int lastIndex = 0;
        for (File f : dir.listFiles(fileFilter)) {
            int pos = f.getName().lastIndexOf(".");
            try {
                if (pos >= 0) {
                    int index = Integer.parseInt(f.getName().substring(pos + 1));
                    if (index > lastIndex) {
                        lastIndex = index;
                    }
                }
            } catch (NumberFormatException localNumberFormatException) {
            }
        }

        return lastIndex;
    }

    public void setDebuggingMode(boolean enabled) {
        if (this.mainShell == null) {
            return;
        }

        String sfx = " [DEBUGGING]";

        String s = this.mainShell.getText();
        if (enabled) {
            if (s.endsWith(" [DEBUGGING]")) {
                return;
            }
            s = s + " [DEBUGGING]";
        } else {
            if (!s.endsWith(" [DEBUGGING]")) {
                return;
            }
            s = s.substring(0, s.length() - " [DEBUGGING]".length());
        }
        this.mainShell.setText(s);
    }

    public boolean loadInputAsAdditionalArtifact(Shell shell, String path) {
        IRuntimeProject project = getOpenedProject();
        if (project == null) {
            return false;
        }

        processFileArtifact(shell, project, path);
        return true;
    }

    public boolean processFileArtifact(Shell shell, final IRuntimeProject project, String path) {
        logger.info("Adding artifact to project: %s", path);
        File artifactFile = new File(path);
        final IArtifact artifact;
        try {
            artifact = new Artifact(artifactFile.getName(), new FileInput(artifactFile));
            this.projectArtifactCount += 1;
        } catch (IOException e) {
            logger.warn("The artifact cannot be processed");
            return false;
        }

        getTelemetry().record("projectAddArtifact", "filesize", "" + artifactFile.length());

        return executeTask(S.s(664) + "...", new Runnable() {
            public void run() {
                ILiveArtifact liveArtifact = project.processArtifact(artifact);
                RcpClientContext.logger.i("Artifact yield: %d units", liveArtifact.getUnits().size());
            }
        });
    }

    public <T> T executeTask(String taskName, Callable<T> callable) {
        return (T) executeTask(taskName, false, callable);
    }

    public <T> T executeTask(String taskName, boolean errorOnCancel, Callable<T> callable) {
        Shell shell = getActiveShell();
        if ((shell == null) || (shell.isDisposed())) {
            throw new RuntimeException("It seems the shell or display is being shut down; the task cannot be executed.");
        }
        try {
            return (T) UI.getTaskManager().create(shell, taskName, callable, 0L);

        } catch (InterruptedException localInterruptedException) {
        } catch (InvocationTargetException e) {
            processTaskException(e.getTargetException(), errorOnCancel);
        }
        return null;
    }

    public boolean executeTask(String taskName, Runnable runnable) {
        return executeTask(taskName, false, runnable);
    }

    public boolean executeTask(String taskName, boolean errorOnCancel, Runnable runnable) {
        Shell shell = getActiveShell();
        if ((shell == null) || (shell.isDisposed())) {
            throw new RuntimeException("It seems the shell or display is being shut down; the task cannot be executed.");
        }
        try {
            UI.getTaskManager().create(shell, taskName, runnable, 0L);
            return true;

        } catch (InterruptedException localInterruptedException) {
        } catch (InvocationTargetException e) {
            processTaskException(e.getTargetException(), errorOnCancel);
        }
        return false;
    }


    public void executeTaskWithPopupDelay(int delayMs, String taskName, boolean errorOnCancel, final Runnable runnable) {
        executeTaskWithPopupDelay(delayMs, taskName, errorOnCancel, new Callable() {
            public Void call() throws Exception {
                runnable.run();
                return null;
            }
        });
    }

    public <T> T executeTaskWithPopupDelay(int delayMs, String taskName, boolean errorOnCancel, Callable<T> callable) {
        Shell shell = getActiveShell();
        if ((shell == null) || (shell.isDisposed())) {
            throw new RuntimeException("It seems the shell or display is being shut down; the task cannot be executed.");
        }
        try {
            return (T) UI.getTaskManager().create(shell, taskName, callable, delayMs);

        } catch (InterruptedException localInterruptedException) {
        } catch (InvocationTargetException e) {
            processTaskException(e.getTargetException(), errorOnCancel);
        }
        return null;
    }

    public <T> T executeNetworkTask(Callable<T> callable) {
        return (T) executeTaskWithPopupDelay(2000, "Please wait, a network query is taking longer than expected...", false, callable);
    }

    public <T> T executeUsuallyShortTask(Callable<T> callable) {
        return (T) executeTaskWithPopupDelay(1000, "Please wait, a task is taking longer than expected...", false, callable);
    }

    private void processTaskException(Throwable e, boolean errorOnCancel) {
        if (e == null) {
            return;
        }

        logger.i(Throwables.formatStacktrace(e), new Object[0]);


        if ((!(e instanceof InterruptionException)) || (errorOnCancel)) {
            UI.error(null, S.s(768), formatTaskException(e));
            getErrorHandler().processThrowableVerbose(e);

        } else if ((e instanceof UnitLockedException)) {
            getErrorHandler().processThrowableVerbose(e);
        }
    }

    private static String formatTaskException(Throwable e) {
        String msg = "An error occurred while executing the task";
        Throwable e0 = Throwables.getRootCause(e);
        if (e0 != null) {
            msg = e0.toString();
            if (((e0 instanceof LinkageError)) || ((e0 instanceof ClassNotFoundException))) {
                msg = msg + "\n\nIf processing was done by a plugin, make sure to use the latest version.";
            } else if ((e0 instanceof SerializationException)) {
                String classname = ((SerializationException) e0).getClassName();
                if (classname != null) {
                    msg = msg + String.format("\n\nSerialization error related to type: %s\n", new Object[]{classname});
                }
            }
        }
        return msg;
    }

    public boolean setupController() {
        Shell shell = getActiveShell();
        if ((shell == null) || (shell.isDisposed())) {
            logger.warn("The controller setup dialog cannot be displayed", new Object[0]);
            return false;
        }

        ControllerAddressDialog dlg = new ControllerAddressDialog(shell, this);
        return dlg.open().booleanValue();
    }

    public void notifyFloatingClient(final ControllerNotification notification) {
        final long code = notification.ctlCode;
        int hintContinueLeft = notification.ctlHintContinueLeft;

        if (code == 0L) {
            throw new RuntimeException();
        }
        notification.clientChoice = 0;
        UIExecutor.sync(this.display, new UIRunnable() {
            public void runi() {
                Shell shell = RcpClientContext.this.getActiveShell();
                if ((shell == null) || (shell.isDisposed())) {
                    return;
                }
                if (code == -1L) {
                    if (hintContinueLeft >= 1) {
                        MessageBox mbox = new MessageBox(shell, 456);
                        mbox.setText(S.s(821));
                        mbox.setMessage(String.format("%s.\n\n%s.", S.s(219), S.s(220)));
                        int r = mbox.open();
                        if (r == 64) {
                            RcpClientContext.this.saveOpenedProject(shell, false, null);
                        } else if (r == 256) {
                            notification.clientChoice = 1;
                        }
                    } else {
                        MessageBox mbox = new MessageBox(shell, 193);
                        mbox.setText(S.s(304));
                        mbox.setMessage(String.format("%s.\n\n%s.", S.s(219), "Press Yes to save your work and exit, No to exit without saving"));

                        int r = mbox.open();
                        if (r == 64) {
                            RcpClientContext.this.saveOpenedProject(shell, false, null);
                        }
                    }
                } else if (code == 1L) {
                    MessageBox mbox = new MessageBox(shell, 66);
                    mbox.setText(S.s(821));
                    mbox.setMessage(String.format("%s.\n%s.", new Object[]{S.s(217), S.s(652)}));
                    mbox.open();
                } else if (code == 2L) {
                    MessageBox mbox = new MessageBox(shell, 66);
                    mbox.setText(S.s(821));
                    mbox.setMessage(String.format("%s.\n%s.", new Object[]{S.s(218), S.s(652)}));
                    mbox.open();
                } else if (code == 3L) {
                    MessageBox mbox = new MessageBox(shell, 66);
                    mbox.setText(S.s(821));
                    mbox.setMessage(String.format("%s.\n%s.", new Object[]{S.s(216), S.s(652)}));
                    mbox.open();
                }
            }
        });
    }

    public IDocument getLogDocument() {
        return this.logDocument;
    }

    public void setStatusIndicator(IStatusIndicator statusIndicator) {
        this.statusIndicator = statusIndicator;
    }

    public IStatusIndicator getStatusIndicator() {
        return this.statusIndicator;
    }

    public RcpClientProperties getProperties() {
        return this.uiProperties;
    }

    public TextHistoryCollection getTextHistoryData() {
        if (this.textHistoryData == null) {
            this.textHistoryData = this.uiProperties.getTextHistoryData();
        }
        return this.textHistoryData;
    }

    private void saveTextHistoryData() {
        if (this.textHistoryData != null) {
            this.uiProperties.setTextHistoryData(this.textHistoryData);
        }
    }

    public void clearRecentlyOpenedFiles() {
        if (this.recentFiles == null) {
            this.recentFiles = this.uiProperties.getRecentlyOpenedFiles();
        }
        this.recentFiles.clear();
    }

    public List<String> getRecentlyOpenedFiles() {
        if (this.recentFiles == null) {
            this.recentFiles = this.uiProperties.getRecentlyOpenedFiles();
        }
        return this.recentFiles;
    }

    private void saveRecentlyOpenedFiles() {
        if (this.recentFiles != null) {
            this.uiProperties.setRecentlyOpenedFiles(this.recentFiles);
        }
    }

    public void addRecentlyOpenedFile(String path) {
        List<String> currentList = getRecentlyOpenedFiles();


        int i = 0;
        for (String elt : currentList) {
            if (elt.equals(path)) {
                currentList.remove(i);
                break;
            }
            i++;
        }


        currentList.add(0, path);
        while (currentList.size() > 10) {
            currentList.remove(10);
        }
    }

    public List<String> getRecentlyExecutedScripts() {
        if (this.recentScripts == null) {
            this.recentScripts = this.uiProperties.getRecentlyExecutedScripts();
        }
        return this.recentScripts;
    }

    private void saveRecentlyExecutedScripts() {
        if (this.recentScripts != null) {
            this.uiProperties.setRecentlyExecutedScripts(this.recentScripts);
        }
    }

    public void addRecentlyExecutedScript(String path) {
        List<String> currentList = getRecentlyExecutedScripts();


        int i = 0;
        for (String elt : currentList) {
            if (elt.equals(path)) {
                currentList.remove(i);
                break;
            }
            i++;
        }


        currentList.add(0, path);
        while (currentList.size() > 10) {
            currentList.remove(10);
        }
    }

    public WidgetBoundsManager getWidgetBoundsManager() {
        if (this.widgetBoundsManager == null) {
            this.widgetBoundsManager = this.uiProperties.getWidgetBoundsManager();
        }
        return this.widgetBoundsManager;
    }

    private void saveWidgetBoundsManager() {
        this.uiProperties.setWidgetBoundsManager(getWidgetBoundsManager());
    }

    public void setRecordedBounds(int widgetId, Rectangle bounds) {
        getWidgetBoundsManager().setRecordedBounds(widgetId, bounds);
    }

    public Rectangle getRecordedBounds(int widgetId) {
        return getWidgetBoundsManager().getRecordedBounds(widgetId);
    }

    public void clearWidgetBounds() {
        getWidgetBoundsManager().clearBounds();
    }

    public StateDataProvider getWidgetPersistenceProvider() {
        if (this.widgetPersistenceProvider == null) {
            this.widgetPersistenceProvider = this.uiProperties.getWidgetPersistenceProvider();
        }
        return this.widgetPersistenceProvider;
    }

    public void saveWidgetPersistenceProvider() {
        this.uiProperties.setWidgetPersistenceProvider(getWidgetPersistenceProvider());
    }

    public void clearWidgetPersistenceProvider() {
        getWidgetPersistenceProvider().clear();
    }

    public WidgetWrapper getWidgetWrapper() {
        if (this.widgetWrapper == null) {
            this.widgetWrapper = new WidgetWrapper(getWidgetPersistenceProvider());
        }
        return this.widgetWrapper;
    }

    public void wrapWidget(Control ctl, String widgetName) {
        UIUtil.setWidgetName(ctl, widgetName);
        getWidgetWrapper().wrap(ctl);
    }

    public static void wrapWidget(RcpClientContext ctx, Control ctl, String widgetName) {
        if (ctx != null) {
            ctx.wrapWidget(ctl, widgetName);
        }
    }

    public StateDataProvider getDialogPersistenceDataProvider() {
        if (this.dialogPersistenceProvider == null) {
            this.dialogPersistenceProvider = this.uiProperties.getDialogPersistenceDataProvider();
        }
        return this.dialogPersistenceProvider;
    }

    public void saveDialogPersistenceDataProvider() {
        this.uiProperties.setDialogPersistenceDataProvider(getDialogPersistenceDataProvider());
    }

    public void clearDialogPersistenceDataProvider() {
        getDialogPersistenceDataProvider().clear();
    }

    public FontManager getFontManager() {
        if (this.fontManager == null) {
            this.fontManager = new FontManager(getPropertyManager());
        }
        return this.fontManager;
    }

    public StyleManager getStyleManager() {
        if (this.styleManager == null) {
            this.styleManager = loadStyleManager();
        }
        return this.styleManager;
    }

    private StyleManager loadStyleManager() {
        StyleManager styleman = null;
        try {
            String smData = getPropertyManager().getString(".ui.ItemStyles");
            if ((smData != null) && (!smData.isEmpty())) {
                styleman = new StyleManager(smData, getThemeManager());
            }
        } catch (Exception e) {
            logger.catching(e);
        }
        if (styleman == null) {
            styleman = new StyleManager("<default>", getThemeManager());
        }
        return styleman;
    }

    private void saveStyleManager(StyleManager styleman) {
        String s = styleman == null ? "" : styleman.encode();

        getPropertyManager().setString(".ui.ItemStyles", s);
    }

    public ThemeManager getThemeManager() {
        return ThemeManager.getInstance();
    }

    private void initTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        String themeId = getPropertyManager().getString(".ui.Theme");
        themeManager.setActiveTheme(themeId);
    }

    private void saveTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        String themeId = themeManager.getActiveTheme();
        if (themeId.equals("theme.standard")) {
            getPropertyManager().setString(".ui.Theme", null);
        } else {
            getPropertyManager().setString(".ui.Theme", themeId);
        }
    }


    public void setAssociatedData(Object objectKey, Object valueKey, Object valueData) {
        Map<Object, Object> map = (Map) this.assoData.get(objectKey);
        if (map == null) {
            map = new WeakHashMap();
            this.assoData.put(objectKey, map);
        }

        map.put(valueKey, valueData);
    }


    public Object getAssociatedData(Object objectKey, Object valueKey) {
        Map<Object, Object> map = (Map) this.assoData.get(objectKey);
        if (map == null) {
            return null;
        }

        return map.get(valueKey);
    }

    public UIState getUIState(IUnit unit) {
        UIState r = (UIState) getAssociatedData(unit, "uiState");
        if (r == null) {
            r = new UIState(unit);
            setAssociatedData(unit, "uiState", r);
        }
        return r;
    }


    public UIState getUIGroupState(IUnit unit) {
        IUnit masterUnit = findMasterUnit(unit);

        UIState r = (UIState) getAssociatedData(masterUnit, "uiGroupState");
        if (r == null) {
            r = new UIState(masterUnit);
            setAssociatedData(masterUnit, "uiGroupState", r);
        }
        return r;
    }


    private static IUnit findMasterUnit(IUnit unit) {
        if ((unit.getParent() instanceof IDecompilerUnit)) {
            unit = (IUnit) unit.getParent();
        }
        if (((unit instanceof IDecompilerUnit)) || ((unit instanceof IDebuggerUnit))) {
            unit = (IUnit) unit.getParent();
        }
        return unit;
    }

    public boolean executeScript(Shell shell, String path) {
        getTelemetry().record("handlerExecuteClientScript");


        if (shell != null) {
            Display display = shell.getDisplay();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    break;
                }
            }
        }
        try {
            String libDir = getPropertyManager().getString(".ScriptsFolder");
            if (libDir == null) {
                logger.error("The .ScriptsFolder property yielded a null result", new Object[0]);
                return false;
            }
            ScriptLoader sl = new ScriptLoader(path, libDir);
            PublicContext context = new PublicContext(this);
            sl.execute(context);
        } catch (ScriptPreparationException e) {
            if (isDevelopmentMode()) {
                logger.catching(e);
            }
            logger.error("Script preparation error: %s", new Object[]{e.getMessage()});
            return false;
        } catch (ScriptInitializationException e) {
            if (isDevelopmentMode()) {
                logger.catching(e);
            }
            logger.error("Script initialization error: %s", new Object[]{e.getMessage()});
            return false;
        } catch (ScriptExecutionException e) {
            if (isDevelopmentMode()) {
                logger.catching(e);
            }
            logger.error("Script execution error: %s", new Object[]{e.getMessage()});
            return false;
        } catch (ScriptException e) {
            logger.error("An exception was thrown when executing the script:", new Object[0]);
            if (isDevelopmentMode()) {
                logger.catching(e);
            } else {
                logger.error(Throwables.formatStacktraceShort(e), new Object[0]);
            }
            if (e.getCause() != null) {
                getErrorHandler().processThrowableSilent(e.getCause());
            }
            return false;
        }

        addRecentlyExecutedScript(path);
        setLastExecutedScript(path);
        return true;
    }

    public void setLastExecutedScript(String path) {
        this.lastScript = path;
    }

    public String getLastExecutedScript() {
        return this.lastScript;
    }

    private void saveAppLayoutInformation() {
        if (this.mainShell != null) {
            String s;
            if (this.mainShell.getMaximized()) {
                s = "-1";
            } else {
                Rectangle bounds = this.mainShell.getBounds();
                s = String.format("%d,%d,%d,%d", bounds.x, bounds.y, bounds.width, bounds.height);
            }
            getPropertyManager().setString("state.MainShellBounds", s);
        }

        int ratio = this.app.folderProject.getPanelShare();
        getPropertyManager().setInteger("state.ProjectFolderRatio", ratio);

        ratio = this.app.folderConsoles.getPanelShare();
        getPropertyManager().setInteger("state.ConsolesFolderRatio", ratio);
    }

    private void removeAppLayoutInformation() {
        getPropertyManager().setString("state.MainShellBounds", "");
        getPropertyManager().setInteger("state.ProjectFolderRatio", 0);
        getPropertyManager().setInteger("state.ConsolesFolderRatio", 0);
    }

    public void setShouldShowDialog(String widgetName, boolean enabled) {
        String encodedState = getDialogPersistenceDataProvider().load(widgetName);
        Map<String, String> state = Strings.decodeMap(encodedState);
        state.put("doNotShow", Boolean.toString(!enabled));

        getDialogPersistenceDataProvider().save(widgetName, Strings.encodeMap(state));
    }

    public boolean getShouldShowDialog(String widgetName) {
        String encodedState = getDialogPersistenceDataProvider().load(widgetName);
        Map<String, String> state = Strings.decodeMap(encodedState);
        boolean doNotShow = BooleanUtils.toBoolean((String) state.get("doNotShow"));
        return !doNotShow;
    }

    public void setDefaultPathForDialog(String widgetName, String path) {
        String encodedState = getDialogPersistenceDataProvider().load(widgetName);
        Map<String, String> state = Strings.decodeMap(encodedState);
        state.put("defaultPath", path);

        getDialogPersistenceDataProvider().save(widgetName, Strings.encodeMap(state));
    }

    public String getDefaultPathForDialog(String widgetName) {
        String encodedState = getDialogPersistenceDataProvider().load(widgetName);
        Map<String, String> state = Strings.decodeMap(encodedState);
        return state.get("defaultPath");
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }


    public static TextHistory getStandardAddressHistory(RcpClientContext context) {
        if (context == null) {
            return null;
        }
        return context.getTextHistoryData().get("addressHistory");
    }

    public static TextHistory getStandardRenamingHistory(RcpClientContext context) {
        if (context == null) {
            return null;
        }
        return context.getTextHistoryData().get("renamingHistory");
    }

    public static TextHistory getStandardFindTextHistory(RcpClientContext context) {
        if (context == null) {
            return null;
        }
        return context.getTextHistoryData().get("findTextHistory");
    }

    public static int clientNotificationLevelToLoggerLevel(ClientNotificationLevel level) {
        switch (level) {
            case ERROR:
                return 50;
            case WARNING:
                return 40;
        }
        return 30;
    }
}


