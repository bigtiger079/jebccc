package com.pnfsoftware.jeb.rcpclient.dialogs;

import com.pnfsoftware.jeb.client.S;
import com.pnfsoftware.jeb.core.units.IInteractiveUnit;
import com.pnfsoftware.jeb.core.units.INativeCodeUnit;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;


public class ReferencesDialog
        extends DataFrameDialog {
    private static final ILogger logger = GlobalLog.getLogger(ReferencesDialog.class);


    public ReferencesDialog(Shell parent, String caption, List<String> addresses, List<String> details, IUnit unit) {
        super(parent, caption, true, "referencesDialog");

        if (addresses == null) {
            logger.i("The list of addresses is null", new Object[0]);
            addresses = new ArrayList();
        }
        if ((details != null) && (details.size() != addresses.size()))
            throw new IllegalArgumentException();
        IInteractiveUnit iunit;
        DataFrame df;
        int i;
        if ((unit instanceof IInteractiveUnit)) {
            iunit = (IInteractiveUnit) unit;
            df = new DataFrame(new String[]{S.s(52), S.s(424), details != null ? S.s(270) : S.s(203)});
            df.setRenderedBaseForNumberObjects(16);
            i = 0;
            for (String address : addresses) {
                Object label = iunit.getAddressLabel(address);
                if ((label == null) && ((unit instanceof INativeCodeUnit))) {
                    label = Long.valueOf(((INativeCodeUnit) iunit).getCanonicalMemoryAddress(address));
                }
                String extra;
                if ((details == null) || (details.get(i) == null)) {
                    extra = iunit.getComment(address);
                } else {
                    extra = (String) details.get(i);
                }

                df.addRow(new Object[]{address, label, extra});
                i++;
            }
        } else {
            df = new DataFrame(new String[]{S.s(52)});
            for (String address : addresses) {
                df.addRow(new Object[]{address});
            }
        }

        setDataFrame(df);
        setDisplayIndex(true);
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\dialogs\ReferencesDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */