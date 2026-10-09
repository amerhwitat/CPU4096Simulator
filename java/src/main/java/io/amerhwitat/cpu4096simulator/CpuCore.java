package io.amerhwitat.cpu4096simulator;

import java.math.BigInteger;
import java.util.Arrays;

/** Headless deterministic core for the 16-byte instruction contract. */
public final class CpuCore {
    public static final int ADD = 0x0001;
    public static final int SUB = 0x0002;
    public static final int MOV = 0x0007;
    public static final int SHL = 0x0008;
    public static final int SHR = 0x0009;
    private final int bits;
    private final WideWord[] registers;
    private long pc;
    private int privilege;

    public CpuCore(int bits, int registerCount) {
        if (registerCount <= 0) throw new IllegalArgumentException("register count must be positive");
        this.bits = bits;
        this.registers = new WideWord[registerCount];
        Arrays.setAll(registers, ignored -> WideWord.zero(bits));
    }
    public WideWord[] registers() { return registers.clone(); }
    public long pc() { return pc; }
    public int privilege() { return privilege; }
    public void setRegister(int index, WideWord value) {
        if (value.bits() != bits) throw new IllegalArgumentException("register width mismatch");
        registers[index] = value;
    }
    public WideWord execute(int opcode, int dst, int srcA, int srcB, int immediate) {
        WideWord a = registers[srcA], b = registers[srcB], out = null;
        switch (opcode) {
            case ADD -> out = a.add(b);
            case SUB -> out = a.subtract(b);
            case MOV -> out = a;
            case SHL -> out = a.shiftLeft(immediate);
            case SHR -> out = a.shiftRight(immediate);
            default -> throw new IllegalArgumentException("unsupported opcode: " + opcode);
        }
        registers[dst] = out;
        pc += 16;
        return out;
    }
    public byte[] encode(int opcode, int dst, int srcA, int srcB, long immediate) {
        byte[] bytes = new byte[16];
        put16(bytes, 0, opcode); put16(bytes, 2, dst); put16(bytes, 4, srcA); put16(bytes, 6, srcB);
        for (int i = 0; i < 8; i++) bytes[8 + i] = (byte) (immediate >>> (8 * i));
        return bytes;
    }
    public String[] snapshotRegisters() {
        return Arrays.stream(registers).limit(32).map(WideWord::hex).toArray(String[]::new);
    }
    public String workloadEnvelope(String kind, String payload) {
        return "{\"schema\":\"chimera.cpu.workload\",\"version\":1,\"kind\":\"" + escape(kind) + "\",\"payload\":\"" + escape(payload) + "\"}";
    }
    private static void put16(byte[] bytes, int at, int value) {
        bytes[at] = (byte) value; bytes[at + 1] = (byte) (value >>> 8);
    }
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
