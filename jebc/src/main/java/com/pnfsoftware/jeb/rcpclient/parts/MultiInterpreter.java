/*     */
package com.pnfsoftware.jeb.rcpclient.parts;
/*     */
/*     */

import com.pnfsoftware.jeb.core.units.AutocompletionResult;
/*     */ import com.pnfsoftware.jeb.core.units.ExecutionResult;
/*     */ import com.pnfsoftware.jeb.core.units.ICommandInterpreter;
/*     */ import com.pnfsoftware.jeb.core.units.IUnit;
/*     */ import com.pnfsoftware.jeb.core.units.IUnitInterpreter;
/*     */ import com.pnfsoftware.jeb.util.collect.ItemHistory;
/*     */ import com.pnfsoftware.jeb.util.encoding.Conversion;
/*     */ import com.pnfsoftware.jeb.util.events.Event;
/*     */ import com.pnfsoftware.jeb.util.events.EventSource;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
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
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class MultiInterpreter
        /*     */ extends EventSource
        /*     */ implements ICommandInterpreter
        /*     */ {
    /*     */   public static final int EVENT_INTERPRETER_CHANGE = 0;
    /*     */   public static final int EVENT_INTERPRETER_CHANGE_IMMEDIATE = 1;
    /*  40 */ List<ICommandInterpreter> interpreters = new ArrayList();
    /*  41 */   private ItemHistory<String> history = new ItemHistory(false);
    /*  42 */ int primary = -1;
    /*     */
    /*     */   private List<String> autocompleteTokens;

    /*     */
    /*     */
    public int registerInterpreter(ICommandInterpreter inter)
    /*     */ {
        /*  48 */
        if (inter == null) {
            /*  49 */
            throw new IllegalArgumentException();
            /*     */
        }
        /*     */
        /*  52 */
        if (this.interpreters.contains(inter)) {
            /*  53 */
            return this.interpreters.indexOf(inter);
            /*     */
        }
        /*     */
        /*  56 */
        int index = this.interpreters.size();
        /*  57 */
        this.interpreters.add(inter);
        /*  58 */
        return index;
        /*     */
    }

    /*     */
    /*     */
    public void unregisterInterpreter(int index) {
        /*  62 */
        if ((index < 0) || (index >= this.interpreters.size())) {
            /*  63 */
            return;
            /*     */
        }
        /*     */
        /*  66 */
        unregisterInterpreter((ICommandInterpreter) this.interpreters.get(index));
        /*     */
    }

    /*     */
    /*     */
    public void unregisterInterpreter(ICommandInterpreter inter) {
        /*  70 */
        if (!this.interpreters.contains(inter)) {
            /*  71 */
            return;
            /*     */
        }
        /*     */
        /*  74 */
        int index = this.interpreters.indexOf(inter);
        /*  75 */
        if (index == this.primary) {
            /*  76 */
            setPrimary(-1);
            /*     */
        }
        /*     */
        /*  79 */
        this.interpreters.set(index, null);
        /*     */
    }

    /*     */
    /*     */
    public String getName()
    /*     */ {
        /*  84 */
        ICommandInterpreter interpreter = getRealInterpreter();
        /*  85 */
        if (interpreter == this) {
            /*  86 */
            return "";
            /*     */
        }
        /*     */
        /*  89 */
        return interpreter.getName();
        /*     */
    }

    /*     */
    /*     */
    public ExecutionResult executeCommand(String command)
    /*     */ {
        /*  94 */
        command = command.trim();
        /*     */
        /*  96 */
        if (command.equals("exit")) {
            /*  97 */
            setPrimary(-1);
            /*  98 */
            return ExecutionResult.GENERIC_SUCCESS;
            /*     */
        }
        /*     */
        /* 101 */
        if ((this.primary >= 0) && (!command.startsWith("."))) {
            /* 102 */
            ICommandInterpreter inter = getRealInterpreter();
            /* 103 */
            if (inter == null) {
                /* 104 */
                return ExecutionResult.error("Illegal interpreter");
                /*     */
            }
            /*     */
            /* 107 */
            return inter.executeCommand(command);
            /*     */
        }
        /*     */
        /* 110 */
        if (command.startsWith(".")) {
            /* 111 */
            command = command.substring(1);
            /*     */
        }
        /* 113 */
        String[] tokens = command.split("\\s+");
        /*     */
        /*     */
        /* 116 */
        if (command.equals("help"))
            /*     */ {
            /* 118 */
            String msg = "help              : help about the master interpreter\nlist              : display a list of available console ports\nuse <interpreter> : set the active console associated with this display; -1 for master\nexit              : exit the current interpreter and return to the master interpreter";
            /*     */
            /*     */
            /*     */
            /*     */
            /*     */
            /* 124 */
            return ExecutionResult.success(msg);
            /*     */
        }
        /* 126 */
        if (command.equals("list")) {
            /* 127 */
            StringBuilder sb = new StringBuilder();
            /* 128 */
            int cnt = getInterpreterCount();
            /* 129 */
            int i;
            if (cnt == 0) {
                /* 130 */
                sb.append("No interpreter is available at the moment.");
                /*     */
            }
            /*     */
            else {
                /* 133 */
                sb.append(String.format("%d interpreter%s available", new Object[]{Integer.valueOf(cnt), cnt >= 2 ? "s" : ""}));
                /* 134 */
                i = 0;
                /* 135 */
                for (ICommandInterpreter inter : this.interpreters) {
                    /* 136 */
                    if (inter != null) {
                        /* 137 */
                        sb.append(String.format("\n- %d: %s", new Object[]{Integer.valueOf(i), inter.getName()}));
                        /*     */
                    }
                    /* 139 */
                    i++;
                    /*     */
                }
                /*     */
            }
            /* 142 */
            return ExecutionResult.success(sb.toString());
            /*     */
        }
        /* 144 */
        if ((tokens.length >= 1) && (tokens[0].equals("use"))) {
            /* 145 */
            if (tokens.length != 2) {
                /* 146 */
                return ExecutionResult.error("Specify a console port");
                /*     */
            }
            /*     */
            /* 149 */
            int index = Conversion.stringToInt(tokens[1], -1);
            /* 150 */
            if (index < 0) {
                /* 151 */
                index = findInterpreterIndexByName(tokens[1], false);
                /*     */
            }
            /*     */
            /* 154 */
            if (index >= this.interpreters.size()) {
                /* 155 */
                return ExecutionResult.error("Invalid console port");
                /*     */
            }
            /*     */
            /* 158 */
            setPrimary(index);
            /*     */
        }
        /* 160 */
        else if (command.equals("exit")) {
            /* 161 */
            setPrimary(-1);
            /*     */
        }
        /*     */
        else {
            /* 164 */
            return ExecutionResult.error("Unknown command");
            /*     */
        }
        /*     */
        /* 167 */
        return ExecutionResult.GENERIC_SUCCESS;
        /*     */
    }

    /*     */
    /*     */   int findInterpreterIndexByName(String name, boolean allowAmbiguity) {
        /* 171 */
        int candidate = -1;
        /* 172 */
        int i = 0;
        /* 173 */
        for (ICommandInterpreter inter : this.interpreters) {
            /* 174 */
            if ((inter != null) && (inter.getName().contains(name))) {
                /* 175 */
                if (allowAmbiguity) {
                    /* 176 */
                    return i;
                    /*     */
                }
                /* 178 */
                if (candidate >= 0) {
                    /* 179 */
                    return -1;
                    /*     */
                }
                /* 181 */
                candidate = i;
                /*     */
            }
            /* 183 */
            i++;
            /*     */
        }
        /* 185 */
        return candidate;
        /*     */
    }

    /*     */
    /*     */   ICommandInterpreter findInterpreterByName(String name, boolean allowAmbiguity) {
        /* 189 */
        int index = findInterpreterIndexByName(name, allowAmbiguity);
        /* 190 */
        if (index < 0) {
            /* 191 */
            return null;
            /*     */
        }
        /*     */
        /* 194 */
        return (ICommandInterpreter) this.interpreters.get(index);
        /*     */
    }

    /*     */
    /*     */   void setPrimary(int index) {
        /* 198 */
        setPrimary(index, false);
        /*     */
    }

    /*     */
    /*     */   void setPrimary(int index, boolean immediate) {
        /* 202 */
        if ((index < 0) || (index >= this.interpreters.size()) || (this.interpreters.get(index) == null)) {
            /* 203 */
            index = -1;
            /*     */
        }
        /*     */
        /* 206 */
        this.primary = index;
        /*     */
        /* 208 */
        notifyListeners(new Event(immediate ? 1 : 0));
        /*     */
    }

    /*     */
    /*     */   ICommandInterpreter getRealInterpreter() {
        /* 212 */
        if (this.primary == -1) {
            /* 213 */
            return this;
            /*     */
        }
        /*     */
        /* 216 */
        if ((this.primary < 0) || (this.primary >= this.interpreters.size())) {
            /* 217 */
            return null;
            /*     */
        }
        /*     */
        /* 220 */
        return (ICommandInterpreter) this.interpreters.get(this.primary);
        /*     */
    }

    /*     */
    /*     */   int getInterpreterCount() {
        /* 224 */
        int i = 0;
        /* 225 */
        for (ICommandInterpreter inter : this.interpreters) {
            /* 226 */
            if (inter != null) {
                /* 227 */
                i++;
                /*     */
            }
            /*     */
        }
        /* 230 */
        return i;
        /*     */
    }

    /*     */
    /*     */
    public ItemHistory<String> getHistory()
    /*     */ {
        /* 235 */
        if (this.primary == -1) {
            /* 236 */
            return this.history;
            /*     */
        }
        /* 238 */
        return getRealInterpreter().getHistory();
        /*     */
    }

    /*     */
    /*     */
    public boolean onFocusChanged(IUnit unit) {
        /* 242 */
        for (int i = 0; i < this.interpreters.size(); i++) {
            /* 243 */
            ICommandInterpreter interpreter = (ICommandInterpreter) this.interpreters.get(i);
            /* 244 */
            if (((interpreter instanceof IUnitInterpreter)) &&
                    /* 245 */         (((IUnitInterpreter) interpreter).isTarget(unit))) {
                /* 246 */
                setPrimary(i, true);
                /* 247 */
                return true;
                /*     */
            }
            /*     */
        }
        /*     */
        /* 251 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    public AutocompletionResult autoComplete(String command)
    /*     */ {
        /* 258 */
        if ((this.primary >= 0) && (!command.startsWith(".")))
            /*     */ {
            /* 260 */
            ICommandInterpreter inter = getRealInterpreter();
            /* 261 */
            if (inter == null) {
                /* 262 */
                return null;
                /*     */
            }
            /* 264 */
            return inter.autoComplete(command);
            /*     */
        }
        /*     */
        /* 267 */
        char lastSeparator = ' ';
        /* 268 */
        boolean dot = false;
        /* 269 */
        if (command.startsWith(".")) {
            /* 270 */
            command = command.substring(1);
            /* 271 */
            dot = true;
            /*     */
        }
        /*     */
        /* 274 */
        if (this.autocompleteTokens == null) {
            /* 275 */
            this.autocompleteTokens = new ArrayList(Arrays.asList(new String[]{"help", "list", "use", "exit"}));
            /*     */
        }
        /* 277 */
        List<String> coms = new ArrayList(Arrays.asList(command.split(" ")));
        /* 278 */
        if (command.endsWith(" ")) {
            /* 279 */
            coms.add("");
            /*     */
        }
        /* 281 */
        if ((dot) && (coms.size() == 1))
            /*     */ {
            /* 283 */
            lastSeparator = '.';
            /*     */
        }
        /* 285 */
        if (coms.size() <= 1) {
            /* 286 */
            return new AutocompletionResult(AutocompletionResult.filterStartsWith(command, this.autocompleteTokens), lastSeparator);
            /*     */
        }
        /*     */
        /* 289 */
        if (((String) coms.get(0)).equals("use")) {
            /* 290 */
            List<String> tokens = new ArrayList();
            /* 291 */
            for (int i = 0; i < this.interpreters.size(); i++) {
                /* 292 */
                tokens.add(String.valueOf(i));
                /*     */
            }
            /* 294 */
            return new AutocompletionResult(tokens, lastSeparator);
            /*     */
        }
        /*     */
        /* 297 */
        return null;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\parts\MultiInterpreter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */