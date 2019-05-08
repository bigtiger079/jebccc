package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.IAddressableUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitNotification;
import com.pnfsoftware.jeb.core.units.UnitUtil;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
import com.pnfsoftware.jeb.util.base.Couple;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

public class AllNotificationsDialog extends DataFrameDialog {
    private static AllNotificationsDialog instance;

    public static AllNotificationsDialog getInstance() {
        return instance;
    }

    public AllNotificationsDialog(Shell parent, List<Couple<IUnit, IUnitNotification>> elements) {
        super(parent, S.s(602), false, "allNotificationsDialog");
        DataFrame df = new DataFrame(new String[]{S.s(785), S.s(779), S.s(268), S.s(52), S.s(424)});
        for (Couple<IUnit, IUnitNotification> elt : elements) {
            IUnit unit = (IUnit) elt.getFirst();
            IUnitNotification not = (IUnitNotification) elt.getSecond();
            String path = UnitUtil.buildFullyQualifiedUnitPath(unit);
            String addr = not.getAddress();
            if ((unit instanceof IAddressableUnit)) {
                df.addRow(new Object[]{path, not.getType(), not.getDescription(), addr, addr == null ? "" : ((IAddressableUnit) unit).getAddressLabel(addr)});
            } else {
                df.addRow(new Object[]{path, not.getType(), not.getDescription(), addr, ""});
            }
        }
        setDataFrame(df);
        setDisplayIndex(false);
    }

    public Integer open() {
        instance = this;
        Integer r = super.open();
        instance = null;
        return r;
    }
}


