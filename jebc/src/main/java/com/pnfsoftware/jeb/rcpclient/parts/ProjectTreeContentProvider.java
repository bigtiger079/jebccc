/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.pnfsoftware.jeb.core.IArtifact;
/*     */ import com.pnfsoftware.jeb.core.IEnginesContext;
/*     */ import com.pnfsoftware.jeb.core.ILiveArtifact;
/*     */ import com.pnfsoftware.jeb.core.IRuntimeProject;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
/*     */ import com.pnfsoftware.jeb.rcpclient.RcpClientProperties;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
/*     */ import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTreeContentProvider;
/*     */ import com.pnfsoftware.jeb.util.collect.ArrayUtil;
/*     */ import com.pnfsoftware.jeb.util.events.AggregatorDispatcher;
/*     */ import com.pnfsoftware.jeb.util.events.IEvent;
/*     */ import com.pnfsoftware.jeb.util.events.IEventListener;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.eclipse.jface.viewers.TreeViewer;
/*     */ import org.eclipse.jface.viewers.Viewer;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;

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
/*     */ class ProjectTreeContentProvider
        /*     */ implements IFilteredTreeContentProvider
        /*     */ {
    /*  40 */   private static final ILogger logger = GlobalLog.getLogger(ProjectTreeContentProvider.class);
    /*     */   private IEnginesContext engctx;
    /*     */   private IEventListener listener;
    /*     */   private RcpClientContext context;
    /*     */   private PartManager pman;
    /*     */   private RcpClientProperties contextProperties;

    /*     */
    /*     */
    public ProjectTreeContentProvider(RcpClientContext context, PartManager pman)
    /*     */ {
        /*  49 */
        this.context = context;
        /*  50 */
        this.pman = pman;
        /*     */
        /*  52 */
        this.contextProperties = context.getProperties();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void dispose() {
    }

    /*     */
    /*     */
    /*     */
    public void inputChanged(final Viewer viewer, Object oldInput, Object newInput)
    /*     */ {
        /*  61 */
        logger.i("Input changed from %s to %s", new Object[]{oldInput, newInput});
        /*     */
        /*  63 */
        if (this.listener != null) {
            /*  64 */
            if (oldInput != this.engctx) {
                /*  65 */
                throw new RuntimeException();
                /*     */
            }
            /*  67 */
            this.engctx.removeListener(this.listener);
            /*  68 */
            this.listener = null;
            /*     */
        }
        /*     */
        /*  71 */
        if (newInput == null) {
            /*  72 */
            this.engctx = null;
            /*  73 */
            return;
            /*     */
        }
        /*     */
        /*  76 */
        if (!(newInput instanceof IEnginesContext)) {
            /*  77 */
            throw new RuntimeException();
            /*     */
        }
        /*  79 */
        this.engctx = ((IEnginesContext) newInput);
        /*     */
        /*  81 */
        Display display = viewer.getControl().getDisplay();
        /*     */
        /*     */
        /*     */
        /*  85 */
        this.engctx.addListener(this. = new AggregatorDispatcher(10000, 500L)
                /*     */ {
            /*     */
            public void onMultipleEvents(final List<IEvent> events) {
                /*  88 */
                ProjectTreeContentProvider.logger.i("Event received: %s", new Object[]{events});
                /*     */
                /*     */
                /*  91 */
                if (!viewer.isDisposed()) {
                    /*  92 */
                    UIExecutor.async(viewer, new UIRunnable()
                            /*     */ {
                        /*     */
                        public void runi() {
                            /*  95 */
                            if ((ProjectTreeContentProvider .1. this.val$display.isDisposed()) ||
                            (ProjectTreeContentProvider .1. this.val$viewer.getControl().isDisposed())){
                                /*  96 */
                                return;
                                /*     */
                            }
                            /*     */
                            /*  99 */
                            TreeViewer treeViewer = (TreeViewer) ProjectTreeContentProvider .1. this.val$viewer;
                            /*     */
                            /*     */
                            /* 102 */
                            ProjectTreeContentProvider .1. this.val$viewer.refresh();
                            /*     */
                            /* 104 */
                            for (IEvent e : events) {
                                /* 105 */
                                Object object = e.getData();
                                /* 106 */
                                if ((object instanceof ILiveArtifact))
                                    /*     */ {
                                    /* 108 */
                                    boolean useAManager = (ProjectTreeContentProvider.this.contextProperties != null) && (ProjectTreeContentProvider.this.contextProperties.shouldAutoOpenDefaultUnit());
                                    /*     */
                                    /* 110 */
                                    IArtifactManager amanager = ArtifactManager.getInstance();
                                    /*     */
                                    /*     */
                                    /* 113 */
                                    List<Object> expanded = new ArrayList();
                                    /* 114 */
                                    expanded.add(((ILiveArtifact) object).getRuntimeProject());
                                    /* 115 */
                                    expanded.add(object);
                                    /* 116 */
                                    if (useAManager) {
                                        /* 117 */
                                        expanded.addAll(amanager.getExpandedUnits((ILiveArtifact) object));
                                        /*     */
                                    }
                                    /* 119 */
                                    ProjectTreeContentProvider.logger.i("  newly expanded (%d): %s", new Object[]{Integer.valueOf(expanded.size()), expanded});
                                    /* 120 */
                                    expanded.addAll(0, Arrays.asList(treeViewer.getExpandedElements()));
                                    /* 121 */
                                    treeViewer.setExpandedElements(expanded.toArray());
                                    /*     */
                                    /* 123 */
                                    if (useAManager)
                                        /*     */ {
                                        /* 125 */
                                        amanager.processLiveArtifact(ProjectTreeContentProvider.this.context, ProjectTreeContentProvider.this.pman, (ILiveArtifact) object, treeViewer);
                                        /*     */
                                    }
                                    /*     */
                                }
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                    });
                    /*     */
                }
                /*     */
            }
            /*     */
        });
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public Object[] getElements(Object element)
    /*     */ {
        /* 139 */
        return getChildren(element);
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public Object getParent(Object element)
    /*     */ {
        /* 145 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public Object[] getChildren(Object element)
    /*     */ {
        /* 151 */
        Object[] r = new Object[0];
        /* 152 */
        if ((element instanceof IEnginesContext)) {
            /* 153 */
            IEnginesContext input = (IEnginesContext) element;
            /* 154 */
            r = input.getProjects().toArray();
            /*     */
        }
        /* 156 */
        else if ((element instanceof IRuntimeProject)) {
            /* 157 */
            IRuntimeProject input = (IRuntimeProject) element;
            /* 158 */
            r = input.getLiveArtifacts().toArray();
            /*     */
        }
        /* 160 */
        else if ((element instanceof ILiveArtifact)) {
            /* 161 */
            ILiveArtifact input = (ILiveArtifact) element;
            /* 162 */
            r = input.getUnits().toArray();
            /*     */
        }
        /* 164 */
        else if ((element instanceof IUnit)) {
            /* 165 */
            IUnit input = (IUnit) element;
            /* 166 */
            if (input.getChildren() != null) {
                /* 167 */
                r = input.getChildren().toArray();
                /*     */
            }
            /*     */
            else {
                /* 170 */
                r = ArrayUtil.NO_OBJECT;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 174 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean hasChildren(Object element)
    /*     */ {
        /* 180 */
        boolean r = false;
        /* 181 */
        if ((element instanceof IEnginesContext)) {
            /* 182 */
            IEnginesContext input = (IEnginesContext) element;
            /* 183 */
            r = !input.getProjects().isEmpty();
            /*     */
        }
        /* 185 */
        else if ((element instanceof IRuntimeProject)) {
            /* 186 */
            IRuntimeProject input = (IRuntimeProject) element;
            /* 187 */
            r = !input.getLiveArtifacts().isEmpty();
            /*     */
        }
        /* 189 */
        else if ((element instanceof ILiveArtifact)) {
            /* 190 */
            ILiveArtifact input = (ILiveArtifact) element;
            /* 191 */
            r = !input.getUnits().isEmpty();
            /*     */
        }
        /* 193 */
        else if ((element instanceof IUnit)) {
            /* 194 */
            IUnit input = (IUnit) element;
            /* 195 */
            r = (input.getChildren() != null) && (!input.getChildren().isEmpty());
            /*     */
        }
        /*     */
        /* 198 */
        return r;
        /*     */
    }

    /*     */
    /*     */
    public static String getString(Object element) {
        /* 202 */
        if ((element instanceof IRuntimeProject)) {
            /* 203 */
            return ((IRuntimeProject) element).getName();
            /*     */
        }
        /* 205 */
        if ((element instanceof ILiveArtifact)) {
            /* 206 */
            return ((ILiveArtifact) element).getArtifact().getName();
            /*     */
        }
        /* 208 */
        if ((element instanceof IUnit)) {
            /* 209 */
            return ((IUnit) element).getName();
            /*     */
        }
        /* 211 */
        if (element != null) {
            /* 212 */
            return element.toString();
            /*     */
        }
        /* 214 */
        return null;
        /*     */
    }

    /*     */
    /*     */
    public Object[] getRowElements(Object row)
    /*     */ {
        /* 219 */
        return new Object[]{getString(row)};
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\ProjectTreeContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */