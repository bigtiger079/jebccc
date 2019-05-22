package com.pnfsoftware.jeb.rcpclient.actions;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.client.api.IUnitFragment;
import com.pnfsoftware.jeb.core.actions.ActionCommentData;
import com.pnfsoftware.jeb.core.actions.ActionContext;
import com.pnfsoftware.jeb.core.actions.ActionConvertData;
import com.pnfsoftware.jeb.core.actions.ActionCreatePackageData;
import com.pnfsoftware.jeb.core.actions.ActionDeleteData;
import com.pnfsoftware.jeb.core.actions.ActionMoveToPackageData;
import com.pnfsoftware.jeb.core.actions.ActionOverridesData;
import com.pnfsoftware.jeb.core.actions.ActionRenameData;
import com.pnfsoftware.jeb.core.actions.ActionReplaceData;
import com.pnfsoftware.jeb.core.actions.ActionTypeHierarchyData;
import com.pnfsoftware.jeb.core.actions.ActionXrefsData;
import com.pnfsoftware.jeb.core.output.text.ICoordinates;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.ICodeUnit;
import com.pnfsoftware.jeb.core.units.code.ISourceUnit;
import com.pnfsoftware.jeb.core.units.code.asm.type.CodeConstant;
import com.pnfsoftware.jeb.core.util.DecompilerHelper;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.CodeHierarchyDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.CommentDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.CreatePackageDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.MoveToPackageDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.ReferencesDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.RenameItemDialog;
import com.pnfsoftware.jeb.rcpclient.dialogs.nativecode.NamedConstantsChooserDialog;
import com.pnfsoftware.jeb.rcpclient.handlers.nativeactions.NativeActionUtil;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.IRcpUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

public class GraphicalActionExecutor {
    private static final ILogger logger = GlobalLog.getLogger(GraphicalActionExecutor.class);
    private Shell shell;
    private RcpClientContext context;

    public GraphicalActionExecutor(Shell shell, RcpClientContext context) {
        this.shell = shell;
        this.context = context;
    }

    public boolean execute(ActionUIContext uictx) {
        return execute(uictx, null);
    }

