package com.pnfsoftware.jeb.rcpclient.extensions.viewers;

import com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup.ArrayGroup;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup.ArrayLogicalGroup;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup.IArrayGroup;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.arraygroup.VirtualArrayGroup;
import com.pnfsoftware.jeb.util.format.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractArrayGroupFilteredTreeContentProvider implements IFilteredTreeContentProvider {
    public static final int DEFAULT_LIMIT = 1000;
    public static final int DEFAULT__GROUP_LIMIT = 100;
    public static final int DEFAULT_PERFORMANCE_LIMIT = 20;
    private static final int MIN_GROUP_SIZE_ALLOWED = 10;
    private int limit;
    private int groupLimit;
    private int performanceLimit;
    private boolean performanceOptimizer = false;
    private Map<Integer, Map<Integer, Map<Object, ArrayGroup>>> arrayGroups = new HashMap();
    private Map<Integer, Map<Integer, Map<Object, ArrayLogicalGroup>>> arrayLogicalGroups = new HashMap();
    private Map<Integer, Map<Object, VirtualArrayGroup>> virtualElements = new HashMap();

    public AbstractArrayGroupFilteredTreeContentProvider(int limit, int groupLimit, int performanceLimit) {
        this.limit = limit;
        this.groupLimit = groupLimit;
        this.performanceLimit = performanceLimit;
    }

    public AbstractArrayGroupFilteredTreeContentProvider() {
        this(1000, 100, 20);
    }

    protected ArrayGroup getArrayGroup(Object parentElement, int firstIndex, int toplevel) {
        Map<Integer, Map<Object, ArrayGroup>> lev = (Map) this.arrayGroups.get(Integer.valueOf(toplevel));
        if (lev == null) {
            lev = new HashMap();
            this.arrayGroups.put(Integer.valueOf(toplevel), lev);
        }
        Map<Object, ArrayGroup> gr = (Map) lev.get(Integer.valueOf(firstIndex));
        if (gr == null) {
            gr = new HashMap();
            lev.put(Integer.valueOf(firstIndex), gr);
        }
        ArrayGroup g = (ArrayGroup) gr.get(parentElement);
        if (g == null) {
            g = new ArrayGroup(firstIndex);
            gr.put(parentElement, g);
        }
        g.getChildren().clear();
        return g;
    }

    protected ArrayLogicalGroup getArrayLogicalGroup(Object parentElement, int firstIndex, String groupName, boolean packaged, int toplevel) {
        Map<Integer, Map<Object, ArrayLogicalGroup>> lev = (Map) this.arrayLogicalGroups.get(Integer.valueOf(toplevel));
        if (lev == null) {
            lev = new HashMap();
            this.arrayLogicalGroups.put(Integer.valueOf(toplevel), lev);
        }
        Map<Object, ArrayLogicalGroup> gr = (Map) lev.get(Integer.valueOf(firstIndex));
        if (gr == null) {
            gr = new HashMap();
            lev.put(Integer.valueOf(firstIndex), gr);
        }
        ArrayLogicalGroup g = (ArrayLogicalGroup) gr.get(parentElement);
        if ((g == null) || (!Strings.equals(g.getGroupName(), groupName)) || (g.isPackaged() != packaged)) {
            g = new ArrayLogicalGroup(firstIndex, groupName, packaged);
            gr.put(parentElement, g);
        }
        g.getChildren().clear();
        return g;
    }

    protected VirtualArrayGroup getVirtualElement(Object elt, int firstIndex, String label) {
        Map<Object, VirtualArrayGroup> gr = (Map) this.virtualElements.get(Integer.valueOf(firstIndex));
        if (gr == null) {
            gr = new HashMap();
            this.virtualElements.put(Integer.valueOf(firstIndex), gr);
        }
        VirtualArrayGroup g = (VirtualArrayGroup) gr.get(elt);
        if ((g == null) || (!Strings.equals(g.getGroupName(), label))) {
            g = new VirtualArrayGroup(firstIndex, elt, label);
            gr.put(elt, g);
        }
        g.getChildren().clear();
        return g;
    }

    public final boolean hasChildren(Object element) {
        if ((element instanceof IArrayGroup)) {
            if (((IArrayGroup) element).isSingle()) {
                return hasChildren(((IArrayGroup) element).getFirstElement());
            }
            return true;
        }
        return hasChildren2(element);
    }

    public abstract boolean hasChildren2(Object paramObject);

    public final Object[] getChildren(Object parentElement) {
        if ((parentElement instanceof IArrayGroup)) {
            if (((IArrayGroup) parentElement).isSingle()) {
                parentElement = ((IArrayGroup) parentElement).getFirstElement();
            } else {
                return ((ArrayGroup) parentElement).getChildren().toArray();
            }
        }
        List<?> children = getChildren2(parentElement);
        if (children == null) {
            return null;
        }
        if (this.limit < 10) {
            return children.toArray();
        }
        Object[] elts = children.toArray();
        sort(elts);
        children = Arrays.asList(elts);
        return noMoreThanNChildren(children, parentElement);
    }

    private Object[] noMoreThanNChildren(List<?> r, Object parentElement) {
        if (((this.performanceOptimizer) && (r.size() >= this.performanceLimit)) || (r.size() > this.limit)) {
            if (!this.performanceOptimizer) {
                this.performanceOptimizer = true;
                onFirstOptimization(r);
            }
            List<IArrayGroup> groups = new ArrayList();
            Map<Integer, ArrayLogicalGroup> logicalGroups = getLogicalGroups(r, parentElement);
            int logicalGroupSize = getLogicalGroupSize(logicalGroups);
            if (r.size() <= this.limit) {
                if (logicalGroups.size() == 0) {
                    return r.toArray();
                }
                if ((logicalGroups.size() == 1) && (r.size() - logicalGroupSize < this.performanceLimit)) {
                    return r.toArray();
                }
            }
            ArrayGroup currentGroup = null;
            boolean shouldCreateIntermediateGroups = r.size() - logicalGroupSize > this.limit;
            for (int i = 0; i < r.size(); i++) {
                if (!logicalGroups.isEmpty()) {
                    ArrayLogicalGroup logicalGroup = (ArrayLogicalGroup) logicalGroups.get(Integer.valueOf(i));
                    if (logicalGroup != null) {
                        currentGroup = null;
                        groups.add(logicalGroup);
                        i += logicalGroup.size() - 1;
                        continue;
                    }
                }
                if ((shouldCreateIntermediateGroups) && ((currentGroup == null) || (currentGroup.size() >= getGroupLimit()))) {
                    currentGroup = getArrayGroup(parentElement, i, 0);
                    groups.add(currentGroup);
                }
                if (currentGroup != null) {
                    currentGroup.add(r.get(i));
                } else {
                    groups.add(getVirtualElement(r.get(i), i, null));
                }
            }
            if ((shouldCreateIntermediateGroups) && (logicalGroupSize > 0)) {
                int flattenedElements = this.limit - groups.size() % this.limit;
                while ((flattenedElements != this.limit) && (flattenedElements > 0)) {
                    List<Integer> groupsBySize = getSmallestGroups(groups);
                    if (groupsBySize.isEmpty()) {
                        break;
                    }
                    for (int j = groupsBySize.size() - 1; j >= 0; j--) {
                        int index = ((Integer) groupsBySize.get(j)).intValue();
                        flattenedElements -= ((IArrayGroup) groups.get(index)).size();
                        if (flattenedElements < 0) break;
                        IArrayGroup g = (IArrayGroup) groups.remove(index);
                        for (int k = 0; k < g.size(); k++) {
                            groups.add(index + k, new VirtualArrayGroup(g.getFirstElementIndex() + k, g.getChildren().get(k)));
                        }
                    }
                }
            }
            return noMoreThanNChildrenGroup(groups, parentElement, 1);
        }
        return r.toArray();
    }

    private List<Integer> getSmallestGroups(List<IArrayGroup> groups) {
        int minSize = -1;
        List<Integer> subGroups = new ArrayList();
        for (int i = 1; i < groups.size(); i++) {
            IArrayGroup g = (IArrayGroup) groups.get(i);
            if (!(g instanceof ArrayLogicalGroup)) {
                int size = g.size();
                if (size != 1) {
                    if ((size < minSize) || (minSize == -1)) {
                        minSize = size;
                        subGroups.clear();
                        subGroups.add(Integer.valueOf(i));
                    } else if (size == minSize) {
                        subGroups.add(Integer.valueOf(i));
                    }
                }
            }
        }
        return subGroups;
    }

    private int getLogicalGroupSize(Map<Integer, ArrayLogicalGroup> logicalGroups) {
        int size = 0;
        if ((logicalGroups == null) || (logicalGroups.size() == 0)) {
            return size;
        }
        for (ArrayLogicalGroup g : logicalGroups.values()) {
            size += g.size();
        }
        return size;
    }

    private Object[] noMoreThanNChildrenGroup(List<IArrayGroup> r, Object parentElement, int toplevel) {
        if (this.groupLimit < 10) {
            return r.toArray();
        }
        if (r.size() > this.limit) {
            List<IArrayGroup> groups = new ArrayList();
            ArrayGroup currentGroup = null;
            for (int i = 0; i < r.size(); i++) {
                if ((currentGroup == null) || (currentGroup.size() >= this.groupLimit)) {
                    currentGroup = getArrayGroup(parentElement, ((IArrayGroup) r.get(i)).getFirstElementIndex(), toplevel);
                    groups.add(currentGroup);
                }
                currentGroup.add(r.get(i));
            }
            return noMoreThanNChildrenGroup(groups, parentElement, toplevel + 1);
        }
        return r.toArray();
    }

    public Map<Integer, ArrayLogicalGroup> getLogicalGroups(List<?> r, Object parentElement) {
        return new HashMap();
    }

    public abstract List<?> getChildren2(Object paramObject);

    public abstract void sort(Object[] paramArrayOfObject);

    public void onFirstOptimization(List<?> r) {
    }

    public static String getStringAt(Object element, int index) {
        if ((element instanceof IArrayGroup)) {
            IArrayGroup v = (IArrayGroup) element;
            if (index == 0) {
                return String.format("[%d..%d]", new Object[]{Integer.valueOf(v.getFirstElementIndex()), Integer.valueOf(v.getLastElementIndex())});
            }
        }
        return null;
    }

    public int getLimit() {
        return this.limit;
    }

    public int getGroupLimit() {
        return this.groupLimit < 10 ? this.limit : this.groupLimit;
    }
}


