package com.pnfsoftware.jeb.rcpclient.extensions.graph.layout;

import com.pnfsoftware.jeb.util.base.Assert;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConstraintSolver implements Cloneable {
    private static final ILogger logger = GlobalLog.getLogger(ConstraintSolver.class);
    boolean solved;
    int[] varsmin;
    List<Constraint> constraints = new ArrayList<>();
    List<Constraint> constraints0 = new ArrayList<>();
    private int strategy = 3;

    static class Constraint {
        final boolean[] vector;
        final int minval;
        int usedCount;
        int[] usedPositions;

        public Constraint(boolean[] vector, int minval) {
            this.vector = vector;
            this.minval = minval;
            this.usedPositions = new int[vector.length];
            int pos = 0;
            int i = 0;
            for (boolean incl : vector) {
                if (incl) {
                    this.usedPositions[(i++)] = pos;
                    this.usedCount += 1;
                }
                pos++;
            }
        }

        public int countUsed() {
            return this.usedCount;
        }

        public int countKnowns(int[] varsmin) {
            int cnt = 0;
            for (int i = 0; i < varsmin.length; i++) {
                if ((this.vector[i]) && (varsmin[i] > 0)) {
                    cnt++;
                }
            }
            return cnt;
        }

        public int countUnknowns(int[] varsmin) {
            int cnt = 0;
            for (int i = 0; i < varsmin.length; i++) {
                if ((this.vector[i]) && (varsmin[i] == 0)) {
                    cnt++;
                }
            }
            return cnt;
        }

        public void verify(int[] varsmin) {
            int minval1 = this.minval;
            for (int i = 0; i < varsmin.length; i++) {
                if (this.vector[i]) {
                    minval1 -= varsmin[i];
                }
            }
            if (minval1 > 0) {
                throw new RuntimeException("Constraint verification error: " + this);
            }
        }

        public String toString() {
            return String.format("%s -> %d", Arrays.toString(this.vector), this.minval);
        }
    }

    public ConstraintSolver(int varcnt) {
        if (varcnt <= 0) {
            throw new IllegalArgumentException();
        }
        this.varsmin = new int[varcnt];
    }

    private ConstraintSolver() {
    }

    public ConstraintSolver clone() {
        ConstraintSolver r = new ConstraintSolver();
        r.varsmin = this.varsmin.clone();
        r.constraints = new ArrayList<>(this.constraints.size());
        for (Constraint c : this.constraints) {
            r.constraints.add(new Constraint(c.vector.clone(), c.minval));
        }
        r.solved = this.solved;
        return r;
    }

    public int getVariableCount() {
        return this.varsmin.length;
    }

    public boolean isSolved() {
        return this.solved;
    }

    public int[] getResolution() {
        if (!isSolved()) {
            throw new IllegalStateException("The constraints have not been solved yet");
        }
        return this.varsmin;
    }

    public void add(boolean[] vector, int minval) {
        if (isSolved()) {
            throw new IllegalStateException("Constraints were solved already");
        }
        if (vector.length != getVariableCount()) {
            throw new IllegalArgumentException("Constraints vector size must have length " + this.varsmin.length);
        }
        int[] usedPositions = new int[getVariableCount()];
        int cnt = countUsedVariables(vector, usedPositions);
        if (cnt == 0) {
            throw new IllegalArgumentException("No constraints to resolve " + minval);
        }
        if (minval < 0) {
            throw new IllegalArgumentException("Illegal constraint: negative integer: " + minval);
        }
        this.constraints0.add(new Constraint(vector, minval));
        if (minval == 0) {
            return;
        }
        logger.i("Adding constraint: %s -> %d", Arrays.toString(vector), minval);
        if (cnt == 1) {
            int pos = usedPositions[0];
            if (this.varsmin[pos] < minval) {
                this.varsmin[pos] = minval;
            }
            return;
        }
        this.constraints.add(new Constraint(vector, minval));
    }

    static int countUsedVariables(boolean[] vector, int[] positions) {
        int cnt = 0;
        int i = 0;
        int j = 0;
        for (boolean incl : vector) {
            if (incl) {
                if (positions != null) {
                    positions[(j++)] = i;
                }
                cnt++;
            }
            i++;
        }
        return cnt;
    }

    public int[] solve() {
        return solve(true);
    }

    public int[] solve(boolean verify) {
        if (isSolved()) {
            throw new IllegalStateException("Constraints were solved already");
        }
        Constraint c;
        while (!this.constraints.isEmpty()) {
            while (!this.constraints.isEmpty()) {
                int changecnt = 0;
                int icst = 0;
                while (icst < this.constraints.size()) {
                    if (isUselessConstraint(this.constraints.get(icst))) {
                        this.constraints.remove(icst);
                        changecnt++;
                    } else {
                        icst++;
                    }
                }
                if (changecnt == 0) {
                    break;
                }
            }
            if (this.constraints.isEmpty()) {
                break;
            }
            if (this.strategy != 1) {
                if (this.strategy == 2) {
                    Collections.sort(this.constraints, new Comparator<Constraint>() {
                        public int compare(ConstraintSolver.Constraint c1, ConstraintSolver.Constraint c2) {
                            int r = -(c1.countUnknowns(ConstraintSolver.this.varsmin) - c2.countUnknowns(ConstraintSolver.this.varsmin));
                            if (r == 0) {
                                r = -(c1.usedCount - c2.usedCount);
                            }
                            return r;
                        }
                    });
                } else if (this.strategy == 3) {
                    Collections.sort(this.constraints, new Comparator<Constraint>() {
                        public int compare(Constraint c1, Constraint c2) {
                            int r = -(c1.usedCount - c2.usedCount);
                            if (r == 0) {
                                r = -(c1.countUnknowns(ConstraintSolver.this.varsmin) - c2.countUnknowns(ConstraintSolver.this.varsmin));
                            }
                            return r;
                        }
                    });
                } else {
                    throw new RuntimeException("Unknown strategy: " + this.strategy);
                }
            }
            c = this.constraints.remove(0);
            applyFairSplit(c);
        }
        if (verify) {
            for (Constraint constraint : this.constraints0) {
                constraint.verify(this.varsmin);
            }
        }
        this.solved = true;
        return this.varsmin;
    }

    private boolean isUselessConstraint(Constraint constraint) {
        int minval1 = constraint.minval;
        for (int i = 0; i < this.varsmin.length; i++) {
            if (constraint.vector[i]) {
                int v = this.varsmin[i];
                if (v <= 0) {
                    return false;
                }
                minval1 -= v;
                if (minval1 <= 0) return true;
            }
        }
        return false;
    }

    private void applyFairSplit(Constraint c) {
        Assert.a(c.usedCount >= 2, "Expecting a multi-variable constraint");
        int avg = c.minval / c.usedCount;
        for (int i = 0; i < c.usedCount; i++) {
            int pos = c.usedPositions[i];
            if (this.varsmin[pos] < avg) {
                this.varsmin[pos] = avg;
            }
        }
        int rem = c.minval % c.usedCount;
        if (rem != 0) {
            this.varsmin[c.usedPositions[0]] += rem;
        }
    }
}


