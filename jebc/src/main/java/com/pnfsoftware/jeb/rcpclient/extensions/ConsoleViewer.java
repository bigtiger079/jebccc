
package com.pnfsoftware.jeb.rcpclient.extensions;

import com.pnfsoftware.jeb.client.api.IOperable;
import com.pnfsoftware.jeb.client.api.OperationRequest;
import com.pnfsoftware.jeb.core.units.AutocompletionResult;
import com.pnfsoftware.jeb.core.units.ExecutionResult;
import com.pnfsoftware.jeb.core.units.ICommandInterpreter;
import com.pnfsoftware.jeb.rcpclient.dialogs.FindTextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.search.FindTextOptions;
import com.pnfsoftware.jeb.rcpclient.extensions.search.GraphicalTextFinder;
import com.pnfsoftware.jeb.rcpclient.extensions.search.SimpleTextFindResults;
import com.pnfsoftware.jeb.rcpclient.extensions.search.StyledTextFindImpl;
import com.pnfsoftware.jeb.util.collect.ItemHistory;
import com.pnfsoftware.jeb.util.format.Strings;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ConsoleViewer
        extends Viewer
        implements IOperable {
    private static final ILogger logger = GlobalLog.getLogger(ConsoleViewer.class);
    public static final String DEFAULT_PROMPT = "> ";
    private StyledText console;
    private StyledTextFindImpl findimpl;
    private GraphicalTextFinder<SimpleTextFindResults> finder;
    private ICommandInterpreter interpreter;
    private String prompt = "> ";
    private boolean shouldSetNewPrompt;
    private String newPrompt;
    private boolean allowEmptyCommands = true;
    private final AtomicBoolean previousKeyWasTab = new AtomicBoolean(false);

    public ConsoleViewer(Composite parent, int style) {
        style &= 0x800;
        this.console = new StyledText(parent, style | 0x2 | 0x200 | 0x100);
        this.console.setFont(JFaceResources.getTextFont());
        this.findimpl = new StyledTextFindImpl(this.console);
        this.finder = new GraphicalTextFinder(this.findimpl, null);
        this.console.addVerifyListener(new VerifyListener() {
            public void verifyText(VerifyEvent e) {
                if (ConsoleViewer.this.console.isTextSelected()) {
                    if (ConsoleViewer.this.console.getSelection().x < ConsoleViewer.this.getFirstValidCaretPosition()) {
                        e.doit = false;
                    }
                }
            }
        });
        this.console.addVerifyKeyListener(new VerifyKeyListener() {
            public void verifyKey(VerifyEvent e) {
                int key = e.keyCode;
                int mask = e.stateMask;
                ConsoleViewer.logger.i("key=%Xh mask=%Xh", new Object[]{Integer.valueOf(key), Integer.valueOf(e.stateMask)});
                if ((mask == 0) && ((key & SWT.MODIFIER_MASK) != 0)) {
                    return;
                }
                if (e.stateMask == SWT.MOD1) {
                    if (key == 108) {
                        ConsoleViewer.this.clearConsole();
                        return;
                    }
                    if (key == 99) {
                        return;
                    }
                    if (key == 118) {
                        if (!ConsoleViewer.this.isValidCaret()) {
                            ConsoleViewer.this.setCaretEnd();
                        }
                        String s = UIUtil.getTextFromClipboard();
                        if ((s != null) && (s.length() > 0)) {
                            s = Strings.splitLines(s)[0];
                            ConsoleViewer.logger.i("Pasting: %s", new Object[]{s});
                            ConsoleViewer.this.replaceSelection(s);
                        }
                        ConsoleViewer.this.showInputLine();
                        e.doit = false;
                        return;
                    }
                }
                if (e.stateMask == SWT.MOD2) {
                    if (UIUtil.isArrowKey(key)) {
                        return;
                    }
                }
                if (!ConsoleViewer.this.isValidCaret()) {
                    ConsoleViewer.this.setCaretEnd();
                }
                if (key == 9) {
                    e.doit = false;
                    if (ConsoleViewer.this.previousKeyWasTab.get()) {
                        ConsoleViewer.this.previousKeyWasTab.set(false);
                        AutocompletionResult auto = ConsoleViewer.this.interpreter.autoComplete(ConsoleViewer.this.getCurrentUserInput());
                        if ((auto == null) || (auto.getAutocompletes() == null) || (auto.getAutocompletes().isEmpty())) {
                            return;
                        }
                        ConsoleViewer.this.executeAutoComplete(auto);
                    } else {
                        ConsoleViewer.this.previousKeyWasTab.set(true);
                    }
                } else {
                    ConsoleViewer.this.previousKeyWasTab.set(false);
                }
                if ((key == 16777217) || (key == 16777218)) {
                    String s = key == 16777217 ? (String) ConsoleViewer.this.getHistory().getPrevious() : (String) ConsoleViewer.this.getHistory().getNext();
                    if (ConsoleViewer.this.getCurrentUserInput().equals(s)) {
                        s = key == 16777217 ? (String) ConsoleViewer.this.getHistory().getPrevious() : (String) ConsoleViewer.this.getHistory().getNext();
                    } else if ((key == 16777218) && (s == null)) {
                        s = "";
                    }
                    if (s != null) {
                        ConsoleViewer.this.clearLine();
                        ConsoleViewer.this.appendText(s);
                        ConsoleViewer.this.setCaretEnd();
                    }
                    e.doit = false;
                } else if ((key == 16777221) || (key == 16777222)) {
                    e.doit = false;
                } else if (key == 16777219) {
                    if (ConsoleViewer.this.getCaretColumnOffset() <= ConsoleViewer.this.prompt.length()) {
                        ConsoleViewer.this.console.setSelection(ConsoleViewer.this.console.getCaretOffset());
                        e.doit = false;
                    }
                } else if (key == 8) {
                    int thres = ConsoleViewer.this.prompt.length();
                    if (ConsoleViewer.this.console.getSelectionCount() == 0) {
                        thres++;
                    }
                    if (ConsoleViewer.this.getCaretColumnOffset() < thres) {
                        ConsoleViewer.this.console.setSelection(ConsoleViewer.this.console.getCaretOffset());
                        e.doit = false;
                    }
                } else if (key == 16777223) {
                    if (e.stateMask == SWT.MOD2) {
                        if (!ConsoleViewer.this.isValidCaret()) {
                            ConsoleViewer.this.setCaretEnd();
                        }
                        ConsoleViewer.this.console.setSelection(ConsoleViewer.this.console.getCaretOffset(), ConsoleViewer.this.getFirstValidCaretPosition());
                    } else {
                        ConsoleViewer.this.setCaretBegin();
                    }
                    e.doit = false;
                } else if ((key == 13) || (key == 16777296)) {
                    String userInput = ConsoleViewer.this.getCurrentUserInput();
                    ConsoleViewer.logger.i("User requested execution of command: \"%s\"", new Object[]{userInput});
                    if ((ConsoleViewer.this.allowEmptyCommands) || (!userInput.trim().isEmpty())) {
                        ConsoleViewer.this.executeCommand(userInput);
                    }
                    ConsoleViewer.this.showInputLine();
                    e.doit = false;
                }
            }
        });
        generatePromp();
    }

    public void setFont(Font font) {
        this.console.setFont(font);
    }

    public Font getFont() {
        return this.console.getFont();
    }

    public void setInterpreter(ICommandInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public ICommandInterpreter getInterpreter() {
        return this.interpreter;
    }

    public void setAllowEmptyCommands(boolean enabled) {
        this.allowEmptyCommands = enabled;
    }

    public boolean getAllowEmptyCommands() {
        return this.allowEmptyCommands;
    }

    public void updatePromptAfterCommand(String prompt) {
        this.shouldSetNewPrompt = true;
        this.newPrompt = prompt;
    }

    public void updatePrompt(String prompt) {
        if (prompt == null) {
            prompt = "> ";
        }
        String currentPrompt = getPrompt();
        if (prompt.equals(currentPrompt)) {
            return;
        }
        String line = getLastLine();
        String currentCommand = "";
        if (line.startsWith(currentPrompt)) {
            currentCommand = line.substring(currentPrompt.length());
        }
        removeTrailingCharacters(line.length());
        appendText(prompt);
        appendText(currentCommand);
        this.prompt = prompt;
    }

    public String getPrompt() {
        return this.prompt;
    }

    private void generatePromp() {
        appendText(getPrompt());
        setCaretEnd();
        updateNewPrompt();
    }

    public void updateNewPrompt() {
        if (this.shouldSetNewPrompt) {
            updatePrompt(this.newPrompt);
            this.shouldSetNewPrompt = false;
            setCaretEnd();
        }
    }

    public void clearConsole() {
        setText("", true);
        generatePromp();
    }

    public void clearLine() {
        removeTrailingCharacters(getCurrentUserInput().length());
    }

    private int getCaretColumnOffset() {
        int offset = this.console.getCaretOffset();
        int lineIndex = this.console.getLineAtOffset(offset);
        return offset - this.console.getOffsetAtLine(lineIndex);
    }

    private String getLastLine() {
        return this.console.getLine(this.console.getLineCount() - 1);
    }

    private String getCurrentUserInput() {
        String raw = this.console.getLine(this.console.getLineCount() - 1);
        if (raw.length() < getPrompt().length()) {
            return "";
        }
        return raw.substring(getPrompt().length());
    }

    public void simulateInputAndExecute(String command) {
        setCaretEnd();
        appendText(command);
        executeCommand(command);
    }

    private void executeCommand(String command) {
        if (!command.trim().isEmpty()) {
            getHistory().add(command);
        }
        if (this.interpreter == null) {
            appendText("\rInterpreter is unavailable\r");
        } else {
            ExecutionResult result = this.interpreter.executeCommand(command);
            String msg = "\r";
            if (result.getCode() != 0) {
                msg = msg + String.format("%d: ", new Object[]{Integer.valueOf(result.getCode())});
                if (!Strings.isBlank(result.getMessage())) {
                    msg = msg + Strings.rtrim(result.getMessage());
                } else {
                    msg = msg + "An error occurred";
                }
                msg = msg + String.format(" (\"%s\")", new Object[]{command});
                msg = msg + "\r";
            } else if (result.getMessage() != null) {
                msg = msg + Strings.rtrim(result.getMessage());
                if (!Strings.isBlank(msg)) {
                    msg = msg + "\r";
                }
            }
            appendText(msg);
        }
        generatePromp();
    }

    private void appendText(String text) {
        this.console.append(text);
    }

    private void executeAutoComplete(AutocompletionResult auto) {
        String userInput = getCurrentUserInput();
        if (auto.getAutocompletes().size() > 1) {
            StringBuilder msg = new StringBuilder();
            msg.append("\r");
            int maxSize = 0;
            for (String tok : auto.getAutocompletes()) {
                maxSize = Math.max(maxSize, tok.length());
            }
            maxSize += 4;
            int perLine = 80;
            int currentLine = 0;
            for (String tok : auto.getAutocompletes()) {
                if (currentLine + maxSize > perLine) {
                    msg.append('\r');
                    currentLine = 0;
                }
                msg.append(String.format("%-" + maxSize + "s", new Object[]{tok}));
                currentLine += maxSize;
            }
            msg.append('\r');
            appendText(msg.toString());
            generatePromp();
            int from = userInput.lastIndexOf(auto.getLastSeparator());
            if (from == -1) {
                appendText(userInput);
            } else {
                appendText(userInput.substring(0, from + 1));
                appendText(getCommon(auto.getAutocompletes()));
            }
        } else {
            int from = userInput.lastIndexOf(auto.getLastSeparator()) + 1;
            String autocompl = (String) auto.getAutocompletes().get(0);
            int fromAuto = userInput.length() - from;
            appendText(autocompl.substring(fromAuto));
        }
        setCaretEnd();
        showInputLine();
    }

    private String getCommon(List<String> autocompletes) {
        String reference = (String) autocompletes.get(0);
        for (int i = 1; i < autocompletes.size(); i++) {
            String comparison = (String) autocompletes.get(i);
            int commonLength = getCommonLength(reference, comparison);
            if (reference.length() > commonLength) {
                reference = reference.substring(0, commonLength);
            }
            if (commonLength == 0) {
                return reference;
            }
        }
        return reference;
    }

    private int getCommonLength(String reference, String comparison) {
        for (int i = 0; (i < reference.length()) && (i < comparison.length()); i++) {
            if (reference.charAt(i) != comparison.charAt(i)) {
                return i;
            }
        }
        return 0;
    }

    private void replaceSelection(String text) {
        int offset = -1;
        if (this.console.getSelectionCount() == 0) {
            offset = this.console.getCaretOffset() + text.length();
        }
        this.console.insert(text);
        if (offset >= 0) {
            this.console.setCaretOffset(offset);
        }
    }

    private void removeTrailingCharacters(int count) {
        String text = this.console.getText();
        this.console.replaceTextRange(text.length() - count, count, "");
    }

    private void setText(String text, boolean show) {
        this.console.setText(text);
        if (show) {
            showInputLine();
        }
    }

    private void showInputLine() {
        this.console.setTopIndex(this.console.getLineCount() - 1);
    }

    private void setCaretBegin() {
        int offset = this.console.getCaretOffset();
        int lineIndex = this.console.getLineAtOffset(offset);
        this.console.setCaretOffset(this.console.getOffsetAtLine(lineIndex) + getPrompt().length());
    }

    private int getFirstValidCaretPosition() {
        return this.console.getOffsetAtLine(this.console.getLineCount() - 1) + getPrompt().length();
    }

    private boolean isValidCaret() {
        int offset = this.console.getCaretOffset();
        if (offset == this.console.getText().length()) {
            return true;
        }
        int lineIndex = this.console.getLineAtOffset(offset);
        if (lineIndex != this.console.getLineCount() - 1) {
            return false;
        }
        int delta = offset - this.console.getOffsetAtLine(lineIndex);
        return delta >= getPrompt().length();
    }

    private void setCaretEnd() {
        this.console.setCaretOffset(this.console.getText().length());
    }

    public Control getControl() {
        return this.console;
    }

    public void setInput(Object input) {
        if (!(input instanceof ICommandInterpreter)) {
            throw new IllegalArgumentException();
        }
        setInterpreter((ICommandInterpreter) input);
    }

    public ICommandInterpreter getInput() {
        return this.interpreter;
    }

    public void refresh() {
    }

    public void setSelection(ISelection selection, boolean reveal) {
    }

    public ISelection getSelection() {
        return null;
    }

    public boolean verifyOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case SELECT_ALL:
                return true;
            case COPY:
                return this.console.isTextSelected();
            case CLEAR:
                return true;
            case FIND:
                return true;
            case FIND_NEXT:
                return this.finder.isReady();
        }
        return false;
    }

    public boolean doOperation(OperationRequest req) {
        switch (req.getOperation()) {
            case SELECT_ALL:
                this.console.selectAll();
                return true;
            case COPY:
                this.console.copy();
                return true;
            case CLEAR:
                clearConsole();
                return true;
            case FIND:
                String selectedText = this.console.getSelectionText();
                if (selectedText.length() > 0) {
                    this.findimpl.getFindTextOptions(false).setSearchString(selectedText);
                }
                FindTextDialog dlg = new FindTextDialog(this.console.getShell(), this.finder, null);
                dlg.open();
                return true;
            case FIND_NEXT:
                this.finder.search(null);
                return true;
        }
        return false;
    }

    public ItemHistory<String> getHistory() {
        return this.interpreter.getHistory();
    }
}


