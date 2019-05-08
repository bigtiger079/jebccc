package com.pnfsoftware.jeb.rcpclient.util;

import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerTargetInformation;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.core.units.code.debug.ITypedValue;
import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValueComposite;
import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValueNumber;
import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValuePrimitive;
import com.pnfsoftware.jeb.core.units.code.debug.impl.DebuggerUtil;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueArray;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueDouble;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueFloat;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueObject;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueRaw;
import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueString;
import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
import com.pnfsoftware.jeb.util.format.Formatter;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.io.Endianness;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class DbgTypedValueUtil {
    public static String formatAddress(long address, IDebuggerUnit unit) {
        if (unit.getTargetInformation().getProcessorType().is64Bit()) {
            return Strings.f("%08X'%08X", new Object[]{Integer.valueOf((int) (address >> 32)), Integer.valueOf((int) address)});
        }
        return Strings.f("%08X", new Object[]{Integer.valueOf((int) address)});
    }

    public static long bytesToAddress(byte[] data, IDebuggerUnit unit) {
        ByteOrder targetByteOrder = unit.getTargetInformation().getEndianness().toByteOrder();
        ByteBuffer b = ByteBuffer.wrap(data).order(targetByteOrder);
        if (unit.getTargetInformation().getProcessorType().is64Bit()) {
            if (data.length >= 8) {
                return b.getLong();
            }
        } else if (data.length >= 4) {
            return b.getInt();
        }
        return 0L;
    }

    public static String formatValue(ITypedValue value, int index, IDebuggerUnit unit) {
        if (((value instanceof ValueDouble)) || ((value instanceof ValueFloat))) {
            if (index == 0) {
                Number v = ((AbstractValueNumber) value).getValue();
                return Strings.f("%f", new Object[]{Double.valueOf(v.doubleValue())});
            }
        } else if ((value instanceof AbstractValueNumber)) {
            Number v = ((AbstractValueNumber) value).getValue();
            if (index == 0) {
                return Strings.f("%d", new Object[]{Long.valueOf(v.longValue())});
            }
            if (index == 1) {
                return Strings.f("%Xh", new Object[]{Long.valueOf(v.longValue())});
            }
        } else if ((value instanceof ValueString)) {
            if (index == 0) {
                String v = ((ValueString) value).getValue();
                return Strings.f("%s", new Object[]{v});
            }
        } else if ((value instanceof ValueObject)) {
            if (index == 0) {
                long objectId = ((ValueObject) value).getObjectId();
                if (objectId == 0L) {
                    return "null";
                }
                return Strings.f("id=%d", new Object[]{Long.valueOf(objectId)});
            }
        } else if ((value instanceof ValueArray)) {
            if (index == 0) {
                long objectId = ((ValueArray) value).getObjectId();
                if (objectId == 0L) {
                    return "null";
                }
                return Strings.f("id=%d", new Object[]{Long.valueOf(objectId)});
            }
        } else if ((value instanceof ValueRaw)) {
            byte[] v = ((ValueRaw) value).getValue();
            ByteOrder targetByteOrder = unit.getTargetInformation().getEndianness().toByteOrder();
            ByteBuffer b = ByteBuffer.wrap(v).order(targetByteOrder);
            switch (v.length) {
                case 1:
                    if (index == 0) {
                        return Strings.f("%02Xh", new Object[]{Byte.valueOf(b.get())});
                    }
                    break;
                case 2:
                    if (index == 0) {
                        return Strings.f("%04Xh", new Object[]{Short.valueOf(b.getShort())});
                    }
                    break;
                case 4:
                    if (index == 0) {
                        return Strings.f("%08Xh", new Object[]{Integer.valueOf(b.getInt())});
                    }
                    if (index == 1) {
                        return getMemory(unit, b.getInt() & 0xFFFFFFFF, 4);
                    }
                    break;
                case 8:
                    if (index == 0) {
                        return Strings.f("%016Xh", new Object[]{Long.valueOf(b.getLong())});
                    }
                    if (index == 1) {
                        return getMemory(unit, b.getLong(), 8);
                    }
                    break;
                case 3:
                case 5:
                case 6:
                case 7:
                default:
                    if (index == 0) {
                        return Strings.f("b'%s", new Object[]{Formatter.byteArrayToHexString(v)});
                    }
                    break;
            }
        } else {
            return value.toString();
        }
        return null;
    }

    private static String getMemory(IDebuggerUnit unit, long address, int size) {
        byte[] mem = DebuggerUtil.readMemoryStringSafe(unit, address, size);
        if (mem == null) {
            return null;
        }
        int asciiLength = Strings.getAsciiLength(mem);
        if (asciiLength < size) {
            if (mem.length >= size) {
                return Strings.f("%sh", new Object[]{Formatter.byteArrayToHexString(mem, 0, size)});
            }
            return Strings.f("b'%s", new Object[]{Formatter.byteArrayToHexString(mem)});
        }
        return new String(mem, 0, asciiLength, Charset.defaultCharset());
    }

    public static ITypedValue buildValue(ITypedValue value, String newValue) {
        if (newValue == null) {
            return null;
        }
        if ((value instanceof AbstractValueComposite)) {
        }
        String type = value.getTypeName();
        newValue = newValue.trim();
        if (newValue.isEmpty()) {
            if ((type.equals("string")) || (type.equals("char"))) {
                return AbstractValuePrimitive.parseValue(type, newValue);
            }
            return null;
        }
        try {
            if (((value instanceof ValueRaw)) && (newValue.startsWith("b'"))) {
                return AbstractValuePrimitive.parseValue(type, newValue.substring(2));
            }
            return AbstractValuePrimitive.parseValue(type, newValue);
        } catch (NumberFormatException e) {
        }
        return null;
    }

    public static boolean equals(ITypedValue value, String newValue, IDebuggerUnit unit) {
        for (int i = 0; i < 2; i++) {
            String formatted = formatValue(value, i, unit);
            if ((formatted != null) && (formatted.equals(newValue))) {
                return true;
            }
        }
        return false;
    }
}


