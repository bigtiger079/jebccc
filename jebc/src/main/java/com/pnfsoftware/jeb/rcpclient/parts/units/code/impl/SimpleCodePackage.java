/*    */
package com.pnfsoftware.jeb.rcpclient.parts.units.code.impl;
/*    */
/*    */

import com.pnfsoftware.jeb.core.units.code.ICodeItem;
/*    */ import com.pnfsoftware.jeb.core.units.code.ICodePackage;
/*    */ import java.util.List;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class SimpleCodePackage
        /*    */ implements ICodePackage
        /*    */ {
    /*    */   private String name;
    /*    */   private String address;

    /*    */
    /*    */
    public SimpleCodePackage(String name)
    /*    */ {
        /* 29 */
        this(name, null);
        /*    */
    }

    /*    */
    /*    */
    public SimpleCodePackage(String name, String address) {
        /* 33 */
        this.name = name;
        /* 34 */
        this.address = address;
        /*    */
    }

    /*    */
    /*    */
    public long getItemId()
    /*    */ {
        /* 39 */
        return 0L;
        /*    */
    }

    /*    */
    /*    */
    public int getIndex()
    /*    */ {
        /* 44 */
        return 0;
        /*    */
    }

    /*    */
    /*    */
    public String getAddress()
    /*    */ {
        /* 49 */
        return this.address;
        /*    */
    }

    /*    */
    /*    */
    public String getName(boolean effective)
    /*    */ {
        /* 54 */
        return this.name;
        /*    */
    }

    /*    */
    /*    */
    public String getSignature(boolean effective)
    /*    */ {
        /* 59 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public boolean isInternal()
    /*    */ {
        /* 64 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public boolean isArtificial()
    /*    */ {
        /* 69 */
        return true;
        /*    */
    }

    /*    */
    /*    */
    public int getGenericFlags()
    /*    */ {
        /* 74 */
        return 0;
        /*    */
    }

    /*    */
    /*    */
    public boolean isRootPackage()
    /*    */ {
        /* 79 */
        return false;
        /*    */
    }

    /*    */
    /*    */
    public ICodePackage getParentPackage()
    /*    */ {
        /* 84 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public List<? extends ICodePackage> getChildrenPackages()
    /*    */ {
        /* 89 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    public List<? extends ICodeItem> getChildren()
    /*    */ {
        /* 94 */
        return null;
        /*    */
    }
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\code\impl\SimpleCodePackage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */