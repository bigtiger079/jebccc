package com.bigger.jebextern;

import com.pnfsoftware.jeb.core.*;
import com.pnfsoftware.jeb.core.events.J;
import com.pnfsoftware.jeb.util.events.IEvent;
import com.pnfsoftware.jeb.util.events.IEventListener;
import com.pnfsoftware.jeb.util.events.IEventSource;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import net.sf.cglib.proxy.Enhancer;

import java.util.List;
import java.util.Map;

public class JEBExtern extends AbstractEnginesPlugin {
    private static final ILogger logger = GlobalLog.getLogger(JEBExtern.class);

    @Override
    public void execute(IEnginesContext iEnginesContext, Map<String, String> map) {

    }

    @Override
    public IPluginInformation getPluginInformation() {
        return new PluginInformation("JEB Extension Plugin",
                "Hook JEB Action", "PNF Software",
                Version.create(1, 0, 0), Version.create(3, 0, 9), null);
    }

    @Override
    public void load(IEnginesContext iEnginesContext) {
        super.load(iEnginesContext);
        List<? extends IEventListener> listeners = iEnginesContext.getListeners();
        logger.info("Current listeners is " + listeners.size());
        getTopSource(iEnginesContext).addListener(new IEventListener() {
            @Override
            public void onEvent(IEvent iEvent) {
                logger.info("On event: " + iEvent.getSource()+"  "+((J)iEvent.getType()).name()+"   "+iEvent.getData());
                logger.info("eventSource: " + parseSource(iEvent.getSource()));
                checkClass();
            }
        });

//        Enhancer enhancer = new Enhancer();
        ClassLoader classLoader = this.getClass().getClassLoader();
        classLoader(classLoader);

        logger.info("systemClass Loader: ");
        classLoader(ClassLoader.getSystemClassLoader());


    }

    private static void classLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            return;
        }
        logger.info("Class Loader: " + classLoader.toString());
        classLoader(classLoader.getParent());
    }

    private static void checkClass() {
        try {
            Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass("com.pnf.software.jeb.rcpclient.actions.GraphicalActionExecutor");
            if (aClass != null) {
                logger.info("GraphicalActionExecutor: find Class");
            }

            Class<?> graphicalActionExecutor = Class.forName("com.pnf.software.jeb.rcpclient.actions.GraphicalActionExecutor");
            if (graphicalActionExecutor != null) {
                logger.info("GraphicalActionExecutor: find Class");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            logger.info("GraphicalActionExecutor: ClassNotFoundException");
        }
    }
    private String parseSource(IEventSource event) {
        if (event.getParentSource() != null) {
            return event.toString() + " -> " + parseSource(event.getParentSource());
        } else {
            return event.toString();
        }
    }

    private IEventSource getTopSource(IEventSource source) {
        if (source.getParentSource() == null) {
            return source;
        } else {
            return source.getParentSource();
        }
    }
}
