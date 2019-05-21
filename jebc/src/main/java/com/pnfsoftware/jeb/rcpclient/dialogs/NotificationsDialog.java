package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.IUnitNotification;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

public class NotificationsDialog extends DataFrameDialog {
    public NotificationsDialog(Shell parent, List<? extends IUnitNotification> anomalies) {
        super(parent, S.s(602), true, "notificationsDialog");
        DataFrame df = new DataFrame(S.s(779), S.s(268), S.s(52));
        for (IUnitNotification a : anomalies) {
            df.addRow(a.getType(), a.getDescription(), a.getAddress());
        }
        setDataFrame(df);
        setDisplayIndex(true);
    }
}