    public boolean execute(ActionUIContext uictx, Object value) {
        logger.info("Executing: %s", uictx);
        ActionContext info = uictx.getActionContext();
        switch (info.getActionId()) {
            case 2: {
                ActionRenameData data = new ActionRenameData();
                if (info.getUnit().prepareExecution(info, data)) {
                    String newName = null;
                    if (value == null) {
                        RenameItemDialog dlg = new RenameItemDialog(this.shell, RcpClientContext.getStandardRenamingHistory(this.context));
                        dlg.setDescription(data.getDescription());
                        dlg.setInitialValue(data.getCurrentName());
                        dlg.setOriginalValue(data.getOriginalName());
                        newName = dlg.open();
                    } else {
                        newName = value.toString();
                    }
                    if (newName != null) {
                        data.setNewName(newName);
                        return info.getUnit().executeAction(info, data);
                    }
                }
                break;
            }
            case 3: {
                ActionCommentData data = new ActionCommentData();
                if (info.getUnit().prepareExecution(info, data)) {
                    String address = info.getAddress();
                    if (address != null) {
                        String newComment = null;
                        if (value == null) {
                            CommentDialog dlg = new CommentDialog(this.shell, address);
                            dlg.setDescription(data.getDescription());
                            dlg.setInitialComment(data.getComment());
                            newComment = dlg.open();
                        } else {
                            newComment = value.toString();
                        }
                        if (newComment != null) {
                            data.setNewComment(newComment);
                            return info.getUnit().executeAction(info, data);
                        }
                    }
                }
                break;
            }
            case 10: {
                ActionCreatePackageData data = new ActionCreatePackageData();
                if (info.getUnit().prepareExecution(info, data)) {
                    String fqname = null;
                    if (value == null) {
                        CreatePackageDialog dlg = new CreatePackageDialog(this.shell, RcpClientContext.getStandardRenamingHistory(this.context));
                        dlg.setDescription(data.getDescription());
                        dlg.setInitialValue(data.getCurrentPackageFqname());
                        fqname = dlg.open();
                    } else {
                        fqname = value.toString();
                    }
                    if (fqname != null) {
                        data.setFqname(fqname);
                        return info.getUnit().executeAction(info, data);
                    }
                }
                break;
            }
            case 11: {
                ActionMoveToPackageData data = new ActionMoveToPackageData();
                if (info.getUnit().prepareExecution(info, data)) {
                    String fqname = null;
                    if (value == null) {
                        MoveToPackageDialog dlg = new MoveToPackageDialog(this.shell, RcpClientContext.getStandardRenamingHistory(this.context));
                        dlg.setDescription(data.getDescription());
                        dlg.setInitialValue(data.getCurrentPackageFqname());
                        fqname = dlg.open();
                    } else {
                        fqname = value.toString();
                    }
                    if (fqname != null) {
                        data.setDstPackageFqname(fqname);
                        return info.getUnit().executeAction(info, data);
                    }
                }
                break;
            }
            case 4: {
                ActionXrefsData data = new ActionXrefsData();
                if (info.getUnit().prepareExecution(info, data)) {
                    String caption = S.s(45);
                    if (data.getTarget() != null) {
                        caption = caption + " to " + data.getTarget();
                    }
                    ReferencesDialog dlg = new ReferencesDialog(this.shell, caption, data.getAddresses(), data.getDetails(), info.getUnit());
                    int index = dlg.open();
                    if (index >= 0) {
                        String address = data.getAddresses().get(index);
                        gotoAddress(this.context, info.getUnit(), address);
                    }
                    return info.getUnit().executeAction(info, data);
                }
                IUnitFragment fragment = uictx.getFragment();
                if ((fragment instanceof InteractiveTextView)) {
                    long itemId = info.getItemId();
                    List<ICoordinates> coords = ((InteractiveTextView) fragment).collectItemCoordinates(itemId);
                    if (!coords.isEmpty()) {
                        List<String> addresses = new ArrayList<>();
                        for (ICoordinates coord : coords) {
                            addresses.add(String.format("Text @ %d:%d", 1 + coord.getLineDelta(), 1 + coord.getColumnOffset()));
                        }
                        ReferencesDialog dlg = new ReferencesDialog(this.shell, S.s(47), addresses, null, info.getUnit());
                        int index = dlg.open();
                        if (index >= 0) {
                            ICoordinates coord = coords.get(index);
                            ((InteractiveTextView) fragment).setCaretCoordinates(coord);
                        }
                    }
                }
                break;
            }
            case 12: {
                ActionTypeHierarchyData data = new ActionTypeHierarchyData();
                if (info.getUnit().prepareExecution(info, data)) {
                    ICodeUnit codeUnit = null;
                    IUnit unit = info.getUnit();
                    if ((unit instanceof ICodeUnit)) {
                        codeUnit = (ICodeUnit) unit;
                    }
                    if ((unit instanceof ISourceUnit)) {
                        codeUnit = DecompilerHelper.getRelatedCodeUnit(unit);
                    }
                    if (codeUnit != null) {
                        CodeHierarchyDialog dlg = new CodeHierarchyDialog(this.shell, codeUnit, data.getBaseNode(), data.getBaseNodeForAscendingHierarchy(), this.context);
                        String selectedAddress = dlg.open();
                        gotoAddress(this.context, info.getUnit(), selectedAddress);
                        return info.getUnit().executeAction(info, data);
                    }
                }
                break;
            }
            case 13: {
                ActionOverridesData data = new ActionOverridesData();
                if (info.getUnit().prepareExecution(info, data)) {
                    ReferencesDialog dlg = new ReferencesDialog(this.shell, S.s(534), data.getAddresses(), null, info.getUnit());
                    int index = dlg.open();
                    if (index >= 0) {
                        String address = data.getAddresses().get(index);
                        gotoAddress(this.context, info.getUnit(), address);
                    }
                    return info.getUnit().executeAction(info, data);
                }
                break;
            }
            case 1: {
                ActionDeleteData data = new ActionDeleteData();
                if (info.getUnit().prepareExecution(info, data)) {
                    return info.getUnit().executeAction(info, data);
                }
                break;
            }
            case 5: {
                ActionConvertData data = new ActionConvertData();
                if (info.getUnit().prepareExecution(info, data)) {
                    return info.getUnit().executeAction(info, data);
                }
                break;
            }
            case 6: {
                INativeCodeUnit<?> pbcu = NativeActionUtil.getRelatedNativeCodeUnit(info.getUnit());
                if (pbcu == null) {
                    return false;
                }
                ActionReplaceData data = new ActionReplaceData();
                if (info.getUnit().prepareExecution(info, data)) {
                    Object o = data.getTargetObject();
                    if (o == null) {
                        return false;
                    }
                    NamedConstantsChooserDialog dlg = new NamedConstantsChooserDialog(this.shell, pbcu, o);
                    CodeConstant cst = dlg.open();
                    if (cst == null) {
                        return false;
                    }
                    data.setWantedReplacement(cst);
                    return info.getUnit().executeAction(info, data);
                }
                break;
            }
            case 7:
            case 8:
            case 9:
            default:
                logger.debug("The action (%d) is not supported by this client", info.getActionId());
        }
        return false;
    }

    public static int gotoAddress(RcpClientContext context, IUnit unit, String selectedAddress) {
        PartManager pman = context.getPartManager();
        if ((pman == null) || (unit == null)) {
            throw new IllegalArgumentException();
        }
        if (selectedAddress == null) {
            return 0;
        }
        int cnt = 0;
        List<UnitPartManager> objects = pman.getPartManagersForUnit(unit);
        if ((objects.isEmpty()) && (unit.isProcessed())) {
            pman.create(unit, true);
            objects = pman.getPartManagersForUnit(unit);
        }
        UnitPartManager object;
        IRcpUnitFragment activeFragment;
        for (UnitPartManager object1 : objects) {
            object = object1;
            activeFragment = object.getActiveFragment();
            if ((activeFragment != null) && (activeFragment.setActiveAddress(selectedAddress, null, true))) {
                cnt++;
                break;
            }
            for (IRcpUnitFragment fragment : object.getPreviouslyActiveFragments())
                if (fragment != activeFragment) {
                    if (fragment.setActiveAddress(selectedAddress, null, true)) {
                        object.setActiveFragment(fragment);
                        cnt++;
                        break;
                    }
                }
        }
        return cnt;
    }
}
