package io.amerhwitat.cpu4096simulator;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * Codec for the simulator's fixed 16-byte instruction envelope.
 *
 * Layout (little-endian): opcode:u16, dst:u16, srcA:u16, srcB:u16,
 * immediate:u64. This describes the repository's simulator contract only;
 * it is not claimed to match a commercial or standardized ISA.
 */
public final class InstructionCodec {
    public static final int INSTRUCTION_BYTES = 16;
    private static final BigInteger U16_MAX = BigInteger.valueOf(0xffffL);
    private static final BigInteger U64_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

    private InstructionCodec() {}

    public record Instruction(int opcode, int dst, int srcA, int srcB, BigInteger immediate) {
        public Instruction {
            checkU16("opcode", opcode);
            checkU16("dst", dst);
            checkU16("srcA", srcA);
            checkU16("srcB", srcB);
            Objects.requireNonNull(immediate, "immediate");
            if (immediate.signum() < 0 || immediate.compareTo(U64_MAX) > 0) {
                throw new IllegalArgumentException("immediate must be an unsigned 64-bit value");
            }
        }
    }

    public static byte[] encode(Instruction instruction) {
        Objects.requireNonNull(instruction, "instruction");
        ByteBuffer buffer = ByteBuffer.allocate(INSTRUCTION_BYTES).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort((short) instruction.opcode());
        buffer.putShort((short) instruction.dst());
        buffer.putShort((short) instruction.srcA());
        buffer.putShort((short) instruction.srcB());
        byte[] magnitude = instruction.immediate().toByteArray();
        int source = magnitude.length > 8 && magnitude[0] == 0 ? 1 : 0;
        int length = magnitude.length - source;
        for (int i = 0; i < 8; i++) {
            int fromRight = length - 1 - i;
            buffer.put(i + 8, fromRight >= 0 ? magnitude[source + fromRight] : (byte) 0);
        }
        return buffer.array();
    }

    public static Instruction decode(byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes");
        if (bytes.length != INSTRUCTION_BYTES) {
            throw new IllegalArgumentException("instruction must be exactly 16 bytes");
        }
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        int opcode = Short.toUnsignedInt(buffer.getShort());
        int dst = Short.toUnsignedInt(buffer.getShort());
        int srcA = Short.toUnsignedInt(buffer.getShort());
        int srcB = Short.toUnsignedInt(buffer.getShort());
        byte[] immediateBytes = new byte[9];
        for (int i = 0; i < 8; i++) immediateBytes[8 - i] = bytes[8 + i];
        BigInteger immediate = new BigInteger(immediateBytes);
        return new Instruction(opcode, dst, srcA, srcB, immediate);
    }

    public static String disassemble(Instruction instruction) {
        Objects.requireNonNull(instruction, "instruction");
        return String.format("op=0x%04X r%u, r%u, r%u, imm=0x%s",
            instruction.opcode(), instruction.dst(), instruction.srcA(), instruction.srcB(),
            instruction.immediate().toString(16).toUpperCase());
    }

    private static void checkU16(String name, int value) {
        if (value < 0 || BigInteger.valueOf(value).compareTo(U16_MAX) > 0) {
            throw new IllegalArgumentException(name + " must be an unsigned 16-bit value");
        }
    }
}
