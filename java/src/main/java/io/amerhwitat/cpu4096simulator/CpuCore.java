package io.amerhwitat.cpu4096simulator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/** Deterministic host-side CPU model matching src/chimera.js opcode semantics. */
public final class CpuCore {
    public static final int ADD=0x0001, SUB=0x0002, AND=0x0003, OR=0x0004, XOR=0x0005,
        NOT=0x0006, MOV=0x0007, SHL=0x0008, SHR=0x0009, MUL=0x000a, MULHI=0x000b,
        ROL=0x000c, ROR=0x000d, DIV=0x000e, REM=0x000f, CMP=0x0010, CMPEQ=0x0011,
        CMPLT=0x0012, SYS_CALL=0x0013, BARRIER=0x0014, POLICY_UPDATE=0x0015;

    public record Flags(boolean zero, boolean negative, boolean carry, boolean overflow) {}
    private final int bits;
    private final WideWord[] registers;
    private long pc;
    private int privilege;
    private Flags flags = new Flags(false, false, false, false);

    public CpuCore(int bits, int registerCount) {
        if (registerCount <= 0) throw new IllegalArgumentException("register count must be positive");
        this.bits = bits;
        this.registers = new WideWord[registerCount];
        Arrays.setAll(registers, ignored -> WideWord.zero(bits));
    }
    public WideWord[] registers() { return registers.clone(); }
    public long pc() { return pc; }
    public int privilege() { return privilege; }
    public Flags flags() { return flags; }
    public void setPrivilege(int privilege) {
        if (privilege < 0 || privilege > 3) throw new IllegalArgumentException("privilege must be 0..3");
        this.privilege = privilege;
    }
    public void setRegister(int index, WideWord value) {
        Objects.requireNonNull(value, "value");
        if (value.bits() != bits) throw new IllegalArgumentException("register width mismatch");
        registers[index] = value;
    }
    public WideWord execute(int opcode, int dst, int srcA, int srcB, int immediate) {
        return execute(new InstructionCodec.Instruction(opcode, dst, srcA, srcB,
            BigInteger.valueOf(Integer.toUnsignedLong(immediate))));
    }
    /** Execute one decoded instruction; pc advances by 16 bytes on success. */
    public WideWord execute(InstructionCodec.Instruction instruction) {
        Objects.requireNonNull(instruction, "instruction");
        int dst=instruction.dst(), srcA=instruction.srcA(), srcB=instruction.srcB();
        if (dst >= registers.length || srcA >= registers.length || srcB >= registers.length) {
            throw new IllegalArgumentException("register index outside configured register file");
        }
        WideWord a=registers[srcA], b=registers[srcB], out=null;
        BigInteger imm=instruction.immediate();
        int shift;
        switch (instruction.opcode()) {
            case ADD -> out=a.add(b);
            case SUB -> out=a.subtract(b);
            case AND -> out=a.and(b);
            case OR -> out=a.or(b);
            case XOR -> out=a.xor(b);
            case NOT -> out=a.not();
            case MOV -> out=a;
            case SHL -> { shift=shiftCount(imm); out=a.shiftLeft(shift); }
            case SHR -> { shift=shiftCount(imm); out=a.shiftRight(shift); }
            case MUL -> out=new WideWord(a.toBigInteger().multiply(b.toBigInteger()), bits);
            case MULHI -> out=new WideWord(a.toBigInteger().multiply(b.toBigInteger()).shiftRight(bits), bits);
            case ROL -> out=rotate(a, shiftCount(imm), true);
            case ROR -> out=rotate(a, shiftCount(imm), false);
            case DIV -> {
                if (b.toBigInteger().signum()==0) throw new ArithmeticException("division by zero");
                out=new WideWord(a.toBigInteger().divide(b.toBigInteger()), bits);
            }
            case REM -> {
                if (b.toBigInteger().signum()==0) throw new ArithmeticException("remainder by zero");
                out=new WideWord(a.toBigInteger().remainder(b.toBigInteger()), bits);
            }
            case CMP -> {
                int cmp=a.toBigInteger().compareTo(b.toBigInteger());
                flags=new Flags(cmp==0, cmp<0, cmp>=0, false);
            }
            case CMPEQ -> out=WideWord.fromBigInteger(a.toBigInteger().equals(b.toBigInteger())?BigInteger.ONE:BigInteger.ZERO,bits);
            case CMPLT -> out=WideWord.fromBigInteger(a.toBigInteger().compareTo(b.toBigInteger())<0?BigInteger.ONE:BigInteger.ZERO,bits);
            case SYS_CALL -> { if (privilege < 1) throw new IllegalStateException("SYS_CALL requires privilege >= 1"); }
            case BARRIER -> { /* deterministic single-core barrier */ }
            case POLICY_UPDATE -> { if (privilege < 3) throw new IllegalStateException("POLICY_UPDATE requires privilege >= 3"); }
            default -> throw new IllegalArgumentException("unsupported opcode: " + instruction.opcode());
        }
        if (out != null) registers[dst]=out;
        pc += InstructionCodec.INSTRUCTION_BYTES;
        return out;
    }
    private int shiftCount(BigInteger value) {
        if (value.bitLength() > 31) return bits;
        return value.intValue();
    }
    private WideWord rotate(WideWord word, int count, boolean left) {
        if (count < 0) throw new IllegalArgumentException("negative rotation");
        int s=count % bits;
        if (s==0) return word;
        BigInteger v=word.toBigInteger(), mask=BigInteger.ONE.shiftLeft(bits).subtract(BigInteger.ONE);
        BigInteger result=left ? v.shiftLeft(s).or(v.shiftRight(bits-s)) : v.shiftRight(s).or(v.shiftLeft(bits-s));
        return new WideWord(result.and(mask), bits);
    }
    public byte[] encode(int opcode, int dst, int srcA, int srcB, long immediate) {
        return InstructionCodec.encode(new InstructionCodec.Instruction(opcode,dst,srcA,srcB,
            new BigInteger(Long.toUnsignedString(immediate))));
    }
    public String[] snapshotRegisters() {
        return Arrays.stream(registers).limit(32).map(WideWord::hex).toArray(String[]::new);
    }
    public String workloadEnvelope(String kind, String payload) {
        return "{\"schema\":\"chimera.cpu.workload\",\"version\":1,\"kind\":\"" + escape(kind) + "\",\"payload\":\"" + escape(payload) + "\"}";
    }
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
