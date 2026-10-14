package io.amerhwitat.cpu4096simulator;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import org.junit.jupiter.api.Test;

class MemoryBusTest {
    @Test void readsAndWritesBytesAndUnsigned64Values() {
        MemoryBus memory = new MemoryBus(64);
        BigInteger value = new BigInteger("FEDCBA9876543210", 16);
        memory.writeU64(8, value);
        assertEquals(value, memory.readU64(8));
        assertArrayEquals(new byte[] {(byte) 0xFE, (byte) 0xDC, (byte) 0xBA, (byte) 0x98,
            0x76, 0x54, 0x32, 0x10}, memory.read(8, 8));
    }

    @Test void rejectsOutOfBoundsAndNegativeRanges() {
        MemoryBus memory = new MemoryBus(16);
        MemoryBus.MemoryFault fault = assertThrows(MemoryBus.MemoryFault.class,
            () -> memory.read(15, 2));
        assertEquals(MemoryBus.FaultCode.ADDRESS_OUT_OF_RANGE, fault.code());
        assertThrows(MemoryBus.MemoryFault.class, () -> memory.read(-1, 1));
    }

    @Test void enforcesMappedPermissionsAndWxorx() {
        MemoryBus memory = new MemoryBus(128);
        memory.map(0, 32, true, false, true);
        assertThrows(MemoryBus.MemoryFault.class, () -> memory.write(0, new byte[] {1}));
        assertDoesNotThrow(() -> memory.checkExecute(0, 4));
        assertThrows(IllegalArgumentException.class, () -> memory.map(40, 50, true, true, true));
        assertThrows(IllegalArgumentException.class, () -> memory.map(16, 48, true, false, false));
    }

    @Test void rejectsUnsigned64Overflow() {
        MemoryBus memory = new MemoryBus(16);
        assertThrows(IllegalArgumentException.class, () -> memory.writeU64(0, BigInteger.ONE.shiftLeft(64)));
        assertThrows(IllegalArgumentException.class, () -> memory.writeU64(0, BigInteger.valueOf(-1)));
    }
}
