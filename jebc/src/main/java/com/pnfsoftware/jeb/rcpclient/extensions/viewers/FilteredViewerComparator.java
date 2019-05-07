
package com.pnfsoftware.jeb.rcpclient.extensions.viewers;


import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;


public class FilteredViewerComparator
        extends ViewerComparator {
    AbstractFilteredViewer<?, ?> v;
    int propertyIndex;
    int direction;


    public FilteredViewerComparator(Comparator<? super String> comp, AbstractFilteredViewer<?, ?> v) {

        super(comp);

        this.v = v;


        this.propertyIndex = -1;

        this.direction = 0;

    }


    public FilteredViewerComparator(AbstractFilteredViewer<?, ?> v) {

        this(null, v);

    }


    public int getDirection() {

        return this.direction;

    }


    public void setColumn(int column) {

        if (column == this.propertyIndex) {

            if (this.direction == 0) {

                this.direction = 128;

            } else if (this.direction == 128) {

                this.direction = 1024;

            } else if (this.direction == 1024) {

                this.direction = 0;

            }

        } else {

            this.propertyIndex = column;

            this.direction = 128;

        }

    }


    public int compare(Viewer viewer, Object e1, Object e2) {

        if (this.propertyIndex < 0) {

            return 0;

        }

        if (this.direction == 0) {

            return 0;

        }


        Object[] l1 = this.v.getContentProvider().getRowElements(e1);

        Object[] l2 = this.v.getContentProvider().getRowElements(e2);


        Object o1 = this.propertyIndex < l1.length ? l1[this.propertyIndex] : null;

        Object o2 = this.propertyIndex < l2.length ? l2[this.propertyIndex] : null;


        int i = 0;

        while ((i < 5) && ((o1 instanceof Object[])) && ((o2 instanceof Object[]))) {

            if ((((Object[]) o1).length > 0) && (((Object[]) o2).length > 0)) {

                o1 = ((Object[]) (Object[]) o1)[0];

                o2 = ((Object[]) (Object[]) o2)[0];

            }

            i++;

        }


        int rc = 0;

        if (((o1 instanceof String)) && ((o2 instanceof String))) {

            rc = getComparator().compare((String) o1, (String) o2);

        } else if (((o1 instanceof Comparable)) && ((o2 instanceof Comparable)) && (o1.getClass() == o2.getClass())) {

            rc = ((Comparable) o1).compareTo(o2);

        } else {

            if (o1 == null) {

                o1 = "";

            }

            if (o2 == null) {

                o2 = "";

            }

            rc = getComparator().compare(o1.toString(), o2.toString());

        }

        return this.direction == 128 ? rc : -rc;

    }

}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\extensions\viewers\FilteredViewerComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */