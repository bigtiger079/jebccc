/*     */
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;
/*     */
/*     */

import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
/*     */ import com.pnfsoftware.jeb.core.output.text.IAnchor;
/*     */ import com.pnfsoftware.jeb.core.output.text.ILine;
/*     */ import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.AbstractTextDocument;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.Anchor;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.Line;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.TextDocumentPart;
/*     */ import com.pnfsoftware.jeb.core.output.text.impl.TextItem;
/*     */ import com.pnfsoftware.jeb.core.units.code.IEntryPointDescription;
/*     */ import com.pnfsoftware.jeb.core.units.code.IFlowInformation;
/*     */ import com.pnfsoftware.jeb.core.units.code.IInstruction;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.memory.IVirtualMemory;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.processor.IRegisterBank;
/*     */ import com.pnfsoftware.jeb.core.units.code.asm.processor.ProcessorException;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerTargetInformation;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
/*     */ import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerVirtualMemory;
/*     */ import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
/*     */ import com.pnfsoftware.jeb.util.format.Formatter;
/*     */ import com.pnfsoftware.jeb.util.logging.GlobalLog;
/*     */ import com.pnfsoftware.jeb.util.logging.ILogger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import org.apache.commons.lang3.BooleanUtils;

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
/*     */ public class DbgCodeDocument
        /*     */ extends AbstractTextDocument
        /*     */ {
    /*  53 */   private static final ILogger logger = GlobalLog.getLogger(DbgCodeDocument.class);
    /*     */
    /*     */
    /*     */   private static final int maxBytesPerLine = 16;
    /*     */
    /*     */   private IDebuggerUnit unit;
    /*     */
    /*  60 */   private TreeMap<Long, IInstruction> insns = new TreeMap();
    /*     */
    /*     */
    /*  63 */   private TreeMap<Long, Boolean> pcInsns = new TreeMap();
    /*     */
    /*  65 */   private TreeSet<Long> unreachableAddresses = new TreeSet();
    /*     */
    /*  67 */   private Chunk chunk = new Chunk(null);

    /*     */
    /*     */   static enum ViewType {
        /*  70 */     MEMORY, CODE;

        /*     */
        /*     */
        private ViewType() {
        }
    }

    /*  73 */   private ViewType viewType = ViewType.CODE;

    /*     */
    /*     */
    /*     */
    /*     */   private class Chunk
            /*     */ {
        /*     */     private byte[] data;
        /*     */
        /*     */
        /*  82 */     private int minRead = 0;
        /*     */     private int read;
        /*     */ long firstAddress;

        /*     */
        /*     */
        private Chunk() {
        }

        /*     */
        /*     */     void update(long anchorId, int linesAfter, int linesBefore) {
            /*  89 */
            int memspace = DbgCodeDocument.this.unit.getMemory().getSpaceBits();
            /*     */
            /*  91 */
            this.firstAddress = (anchorId - linesBefore * 16 & 0xFFFFFFFFFFFFFFF0);
            /*  92 */
            if (this.firstAddress < 0L) {
                /*  93 */
                this.firstAddress = 0L;
                /*     */
            }
            /*     */
            /*  96 */
            long longSize = (linesBefore + linesAfter + 1L) * 16L;
            /*     */
            /*  98 */
            int size = (int) Math.min(longSize, 1048576L);
            /*     */
            /*     */
            /* 101 */
            if (memspace <= 32) {
                /* 102 */
                if (this.firstAddress + size > 4294967295L)
                    /*     */ {
                    /* 104 */
                    if (this.firstAddress > 4294967295L) {
                        /* 105 */
                        size = 0;
                        /*     */
                    }
                    /*     */
                    else {
                        /* 108 */
                        size = (int) (4294967296L - this.firstAddress);
                        /*     */
                    }
                    /*     */
                }
                /*     */
            }
            /* 112 */
            else if ((memspace <= 64) &&
                    /* 113 */         (this.firstAddress + size > Long.MAX_VALUE))
                /*     */ {
                /* 115 */
                if (this.firstAddress > Long.MAX_VALUE) {
                    /* 116 */
                    size = 0;
                    /*     */
                }
                /*     */
                else {
                    /* 119 */
                    size = (int) (Long.MIN_VALUE - this.firstAddress);
                    /*     */
                }
                /*     */
            }
            /*     */
            /* 123 */
            this.data = new byte[size];
            /* 124 */
            this.minRead = 0;
            /* 125 */
            this.read = DbgCodeDocument.this.unit.readMemory(this.firstAddress, size, this.data, 0);
            /* 126 */
            if (this.read == -1)
                /*     */ {
                /*     */
                /* 129 */
                this.minRead = toRelative(anchorId & 0xFFFFFFFFFFFFFFF0);
                /* 130 */
                if (size - this.minRead > 0) {
                    /* 131 */
                    this.read = DbgCodeDocument.this.unit.readMemory(this.firstAddress + this.minRead, size - this.minRead, this.data, 0);
                    /* 132 */
                    if (this.read == -1) {
                        /* 133 */
                        this.minRead = 0;
                        /*     */
                    }
                    /* 135 */
                    this.read += this.minRead;
                    /*     */
                }
                /*     */
                else {
                    /* 138 */
                    this.minRead = 0;
                    /*     */
                }
                /*     */
            }
            /*     */
        }

        /*     */
        /*     */     boolean isReachable(long address) {
            /* 144 */
            return (address >= this.firstAddress + this.minRead) && (address < this.firstAddress + this.read);
            /*     */
        }

        /*     */
        /*     */     boolean isReachableRel(int address) {
            /* 148 */
            return isReachable(this.firstAddress + address);
            /*     */
        }

        /*     */
        /*     */     int toRelative(long address) {
            /* 152 */
            return (int) (address - this.firstAddress);
            /*     */
        }

        /*     */
        /*     */     long toAbsolute(int address) {
            /* 156 */
            return address + this.firstAddress;
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public DbgCodeDocument(IDebuggerUnit unit) {
        /* 161 */
        if (unit == null) {
            /* 162 */
            throw new IllegalArgumentException("Can not build a DbgCodeDocument with a null unit");
            /*     */
        }
        /* 164 */
        this.unit = unit;
        /*     */
    }

    /*     */
    /*     */
    public void removeInsn(long anchorId)
    /*     */ {
        /* 169 */
        this.insns.remove(Long.valueOf(anchorId));
        /*     */
    }

    /*     */
    /*     */
    public boolean hasInsnAt(long anchorId)
    /*     */ {
        /* 174 */
        return this.insns.containsKey(Long.valueOf(anchorId));
        /*     */
    }

    /*     */
    /*     */
    public Long getNextInsnAddress(long anchorId) {
        /* 178 */
        return (Long) this.insns.higherKey(Long.valueOf(anchorId));
        /*     */
    }

    /*     */
    /*     */
    public long getInsnAddressAt(long anchorId, int columnOffset) {
        /* 182 */
        int addressPrefixLength = formatAddress(anchorId).length();
        /* 183 */
        if (columnOffset < addressPrefixLength)
            /*     */ {
            /* 185 */
            return anchorId;
            /*     */
        }
        /* 187 */
        long maxOffset = anchorId % 16L;
        /* 188 */
        maxOffset = maxOffset == 0L ? 16L : maxOffset;
        /* 189 */
        Long nextAddress = getNextInsnAddress(anchorId);
        /* 190 */
        if (nextAddress != null) {
            /* 191 */
            maxOffset = Math.min(nextAddress.longValue() - anchorId, maxOffset);
            /*     */
        }
        /* 193 */
        int offset = (columnOffset - addressPrefixLength) / 3;
        /* 194 */
        if (offset < maxOffset) {
            /* 195 */
            return anchorId + offset;
            /*     */
        }
        /* 197 */
        return anchorId;
        /*     */
    }

    /*     */
    /*     */
    public IInstruction getInsnAt(long address) {
        /* 201 */
        return (IInstruction) this.insns.get(Long.valueOf(address));
        /*     */
    }

    /*     */
    /*     */
    public boolean forceInsnAt(long address) {
        /* 205 */
        return forceInsnAt(address, 0);
        /*     */
    }

    /*     */
    /*     */
    public boolean forceInsnAt(long address, int procMode) {
        /* 209 */
        IProcessor<? extends IInstruction> proc = this.unit.getProcessor();
        /* 210 */
        int mode = proc.getMode();
        /* 211 */
        created = false;
        /*     */
        try {
            /* 213 */
            if (procMode == 0) {
                /* 214 */
                proc.setMode(proc.getDefaultMode());
                /*     */
            }
            /*     */
            else {
                /* 217 */
                proc.setMode(procMode);
                /*     */
            }
            /*     */
            /*     */
            /* 221 */
            int alignment = proc.getInstructionAlignment();
            /* 222 */
            address = address / alignment * alignment;
            /*     */
            /* 224 */
            return updateInstructions(proc, this.chunk.toRelative(address), this.chunk.read, false);
            /*     */
        }
        /*     */ catch (ProcessorException e)
            /*     */ {
            /* 228 */
            logger.warning("Can not set mode %d to processor %s", new Object[]{Integer.valueOf(procMode), this.unit
/* 229 */.getTargetInformation().getProcessorType()});
            /* 230 */
            return false;
            /*     */
        }
        /*     */ finally {
            /*     */
            try {
                /* 234 */
                proc.setMode(mode);
                /*     */
            }
            /*     */ catch (ProcessorException e)
                /*     */ {
                /* 238 */
                logger.error("Inner error: Mode %d can not be restored", new Object[]{Integer.valueOf(mode)});
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public long getAnchorCount()
    /*     */ {
        /* 246 */
        IDebuggerTargetInformation targetInfo = this.unit.getTargetInformation();
        /* 247 */
        if ((targetInfo == null) || (targetInfo.getProcessorType() == null) || (targetInfo.getProcessorType().is64Bit())) {
            /* 248 */
            return Long.MAX_VALUE;
            /*     */
        }
        /* 250 */
        return 4294967295L;
        /*     */
    }

    /*     */
    /*     */
    private boolean addInstruction(long address, IInstruction insn, boolean usePC) {
        /* 254 */
        SortedMap<Long, IInstruction> collisions = this.insns.subMap(Long.valueOf(address), Long.valueOf(address + insn.getSize()));
        /* 255 */
        Long previousInsnAddress = (Long) this.insns.floorKey(Long.valueOf(address - 1L));
        /* 256 */
        if ((previousInsnAddress != null) && (previousInsnAddress.longValue() + ((IInstruction) this.insns.get(previousInsnAddress)).getSize() > address))
            /*     */ {
            /* 258 */
            collisions = this.insns.subMap(previousInsnAddress, Long.valueOf(address + insn.getSize()));
            /*     */
        }
        /* 260 */
        if (collisions.isEmpty())
            /*     */ {
            /* 262 */
            this.insns.put(Long.valueOf(address), insn);
            /* 263 */
            if (usePC) {
                /* 264 */
                this.pcInsns.put(Long.valueOf(address), Boolean.valueOf(usePC));
                /*     */
            }
            /* 266 */
            return true;
            /*     */
        }
        /*     */
        /*     */
        /* 270 */
        if (usePC)
            /*     */ {
            /*     */
            /* 273 */
            List<Long> toRemove = new ArrayList();
            /* 274 */
            for (Map.Entry<Long, IInstruction> insnEntry : collisions.entrySet()) {
                /* 275 */
                if (BooleanUtils.toBoolean((Boolean) this.pcInsns.get(insnEntry.getKey())))
                    /*     */ {
                    /* 277 */
                    toRemove.clear();
                    /* 278 */
                    break;
                    /*     */
                }
                /*     */
                /* 281 */
                toRemove.add(insnEntry.getKey());
                /*     */
            }
            /*     */
            /* 284 */
            if (!toRemove.isEmpty())
                /*     */ {
                /*     */
                /*     */
                /* 288 */
                for (Long removed : toRemove) {
                    /* 289 */
                    this.insns.remove(removed);
                    /* 290 */
                    this.pcInsns.remove(removed);
                    /*     */
                }
                /* 292 */
                this.insns.put(Long.valueOf(address), insn);
                /* 293 */
                this.pcInsns.put(Long.valueOf(address), Boolean.valueOf(usePC));
                /* 294 */
                return true;
                /*     */
            }
            /*     */
        }
        /*     */
        /*     */
        /*     */
        /*     */
        /* 301 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private boolean updateInstructions(IProcessor<? extends IInstruction> proc, int from, int size, boolean usePC) {
        /* 305 */
        if (size <= 0) {
            /* 306 */
            return false;
            /*     */
        }
        /*     */
        /* 309 */
        SortedMap<Long, IInstruction> subInsns = this.insns.subMap(Long.valueOf(this.chunk.toAbsolute(0)), Long.valueOf(this.chunk.toAbsolute(size)));
        /* 310 */
        List<Long> toRemove = new ArrayList();
        /* 311 */
        for (Map.Entry<Long, IInstruction> insnEntry : subInsns.entrySet()) {
            /* 312 */
            relativeAddress = this.chunk.toRelative(((Long) insnEntry.getKey()).longValue());
            /* 313 */
            if (memoryChanged(((IInstruction) insnEntry.getValue()).getCode(), relativeAddress))
                /* 314 */ toRemove.add(insnEntry.getKey());
            /*     */
        }
        /*     */
        int relativeAddress;
        /* 317 */
        for (Long removed : toRemove) {
            /* 318 */
            this.insns.remove(removed);
            /* 319 */
            this.pcInsns.remove(removed);
            /*     */
        }
        /*     */
        /*     */
        /* 323 */
        Object subAdresses = this.unreachableAddresses.subSet(Long.valueOf(this.chunk.toAbsolute(from)), Long.valueOf(this.chunk.toAbsolute(size)));
        /*     */
        /* 325 */
        Set<Long> subAddressesCopy = new TreeSet((SortedSet) subAdresses);
        /* 326 */
        for (Long addr : subAddressesCopy) {
            /* 327 */
            processBlock(proc, this.chunk.toRelative(addr.longValue()), size, usePC);
            /*     */
        }
        /* 329 */
        ((SortedSet) subAdresses).clear();
        /*     */
        /* 331 */
        return processBlock(proc, from, size, usePC);
        /*     */
    }

    /*     */
    /*     */
    private boolean processBlock(IProcessor<? extends IInstruction> proc, int from, int size, boolean usePC)
    /*     */ {
        /* 336 */
        int currentAddress = from;
        /* 337 */
        boolean created = false;
        /*     */
        try {
            /* 339 */
            while (currentAddress < size) {
                /* 340 */
                long absoluteAddress = this.chunk.toAbsolute(currentAddress);
                /* 341 */
                IInstruction insn = (IInstruction) this.insns.get(Long.valueOf(absoluteAddress));
                /* 342 */
                if (insn != null) {
                    /* 343 */
                    if ((usePC) && (!BooleanUtils.toBoolean((Boolean) this.pcInsns.get(Long.valueOf(absoluteAddress))))) {
                        /* 344 */
                        this.pcInsns.put(Long.valueOf(absoluteAddress), Boolean.valueOf(usePC));
                        /*     */
                        /*     */
                    }
                    /*     */
                    else
                        /*     */ {
                        /* 349 */
                        return created;
                        /*     */
                    }
                    /*     */
                }
                /*     */
                else {
                    /* 353 */
                    insn = proc.parseAt(this.chunk.data, currentAddress, size);
                    /* 354 */
                    boolean newcreated = addInstruction(absoluteAddress, insn, usePC);
                    /* 355 */
                    created |= newcreated;
                    /*     */
                }
                /* 357 */
                IFlowInformation flow = insn.getBreakingFlow(absoluteAddress);
                /* 358 */
                boolean shouldContinue;
                if (flow.isBroken()) {
                    /* 359 */
                    shouldContinue = false;
                    /* 360 */
                    for (IEntryPointDescription desc : flow.getTargets()) {
                        /* 361 */
                        if (desc.getAddress() == absoluteAddress + insn.getSize())
                            /*     */ {
                            /* 363 */
                            shouldContinue = true;
                            /*     */
                        }
                        /* 365 */
                        else if (this.chunk.isReachable(desc.getAddress()))
                            /*     */ {
                            /* 367 */
                            processBlock(proc, this.chunk.toRelative(desc.getAddress()), size, usePC);
                            /*     */
                        }
                        /* 369 */
                        else if (usePC) {
                            /* 370 */
                            processUnreachableAddress(desc.getAddress());
                            /*     */
                        }
                        /*     */
                    }
                    /* 373 */
                    if (!shouldContinue) {
                        /* 374 */
                        return created;
                        /*     */
                    }
                    /*     */
                }
                /* 377 */
                flow = insn.getRoutineCall(absoluteAddress);
                /* 378 */
                if (flow.isBroken()) {
                    /* 379 */
                    for (IEntryPointDescription desc : flow.getTargets()) {
                        /* 380 */
                        if (this.chunk.isReachable(desc.getAddress()))
                            /*     */ {
                            /* 382 */
                            processBlock(proc, this.chunk.toRelative(desc.getAddress()), size, usePC);
                            /*     */
                        }
                        /* 384 */
                        else if (usePC) {
                            /* 385 */
                            processUnreachableAddress(desc.getAddress());
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                }
                /*     */
                /* 390 */
                currentAddress += insn.getSize();
                /*     */
            }
            /*     */
        }
        /*     */ catch (ProcessorException | RuntimeException e) {
            /* 394 */
            logger.error("Unable to process instruction at %Xh: %s", new Object[]{Long.valueOf(this.chunk.toAbsolute(currentAddress)), e.getMessage()});
            /*     */
        }
        /* 396 */
        return created;
        /*     */
    }

    /*     */
    /*     */
    private boolean memoryChanged(byte[] code, int relativeAddress) {
        /* 400 */
        for (int i = 0; (i < code.length) && (relativeAddress + i < this.chunk.read); i++) {
            /* 401 */
            if (code[i] != this.chunk.data[(relativeAddress + i)]) {
                /* 402 */
                return true;
                /*     */
            }
            /*     */
        }
        /* 405 */
        return false;
        /*     */
    }

    /*     */
    /*     */
    private void processUnreachableAddress(long address) {
        /* 409 */
        IInstruction insn = (IInstruction) this.insns.get(Long.valueOf(address));
        /* 410 */
        if (insn == null)
            /*     */ {
            /* 412 */
            this.unreachableAddresses.add(Long.valueOf(address));
            /*     */
            /*     */
        }
        /* 415 */
        else if (!BooleanUtils.toBoolean((Boolean) this.pcInsns.get(Long.valueOf(address)))) {
            /* 416 */
            this.pcInsns.put(Long.valueOf(address), Boolean.TRUE);
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public ITextDocumentPart getDocumentPart(long anchorId, int linesAfter, int linesBefore)
    /*     */ {
        /* 423 */
        if ((this.unit.isAttached()) && (this.unit.getDefaultThread() != null) &&
                /* 424 */       (this.unit.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED)) {
            /* 425 */
            IVirtualMemory vm = this.unit.getMemory();
            /* 426 */
            if (vm != null) {
                /* 427 */
                this.chunk.update(anchorId, linesAfter, linesBefore);
                /* 428 */
                if (this.chunk.read == -1) {
                    /* 429 */
                    return TextDocumentPart.EMPTY;
                    /*     */
                }
                /*     */
                /* 432 */
                List<ILine> lines = new ArrayList();
                /* 433 */
                List<IAnchor> anchors = new ArrayList();
                /*     */
                /* 435 */
                if (this.viewType == ViewType.CODE)
                    /*     */ {
                    /* 437 */
                    IProcessor<? extends IInstruction> proc = this.unit.getProcessor();
                    /* 438 */
                    IDebuggerThread thread = this.unit.getDefaultThread();
                    /* 439 */
                    IRegisterBank registers = thread != null ? thread.getRegisters() : null;
                    /* 440 */
                    ProcessorType processorType = this.unit.getTargetInformation().getProcessorType();
                    /*     */
                    /*     */
                    /* 443 */
                    if ((registers != null) && (this.chunk.isReachable(registers.getProgramCounter()))) {
                        /* 444 */
                        if (processorType == ProcessorType.ARM)
                            /*     */ {
                            /* 446 */
                            int procMode = (registers.getFlags() & 0x20) != 0L ? 16 : 32;
                            /*     */
                            try {
                                /* 448 */
                                proc.setMode(procMode);
                                /*     */
                            }
                            /*     */ catch (ProcessorException e)
                                /*     */ {
                                /* 452 */
                                logger.warning("Can not set mode %d to processor ARM", new Object[]{Integer.valueOf(procMode)});
                                /*     */
                            }
                            /*     */
                        }
                        /*     */
                        /* 456 */
                        updateInstructions(proc, this.chunk.toRelative(registers.getProgramCounter()), this.chunk.read, true);
                        /*     */
                        /* 458 */
                        int alignment = proc.getInstructionAlignment();
                        /* 459 */
                        int relativeAddress = this.chunk.toRelative(registers.getProgramCounter());
                        /* 460 */
                        relativeAddress -= alignment;
                        /* 461 */
                        while (relativeAddress >= 0) {
                            /* 462 */
                            IInstruction insn = (IInstruction) this.insns.get(Long.valueOf(this.chunk.toAbsolute(relativeAddress)));
                            /* 463 */
                            if (insn != null) {
                                /*     */
                                break;
                                /*     */
                            }
                            /*     */
                            try
                                /*     */ {
                                /* 468 */
                                insn = proc.parseAt(this.chunk.data, relativeAddress, this.chunk.read);
                                /* 469 */
                                addInstruction(this.chunk.toAbsolute(relativeAddress), insn, false);
                                /*     */
                            }
                            /*     */ catch (Exception e)
                                /*     */ {
                                /* 473 */
                                if (processorType == ProcessorType.ARM) {
                                    /*     */
                                    try
                                        /*     */ {
                                        /* 476 */
                                        relativeAddress -= alignment;
                                        /* 477 */
                                        if (relativeAddress >= 0) {
                                            /* 478 */
                                            insn = proc.parseAt(this.chunk.data, relativeAddress, this.chunk.read);
                                            /* 479 */
                                            addInstruction(this.chunk.toAbsolute(relativeAddress), insn, false);
                                            /*     */
                                        }
                                        /*     */
                                    }
                                    /*     */ catch (ProcessorException e2)
                                        /*     */ {
                                        /*     */
                                        break;
                                        /*     */
                                    }
                                    /*     */
                                } else {
                                    /*     */
                                    break;
                                    /*     */
                                }
                                /*     */
                            }
                            /* 490 */
                            relativeAddress -= alignment;
                            /*     */
                        }
                        /*     */
                    }
                    /*     */
                    else
                        /*     */ {
                        /*     */
                        try {
                            /* 496 */
                            updateInstructions(proc, this.chunk.toRelative(anchorId), this.chunk.read, false);
                            /*     */
                        }
                        /*     */ catch (Exception localException1) {
                        }
                        /*     */
                    }
                    /*     */
                    /*     */
                    /*     */
                    /* 503 */
                    SortedMap<Long, IInstruction> subInsns = this.insns.subMap(Long.valueOf(this.chunk.firstAddress),
                            /* 504 */             Long.valueOf(this.chunk.toAbsolute(this.chunk.data.length)));
                    /* 505 */
                    long notProcessedAddress = this.chunk.firstAddress;
                    /* 506 */
                    for (Map.Entry<Long, IInstruction> insn : subInsns.entrySet()) {
                        /* 507 */
                        if (notProcessedAddress != ((Long) insn.getKey()).longValue())
                            /*     */ {
                            /* 509 */
                            formatMemory(lines, anchors, this.chunk.toRelative(notProcessedAddress), this.chunk
/* 510 */.toRelative(((Long) insn.getKey()).longValue()));
                            /* 511 */
                            lines.add(ILine.EMPTY_LINE);
                            /*     */
                        }
                        /*     */
                        /*     */
                        /* 515 */
                        lines.add(formatInsn((IInstruction) insn.getValue(), ((Long) insn.getKey()).longValue()));
                        /* 516 */
                        anchors.add(new Anchor(((Long) insn.getKey()).longValue(), lines.size() - 1));
                        /* 517 */
                        if ((((IInstruction) insn.getValue()).getBreakingFlow(((Long) insn.getKey()).longValue()).isBroken()) ||
                                /* 518 */               (((IInstruction) insn.getValue()).getRoutineCall(((Long) insn.getKey()).longValue()).isBroken()))
                            /*     */ {
                            /* 520 */
                            lines.add(ILine.EMPTY_LINE);
                            /*     */
                        }
                        /* 522 */
                        notProcessedAddress = ((Long) insn.getKey()).longValue() + ((IInstruction) insn.getValue()).getSize();
                        /*     */
                    }
                    /* 524 */
                    if ((!lines.isEmpty()) && (((ILine) lines.get(lines.size() - 1)).getText().length() != 0)) {
                        /* 525 */
                        lines.add(ILine.EMPTY_LINE);
                        /*     */
                    }
                    /* 527 */
                    formatMemory(lines, anchors, this.chunk.toRelative(notProcessedAddress), this.chunk.data.length);
                    /*     */
                    /* 529 */
                    return new TextDocumentPart(lines, anchors);
                    /*     */
                }
                /*     */
                /* 532 */
                formatMemory(lines, anchors, 0, this.chunk.data.length);
                /* 533 */
                return new TextDocumentPart(lines, anchors);
                /*     */
            }
            /*     */
        }
        /* 536 */
        return TextDocumentPart.EMPTY;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    /*     */
    private void formatMemory(List<ILine> lines, List<IAnchor> anchors, int from, int to)
    /*     */ {
        /* 547 */
        for (int i = from; i < to; ) {
            /* 548 */
            long absoluteAddress = this.chunk.toAbsolute(i);
            /* 549 */
            StringBuilder stb = new StringBuilder();
            /* 550 */
            String addressStr = formatAddress(absoluteAddress);
            /* 551 */
            stb.append(addressStr);
            /* 552 */
            if ((i & 0xF) != 0)
                /*     */ {
                /* 554 */
                int bytesToDisplay = Math.min(16 - (i & 0xF), to - i);
                /* 555 */
                appendLine(stb, i, bytesToDisplay);
                /* 556 */
                i += bytesToDisplay;
                /*     */
            }
            /*     */
            else {
                /* 559 */
                int bytesToDisplay = Math.min(16, Math.min(this.chunk.read - i, to - i));
                /* 560 */
                appendLine(stb, i, bytesToDisplay);
                /* 561 */
                i += 16;
                /*     */
            }
            /* 563 */
            lines.add(new Line(stb,
                    /* 564 */         Arrays.asList(new TextItem[]{new TextItem(0, addressStr.length() - 2, ItemClassIdentifiers.ADDRESS)})));
            /* 565 */
            anchors.add(new Anchor(absoluteAddress, lines.size() - 1));
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private StringBuilder formatReadableMemory(int from, int bytesToDisplay) {
        /* 570 */
        StringBuilder s = new StringBuilder();
        /* 571 */
        for (int j = 0; j < bytesToDisplay; j++) {
            /* 572 */
            if (j >= 1) {
            }
            /*     */
            /*     */
            /*     */
            /* 576 */
            byte b = this.chunk.data[(from + j)];
            /* 577 */
            if ((b >= 32) && (b < Byte.MAX_VALUE)) {
                /* 578 */
                s.append(String.format("%c", new Object[]{Byte.valueOf(b)}));
                /*     */
            }
            /*     */
            else {
                /* 581 */
                s.append('.');
                /*     */
            }
            /*     */
        }
        /* 584 */
        return s;
        /*     */
    }

    /*     */
    /*     */
    private void appendLine(StringBuilder stb, int from, int bytesToDisplay) {
        /* 588 */
        if ((this.chunk.isReachableRel(from)) && (this.chunk.isReachableRel(from + bytesToDisplay - 1))) {
            /* 589 */
            stb.append(Formatter.formatBinaryLineTruncate(this.chunk.data, from, bytesToDisplay, 16)).append(' ');
            /* 590 */
            stb.append(formatReadableMemory(from, bytesToDisplay));
            /*     */
        }
        /*     */
        else
            /*     */ {
            /* 594 */
            stb.append(Formatter.formatBinaryLineTruncate(this.chunk.data, from, 0, 16)).append(' ');
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    private Line formatInsn(IInstruction insn, long address) {
        /* 599 */
        StringBuilder stb = new StringBuilder();
        /* 600 */
        String addressStr = formatAddress(address);
        /* 601 */
        stb.append(addressStr);
        /* 602 */
        String bytecode = Formatter.formatBinaryLineTruncate(insn.getCode(), 0, insn.getSize(), 8);
        /* 603 */
        stb.append(bytecode).append(' ');
        /* 604 */
        stb.append(insn.format(Long.valueOf(address)));
        /* 605 */
        return new Line(stb.toString(),
                /* 606 */       Arrays.asList(new TextItem[]{new TextItem(0, addressStr.length() - 2, ItemClassIdentifiers.ADDRESS), new TextItem(addressStr
/* 607 */.length(), bytecode.length(), ItemClassIdentifiers.BYTECODE)}));
        /*     */
    }

    /*     */
    /*     */
    private String formatAddress(long address)
    /*     */ {
        /* 612 */
        int memspace = this.unit.getMemory().getSpaceBits();
        /* 613 */
        String s;
        String s;
        if (memspace <= 32) {
            /* 614 */
            s = String.format("%08X  ", new Object[]{Long.valueOf(address)});
            /*     */
        } else {
            String s;
            /* 616 */
            if (memspace <= 64) {
                /* 617 */
                s = String.format("%08X'%08X  ", new Object[]{Integer.valueOf((int) (address >> 32)), Integer.valueOf((int) address)});
                /*     */
            }
            /*     */
            else {
                /* 620 */
                s = String.format("%16X  ", new Object[]{Long.valueOf(address)});
                /*     */
            }
            /*     */
        }
        /* 623 */
        return s;
        /*     */
    }

    /*     */
    /*     */
    public void switchViewType() {
        /* 627 */
        switch (this.viewType) {
            /*     */
            case CODE:
                /* 629 */
                this.viewType = ViewType.MEMORY;
                /* 630 */
                break;
            /*     */
            case MEMORY:
                /* 632 */
                this.viewType = ViewType.CODE;
                /* 633 */
                break;
            /*     */
            default:
                /* 635 */
                this.viewType = ViewType.MEMORY;
                /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    public ViewType getViewType()
    /*     */ {
        /* 641 */
        return this.viewType;
        /*     */
    }
    /*     */
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgCodeDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */