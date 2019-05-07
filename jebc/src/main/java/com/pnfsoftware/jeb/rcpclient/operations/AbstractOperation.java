/*    */
package com.pnfsoftware.jeb.rcpclient.operations;
/*    */
/*    */

import com.pnfsoftware.jeb.client.api.IOperable;
/*    */ import com.pnfsoftware.jeb.client.api.Operation;
/*    */ import com.pnfsoftware.jeb.client.api.OperationRequest;
/*    */ import com.pnfsoftware.jeb.rcpclient.Assets;
/*    */ import java.io.InputStream;
/*    */ import org.eclipse.jface.action.Action;
/*    */ import org.eclipse.jface.resource.ImageDescriptor;
/*    */ import org.eclipse.swt.graphics.ImageData;

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
/*    */ public abstract class AbstractOperation
        /*    */ extends Action
        /*    */ {
    /*    */   private IOperable object;

    /*    */
    /*    */
    public AbstractOperation(IOperable object, String text)
    /*    */ {
        /* 30 */
        super(text);
        /* 31 */
        this.object = object;
        /* 32 */
        if (getOperationImageData() != null) {
            /* 33 */
            setImageDescriptor(new ImageDescriptor()
                    /*    */ {
                /*    */
                public ImageData getImageData()
                /*    */ {
                    /* 37 */
                    String s = AbstractOperation.this.getOperationImageData();
                    /* 38 */
                    if (s != null) {
                        /* 39 */
                        InputStream is = Assets.getAsset(s);
                        /* 40 */
                        if (is != null) {
                            /* 41 */
                            return new ImageData(is);
                            /*    */
                        }
                        /*    */
                    }
                    /* 44 */
                    return null;
                    /*    */
                }
                /*    */
            });
            /*    */
        }
        /* 48 */
        setEnabled(object.verifyOperation(new OperationRequest(getOperation())));
        /*    */
    }

    /*    */
    /*    */
    public void run()
    /*    */ {
        /* 53 */
        this.object.doOperation(new OperationRequest(getOperation()));
        /*    */
    }

    /*    */
    /*    */
    protected String getOperationImageData() {
        /* 57 */
        return null;
        /*    */
    }

    /*    */
    /*    */
    protected abstract Operation getOperation();
    /*    */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\AbstractOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */