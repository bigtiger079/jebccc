
package com.pnfsoftware.jeb.rcpclient.parts.units.debuggers;

import com.pnfsoftware.jeb.core.output.ItemClassIdentifiers;
import com.pnfsoftware.jeb.core.output.text.IAnchor;
import com.pnfsoftware.jeb.core.output.text.ILine;
import com.pnfsoftware.jeb.core.output.text.ITextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.impl.AbstractTextDocument;
import com.pnfsoftware.jeb.core.output.text.impl.Anchor;
import com.pnfsoftware.jeb.core.output.text.impl.Line;
import com.pnfsoftware.jeb.core.output.text.impl.TextDocumentPart;
import com.pnfsoftware.jeb.core.output.text.impl.TextItem;
import com.pnfsoftware.jeb.core.units.code.IEntryPointDescription;
import com.pnfsoftware.jeb.core.units.code.IFlowInformation;
import com.pnfsoftware.jeb.core.units.code.IInstruction;
import com.pnfsoftware.jeb.core.units.code.asm.memory.IVirtualMemory;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IProcessor;
import com.pnfsoftware.jeb.core.units.code.asm.processor.IRegisterBank;
import com.pnfsoftware.jeb.core.units.code.asm.processor.ProcessorException;
import com.pnfsoftware.jeb.core.units.code.debug.DebuggerThreadStatus;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerTargetInformation;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerThread;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerUnit;
import com.pnfsoftware.jeb.core.units.code.debug.IDebuggerVirtualMemory;
import com.pnfsoftware.jeb.core.units.codeobject.ProcessorType;
import com.pnfsoftware.jeb.util.format.Formatter;
import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.BooleanUtils;

