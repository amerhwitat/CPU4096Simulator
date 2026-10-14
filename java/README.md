# Java implementation

JavaFX visualization layer for simulator traces, benchmark dashboards, deterministic vectors, and cross-language experiment metadata.

## Java 21 headless simulator core

The tracked Java directory previously contained only this README; it did not contain the documented JavaFX trace/dashboard source. The current headless core includes `WideWord` fixed-width arithmetic, a deterministic `CpuCore` supporting ADD/SUB/MOV/SHL/SHR, canonical 16-byte little-endian instruction encoding, ordered register snapshots, and a stable workload-envelope boundary. Shared vectors live in `../contracts/fixtures/cpu4096sim-runtime-v1.tsv`; run `mvn --batch-mode --no-transfer-progress clean verify` from this directory.

## Memory bus contract

`MemoryBus` provides bounded byte-addressed RAM, unsigned 64-bit data accessors, explicit half-open permission regions, executable checks, and W^X enforcement for mapped regions. `MemoryBusTest` covers byte order, out-of-range accesses, region overlap, write denial, W^X, and unsigned overflow. The memory bus is a host-side simulator model; it does not claim hardware MMU, TLB, DMA, or real CPU memory protection.

This still does not claim the JavaFX UI or the full opcode catalog is implemented.