/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units;
/*     */
/*     */

import com.pnfsoftware.jeb.client.api.OperationRequest;
/*     */ import com.pnfsoftware.jeb.core.input.InputHelper;
/*     */ import com.pnfsoftware.jeb.core.output.AddressConversionPrecision;
/*     */ import com.pnfsoftware.jeb.core.output.IItem;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.HexDumpDocument;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.controls.PrimitiveDisplayView;
/*     */ import com.pnfsoftware.jeb.util.encoding.Conversion;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.io.IOException;
/*     */ import org.eclipse.swt.custom.SashForm;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;

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
/*     */ public class BinaryDataView
        /*     */ extends AbstractUnitFragment<IUnit>
        /*     */ {
    /*  38 */   private static final ILogger logger = GlobalLog.getLogger(BinaryDataView.class);
    /*     */
    /*     */   private InteractiveTextView text;
    /*     */   private PrimitiveDisplayView helper;
    /*     */   private ILocationListener locationListener;

    /*     */
    /*     */
    public BinaryDataView(Composite parent, int flags, RcpClientContext context, IUnit unit, IRcpUnitView unintView, final HexDumpDocument idoc)
    /*     */ {
        /*  46 */
        super(parent, flags, unit, unintView, context);
        /*  47 */
        setLayout(new FillLayout());
        /*     */
        /*  49 */
        SashForm container = new SashForm(this, 256);
        /*     */
        /*  51 */
        this.text = new InteractiveTextView(container, 0, context, unit, this.unitView, idoc);
        /*     */
        /*  53 */
        this.helper = new PrimitiveDisplayView(container, 0);
        /*  54 */
        container.setWeights(new int[]{65, 35});
        /*     */
        /*  56 */
        this.text.addLocationListener(this. = new ILocationListener()
                /*     */ {
            /*     */
            public void locationChanged(String address) {
                /*  59 */
                if ((address == null) || (address.isEmpty()) || (address.charAt(0) != '@')) {
                    /*  60 */
                    BinaryDataView.this.helper.setBytes(null);
                    /*  61 */
                    return;
                    /*     */
                }
                /*     */
                /*     */
                /*  65 */
                long offset = Conversion.stringToLong(address.substring(1), -1L);
                /*  66 */
                if (offset < 0L) {
                    /*  67 */
                    BinaryDataView.this.helper.setBytes(null);
                    /*  68 */
                    return;
                    /*     */
                }
                /*     */
                /*  71 */
                byte[] data = new byte[32];
                /*     */
                try {
                    /*  73 */
                    int len = InputHelper.readBytes(idoc.getInput(), offset, data, 0, data.length);
                    /*  74 */
                    BinaryDataView.this.helper.setBytes(data, 0, len);
                    /*     */
                    /*     */
                }
                /*     */ catch (IOException localIOException) {
                }
                /*     */
            }
            /*     */
            /*  80 */
        });
        /*  81 */
        addDisposeListener(new DisposeListener()
                /*     */ {
            /*     */
            public void widgetDisposed(DisposeEvent e) {
                /*  84 */
                BinaryDataView.this.text.removeLocationListener(BinaryDataView.this.locationListener);
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    public InteractiveTextView getInteractiveText() {
        /*  90 */
        return this.text;
        /*     */
    }

    /*     */
    /*     */
    public PrimitiveDisplayView getHelper() {
        /*  94 */
        return this.helper;
        /*     */
    }

    /*     */
    /*     */
    public boolean verifyOperation(OperationRequest req)
    /*     */ {
        /*  99 */
        return this.text.verifyOperation(req);
        /*     */
    }

    /*     */
    /*     */
    public boolean doOperation(OperationRequest req)
    /*     */ {
        /* 104 */
        return this.text.doOperation(req);
        /*     */
    }

    /*     */
    /*     */
    public boolean isActiveItem(IItem item)
    /*     */ {
        /* 109 */
        return this.text.isActiveItem(item);
        /*     */
    }

    /*     */
    /*     */
    public IItem getActiveItem()
    /*     */ {
        /* 114 */
        return this.text.getActiveItem();
        /*     */
    }

    /*     */
    /*     */
    public String getActiveAddress(AddressConversionPrecision precision)
    /*     */ {
        /* 119 */
        return this.text.getActiveAddress();
        /*     */
    }

    /*     */
    /*     */
    public boolean isValidActiveAddress(String address, Object object)
    /*     */ {
        /* 124 */
        return this.text.isValidActiveAddress(address, object);
        /*     */
    }

    /*     */
    /*     */
    public boolean setActiveAddress(String address, Object extra, boolean record)
    /*     */ {
        /* 129 */
        return this.text.setActiveAddress(address, extra, record);
        /*     */
    }

    /*     */
    /*     */
    public byte[] export()
    /*     */ {
        /* 134 */
        return this.text.export();
        /*     */
    }

    /*     */
    /*     */
    public AbstractUnitFragment.FragmentType getFragmentType()
    /*     */ {
        /* 139 */
        return this.text.getFragmentType();
        /*     */
    }

    /*     */
    /*     */
    public String getExportExtension()
    /*     */ {
        /* 144 */
        return this.text.getExportExtension();
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\BinaryDataView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */