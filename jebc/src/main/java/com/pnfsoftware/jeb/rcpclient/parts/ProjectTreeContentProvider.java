package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.IArtifact;
import com.pnfsoftware.jeb.core.IEnginesContext;
import com.pnfsoftware.jeb.core.ILiveArtifact;
import com.pnfsoftware.jeb.core.IRuntimeProject;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.RcpClientProperties;
import com.pnfsoftware.jeb.rcpclient.extensions.UIExecutor;
import com.pnfsoftware.jeb.rcpclient.extensions.UIRunnable;
import com.pnfsoftware.jeb.rcpclient.extensions.viewers.IFilteredTreeContentProvider;
import com.pnfsoftware.jeb.util.collect.ArrayUtil;
import com.pnfsoftware.jeb.util.events.AggregatorDispatcher;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

class ProjectTreeContentProvider implements IFilteredTreeContentProvider {
    private static final ILogger logger = GlobalLog.getLogger(ProjectTreeContentProvider.class);
    private IEnginesContext engctx;
    private IEventListener listener;
    private RcpClientContext context;
    private PartManager pman;
    private RcpClientProperties contextProperties;

    public ProjectTreeContentProvider(RcpClientContext context, PartManager pman) {
        this.context = context;
        this.pman = pman;
        this.contextProperties = context.getProperties();
    }

    public void dispose() {
    }

    public void inputChanged(final Viewer viewer, Object oldInput, Object newInput) {
        logger.info("Input changed from %s to %s", new Object[]{oldInput, newInput});
        if (this.listener != null) {
            if (oldInput != this.engctx) {
                throw new RuntimeException();
            }
            this.engctx.removeListener(this.listener);
            this.listener = null;
        }
        if (newInput == null) {
            this.engctx = null;
        } else if (newInput instanceof IEnginesContext) {
            this.engctx = (IEnginesContext) newInput;
            final Display display = viewer.getControl().getDisplay();
            IEnginesContext iEnginesContext = this.engctx;
            final Viewer viewer2 = viewer;
            this.listener = new AggregatorDispatcher(10000, 500) {
                public void onMultipleEvents(final List<IEvent> events) {
                    ProjectTreeContentProvider.logger.info("Event received: %s", new Object[]{events});
                    if (!display.isDisposed()) {
                        UIExecutor.async(display, new UIRunnable() {
                            public void runi() {
                                if (!display.isDisposed() && !viewer2.getControl().isDisposed()) {
                                    TreeViewer treeViewer = (TreeViewer) viewer2;
                                    viewer2.refresh();
                                    for (IEvent e : events) {
                                        Object object = e.getData();
                                        if (object instanceof ILiveArtifact) {
                                            boolean useAManager;
                                            if (ProjectTreeContentProvider.this.contextProperties == null || !ProjectTreeContentProvider.this.contextProperties.shouldAutoOpenDefaultUnit()) {
                                                useAManager = false;
                                            } else {
                                                useAManager = true;
                                            }
                                            IArtifactManager amanager = ArtifactManager.getInstance();
                                            List<Object> expanded = new ArrayList<>();
                                            expanded.add(((ILiveArtifact) object).getRuntimeProject());
                                            expanded.add(object);
                                            if (useAManager) {
                                                expanded.addAll(amanager.getExpandedUnits((ILiveArtifact) object));
                                            }
                                            ProjectTreeContentProvider.logger.info("  newly expanded (%d): %s", new Object[]{Integer.valueOf(expanded.size()), expanded});
                                            expanded.addAll(0, Arrays.asList(treeViewer.getExpandedElements()));
                                            treeViewer.setExpandedElements(expanded.toArray());
                                            if (useAManager) {
                                                amanager.processLiveArtifact(ProjectTreeContentProvider.this.context, ProjectTreeContentProvider.this.pman, (ILiveArtifact) object, treeViewer);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            };
            iEnginesContext.addListener(this.listener);
        } else {
            throw new RuntimeException();
        }
    }

    public Object[] getElements(Object element) {
        return getChildren(element);
    }

    public Object getParent(Object element) {
        return null;
    }

    public Object[] getChildren(Object element) {
        Object[] r = new Object[0];
        if ((element instanceof IEnginesContext)) {
            IEnginesContext input = (IEnginesContext) element;
            r = input.getProjects().toArray();
        } else if ((element instanceof IRuntimeProject)) {
            IRuntimeProject input = (IRuntimeProject) element;
            r = input.getLiveArtifacts().toArray();
        } else if ((element instanceof ILiveArtifact)) {
            ILiveArtifact input = (ILiveArtifact) element;
            r = input.getUnits().toArray();
        } else if ((element instanceof IUnit)) {
            IUnit input = (IUnit) element;
            if (input.getChildren() != null) {
                r = input.getChildren().toArray();
            } else {
                r = ArrayUtil.NO_OBJECT;
            }
        }
        return r;
    }

    public boolean hasChildren(Object element) {
        boolean r = false;
        if ((element instanceof IEnginesContext)) {
            IEnginesContext input = (IEnginesContext) element;
            r = !input.getProjects().isEmpty();
        } else if ((element instanceof IRuntimeProject)) {
            IRuntimeProject input = (IRuntimeProject) element;
            r = !input.getLiveArtifacts().isEmpty();
        } else if ((element instanceof ILiveArtifact)) {
            ILiveArtifact input = (ILiveArtifact) element;
            r = !input.getUnits().isEmpty();
        } else if ((element instanceof IUnit)) {
            IUnit input = (IUnit) element;
            r = (input.getChildren() != null) && (!input.getChildren().isEmpty());
        }
        return r;
    }

    public static String getString(Object element) {
        if ((element instanceof IRuntimeProject)) {
            return ((IRuntimeProject) element).getName();
        }
        if ((element instanceof ILiveArtifact)) {
            return ((ILiveArtifact) element).getArtifact().getName();
        }
        if ((element instanceof IUnit)) {
            return ((IUnit) element).getName();
        }
        if (element != null) {
            return element.toString();
        }
        return null;
    }

    public Object[] getRowElements(Object row) {
        return new Object[]{getString(row)};
    }
}


