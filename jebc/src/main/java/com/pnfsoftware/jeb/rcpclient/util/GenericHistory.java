/*     */
package com.pnfsoftware.jeb.rcpclient.util;
/*     */
/*     */

import java.util.ArrayList;
/*     */ import java.util.List;

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
/*     */ public class GenericHistory<T>
        /*     */ {
    /*     */   private ArrayList<T> history;
    /*     */   private int ptr;

    /*     */
    /*     */
    public GenericHistory()
    /*     */ {
        /*  26 */
        this(null);
        /*     */
    }

    /*     */
    /*     */
    public GenericHistory(List<T> init) {
        /*  30 */
        this.history = new ArrayList();
        /*  31 */
        this.ptr = 0;
        /*     */
        /*  33 */
        if (init == null) {
            /*  34 */
            return;
            /*     */
        }
        /*     */
        /*  37 */
        this.history = new ArrayList(init);
        /*  38 */
        this.ptr = this.history.size();
        /*     */
    }

    /*     */
    /*     */
    public void clear() {
        /*  42 */
        this.history.clear();
        /*  43 */
        this.ptr = 0;
        /*     */
    }

    /*     */
    /*     */
    public int size() {
        /*  47 */
        return this.history.size();
        /*     */
    }

    /*     */
    /*     */
    public boolean isEmpty() {
        /*  51 */
        return this.history.isEmpty();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void record(T o)
    /*     */ {
        /*  61 */
        if (o == null) {
            /*  62 */
            return;
            /*     */
        }
        /*     */
        /*  65 */
        if (this.ptr == this.history.size())
            /*     */ {
            /*  67 */
            this.history.add(o);
            /*  68 */
            this.ptr += 1;
            /*     */
        }
        /*     */
        else {
            /*  71 */
            this.history.set(this.ptr, o);
            /*  72 */
            this.ptr += 1;
            /*  73 */
            while (this.history.size() > this.ptr) {
                /*  74 */
                this.history.remove(this.ptr);
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    public void replace(T o)
    /*     */ {
        /*  85 */
        this.history.set(this.ptr - 1, o);
        /*     */
    }

    /*     */
    /*     */
    public List<T> getAll() {
        /*  89 */
        return new ArrayList(this.history);
        /*     */
    }

    /*     */
    /*     */
    public T getLast() {
        /*  93 */
        if (this.history.isEmpty()) {
            /*  94 */
            throw new ArrayIndexOutOfBoundsException();
            /*     */
        }
        /*  96 */
        if ((this.ptr <= 0) || (this.ptr > this.history.size())) {
            /*  97 */
            throw new IllegalStateException("pointer is not coherent with History");
            /*     */
        }
        /*     */
        /* 100 */
        return (T) this.history.get(this.ptr - 1);
        /*     */
    }

    /*     */
    /*     */
    public boolean hasBackward() {
        /* 104 */
        return this.ptr > 1;
        /*     */
    }

    /*     */
    /*     */
    public boolean hasForward() {
        /* 108 */
        return this.ptr < size();
        /*     */
    }

    /*     */
    /*     */
    public T backward() {
        /* 112 */
        if (this.ptr > 1) {
            /* 113 */
            this.ptr -= 1;
            /* 114 */
            return (T) getLast();
            /*     */
        }
        /* 116 */
        throw new ArrayIndexOutOfBoundsException("There is no backward element");
        /*     */
    }

    /*     */
    /*     */
    public T forward() {
        /* 120 */
        if (this.ptr < size()) {
            /* 121 */
            this.ptr += 1;
            /* 122 */
            return (T) getLast();
            /*     */
        }
        /* 124 */
        throw new ArrayIndexOutOfBoundsException("There is no forward element");
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclien\\util\GenericHistory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */