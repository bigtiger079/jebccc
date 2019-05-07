
package com.pnfsoftware.jeb.rcpclient.operations;


import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.Operation;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.rcpclient.Assets;

import java.io.InputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;


public abstract class AbstractOperation
        extends Action {
    private IOperable object;


    public AbstractOperation(IOperable object, String text) {

        super(text);

        this.object = object;

        if (getOperationImageData() != null) {

            setImageDescriptor(new ImageDescriptor() {

                public ImageData getImageData() {

                    String s = AbstractOperation.this.getOperationImageData();

                    if (s != null) {

                        InputStream is = Assets.getAsset(s);

                        if (is != null) {

                            return new ImageData(is);

                        }

                    }

                    return null;

                }

            });

        }

        setEnabled(object.verifyOperation(new OperationRequest(getOperation())));

    }


    public void run() {

        this.object.doOperation(new OperationRequest(getOperation()));

    }


    protected String getOperationImageData() {

        return null;

    }


    protected abstract Operation getOperation();

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\operations\AbstractOperation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */