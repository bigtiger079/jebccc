
package com.pnfsoftware.jeb.rcpclient.extensions.controls;

import com.pnfsoftware.jeb.rcpclient.util.DataFrame;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class PrimitiveDisplayView
        extends Composite {
    private static final ILogger logger = GlobalLog.getLogger(PrimitiveDisplayView.class);
    private DataFrame df;
    private DataFrameView dfv;

    public PrimitiveDisplayView(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout());
        this.df = new DataFrame(new String[]{"Type", "Value", "Hex value"});
        this.dfv = new DataFrameView(this, this.df, false);
        this.dfv.addExtraEntriesToContextMenu();
    }

    public void setBytes(byte[] data) {
        setBytes(data, 0, data != null ? data.length : -1);
    }

    public void setBytes(byte[] data, int offset, int length) {
        this.df.clear();
        if ((data != null) && (offset >= 0) && (length > 0) && (offset + length <= data.length)) {
            ByteBuffer b = ByteBuffer.wrap(data, offset, length);
            if (b.limit() >= 2) {
                b.order(ByteOrder.LITTLE_ENDIAN);
                short v = b.getShort(0);
                this.df.addRow(new Object[]{"i16 LE", Short.toString(v), Integer.toHexString(v & 0xFFFF)});
                int u = v & 0xFFFF;
                this.df.addRow(new Object[]{"u16 LE", Integer.toString(u), Integer.toHexString(u & 0xFFFF)});
                b.order(ByteOrder.BIG_ENDIAN);
                v = b.getShort(0);
                this.df.addRow(new Object[]{"i16 BE", Short.toString(v), Integer.toHexString(v & 0xFFFF)});
                u = v & 0xFFFF;
                this.df.addRow(new Object[]{"i16 BE", Integer.toString(u), Integer.toHexString(u & 0xFFFF)});
            }
            if (b.limit() >= 4) {
                b.order(ByteOrder.LITTLE_ENDIAN);
                int v = b.getInt(0);
                this.df.addRow(new Object[]{"i32 LE", Integer.toString(v), Integer.toHexString(v)});
                b.order(ByteOrder.BIG_ENDIAN);
                v = b.getInt(0);
                this.df.addRow(new Object[]{"i32 BE", Integer.toString(v), Integer.toHexString(v)});
            }
            if (b.limit() >= 8) {
                b.order(ByteOrder.LITTLE_ENDIAN);
                long v = b.getLong(0);
                this.df.addRow(new Object[]{"i64 LE", Long.toString(v), Long.toHexString(v)});
                b.order(ByteOrder.BIG_ENDIAN);
                v = b.getLong(0);
                this.df.addRow(new Object[]{"i64 BE", Long.toString(v), Long.toHexString(v)});
            }
            if (b.limit() >= 4) {
                b.order(ByteOrder.LITTLE_ENDIAN);
                float v = b.getFloat(0);
                this.df.addRow(new Object[]{"f32 LE", Float.toString(v), Float.toHexString(v)});
                b.order(ByteOrder.BIG_ENDIAN);
                v = b.getFloat(0);
                this.df.addRow(new Object[]{"f32 BE", Float.toString(v), Float.toHexString(v)});
            }
            if (b.limit() >= 8) {
                b.order(ByteOrder.LITTLE_ENDIAN);
                double v = b.getDouble(0);
                this.df.addRow(new Object[]{"f64 LE", Double.toString(v), Double.toHexString(v)});
                b.order(ByteOrder.BIG_ENDIAN);
                v = b.getDouble(0);
                this.df.addRow(new Object[]{"f64 BE", Double.toString(v), Double.toHexString(v)});
            }
            try {
                String s = new String(data, offset, length, "UTF-8");
                this.df.addRow(new Object[]{"UTF-8", s});
            } catch (Exception e) {
                logger.catching(e);
            }
        }
        this.dfv.refresh();
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\controls\PrimitiveDisplayView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */