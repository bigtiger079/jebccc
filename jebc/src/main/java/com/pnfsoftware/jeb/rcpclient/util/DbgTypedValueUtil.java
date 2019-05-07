/*     */
package com.pnfsoftware.jeb.rcpclient.util;
/*     */
/*     */

import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerTargetInformation;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.ITypedValue;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValueComposite;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValueNumber;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.AbstractValuePrimitive;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.DebuggerUtil;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueArray;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueDouble;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueFloat;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueObject;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueRaw;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.impl.ValueString;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
/*     */ import com.pnfsoftware.jeb.util.format.Formatter;
/*     */ import com.pnfsoftware.jeb.util.format.Strings;
/*     */ import com.pnfsoftware.jeb.util.io.Endianness;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.charset.Charset;

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
/*     */ public class DbgTypedValueUtil
        /*     */ {
    /*     */
    public static String formatAddress(long address, IDebuggerUnit unit)
    /*     */ {
        /*  44 */
        if (unit.getTargetInformation().getProcessorType().is64Bit()) {
            /*  45 */
            return Strings.f("%08X'%08X", new Object[]{Integer.valueOf((int) (address >> 32)), Integer.valueOf((int) address)});
            /*     */
        }
        /*  47 */
        return Strings.f("%08X", new Object[]{Integer.valueOf((int) address)});
        /*     */
    }

    /*     */
    /*     */
    public static long bytesToAddress(byte[] data, IDebuggerUnit unit) {
        /*  51 */
        ByteOrder targetByteOrder = unit.getTargetInformation().getEndianness().toByteOrder();
        /*  52 */
        ByteBuffer b = ByteBuffer.wrap(data).order(targetByteOrder);
        /*     */
        /*     */
        /*  55 */
        if (unit.getTargetInformation().getProcessorType().is64Bit()) {
            /*  56 */
            if (data.length >= 8) {
                /*  57 */
                return b.getLong();
                /*     */
            }
            /*     */
            /*     */
            /*     */
        }
        /*  62 */
        else if (data.length >= 4) {
            /*  63 */
            return b.getInt();
            /*     */
        }
        /*     */
        /*     */
        /*  67 */
        return 0L;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static String formatValue(ITypedValue value, int index, IDebuggerUnit unit)
    /*     */ {
        /*  79 */
        if (((value instanceof ValueDouble)) || ((value instanceof ValueFloat))) {
            /*  80 */
            if (index == 0) {
                /*  81 */
                Number v = ((AbstractValueNumber) value).getValue();
                /*  82 */
                return Strings.f("%f", new Object[]{Double.valueOf(v.doubleValue())});
                /*     */
            }
            /*     */
        }
        /*  85 */
        else if ((value instanceof AbstractValueNumber)) {
            /*  86 */
            Number v = ((AbstractValueNumber) value).getValue();
            /*  87 */
            if (index == 0) {
                /*  88 */
                return Strings.f("%d", new Object[]{Long.valueOf(v.longValue())});
                /*     */
            }
            /*  90 */
            if (index == 1) {
                /*  91 */
                return Strings.f("%Xh", new Object[]{Long.valueOf(v.longValue())});
                /*     */
            }
            /*     */
        }
        /*  94 */
        else if ((value instanceof ValueString)) {
            /*  95 */
            if (index == 0) {
                /*  96 */
                String v = ((ValueString) value).getValue();
                /*  97 */
                return Strings.f("%s", new Object[]{v});
                /*     */
            }
            /*     */
        }
        /* 100 */
        else if ((value instanceof ValueObject)) {
            /* 101 */
            if (index == 0) {
                /* 102 */
                long objectId = ((ValueObject) value).getObjectId();
                /* 103 */
                if (objectId == 0L) {
                    /* 104 */
                    return "null";
                    /*     */
                }
                /* 106 */
                return Strings.f("id=%d", new Object[]{Long.valueOf(objectId)});
                /*     */
            }
            /*     */
        }
        /* 109 */
        else if ((value instanceof ValueArray)) {
            /* 110 */
            if (index == 0) {
                /* 111 */
                long objectId = ((ValueArray) value).getObjectId();
                /* 112 */
                if (objectId == 0L) {
                    /* 113 */
                    return "null";
                    /*     */
                }
                /* 115 */
                return Strings.f("id=%d", new Object[]{Long.valueOf(objectId)});
                /*     */
            }
            /*     */
        }
        /* 118 */
        else if ((value instanceof ValueRaw)) {
            /* 119 */
            byte[] v = ((ValueRaw) value).getValue();
            /*     */
            /* 121 */
            ByteOrder targetByteOrder = unit.getTargetInformation().getEndianness().toByteOrder();
            /* 122 */
            ByteBuffer b = ByteBuffer.wrap(v).order(targetByteOrder);
            /*     */
            /* 124 */
            switch (v.length) {
                /*     */
                case 1:
                    /* 126 */
                    if (index == 0) {
                        /* 127 */
                        return Strings.f("%02Xh", new Object[]{Byte.valueOf(b.get())});
                        /*     */
                    }
                    /*     */
                    break;
                /*     */
                case 2:
                    /* 131 */
                    if (index == 0) {
                        /* 132 */
                        return Strings.f("%04Xh", new Object[]{Short.valueOf(b.getShort())});
                        /*     */
                    }
                    /*     */
                    break;
                /*     */
                case 4:
                    /* 136 */
                    if (index == 0) {
                        /* 137 */
                        return Strings.f("%08Xh", new Object[]{Integer.valueOf(b.getInt())});
                        /*     */
                    }
                    /* 139 */
                    if (index == 1)
                        /*     */ {
                        /* 141 */
                        return getMemory(unit, b.getInt() & 0xFFFFFFFF, 4);
                        /*     */
                    }
                    /*     */
                    break;
                /*     */
                case 8:
                    /* 145 */
                    if (index == 0) {
                        /* 146 */
                        return Strings.f("%016Xh", new Object[]{Long.valueOf(b.getLong())});
                        /*     */
                    }
                    /* 148 */
                    if (index == 1)
                        /*     */ {
                        /* 150 */
                        return getMemory(unit, b.getLong(), 8);
                        /*     */
                    }
                    /*     */
                    break;
                /*     */
                case 3:
                case 5:
                case 6:
                case 7:
                default:
                    /* 154 */
                    if (index == 0) {
                        /* 155 */
                        return Strings.f("b'%s", new Object[]{Formatter.byteArrayToHexString(v)});
                        /*     */
                    }
                    /*     */
                    break;
                /*     */
            }
            /*     */
        }
        /*     */
        else {
            /* 161 */
            return value.toString();
            /*     */
        }
        /* 163 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    private static String getMemory(IDebuggerUnit unit, long address, int size) {
        /* 167 */
        byte[] mem = DebuggerUtil.readMemoryStringSafe(unit, address, size);
        /* 168 */
        if (mem == null) {
            /* 169 */
            return null;
            /*     */
        }
        /* 171 */
        int asciiLength = Strings.getAsciiLength(mem);
        /* 172 */
        if (asciiLength < size) {
            /* 173 */
            if (mem.length >= size) {
                /* 174 */
                return Strings.f("%sh", new Object[]{Formatter.byteArrayToHexString(mem, 0, size)});
                /*     */
            }
            /*     */
            /* 177 */
            return Strings.f("b'%s", new Object[]{Formatter.byteArrayToHexString(mem)});
            /*     */
        }
        /*     */
        /*     */
        /* 181 */
        return new String(mem, 0, asciiLength, Charset.defaultCharset());
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public static ITypedValue buildValue(ITypedValue value, String newValue)
    /*     */ {
        /* 189 */
        if (newValue == null) {
            /* 190 */
            return null;
            /*     */
        }
        /* 192 */
        if ((value instanceof AbstractValueComposite)) {
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /*     */
        /* 199 */
        String type = value.getTypeName();
        /* 200 */
        newValue = newValue.trim();
        /* 201 */
        if (newValue.isEmpty()) {
            /* 202 */
            if ((type.equals("string")) || (type.equals("char"))) {
                /* 203 */
                return AbstractValuePrimitive.parseValue(type, newValue);
                /*     */
            }
            /* 205 */
            return null;
            /*     */
        }
        /*     */
        try {
            /* 208 */
            if (((value instanceof ValueRaw)) &&
                    /* 209 */         (newValue.startsWith("b'"))) {
                /* 210 */
                return AbstractValuePrimitive.parseValue(type, newValue.substring(2));
                /*     */
            }
            /*     */
            /* 213 */
            return AbstractValuePrimitive.parseValue(type, newValue);
            /*     */
        }
        /*     */ catch (NumberFormatException e) {
        }
        /*     */
        /* 217 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public static boolean equals(ITypedValue value, String newValue, IDebuggerUnit unit)
    /*     */ {
        /* 223 */
        for (int i = 0; i < 2; i++) {
            /* 224 */
            String formatted = formatValue(value, i, unit);
            /* 225 */
            if ((formatted != null) && (formatted.equals(newValue))) {
                /* 226 */
                return true;
                /*     */
            }
            /*     */
        }
        /* 229 */
        return false;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\DbgTypedValueUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */