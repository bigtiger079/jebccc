package com.pnfsoftware.jeb.rcpclient.handlers.actions;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.ITextItem;
import com.pnfsoftware.jeb.core.output.text.impl.TextItem;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.code.java.IJavaSourceUnit;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.AbstractUnitFragment;
import com.pnfsoftware.jeb.rcpclient.parts.units.InteractiveTextView;
import com.pnfsoftware.jeb.rcpclient.util.DbgUtils;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ActionFormatterHandler extends JebBaseHandler {
    private static final ILogger logger = GlobalLog.getLogger(ActionFormatterHandler.class);
    private static String[] BASE_TYPES = {"byte", "short", "char", "int", "long", "float", "double", "boolean"};
    private static String[] BASE_ARR_TYPES = {"byte[]", "short[]", "char[]", "int[]", "long[]", "float[]", "double[]", "boolean[]"};

    public ActionFormatterHandler(){
        super("formatter", "Formatter", 0, "Formatter Java source", "eclipse/all_sc_obj.png", 32);
    }

    @Override
    public boolean canExecute() {
        IUnit activeUnit = getActiveUnit(part);
        AbstractUnitFragment<?> activeFragment = getActiveFragment(part);
        return (activeUnit instanceof IJavaSourceUnit) && (activeFragment instanceof InteractiveTextView);
    }

    @Override
    public void execute() {
        if (getUnit().getFormatType().equals("java")) {
            InteractiveTextView fragment = (InteractiveTextView) ((UnitPartManager) part.getManager()).getActiveFragment();
            String source = new String(fragment.export());
            CompilationUnit compilationUnit = StaticJavaParser.parse(source);
            compilationUnit.findAll(MethodDeclaration.class).stream().forEach(new Consumer<MethodDeclaration>() {
                @Override
                public void accept(MethodDeclaration methodDeclaration) {
                    SimpleName name = methodDeclaration.getName();
                    logger.info(name.asString());
                    if (methodDeclaration.getParameters().size() > 0) {
                        logger.info(methodDeclaration.getParameters().get(0).getName().asString());
                    }
                }
            });
            ITextDocumentPart documentPart = fragment.getDocument().getDocumentPart(0, 0, 0);
            List<? extends ILine> lines = documentPart.getLines();
            int index = 0;
            for (ILine line : lines) {
                TextItem lastItem=null;
                List<? extends ITextItem> items = line.getItems();
                if (items != null && !items.isEmpty()) {
                    if (!DbgUtils.isMethodDefineLine(line)) {
                        continue;
                    }
                    logger.info("MethodDefineLine: %s", line.getText());
                    Pair<String, List<Pair<String, String>>> methodInfo = DbgUtils.getMethodInfo(line);
                    if (methodInfo != null && methodInfo.getValue().isEmpty()) {
                        continue;
                    }

                    for (ITextItem item : items) {
                        if (item instanceof TextItem) {
                            long itemId = ((TextItem) item).getItemId();
                            String s = line.getText().subSequence(item.getOffset(), item.getOffsetEnd()).toString();
                            if (s.matches("arg(\\d){1,2}") && itemId != 0 && lastItem != null) {
                                logger.info("find urgly method arg:  %s in line: %d, offset %d, type: %s", s, index, item.getOffset(), lastItem.getText());
                                if (isBaseType(lastItem.getText())) {

                                } else if (isBaseArrayType(lastItem.getText())) {

                                } else {

                                }
                            }
                            lastItem = (TextItem) item;
                        }
                    }
                }
                index++;
            }
        }
    }

    private static boolean isBaseType(String type) {
        return Arrays.asList(BASE_TYPES).contains(type.toLowerCase());
    }

    private static boolean isBaseArrayType(String type) {
        return Arrays.asList(BASE_ARR_TYPES).contains(type.toLowerCase());
    }

    public IUnit getUnit() {
        return getActiveUnit(part);
    }

    public static IUnit getActiveUnit(IMPart part) {
        Object object = part == null ? null : part.getManager();
        return !(object instanceof UnitPartManager) ? null : ((UnitPartManager) object).getUnit();
    }

    public static AbstractUnitFragment<?> getActiveFragment(IMPart part) {
        Object object = part == null ? null : part.getManager();
        return !(object instanceof UnitPartManager) ? null : ((UnitPartManager) object).getActiveFragment();
    }
}