public class DbgCodeDocument
        extends AbstractTextDocument {
    private static final ILogger logger = GlobalLog.getLogger(DbgCodeDocument.class);
    private static final int maxBytesPerLine = 16;
    private IDebuggerUnit unit;
    private TreeMap<Long, IInstruction> insns = new TreeMap();
    private TreeMap<Long, Boolean> pcInsns = new TreeMap();
    private TreeSet<Long> unreachableAddresses = new TreeSet();
    private Chunk chunk = new Chunk();

    static enum ViewType {
        MEMORY, CODE;

        private ViewType() {
        }
    }

    private ViewType viewType = ViewType.CODE;

    private class Chunk {
        private byte[] data;
        private int minRead = 0;
        private int read;
        long firstAddress;

        private Chunk() {
        }

        void update(long anchorId, int linesAfter, int linesBefore) {
            int memspace = DbgCodeDocument.this.unit.getMemory().getSpaceBits();
            this.firstAddress = (anchorId - linesBefore * 16 & 0xFFFFFFF);
            if (this.firstAddress < 0L) {
                this.firstAddress = 0L;
            }
            long longSize = (linesBefore + linesAfter + 1L) * 16L;
            int size = (int) Math.min(longSize, 1048576L);
            if (memspace <= 32) {
                if (this.firstAddress + size > 4294967295L) {
                    if (this.firstAddress > 4294967295L) {
                        size = 0;
                    } else {
                        size = (int) (4294967296L - this.firstAddress);
                    }
                }
            } else if ((memspace <= 64) &&
                    (this.firstAddress + size > Long.MAX_VALUE)) {
                if (this.firstAddress > Long.MAX_VALUE) {
                    size = 0;
                } else {
                    size = (int) (Long.MIN_VALUE - this.firstAddress);
                }
            }
            this.data = new byte[size];
            this.minRead = 0;
            this.read = DbgCodeDocument.this.unit.readMemory(this.firstAddress, size, this.data, 0);
            if (this.read == -1) {
                this.minRead = toRelative(anchorId & 0xFFFFFFF);
                if (size - this.minRead > 0) {
                    this.read = DbgCodeDocument.this.unit.readMemory(this.firstAddress + this.minRead, size - this.minRead, this.data, 0);
                    if (this.read == -1) {
                        this.minRead = 0;
                    }
                    this.read += this.minRead;
                } else {
                    this.minRead = 0;
                }
            }
        }

        boolean isReachable(long address) {
            return (address >= this.firstAddress + this.minRead) && (address < this.firstAddress + this.read);
        }

        boolean isReachableRel(int address) {
            return isReachable(this.firstAddress + address);
        }

        int toRelative(long address) {
            return (int) (address - this.firstAddress);
        }

        long toAbsolute(int address) {
            return address + this.firstAddress;
        }
    }

    public DbgCodeDocument(IDebuggerUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Can not build a DbgCodeDocument with a null unit");
        }
        this.unit = unit;
    }

    public void removeInsn(long anchorId) {
        this.insns.remove(Long.valueOf(anchorId));
    }

    public boolean hasInsnAt(long anchorId) {
        return this.insns.containsKey(Long.valueOf(anchorId));
    }

    public Long getNextInsnAddress(long anchorId) {
        return (Long) this.insns.higherKey(Long.valueOf(anchorId));
    }

    public long getInsnAddressAt(long anchorId, int columnOffset) {
        int addressPrefixLength = formatAddress(anchorId).length();
        if (columnOffset < addressPrefixLength) {
            return anchorId;
        }
        long maxOffset = anchorId % 16L;
        maxOffset = maxOffset == 0L ? 16L : maxOffset;
        Long nextAddress = getNextInsnAddress(anchorId);
        if (nextAddress != null) {
            maxOffset = Math.min(nextAddress.longValue() - anchorId, maxOffset);
        }
        int offset = (columnOffset - addressPrefixLength) / 3;
        if (offset < maxOffset) {
            return anchorId + offset;
        }
        return anchorId;
    }

    public IInstruction getInsnAt(long address) {
        return (IInstruction) this.insns.get(Long.valueOf(address));
    }

    public boolean forceInsnAt(long address) {
        return forceInsnAt(address, 0);
    }

    public boolean forceInsnAt(long address, int procMode) {
        IProcessor<? extends IInstruction> proc = this.unit.getProcessor();
        int mode = proc.getMode();
        boolean created = false;
        try {
            if (procMode == 0) {
                proc.setMode(proc.getDefaultMode());
            } else {
                proc.setMode(procMode);
            }
            int alignment = proc.getInstructionAlignment();
            address = address / alignment * alignment;
            return updateInstructions(proc, this.chunk.toRelative(address), this.chunk.read, false);
        } catch (ProcessorException e) {
            logger.warning("Can not set mode %d to processor %s", procMode, this.unit.getTargetInformation().getProcessorType());
            return false;
        } finally {
            try {
                proc.setMode(mode);
            } catch (ProcessorException e) {
                logger.error("Inner error: Mode %d can not be restored", mode);
            }
        }
    }

    public long getAnchorCount() {
        IDebuggerTargetInformation targetInfo = this.unit.getTargetInformation();
        if ((targetInfo == null) || (targetInfo.getProcessorType() == null) || (targetInfo.getProcessorType().is64Bit())) {
            return Long.MAX_VALUE;
        }
        return 4294967295L;
    }

    private boolean addInstruction(long address, IInstruction insn, boolean usePC) {
        SortedMap<Long, IInstruction> collisions = this.insns.subMap(address, address + insn.getSize());
        Long previousInsnAddress = (Long) this.insns.floorKey(address - 1L);
        if ((previousInsnAddress != null) && (previousInsnAddress + ((IInstruction) this.insns.get(previousInsnAddress)).getSize() > address)) {
            collisions = this.insns.subMap(previousInsnAddress, address + insn.getSize());
        }
        if (collisions.isEmpty()) {
            this.insns.put(address, insn);
            if (usePC) {
                this.pcInsns.put(address, usePC);
            }
            return true;
        }
        if (usePC) {
            List<Long> toRemove = new ArrayList();
            for (Map.Entry<Long, IInstruction> insnEntry : collisions.entrySet()) {
                if (BooleanUtils.toBoolean((Boolean) this.pcInsns.get(insnEntry.getKey()))) {
                    toRemove.clear();
                    break;
                }
                toRemove.add(insnEntry.getKey());
            }
            if (!toRemove.isEmpty()) {
                for (Long removed : toRemove) {
                    this.insns.remove(removed);
                    this.pcInsns.remove(removed);
                }
                this.insns.put(Long.valueOf(address), insn);
                this.pcInsns.put(Long.valueOf(address), Boolean.valueOf(usePC));
                return true;
            }
        }
        return false;
    }

    private boolean updateInstructions(IProcessor<? extends IInstruction> proc, int from, int size, boolean usePC) {
        if (size <= 0) {
            return false;
        }
        int relativeAddress;
        SortedMap<Long, IInstruction> subInsns = this.insns.subMap(Long.valueOf(this.chunk.toAbsolute(0)), Long.valueOf(this.chunk.toAbsolute(size)));
        List<Long> toRemove = new ArrayList();
        for (Map.Entry<Long, IInstruction> insnEntry : subInsns.entrySet()) {
            relativeAddress = this.chunk.toRelative(((Long) insnEntry.getKey()).longValue());
            if (memoryChanged(((IInstruction) insnEntry.getValue()).getCode(), relativeAddress))
                toRemove.add(insnEntry.getKey());
        }
        for (Long removed : toRemove) {
            this.insns.remove(removed);
            this.pcInsns.remove(removed);
        }
        Object subAdresses = this.unreachableAddresses.subSet(Long.valueOf(this.chunk.toAbsolute(from)), Long.valueOf(this.chunk.toAbsolute(size)));
        Set<Long> subAddressesCopy = new TreeSet((SortedSet) subAdresses);
        for (Long addr : subAddressesCopy) {
            processBlock(proc, this.chunk.toRelative(addr.longValue()), size, usePC);
        }
        ((SortedSet) subAdresses).clear();
        return processBlock(proc, from, size, usePC);
    }

    private boolean processBlock(IProcessor<? extends IInstruction> proc, int from, int size, boolean usePC) {
        int currentAddress = from;
        boolean created = false;
        try {
            while (currentAddress < size) {
                long absoluteAddress = this.chunk.toAbsolute(currentAddress);
                IInstruction insn = (IInstruction) this.insns.get(Long.valueOf(absoluteAddress));
                if (insn != null) {
                    if ((usePC) && (!BooleanUtils.toBoolean((Boolean) this.pcInsns.get(Long.valueOf(absoluteAddress))))) {
                        this.pcInsns.put(Long.valueOf(absoluteAddress), Boolean.valueOf(usePC));
                    } else {
                        return created;
                    }
                } else {
                    insn = proc.parseAt(this.chunk.data, currentAddress, size);
                    boolean newcreated = addInstruction(absoluteAddress, insn, usePC);
                    created |= newcreated;
                }
                IFlowInformation flow = insn.getBreakingFlow(absoluteAddress);
                boolean shouldContinue;
                if (flow.isBroken()) {
                    shouldContinue = false;
                    for (IEntryPointDescription desc : flow.getTargets()) {
                        if (desc.getAddress() == absoluteAddress + insn.getSize()) {
                            shouldContinue = true;
                        } else if (this.chunk.isReachable(desc.getAddress())) {
                            processBlock(proc, this.chunk.toRelative(desc.getAddress()), size, usePC);
                        } else if (usePC) {
                            processUnreachableAddress(desc.getAddress());
                        }
                    }
                    if (!shouldContinue) {
                        return created;
                    }
                }
                flow = insn.getRoutineCall(absoluteAddress);
                if (flow.isBroken()) {
                    for (IEntryPointDescription desc : flow.getTargets()) {
                        if (this.chunk.isReachable(desc.getAddress())) {
                            processBlock(proc, this.chunk.toRelative(desc.getAddress()), size, usePC);
                        } else if (usePC) {
                            processUnreachableAddress(desc.getAddress());
                        }
                    }
                }
                currentAddress += insn.getSize();
            }
        } catch (ProcessorException | RuntimeException e) {
            logger.error("Unable to process instruction at %Xh: %s", new Object[]{Long.valueOf(this.chunk.toAbsolute(currentAddress)), e.getMessage()});
        }
        return created;
    }

    private boolean memoryChanged(byte[] code, int relativeAddress) {
        for (int i = 0; (i < code.length) && (relativeAddress + i < this.chunk.read); i++) {
            if (code[i] != this.chunk.data[(relativeAddress + i)]) {
                return true;
            }
        }
        return false;
    }

    private void processUnreachableAddress(long address) {
        IInstruction insn = (IInstruction) this.insns.get(Long.valueOf(address));
        if (insn == null) {
            this.unreachableAddresses.add(Long.valueOf(address));
        } else if (!BooleanUtils.toBoolean((Boolean) this.pcInsns.get(Long.valueOf(address)))) {
            this.pcInsns.put(Long.valueOf(address), Boolean.TRUE);
        }
    }

    public ITextDocumentPart getDocumentPart(long anchorId, int linesAfter, int linesBefore) {
        if ((this.unit.isAttached()) && (this.unit.getDefaultThread() != null) &&
                (this.unit.getDefaultThread().getStatus() == DebuggerThreadStatus.PAUSED)) {
            IVirtualMemory vm = this.unit.getMemory();
            if (vm != null) {
                this.chunk.update(anchorId, linesAfter, linesBefore);
                if (this.chunk.read == -1) {
                    return TextDocumentPart.EMPTY;
                }
                List<ILine> lines = new ArrayList();
                List<IAnchor> anchors = new ArrayList();
                if (this.viewType == ViewType.CODE) {
                    IProcessor<? extends IInstruction> proc = this.unit.getProcessor();
                    IDebuggerThread thread = this.unit.getDefaultThread();
                    IRegisterBank registers = thread != null ? thread.getRegisters() : null;
                    ProcessorType processorType = this.unit.getTargetInformation().getProcessorType();
                    if ((registers != null) && (this.chunk.isReachable(registers.getProgramCounter()))) {
                        if (processorType == ProcessorType.ARM) {
                            int procMode = (registers.getFlags() & 0x20) != 0L ? 16 : 32;
                            try {
                                proc.setMode(procMode);
                            } catch (ProcessorException e) {
                                logger.warning("Can not set mode %d to processor ARM", new Object[]{Integer.valueOf(procMode)});
                            }
                        }
                        updateInstructions(proc, this.chunk.toRelative(registers.getProgramCounter()), this.chunk.read, true);
                        int alignment = proc.getInstructionAlignment();
                        int relativeAddress = this.chunk.toRelative(registers.getProgramCounter());
                        relativeAddress -= alignment;
                        while (relativeAddress >= 0) {
                            IInstruction insn = (IInstruction) this.insns.get(Long.valueOf(this.chunk.toAbsolute(relativeAddress)));
                            if (insn != null) {
                                break;
                            }
                            try {
                                insn = proc.parseAt(this.chunk.data, relativeAddress, this.chunk.read);
                                addInstruction(this.chunk.toAbsolute(relativeAddress), insn, false);
                            } catch (Exception e) {
                                if (processorType == ProcessorType.ARM) {
                                    try {
                                        relativeAddress -= alignment;
                                        if (relativeAddress >= 0) {
                                            insn = proc.parseAt(this.chunk.data, relativeAddress, this.chunk.read);
                                            addInstruction(this.chunk.toAbsolute(relativeAddress), insn, false);
                                        }
                                    } catch (ProcessorException e2) {
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            relativeAddress -= alignment;
                        }
                    } else {
                        try {
                            updateInstructions(proc, this.chunk.toRelative(anchorId), this.chunk.read, false);
                        } catch (Exception localException1) {
                        }
                    }
                    SortedMap<Long, IInstruction> subInsns = this.insns.subMap(Long.valueOf(this.chunk.firstAddress),
                            Long.valueOf(this.chunk.toAbsolute(this.chunk.data.length)));
                    long notProcessedAddress = this.chunk.firstAddress;
                    for (Map.Entry<Long, IInstruction> insn : subInsns.entrySet()) {
                        if (notProcessedAddress != ((Long) insn.getKey()).longValue()) {
                            formatMemory(lines, anchors, this.chunk.toRelative(notProcessedAddress), this.chunk
                                    .toRelative(((Long) insn.getKey()).longValue()));
                            lines.add(ILine.EMPTY_LINE);
                        }
                        lines.add(formatInsn((IInstruction) insn.getValue(), ((Long) insn.getKey()).longValue()));
                        anchors.add(new Anchor(((Long) insn.getKey()).longValue(), lines.size() - 1));
                        if ((((IInstruction) insn.getValue()).getBreakingFlow(((Long) insn.getKey()).longValue()).isBroken()) ||
                                (((IInstruction) insn.getValue()).getRoutineCall(((Long) insn.getKey()).longValue()).isBroken())) {
                            lines.add(ILine.EMPTY_LINE);
                        }
                        notProcessedAddress = ((Long) insn.getKey()).longValue() + ((IInstruction) insn.getValue()).getSize();
                    }
                    if ((!lines.isEmpty()) && (((ILine) lines.get(lines.size() - 1)).getText().length() != 0)) {
                        lines.add(ILine.EMPTY_LINE);
                    }
                    formatMemory(lines, anchors, this.chunk.toRelative(notProcessedAddress), this.chunk.data.length);
                    return new TextDocumentPart(lines, anchors);
                }
                formatMemory(lines, anchors, 0, this.chunk.data.length);
                return new TextDocumentPart(lines, anchors);
            }
        }
        return TextDocumentPart.EMPTY;
    }

    private void formatMemory(List<ILine> lines, List<IAnchor> anchors, int from, int to) {
        for (int i = from; i < to; ) {
            long absoluteAddress = this.chunk.toAbsolute(i);
            StringBuilder stb = new StringBuilder();
            String addressStr = formatAddress(absoluteAddress);
            stb.append(addressStr);
            if ((i & 0xF) != 0) {
                int bytesToDisplay = Math.min(16 - (i & 0xF), to - i);
                appendLine(stb, i, bytesToDisplay);
                i += bytesToDisplay;
            } else {
                int bytesToDisplay = Math.min(16, Math.min(this.chunk.read - i, to - i));
                appendLine(stb, i, bytesToDisplay);
                i += 16;
            }
            lines.add(new Line(stb,
                    Arrays.asList(new TextItem[]{new TextItem(0, addressStr.length() - 2, ItemClassIdentifiers.ADDRESS)})));
            anchors.add(new Anchor(absoluteAddress, lines.size() - 1));
        }
    }

    private StringBuilder formatReadableMemory(int from, int bytesToDisplay) {
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < bytesToDisplay; j++) {
            if (j >= 1) {
            }
            byte b = this.chunk.data[(from + j)];
            if ((b >= 32) && (b < Byte.MAX_VALUE)) {
                s.append(String.format("%c", new Object[]{Byte.valueOf(b)}));
            } else {
                s.append('.');
            }
        }
        return s;
    }

    private void appendLine(StringBuilder stb, int from, int bytesToDisplay) {
        if ((this.chunk.isReachableRel(from)) && (this.chunk.isReachableRel(from + bytesToDisplay - 1))) {
            stb.append(Formatter.formatBinaryLineTruncate(this.chunk.data, from, bytesToDisplay, 16)).append(' ');
            stb.append(formatReadableMemory(from, bytesToDisplay));
        } else {
            stb.append(Formatter.formatBinaryLineTruncate(this.chunk.data, from, 0, 16)).append(' ');
        }
    }

    private Line formatInsn(IInstruction insn, long address) {
        StringBuilder stb = new StringBuilder();
        String addressStr = formatAddress(address);
        stb.append(addressStr);
        String bytecode = Formatter.formatBinaryLineTruncate(insn.getCode(), 0, insn.getSize(), 8);
        stb.append(bytecode).append(' ');
        stb.append(insn.format(Long.valueOf(address)));
        return new Line(stb.toString(),
                Arrays.asList(new TextItem(0, addressStr.length() - 2, ItemClassIdentifiers.ADDRESS), new TextItem(addressStr
                        .length(), bytecode.length(), ItemClassIdentifiers.BYTECODE)));
    }

    private String formatAddress(long address) {
        int memspace = this.unit.getMemory().getSpaceBits();
        String s;
        if (memspace <= 32) {
            s = String.format("%08X  ", address);
        } else {
            if (memspace <= 64) {
                s = String.format("%08X'%08X  ", (int) (address >> 32), (int) address);
            } else {
                s = String.format("%16X  ", address);
            }
        }
        return s;
    }

    public void switchViewType() {
        switch (this.viewType) {
            case CODE:
                this.viewType = ViewType.MEMORY;
                break;
            case MEMORY:
                this.viewType = ViewType.CODE;
                break;
            default:
                this.viewType = ViewType.MEMORY;
        }
    }

    public ViewType getViewType() {
        return this.viewType;
    }
}


/* Location:              E:\tools\jeb32\jebc.jar!\com\pnfsoftware\jeb\rcpclient\part\\units\debuggers\DbgCodeDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */