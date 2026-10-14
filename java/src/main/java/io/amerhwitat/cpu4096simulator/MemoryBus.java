package io.amerhwitat.cpu4096simulator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Bounded deterministic memory model for host-side simulator tests.
 * Addresses are byte offsets; u64 helpers use the repository's big-endian
 * data-bus convention (instruction encoding remains separately little-endian).
 */
public final class MemoryBus {
    public enum Access { READ, WRITE, EXECUTE }
    public enum FaultCode { ADDRESS_OUT_OF_RANGE, PROTECTION_FAULT }

    public static final class MemoryFault extends RuntimeException {
        private final FaultCode code;
        private final long address;
        public MemoryFault(String message, FaultCode code, long address) {
            super(message);
            this.code = Objects.requireNonNull(code, "code");
            this.address = address;
        }
        public FaultCode code() { return code; }
        public long address() { return address; }
    }

    private record Region(long start, long end, boolean readable, boolean writable, boolean executable) {}
    private final byte[] data;
    private final List<Region> regions = new ArrayList<>();

    public MemoryBus(int size) {
        if (size <= 0) throw new IllegalArgumentException("memory size must be positive");
        this.data = new byte[size];
    }

    public int size() { return data.length; }

    /** Maps a half-open region [start, end) with explicit permissions. */
    public synchronized void map(long start, long end, boolean readable, boolean writable, boolean executable) {
        if (start < 0 || end <= start || end > data.length) {
            throw new IllegalArgumentException("region must be a non-empty range inside memory");
        }
        if (writable && executable) {
            throw new IllegalArgumentException("writable+executable mappings are forbidden (W^X)");
        }
        for (Region region : regions) {
            if (start < region.end && end > region.start) {
                throw new IllegalArgumentException("memory regions may not overlap");
            }
        }
        regions.add(new Region(start, end, readable, writable, executable));
    }

    public byte[] read(long address, int length) {
        checkRange(address, length);
        checkPermission(address, length, Access.READ);
        byte[] out = new byte[length];
        System.arraycopy(data, (int) address, out, 0, length);
        return out;
    }

    public void write(long address, byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes");
        checkRange(address, bytes.length);
        checkPermission(address, bytes.length, Access.WRITE);
        System.arraycopy(bytes, 0, data, (int) address, bytes.length);
    }

    public BigInteger readU64(long address) {
        byte[] bytes = read(address, Long.BYTES);
        return new BigInteger(1, bytes);
    }

    public void writeU64(long address, BigInteger value) {
        Objects.requireNonNull(value, "value");
        if (value.signum() < 0 || value.bitLength() > 64) {
            throw new IllegalArgumentException("value must be an unsigned 64-bit integer");
        }
        byte[] encoded = new byte[Long.BYTES];
        byte[] magnitude = value.toByteArray();
        int source = magnitude.length > 8 && magnitude[0] == 0 ? 1 : 0;
        int length = magnitude.length - source;
        System.arraycopy(magnitude, source, encoded, encoded.length - length, length);
        write(address, encoded);
    }

    public void checkExecute(long address, int length) {
        checkRange(address, length);
        checkPermission(address, length, Access.EXECUTE);
    }

    private void checkRange(long address, int length) {
        if (address < 0 || length < 0 || address > data.length || length > data.length - address) {
            throw new MemoryFault("address out of range", FaultCode.ADDRESS_OUT_OF_RANGE, address);
        }
    }

    private void checkPermission(long address, int length, Access access) {
        if (length == 0) return;
        long end = address + length;
        boolean covered = false;
        for (Region region : regions) {
            if (address >= region.start && end <= region.end) {
                covered = true;
                boolean allowed = switch (access) {
                    case READ -> region.readable;
                    case WRITE -> region.writable;
                    case EXECUTE -> region.executable;
                };
                if (!allowed) {
                    throw new MemoryFault("memory permission denied: " + access,
                        FaultCode.PROTECTION_FAULT, address);
                }
                break;
            }
        }
        // Unmapped bytes are a permissive legacy RAM region; mapped regions
        // opt into explicit permissions without silently weakening protections.
        if (!covered) {
            for (Region region : regions) {
                if (address < region.end && end > region.start) {
                    throw new MemoryFault("access crosses a mapped-region boundary",
                        FaultCode.PROTECTION_FAULT, address);
                }
            }
            if (access == Access.EXECUTE) {
                throw new MemoryFault("execution requires an executable mapping",
                    FaultCode.PROTECTION_FAULT, address);
            }
        }
    }
}
