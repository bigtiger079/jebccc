
package com.pnfsoftware.jeb.rcpclient.parts.units;


import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.input.InputHelper;
import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
import com.pnfsoftware.jeb.core.output.IItem;
import com.pnfsoftware.jeb.core.output.text.impl.HexDumpDocument;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.extensions.controls.PrimitiveDisplayView;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.IOException;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;


public class BinaryDataView
        extends AbstractUnitFragment<IUnit> {
    private static final ILogger logger = GlobalLog.getLogger(BinaryDataView.class);

    private InteractiveTextView text;
    private PrimitiveDisplayView helper;
    private ILocationListener locationListener;


    public BinaryDataView(Composite parent, int flags, RcpClientContext context, IUnit unit, IRcpUnitView unintView, final HexDumpDocument idoc) {

        super(parent, flags, unit, unintView, context);

        setLayout(new FillLayout());


        SashForm container = new SashForm(this, 256);


        this.text = new InteractiveTextView(container, 0, context, unit, this.unitView, idoc);


        this.helper = new PrimitiveDisplayView(container, 0);

        container.setWeights(new int[]{65, 35});


        this.text.addLocationListener(this.locationListener = new ILocationListener() {

            public void locationChanged(String address) {

                if ((address == null) || (address.isEmpty()) || (address.charAt(0) != '@')) {

                    BinaryDataView.this.helper.setBytes(null);

                    return;

                }


                long offset = Conversion.stringToLong(address.substring(1), -1L);

                if (offset < 0L) {

                    BinaryDataView.this.helper.setBytes(null);

                    return;

                }


                byte[] data = new byte[32];

                try {

                    int len = InputHelper.readBytes(idoc.getInput(), offset, data, 0, data.length);

                    BinaryDataView.this.helper.setBytes(data, 0, len);


                } catch (IOException localIOException) {
                }

            }


        });

        addDisposeListener(new DisposeListener() {

            public void widgetDisposed(DisposeEvent e) {

                BinaryDataView.this.text.removeLocationListener(BinaryDataView.this.locationListener);

            }

        });

    }


    public InteractiveTextView getInteractiveText() {

        return this.text;

    }


    public PrimitiveDisplayView getHelper() {

        return this.helper;

    }


    public boolean verifyOperation(OperationRequest req) {

        return this.text.verifyOperation(req);

    }


    public boolean doOperation(OperationRequest req) {

        return this.text.doOperation(req);

    }


    public boolean isActiveItem(IItem item) {

        return this.text.isActiveItem(item);

    }


    public IItem getActiveItem() {

        return this.text.getActiveItem();

    }


    public String getActiveAddress(AddressConversionPrecision precision) {

        return this.text.getActiveAddress();

    }


    public boolean isValidActiveAddress(String address, Object object) {

        return this.text.isValidActiveAddress(address, object);

    }


    public boolean setActiveAddress(String address, Object extra, boolean record) {

        return this.text.setActiveAddress(address, extra, record);

    }


    public byte[] export() {

        return this.text.export();

    }


    public AbstractUnitFragment.FragmentType getFragmentType() {

        return this.text.getFragmentType();

    }


    public String getExportExtension() {

        return this.text.getExportExtension();

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\BinaryDataView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */