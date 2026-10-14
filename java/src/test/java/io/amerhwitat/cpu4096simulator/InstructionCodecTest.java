package io.amerhwitat.cpu4096simulator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class InstructionCodecTest {
    @Test
    void roundTripsUnsignedMaximumImmediateAndHighRegisterIds() {
        var expected = new InstructionCodec.Instruction(
            0xffff, 0x8001, 0xfffe, 0x1234,
            new BigInteger("18446744073709551615"));
        byte[] bytes = InstructionCodec.encode(expected);
        assertEquals(16, bytes.length);
        assertEquals(expected, InstructionCodec.decode(bytes));
        assertArrayEquals(bytes, InstructionCodec.encode(InstructionCodec.decode(bytes)));
    }

    @Test
    void encodesAndDecodesLittleEndianFields() {
        var instruction = new InstructionCodec.Instruction(
            0x0102, 0x0304, 0x0506, 0x0708, BigInteger.valueOf(0x1122334455667788L));
        byte[] bytes = InstructionCodec.encode(instruction);
        assertEquals(0x02, Byte.toUnsignedInt(bytes[0]));
        assertEquals(0x01, Byte.toUnsignedInt(bytes[1]));
        assertEquals(0x88, Byte.toUnsignedInt(bytes[8]));
        assertEquals(0x11, Byte.toUnsignedInt(bytes[15]));
        assertEquals(instruction, InstructionCodec.decode(bytes));
    }

    @Test
    void rejectsInvalidLengthsAndOutOfRangeValues() {
        assertThrows(IllegalArgumentException.class, () -> InstructionCodec.decode(new byte[15]));
        assertThrows(IllegalArgumentException.class,
            () -> new InstructionCodec.Instruction(0x10000, 0, 0, 0, BigInteger.ZERO));
        assertThrows(IllegalArgumentException.class,
            () -> new InstructionCodec.Instruction(0, 0, 0, 0, BigInteger.ONE.shiftLeft(64)));
        assertThrows(IllegalArgumentException.class,
            () -> new InstructionCodec.Instruction(0, 0, 0, 0, BigInteger.valueOf(-1)));
    }

    @Test
    void disassemblyUsesStableHexFields() {
        var instruction = new InstructionCodec.Instruction(
            7, 2, 3, 4, BigInteger.valueOf(255));
        assertEquals("op=0x0007 r2, r3, r4, imm=0xFF",
            InstructionCodec.disassemble(instruction));
    }
}
