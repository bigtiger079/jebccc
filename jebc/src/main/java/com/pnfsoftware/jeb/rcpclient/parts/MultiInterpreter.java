package com.pnfsoftware.jeb.rcpclient.parts;

import com.pnfsoftware.jeb.core.units.AutocompletionResult;
import com.pnfsoftware.jeb.core.units.ExecutionResult;
import com.pnfsoftware.jeb.core.units.ICommandInterpreter;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.core.units.IUnitInterpreter;
import com.pnfsoftware.jeb.util.collect.ItemHistory;
import com.pnfsoftware.jeb.util.encoding.Conversion;
import com.pnfsoftware.jeb.util.events.Event;
import com.pnfsoftware.jeb.util.events.EventSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiInterpreter extends EventSource implements ICommandInterpreter {
    public static final int EVENT_INTERPRETER_CHANGE = 0;
    public static final int EVENT_INTERPRETER_CHANGE_IMMEDIATE = 1;
    List<ICommandInterpreter> interpreters = new ArrayList<>();
    private ItemHistory<String> history = new ItemHistory(false);
    int primary = -1;
    private ArrayList autocompleteTokens;

    public int registerInterpreter(ICommandInterpreter inter) {
        if (inter == null) {
            throw new IllegalArgumentException();
        }
        if (this.interpreters.contains(inter)) {
            return this.interpreters.indexOf(inter);
        }
        int index = this.interpreters.size();
        this.interpreters.add(inter);
        return index;
    }

    public void unregisterInterpreter(int index) {
        if ((index < 0) || (index >= this.interpreters.size())) {
            return;
        }
        unregisterInterpreter(this.interpreters.get(index));
    }

    public void unregisterInterpreter(ICommandInterpreter inter) {
        if (!this.interpreters.contains(inter)) {
            return;
        }
        int index = this.interpreters.indexOf(inter);
        if (index == this.primary) {
            setPrimary(-1);
        }
        this.interpreters.set(index, null);
    }

    public String getName() {
        ICommandInterpreter interpreter = getRealInterpreter();
        if (interpreter == this) {
            return "";
        }
        return interpreter.getName();
    }

    public ExecutionResult executeCommand(String command) {
        command = command.trim();
        if (command.equals("exit")) {
            setPrimary(-1);
            return ExecutionResult.GENERIC_SUCCESS;
        }
        if ((this.primary >= 0) && (!command.startsWith("."))) {
            ICommandInterpreter inter = getRealInterpreter();
            if (inter == null) {
                return ExecutionResult.error("Illegal interpreter");
            }
            return inter.executeCommand(command);
        }
        if (command.startsWith(".")) {
            command = command.substring(1);
        }
        String[] tokens = command.split("\\s+");
        if (command.equals("help")) {
            String msg = "help              : help about the master interpreter\nlist              : display a list of available console ports\nuse <interpreter> : set the active console associated with this display; -1 for master\nexit              : exit the current interpreter and return to the master interpreter";
            return ExecutionResult.success(msg);
        }
        if (command.equals("list")) {
            StringBuilder sb = new StringBuilder();
            int cnt = getInterpreterCount();
            int i;
            if (cnt == 0) {
                sb.append("No interpreter is available at the moment.");
            } else {
                sb.append(String.format("%d interpreter%s available", cnt, cnt >= 2 ? "s" : ""));
                i = 0;
                for (ICommandInterpreter inter : this.interpreters) {
                    if (inter != null) {
                        sb.append(String.format("\n- %d: %s", i, inter.getName()));
                    }
                    i++;
                }
            }
            return ExecutionResult.success(sb.toString());
        }
        if ((tokens.length >= 1) && (tokens[0].equals("use"))) {
            if (tokens.length != 2) {
                return ExecutionResult.error("Specify a console port");
            }
            int index = Conversion.stringToInt(tokens[1], -1);
            if (index < 0) {
                index = findInterpreterIndexByName(tokens[1], false);
            }
            if (index >= this.interpreters.size()) {
                return ExecutionResult.error("Invalid console port");
            }
            setPrimary(index);
        } else if (command.equals("exit")) {
            setPrimary(-1);
        } else {
            return ExecutionResult.error("Unknown command");
        }
        return ExecutionResult.GENERIC_SUCCESS;
    }

    int findInterpreterIndexByName(String name, boolean allowAmbiguity) {
        int candidate = -1;
        int i = 0;
        for (ICommandInterpreter inter : this.interpreters) {
            if ((inter != null) && (inter.getName().contains(name))) {
                if (allowAmbiguity) {
                    return i;
                }
                if (candidate >= 0) {
                    return -1;
                }
                candidate = i;
            }
            i++;
        }
        return candidate;
    }

    ICommandInterpreter findInterpreterByName(String name, boolean allowAmbiguity) {
        int index = findInterpreterIndexByName(name, allowAmbiguity);
        if (index < 0) {
            return null;
        }
        return this.interpreters.get(index);
    }

    void setPrimary(int index) {
        setPrimary(index, false);
    }

    void setPrimary(int index, boolean immediate) {
        if ((index < 0) || (index >= this.interpreters.size()) || (this.interpreters.get(index) == null)) {
            index = -1;
        }
        this.primary = index;
        notifyListeners(new Event(immediate ? 1 : 0));
    }

    ICommandInterpreter getRealInterpreter() {
        if (this.primary == -1) {
            return this;
        }
        if ((this.primary < 0) || (this.primary >= this.interpreters.size())) {
            return null;
        }
        return this.interpreters.get(this.primary);
    }

    int getInterpreterCount() {
        int i = 0;
        for (ICommandInterpreter inter : this.interpreters) {
            if (inter != null) {
                i++;
            }
        }
        return i;
    }

    public ItemHistory<String> getHistory() {
        if (this.primary == -1) {
            return this.history;
        }
        return getRealInterpreter().getHistory();
    }

    public boolean onFocusChanged(IUnit unit) {
        for (int i = 0; i < this.interpreters.size(); i++) {
            ICommandInterpreter interpreter = this.interpreters.get(i);
            if (((interpreter instanceof IUnitInterpreter)) && (((IUnitInterpreter) interpreter).isTarget(unit))) {
                setPrimary(i, true);
                return true;
            }
        }
        return false;
    }

    public AutocompletionResult autoComplete(String command) {
        if ((this.primary >= 0) && (!command.startsWith("."))) {
            ICommandInterpreter inter = getRealInterpreter();
            if (inter == null) {
                return null;
            }
            return inter.autoComplete(command);
        }
        char lastSeparator = ' ';
        boolean dot = false;
        if (command.startsWith(".")) {
            command = command.substring(1);
            dot = true;
        }
        if (this.autocompleteTokens == null) {
            this.autocompleteTokens = new ArrayList<>(Arrays.asList("help", "list", "use", "exit"));
        }
        List<String> coms = new ArrayList<>(Arrays.asList(command.split(" ")));
        if (command.endsWith(" ")) {
            coms.add("");
        }
        if ((dot) && (coms.size() == 1)) {
            lastSeparator = '.';
        }
        if (coms.size() <= 1) {
            return new AutocompletionResult(AutocompletionResult.filterStartsWith(command, this.autocompleteTokens), lastSeparator);
        }
        if (coms.get(0).equals("use")) {
            List<String> tokens = new ArrayList<>();
            for (int i = 0; i < this.interpreters.size(); i++) {
                tokens.add(String.valueOf(i));
            }
            return new AutocompletionResult(tokens, lastSeparator);
        }
        return null;
    }
}


